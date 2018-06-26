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
package net.lc4ever.framework.state.definition;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import net.lc4ever.framework.domain.TimestampObject;

/**
 * @author q-wang
 */
@Immutable
@Entity
@Table(name = "STATE_MACHINE_DEF")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StateMachineDefinition extends TimestampObject<String> {

	/** 状态机定义内部编号 */
	private String id;

	@Id
	@Column(name = "ID", length=64)
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	private int version;

	private String name;

	private StartStateDefinition startState;

	private FinalStateDefinition finalState;

	private TerminateStateDefinition terminateState;

	private List<StateDefinition> states;

	private List<TransitionDefinition> transitions;

	private List<ActorDefinition> actors;

	@Column(name = "VERSION", precision = 11)
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Column(name = "NAME", length = 255, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne(mappedBy = "machine", optional = false)
	@JoinColumn(name = "START_STATE")
	public StartStateDefinition getStartState() {
		return startState;
	}

	public void setStartState(StartStateDefinition startState) {
		this.startState = startState;
	}

	@OneToMany(mappedBy = "machine", cascade = { CascadeType.ALL })
	public List<StateDefinition> getStates() {
		return states;
	}

	public void setStates(List<StateDefinition> states) {
		this.states = states;
	}

	@OneToMany(mappedBy = "machine", cascade = { CascadeType.ALL })
	public List<TransitionDefinition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<TransitionDefinition> transitions) {
		this.transitions = transitions;
	}

	@OneToMany(mappedBy = "machine", cascade = { CascadeType.ALL })
	public List<ActorDefinition> getActors() {
		return actors;
	}

	public void setActors(List<ActorDefinition> actors) {
		this.actors = actors;
	}

	@OneToOne(mappedBy = "machine", optional = false)
	@JoinColumn(name = "FINAL_STATE")
	public FinalStateDefinition getFinalState() {
		return finalState;
	}

	public void setFinalState(FinalStateDefinition finalState) {
		this.finalState = finalState;
	}

	@OneToOne(mappedBy = "machine", optional = true)
	@JoinColumn(name = "TERMINATE_STATE")
	public TerminateStateDefinition getTerminateState() {
		return terminateState;
	}

	public void setTerminateState(TerminateStateDefinition terminateState) {
		this.terminateState = terminateState;
	}

	protected void addState(StateDefinition state) {
		if (states == null) {
			states = new ArrayList<>();
		}
		states.add(state);
	}
	protected void addTransition(TransitionDefinition transition) {
		if (transitions==null) {
			transitions = new ArrayList<>();
		}
		transitions.add(transition);
	}
	protected TransitionDefinition createTransition(StateDefinition from, StateDefinition to) {
		TransitionDefinition transition = new TransitionDefinition();
		transition.setMachine(this);
		transition.setFromState(from);
		transition.setToState(to);
		addTransition(transition);
		return transition;
	}

	public StateDefinition createState() {
		StateDefinition state = new StateDefinition();
		state.setMachine(this);
		addState(state);
		return state;
	}

	public StartStateDefinition createStartState() {
		StartStateDefinition start = new StartStateDefinition();
		start.setMachine(this);
		setStartState(start);
		addState(start);
		return start;
	}

	public FinalStateDefinition createFinalState() {
		FinalStateDefinition ended = new FinalStateDefinition();
		ended.setMachine(this);
		setFinalState(ended);
		addState(ended);
		return ended;
	}

	public ActorDefinition createActor() {
		ActorDefinition actor = new ActorDefinition();
		actor.setMachine(this);
		if (actors == null) {
			actors = new ArrayList<>();
		}
		actors.add(actor);
		return actor;
	}

}
