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
package net.lc4ever.framework.state;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import net.lc4ever.framework.service.GenericCrudService;
import net.lc4ever.framework.state.definition.ActorDefinition;
import net.lc4ever.framework.state.definition.FinalStateDefinition;
import net.lc4ever.framework.state.definition.StartStateDefinition;
import net.lc4ever.framework.state.definition.StateDefinition;
import net.lc4ever.framework.state.definition.StateMachineDefinition;
import net.lc4ever.framework.state.definition.TransitionDefinition;
import net.lc4ever.framework.state.facade.StateMachineFactory;
import net.lc4ever.framework.state.facade.StateMachineMappingService;
import net.lc4ever.framework.state.mapper.StateMachineTemplate;

/**
 * @author q-wang
 */
@ContextConfiguration("classpath:net/lc4ever/framework/state/application-state-test.xml")
@ActiveProfiles("test")
public abstract class AbstractStateMachineTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	static {
		try {
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			context.setConfigLocation(AbstractStateMachineTest.class.getResource("/net/lc4ever/framework/state/log4j2.xml").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	protected GenericCrudService crudService;

	@Autowired
	public void setCrudService(GenericCrudService crudService) {
		this.crudService = crudService;
	}

	protected StateMachineFactory machineFactory;

	@Autowired
	public void setMachineFactory(StateMachineFactory machineFactory) {
		this.machineFactory = machineFactory;
	}

	protected StateMachineMappingService mappingService;

	@Autowired
	public void setMappingService(StateMachineMappingService mappingService) {
		this.mappingService = mappingService;
	}

	protected MockingCurrentUserProvier userProvider;

	@Autowired
	public void setUserProvider(MockingCurrentUserProvier userProvider) {
		this.userProvider = userProvider;
	}

	protected boolean clearBefore = false;

	protected AbstractStateMachineTest(boolean clearBefore) {
		this.clearBefore = clearBefore;
	}

	protected AbstractStateMachineTest() {
	}

	@Before
	@Transactional(propagation = REQUIRED)
	public void clear() {
		if (clearBefore) {
			// instance
			jdbcTemplate.update("delete state_machine_log_action");
			jdbcTemplate.update("delete state_machine_log_action_his");
			jdbcTemplate.update("delete state_machine_instance");
			jdbcTemplate.update("delete state_machine_instance_his");
			// mapping
			jdbcTemplate.update("delete state_machine_mapper_template");
			jdbcTemplate.update("delete state_machine_mapper_iptr");
			jdbcTemplate.update("delete state_machine_mapper_actor");
			// template
			jdbcTemplate.update("delete state_machine_template");
			// definition
			jdbcTemplate.update("delete state_machine_def_interceptor");
			jdbcTemplate.update("delete state_machine_actor_state");
			jdbcTemplate.update("delete state_machine_actor_transition");
			jdbcTemplate.update("delete state_machine_def_transition");
			jdbcTemplate.update("delete state_machine_def_actor");
			jdbcTemplate.update("delete state_machine_def_state");
			jdbcTemplate.update("delete state_machine_def");
		}
	}

	public StateMachineDefinition create1StepDefinition() {
		StateMachineDefinition definition = new StateMachineDefinition();
		definition.setId("ONE-STEP");
		definition.setName("一步审批");

		StartStateDefinition startState = definition.createStartState();
		startState.setCode("EDIT");
		startState.setName("编辑中");

		StateDefinition rejected = definition.createState();
		rejected.setCode("REJECTED");
		rejected.setName("被打回");

		StateDefinition wait1 = definition.createState();
		wait1.setCode("WAIT1");
		wait1.setName("待复核");

		FinalStateDefinition finalState = definition.createFinalState();
		finalState.setCode("DONE");
		finalState.setName("完成");

		crudService.save(definition);
		crudService.save(startState);
		crudService.save(finalState);
		crudService.save(wait1);
		crudService.save(rejected);
		crudService.flush();

		TransitionDefinition commit = startState.createOutgoing(wait1);
		commit.setCode("COMMIT");
		commit.setName("提交");

		TransitionDefinition reject1 = wait1.createOutgoing(rejected);
		reject1.setCode("REJECT1");
		reject1.setName("打回");

		TransitionDefinition approve1 = wait1.createOutgoing(finalState);
		approve1.setCode("APPROVE1");
		approve1.setName("通过");

		TransitionDefinition cancel = wait1.createOutgoing(startState);
		cancel.setCode("CANCEL");
		cancel.setName("撤回");

		TransitionDefinition known = rejected.createOutgoing(startState);
		known.setCode("EDIT");
		known.setName("编辑");

		crudService.save(commit);
		crudService.save(cancel);
		crudService.save(known);
		crudService.save(approve1);
		crudService.save(reject1);
		crudService.flush();

		ActorDefinition creator = definition.createActor();
		creator.setCode("CREATOR");
		creator.setName("创建人");
		creator.setAvailableTransitions(new HashSet<>(Arrays.asList(commit, cancel, known)));
		creator.setVisibleStates(new HashSet<>(Arrays.asList(commit.getFromState(), cancel.getFromState(), known.getFromState())));

		ActorDefinition approver1 = definition.createActor();
		approver1.setMachine(definition);
		approver1.setCode("APPROVER1");
		approver1.setName("复核人");
		approver1.setAvailableTransitions(new HashSet<>(Arrays.asList(reject1, approve1)));
		approver1.setVisibleStates(new HashSet<>(Arrays.asList(reject1.getFromState(), approve1.getFromState())));

		crudService.save(creator);
		crudService.save(approver1);
		crudService.update(definition);
		crudService.flush();
		crudService.refresh(definition);
		return definition;
	}

	public StateMachineTemplate createTemplate(String templateId, StateMachineDefinition definition, String dataType,
			String action) {
		return mappingService.createTemplate(templateId, definition.getId(), dataType, action);
	}

	public StateMachineDefinition create2StepDefinition() {
		StateMachineDefinition definition = new StateMachineDefinition();
		definition.setId("TWO-STEP");
		definition.setName("二步审批");

		StartStateDefinition startState = definition.createStartState();
		startState.setCode("EDIT");
		startState.setName("编辑中");

		StateDefinition rejected = definition.createState();
		rejected.setCode("REJECTED");
		rejected.setName("被打回");

		StateDefinition wait1 = definition.createState();
		wait1.setCode("WAIT1");
		wait1.setName("待审核");
		
		StateDefinition wait2 = definition.createState();
		wait2.setCode("WAIT2");
		wait2.setName("待复核");

		FinalStateDefinition finalState = definition.createFinalState();
		finalState.setCode("DONE");
		finalState.setName("完成");

		crudService.save(definition);
		crudService.save(startState);
		crudService.save(finalState);
		crudService.save(wait1);
		crudService.save(wait2);
		crudService.save(rejected);
		crudService.flush();

		TransitionDefinition commit = startState.createOutgoing(wait1);
		commit.setCode("COMMIT");
		commit.setName("提交");
		
		TransitionDefinition cancel = wait1.createOutgoing(startState);
		cancel.setCode("CANCEL");
		cancel.setName("撤回");

		TransitionDefinition known = rejected.createOutgoing(startState);
		known.setCode("EDIT");
		known.setName("编辑");

		TransitionDefinition reject1 = wait1.createOutgoing(rejected);
		reject1.setCode("REJECT1");
		reject1.setName("打回");

		TransitionDefinition approve1 = wait1.createOutgoing(wait2);
		approve1.setCode("APPROVE1");
		approve1.setName("通过");
		
		TransitionDefinition approve2 = wait2.createOutgoing(finalState);
		approve2.setCode("APPROVE2");
		approve2.setName("通过");
		
		TransitionDefinition reject2 = wait2.createOutgoing(rejected);
		reject2.setCode("REJECT2");
		reject2.setName("打回");

		crudService.save(commit);
		crudService.save(cancel);
		crudService.save(known);
		crudService.save(approve1);
		crudService.save(approve2);
		crudService.save(reject1);
		crudService.save(reject2);
		crudService.flush();

		ActorDefinition creator = definition.createActor();
		creator.setCode("CREATOR");
		creator.setName("创建人");
		creator.setAvailableTransitions(new HashSet<>(Arrays.asList(commit, cancel, known)));
		creator.setVisibleStates(new HashSet<>(Arrays.asList(commit.getFromState(), cancel.getFromState(), known.getFromState())));

		ActorDefinition approver1 = definition.createActor();
		approver1.setCode("APPROVER1");
		approver1.setName("审核人");
		approver1.setAvailableTransitions(new HashSet<>(Arrays.asList(reject1, approve1)));
		approver1.setVisibleStates(new HashSet<>(Arrays.asList(reject1.getFromState(), approve1.getFromState())));
		
		ActorDefinition approver2 = definition.createActor();
		approver2.setCode("APPROVER2");
		approver2.setName("复核人");
		approver2.setAvailableTransitions(new HashSet<>(Arrays.asList(reject2, approve2)));
		approver2.setVisibleStates(new HashSet<>(Arrays.asList(reject2.getFromState(), approve2.getFromState())));
		
		crudService.save(creator);
		crudService.save(approver1);
		crudService.save(approver2);
		crudService.update(definition);
		crudService.flush();
		crudService.refresh(definition);
		
		return definition;
	}

	public StateMachineDefinition step3() {
		StateMachineDefinition definition = new StateMachineDefinition();
		definition.setId("THREE-STEP");
		definition.setName("三步审批");

		StartStateDefinition startState = definition.createStartState();
		startState.setCode("EDIT");
		startState.setName("编辑中");

		StateDefinition rejected = definition.createState();
		rejected.setCode("REJECTED");
		rejected.setName("被打回");

		StateDefinition wait1 = definition.createState();
		wait1.setCode("WAIT1");
		wait1.setName("待审核");
		
		StateDefinition wait2 = definition.createState();
		wait2.setCode("WAIT2");
		wait2.setName("待复核");
		
		StateDefinition wait3 = definition.createState();
		wait3.setCode("WAIT3");
		wait3.setName("待终审");

		FinalStateDefinition finalState = definition.createFinalState();
		finalState.setCode("DONE");
		finalState.setName("完成");

		crudService.save(definition);
		crudService.save(startState);
		crudService.save(finalState);
		crudService.save(wait1);
		crudService.save(wait2);
		crudService.save(wait3);
		crudService.save(rejected);
		crudService.flush();

		TransitionDefinition commit = startState.createOutgoing(wait1);
		commit.setCode("COMMIT");
		commit.setName("提交");

		TransitionDefinition reject1 = wait1.createOutgoing(rejected);
		reject1.setCode("REJECT1");
		reject1.setName("打回");

		TransitionDefinition approve1 = wait1.createOutgoing(wait2);
		approve1.setCode("APPROVE1");
		approve1.setName("通过");
		
		TransitionDefinition approve2 = wait2.createOutgoing(wait3);
		approve2.setCode("APPROVE2");
		approve2.setName("通过");
		
		TransitionDefinition reject2 = wait2.createOutgoing(rejected);
		reject2.setCode("REJECT2");
		reject2.setName("打回");
		
		TransitionDefinition approve3 = wait3.createOutgoing(finalState);
		approve3.setCode("APPROVE3");
		approve3.setName("通过");
		
		TransitionDefinition reject3 = wait3.createOutgoing(rejected);
		reject3.setCode("REJECT3");
		reject3.setName("打回");

		TransitionDefinition cancel = wait1.createOutgoing(startState);
		cancel.setCode("CANCEL");
		cancel.setName("撤回");

		TransitionDefinition known = rejected.createOutgoing(startState);
		known.setCode("EDIT");
		known.setName("编辑");

		crudService.save(commit);
		crudService.save(cancel);
		crudService.save(known);
		crudService.save(approve1);
		crudService.save(approve2);
		crudService.save(approve3);
		crudService.save(reject1);
		crudService.save(reject2);
		crudService.save(reject3);
		crudService.flush();

		ActorDefinition creator = definition.createActor();
		creator.setCode("CREATOR");
		creator.setName("创建人");
		creator.setAvailableTransitions(new HashSet<>(Arrays.asList(commit, cancel, known)));
		creator.setVisibleStates(new HashSet<>(Arrays.asList(commit.getFromState(), cancel.getFromState(), known.getFromState())));

		ActorDefinition approver1 = definition.createActor();
		approver1.setCode("APPROVER1");
		approver1.setName("审核人");
		approver1.setAvailableTransitions(new HashSet<>(Arrays.asList(reject1, approve1)));
		approver1.setVisibleStates(new HashSet<>(Arrays.asList(reject1.getFromState(), approve1.getFromState())));
		
		ActorDefinition approver2 = definition.createActor();
		approver2.setCode("APPROVER2");
		approver2.setName("复核人");
		approver2.setAvailableTransitions(new HashSet<>(Arrays.asList(reject2, approve2)));
		approver2.setVisibleStates(new HashSet<>(Arrays.asList(reject2.getFromState(), approve2.getFromState())));
		
		ActorDefinition approver3 = definition.createActor();
		approver3.setCode("APPROVER3");
		approver3.setName("终身人");
		approver3.setAvailableTransitions(new HashSet<>(Arrays.asList(reject3, approve3)));
		approver3.setVisibleStates(new HashSet<>(Arrays.asList(reject3.getFromState(), approve3.getFromState())));
		
		crudService.save(creator);
		crudService.save(approver1);
		crudService.save(approver2);
		crudService.save(approver3);
		crudService.update(definition);
		crudService.flush();
		crudService.refresh(definition);
		throw new UnsupportedOperationException();
	}

}
