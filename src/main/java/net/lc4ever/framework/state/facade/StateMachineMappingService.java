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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import net.lc4ever.framework.service.GenericCrudService;
import net.lc4ever.framework.state.definition.ActorDefinition;
import net.lc4ever.framework.state.definition.StateMachineDefinition;
import net.lc4ever.framework.state.mapper.ActorRoleMapper;
import net.lc4ever.framework.state.mapper.StateMachineTemplate;
import net.lc4ever.framework.state.mapper.TemplateCallbackMapper;

/**
 * @author q-wang
 */
public class StateMachineMappingService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private GenericCrudService crudService;

	public void setCrudService(GenericCrudService crudService) {
		this.crudService = crudService;
	}

	// MachineDefinition
	public List<StateMachineDefinition> definitions() {
		return crudService.list(StateMachineDefinition.class);
	}

	public StateMachineDefinition definition(String code) {
		return crudService.get(StateMachineDefinition.class, code);
	}

	// TemplateDefinition
	@Transactional(propagation = REQUIRED)
	public StateMachineTemplate createTemplate(String templateId, String machineId, String dataType, String action) {
		StateMachineTemplate template = new StateMachineTemplate();
		template.setId(templateId);
		template.setAction(action);
		template.setDataType(dataType);
		template.setMachine(crudService.get(StateMachineDefinition.class, machineId));
		crudService.save(template);
		crudService.flush();
		return template;
	}

	@Transactional(propagation = SUPPORTS)
	public StateMachineTemplate getTemplate(String machineId, String dataType, String action) {
		return crudService.uniqueResultHql(StateMachineTemplate.class,
				"from StateMachineTemplate where machine.id = ? and dataType = ? and action = ?", machineId, dataType,
				action);
	}

	public StateMachineTemplate getTemplate(String templateId) {
		return crudService.get(StateMachineTemplate.class, templateId);
	}

	@Transactional(propagation = REQUIRED)
	public StateMachineTemplate alterTemplate(String templateId, String machineId, boolean cascade) {
		StateMachineTemplate template = crudService.get(StateMachineTemplate.class, templateId);
		if (template.getMachine().getId().equals(machineId)) {
			return template;
		}
		if (cascade) {
			// remove old allocation
			logger.warn("Removing All ActorRoleMapper using old templateId: {}", templateId);
			crudService.update("delete ActorRoleMapper where template.id = ?", templateId);
			// TODO remove old instance? and how to result?
		}
		template.setMachine(crudService.get(StateMachineDefinition.class, machineId));
		crudService.update(template);
		return template;
	}

	// ActorRoleMapper
	@Transactional(propagation = SUPPORTS)
	public List<ActorDefinition> actors(String templateId) {
		return crudService.hql(ActorDefinition.class, "select actor from ActorRoleMapper where id.template = ?",
				templateId);
	}

	@Transactional(propagation = SUPPORTS)
	public Long actorId(String templateId, String actorCode) {
		return crudService.uniqueResultHql(Long.class,
				"select a.id from StateMachineTemplate t join t.machine.actors a where t.id = ? and a.code = ?",
				templateId, actorCode);
	}

	@Transactional(propagation = REQUIRED)
	public void addRoles(String templateId, String actorCode, String... roles) {
		addRoles(templateId, actorId(templateId, actorCode), roles);
	}

	@Transactional(propagation = REQUIRED)
	public void addRoles(String templateId, Long actorId, String... roles) {
		ActorDefinition actor = crudService.get(ActorDefinition.class, actorId);
		StateMachineTemplate template = crudService.get(StateMachineTemplate.class, templateId);
		for (String role : roles) {
			ActorRoleMapper mapper = new ActorRoleMapper();
			ActorRoleMapper.ID id = new ActorRoleMapper.ID();
			//			id.setActor(actorId);
			id.setRole(role);
			//			id.setTemplate(templateId);
			mapper.setId(id);
			mapper.setActor(actor);
			mapper.setTemplate(template);
			crudService.save(mapper);
		}
	}

	@Transactional(propagation = REQUIRED)
	public void removeRoles(String templateId, String actorCode, String... roles) {
		removeRoles(templateId, actorId(templateId, actorCode), roles);
	}

	@Transactional(propagation = REQUIRED)
	public void removeRoles(String templateId, Long actorId, String... roles) {
		for (String role : roles) {
			crudService.update("delete ActorRoleMapper where id.template = ? and id.actor = ? and id.role = ?",
					templateId, actorId, role);
		}
		// TODO clear cache of ActorRoleMapper(evict class cache) or iterate delete(evict object cache)?
	}

	@Transactional(propagation = REQUIRED)
	public void setRoles(String templateId, String actorCode, String... roles) {
		setRoles(templateId, actorId(templateId, actorCode), roles);
	}

	@Transactional(propagation = REQUIRED)
	public void setRoles(String templateId, Long actorId, String... roles) {
		for (String role : roles) {
			ActorRoleMapper.ID id = new ActorRoleMapper.ID();
			id.setTemplate(templateId);
			id.setActor(actorId);
			id.setRole(role);
			ActorRoleMapper mapper = crudService.get(ActorRoleMapper.class, id);
			if (mapper == null) {
				mapper = new ActorRoleMapper();
				mapper.setActor(crudService.get(ActorDefinition.class, actorId));
				mapper.setTemplate(crudService.get(StateMachineTemplate.class, templateId));
				mapper.setId(id);
				crudService.save(mapper);
			}
		}
	}

	@Transactional(propagation = REQUIRED)
	public void clearRoles(String templateId, String actorCode) {
		clearRoles(templateId, actorId(templateId, actorCode));
	}

	@Transactional(propagation = REQUIRED)
	public void clearRoles(String templateId, Long actorId) {
		crudService.update("delete ActorRoleMapper where id.template = ? and id.actor = ?", templateId, actorId);
		// TODO clear cache of ActorRoleMapper(evict class cache) or iterate delete(evict object cache)?
	}

	public List<String> currentRoles(String templateId, String actorCode) {
		return currentRoles(templateId, actorId(templateId, actorCode));
	}

	public List<String> currentRoles(String templateId, Long actorId) {
		return crudService.hql(String.class,
				"select id.role from ActorRoleMapper where id.template = ? and id.actor = ?", templateId, actorId);
	}

	// InterceptorMapper
	public void setCallbackSql(String templateId, String state, String callbackSql) {
		TemplateCallbackMapper mapper = crudService.uniqueResultHql(TemplateCallbackMapper.class,
				"from TemplateCallbackMapper where id.template = ? and id.state = ?", templateId, state);
		if (mapper == null) {
			mapper = new TemplateCallbackMapper();
			TemplateCallbackMapper.ID id = new TemplateCallbackMapper.ID();
			id.setTemplate(templateId);
			id.setState(state);
			mapper.setId(id);
		}
		mapper.setCallback(callbackSql);
		crudService.saveOrUpdate(mapper);
	}

	public Map<String, String> getCallbackSql(String templateId) {
		List<TemplateCallbackMapper> mappers = crudService.hql(TemplateCallbackMapper.class,
				"from TemplateCallbackMapper where id.template = ?", templateId);
		Map<String, String> result = new HashMap<>();
		for (TemplateCallbackMapper mapper : mappers) {
			result.put(mapper.getId().getState(), mapper.getCallback());
		}
		return result;
	}

	public String getCallbackSql(String templateId, String state) {
		TemplateCallbackMapper mapper = crudService.uniqueResultHql(TemplateCallbackMapper.class,
				"from TemplateCallbackMapper where id.template = ? and id.state = ?", templateId, state);
		return mapper == null ? null : mapper.getCallback();
	}

}
