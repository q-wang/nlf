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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import net.lc4ever.framework.domain.AuditableObject;

/**
 * @author q-wang
 */
@MappedSuperclass
public abstract class AbstractStateMachineInstance extends AuditableObject<Long> {

	protected Long id;

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/** 业务主键 */
	private String businessId;
	/**  */
	private String stateCode;

	private boolean initial;

	private boolean ended;

	private String creator;

	private String previousActor;

	private String actorHistory;

	private String stateHistory;

	@Column(name = "BUSINESS_ID", length = 64, nullable = false, updatable = false)
	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	@Column(name = "STATE_CODE", length = 64, nullable = false)
	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	@Column(name = "INITIAL_")
	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	@Column(name = "ENDED")
	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	@Column(name = "CREATOR", length = 64, nullable = false, updatable = false)
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Column(name = "PREVIOUS_ACTOR", length = 64, nullable = false)
	public String getPreviousActor() {
		return previousActor;
	}

	public void setPreviousActor(String previousActor) {
		this.previousActor = previousActor;
	}

	@Column(name = "ACTOR_HISTORY", length = 512)
	public String getActorHistory() {
		return actorHistory;
	}

	public void setActorHistory(String actorHistory) {
		this.actorHistory = actorHistory;
	}

	@Column(name = "STATE_HISTORY", length = 512)
	public String getStateHistory() {
		return stateHistory;
	}

	public void setStateHistory(String stateHistory) {
		this.stateHistory = stateHistory;
	}
	
	public Set<String> actors() {
		return new HashSet<>(Arrays.asList(actorHistory.split(",")));
	}
	
}
