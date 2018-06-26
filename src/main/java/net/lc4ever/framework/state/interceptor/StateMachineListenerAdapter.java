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
package net.lc4ever.framework.state.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lc4ever.framework.service.GenericCrudService;
import net.lc4ever.framework.state.definition.TransitionDefinition;
import net.lc4ever.framework.state.instance.StateMachineInstance;
import net.lc4ever.framework.state.mapper.StateMachineTemplate;
import net.lc4ever.framework.state.spi.CurrentUserProvider;

/**
 * @author q-wang
 */
public class StateMachineListenerAdapter implements StateMachineListener {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected CurrentUserProvider currentUserProvider;

	public void setCurrentUserProvider(CurrentUserProvider currentUserProvider) {
		this.currentUserProvider = currentUserProvider;
	}

	protected GenericCrudService crudService;

	public void setCrudService(GenericCrudService crudService) {
		this.crudService = crudService;
	}

	@Override
	public void beforeCreate(StateMachineTemplate template, String businessId) {
		logger.info("User: [{}] create machine using template: [{}] with businessId: [{}].",
				currentUserProvider.userId(), template.getId(), businessId);
	}

	@Override
	public void afterCreate(StateMachineInstance instance) {
		logger.info("instance with template: [{}], businessId: [{}] created -> id: [{}].", instance.getTemplate().getId(), instance.getBusinessId(), instance.getId());
	}

	@Override
	public void beforeTransition(TransitionDefinition transition, StateMachineInstance instance) {
	}

	@Override
	public void afterTransition(TransitionDefinition transition, StateMachineInstance instance) {
		String to = transition.getToState().getCode();
		logger.info("instance with template: [{}], businessId: [{}], state: [{}->{}]", instance.getTemplate().getId(), instance.getBusinessId(), transition.getFromState().getCode(),
				to);
		String templateId = instance.getTemplate().getId();
		String callback = crudService.uniqueResultHql(String.class,
				"select callback from TemplateCallbackMapper where id.template = ? and id.state = ?", templateId, to);
		if (callback != null) {
			crudService.sqlUpdate(callback, instance.getBusinessId());
		}
	}

	@Override
	public void onFinal(TransitionDefinition transition, StateMachineInstance instance) {
	}

	@Override
	public void onTerminate(TransitionDefinition transition, StateMachineInstance instance) {
	}

	@Override
	public void onReset(TransitionDefinition transition, StateMachineInstance instance) {
	}

}
