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

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import net.lc4ever.framework.domain.TimestampObject;

/**
 * @author q-wang
 */
@Immutable
@Entity
@Table(name = "STATE_MACHINE_DEF_TRANSITION", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "FROM_STATE", "CODE" }, name = "UK_STATE_MACHINE_TRANSITION_FC") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TransitionDefinition extends TimestampObject<Long> {

	/** 越迁ID, 内部使用 */
	private Long id;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state.machine.transition")
	@SequenceGenerator(name = "state.machine.transition", sequenceName = "SEQ_STATE_MACHINE_TRANSITION")
	@Column(name = "ID", precision = 19)
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/** 状态机定义ID */
	private StateMachineDefinition machine;

	/** 越迁代码: column code */
	private String code;

	/** 越迁名称: column name */
	private String name;

	private StateDefinition fromState;

	private StateDefinition toState;

	@ManyToOne(optional = false)
	@JoinColumn(name = "MACHINE", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_TR_DEF"))
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

	@ManyToOne(optional = false)
	@JoinColumn(name = "FROM_STATE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_TR_FR"))
	public StateDefinition getFromState() {
		return fromState;
	}

	public void setFromState(StateDefinition fromState) {
		this.fromState = fromState;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "TO_STATE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_TR_TO"))
	public StateDefinition getToState() {
		return toState;
	}

	public void setToState(StateDefinition toState) {
		this.toState = toState;
	}

	private List<ActorDefinition> actors;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "STATE_MACHINE_ACTOR_TRANSITION", joinColumns = @JoinColumn(name = "TRANSITION"), foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_T_T"), inverseJoinColumns = @JoinColumn(name = "ACTOR"), inverseForeignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_T_A"))
	public List<ActorDefinition> getActors() {
		return actors;
	}

	public void setActors(List<ActorDefinition> actors) {
		this.actors = actors;
	}

}
