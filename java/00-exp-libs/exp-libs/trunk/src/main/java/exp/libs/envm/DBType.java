package exp.libs.envm;

/**
 * <PRE>
 * 枚举类：数据库类型
 * 	(提供数据库驱动名、以及JDBC-URL模板)
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public enum DBType {

	UNKNOW("unknow", "null", "null"), 
	
	PROXOOL("proxool", "org.logicalcobwebs.proxool.ProxoolDriver", "proxool.<alias>"), 
	
	MYSQL("mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://<host>:<port>/<dbname>?autoReconnect=true&amp;useUnicode=true&amp;zeroDateTimeBehavior=convertToNull&amp;socketTimeout=<timeout>&amp;characterEncoding=<charset>"),
	
	SQLITE("sqlite", "org.sqlite.JDBC", "jdbc:sqlite:<dbname>"),
	
	/**
	 * Java JDBC 连接 Oracle有三种方法:
	 * -----------------------------------------
	 * 格式一: 使用ServiceName方式:
	 * 		jdbc:oracle:thin:@//<host>:<port>/<service_name>
	 * 	   如： jdbc:oracle:thin:@//xxx.xxx.xxx.xxx:1526/CDEV
	 * 	   注： @后面有//, 这是与使用SID的主要区别。（11g在@后可不加//）
	 *  	这是Oracle推荐的格式, 因为对于集群而言, 每个节点的SID是不一样的, 而SERVICE NAME可以包含所有节点。
	 *  
	 * 格式二: 使用SID方式: 
	 * 		jdbc:oracle:thin:@<host>:<port>:<SID> 
	 *   如： jdbc:oracle:thin:@xxx.xxx.xxx.xxx:1526:CDEV2
	 *   
	 * 格式三：使用TNSName方式: 
	 * 		jdbc:oracle:thin:@<TNSName>
	 * 	   如： jdbc:oracle:thin:@CDEV
	 * 	   注： ORACLE从10.2.0.1后支持TNSNames
	 */
	ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	ORACLE_8I("oracle-8i", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	ORACLE_8I_OCI("oracle-8i-oci", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	ORACLE_9I("oracle-9i", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	ORACLE_10G("oracle-11g", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	ORACLE_10G_OCI("oracle-10g-oci", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	ORACLE_11G("oracle-11g", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	ORACLE_11G_OCI("oracle-11g-oci", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<dbname>"),
	
	SYBASE("sybase", "com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>?ServiceName=<dbname>&amp;charset=<charset>&amp;jconnect_version=6"),
	
	SYBASE_IQ("sybase-iq", "com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>?ServiceName=<dbname>&amp;charset=<charset>&amp;jconnect_version=6"),
	
	SYBASE_ASE("sybase-ase", "com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>?ServiceName=<dbname>&amp;charset=<charset>&amp;jconnect_version=6"),
	
	SYBASE_125ASE("sybase-125ase", "com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>?ServiceName=<dbname>&amp;charset=<charset>&amp;jconnect_version=6"),
	
	SYBASE_15ASE("sybase-15ase", "com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>?ServiceName=<dbname>&amp;charset=<charset>&amp;jconnect_version=6"),
	
	SYBASE_155ASE("sybase-155ase", "com.sybase.jdbc4.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>?ServiceName=<dbname>&amp;charset=<charset>&amp;jconnect_version=7"),
	
	MSSQL("mssql", "com.microsoft.jdbc.sqlserver.SQLServerDriver", "jdbc:microsoft:sqlserver://<host>:<port>;DatabaseName=<dbname>"),
	
	MSSQL2000("mssql2000", "com.microsoft.jdbc.sqlserver.SQLServerDriver", "jdbc:microsoft:sqlserver://<host>:<port>;DatabaseName=<dbname>"),
	
	MSSQL2005("mssql2005", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:microsoft:sqlserver://<host>:<port>;DatabaseName=<dbname>"),
	
	MSSQL2008("mssql2008", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:microsoft:sqlserver://<host>:<port>;DatabaseName=<dbname>"),
	
	POSTGRESQL("postgresql", "org.postgresql.Driver", "jdbc:postgresql://<host>:<port>/<dbname>"),
	
	ACCESS("access", "org.objectweb.rmijdbc.Driver", "jdbc:rmi://<host>:<port>/jdbc:odbc:<dbname>"),
	
	INFORMIX("informix", "com.informix.jdbc.IfxDriver", "jdbc:informix-sqli://<host>:<port>/<dbname>:INFORMIXSERVER=dbserver;newlocale=en_us,zh_cn;newcodeset=<charset>"),
	
	IBM("ibm", "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://<host>:<port>/<dbname>"),
	
	;
	
	public final static String PH_ALIAS = "<alias>";
	
	public final static String PH_HOST = "<host>";
	
	public final static String PH_PORT = "<port>";
	
	public final static String PH_DBNAME = "<dbname>";
	
	public final static String PH_TIMEOUT = "<timeout>";
	
	public final static String PH_CHARSET = "<charset>";
	
	public String NAME;
	
	public String DRIVER;
	
	public String JDBCURL;
	
	private DBType(String name, String driver, String jdbcUrl) {
		this.NAME = (name != null ? name.trim().toLowerCase() : "");
		this.DRIVER = driver;
		this.JDBCURL = jdbcUrl;
	}
	
}
