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
package net.lc4ever.framework.state.instance;

import java.sql.Blob;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author q-wang
 */
@Entity
@Table(name = "STATE_MACHINE_INSTANCE_HIS")
public class StateMachineInstanceHistory extends AbstractStateMachineInstance {

	@Id
	@Column(name = "ID", precision = 19)
	@Override
	public Long getId() {
		return id;
	}

	private String machine;

	private String machineCode;

	private String dataType;

	private String action;

	private String endedState;

	private Date endedTimestamp;

	@Column(name = "ACTION_", length = 64, nullable = false)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Column(name = "DATA_TYPE", length = 64, nullable = false)
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Column(name = "MACHINE", length = 64, nullable = false)
	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	@Column(name = "ENDED_STATE", length = 64, nullable = false)
	public String getEndedState() {
		return endedState;
	}

	public void setEndedState(String endedState) {
		this.endedState = endedState;
	}

	@Column(name = "MACHINE_CODE", length = 64, nullable = false)
	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	@Column(name = "ENDED_TIMESTAMP", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndedTimestamp() {
		return endedTimestamp;
	}

	public void setEndedTimestamp(Date endedTimestamp) {
		this.endedTimestamp = endedTimestamp;
	}

	private List<ActionLogHistory> actionLogs;

	@OneToMany(mappedBy = "instance")
	public List<ActionLogHistory> getActionLogs() {
		return actionLogs;
	}

	public void setActionLogs(List<ActionLogHistory> actionLogs) {
		this.actionLogs = actionLogs;
	}

	private String template;

	@Column(name = "TEMPLATE", length = 64, nullable = false)
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	private Blob context;

	@Column(name = "CONTEXT", nullable = true)
	public Blob getContext() {
		return context;
	}

	public void setContext(Blob context) {
		this.context = context;
	}

}
