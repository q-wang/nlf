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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author q-wang
 */
public class MetaColumn {

	public static final String SQL_TABLE_CREATE = "create table META_COLUMNS ("
			+ " TABLE_NAME varchar2(32) not null, COLUMN_NAME varchar2(32) not null, DATA_TYPE varchar2(32) not null, DATA_LENGTH number(11) not null, "
			+ " DATA_PRECISION number(11), DATA_SCALE number(11), NULLABLE char(1) not null, COLUMN_ID number(11) not null,"
			+ " COLUMN_COMMENTS varchar2(1000), TABLE_COMMENTS varchar2(1000), KEY_POSITION number(11), CONSTRAINT_NAME varchar(32),"
			+ " CONSTRAINT_TYPE char(1), ALTER_TABLE_NAME varchar2(32), ALTER_TABLE_COMMENTS varchar2(255), ALTER_COLUMN_NAME varchar2(32),"
			+ " ALTER_COLUMN_COMMENTS varchar2(255), ALTER_DATA_TYPE varchar2(32), ALTER_LENGTH number(11), ALTER_PRECISION number(11),"
			+ " ALTER_SCALE number(11), ALTER_NULLABLE char(1), ALTER_COLUMN_ID number(11), ALTER_REFERENCE_TABLE varchar2(32),"
			+ " ALTER_REFERENCE_COLUMN varchar2(32), ALTER_CONSTRAINT_NAME varchar2(32), JAVA_CLASS varchar2(255), JAVA_TEMPLATE varchar2(32),"
			+ " JAVA_FIELD varchar2(255), JAVA_TYPE varchar2(255), primary key (TABLE_NAME, COLUMN_NAME) )";

	public static final String SQL_TABLE_DROP = "drop table META_COLUMNS";

	public static final String SQL_DATA_INIT = "insert into META_COLUMNS ("
			+ "		TABLE_NAME, COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_SCALE, NULLABLE, "
			+ " 	COLUMN_ID, COLUMN_COMMENTS, TABLE_COMMENTS, KEY_POSITION, CONSTRAINT_NAME, CONSTRAINT_TYPE) "
			+ " select COL.TABLE_NAME, COL.COLUMN_NAME, COL.DATA_TYPE, COL.DATA_LENGTH, COL.DATA_PRECISION, COL.DATA_SCALE,"
			+ "		COL.NULLABLE, COL.COLUMN_ID, CM.COMMENTS AS COLUMN_COMMENTS, TM.COMMENTS AS TABLE_COMMENTS,"
			+ "		CON.KEY_POSITION, CON.CONSTRAINT_NAME, CON.CONSTRAINT_TYPE" + " from SYS.USER_TAB_COLS COL "
			+ " left join SYS.USER_COL_COMMENTS CM on CM.TABLE_NAME = COL.TABLE_NAME and CM.COLUMN_NAME = COL.COLUMN_NAME "
			+ " left join SYS.USER_TAB_COMMENTS TM on TM.TABLE_NAME = COL.TABLE_NAME " + " left join ( "
			+ " 	select C1.TABLE_NAME, C1.COLUMN_NAME, C1.POSITION as KEY_POSITION, C2.CONSTRAINT_NAME, C2.CONSTRAINT_TYPE "
			+ "		from SYS.USER_CONSTRAINTS c2 "
			+ "		left join SYS.USER_CONS_COLUMNS C1 on C1.CONSTRAINT_NAME = C2.CONSTRAINT_NAME"
			+ "		where C2.CONSTRAINT_TYPE = 'P' "
			+ "	) CON on CON.TABLE_NAME = COL.TABLE_NAME and CON.COLUMN_NAME = COL.COLUMN_NAME ";

	public static final String[] SQL_DATA_PREPARE = new String[] {
			"update META_COLUMNS set ALTER_DATA_TYPE = DATA_TYPE, ALTER_TABLE_NAME = TABLE_NAME, ALTER_TABLE_COMMENTS = TABLE_COMMENTS, "
					+ " ALTER_COLUMN_NAME = COLUMN_NAME, ALTER_COLUMN_COMMENTS = COLUMN_COMMENTS, ALTER_LENGTH = DATA_LENGTH, "
					+ " ALTER_PRECISION = DATA_PRECISION, ALTER_SCALE = DATA_SCALE, ALTER_NULLABLE = NULLABLE, "
					+ "	ALTER_COLUMN_ID = COLUMN_ID, ALTER_CONSTRAINT_NAME = CONSTRAINT_NAME",
			"update meta_columns set java_type = 'String' where data_type in('VARCHAR','VARCHAR2','CLOB','CHAR','NVARCHAR2')",
			"update meta_columns set java_type = 'char' where data_type = 'CHAR' and data_length = 1 and nullable = 'N'",
			"update meta_columns set java_type = 'Timestamp', alter_data_type = 'DATE' where data_type = 'TIMESTAMP(6)' or data_type = 'TIMESTAMP(3)' or column_name in ('INS_DATE', 'UPD_DATE')",
			"update meta_columns set java_type = 'Timestamp' where data_type = 'DATE'",
			"update meta_columns set java_type = 'int' where data_type = 'NUMBER' and data_precision <= 11 and data_scale = 0 and nullable = 'N'",
			"update meta_columns set java_type = 'String', alter_data_type = 'VARCHAR2', alter_length = 32 where table_name = 'TRACK_LOG_LINK_VISIT_ACTION' and column_name = 'CUSTOM_FLOAT'",
			"update meta_columns set java_type = 'Integer' where data_type = 'NUMBER' and data_precision <=11 and data_scale = 0 and nullable = 'Y'",
			"update meta_columns set java_type = 'long' where data_type = 'NUMBER' and data_precision <=19 and data_scale = 0 and nullable = 'N'",
			"update meta_columns set java_type = 'Long' where data_type = 'NUMBER' and data_precision <=19 and data_scale = 0 and nullable = 'Y'",
			"update meta_columns set java_type = 'java.math.BigDecimal' where data_type = 'NUMBER' and data_scale <> 0 and data_precision is not null",
			"update meta_columns set java_type = 'String', alter_data_type = 'VARCHAR2', alter_length = 20 where table_name = 'CUSTOMER_AGENT_M' and column_name = 'CODE'",
			"update meta_columns set java_type = 'int', alter_precision = 11, alter_scale = 0 where table_name = 'ACCESS_CONTROL' and column_name = 'MENU_ID'",
			"update meta_columns set java_type = 'int', alter_precision = 11 where table_name = 'TRACK_LOG_VISIT' and column_name = 'CONFIG_DEVICE_TYPE'",
			"update meta_columns set java_type = 'int', alter_precision = 11 where table_name = 'APP_VERSION' and column_name = 'version_type'",
			"update meta_columns set java_type = 'int', alter_precision = 11 where table_name = 'SYSTEM_PARAM_ITEM' and column_name = 'ITEM_ORDER'",
			"update meta_columns set java_type = 'int', alter_precision = 11 where table_name = 'ADMIN_MENU_M' and column_name = 'MENU_ID'",
			"update meta_columns set java_type = 'String', alter_data_type = 'VARCHAR2', alter_length = 20 where table_name = 'AREAS' and column_name = 'PARENT_ID'", };

	/** @formatter:off */
	public static final Map<String, String[]> TABLE_CLASS_MAPPING = Collections.unmodifiableMap(new HashMap<String, String[]>() {{
	}});

	/** @formatter:on */
	public static final String SQL_DATA_CLEAR = "truncate table META_COLUMNS";

	public static final RowMapper<MetaColumn> MAPPER = new RowMapper<MetaColumn>() {
		@Override
		public MetaColumn mapRow(ResultSet rs, int rowNum) throws SQLException {
			MetaColumn meta = new MetaColumn();
			meta.tableName = rs.getString("TABLE_NAME");
			meta.columnName = rs.getString("COLUMN_NAME");
			meta.dataType = rs.getString("DATA_TYPE");
			meta.dataLength = rs.getInt("DATA_LENGTH");
			meta.dataPrecision = rs.getInt("DATA_PRECISION");
			meta.dataScale = rs.getInt("DATA_SCALE");
			meta.nullbale = rs.getString("NULLABLE").equals("Y");
			meta.columnId = rs.getInt("COLUMN_ID");
			meta.columnComments = rs.getString("COLUMN_COMMENTS");
			meta.tableComments = rs.getString("TABLE_COMMENTS");
			meta.keyPosition = rs.getInt("KEY_POSITION");
			meta.constraintName = rs.getString("CONSTRAINT_NAME");
			meta.constraintType = rs.getString("CONSTRAINT_TYPE");
			meta.alterTableName = rs.getString("ALTER_TABLE_NAME");
			meta.alterTableComments = rs.getString("ALTER_TABLE_COMMENTS");
			meta.alterColumnName = rs.getString("ALTER_COLUMN_NAME");
			meta.alterColumnComments = rs.getString("ALTER_COLUMN_COMMENTS");
			meta.alterDataType = rs.getString("ALTER_DATA_TYPE");
			meta.alterLength = rs.getInt("ALTER_LENGTH");
			meta.alterPrecision = rs.getInt("ALTER_PRECISION");
			meta.alterScale = rs.getInt("ALTER_SCALE");
			meta.alterNullable = rs.getString("ALTER_NULLABLE").equals("Y");
			meta.alterReferenceTable = rs.getString("ALTER_REFERENCE_TABLE");
			meta.alterReferenceColumn = rs.getString("ALTER_REFERENCE_COLUMN");
			meta.alterConstraintName = rs.getString("ALTER_CONSTRAINT_NAME");
			meta.javaClass = rs.getString("JAVA_CLASS");
			meta.javaTemplate = rs.getString("JAVA_TEMPLATE");
			meta.javaField = rs.getString("JAVA_FIELD");
			meta.javaType = rs.getString("JAVA_TYPE");
			meta.javaLegacyClass = rs.getString("JAVA_LEGACY_CLASS");
			meta.javaLegacyField = rs.getString("JAVA_LEGACY_FIELD");
			meta.javaLegacyType = rs.getString("JAVA_LEGACY_TYPE");
			return meta;
		}
	};

	private String tableName;

	private String columnName;

	private String dataType;

	private int dataLength;

	private int dataPrecision;

	private int dataScale;

	private boolean nullbale;

	private int columnId;

	private String columnComments;

	private String tableComments;

	private int keyPosition;

	private String constraintName;

	private String constraintType;

	private String alterTableName;

	private String alterTableComments;

	private String alterColumnName;

	private String alterColumnComments;

	private String alterDataType;

	private int alterLength;

	private int alterPrecision;

	private int alterScale;

	private boolean alterNullable;

	private int alterColumnId;

	private String alterReferenceTable;

	private String alterReferenceColumn;

	private String alterConstraintName;

	private String javaClass;

	private String javaTemplate;

	private String javaField;

	private String javaType;

	private String javaLegacyClass;

	private String javaLegacyField;

	private String javaLegacyType;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(int dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public int getDataScale() {
		return dataScale;
	}

	public void setDataScale(int dataScale) {
		this.dataScale = dataScale;
	}

	public boolean isNullbale() {
		return nullbale;
	}

	public void setNullbale(boolean nullbale) {
		this.nullbale = nullbale;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public String getColumnComments() {
		return columnComments;
	}

	public void setColumnComments(String columnComments) {
		this.columnComments = columnComments;
	}

	public String getTableComments() {
		return tableComments;
	}

	public void setTableComments(String tableComments) {
		this.tableComments = tableComments;
	}

	public int getKeyPosition() {
		return keyPosition;
	}

	public void setKeyPosition(int keyPosition) {
		this.keyPosition = keyPosition;
	}

	public String getConstraintName() {
		return constraintName;
	}

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}

	public String getAlterTableName() {
		return alterTableName;
	}

	public void setAlterTableName(String alterTableName) {
		this.alterTableName = alterTableName;
	}

	public String getAlterTableComments() {
		return alterTableComments;
	}

	public void setAlterTableComments(String alterTableComments) {
		this.alterTableComments = alterTableComments;
	}

	public String getAlterColumnName() {
		return alterColumnName;
	}

	public void setAlterColumnName(String alterColumnName) {
		this.alterColumnName = alterColumnName;
	}

	public String getAlterColumnComments() {
		return alterColumnComments;
	}

	public void setAlterColumnComments(String alterColumnComments) {
		this.alterColumnComments = alterColumnComments;
	}

	public String getAlterDataType() {
		return alterDataType;
	}

	public void setAlterDataType(String alterDataType) {
		this.alterDataType = alterDataType;
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

	public boolean isAlterNullable() {
		return alterNullable;
	}

	public void setAlterNullable(boolean alterNullable) {
		this.alterNullable = alterNullable;
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

	public String getAlterConstraintName() {
		return alterConstraintName;
	}

	public void setAlterConstraintName(String alterConstraintName) {
		this.alterConstraintName = alterConstraintName;
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

	public String getJavaLegacyClass() {
		return javaLegacyClass;
	}

	public void setJavaLegacyClass(String javaLegacyClass) {
		this.javaLegacyClass = javaLegacyClass;
	}

	public String getJavaLegacyField() {
		return javaLegacyField;
	}

	public void setJavaLegacyField(String javaLegacyField) {
		this.javaLegacyField = javaLegacyField;
	}

	public String getJavaLegacyType() {
		return javaLegacyType;
	}

	public void setJavaLegacyType(String javaLegacyType) {
		this.javaLegacyType = javaLegacyType;
	}

}
