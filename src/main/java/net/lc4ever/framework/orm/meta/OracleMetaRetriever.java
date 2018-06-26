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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author q-wang
 */
public class OracleMetaRetriever implements MetaRetriever {

	private static final String SQL_COLUMNS = "select c.table_name, c.column_name, c.data_type, c.data_length, c.data_precision, c.data_scale, c.nullable, c.column_id, "
			+ "m.comments as column_comments,tm.comments as table_comments,con.position,con.constraint_name, con.constraint_type "
			+ " from sys.user_tab_cols c "
			+ " left join sys.user_col_comments m on m.table_name = c.table_name and m.column_name = c.column_name "
			+ " left join sys.user_tab_comments tm on tm.table_name = c.table_name "
			+ " left join (select c1.table_name,c1.column_name, c1.position, c2.constraint_name, c2.constraint_type "
			+ "        from sys.user_constraints c2"
			+ "        left join sys.user_cons_columns c1 on c1.constraint_name = c2.constraint_name "
			+ "        where c2.constraint_type <> 'C') con on con.table_name = c.table_name and con.column_name = c.column_name "
			+ " order by c.table_name,c.column_id";
	
	@SuppressWarnings("unused")
	private static final String SQL_META = "select * from meta_columns";
	
	public List<Table> tablesFromTable(Connection connection) {
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(SQL_COLUMNS);
	}

	/**
	 * @see net.lc4ever.framework.orm.meta.MetaRetriever#tables(java.sql.Connection)
	 */
	@Override
	public List<Table> tables(Connection connection) throws SQLException {
		Map<String, Table> tables = new HashMap<>();
		Map<String, Map<String, Column>> tableColumns = new HashMap<>();
		Map<String, List<Column>> primaryKeys = new HashMap<>();
		List<Table> results = new ArrayList<>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(SQL_COLUMNS)) {
			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");
				String columnName = resultSet.getString("COLUMN_NAME");
				String dataType = resultSet.getString("DATA_TYPE");
				int length = resultSet.getInt("DATA_LENGTH");
				int precision = resultSet.getInt("DATA_PRECISION");
				int scale = resultSet.getInt("DATA_SCALE");
				boolean nullable = resultSet.getString("NULLABLE").equals("Y");
				int columnId = resultSet.getInt("COLUMN_ID");
				String columnComments = resultSet.getString("COLUMN_COMMENTS");
				String tableComments = resultSet.getString("TABLE_COMMENTS");
				int keyPosition = resultSet.getInt("POSITION");
				String constraintName = resultSet.getString("CONSTRAINT_NAME");
				String constraintType = resultSet.getString("CONSTRAINT_TYPE");
				Table table = tables.get(tableName);
				if (table==null) {
					table = new Table();
					tableColumns.put(tableName, new HashMap<String, Column>());
					tables.put(tableName, table);
					results.add(table);
					
					table.setName(tableName);
					table.setComments(tableComments);
					table.setColumns(new ArrayList<Column>());
					table.setPrimaryKey(new PrimaryKey());
					
					primaryKeys.put(tableName, new ArrayList<Column>());
				}
				Map<String, Column> columns = tableColumns.get(tableName);
				Column column = columns.get(columnName);
				if (column==null) {
					column = new Column();
					columns.put(columnName, column);
					table.getColumns().add(column);

					column.setName(columnName);
					column.setComments(columnComments);
					column.setType(dataType);
					column.setLength(length);
					column.setPrecision(precision);
					column.setScale(scale);
					column.setNullable(nullable);
					column.setColumnId(columnId);
				}
				if ("P".equals(constraintType)) { // primaryKey found
					table.getPrimaryKey().setName(constraintName);
					table.getPrimaryKey().setType("P");
					table.getPrimaryKey().setColumn(keyPosition-1, column);
				}
			}
		}
		return results;
	}
}
