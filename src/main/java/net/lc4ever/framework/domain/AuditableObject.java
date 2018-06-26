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
package net.lc4ever.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
@MappedSuperclass
public abstract class AuditableObject<K extends Serializable> extends TimestampObject<K> {

	/**
	 * 修改人ID.
	 * 
	 * MODIFIERS_ID
	 */
	@Column(name="MODIFIERS_ID")
	protected String modifiersId;

	/**
	 * 创建人ID.
	 * 
	 * CREATORS_ID
	 */
	@Column(name="CREATORS_ID")
	protected String creatorsId;


	/**
	 * 修改人ID.
	 * 
	 * MODIFIERS_ID
	 * @return the modifiersId
	 */
	@Column(name="MODIFIERS_ID",length=36,insertable=false)
	public String getModifiersId() {
		return modifiersId;
	}


	/**
	 * 修改人ID.
	 * 
	 * MODIFIERS_ID
	 * @param modifiersId the modifiersId to set
	 */
	public void setModifiersId(final String modifiersId) {
		this.modifiersId = modifiersId;
	}


	/**
	 * 创建人ID.
	 * 
	 * CREATORS_ID
	 * @return the creatorsId
	 */
	@Column(name="CREATORS_ID",length=36,updatable=false)
	public String getCreatorsId() {
		return creatorsId;
	}


	/**
	 * 创建人ID.
	 * 
	 * CREATORS_ID
	 * @param creatorsId the creatorsId to set
	 */
	public void setCreatorsId(final String creatorsId) {
		this.creatorsId = creatorsId;
	}

}
