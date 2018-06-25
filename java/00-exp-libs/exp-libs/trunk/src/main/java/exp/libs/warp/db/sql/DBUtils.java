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
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.DBType;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

/**
 * <PRE>
 * 数据库工具.
 * 
 * 使用示例:
 * 	DataSourceBean ds = new DataSourceBean();
 * 	ds.setDriver(DBType.MYSQL.DRIVER);
 * 	ds.set....
 * 
 * 	Connection conn = DBUtils.getConn(ds);
 * 	DBUtils.query(DBUtils, sql);
 * 	DBUtils.close(conn);
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DBUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(DBUtils.class);
	
	/** DB重连间隔(ms) */
	private final static long RECONN_INTERVAL = 10000;
	
	/** DB连续重连次数上限 */
	private final static int RECONN_LIMIT = 10;
	
	/** 私有化构造函�? */
	protected DBUtils() {}
	
	/**
	 * <PRE>
	 * 设置是否自动关闭线程�?.
	 * 
	 * 	使用场景�?
	 *   通常当在进程销毁钩子中需要入库操作时, 若使用线程池, 则必定报错：
	 * 		Attempt to refer to a unregistered pool by its alias xxx
	 * 
	 *   这是因为proxool总是最先向JVM请求销毁自�?, 导致在进程销毁钩子无法使用线程池, 只能使用常规的JDBC操作.
	 *   
	 *   通过此方�?, 在程序使用线程池之前设置 {@link #setAutoShutdownPool() false} 可以避免这种主动销毁的行为
	 *   但是在进程钩子的最�?, 需要手动调�? {@link #shutdownPool()} 方法关闭线程�?
	 * </PRE>
	 * @param auto true:自动关闭线程�?(默认); false:手动关闭线程�?
	 */
	public static void setAutoShutdownPool(boolean auto) {
		if(auto == true) {
			ProxoolFacade.enableShutdownHook();
		} else {
			ProxoolFacade.disableShutdownHook();
		}
	}
	
	/**
	 * 马上关闭线程�?
	 */
	public static void shutdownPool() {
		shutdownPool(0);
	}
	
	/**
	 * 延迟一段时间后关闭线程�?
	 * @param delay 延迟时间, 单位:ms
	 */
	public static void shutdownPool(int delay) {
		ProxoolFacade.shutdown(delay < 0 ? 0 : delay);
	}
	
	/**
	 * 测试数据源连接是否可�?
	 * @param ds 数据�?
	 * @return true:连接可用; false:连接不可�?
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
	 * 通过Connection判断数据库类�?
	 * @param conn 数据库连�?
	 * @return 数据库类�?
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
			log.error("判断数据库类型失�?", e);
		}
		return db;
	}
	
	/**
	 * <PRE>
	 * 获取数据库连�?(先通过连接池获取，若连接池获取失败，则改用JDBC获取).
	 * 	在连接成功前，重试若干次(默认10�?)
	 * <PRE>
	 * @param ds 数据库配置信�?
	 * @return 数据库连�?(若连接失败返回null)
	 */
	public static Connection getConn(DataSourceBean ds) {
		return getConn(ds, RECONN_LIMIT);
	}
	
	/**
	 * <PRE>
	 * 获取数据库连�?(先通过连接池获取，若连接池获取失败，则改用JDBC获取).
	 * 	在连接成功前，重试若干次.
	 * </PRE>
	 * @param ds 数据库配置信�?
	 * @param retry 重试次数
	 * @return 数据库连�?(若连接失败返回null)
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
	 * <PRE>
	 * 获取数据库连接�?
	 * 	先通过连接池获取，若连接池获取失败，则改用JDBC获取
	 * <PRE>
	 * @param ds 数据库配置信�?
	 * @return 数据库连�?(若连接失败返回null)
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
	 * @param ds 数据库配置信�?
	 * @return 数据库连�?(若连接失败返回null)
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
				String errMsg = e.getMessage();
				if(errMsg != null && errMsg.contains("maximum connection count (0/0)")) {
					// Undo 连接正常
				} else {
					log.error("获取数据�? [{}] 连接失败.", ds.getName(), e);
				}
			}
		}
		return conn;
	}
	
	/**
	 * <PRE>
	 * 通过JDBC获取数据库连�?.
	 * （在shutdown等场景下无法通过连接池获取连接，此时需用JDBC方式�?
	 * <PRE>
	 * @param ds 数据库配置信�?
	 * @return 数据库连�?(若连接失败返回null)
	 */
	public static Connection getConnByJDBC(DataSourceBean ds) {
		Connection conn = null;
		if(ds != null) {
			try {
				Class.forName(ds.getDriver());
				conn = DriverManager.getConnection(
						ds.getUrl(), ds.getUsername(), ds.getPassword());
				
			} catch (Throwable e) {
				log.error("获取数据�? [{}] 连接失败.", ds.getName(), e);
			}
		}
		return conn;
	}
	
	/**
	 * 开/�? 数据库自动提�?
	 * @param conn 数据库连�?
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
	 * 关闭数据库连�?
	 * @param conn 数据库连�?
	 */
	public static void close(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("关闭数据库连接失�?.", e);
			}
		}
	}
	
	/**
	 * <PRE>
	 * 根据物理表生成对应的JavaBean类文件（类似Bean与Dao的复合对象）.
	 * 	表名和列名会自动做前缀删除和驼峰处�?.
	 * 	例如�?
	 * 		表名�? T_CP_USER�? 类名则为 CpUser
	 * 		列名�? I_ID，类成员域名则为iId
	 * <PRE>
	 * @param conn 数据库连�?
	 * @param packageName 所生成的JavaBean类文件的包路�?, �?: foo.bar.bean
	 * @param outDirPath 所生成的JavaBean类文件的存储路径, �?: ./src/main/java/foo/bar/bean
	 * @param exportTables 需要导出为JavaBean的物理表名集（若为空则全库导出）
	 * @return true:成功; false:失败
	 */
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
	
	/**
	 * <PRE>
	 * 根据物理模型生成对应的JavaBean类文件（类似Bean与Dao的复合对象）.
	 * 	表名和列名会自动做前缀删除和驼峰处�?.
	 * 	例如�?
	 * 		表名�? T_CP_USER�? 类名则为 CpUser
	 * 		列名�? I_ID，类成员域名则为iId
	 * <PRE>
	 * @param pdmPath 物理模型文件路径（支持PowerDesigner�?
	 * @param packageName 所生成的JavaBean类文件的包路�?, �?: foo.bar.bean
	 * @param outDirPath 所生成的JavaBean类文件的存储路径, �?: ./src/main/java/foo/bar/bean
	 * @param exportTables 需要导出为JavaBean的物理表名集（若为空则全库导出）
	 * @return true:成功; false:失败
	 */
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
	 * 查询一个JavaBean对应的物理表数据，并把对应列值转存到JavaBean对应的成员域.
	 * @param clazz JavaBean类定�?
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return JavaBean对象列表（查询失败返回null�?
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
	 * <PRE>
	 * 查询键值对(其中值会被强制转换为String类型).
	 * 	仅适用于形�? 【select key, value from table where ...�? 的sql
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 键值对查询SQL
	 * @return Map<key, value> 键值对�? （不会返回null�?
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
	 * <PRE>
	 * 查询键值对(其中值会保留其原本数据类�?).
	 * 	仅适用于形�? 【select key, value from table where ...�? 的sql
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 键值对查询SQL
	 * @return Map<key, value> 键值对�? （不会返回null�?
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
	 * <PRE>
	 * 查询多行表数�?.
	 * 	每行数据以列名为key，以列值为val（列值会被强制转换成String类型�?.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return List<Map<colName, colVal>> (不会返回null)
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
	 * <PRE>
	 * 查询多行表数�?.
	 * 	每行数据以列名为key，以列值为val（列值会保留其原本数据类型）.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return List<Map<colName, colVal>> (不会返回null)
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
	 * <PRE>
	 * 查询一个int整数�?.
	 *  若返回的不是 1x1 的结果集，只�? [1][1] 作为返回�?.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return 查询结果（查询失败则返回-1�?
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
	 * <PRE>
	 * 查询一个long整数�?.
	 *  若返回的不是 1x1 的结果集，只�? [1][1] 作为返回�?.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return 查询结果（查询失败则返回-1�?
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
	 * <PRE>
	 * 查询[第一行][第一列]的单元格值（所得值强制转换为String类型�?.
	 *  若返回的不是 1x1 的结果集，只�? [1][1] 作为返回�?.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return 查询结果（查询失败则返回null�?
	 */
	public static String queryCellStr(Connection conn, String sql) {
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
	 * <PRE>
	 * 查询[第一行][第一列]的单元格值（所得值保留其原本的数据类型）.
	 *  若返回的不是 1x1 的结果集，只�? [1][1] 作为返回�?.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return 查询结果（查询失败则返回null�?
	 */
	public static Object queryCellObj(Connection conn, String sql) {
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
	
	/**
	 * <PRE>
	 * 查询[第一行]表数�?.
	 * 	行数据以列名为key，以列值为val（列值会被强制转换成String类型�?.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return Map<colName, colVal> (不会返回null)
	 */
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
	 * <PRE>
	 * 查询[第一行]表数�?.
	 * 	行数据以列名为key，以列值为val（列值保留其原本的数据类型）.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return Map<colName, colVal> (不会返回null)
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
	
	/**
	 * <PRE>
	 * 查询[第col列]表数据（数据值会被强制转换成String类型�?.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return List<colVal> (不会返回null)
	 */
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
	 * <PRE>
	 * 查询[第col列]表数据（数据值保留其原本的数据类型）.
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param sql 查询sql
	 * @return List<colVal> (不会返回null)
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
	
	
	
	/**
	 * 执行预编译sql
	 * @param conn 数据库连�?
	 * @param preSql 预编译sql
	 * @param params 参数�?
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
	 * @param conn 数据库连�?
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
	 * 执行存储过程，获得简单返回值（支持[无返回值]�? [单值]返回两种形式）�?
	 * 根据数据库连接自动识�? mysql、sybase、oracle�?
	 * 
	 * 注意�?
	 * 参数如果有null，则可能出错，特别是sybase数据�?
	 * 
	 * mysql存储过程要求�?
	 *  入参表：proSql的占位符?个数 必须�? 入参表params长度相同，否则抛出SQLException异常�?
	 * 	返回值：最后一个结果集（即SELECT语句）的�?1行、第1列的值�?
	 * 
	 * sybase存储过程要求�?
	 *  入参表：proSql的占位符?个数 必须�? 入参表params长度相同，否则抛出SQLException异常�?
	 * 	返回值：return所指定的值�?
	 * 
	 * oracle存储过程要求�?
	 *  入参表：当proSql的占位符?个数 �? 入参表params长度�?0，为无返回值形式；
	 *       �?1，为有返回值形式。其余情况抛出SQLException异常�?
	 * 	返回值：当proSql的占位符?个数比入参表params�?1，则认为最�?1个占位符是出参�?
	 * </pre>
	 * @param conn 数据库连�?
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参�?
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式�?
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可�?
	 */
	public static String execSP(Connection conn, String proSql, Object[] params) {
		if(conn == null) {
			log.error("DB connection is closed.");
		}
		
		String result = null;
		DBType dbType = judgeDBType(conn);
		switch (dbType) {
			case MYSQL: {
				result = _execSpByMysql(conn, proSql, params);
				break;
			}
			case SYBASE: {
				result = _execSpBySybase(conn, proSql, params);
				break;
			}
			case ORACLE: {
				result = _execSpByOracle(conn, proSql, params);
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
	 * <PRE>
	 * mysql存储过程调用，支持[无返回值]�? [单值]返回两种形式�?
	 * 
	 * 要求�?
	 *  入参表：proSql的占位符?个数 必须�? 入参表params长度相同，否则抛出SQLException异常�?
	 * 	返回值：最后一个结果集（即SELECT语句）的�?1行、第1列的值�?
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参�?
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式�?
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可�?
	 * 
	 * @throws SQLException 占位符与入参表个数不一致，或执行异常则抛出错误�?
	 */
	private static String _execSpByMysql(Connection conn, String proSql, Object[] params) {
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
					
					//取最后一个结果集的首行首列�?
					try {
						do {
							rs = cs.getResultSet();
							if(rs != null && rs.next()) {
								result = rs.getString(1);
							}
						} while(cs.getMoreResults() == true);
						
					} catch(NullPointerException e) {
						result = "";	// 存储过程无返回�?
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
	 * <PRE>
	 * sybase存储过程调用，支持[无返回值]�? [单值]返回两种形式�?
	 * 
	 * 要求�?
	 *  入参表：proSql的占位符?个数 必须�? 入参表params长度相同，否则抛出SQLException异常�?
	 * 	返回值：return所指定的值�?
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参�?
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式�?
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可�?
	 * 
	 * @throws SQLException 占位符与入参表个数不一致，或执行异常则抛出错误�?
	 */
	private static String _execSpBySybase(Connection conn, String proSql, Object[] params) {
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
	 * <PRE>
	 * oracle存储过程调用，支持[无返回值]�? [单值]返回两种形式�?
	 * 
	 * 要求�?
	 *  入参表：当proSql的占位符?个数 �? 入参表params长度�?0，为无返回值形式；
	 *       �?1，为有返回值形式。其余情况抛出SQLException异常�?
	 * 	返回值：当proSql的占位符?个数比入参表params�?1，则认为最�?1个占位符是出参�?
	 * </PRE>
	 * @param conn 数据库连�?
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参�?
	 * @return 
	 * 		对于返回单值的存储过程，返回String类型，即兼容数字和字符、但日期类型无法保证格式�?
	 * 		对于无返回值的存储过程，会返回任意值，不取返回值即可�?
	 * 
	 * @throws SQLException 占位符与入参表个数不一致，或执行异常则抛出错误�?
	 */
	private static String _execSpByOracle(Connection conn, String proSql, Object[] params) {
		String result = null;
		if(conn == null) {
			log.error("DB connection is closed.");
			
		} else if(proSql == null) {
			log.error("Procedure Sql is null.");
			
		} else {
			int paramNum = (params == null ? 0 : params.length);
			int placeNum = StrUtils.count(proSql, '?');
			int diff = placeNum - paramNum;	// 占位符数 �? 参数个数 的差异�?
			
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
					
					// 占位符数 �? 参数个数 �?1�? 说明最后一个参数是出参
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
	 * 调用存储过程，获取[结果集]返回�?
	 * 根据数据库连接自动识�? mysql、sybase、oracle�?
	 * 
	 * 注意�?
	 * 参数如果有null，则可能出错，特别是sybase数据�?
	 * 
	 * mysql存储过程要求�?
	 *  入参表：proSql的占位符?个数 必须�? 入参表params长度相同，否则抛出SQLException异常�?
	 * 	返回值：最后一个结果集（即SELECT语句）的�?1行、第1列的值�?
	 * 
	 * sybase存储过程要求�?
	 *  入参表：proSql的占位符?个数 必须�? 入参表params长度相同，否则抛出SQLException异常�?
	 * 	返回值：return所指定的值�?
	 * 
	 * oracle存储过程要求�?
	 *  入参表：当proSql的占位符?个数 �? 入参表params长度�?0，为无返回值形式；
	 *       �?1，为有返回值形式。其余情况抛出SQLException异常�?
	 * 	返回值：当proSql的占位符?个数比入参表params�?1，则认为最�?1个占位符是出参�?
	 * </pre>
	 * 
	 * @param conn 数据库连�?
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参�?
	 * @return List<Map<String, Object>>结果集（不会返回null�?
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
				result = _callSpByMysqlOrSybase(conn, proSql, params);
				break;
			}
			case SYBASE: {
				result = _callSpByMysqlOrSybase(conn, proSql, params);
				break;
			}
			case ORACLE: {
				result = _callSpByOracle(conn, proSql, params);
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
	 * <PRE>
	 * 存储过程调用，支持[结果集]返回形式�?
	 * 兼容mysql和sybase，不支持oralce�?
	 * 
	 * 要求�?
	 * 	入参表：proSql的占位符?个数 必须�? 入参表params长度相同，否则抛出SQLException异常�?
	 * 	返回值：最后一个结果集（即SELECT语句）�?
	 * <PRE>
	 * @param conn 数据库连�?
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参�?
	 * @return 返回结果集的多行记录，每行为 列名-列�? 的键值对�?
	 */
	private static List<Map<String, Object>> _callSpByMysqlOrSybase(
			Connection conn, String proSql, Object[] params) {
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
					
					//取最后一个结果集，拼装返回�?
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
	 * <PRE>
	 * oracle存储过程调用，仅支持[结果集]返回�?
	 * 
	 * 要求�?
	 *  入参表：proSql的占位符?个数 �? 入参表params长度�?1，且最�?1个占位符为返回结果集�?
	 *  	其余情况抛出SQLException异常�?
	 * 	返回值：结果集�?
	 * </PRE>
	 * @param conn Oracle数据库连�?
	 * @param proSql 存储过程SQL，占位符格式，如 SP_TEST(?,?,?)
	 * @param params 入参表，长度必须必占位符�?1
	 * @return 返回结果集的多行记录，每行为 列名-列�? 的键值对�?
	 */
	private static List<Map<String, Object>> _callSpByOracle(
			Connection conn, String proSql, Object[] params) {
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		if(conn == null) {
			log.error("DB connection is closed.");
			
		} else if(proSql == null) {
			log.error("Procedure Sql is null.");
			
		} else {
			int paramNum = (params == null ? 0 : params.length);
			int placeNum = StrUtils.count(proSql, '?');
			int diff = placeNum - paramNum;	// 占位符数 �? 参数个数 的差异�?
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
					
					//注册最后一个出参（游标类型�?
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
