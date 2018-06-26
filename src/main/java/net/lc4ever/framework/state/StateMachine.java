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

import java.util.List;
import java.util.Set;

import net.lc4ever.framework.state.definition.StateDefinition;
import net.lc4ever.framework.state.definition.TransitionDefinition;
import net.lc4ever.framework.state.facade.StateMachineFactory;
import net.lc4ever.framework.state.instance.ActionLog;
import net.lc4ever.framework.state.instance.ActionLogHistory;
import net.lc4ever.framework.state.instance.StateMachineInstanceHistory;

import com.google.gson.reflect.TypeToken;

/**
 * 针对每条记录的流程实例.
 * 
 * @author q-wang
 */
public interface StateMachine {
	
	/*
	 * update TABLE1 set status = 'OK' where CONTIN = ?
	 */
	
	/**
	 * 流程创建人.
	 */
	String creator();

	/**
	 * 最后参与人.
	 */
	String lastActor();
	
	/**
	 * 所有状态
	 */
	List<StateDefinition> states();
	
	/**
	 * 当前参与人(含被退回前).
	 * 返回审批历史(倒序).
	 */
	List<ActionLogHistory> actionLogHistories();
	
	/**
	 * 当前参与人(不含被退回前).
	 * TODO: 返回流程历史(倒序).
	 */
	List<ActionLog> currentLogs();
	
	/**
	 * 所有参与人(含被退回前)
	 * TODO: 返回流程历史.
	 */
	List<StateMachineInstanceHistory> histories();

	/**
	 * 当前状态可用操作.
	 */
	List<TransitionDefinition> transitions();

	/**
	 * 当前状态.
	 */
	StateDefinition currentState();
	
	Set<String> currentRoles();

	/**
	 * 下一状态.
	 * @param transitionCode
	 * @param addition 附加信息,如审批意见等
	 */
	StateMachine next(String transitionCode, String addition);
	
	StateMachine next(TransitionDefinition transition, String addition);
	
	/**
	 * 是否初态(除创建人外无参与者, eg. lastActor = creator || currentActors.size = 1).
	 */
	boolean isNew();
	
	/**
	 * 是否终态.
	 */
	boolean isEnded();
	
	/**
	 * NOTE: 必须存在Hibernate Session, 否则无法获取.
	 * @see #getContext(TypeToken)
	 * @see StateMachineFactory#create(String, String, Object)
	 * @return 流程上下文数据
	 */
	public <T> T getContext(Class<T> clazz);
	
	/**
	 * NOTE: 必须存在Hibernate Session, 否则无法获取.
	 * @see #getContext(Class)
	 * @see StateMachineFactory#create(String, String, Object)
	 * @return 流程上下文数据
	 */
	public <T> T getContext(TypeToken<T> typeToken);

}
