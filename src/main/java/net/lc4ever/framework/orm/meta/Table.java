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
package net.lc4ever.framework.orm.meta;

import java.util.List;

/**
 * @author q-wang
 */
public class Table {

	private String name;

	private String comments;

	private List<Column> columns;

	private PrimaryKey primaryKey;

	private List<UniqueKey> uniques;
	
	private String alterName;
	
	private String alterComments;
	
	private String javaClass;
	
	private String javaTemplate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<UniqueKey> getUniques() {
		return uniques;
	}

	public void setUniques(List<UniqueKey> uniques) {
		this.uniques = uniques;
	}

	public String getAlterName() {
		return alterName;
	}

	public void setAlterName(String alterName) {
		this.alterName = alterName;
	}

	public String getAlterComments() {
		return alterComments;
	}

	public void setAlterComments(String alterComments) {
		this.alterComments = alterComments;
	}

	public String getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	public String getJavaTemplate() {
		return javaTemplate;
	}

	public void setJavaTemplate(String javaTemplate) {
		this.javaTemplate = javaTemplate;
	}

	@Override
	public String toString() {
		return "Table [name=" + name + ", comments=" + comments + ", columns=" + columns + ", primaryKey=" + primaryKey
				+ ", uniques=" + uniques + ", alterName=" + alterName + ", alterComments=" + alterComments
				+ ", javaClass=" + javaClass + ", javaTemplate=" + javaTemplate + "]";
	}
}
