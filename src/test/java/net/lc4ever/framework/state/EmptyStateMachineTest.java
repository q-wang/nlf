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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import net.lc4ever.framework.state.definition.ActorDefinition;
import net.lc4ever.framework.state.definition.StateDefinition;
import net.lc4ever.framework.state.definition.StateMachineDefinition;
import net.lc4ever.framework.state.definition.TransitionDefinition;
import net.lc4ever.framework.state.mapper.StateMachineTemplate;

/**
 * @author q-wang
 */
public class EmptyStateMachineTest extends AbstractStateMachineTest {

//	@Test
	public void oneStep() throws Exception {
		StateMachineDefinition definition = create1StepDefinition();
		crudService.update(definition);

		String templateId = "LOAN_PRODUCTS-ADD-1";
		String businessId = "BID1";

		StateMachineTemplate template = createTemplate(templateId, definition, "LOAN_PRODUCTS", "ADD");
		assertNotNull(template.getId());

		List<ActorDefinition> allActors = definition.getActors();
		for (ActorDefinition actor : allActors) {
			mappingService.addRoles(templateId, actor.getId(), "ROLE1");
		}

		userProvider.setConstraint("JUNIT");
		userProvider.setUserId("JUNIT-CREATOR");
		userProvider.setRoles(Arrays.asList("JUNIT-ROLE"));
		

		StateMachine machine = machineFactory.create(template.getId(), businessId);

		StateDefinition state = machine.currentState();
		logger.info("Current state: id:[{}] code:[{}], name:[{}]", state.getId(), state.getCode(), state.getName());

		List<TransitionDefinition> transitions = machine.transitions();
		for (TransitionDefinition transition : transitions) {
			logger.info("Available Transaction: code:[{}], name:[{}]", transition.getCode(), transition.getName());
		}

		machine.next("COMMIT", "提交");
		machine.next("CANCEL", "撤回");

		machine = machineFactory.create(template.getId(), businessId);
		machine.next("COMMIT", "提交");

		Set<String> roles = machine.currentRoles();

		assertEquals(roles, new HashSet<>(Arrays.asList("ROLE1")));

		userProvider.setUserId("JUNIT-APPROVER");
		userProvider.setRoles(Arrays.asList("ROLE1"));
		machine.next("APPROVE1", "同意");
		assertEquals(machine.currentLogs().size(), 2);
	}
	
	@Test
	@Rollback(false)
	public void twoStep() throws Exception {
//		StateMachineDefinition definition = create2StepDefinition();
//		crudService.update(definition);

		StateMachine machine;
		
		String templateId = "LOAN_PRODUCTS-ADD-2";
		String businessId = "BID5";

//		StateMachineTemplate template = createTemplate(templateId, definition, "LOAN_PRODUCTS", "ADD");
//		assertNotNull(template.getId());
		
//		List<ActorDefinition> allActors = definition.getActors();
//		for (ActorDefinition actor : allActors) {
//			mappingService.addRoles(templateId, actor.getId(), "ROLE1");
//		}

		//		mappingService.setCallbackSql(template.getId(), "", "", "");

//		userProvider.setConstraint("JUNIT");
//		userProvider.setUserId("JUNIT-CREATOR");
//		userProvider.setRoles(Arrays.asList("JUNIT-ROLE"));
//
//		StateMachine machine = machineFactory.create(templateId, businessId);
//
//		StateDefinition state = machine.currentState();
//		logger.info("Current state: id:[{}] code:[{}], name:[{}]", state.getId(), state.getCode(), state.getName());
//
//		List<TransitionDefinition> transitions = machine.transitions();
//		for (TransitionDefinition transition : transitions) {
//			logger.info("Available Transaction: code:[{}], name:[{}]", transition.getCode(), transition.getName());
//		}
//
//		machine.next("COMMIT", "提交");
//		machine.next("CANCEL", "撤回");
//		
//		assertEquals(2, machine.currentLogs().size());
//
//
//		machine = machineFactory.create(templateId, businessId);
//		machine.next("COMMIT", "提交");
//
//		Set<String> roles = machine.currentRoles();
//
//		assertEquals(roles, new HashSet<>(Arrays.asList("JUNIT-ROLE-APPROVER1")));
//
//		userProvider.setUserId("JUNIT-APPROVER1");
//		userProvider.setRoles(Arrays.asList("JUNIT-ROLE-APPROVER1"));
//		
//		machine.next("APPROVE1", "同意");
//		
//		userProvider.setUserId("JUNIT-APPROVER2");
//		userProvider.setRoles(Arrays.asList("JUNIT-ROLE-APPROVER2"));
//		machine.next("REJECT2", "拒绝");
//		
//		assertEquals("REJECTED", machine.currentState().getCode());
//		
//		userProvider.setUserId("JUNIT-CREATOR");
//		machine.next("EDIT", "再编辑");
		
//		Map<String, String> context = new HashMap<>();
//		context.put("key1", "Hello World");
		
		String context = "Hello World";
		
		
		userProvider.setUserId("JUNIT-CREATOR");
		userProvider.setRoles(Arrays.asList("JUNIT-ROLE"));
		
		machine = machineFactory.create(templateId, businessId, context);
		machine.next("COMMIT", "提交审核");
		
		userProvider.setUserId("JUNIT-APPROVER1");
		userProvider.setRoles(Arrays.asList("JUNIT-ROLE-APPROVER1"));
		machine.next("APPROVE1", "同意");
		
		assertEquals(context, machine.getContext(String.class));
		
		userProvider.setUserId("JUNIT-APPROVER2");
		userProvider.setRoles(Arrays.asList("JUNIT-ROLE-APPROVER2"));
		machine.next("APPROVE2", "同意");
	}

}
