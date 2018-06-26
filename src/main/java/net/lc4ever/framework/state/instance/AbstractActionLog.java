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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import net.lc4ever.framework.domain.BaseEntity;

/**
 * @author q-wang
 */
@MappedSuperclass
public abstract class AbstractActionLog implements BaseEntity<Long> {

	protected Long id;

	protected Long instance;

	protected String actor;

	protected Long transition;

	protected Long fromState;

	protected Long toState;

	protected Date actionTimestamp;

	protected String addition;

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "ACTOR", length = 64, nullable = false, updatable = false)
	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	@Column(name = "TRANSITION", precision = 19, nullable = false, updatable = false)
	public Long getTransition() {
		return transition;
	}

	public void setTransition(Long transition) {
		this.transition = transition;
	}

	@Column(name = "FROM_STATE", precision = 19, nullable = false, updatable = false)
	public Long getFromState() {
		return fromState;
	}

	public void setFromState(Long fromState) {
		this.fromState = fromState;
	}

	@Column(name = "TO_STATE", precision = 19, nullable = false, updatable = false)
	public Long getToState() {
		return toState;
	}

	public void setToState(Long toState) {
		this.toState = toState;
	}

	@Column(name = "ACTION_TIMESTAMP", nullable = false, updatable = false)
	public Date getActionTimestamp() {
		return actionTimestamp;
	}

	public void setActionTimestamp(Date actionTimestamp) {
		this.actionTimestamp = actionTimestamp;
	}

	@Column(name = "ADDITION", length = 512, nullable = true, updatable = false)
	public String getAddition() {
		return addition;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}

	@Column(name = "INSTANCE", precision = 19, nullable = false)
	public Long getInstance() {
		return instance;
	}

	public void setInstance(Long instance) {
		this.instance = instance;
	}
	
}
