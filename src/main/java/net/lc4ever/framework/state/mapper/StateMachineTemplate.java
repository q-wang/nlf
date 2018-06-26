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
package net.lc4ever.framework.state.mapper;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import net.lc4ever.framework.domain.AuditableObject;
import net.lc4ever.framework.state.definition.StateMachineDefinition;

/**
 * @author q-wang
 */
@Entity
@Table(name = "STATE_MACHINE_TEMPLATE", uniqueConstraints = {
		@UniqueConstraint(name = "UK_STATE_MACHINE_TEMPLATE_MTA", columnNames = { "MACHINE", "DATA_TYPE",
				"ACTION_" }) })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StateMachineTemplate extends AuditableObject<String> {

	private String id;

	@Id
	@Column(name = "ID", length = 64)
	//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state.machine.template")
	//	@SequenceGenerator(name = "state.machine.template", sequenceName = "SEQ_STATE_MACHINE_TEMPLATE")
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	private StateMachineDefinition machine;

	private String dataType;

	private String action;

	@ManyToOne
	@JoinColumn(name = "MACHINE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_TEMPLATE_DEF"))
	public StateMachineDefinition getMachine() {
		return machine;
	}

	public void setMachine(StateMachineDefinition machine) {
		this.machine = machine;
	}

	@Column(name = "DATA_TYPE", length = 64, nullable = false)
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Column(name = "ACTION_", length = 64, nullable = false)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	private String listener;

	@Column(name = "LISTENER", length = 128)
	public String getListener() {
		return listener;
	}

	public void setListener(String listener) {
		this.listener = listener;
	}

}
