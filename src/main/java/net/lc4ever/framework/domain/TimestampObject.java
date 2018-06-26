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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 时间戳类Domain抽象父类.
 * 
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
@MappedSuperclass
//@FilterDefs({ @FilterDef(name = "deletionTag", defaultCondition = " 0 = DELETION_TAG ") })
//@Filter(name = "deletionTag")
public abstract class TimestampObject<K extends Serializable> extends AbstractEntity<K> {
	
	public static final String COLUMN_CREATE_TIMESTAMP = "CREATE_TIMESTAMP";
	public static final String COLUMN_MODIFY_TIMESTAMP = "MODIFY_TIMESTAMP";

	/**
	 * 创建时间戳.
	 * 
	 * CREATE_TIMESTAMP
	 */
	@Column(name="CREATE_TIMESTAMP")
	protected Date createTimestamp;

	/**
	 * 修改时间戳.
	 * 
	 * MODIFY_TIMESTAMP
	 */
	@Column(name="MODIFY_TIMESTAMP")
	protected Date modifyTimestamp;

//	/**
//	 * 删除标记位.
//	 * 
//	 * DELETION_TAG
//	 */
//	protected Boolean deletionTag = Boolean.FALSE;

	/**
	 * 创建时间戳.
	 * 
	 * CREATE_TIMESTAMP
	 * 
	 * @return the createTimestamp
	 */
	@Column(name = COLUMN_CREATE_TIMESTAMP, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	/**
	 * 创建时间戳.
	 * 
	 * CREATE_TIMESTAMP
	 * 
	 * @param createTimestamp the createTimestamp to set
	 */
	public void setCreateTimestamp(final Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	/**
	 * 修改时间戳.
	 * 
	 * MODIFY_TIMESTAMP
	 * 
	 * @return the modifyTimestamp
	 */
	@Column(name = COLUMN_MODIFY_TIMESTAMP)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getModifyTimestamp() {
		return modifyTimestamp;
	}

	/**
	 * 修改时间戳.
	 * 
	 * MODIFY_TIMESTAMP
	 * 
	 * @param modifyTimestamp the modifyTimestamp to set
	 */
	public void setModifyTimestamp(final Date modifyTimestamp) {
		this.modifyTimestamp = modifyTimestamp;
	}

//	/**
//	 * 删除标记位.
//	 * 
//	 * DELETION_TAG
//	 * 
//	 * @return the deletionTag
//	 */
//	@Column(name = "DELETION_TAG")
//	public Boolean getDeletionTag() {
//		return deletionTag;
//	}
//
//	/**
//	 * 删除标记位.
//	 * 
//	 * DELETION_TAG
//	 * 
//	 * @param deletionTag the deletionTag to set
//	 */
//	public void setDeletionTag(final Boolean deletionTag) {
//		this.deletionTag = deletionTag;
//	}

}
