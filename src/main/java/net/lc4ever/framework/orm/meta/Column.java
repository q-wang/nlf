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

/**
 * @author q-wang
 */
public class Column {
	
	private String talbe;

	private String name;

	private String type;

	private int length;

	private int precision;

	private int scale;

	private boolean nullable;

	private int columnId;

	private String comments;

	private String alterReferenceTable;

	private String alterReferenceColumn;

	private String javaField;

	private String javaType;

	private String alterName;

	private String alterComments;

	private String alterType;

	private int alterLength;

	private int alterPrecision;

	private int alterScale;

	private boolean alterNullbale;

	private int alterColumnId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public String getAlterType() {
		return alterType;
	}

	public void setAlterType(String alterType) {
		this.alterType = alterType;
	}

	public int getAlterLength() {
		return alterLength;
	}

	public void setAlterLength(int alterLength) {
		this.alterLength = alterLength;
	}

	public int getAlterPrecision() {
		return alterPrecision;
	}

	public void setAlterPrecision(int alterPrecision) {
		this.alterPrecision = alterPrecision;
	}

	public int getAlterScale() {
		return alterScale;
	}

	public void setAlterScale(int alterScale) {
		this.alterScale = alterScale;
	}

	public boolean isAlterNullbale() {
		return alterNullbale;
	}

	public void setAlterNullbale(boolean alterNullbale) {
		this.alterNullbale = alterNullbale;
	}

	public int getAlterColumnId() {
		return alterColumnId;
	}

	public void setAlterColumnId(int alterColumnId) {
		this.alterColumnId = alterColumnId;
	}

	public String getAlterReferenceTable() {
		return alterReferenceTable;
	}

	public void setAlterReferenceTable(String alterReferenceTable) {
		this.alterReferenceTable = alterReferenceTable;
	}

	public String getAlterReferenceColumn() {
		return alterReferenceColumn;
	}

	public void setAlterReferenceColumn(String alterReferenceColumn) {
		this.alterReferenceColumn = alterReferenceColumn;
	}

	public String getJavaField() {
		return javaField;
	}

	public void setJavaField(String javaField) {
		this.javaField = javaField;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	
	public String getTalbe() {
		return talbe;
	}

	public void setTalbe(String talbe) {
		this.talbe = talbe;
	}

	@Override
	public String toString() {
		return "Column [talbe=" + talbe + ", name=" + name + ", type=" + type + ", length=" + length + ", precision="
				+ precision + ", scale=" + scale + ", nullable=" + nullable + ", columnId=" + columnId + ", comments="
				+ comments + ", alterReferenceTable=" + alterReferenceTable + ", alterReferenceColumn="
				+ alterReferenceColumn + ", javaField=" + javaField + ", javaType=" + javaType + ", alterName="
				+ alterName + ", alterComments=" + alterComments + ", alterType=" + alterType + ", alterLength="
				+ alterLength + ", alterPrecision=" + alterPrecision + ", alterScale=" + alterScale + ", alterNullbale="
				+ alterNullbale + ", alterColumnId=" + alterColumnId + "]";
	}
}
