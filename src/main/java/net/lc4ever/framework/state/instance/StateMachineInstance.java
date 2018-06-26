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

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.lc4ever.framework.state.definition.StateDefinition;
import net.lc4ever.framework.state.mapper.StateMachineTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * @author q-wang
 */
@Entity
@Table(name = "STATE_MACHINE_INSTANCE", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "TEMPLATE", "BUSINESS_ID" }, name = "UK_STATE_MACHINE_INSTANCE_TB") })
public class StateMachineInstance extends AbstractStateMachineInstance {

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "net.lc4ever.framework.state.instance.StateMachineInstance")
	@SequenceGenerator(name = "net.lc4ever.framework.state.instance.StateMachineInstance", sequenceName = "SEQ_STATE_MACHINE_INSTANCE")
	@Column(name = "ID", precision = 19)
	public Long getId() {
		return id;
	}

	private StateMachineTemplate template;

	@ManyToOne(optional = false)
	@JoinColumn(name = "TEMPLATE", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_INSTANCE_T"))
	public StateMachineTemplate getTemplate() {
		return template;
	}

	public void setTemplate(StateMachineTemplate template) {
		this.template = template;
	}

	private StateDefinition state;

	@ManyToOne
	@JoinColumn(name = "STATE_", foreignKey = @ForeignKey(name = "FK_STATE_MACHINE_INST_ST"))
	public StateDefinition getState() {
		return state;
	}

	public void setState(StateDefinition state) {
		this.state = state;
	}

	private List<ActionLog> actionLogs;

	@OneToMany(mappedBy = "instance")
	public List<ActionLog> getActionLogs() {
		return actionLogs;
	}

	public void setActionLogs(List<ActionLog> actionLogs) {
		this.actionLogs = actionLogs;
	}

	private List<StateMachineInstanceHistory> histories;

	@Transient
	public List<StateMachineInstanceHistory> getHistories() {
		return histories;
	}

	public void setHistories(List<StateMachineInstanceHistory> histories) {
		this.histories = histories;
	}

	private Blob context;

	@Column(name = "CONTEXT", nullable = true)
	@Lob
	public Blob getContext() {
		return context;
	}

	public void setContext(Blob context) {
		this.context = context;
	}
	
	@Transient
	public <T> T getContext(Class<T> clazz) {
		return fromJson(clazz);
	}
	
	@Transient
	public <T> T getContext(TypeToken<T> typeAddapter) {
		if (context==null) {
			return null;
		}
		return fromJson(typeAddapter.getType());
	}
	
	private <T> T fromJson(Type type) {
		if (context==null) {
			return null;
		}
		Gson gson = new Gson();
		try {
			return gson.fromJson(new InputStreamReader(context.getBinaryStream()), type);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new IllegalStateException(); // TODO 友好提示?
	}
}
