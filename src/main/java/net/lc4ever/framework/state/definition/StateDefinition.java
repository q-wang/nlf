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
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import net.lc4ever.framework.domain.TimestampObject;

/**
 * @author q-wang
 */
@Entity
@Table(name = "STATE_MACHINE_DEF_STATE", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "CODE", "MACHINE" }, name = "UK_STATE_MACHINE_STATE_MC") })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", length = 32)
@DiscriminatorValue("STATE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StateDefinition extends TimestampObject<Long> {

	/** 持久化ID,不参与业务操作 */
	private Long id;

	/** 状态机定义ID: column machine */
	protected StateMachineDefinition machine;

	/** 状态代码: column code */
	protected String code;

	/** 状态名称: column name */
	protected String name;

	protected List<TransitionDefinition> transitions;

	protected List<ActorDefinition> actors;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state.machine.state")
	@SequenceGenerator(name = "state.machine.state", sequenceName = "SEQ_STATE_MACHINE_STATE")
	@Column(name = "ID", precision = 19)
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "MACHINE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_ST_DEF"))
	public StateMachineDefinition getMachine() {
		return machine;
	}

	public void setMachine(StateMachineDefinition machine) {
		this.machine = machine;
	}

	@Column(name = "CODE", length = 64, nullable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "NAME", length = 255, nullable = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** outgoing */
	@OneToMany(mappedBy = "fromState")
	public List<TransitionDefinition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<TransitionDefinition> transitions) {
		this.transitions = transitions;
	}

	private boolean isStarter;

	private boolean isFinalizer;

	private boolean isTerminator;

	@Column(name = "IS_STARTER")
	public boolean isStarter() {
		return isStarter;
	}

	public void setStarter(boolean starter) {
		this.isStarter = starter;
	}

	@Column(name = "IS_TERMINATOR")
	public boolean isTerminator() {
		return isTerminator;
	}

	public void setTerminator(boolean terminator) {
		this.isTerminator = terminator;
	}

	@Column(name = "IS_FINALIZER")
	public boolean isFinalizer() {
		return isFinalizer;
	}

	public void setFinalizer(boolean finalizer) {
		this.isFinalizer = finalizer;
	}

	@ManyToMany
	@JoinTable(name = "STATE_MACHINE_ACTOR_STATE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_S_S"), joinColumns = @JoinColumn(name = "STATE_"), inverseJoinColumns = @JoinColumn(name = "ACTOR"), inverseForeignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_S_A"))
	public List<ActorDefinition> getActors() {
		return actors;
	}

	public void setActors(List<ActorDefinition> actors) {
		this.actors = actors;
	}

	public TransitionDefinition createOutgoing(StateDefinition to) {
		TransitionDefinition transition = machine.createTransition(this, to);
		addTransition(transition);
		return transition;
	}
	
	protected void addTransition(TransitionDefinition transition) {
		if (transitions==null) {
			transitions = new ArrayList<>();
		}
		transitions.add(transition);
	}

}
