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

import java.util.Set;

import javax.persistence.Cacheable;
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

import net.lc4ever.framework.domain.TimestampObject;;

/**
 * @author q-wang
 */
@Immutable
@Entity
@Table(name = "STATE_MACHINE_DEF_ACTOR", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "CODE", "MACHINE" }, name = "UK_STATE_MACHINE_ACTOR_MC") })
@SequenceGenerator(name = "state.machine.actor", sequenceName = "SEQ_STATE_MACHINE_ACTOR")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActorDefinition extends TimestampObject<Long> {

	private Long id;

	private StateMachineDefinition machine;

	private String code;

	private String name;

	private Set<StateDefinition> visibleStates;

	private Set<TransitionDefinition> availableTransitions;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state.machine.actor")
	@Column(name = "ID", precision = 19)
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "MACHINE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_ACT_DEF"))
	public StateMachineDefinition getMachine() {
		return machine;
	}

	public void setMachine(StateMachineDefinition machine) {
		this.machine = machine;
	}

	@Column(name = "CODE", length = 64, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany
	@JoinTable(name = "STATE_MACHINE_ACTOR_STATE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_S_A"), joinColumns = @JoinColumn(name = "ACTOR"), inverseJoinColumns = @JoinColumn(name = "STATE_"), inverseForeignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_S_S"))
	public Set<StateDefinition> getVisibleStates() {
		return visibleStates;
	}

	public void setVisibleStates(Set<StateDefinition> visibleStates) {
		this.visibleStates = visibleStates;
	}

	@ManyToMany
	@JoinTable(name = "STATE_MACHINE_ACTOR_TRANSITION", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_T_A"), joinColumns = @JoinColumn(name = "ACTOR"), inverseJoinColumns = @JoinColumn(name = "TRANSITION"), inverseForeignKey = @ForeignKey(name = "FK_STATE_MACHINE_A_T_T"))
	public Set<TransitionDefinition> getAvailableTransitions() {
		return availableTransitions;
	}

	public void setAvailableTransitions(Set<TransitionDefinition> availableTransitions) {
		this.availableTransitions = availableTransitions;
	}

}
