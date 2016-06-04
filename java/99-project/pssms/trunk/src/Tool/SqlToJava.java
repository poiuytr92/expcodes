package Tool;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/*
 * SQL语句封装
 */

public class SqlToJava {

	//getClass().getName()可返回该值定义时的类名，返回值为 "java.lang.类名"
	//substring(10)则可把 "java.lang." 去掉
	private static String getValuesType(Object object) {
		if(object == null) {
			return null;
		}
		return object.getClass().getName().substring(10);
	}
	
	//insert into tableName(attributes[0], attributes[1], ...)
	//values(values[0], values[1], ...);
	public static boolean insert(String tableName, String attributes, Object ...values) {
		
		if(tableName==null || "".equals(tableName)) {
			return false;
		}
		
		if(attributes==null || "".equals(attributes) || values.length==0) {
			return false;
		}
		
		if(attributes.split(",").length != values.length) {
			return false;
		}
		
		String valuesString = "";
		for(Object obj : values) {
			if(obj == null) {
				valuesString += "null, ";
			}
			else if("Integer".equals(getValuesType(obj)) || "Double".equals(getValuesType(obj)) || "Float".equals(getValuesType(obj))) {
				valuesString += obj.toString() + ", ";
			}
			else {
				valuesString += "'" + obj.toString() + "', ";
			}
		}
		valuesString = valuesString.substring(0, valuesString.length()-2);
		
		try {
			
			Statement statement = LinkToDB.connection.createStatement();
			String insertSQL = "insert into " + tableName + "(" + attributes + ") values(" + valuesString +");";
			statement.execute(insertSQL);
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//delete from tableName
	//where conditionAttribute = conditionValue;
	public static boolean delete(String tableName, String conditionAttribute, Object...conditionValue) {
		
		if(tableName==null || "".equals(tableName)) {
			return false;
		}
		
		if(conditionAttribute==null || "".equals(conditionAttribute)) {
			return false;
		}
		
		if(conditionValue == null) {
			return false;
		}
		
		String conditionString = getConditionString(conditionAttribute, conditionValue);
		if (conditionString == null) {
			return false;
		}
		
		
		try {
			
			Statement statement = LinkToDB.connection.createStatement();
			String deleteSQL = "delete from " + tableName + conditionString + ";";
			statement.execute(deleteSQL);
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//update tableName
	//set resetAttributes[0]=resetValues[0], resetAttributes[1]=resetValues[1], ...
	//where conditionAttribute = conditionValue;
	public static boolean update(String tableName, String conditionAttribute, Object conditionValue, String resetAttributes, Object ...resetValues) {
		
		if(tableName==null || "".equals(tableName)) {
			return false;
		}
		
		if(conditionAttribute==null || "".equals(conditionAttribute)) {
			return false;
		}
		
		if(conditionValue == null) {
			return false;
		}
		
		if(resetAttributes==null || "".equals(resetAttributes)) {
			return false;
		}
		
		if (resetValues==null || resetValues.length==0) {
			return false;
		}
		
		String[] attributes = resetAttributes.split(",");
		if(attributes.length != resetValues.length) {
			return false;
		}
		
		String resetString = "";
		for(int i=0; i<attributes.length; i++) {
			if(resetValues[i] == null) {
				resetString += attributes[i] + " = null, ";
			}
			else if("Integer".equals(getValuesType(resetValues[i])) || "Double".equals(getValuesType(resetValues[i])) || "Float".equals(getValuesType(resetValues[i]))) {
				resetString += attributes[i] + " = " + resetValues[i].toString() + ", ";
			}
			else {
				resetString += attributes[i] + " = '" + resetValues[i].toString() + "', ";
			}
		}
		resetString = resetString.substring(0, resetString.length()-2);
		
		if (!("Integer".equals(getValuesType(conditionValue)) || "Double".equals(getValuesType(conditionValue)) || "Float".equals(getValuesType(conditionValue)))) {
			conditionValue =  "'" + conditionValue + "'";
		}
		
		try {
			
			Statement statement = LinkToDB.connection.createStatement();
			String updateSQL = "update " + tableName + " set " + resetString + " where " + conditionAttribute + " = " + conditionValue + ";";
			statement.execute(updateSQL);
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//select * from tableName;
	public static Vector<Vector<Object>> select(String tableName) {
		
		return select(tableName, "*", null);
	}
	
	//select selectAttributes[0], selectAttributes[1], ...
	//from tableName;
	public static Vector<Vector<Object>> select(String tableName, String selectAttributes) {
		
		return select(tableName, selectAttributes, null);
	}
	
	
	/*
	 * SQL:select * from tableName 
	 * 函数:select(tableName, "*", null)
	 */
	/*
	 * SQL:select selectAttributes[0], selectAttributes[1], ...
	 *     from tableName 
	 * 函数:select(tableName, "selectAttributes[0], selectAttributes[1], ...", null)
	 */
	/*
	 * SQL:select selectAttributes[0], selectAttributes[1], ...
	 *     from tableName
	 *     where conditionAttribute[0]=conditionValue[0], conditionAttribute[1]=conditionValue[1], ...;
	 * 函数:select(tableName, "selectAttributes[0], selectAttributes[1]...", "conditionAttribute[0], conditionAttribute[1]...", conditionValue[0], conditionValue[1]...)
	 */
	public static Vector<Vector<Object>> select(String tableName, String selectAttributes, String conditionAttribute, Object...conditionValue) {
		
		Vector<Vector<Object>> objectTabel = new Vector<Vector<Object>>();
		
		if(tableName==null || "".equals(tableName)) {
			return objectTabel;
		}
		
		if(selectAttributes==null || "".equals(selectAttributes)) {
			return objectTabel;
		}
		
		
		String conditionString = getConditionString(conditionAttribute, conditionValue);
		if (conditionString == null) {
			return objectTabel;
		}
		
		
		ResultSet resultSet = null;
		
		try {
			String selectSQL = "select "+ selectAttributes +" from " + tableName + conditionString + ";";

			Statement statement = LinkToDB.connection.createStatement();
			resultSet = statement.executeQuery(selectSQL);
			ResultSetMetaData metaData = resultSet.getMetaData();
			int colCnt = metaData.getColumnCount();
			
			while(resultSet.next()) {
				
				Vector<Object> objectVector = new Vector<Object>();
				for(int c=1; c<=colCnt; c++) {
					objectVector.add(resultSet.getObject(c));
				}
				objectTabel.add(objectVector);
			}
			
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return objectTabel;
	}
	
	
	//直接执行sql的insert, delete, update语句
	public static void execute(String sql) {
		try {
			
			Statement statement = LinkToDB.connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//直接执行sql的select语句
	public static Vector<Vector<Object>> executeQuerty(String sql) {
		
		Vector<Vector<Object>> objectTabel = new Vector<Vector<Object>>();
		ResultSet resultSet = null;
		
		try {
			
			Statement statement = LinkToDB.connection.createStatement();
			
			resultSet = statement.executeQuery(sql);
			ResultSetMetaData metaData = resultSet.getMetaData();
			int colCnt = metaData.getColumnCount();
			
			while(resultSet.next()) {
				
				Vector<Object> objectVector = new Vector<Object>();
				for(int c=1; c<=colCnt; c++) {
					objectVector.add(resultSet.getObject(c));
				}
				objectTabel.add(objectVector);
			}

			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return objectTabel;
	}
	
	//根据条件字段和条件值获取sql的条件语句
	private static String getConditionString(String conditionAttribute, Object...conditionValue) {
		
		String conditionString = null;
		
		if (conditionAttribute==null || "".equals(conditionAttribute) || conditionValue==null || conditionValue.length == 0) {
			conditionString = "";
			
		} else {
			
			String[] attributes = conditionAttribute.split(",");
			if(attributes.length != conditionValue.length) {
				return null;
			}
			
			conditionString = " where ";
			for(int i=0; i<attributes.length; i++) {
				if(conditionValue[i] == null) {
					conditionString += attributes[i] + " = null and ";
				}
				else if("Integer".equals(getValuesType(conditionValue[i])) || "Double".equals(getValuesType(conditionValue[i])) || "Float".equals(getValuesType(conditionValue[i]))) {
					conditionString += attributes[i] + " = " + conditionValue[i].toString() + " and ";
				}
				else {
					conditionString += attributes[i] + " = '" + conditionValue[i].toString() + "' and ";
				}
			}
			conditionString = conditionString.substring(0, conditionString.length()-5);
			
		}
		
		return conditionString;
	}
}


/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */