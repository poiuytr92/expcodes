package exp.libs.warp.db.sql.bean;

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.pub.StrUtils;

public class DataSourceBean {

	private String url;
	
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
	
	private final static String DEFAULT_DBNAME = "test";
	
	private String charset;
	
	private final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/**
	 * 如果发现了空闲的数据库连接.house keeper 将会用这个语句来测试.
	 * 这个语句最好非常快的被执行.如果没有定义,测试过程将会被忽略
	 */
	private String houseKeepingTestSql;
	
	private final static String DEFAULT_KEEP_TEST_SQL = "select 1";
	
	/**
	 * house keeper 保留线程处于睡眠状态的最长时间,
	 * house keeper 的职责就是检查各个连接的状态,并判断是否需要销毁或者创建.
	 */
	private long houseKeepingSleepTime;
	
	private final static long DEFAULT_KEEP_SLEEP_TIME = 300000;
	
	/**
	 * 一次可建立的最大连接数。
	 * 就是新增的连接请求,但还没有可供使用的连接。
	 * 默认是10
	 */
	private int simultaneousBuildThrottle;
	
	private final static int DEFAULT_SIMULTANEOUS_BUILD_THROTTLE = 10;
	
	/** 最大的数据库连接数 */
	private int maximumConnectionCount;
	
	private final static int DEFAULT_MAX_CONN_COUNT = 20;
	
	/** 最小的数据库连接数 */
	private int minimumConnectionCount;
	
	private final static int DEFAULT_MIN_CONN_COUNT = 5;
	
	/**
	 * 没有空闲连接可以分配而在队列中等候的最大请求数,
	 * 超过这个请求数的用户连接就不会被接受
	 */
	private int maximumNewConnections;
	
	private final static int DEFAULT_MAX_NEW_CONN = 10;
	
	/**
	 * 最少保持的空闲连接数(默认2个)
	 */
	private int prototypeCount;
	
	private final static int DEFAULT_PROTOTYPE_COUNT = 2;
	
	/** 一个线程的最大寿命 */
	private long maximumConnectionLifetime;
	
	private final static long DEFAULT_MAX_CONN_LIFETIME = 3600000;
	
	/**
	 * 如果为true，在每个连接被测试前都会服务这个连接，
	 * 如果一个连接失败，那么将被丢弃，另一个连接将会被处理，
	 * 如果所有连接都失败，一个新的连接将会被建立。
	 * 否则将会抛出一个SQLException异常。
	 */
	private boolean testBeforeUse;
	
	private final static boolean DEFAULT_TEST_BEFORE_USE = true;
	
	/**
	 * 如果为true，在每个连接被测试后都会服务这个连接，使其回到连接池中，
	 * 如果连接失败，那么将被废弃。
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
		if(StrUtils.isEmpty(url) && getDriver() != null) {
			String url = getUrlByDriver();
			
			if(url != null) {
				url = url.replace(DBType.PH_ALIAS, getId());
				url = url.replace(DBType.PH_HOST, getIp());
				url = url.replace(DBType.PH_PORT, String.valueOf(getPort()));
				url = url.replace(DBType.PH_DBNAME, getName());
				url = url.replace(DBType.PH_CHARSET, getCharset());
				
				this.url = url;
			}
		}
		return this.url;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = (id == null ? DEFAULT_ID : id);
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = (driver == null ? 
				DEFAULT_DRIVER : getDriverbyName(driver));
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = (ip == null ? DEFAULT_IP : ip);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = (port <= 0 || port > 65535 ? DEFAULT_PORT : port);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = (username == null ? DEFAULT_USERNAME : username);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = (password == null ? DEFAULT_PASSWORD : password);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = (name == null ? DEFAULT_DBNAME : name);
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = (charset == null ? DEFAULT_CHARSET : charset);
	}

	public String getHouseKeepingTestSql() {
		return houseKeepingTestSql;
	}

	public void setHouseKeepingTestSql(String houseKeepingTestSql) {
		this.houseKeepingTestSql = (houseKeepingTestSql == null ? 
				DEFAULT_KEEP_TEST_SQL : houseKeepingTestSql);
	}

	public long getHouseKeepingSleepTime() {
		return houseKeepingSleepTime;
	}

	public void setHouseKeepingSleepTime(long houseKeepingSleepTime) {
		this.houseKeepingSleepTime = (houseKeepingSleepTime < 0 ? 
				DEFAULT_KEEP_SLEEP_TIME : houseKeepingSleepTime);
	}

	public int getSimultaneousBuildThrottle() {
		return simultaneousBuildThrottle;
	}

	public void setSimultaneousBuildThrottle(int simultaneousBuildThrottle) {
		this.simultaneousBuildThrottle = (simultaneousBuildThrottle < 0 ? 
				DEFAULT_SIMULTANEOUS_BUILD_THROTTLE : simultaneousBuildThrottle);
	}

	public int getMaximumConnectionCount() {
		return maximumConnectionCount;
	}

	public void setMaximumConnectionCount(int maximumConnectionCount) {
		this.maximumConnectionCount = (maximumConnectionCount < 0 ? 
				DEFAULT_MAX_CONN_COUNT : maximumConnectionCount);
	}

	public int getMinimumConnectionCount() {
		return minimumConnectionCount;
	}

	public void setMinimumConnectionCount(int minimumConnectionCount) {
		this.minimumConnectionCount = (minimumConnectionCount < 0 ? 
				DEFAULT_MIN_CONN_COUNT : minimumConnectionCount);
	}

	public int getMaximumNewConnections() {
		return maximumNewConnections;
	}

	public void setMaximumNewConnections(int maximumNewConnections) {
		this.maximumNewConnections = (maximumNewConnections < 0 ? 
				DEFAULT_MAX_NEW_CONN : maximumNewConnections);
	}

	public int getPrototypeCount() {
		return prototypeCount;
	}

	public void setPrototypeCount(int prototypeCount) {
		this.prototypeCount = (prototypeCount < 0 ? 
				DEFAULT_PROTOTYPE_COUNT : prototypeCount);
	}

	public long getMaximumConnectionLifetime() {
		return maximumConnectionLifetime;
	}

	public void setMaximumConnectionLifetime(long maximumConnectionLifetime) {
		this.maximumConnectionLifetime = (maximumConnectionLifetime < 0 ? 
				DEFAULT_MAX_CONN_LIFETIME : maximumConnectionLifetime);
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
			if(dirverName.equals(DBType.MYSQL.NAME) || 
					dirverName.equals(DBType.MYSQL.DRIVER)) {
				driver = DBType.MYSQL.DRIVER;
				
			} else if(dirverName.equals(DBType.SQLITE.NAME) || 
					dirverName.equals(DBType.SQLITE.DRIVER)) {
				driver = DBType.SQLITE.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE.NAME) || 
					dirverName.equals(DBType.ORACLE.DRIVER)) {
				driver = DBType.ORACLE.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE_8I.NAME) || 
					dirverName.equals(DBType.ORACLE_8I.DRIVER)) {
				driver = DBType.ORACLE_8I.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE_8I_OCI.NAME) || 
					dirverName.equals(DBType.ORACLE_8I_OCI.DRIVER)) {
				driver = DBType.ORACLE_8I_OCI.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE_9I.NAME) || 
					dirverName.equals(DBType.ORACLE_9I.DRIVER)) {
				driver = DBType.ORACLE_9I.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE_10G.NAME) || 
					dirverName.equals(DBType.ORACLE_10G.DRIVER)) {
				driver = DBType.ORACLE_10G.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE_10G_OCI.NAME) || 
					dirverName.equals(DBType.ORACLE_10G_OCI.DRIVER)) {
				driver = DBType.ORACLE_10G_OCI.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE_11G.NAME) || 
					dirverName.equals(DBType.ORACLE_11G.DRIVER)) {
				driver = DBType.ORACLE_11G.DRIVER;
				
			} else if(dirverName.equals(DBType.ORACLE_11G_OCI.NAME) || 
					dirverName.equals(DBType.ORACLE_11G_OCI.DRIVER)) {
				driver = DBType.ORACLE_11G_OCI.DRIVER;
				
			} else if(dirverName.equals(DBType.SYBASE.NAME) || 
					dirverName.equals(DBType.SYBASE.DRIVER)) {
				driver = DBType.SYBASE.DRIVER;
				
			} else if(dirverName.equals(DBType.SYBASE_IQ.NAME) || 
					dirverName.equals(DBType.SYBASE_IQ.DRIVER)) {
				driver = DBType.SYBASE_IQ.DRIVER;
				
			} else if(dirverName.equals(DBType.SYBASE_ASE.NAME) || 
					dirverName.equals(DBType.SYBASE_ASE.DRIVER)) {
				driver = DBType.SYBASE_ASE.DRIVER;
				
			} else if(dirverName.equals(DBType.SYBASE_125ASE.NAME) || 
					dirverName.equals(DBType.SYBASE_125ASE.DRIVER)) {
				driver = DBType.SYBASE_125ASE.DRIVER;
				
			} else if(dirverName.equals(DBType.SYBASE_15ASE.NAME) || 
					dirverName.equals(DBType.SYBASE_15ASE.DRIVER)) {
				driver = DBType.SYBASE_15ASE.DRIVER;
				
			} else if(dirverName.equals(DBType.SYBASE_155ASE.NAME) || 
					dirverName.equals(DBType.SYBASE_155ASE.DRIVER)) {
				driver = DBType.SYBASE_155ASE.DRIVER;
				
			} else if(dirverName.equals(DBType.MSSQL.NAME) || 
					dirverName.equals(DBType.MSSQL.DRIVER)) {
				driver = DBType.MSSQL.DRIVER;
				
			} else if(dirverName.equals(DBType.MSSQL2000.NAME) || 
					dirverName.equals(DBType.MSSQL2000.DRIVER)) {
				driver = DBType.MSSQL2000.DRIVER;
				
			} else if(dirverName.equals(DBType.MSSQL2005.NAME) || 
					dirverName.equals(DBType.MSSQL2005.DRIVER)) {
				driver = DBType.MSSQL2005.DRIVER;
				
			} else if(dirverName.equals(DBType.MSSQL2008.NAME) || 
					dirverName.equals(DBType.MSSQL2008.DRIVER)) {
				driver = DBType.MSSQL2008.DRIVER;
				
			} else if(dirverName.equals(DBType.POSTGRESQL.NAME) || 
					dirverName.equals(DBType.POSTGRESQL.DRIVER)) {
				driver = DBType.POSTGRESQL.DRIVER;
				
			} else if(dirverName.equals(DBType.ACCESS.NAME) || 
					dirverName.equals(DBType.ACCESS.DRIVER)) {
				driver = DBType.ACCESS.DRIVER;
				
			} else if(dirverName.equals(DBType.INFORMIX.NAME) || 
					dirverName.equals(DBType.INFORMIX.DRIVER)) {
				driver = DBType.INFORMIX.DRIVER;
				
			} else if(dirverName.equals(DBType.IBM.NAME) || 
					dirverName.equals(DBType.IBM.DRIVER)) {
				driver = DBType.IBM.DRIVER;
			}
		}
		return driver;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("+++++++++++++++++++++++++++++++++++");
		sb.append("id : ").append(getId());
		sb.append("driver : ").append(getDriver());
		sb.append("ip : ").append(getIp());
		sb.append("port : ").append(getPort());
		sb.append("username : ").append(getUsername());
		sb.append("password : ").append(getPassword());
		sb.append("name : ").append(getName());
		sb.append("charset : ").append(getCharset());
		sb.append("house-keeping-test-sql : ").append(getHouseKeepingTestSql());
		sb.append("house-keeping-sleep-time : ").append(getHouseKeepingSleepTime());
		sb.append("simultaneous-build-throttle : ").append(getSimultaneousBuildThrottle());
		sb.append("maximum-connection-count : ").append(getMaximumConnectionCount());
		sb.append("minimum-connection-count : ").append(getMinimumConnectionCount());
		sb.append("maximum-new-connections : ").append(getMaximumNewConnections());
		sb.append("prototype-count : ").append(getPrototypeCount());
		sb.append("maximum-connection-lifetime : ").append(getMaximumConnectionLifetime());
		sb.append("test-before-use : ").append(isTestBeforeUse());
		sb.append("test-after-use : ").append(isTestAfterUse());
		sb.append("trace : ").append(isTrace());
		sb.append("-----------------------------------");
		return super.toString();
	}

}
