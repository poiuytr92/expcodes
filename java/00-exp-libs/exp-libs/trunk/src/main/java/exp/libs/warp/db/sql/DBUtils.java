package exp.libs.warp.db.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.DBType;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

/**
 * <PRE>
 * 数据库工具类.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DBUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(DBUtils.class);
	
	/** DB重连间隔(ms) */
	private final static long RECONN_INTERVAL = 10000;
	
	/** DB连续重连次数上限 */
	private final static int RECONN_LIMIT = 10;
	
	/** 私有化构造函数 */
	protected DBUtils() {}
	
	/**
	 * 测试数据源连接是否可用
	 * @param ds
	 * @return
	 */
	public static boolean testConn(DataSourceBean ds) {
		boolean isOk = false;
		if(ds != null) {
			Connection conn = null;
			try {
				Class.forName(ds.getDriver());
				conn = DriverManager.getConnection(
						ds.getUrl(), ds.getUsername(), ds.getPassword());
				isOk = true;
				
			} catch (Throwable e) {}
			close(conn);
		}
		return isOk;
	}
	
	/**
	 * 获取数据库连接.
	 * 	在连接成功前，重试若干次(默认10次)
	 * @param ds 数据库配置信息
	 * @return 数据库连接
	 */
	public static Connection getConn(DataSourceBean ds) {
		return getConn(ds, RECONN_LIMIT);
	}
	
	/**
	 * 获取数据库连接.
	 * 	在连接成功前，重试若干次.
	 * @param ds
	 * @param retry 重试次数
	 * @return
	 */
	public static Connection getConn(DataSourceBean ds, int retry) {
		Connection conn = null;
		if(ds == null) {
			return conn;
		}
		
		int cnt = 0;	
		do {
			conn = _getConn(ds);
			if(conn != null) {
				break;
			}
			cnt++;
			ThreadUtils.tSleep(RECONN_INTERVAL);
		} while(retry < 0 || cnt < retry);
		return conn;
	}
	
	/**
	 * 获取数据库连接。
	 * 	先通过数据池获取，若数据池获取失败，则马上改用JDBC获取
	 * @param ds 数据源
	 * @return 数据库连接
	 */
	private static Connection _getConn(DataSourceBean ds) {
		Connection conn = getConnByPool(ds);
		if(conn == null) {
			conn = getConnByJDBC(ds);
		}
		return conn;
	}
	
	/**
	 * 通过连接池获取数据库连接
	 * @param ds 数据源
	 * @return 数据库连接
	 */
	public static Connection getConnByPool(DataSourceBean ds) {
		Connection conn = null;
		if(ds != null) {
			try {
				_DBUtils.getInstn().registerToProxool(ds);
				Class.forName(DBType.PROXOOL.DRIVER);
				conn = DriverManager.getConnection(
						DBType.PROXOOL.JDBCURL.replace(DBType.PH_ALIAS, ds.getId()));
				
			} catch (Throwable e) {
				if(!e.getMessage().contains("maximum connection count (0/0)")) {
					log.error("获取数据库 [{}] 连接失败.", ds.getName(), e);
				}
			}
		}
		return conn;
	}
	
	/**
	 * 通过JDBC获取数据库连接（备用方式，shutdown清理现场时无法通过数据池获取连接）
	 * @param ds 数据源
	 * @return 数据库连接
	 */
	public static Connection getConnByJDBC(DataSourceBean ds) {
		Connection conn = null;
		if(ds != null) {
			try {
				Class.forName(ds.getDriver());
				conn = DriverManager.getConnection(
						ds.getUrl(), ds.getUsername(), ds.getPassword());
				
			} catch (Throwable e) {
				log.error("获取数据库 [{}] 连接失败.", ds.getName(), e);
			}
		}
		return conn;
	}
	
	/**
	 * 开/关 数据库自动提交
	 * @param conn 数据库连接
	 * @param autoCommit 是否自动提交
	 */
	public static void setAutoCommit(Connection conn, boolean autoCommit) {
		if(conn != null) {
			try {
				conn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				log.error("开/关数据库自动提交失败.", e);
			}
		}
	}
	
	/**
	 * 关闭数据库连接
	 * @param conn 数据库连接
	 */
	public static void close(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("关闭数据库连接失败.", e);
			}
		}
	}
	
	public static boolean createBeanFromDB(Connection conn, String packageName,
			String outDirPath, String[] exportTables) {
		boolean isOk = true;
		try {
			List<String> exportTableList = 
					(exportTables == null ? null : Arrays.asList(exportTables));
			_DBUtils.createBeanFromDB(conn, packageName, outDirPath, exportTableList);
			
		} catch (Exception e) {
			isOk = false;
			log.error("构造JavaBean失败.", e);
		}
		return isOk;
	}
	
	public static boolean createBeanFromPDM(String pdmPath, String packageName,
			String outDirPath, String[] exportTables) {
		boolean isOk = true;
		try {
			List<String> exportTableList = 
					(exportTables == null ? null : Arrays.asList(exportTables));
			_DBUtils.createBeanFromPDM(pdmPath, packageName, outDirPath, exportTableList);
			
		} catch (Exception e) {
			isOk = false;
			log.error("构造JavaBean失败.", e);
		}
		return isOk;
	}
			
	
	/**
	 * 通过Connection判断数据库类型
	 * @param conn 数据库连接
	 * @return 数据库类型
	 */
	public static DBType judgeDBType(Connection conn) {
		DBType db = DBType.UNKNOW;
		if (conn == null) {
			return db;
		}
		
		try {
			String driver = conn.getMetaData().getDatabaseProductName();
			
			if (driver.toUpperCase().contains("MYSQL")) {
				db = DBType.MYSQL;
				
			} else if (driver.toUpperCase().contains("ORACLE")) {
				db = DBType.ORACLE;
				
			} else if (driver.toUpperCase().contains("ADAPTIVE SERVER ENTERPRISE")) {
				db = DBType.SYBASE;
				
			} else if (driver.toUpperCase().contains("SQLITE")) {
				db = DBType.SQLITE;
				
			} else if (driver.toUpperCase().contains("SQLSERVER")) {
				db = DBType.MSSQL;
				
			} else if (driver.toUpperCase().contains("POSTGRESQL")) {
				db = DBType.POSTGRESQL;
				
			} else if (driver.toUpperCase().contains("RMIJDBC") 
					|| driver.toUpperCase().contains("OBJECTWEB")) { 
				db = DBType.ACCESS;
				
			} else if (driver.toUpperCase().contains("DB2")) {
				db = DBType.IBM;
				
			}
		} catch (SQLException e) {
			log.error("判断数据库类型失败", e);
		}
		return db;
	}
	
	/**
	 * 仅适用于形如 【select key, value from table where ...】 的sql
	 * @param conn
	 * @param sql
	 * @return Map<key, value>
	 */
	public static Map<String, String> queryKVS(Connection conn, String sql) {
		Map<String, String> kvo = new HashMap<String, String>();

		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();

			ResultSetMetaData rsmd = null;
			while (rs.next()) {
				rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				if(count < 2) {
					break;
				}
				
				kvo.put(rs.getString(1), rs.getString(2));
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return kvo;
	}
	
	/**
	 * 从数据库查询kv表
	 * @param conn 数据库连接
	 * @param sql 查询sql
	 * @return Map<String, String>类型的kv表,不会返回null
	 */
	public static List<Map<String, String>> queryKVSs(Connection conn, String sql) {
		List<Map<String, String>> kvsList = new LinkedList<Map<String, String>>();

		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();

			ResultSetMetaData rsmd = null;
			while (rs.next()) {
				Map<String, String> kvs = new HashMap<String, String>();
				rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				for (int i = 1; i <= count; i++) {
					kvs.put(rsmd.getColumnLabel(i), rs.getString(i));
				}
				kvsList.add(kvs);
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return kvsList;
	}
	
	/**
	 * 仅适用于形如 【select key, value from table where ...】 的sql
	 * @param conn
	 * @param sql
	 * @return Map<key, value>
	 */
	public static Map<String, Object> queryKVO(Connection conn, String sql) {
		Map<String, Object> kvo = new HashMap<String, Object>();

		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();

			ResultSetMetaData rsmd = null;
			while (rs.next()) {
				rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				if(count < 2) {
					break;
				}
				
				kvo.put(rs.getString(1), rs.getObject(2));
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return kvo;
	}
	
	/**
	 * 从数据库查询kv表
	 * @param conn 数据库连接
	 * @param sql 查询sql
	 * @return Map<String, Object>类型的kv表,不会返回null
	 */
	public static List<Map<String, Object>> queryKVOs(Connection conn, String sql) {
		List<Map<String, Object>> kvsList = new LinkedList<Map<String, Object>>();

		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();

			ResultSetMetaData rsmd = null;
			while (rs.next()) {
				Map<String, Object> kvs = new HashMap<String, Object>();
				rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				for (int i = 1; i <= count; i++) {
					kvs.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
				kvsList.add(kvs);
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return kvsList;
	}
	
	/**
	 * 查询一个JavaBean对应的物理表
	 * @param clazz
	 * @param conn
	 * @param sql
	 * @return
	 */
	public static <BEAN> List<BEAN> query(Class<BEAN> clazz, Connection conn, String sql) {
		List<BEAN> beans = new LinkedList<BEAN>();
		try {
            QueryRunner runner = new QueryRunner();
            beans = runner.query(conn, sql, new BeanListHandler<BEAN>(clazz));
            
        } catch (Throwable e) {
        	log.error("执行sql失败: [{}].", sql, e);
        }
		return beans;
	}
	
	/**
	 * 查询第一行第一列的单元格值.
	 *  若返回的不是 1x1 的结果集，只取 [1][1] 作为返回值.
	 * @param conn
	 * @param sql
	 * @return
	 */
	public static Object queryFirstCellObj(Connection conn, String sql) {
		Object cell = null;
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				if(rsmd.getColumnCount() > 0) {
					cell = rs.getObject(1);
				}
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return cell;
	}
	
	public static String queryFirstCellStr(Connection conn, String sql) {
		String cell = "";
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				if(rsmd.getColumnCount() > 0) {
					cell = rs.getString(1);
				}
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return cell;
	}
	
	/**
	 * 查询第一行
	 * @param conn
	 * @param sql
	 * @return Map<String, Object>类型的kv表,不会返回null（key为表头）
	 */
	public static Map<String, Object> queryFirstRowObj(Connection conn, String sql) {
		Map<String, Object> row = new HashMap<String, Object>();
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= count; i++) {
					row.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return row;
	}
	
	public static Map<String, String> queryFirstRowStr(Connection conn, String sql) {
		Map<String, String> row = new HashMap<String, String>();
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= count; i++) {
					row.put(rsmd.getColumnLabel(i), rs.getString(i));
				}
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return row;
	}
	
	/**
	 * 从数据库查询第col列的所有值.
	 * @param sql 查询sql
	 * @param col 列号
	 * @return List<Object>列表,不会返回null
	 */
	public static List<Object> queryColumnObj(Connection conn, String sql, int col) {
		List<Object> vals = new LinkedList<Object>();
		col = (col <= 0 ? 1 : col);
		
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();

			int count = rs.getMetaData().getColumnCount();
			col = (col >= count ? count : col);
			while (rs.next()) {
				vals.add(rs.getObject(col));
			}
			
			rs.close();
			pstm.close();
		} catch (Exception e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return vals;
	}
	
	public static List<String> queryColumnStr(Connection conn, String sql, int col) {
		List<String> vals = new LinkedList<String>();
		col = (col <= 0 ? 1 : col);
		
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();

			int count = rs.getMetaData().getColumnCount();
			col = (col >= count ? count : col);
			while (rs.next()) {
				vals.add(rs.getString(col));
			}
			
			rs.close();
			pstm.close();
		} catch (Exception e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return vals;
	}
	
	/**
	 * 从数据库查询一个整数值.
	 * 若返回的不是 1x1 的结果集，只取 [1][1] 作为返回值.
	 * 
	 * @param conn 数据库连接
	 * @param sql 查询sql
	 * @return 查询失败则返回-1
	 */
	public static int queryInt(Connection conn, String sql) {
		int num = -1;

		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				num = rs.getInt(1);
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return num;
	}
	
	/**
	 * 从数据库查询一个长整数值.
	 * 若返回的不是 1x1 的结果集，只取 [1][1] 作为返回值.
	 * 
	 * @param conn 数据库连接
	 * @param sql 查询sql
	 * @return 查询失败则返回-1
	 */
	public static long queryLong(Connection conn, String sql) {
		long num = -1;

		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				num = rs.getLong(1);
			}
			
			rs.close();
			pstm.close();
			
		} catch (Throwable e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return num;
	}
	
	/**
	 * 执行预编译sql
	 * @param conn 数据库连接
	 * @param preSql 预编译sql
	 * @param params 参数表
	 * @return true:执行成功; false:执行失败
	 */
	public static boolean execute(Connection conn, String preSql, Object[] params) {
		boolean rst = false;
		try {
			PreparedStatement pstm = conn.prepareStatement(preSql);
			if(params != null) {
				for(int i = 0; i < params.length; i++) {
					if(params[i] == null) {
						pstm.setString(i + 1, null);
					} else {
						pstm.setObject(i + 1, params[i]);
					}
				}
			}
			pstm.execute();
			pstm.close();
			rst = true;
			
		} catch (Exception e) {
			log.error("执行sql失败: [{}].", preSql, e);
		}
		return rst;
	}
	
	/**
	 * 执行普通sql
	 * @param conn 数据库连接
	 * @param sql 普通sql
	 * @return true:执行成功; false:执行sql
	 */
	public static boolean execute(Connection conn, String sql) {
		boolean isOk = false;

		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.execute();
			pstm.close();
			isOk = true;
			
		} catch (Exception e) {
			log.error("执行sql失败: [{}].", sql, e);
		}
		return isOk;
	}
	
	
	
	/**
	 * <pre>
	 * 执行存储过程，获得简单返回值（支持[无返回值]和 [单值]返回两种形式）。
	 * 根据数据库连接自动识别 mysql、sybase、oracle。
	 * 
	 * 注意：
	 * 参数如果有null，则可能出错，特别是sybase数据库
	 * 
	 * mysql存储过程要求：
	 *  入参表：proSql的占位符?个数 必须与 入参表params长度相同，否则抛出SQLException异常。
	 * 	返回值：最后一个结果集（即SELECT语句）的第1行、第1列的值。
	 * 
	 * sybase存储过程要求：
	 *  入参表：proSql的占位符?个数 必须与 入参表params长度相同，否则抛出SQLException异常。
	 * 	返回值：return所指定的值。
	 * 
	 * oracle存储过程要求：
	 *  入参表：当proSql的占位符?个数 比 入参表params长度多0，为无返回值形式；
	 *       多1，为有返回值形式。其余情况抛出SQLException异常。
	 * 	返回值：当proSql的占位符?个数比入参表params多1，则认为最后1个占位符是出参。
	 * </pre>
	 * @param conn 数据库连接
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参表
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式。
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可。
	 * 
	 * @throws SQLException 不支持的数据库类型、或占位符与入参表个数不一致、或执行异常则抛出错误。
	 */
	public static String execSP(Connection conn, String proSql, Object[] params) {
		if(conn == null) {
			log.error("DB connection is closed.");
		}
		
		String result = null;
		DBType dbType = judgeDBType(conn);
		switch (dbType) {
			case MYSQL: {
				result = execSpByMysql(conn, proSql, params);
				break;
			}
			case SYBASE: {
				result = execSpBySybase(conn, proSql, params);
				break;
			}
			case ORACLE: {
				result = execSpByOracle(conn, proSql, params);
				break;
			}
			default: {
				result = "";
				log.error("Unsupport database types.");
			}
		}
		return result;
	}
	
	/**
	 * mysql存储过程调用，支持[无返回值]和 [单值]返回两种形式。
	 * 
	 * 要求：
	 *  入参表：proSql的占位符?个数 必须与 入参表params长度相同，否则抛出SQLException异常。
	 * 	返回值：最后一个结果集（即SELECT语句）的第1行、第1列的值。
	 * 
	 * @param conn 数据库连接
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参表
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式。
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可。
	 * 
	 * @throws SQLException 占位符与入参表个数不一致，或执行异常则抛出错误。
	 */
	private static String execSpByMysql(Connection conn, String proSql, Object[] params) {
		String result = null;
		if(conn == null) {
			log.error("DB connection is closed.");
			
		} else if(proSql == null) {
			log.error("Procedure Sql is null.");
			
		} else {
			int paramNum = (params == null ? 0 : params.length);
			int placeNum = StrUtils.count(proSql, '?');
			if(placeNum - paramNum != 0) {
				log.error("execute procedure [{}] fail: "
						+ "'?' count doesn't match params count.", proSql);
				
			} else {
				CallableStatement cs = null;
				ResultSet rs = null;
				try {
					proSql = proSql.trim();
					if(proSql.matches("^(?i)(call|exec) .*$")) {
						proSql = proSql.substring(5);
					}
					
					cs = conn.prepareCall("{ CALL " + proSql + " }");
					if(params != null){
						for(int i = 0; i < params.length; i++) {
							if (params[i] == null) {
								cs.setNull(i + 1, Types.INTEGER);
							} else {
								cs.setObject(i + 1, params[i]);
							}
						}
					}
					cs.executeQuery();
					
					//取最后一个结果集的首行首列值
					try {
						do {
							rs = cs.getResultSet();
							if(rs != null && rs.next()) {
								result = rs.getString(1);
							}
						} while(cs.getMoreResults() == true);
						
					} catch(NullPointerException e) {
						result = "";	// 存储过程无返回值
					}
					
				} catch (SQLException e) {
					log.error("execute procedure [{}] fail.", proSql, e);
					
				} finally {
					IOUtils.close(rs);
					IOUtils.close(cs);
				}
			}
		}
		return result;
	}
	
	/**
	 * sybase存储过程调用，支持[无返回值]和 [单值]返回两种形式。
	 * 
	 * 要求：
	 *  入参表：proSql的占位符?个数 必须与 入参表params长度相同，否则抛出SQLException异常。
	 * 	返回值：return所指定的值。
	 * 
	 * @param conn 数据库连接
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参表
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式。
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可。
	 * 
	 * @throws SQLException 占位符与入参表个数不一致，或执行异常则抛出错误。
	 */
	private static String execSpBySybase(Connection conn, String proSql, Object[] params) {
		String result = null;
		if(conn == null) {
			log.error("DB connection is closed.");
			
		} else if(proSql == null) {
			log.error("Procedure Sql is null.");
			
		} else {
			int paramNum = (params == null ? 0 : params.length);
			int placeNum = StrUtils.count(proSql, '?');
			if(placeNum - paramNum != 0) {
				log.error("execute procedure [{}] fail: "
						+ "'?' count doesn't match params count.", proSql);
				
			} else {
				CallableStatement cs = null;
				try {
					proSql = proSql.trim();
					if(proSql.matches("^(?i)(call|exec) .*$")) {
						proSql = proSql.substring(5);
					}
					
					cs = conn.prepareCall("{ ? = CALL " + proSql + " }");
					cs.registerOutParameter(1, Types.JAVA_OBJECT);
					if(params != null){
						for(int i = 0; i < params.length; i++) {
//							cs.setObject(i + 2, params[i]);
							if (params[i] == null) {
								cs.setNull(i + 2, Types.INTEGER);
							} else {
								cs.setObject(i + 2, params[i]);
							}
						}
					}
					cs.execute();
					result = cs.getString(1);
					
				} catch (SQLException e) {
					log.error("execute procedure [{}] fail.", proSql, e);
					
				} finally {
					IOUtils.close(cs);
				}
			}
		}
		return result;
	}
	
	/**
	 * oracle存储过程调用，支持[无返回值]和 [单值]返回两种形式。
	 * 
	 * 要求：
	 *  入参表：当proSql的占位符?个数 比 入参表params长度多0，为无返回值形式；
	 *       多1，为有返回值形式。其余情况抛出SQLException异常。
	 * 	返回值：当proSql的占位符?个数比入参表params多1，则认为最后1个占位符是出参。
	 * 
	 * @param conn 数据库连接
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参表
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式。
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可。
	 * 
	 * @throws SQLException 占位符与入参表个数不一致，或执行异常则抛出错误。
	 */
	private static String execSpByOracle(Connection conn, String proSql, Object[] params) {
		String result = null;
		if(conn == null) {
			log.error("DB connection is closed.");
			
		} else if(proSql == null) {
			log.error("Procedure Sql is null.");
			
		} else {
			int paramNum = (params == null ? 0 : params.length);
			int placeNum = StrUtils.count(proSql, '?');
			int diff = placeNum - paramNum;	// 占位符数 与 参数个数 的差异值
			
			if(diff != 0 && diff != 1) {
				log.error("execute procedure [{}] fail: "
						+ "'?' count doesn't match params count.", proSql);
				
			} else {
				CallableStatement cs = null;
				try {
					proSql = proSql.trim();
					if(proSql.matches("^(?i)(call|exec) .*$")) {
						proSql = proSql.substring(5);
					}
					
					cs = conn.prepareCall("{ CALL " + proSql + " }");
					int i = 0;
					if(params != null){
						for(; i < params.length; i++) {
							if (params[i] == null) {
								cs.setNull(i + 1, Types.INTEGER);
							} else {
								cs.setObject(i + 1, params[i]);
							}
						}
					}
					
					// 占位符数 比 参数个数 多1， 说明最后一个参数是出参
					if(diff == 1) {
						i = (i == 0 ? 1 : ++i);
						cs.registerOutParameter(i, Types.VARCHAR);
					}
					
					cs.execute();
					result = (diff == 1 ? cs.getString(i) : null);
					
				} catch (SQLException e) {
					log.error("execute procedure [{}] fail.", proSql, e);
					
				} finally {
					IOUtils.close(cs);
				}
			}
		}
		return result;
	}
	
	/**
	 * <pre>
	 * 调用存储过程，获取[结果集]返回。
	 * 根据数据库连接自动识别 mysql、sybase、oracle。
	 * </pre>
	 * 
	 * @param conn
	 * @param proSql
	 * @param params
	 * @return
	 */
	public static List<Map<String, Object>> callSP(Connection conn, 
			String proSql, Object[] params) {
		if(conn == null) {
			log.error("DB connection is closed.");
		}
		
		List<Map<String, Object>> result = null;
		DBType dbType = judgeDBType(conn);
		switch (dbType) {
			case MYSQL: {
				result = callSpByMysqlOrSybase(conn, proSql, params);
				break;
			}
			case SYBASE: {
				result = callSpByMysqlOrSybase(conn, proSql, params);
				break;
			}
			case ORACLE: {
				result = callSpByOracle(conn, proSql, params);
				break;
			}
			default: {
				result = new LinkedList<Map<String,Object>>();
				log.error("Unsupport database types.");
			}
		}
		return result;
	}
	
	/**
	 * 存储过程调用，支持[结果集]返回形式。
	 * 兼容mysql和sybase，不支持oralce。
	 * 
	 * 要求：
	 * 	入参表：proSql的占位符?个数 必须与 入参表params长度相同，否则抛出SQLException异常。
	 * 	返回值：最后一个结果集（即SELECT语句）。
	 *  
	 * @param conn 数据库连接
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参表
	 * @return 返回结果集的多行记录，每行为 列名-列值 的键值对。
	 * 
	 * @throws SQLException 不支持的数据库类型、或占位符与入参表个数不一致、或执行异常则抛出错误。
	 */
	private static List<Map<String, Object>> callSpByMysqlOrSybase(Connection conn, 
			String proSql, Object[] params) {
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		if(conn == null) {
			log.error("DB connection is closed.");
			
		} else if(proSql == null) {
			log.error("Procedure Sql is null.");
			
		} else {
			int paramNum = (params == null ? 0 : params.length);
			int placeNum = StrUtils.count(proSql, '?');
			if(placeNum - paramNum != 0) {
				log.error("execute procedure [{}] fail: "
						+ "'?' count doesn't match params count.", proSql);
				
			} else {
				CallableStatement cs = null;
				ResultSet rs = null;
				try {
					proSql = proSql.trim();
					if(proSql.matches("^(?i)(call|exec) .*$")) {
						proSql = proSql.substring(5);
					}
					
					cs = conn.prepareCall("{ CALL " + proSql + " }");
					if(params != null){
						for(int i = 0; i < params.length; i++) {
							cs.setObject(i + 1, params[i]);
						}
					}
					cs.executeQuery();
					
					//取最后一个结果集，拼装返回值
					do {
						rs = cs.getResultSet();
						if(rs != null) {
							result.clear();	//若有下一个结果集，则清空前一个结果集
							ResultSetMetaData rsmd = rs.getMetaData();
							int colCnt = rsmd.getColumnCount();
							
							Map<String, Object> rowMap = null;
							while(rs.next()) {
								
								rowMap = new HashMap<String, Object>();
								for(int i = 1; i <= colCnt; i++) {
									rowMap.put(rsmd.getColumnLabel(i), 
											rs.getObject(i));
								}
								result.add(rowMap);
							}
						}
					} while(cs.getMoreResults() == true);
					
				} catch (SQLException e) {
					log.error("execute procedure [{}] fail.", proSql, e);
					
				} finally {
					IOUtils.close(rs);
					IOUtils.close(cs);
				}
			}
		}
		return result;
	}
	
	/**
	 * oracle存储过程调用，仅支持[结果集]返回。
	 * 
	 * 要求：
	 *  入参表：proSql的占位符?个数 比 入参表params长度多1，且最后1个占位符为返回结果集。
	 *  	其余情况抛出SQLException异常。
	 * 	返回值：结果集。
	 * 
	 * @param conn Oracle数据库连接
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参表，长度必须必占位符少1
	 * @return 返回结果集的多行记录，每行为 列名-列值 的键值对。
	 * 
	 * @throws SQLException 占位符-入参表个数!=1，或执行异常则抛出错误。
	 */
	private static List<Map<String, Object>> callSpByOracle(
			Connection conn, String proSql, Object[] params) {
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		if(conn == null) {
			log.error("DB connection is closed.");
			
		} else if(proSql == null) {
			log.error("Procedure Sql is null.");
			
		} else {
			int paramNum = (params == null ? 0 : params.length);
			int placeNum = StrUtils.count(proSql, '?');
			int diff = placeNum - paramNum;	// 占位符数 与 参数个数 的差异值
			if(diff != 1) {
				log.error("execute procedure [{}] fail: "
						+ "'?' count doesn't match params count.", proSql);
				
			} else {
				CallableStatement cs = null;
				ResultSet rs = null;
				try {
					proSql = proSql.trim();
					if(proSql.matches("^(?i)(call|exec) .*$")) {
						proSql = proSql.substring(5);
					}
					
					cs = conn.prepareCall("{ CALL " + proSql + " }");
					int i = 0;
					if(params != null){
						for(; i < params.length; i++) {
							cs.setObject(i + 1, params[i]);
						}
					}
					
					//注册最后一个出参（游标类型）
					cs.registerOutParameter(++i, oracle.jdbc.OracleTypes.CURSOR);
					
					cs.execute();
					rs = cs.getResultSet();
					if(rs != null) {
						ResultSetMetaData rsmd = rs.getMetaData();
						int colCnt = rsmd.getColumnCount();
						Map<String, Object> rowMap = null;
						
						while(rs.next()) {
							rowMap = new HashMap<String, Object>();
							
							for(i = 1; i <= colCnt; i++) {
								rowMap.put(rsmd.getColumnLabel(i), 
										rs.getObject(i));
							}
							result.add(rowMap);
						}
					}
				} catch (SQLException e) {
					log.error("execute procedure [{}] fail.", proSql, e);
					
				} finally {
					IOUtils.close(rs);
					IOUtils.close(cs);
				}
			}
		}
		return result;
	}
}
