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

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.lc4ever.framework.state.StateMachine;
import net.lc4ever.framework.state.definition.StateDefinition;
import net.lc4ever.framework.state.definition.TransitionDefinition;
import net.lc4ever.framework.state.instance.ActionLog;
import net.lc4ever.framework.state.instance.ActionLogHistory;
import net.lc4ever.framework.state.instance.StateMachineInstance;
import net.lc4ever.framework.state.instance.StateMachineInstanceHistory;

/**
 * Statefull Bean
 * @author q-wang
 */
/**
 * @author wangzs
 *
 */
public class StateMachineFacade implements StateMachine {

	protected StateMachineFactory factory;
	
	protected StateMachineInstance instance;

	protected StateMachineFacade(StateMachineFactory factory, StateMachineInstance instance) {
		this.factory = factory;
		this.instance = instance;
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#creator()
	 * @return user
	 */
	@Override
	public String creator() {
		return instance.getCreator();
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#lastActor()
	 * @return user
	 */
	@Override
	public String lastActor() {
		return instance.getPreviousActor();
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#states()
	 */
	@Override
	public List<StateDefinition> states() {
//		return Collections.emptyList();
		throw new UnsupportedOperationException("Not Implement Yet.");
	}

	
	/**
	 * @see net.lc4ever.framework.state.StateMachine#actionLogHistories()
	 */
	@Override
	public List<ActionLogHistory> actionLogHistories() {
		return factory.allLogs(instance);
	}
	
	/**
	 * @see net.lc4ever.framework.state.StateMachine#currentLogs()
	 */
	@Override
	public List<ActionLog> currentLogs() {
		return instance.getActionLogs()==null?Collections.<ActionLog>emptyList():instance.getActionLogs();
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#histories()
	 */
	@Override
	public List<StateMachineInstanceHistory> histories() {
		return instance.getHistories();
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#transitions()
	 */
	@Override
	public List<TransitionDefinition> transitions() {
		return factory.transitions(instance);
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#currentState()
	 */
	@Override
	public StateDefinition currentState() {
		return instance.getState();
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#next(java.lang.String)
	 */
	@Override
	public StateMachine next(String transitionCode, String addition) {
		List<TransitionDefinition> supportedTransitions = transitions();
		for (TransitionDefinition transition : supportedTransitions) {
			if (transition.getCode().equals(transitionCode)) {
				factory.next(instance, transition, addition);
				return this;
			}
		}
		throw new IllegalArgumentException("Instance: " + instance.getId() + ", transitionCode: " + transitionCode + ", not available.");
	}
	
	@Override
	public StateMachine next(TransitionDefinition transition, String addition) {
		factory.next(instance, transition, addition);
		return this;
	}
	
	@Override
	public Set<String> currentRoles() {
		return factory.roles(instance);
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#isNew()
	 */
	@Override
	public boolean isNew() {
		return currentState().isStarter();
	}

	/**
	 * @see net.lc4ever.framework.state.StateMachine#isEnded()
	 */
	@Override
	public boolean isEnded() {
		return currentState().isFinalizer();
	}
	
	@Override
	public <T> T getContext(Class<T> clazz) {
		return fromJson(clazz);
	}
	
	@Override
	public <T> T getContext(TypeToken<T> typeAddapter) {
		if (instance.getContext()==null) {
			return null;
		}
		return fromJson(typeAddapter.getType());
	}
	
	private <T> T fromJson(Type type) {
		if (instance.getContext()==null) {
			return null;
		}
		Gson gson = new Gson();
		try {
			return gson.fromJson(new InputStreamReader(instance.getContext().getBinaryStream()), type);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new IllegalStateException(); // TODO 友好提示?
	}
}
