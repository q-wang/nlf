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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author q-wang
 */
@Entity
@Table(name = "STATE_MACHINE_LOG_ACTION_HIS")
public class ActionLogHistory extends AbstractActionLog {
	
	@Override
	@Id
	@Column(name = "ID", precision = 19)
	public Long getId() {
		return id;
	}

	private String machine;

	private int version;

	private String dataType;

	private String action;

	private String businessId;

	private String transitionCode;

	private String transitionName;

	private String fromStateCode;

	private String fromStateName;

	private String toStateCode;

	private String toStateName;
	
	public ActionLogHistory() {
	}
	
	@Column(name = "MACHINE", length = 64, nullable = false)
	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	@Column(name = "VERSION", precision = 11)
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Column(name = "DATA_TYPE", length = 64, nullable = false)
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Column(name = "ACTION", length = 64, nullable = false)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Column(name = "BUSINESS_ID", length = 64, nullable = false)
	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	@Column(name = "TRANSITION_CODE", length = 64, nullable = false)
	public String getTransitionCode() {
		return transitionCode;
	}

	public void setTransitionCode(String transitionCode) {
		this.transitionCode = transitionCode;
	}

	@Column(name = "TRANSITION_NAME", length = 255, nullable = false)
	public String getTransitionName() {
		return transitionName;
	}

	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}

	@Column(name = "FROM_STATE_CODE", length = 64, nullable = false)
	public String getFromStateCode() {
		return fromStateCode;
	}

	public void setFromStateCode(String fromStateCode) {
		this.fromStateCode = fromStateCode;
	}

	@Column(name = "FROM_STATE_NAME", length = 64, nullable = false)
	public String getFromStateName() {
		return fromStateName;
	}

	public void setFromStateName(String fromStateName) {
		this.fromStateName = fromStateName;
	}

	@Column(name = "TO_STATE_CODE", length = 64, nullable = false)
	public String getToStateCode() {
		return toStateCode;
	}

	public void setToStateCode(String toStateCode) {
		this.toStateCode = toStateCode;
	}

	@Column(name = "TO_STATE_NAME", length = 255, nullable = false)
	public String getToStateName() {
		return toStateName;
	}

	public void setToStateName(String toStateName) {
		this.toStateName = toStateName;
	}

}
