/*
 * MIT License
 *
 * Copyright (c) 2008-2017 q-wang, &lt;apeidou@gmail.com&gt;
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.lc4ever.framework.state.facade;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.lc4ever.framework.cglib.beans.BeanCopier;
import net.lc4ever.framework.cglib.beans.BeanCopierFactory;
import net.lc4ever.framework.context.ContextHolder;
import net.lc4ever.framework.domain.BaseEntity;
import net.lc4ever.framework.format.DateFormatter;
import net.lc4ever.framework.service.GenericCrudService;
import net.lc4ever.framework.state.StateMachine;
import net.lc4ever.framework.state.definition.ActorDefinition;
import net.lc4ever.framework.state.definition.TransitionDefinition;
import net.lc4ever.framework.state.instance.ActionLog;
import net.lc4ever.framework.state.instance.ActionLogHistory;
import net.lc4ever.framework.state.instance.StateMachineInstance;
import net.lc4ever.framework.state.instance.StateMachineInstanceHistory;
import net.lc4ever.framework.state.interceptor.StateMachineListener;
import net.lc4ever.framework.state.mapper.StateMachineTemplate;
import net.lc4ever.framework.state.spi.CurrentUserProvider;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

/**
 * Stateless Bean
 * @author q-wang
 */
public class StateMachineFactory {

	public static final String USER_ANONYMOUS = "anonymous";

	public static final BeanCopier<ActionLog, ActionLogHistory> COPIER_LOG = BeanCopierFactory.create(ActionLog.class,
			ActionLogHistory.class);
	public static final BeanCopier<StateMachineInstance, StateMachineInstanceHistory> COPIER_INSTANCE = BeanCopierFactory
			.create(StateMachineInstance.class, StateMachineInstanceHistory.class);

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected GenericCrudService crudService;

	protected SessionFactory sessionFactory;

	protected StateMachineListener defaultListener;

	protected CurrentUserProvider currentUserProvider;

	@Required
	public void setCrudService(GenericCrudService crudService) {
		this.crudService = crudService;
	}

	@Required
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Required
	public void setListener(StateMachineListener listener) {
		this.defaultListener = listener;
	}

	@Required
	public void setCurrentUserProvider(CurrentUserProvider currentUserProvider) {
		this.currentUserProvider = currentUserProvider;
	}

	public void evictCache() {
		sessionFactory.getCache().evictAllRegions();
	}

	public void evictCache(Class<? extends BaseEntity<?>> clazz) {
		sessionFactory.getCache().evictEntityRegion(clazz);
	}

	/**
	 * 加载已存在的状态机.
	 * 
	 * @param templateId 模板ID(已组合dataType, machineId, action)
	 * @param businessId 业务主键
	 * @param user 当前操作员
	 * @return
	 */
	@Transactional(propagation = SUPPORTS)
	public StateMachine retrieve(String templateId, String businessId) {
		StateMachineInstance instance = crudService.uniqueResultHql(StateMachineInstance.class,
				"from StateMachineInstance where template.id = ? and businessId = ?", templateId, businessId);
		if (instance == null) return null;
		return new StateMachineFacade(this, instance);
	}

	public StateMachine retrieve(String machine, String dataType, String action, String businessId) {
		return retrieve(template(machine, dataType, action), businessId);
	}

	@Transactional
	public String template(String machineCode, String dataType, String action) {
		return crudService.uniqueResultHql(String.class,
				"select id from StateMachineTemplate where machine.code = ? and dataType = ? and action = ?",
				machineCode, dataType, action);
	}

	/**
	 * 创建新的状态机.
	 * 
	 * @param templateId 模板ID(已组合dataType, machineId, action)
	 * @param businessId 业务主键
	 * @param user 当前操作员
	 * @return
	 */
	@Transactional(propagation = REQUIRED)
	public StateMachine create(String templateId, String businessId) {
		return create(templateId, businessId, null);
	}

	public StateMachine create(String templateId, String businessId, Object context) {
		StateMachineTemplate template = crudService.get(StateMachineTemplate.class, templateId);
		StateMachineInstance instance = newInstance(template, businessId, currentUserProvider.userId(), context);
		return new StateMachineFacade(this, instance);
	}

	protected StateMachineListener getListener(StateMachineTemplate template) {
		if (template.getListener() == null) {
			return defaultListener;
		} else {
			return ContextHolder.getBean(template.getListener(), StateMachineListener.class);
		}
	}

	@Transactional(propagation = REQUIRED)
	protected void next(StateMachineInstance instance, TransitionDefinition transition, String addition) {
		if (transition.getFromState().isStarter() || transition.getToState().isStarter()) {
			if (!currentUserProvider.userId().equals(instance.getCreator())) {
				throw new UnsupportedOperationException("仅允许流程创建人操作此步骤");
			}
		} else {
			String[] users = instance.getActorHistory().split(",");
			if (ArrayUtils.contains(users, currentUserProvider.userId())) {
				throw new UnsupportedOperationException("不允许同一人在审批中参与二次操作");
			}
		}
		// TODO verify roles?
		StateMachineListener listener = getListener(instance.getTemplate());

		listener.beforeTransition(transition, instance);
		if (!transition.getToState().isStarter()) {
			instance.setInitial(false);
		}
		instance.setState(transition.getToState());
		instance.setStateCode(transition.getToState().getCode());
		instance.setPreviousActor(currentUserProvider.userId());
		Set<String> actors = instance.actors();
		actors.add(currentUserProvider.userId());
		instance.setActorHistory(StringUtils.join(actors, ','));
		instance.setStateHistory(instance.getStateHistory() + "," + instance.getStateCode());
		addActionLog(instance, transition, addition);
		crudService.update(instance);
		crudService.flush();
		crudService.refresh(instance);
		listener.afterTransition(transition, instance);
		if (transition.getToState().isFinalizer()) {
			instance.setEnded(true);
			crudService.update(instance);
			listener.onFinal(transition, instance);
			history(instance);
		} else if (transition.getToState().isTerminator()) {
			instance.setEnded(true);
			crudService.update(instance);
			listener.onTerminate(transition, instance);
			history(instance);
		} else if (transition.getToState().isStarter()) {
			instance.setInitial(true);
			//crudService.save(instance);
			listener.onReset(transition, instance);
			history(instance);
			StateMachine machine = create(instance.getTemplate().getId(), instance.getBusinessId(), instance.getContext(Object.class));
			// 添加时，如果选择产品状态位为1:WAIT1:待审核，则执行工作流提交操作
			if ("WAIT1".equals(instance.getStateCode())) {
				machine.next("COMMIT", "提交审核");
			}
		}
	}

	protected void history(StateMachineInstance instance) {
		crudService.refresh(instance);
		StateMachineInstanceHistory instanceHistory = createHistory(instance);
		crudService.save(instanceHistory);
		crudService.flush();
		for (ActionLog actionLog : instance.getActionLogs()) {
			ActionLogHistory logHistory = createHistory(actionLog);
			crudService.delete(actionLog);
			crudService.save(logHistory);
		}
		crudService.flush();
		crudService.delete(instance);
		crudService.flush();
	}

	protected StateMachineInstanceHistory createHistory(StateMachineInstance instance) {
		StateMachineTemplate template = instance.getTemplate();
		StateMachineInstanceHistory instanceHistory = new StateMachineInstanceHistory();
		//		COPIER_INSTANCE.copy(instance, instanceHistory);

		instanceHistory.setId(instance.getId());
		instanceHistory.setActorHistory(instance.getActorHistory());
		instanceHistory.setBusinessId(instance.getBusinessId());
		instanceHistory.setCreator(instance.getCreator());
		instanceHistory.setCreatorsId(instance.getCreatorsId());
		instanceHistory.setEnded(instance.isEnded());
		instanceHistory.setInitial(instance.isInitial());
		instanceHistory.setPreviousActor(instance.getPreviousActor());
		instanceHistory.setStateCode(instance.getStateCode());
		instanceHistory.setStateHistory(instance.getStateHistory());

		instanceHistory.setAction(template.getAction());
		instanceHistory.setDataType(template.getDataType());
		instanceHistory.setEndedTimestamp(DateFormatter.now());
		instanceHistory.setEndedState(instance.getStateCode());
		instanceHistory.setMachine(template.getId());
		instanceHistory.setMachineCode(template.getId());
		instanceHistory.setTemplate(instance.getTemplate().getId());
		instanceHistory.setContext(instance.getContext());
		return instanceHistory;
	}

	protected ActionLogHistory createHistory(ActionLog actionLog) {
		ActionLogHistory logHistory = new ActionLogHistory();
		//		COPIER_LOG.copy(actionLog, logHistory);
		StateMachineInstance instance = crudService.get(StateMachineInstance.class, actionLog.getInstance());
		TransitionDefinition transition = crudService.get(TransitionDefinition.class, actionLog.getTransition());
		StateMachineTemplate template = instance.getTemplate();

		logHistory.setId(actionLog.getId());
		logHistory.setAddition(actionLog.getAddition());
		logHistory.setActor(actionLog.getActor());
		logHistory.setInstance(actionLog.getInstance());
		logHistory.setFromState(actionLog.getFromState());
		logHistory.setToState(actionLog.getToState());
		logHistory.setTransition(actionLog.getTransition());
		logHistory.setActionTimestamp(actionLog.getActionTimestamp());

		logHistory.setAction(template.getAction());
		logHistory.setBusinessId(instance.getBusinessId());
		logHistory.setDataType(template.getDataType());
		logHistory.setMachine(template.getMachine().getId());
		logHistory.setFromStateCode(transition.getFromState().getCode());
		logHistory.setFromStateName(transition.getFromState().getName());
		logHistory.setTransitionCode(transition.getCode());
		logHistory.setTransitionName(transition.getName());
		logHistory.setToStateCode(transition.getToState().getCode());
		logHistory.setToStateName(transition.getToState().getName());
		return logHistory;
	}

	protected void addActionLog(final StateMachineInstance instance, TransitionDefinition transition, String addition) {
		ActionLog actionLog = new ActionLog();
		actionLog.setActionTimestamp(DateFormatter.now());
		actionLog.setActor(currentUserProvider.userId());
		actionLog.setInstance(instance.getId());
		actionLog.setFromState(transition.getFromState().getId());
		actionLog.setToState(transition.getToState().getId());
		actionLog.setTransition(transition.getId());
		actionLog.setAddition(addition);
		crudService.save(actionLog);
		crudService.flush();
		crudService.refresh(instance);
	}

	protected List<StateMachineInstanceHistory> histories(StateMachineInstance instance) {
		return crudService.hql(StateMachineInstanceHistory.class,
				"from StateMachineInstanceHistory where template = ? and businessId = ? order by id desc",
				instance.getTemplate().getId(), instance.getBusinessId());
	}

	protected void terminate(StateMachineInstance instance) {

	}

	protected List<ActionLogHistory> allLogs(StateMachineInstance instance) {
		List<ActionLog> actionLogs = instance.getActionLogs();
		List<ActionLogHistory> histories = crudService.hql(ActionLogHistory.class,
				"from ActionLogHistory where dataType = ? and businessId = ? order by id desc",
				instance.getTemplate().getDataType(), instance.getBusinessId());

		List<ActionLogHistory> allLogs = new ArrayList<>(histories);
		for (ActionLog actionLog : actionLogs) {
			ActionLogHistory history = createHistory(actionLog);
			allLogs.add(history);
		}

		Collections.sort(allLogs, new Comparator<ActionLogHistory>() {
			@Override
			public int compare(ActionLogHistory o1, ActionLogHistory o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});

		return allLogs;
	}

	protected List<TransitionDefinition> transitions(StateMachineInstance instance) {
		List<TransitionDefinition> transitions = instance.getState().getTransitions();
		List<TransitionDefinition> results = new ArrayList<>();
		List<String> roles = currentUserProvider.roles();
		List<Long> actors = new ArrayList<>();
		for (String role : roles) {
			actors.addAll(crudService.hql(Long.class,
					"select id.actor from ActorRoleMapper where id.role = ? and id.template =?", role,
					instance.getTemplate().getId()));
		}

		String[] actorHitories = instance.getActorHistory().split(",");
		
		if (currentUserProvider.userId().equals(instance.getCreator())) { // 流程创建人
			for (TransitionDefinition transition: transitions) {
				if ((transition.getFromState().isStarter() || transition.getToState().isStarter())) {
					results.add(transition);
				}
			}
		} else if (!ArrayUtils.contains(actorHitories, currentUserProvider.userId())) { // 历史参与，禁止再次参与
			for (TransitionDefinition transition: transitions) {
				List<ActorDefinition> tas = transition.getActors();
				for (ActorDefinition actor : tas) {
					if (actors.contains(actor.getId())) {
						results.add(transition);
					}
				}
			}
		}
		return results;
	}

	protected Set<String> roles(StateMachineInstance instance) {
		List<TransitionDefinition> transitions = instance.getState().getTransitions();
		Set<String> roles = new HashSet<>();
		for (TransitionDefinition transition : transitions) {
			for (ActorDefinition actor : transition.getActors()) {
				roles.addAll(crudService.hql(String.class,
						"select id.role from ActorRoleMapper where actor.id = ? and template.id = ?", actor.getId(),
						instance.getTemplate().getId()));
			}
		}
		return roles;
	}

	private StateMachineInstance newInstance(StateMachineTemplate template, String businessId, String user,
			final Object context) {
		final StateMachineInstance instance = new StateMachineInstance();
		instance.setTemplate(template);
		instance.setBusinessId(businessId);

		instance.setCreator(user);
		instance.setPreviousActor(user);

		instance.setEnded(false);
		instance.setInitial(true);
		instance.setState(template.getMachine().getStartState());
		instance.setStateCode(template.getMachine().getStartState().getCode());

		instance.setActorHistory(user);
		instance.setStateHistory(instance.getStateCode());

		StateMachineListener listener = getListener(template);
		listener.beforeCreate(template, businessId);

		crudService.saveOrUpdate(instance);
		crudService.flush();
		if (context != null) {
			crudService.callback(new HibernateCallback<Void>() {
				@Override
				public Void doInHibernate(Session session) throws HibernateException {
					Gson gson = new Gson();
					Blob blob = session.getLobHelper().createBlob(gson.toJson(context).getBytes());
					instance.setContext(blob);
					session.update(instance);
					session.flush();
					return null;
				}
			});
		}
		crudService.refresh(instance);
		listener.afterCreate(instance);
		return instance;
	}

	public StateMachine create(String machine, String dataType, String action, String businessId) {
		return create(template(machine, dataType, action), businessId);
	}

}
