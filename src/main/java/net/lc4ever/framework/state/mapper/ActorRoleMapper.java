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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import net.lc4ever.framework.domain.AuditableObject;
import net.lc4ever.framework.state.definition.ActorDefinition;

/**
 * @author q-wang
 */
@Entity
@Table(name = "STATE_MACHINE_MAPPER_ACTOR")
public class ActorRoleMapper extends AuditableObject<ActorRoleMapper.ID> {

	@Embeddable
	public static class ID implements Serializable {

		private String role;

		private Long actor;

		private String template;

		public ID() {
		}

		public ID(String template, Long actor, String role) {
			this.template = template;
			this.actor = actor;
			this.role = role;
		}

		@Column(name = "ROLE_", length = 64, nullable = false)
		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		@Column(name = "ACTOR", precision = 19, nullable = false)
		public Long getActor() {
			return actor;
		}

		public void setActor(Long actor) {
			this.actor = actor;
		}

		@Column(name = "TEMPLATE", length = 64, nullable = false)
		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		@Override
		public String toString() {
			return "ID [role=" + role + ", actor=" + actor + ", template=" + template + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((actor == null) ? 0 : actor.hashCode());
			result = prime * result + ((role == null) ? 0 : role.hashCode());
			result = prime * result + ((template == null) ? 0 : template.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ID other = (ID) obj;
			if (actor == null) {
				if (other.actor != null)
					return false;
			} else if (!actor.equals(other.actor))
				return false;
			if (role == null) {
				if (other.role != null)
					return false;
			} else if (!role.equals(other.role))
				return false;
			if (template == null) {
				if (other.template != null)
					return false;
			} else if (!template.equals(other.template))
				return false;
			return true;
		}

	}

	private ID id;

	@EmbeddedId
	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}

	private StateMachineTemplate template;

	private ActorDefinition actor;

	@ManyToOne
	@MapsId("template")
	@JoinColumn(name = "TEMPLATE", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_AUM_T"))
	public StateMachineTemplate getTemplate() {
		return template;
	}

	/** NOTE: Internal use only. Don't call this method manually. */
	public void setTemplate(StateMachineTemplate template) {
		this.template = template;
	}

	@ManyToOne
	@MapsId("actor")
	@JoinColumn(name = "ACTOR", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_AUM_A"))
	public ActorDefinition getActor() {
		return actor;
	}

	/** NOTE: Internal use only. Don't call this method manually. */
	public void setActor(ActorDefinition actor) {
		this.actor = actor;
	}

}
