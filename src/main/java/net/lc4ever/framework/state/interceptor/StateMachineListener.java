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

import net.lc4ever.framework.state.definition.TransitionDefinition;
import net.lc4ever.framework.state.instance.StateMachineInstance;
import net.lc4ever.framework.state.mapper.StateMachineTemplate;

/**
 * @author q-wang
 */
public interface StateMachineListener {

	public void beforeCreate(StateMachineTemplate template, String businessId);

	public void afterCreate(StateMachineInstance instance);

	public void beforeTransition(TransitionDefinition transition, StateMachineInstance instance);

	public void afterTransition(TransitionDefinition transition, StateMachineInstance instance);

//	public void beforeStateEntry(TransitionDefinition transition, StateMachineInstance instance, String user);
//
//	public void afterStateEntry(TransitionDefinition transition, StateMachineInstance instance, String user);
//
//	public void beforeStateLeave(TransitionDefinition transition, StateMachineInstance instance, String user);
//
//	public void afterStateLeave(TransitionDefinition transition, StateMachineInstance instance, String user);
	
	public void onFinal(TransitionDefinition transition, StateMachineInstance instance);
	
	public void onTerminate(TransitionDefinition transition, StateMachineInstance instance);
	
	public void onReset(TransitionDefinition transition, StateMachineInstance instance);

}
