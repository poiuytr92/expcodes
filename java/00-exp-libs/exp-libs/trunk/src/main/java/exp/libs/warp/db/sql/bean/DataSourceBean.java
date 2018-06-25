package exp.libs.warp.db.sql.bean;

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.VerifyUtils;

/**
 * <PRE>
 * 数据源配置对象.
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DataSourceBean {

	private String id;
	
	private final static String DEFAULT_ID = "DEFAULT_DATASOURCE";
	
	private String driver;
	
	private final static String DEFAULT_DRIVER = DBType.MYSQL.DRIVER;
	
	private String ip;
	
	private final static String DEFAULT_IP = "127.0.0.1";
	
	private int port;
	
	private final static int DEFAULT_PORT = 3306;
	
	private String username;
	
	private final static String DEFAULT_USERNAME = "root";
	
	private String password;
	
	private final static String DEFAULT_PASSWORD = "root";
	
	private String name;
	
	private final static String DEFAULT_DBNAME = "test-db";
	
	private String charset;
	
	private final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/** 单个连接的最大激活时间（即超时时间） */
	private long maximumActiveTime;
	
	private final static long DEFAULT_MAX_ACTIVE_TIME = 60000L;
	
	/**
	 * 空闲连接保活的测试sql.
	 * 如果发现了空闲的数据库连�?.house keeper 将会用这个语句来测试.
	 * 这个语句最好非常快的被执行.如果没有定义,测试过程将会被忽�?
	 */
	private String houseKeepingTestSql;
	
	// FIXME: 此默认测试语句仅mysql可用, 其他数据库需自行设置(或关闭测�?), 
	// 否则连接池会报错 Created a new connection but it failed its test
	private final static String DEFAULT_KEEP_TEST_SQL = "select 1";
	
	/** 空闲连接保活的测试间�?(ms) */
	private long houseKeepingSleepTime;
	
	private final static long DEFAULT_KEEP_SLEEP_TIME = 300000L;
	
	/**
	 * 可同时建立的最大连接数.
	 *  即可同时新增的连接请�?, 但还没有可供使用的连�?.
	 */
	private int simultaneousBuildThrottle;
	
	private final static int DEFAULT_SIMULTANEOUS_BUILD_THROTTLE = 10;
	
	/** 可同时存在的最大连接数 */
	private int maximumConnectionCount;
	
	private final static int DEFAULT_MAX_CONN_COUNT = 20;
	
	/** (连接�?)保有的最少连接数 */
	private int minimumConnectionCount;
	
	private final static int DEFAULT_MIN_CONN_COUNT = 5;
	
	/**
	 * 当没有空闲连接可以分配时, 在队列中等候的最大请求数.
	 * 超过这个请求数的用户连接就不会被接受
	 */
	private int maximumNewConnections;
	
	private final static int DEFAULT_MAX_NEW_CONN = 10;
	
	/**
	 * (连接�?)保有的最少空闲连接数
	 */
	private int prototypeCount;
	
	private final static int DEFAULT_PROTOTYPE_COUNT = 2;
	
	/** 单个连接的最大使用寿�? */
	private long maximumConnectionLifetime;
	
	private final static long DEFAULT_MAX_CONN_LIFETIME = 3600000L;
	
	/**
	 * 在连接使用前测试是否可用.
	 * 
	 * 如果为true，在每个连接被测试前都会服务这个连接�?
	 * 如果一个连接失败，那么将被丢弃，另一个连接将会被处理�?
	 * 如果所有连接都失败，一个新的连接将会被建立�?
	 * 否则将会抛出一个SQLException异常�?
	 */
	private boolean testBeforeUse;
	
	private final static boolean DEFAULT_TEST_BEFORE_USE = false;
	
	/**
	 * 在连接使用后测试是否可用(回收到连接池复用).
	 * 
	 * 如果为true，在每个连接被测试后都会服务这个连接，使其回到连接池中，
	 * 如果连接失败，那么将被废弃�?
	 */
	private boolean testAfterUse;
	
	private final static boolean DEFAULT_TEST_AFTER_USE = false;
	
	/**
	 * 如果为true,那么每个被执行的SQL语句将会在执行期被log记录(DEBUG LEVEL).
	 * 你也可以注册一个ConnectionListener (参看ProxoolFacade)得到这些信息.
	 */
	private boolean trace;
	
	private final static boolean DEFAULT_TRACE = false;
	
	public DataSourceBean() {
		setId(DEFAULT_ID);
		setDriver(DEFAULT_DRIVER);
		setIp(DEFAULT_IP);
		setPort(DEFAULT_PORT);
		setUsername(DEFAULT_USERNAME);
		setPassword(DEFAULT_PASSWORD);
		setName(DEFAULT_DBNAME);
		setCharset(DEFAULT_CHARSET);
		setMaximumActiveTime(DEFAULT_MAX_ACTIVE_TIME);
		setHouseKeepingTestSql(DEFAULT_KEEP_TEST_SQL);
		setHouseKeepingSleepTime(DEFAULT_KEEP_SLEEP_TIME);
		setSimultaneousBuildThrottle(DEFAULT_SIMULTANEOUS_BUILD_THROTTLE);
		setMaximumConnectionCount(DEFAULT_MAX_CONN_COUNT);
		setMinimumConnectionCount(DEFAULT_MIN_CONN_COUNT);
		setMaximumNewConnections(DEFAULT_MAX_NEW_CONN);
		setPrototypeCount(DEFAULT_PROTOTYPE_COUNT);
		setMaximumConnectionLifetime(DEFAULT_MAX_CONN_LIFETIME);
		setTestBeforeUse(DEFAULT_TEST_BEFORE_USE);
		setTestAfterUse(DEFAULT_TEST_AFTER_USE);
		setTrace(DEFAULT_TRACE);
	}

	public String getUrl() {
		String url = getUrlByDriver();
		if(url != null) {
			url = url.replace(DBType.PH_ALIAS, getId());
			url = url.replace(DBType.PH_HOST, getIp());
			url = url.replace(DBType.PH_PORT, String.valueOf(getPort()));
			url = url.replace(DBType.PH_DBNAME, getName());
			url = url.replace(DBType.PH_TIMEOUT, String.valueOf(getMaximumActiveTime()));
			url = url.replace(DBType.PH_CHARSET, getCharset());
		}
		return (url == null ? "" : url);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = (StrUtils.isNotEmpty(id) ? id :
				(StrUtils.isNotEmpty(this.id) ? this.id : DEFAULT_ID));
	}
	
	public void setAlias(String alias) {
		setId(alias);
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = (StrUtils.isNotEmpty(driver) ? getDriverbyName(driver) :
			(StrUtils.isNotEmpty(this.driver) ? this.driver : DEFAULT_DRIVER));
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = (StrUtils.isNotEmpty(ip) ? ip :
			(StrUtils.isNotEmpty(this.ip) ? this.ip : DEFAULT_IP));
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = (VerifyUtils.isPort(port) ? port :
			(VerifyUtils.isPort(this.port) ? this.port : DEFAULT_PORT));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = (username != null ? username :
			(this.username != null ? this.username : DEFAULT_USERNAME));
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = (password != null ? password :
			(this.password != null ? this.password : DEFAULT_PASSWORD));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = (StrUtils.isNotEmpty(name) ? name :
			(StrUtils.isNotEmpty(this.name) ? this.name : DEFAULT_DBNAME));
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = (CharsetUtils.isVaild(charset) ? charset :
			(CharsetUtils.isVaild(this.charset) ? this.charset : DEFAULT_CHARSET));
	}

	public long getMaximumActiveTime() {
		return maximumActiveTime;
	}

	public void setMaximumActiveTime(long maximumActiveTime) {
		this.maximumActiveTime = (maximumActiveTime >= 0 ? maximumActiveTime :
			(this.maximumActiveTime >= 0 ? this.maximumActiveTime : DEFAULT_MAX_ACTIVE_TIME));
	}

	public String getHouseKeepingTestSql() {
		return houseKeepingTestSql;
	}

	public void setHouseKeepingTestSql(String houseKeepingTestSql) {
		this.houseKeepingTestSql = (StrUtils.isNotEmpty(houseKeepingTestSql) ? houseKeepingTestSql :
			(StrUtils.isNotEmpty(this.houseKeepingTestSql) ? this.houseKeepingTestSql : DEFAULT_KEEP_TEST_SQL));
	}

	public long getHouseKeepingSleepTime() {
		return houseKeepingSleepTime;
	}

	public void setHouseKeepingSleepTime(long houseKeepingSleepTime) {
		this.houseKeepingSleepTime = (houseKeepingSleepTime >= 0 ? houseKeepingSleepTime :
			(this.houseKeepingSleepTime >= 0 ? this.houseKeepingSleepTime : DEFAULT_KEEP_SLEEP_TIME));
	}

	public int getSimultaneousBuildThrottle() {
		return simultaneousBuildThrottle;
	}

	public void setSimultaneousBuildThrottle(int simultaneousBuildThrottle) {
		this.simultaneousBuildThrottle = (simultaneousBuildThrottle >= 0 ? simultaneousBuildThrottle :
				(this.simultaneousBuildThrottle >= 0 ? this.simultaneousBuildThrottle : DEFAULT_SIMULTANEOUS_BUILD_THROTTLE));
	}

	public int getMaximumConnectionCount() {
		return maximumConnectionCount;
	}

	public void setMaximumConnectionCount(int maximumConnectionCount) {
		this.maximumConnectionCount = (maximumConnectionCount >= 0 ? maximumConnectionCount :
			(this.maximumConnectionCount >= 0 ? this.maximumConnectionCount : DEFAULT_MAX_CONN_COUNT));
	}

	public int getMinimumConnectionCount() {
		return minimumConnectionCount;
	}

	public void setMinimumConnectionCount(int minimumConnectionCount) {
		this.minimumConnectionCount = (minimumConnectionCount >= 0 ? minimumConnectionCount :
			(this.minimumConnectionCount >= 0 ? this.minimumConnectionCount : DEFAULT_MIN_CONN_COUNT));
	}

	public int getMaximumNewConnections() {
		return maximumNewConnections;
	}

	public void setMaximumNewConnections(int maximumNewConnections) {
		this.maximumNewConnections = (maximumNewConnections >= 0 ? maximumNewConnections :
			(this.maximumNewConnections >= 0 ? this.maximumNewConnections : DEFAULT_MAX_NEW_CONN));
	}

	public int getPrototypeCount() {
		return prototypeCount;
	}

	public void setPrototypeCount(int prototypeCount) {
		this.prototypeCount = (prototypeCount >= 0 ? prototypeCount :
			(this.prototypeCount >= 0 ? this.prototypeCount : DEFAULT_PROTOTYPE_COUNT));
	}

	public long getMaximumConnectionLifetime() {
		return maximumConnectionLifetime;
	}

	public void setMaximumConnectionLifetime(long maximumConnectionLifetime) {
		this.maximumConnectionLifetime = (maximumConnectionLifetime >= 0 ? maximumConnectionLifetime :
			(this.maximumConnectionLifetime >= 0 ? this.maximumConnectionLifetime : DEFAULT_MAX_CONN_LIFETIME));
	}

	public boolean isTestBeforeUse() {
		return testBeforeUse;
	}

	public void setTestBeforeUse(boolean testBeforeUse) {
		this.testBeforeUse = testBeforeUse;
	}

	public boolean isTestAfterUse() {
		return testAfterUse;
	}

	public void setTestAfterUse(boolean testAfterUse) {
		this.testAfterUse = testAfterUse;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}
	
	private String getUrlByDriver() {
		String url = null;
		if(getDriver() != null) {
			if(DBType.MYSQL.DRIVER.equals(getDriver())) {
				url = DBType.MYSQL.JDBCURL;
				
			} else if(DBType.SQLITE.DRIVER.equals(getDriver())) {
				url = DBType.SQLITE.JDBCURL;
				
			} else if(DBType.ORACLE.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE.JDBCURL;
				
			} else if(DBType.ORACLE_8I.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE_8I.JDBCURL;
				
			} else if(DBType.ORACLE_8I_OCI.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE_8I_OCI.JDBCURL;
				
			} else if(DBType.ORACLE_9I.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE_9I.JDBCURL;
				
			} else if(DBType.ORACLE_10G.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE_10G.JDBCURL;
				
			} else if(DBType.ORACLE_10G_OCI.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE_10G_OCI.JDBCURL;
				
			} else if(DBType.ORACLE_11G.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE_11G.JDBCURL;
				
			} else if(DBType.ORACLE_11G_OCI.DRIVER.equals(getDriver())) {
				url = DBType.ORACLE_11G_OCI.JDBCURL;
				
			} else if(DBType.SYBASE.DRIVER.equals(getDriver())) {
				url = DBType.SYBASE.JDBCURL;
				
			} else if(DBType.SYBASE_IQ.DRIVER.equals(getDriver())) {
				url = DBType.SYBASE_IQ.JDBCURL;
				
			} else if(DBType.SYBASE_ASE.DRIVER.equals(getDriver())) {
				url = DBType.SYBASE_ASE.JDBCURL;
				
			} else if(DBType.SYBASE_125ASE.DRIVER.equals(getDriver())) {
				url = DBType.SYBASE_125ASE.JDBCURL;
				
			} else if(DBType.SYBASE_15ASE.DRIVER.equals(getDriver())) {
				url = DBType.SYBASE_15ASE.JDBCURL;
				
			} else if(DBType.SYBASE_155ASE.DRIVER.equals(getDriver())) {
				url = DBType.SYBASE_155ASE.JDBCURL;
				
			} else if(DBType.MSSQL.DRIVER.equals(getDriver())) {
				url = DBType.MSSQL.JDBCURL;
				
			} else if(DBType.MSSQL2000.DRIVER.equals(getDriver())) {
				url = DBType.MSSQL2000.JDBCURL;
				
			} else if(DBType.MSSQL2005.DRIVER.equals(getDriver())) {
				url = DBType.MSSQL2005.JDBCURL;
				
			} else if(DBType.MSSQL2008.DRIVER.equals(getDriver())) {
				url = DBType.MSSQL2008.JDBCURL;
				
			} else if(DBType.POSTGRESQL.DRIVER.equals(getDriver())) {
				url = DBType.POSTGRESQL.JDBCURL;
				
			} else if(DBType.ACCESS.DRIVER.equals(getDriver())) {
				url = DBType.ACCESS.JDBCURL;
				
			} else if(DBType.INFORMIX.DRIVER.equals(getDriver())) {
				url = DBType.INFORMIX.JDBCURL;
				
			} else if(DBType.IBM.DRIVER.equals(getDriver())) {
				url = DBType.IBM.JDBCURL;
			}
		}
		return url;
	}
	
	private String getDriverbyName(String dirverName) {
		String driver = dirverName;
		if(dirverName != null) {
			if(dirverName.equalsIgnoreCase(DBType.MYSQL.NAME) || 
					dirverName.equalsIgnoreCase(DBType.MYSQL.DRIVER)) {
				driver = DBType.MYSQL.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.SQLITE.NAME) || 
					dirverName.equalsIgnoreCase(DBType.SQLITE.DRIVER)) {
				driver = DBType.SQLITE.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE.DRIVER)) {
				driver = DBType.ORACLE.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE_8I.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE_8I.DRIVER)) {
				driver = DBType.ORACLE_8I.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE_8I_OCI.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE_8I_OCI.DRIVER)) {
				driver = DBType.ORACLE_8I_OCI.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE_9I.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE_9I.DRIVER)) {
				driver = DBType.ORACLE_9I.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE_10G.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE_10G.DRIVER)) {
				driver = DBType.ORACLE_10G.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE_10G_OCI.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE_10G_OCI.DRIVER)) {
				driver = DBType.ORACLE_10G_OCI.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE_11G.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE_11G.DRIVER)) {
				driver = DBType.ORACLE_11G.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ORACLE_11G_OCI.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ORACLE_11G_OCI.DRIVER)) {
				driver = DBType.ORACLE_11G_OCI.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.SYBASE.NAME) || 
					dirverName.equalsIgnoreCase(DBType.SYBASE.DRIVER)) {
				driver = DBType.SYBASE.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.SYBASE_IQ.NAME) || 
					dirverName.equalsIgnoreCase(DBType.SYBASE_IQ.DRIVER)) {
				driver = DBType.SYBASE_IQ.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.SYBASE_ASE.NAME) || 
					dirverName.equalsIgnoreCase(DBType.SYBASE_ASE.DRIVER)) {
				driver = DBType.SYBASE_ASE.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.SYBASE_125ASE.NAME) || 
					dirverName.equalsIgnoreCase(DBType.SYBASE_125ASE.DRIVER)) {
				driver = DBType.SYBASE_125ASE.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.SYBASE_15ASE.NAME) || 
					dirverName.equalsIgnoreCase(DBType.SYBASE_15ASE.DRIVER)) {
				driver = DBType.SYBASE_15ASE.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.SYBASE_155ASE.NAME) || 
					dirverName.equalsIgnoreCase(DBType.SYBASE_155ASE.DRIVER)) {
				driver = DBType.SYBASE_155ASE.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.MSSQL.NAME) || 
					dirverName.equalsIgnoreCase(DBType.MSSQL.DRIVER)) {
				driver = DBType.MSSQL.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.MSSQL2000.NAME) || 
					dirverName.equalsIgnoreCase(DBType.MSSQL2000.DRIVER)) {
				driver = DBType.MSSQL2000.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.MSSQL2005.NAME) || 
					dirverName.equalsIgnoreCase(DBType.MSSQL2005.DRIVER)) {
				driver = DBType.MSSQL2005.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.MSSQL2008.NAME) || 
					dirverName.equalsIgnoreCase(DBType.MSSQL2008.DRIVER)) {
				driver = DBType.MSSQL2008.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.POSTGRESQL.NAME) || 
					dirverName.equalsIgnoreCase(DBType.POSTGRESQL.DRIVER)) {
				driver = DBType.POSTGRESQL.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.ACCESS.NAME) || 
					dirverName.equalsIgnoreCase(DBType.ACCESS.DRIVER)) {
				driver = DBType.ACCESS.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.INFORMIX.NAME) || 
					dirverName.equalsIgnoreCase(DBType.INFORMIX.DRIVER)) {
				driver = DBType.INFORMIX.DRIVER;
				
			} else if(dirverName.equalsIgnoreCase(DBType.IBM.NAME) || 
					dirverName.equalsIgnoreCase(DBType.IBM.DRIVER)) {
				driver = DBType.IBM.DRIVER;
			}
		}
		return driver;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n++++++++++++++++ DataSource ++++++++++++++++\r\n");
		sb.append("id : ").append(getId()).append("\r\n");
		sb.append("driver : ").append(getDriver()).append("\r\n");
		sb.append("ip : ").append(getIp()).append("\r\n");
		sb.append("port : ").append(getPort()).append("\r\n");
		sb.append("username : ").append(getUsername()).append("\r\n");
		sb.append("password : ").append(getPassword()).append("\r\n");
		sb.append("name : ").append(getName()).append("\r\n");
		sb.append("charset : ").append(getCharset()).append("\r\n");
		sb.append("maximum-active-time : ").append(getMaximumActiveTime()).append("\r\n");
		sb.append("house-keeping-test-sql : ").append(getHouseKeepingTestSql()).append("\r\n");
		sb.append("house-keeping-sleep-time : ").append(getHouseKeepingSleepTime()).append("\r\n");
		sb.append("simultaneous-build-throttle : ").append(getSimultaneousBuildThrottle()).append("\r\n");
		sb.append("maximum-connection-count : ").append(getMaximumConnectionCount()).append("\r\n");
		sb.append("minimum-connection-count : ").append(getMinimumConnectionCount()).append("\r\n");
		sb.append("maximum-new-connections : ").append(getMaximumNewConnections()).append("\r\n");
		sb.append("prototype-count : ").append(getPrototypeCount()).append("\r\n");
		sb.append("maximum-connection-lifetime : ").append(getMaximumConnectionLifetime()).append("\r\n");
		sb.append("test-before-use : ").append(isTestBeforeUse()).append("\r\n");
		sb.append("test-after-use : ").append(isTestAfterUse()).append("\r\n");
		sb.append("trace : ").append(isTrace()).append("\r\n");
		sb.append("--------------------------------------------\r\n");
		return sb.toString();
	}
	
	public DataSourceBean clone() {
		
		// 部分get/set方法含有校验逻辑，若通过setXX(getXX)可能会因为重复校验导致数据异�?
		// 因此此处采用直接赋值方式进行克�?
		
		DataSourceBean _clone = new DataSourceBean();
		_clone.id = this.id;
		_clone.driver = this.driver;
		_clone.ip = this.ip;
		_clone.port = this.port;
		_clone.username = this.username;
		_clone.password = this.password;
		_clone.name = this.name;
		_clone.charset = this.charset;
		_clone.maximumActiveTime = this.maximumActiveTime;
		_clone.houseKeepingTestSql = this.houseKeepingTestSql;
		_clone.houseKeepingSleepTime = this.houseKeepingSleepTime;
		_clone.simultaneousBuildThrottle = this.simultaneousBuildThrottle;
		_clone.maximumConnectionCount = this.maximumConnectionCount;
		_clone.minimumConnectionCount = this.minimumConnectionCount;
		_clone.maximumNewConnections = this.maximumNewConnections;
		_clone.prototypeCount = this.prototypeCount;
		_clone.maximumConnectionLifetime = this.maximumConnectionLifetime;
		_clone.testBeforeUse = this.testBeforeUse;
		_clone.testAfterUse = this.testAfterUse;
		_clone.trace = this.trace;
		return _clone;
	}

}
