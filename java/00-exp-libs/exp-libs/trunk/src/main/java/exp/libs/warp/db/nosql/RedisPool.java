package exp.libs.warp.db.nosql;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import exp.libs.utils.other.ObjUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.VerifyUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

/**
 * <PRE>
 * Redis连接池.
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedisPool {

	/** 默认的Redis IP */
    private final static String DEFAULT_IP = "127.0.0.1";
    
    private String ip;
    
    /** 默认的Redis端口 */
    private final static int DEFAULT_PORT = 6379;
    
    private int port;
    
    /** 默认的访问密码 */
    private final static String DEFAULT_AUTH = "";
    
    private String password;
    
    /** 默认连接超时时间(ms) */
    private final static int DEFAULT_TIMEOUT = 10000;
    
    private int timeout;
    
    /**
     * <pre>
     * 可用连接Jedis实例的最大数目（即可同时存在的最大连接数）。
     * JedisPool的默认值为8； 若为-1，则表示不限制。
     * 
     * 若连接池已分配了最大的jedis实例，则此时连接池的状态为exhausted(耗尽)
     * </pre>
     */
    private final static int DEFAULT_MAX_TOTAL = 20;
    
    private int maxConn;
    
    /**
     * <pre>
     * 连接池中可同时存在最大的Jedis空闲实例（即空闲连接数）。
     * JedisPool的默认值为8。
     * </pre>
     */
    private final static int DEFAULT_MAX_IDLE = 200;
    
    private int maxIdle;
    
    /**
     * <pre>
     * 当连接池已满时，等待可用连接的最大时间（单位:ms）。
     * JedisPool的默认值为-1，表示永不超时。
     * 如果超过等待时间，则直接抛出JedisConnectionException
     * </pre>
     */
    private final static long DEFAULT_MAX_WAIT = 10000;
    
    private long maxWaitMillis;
    
    /**
     * <pre>
     * 在borrow一个jedis实例时，是否提前进行validate操作。
     * 如果为true，则得到的jedis实例均是可用的。
     * </pre>
     */
    private final static boolean DEFAULT_TEST_ON_BORROW = true;
    
    private boolean testOnBorrow;
    
    /** Redis连接池 */
    private JedisPool pool;
	
    /**
     * 构造函数
     */
	public RedisPool() {
		this(DEFAULT_IP, DEFAULT_PORT, DEFAULT_AUTH, 
				DEFAULT_TIMEOUT, DEFAULT_MAX_TOTAL);
	}
	
	/**
	 * 构造函数
	 * @param ip redis的IP
	 * @param port redis的端口
	 */
	public RedisPool(String ip, int port) {
		this(ip, port, DEFAULT_AUTH, DEFAULT_TIMEOUT, DEFAULT_MAX_TOTAL);
	}
	
	/**
	 * 构造函数
	 * @param ip redis的IP
	 * @param port redis的端口
	 * @param password redis的密码
	 */
	public RedisPool(String ip, int port, String password) {
		this(ip, port, password, DEFAULT_TIMEOUT, DEFAULT_MAX_TOTAL);
	}
	
	/**
	 * 构造函数
	 * @param ip redis的IP
	 * @param port redis的端口
	 * @param password redis的密码
	 *  @param timeout 连超超时时间(ms)，-1表示不限制
	 * @param maxConn 连接池的最大连接数，-1表示不限制
	 */
	public RedisPool(String ip, int port, String password, int timeout, int maxConn) {
		setIp(ip);
		setPort(port);
		setAuthentication(password);
		setTimeout(timeout);
		setMaxConn(maxConn);
		setMaxIdle(DEFAULT_MAX_IDLE);
		setMaxWaitMillis(DEFAULT_MAX_WAIT);
		setTestOnBorrow(DEFAULT_TEST_ON_BORROW);
		
		initRedisPool();
	}
	
	/**
	 * 构造函数
	 * @param ds 数据源
	 */
	public RedisPool(DataSourceBean ds) {
		setIp(ds.getIp());
		setPort(ds.getPort());
		setAuthentication(ds.getPassword());
		setTimeout((int) ds.getMaximumActiveTime());
		setMaxConn(ds.getMaximumConnectionCount());
		setMaxIdle(ds.getPrototypeCount());
		setMaxWaitMillis(ds.getMaximumConnectionLifetime());
		setTestOnBorrow(ds.isTestBeforeUse());
		
		initRedisPool();
	}
	
	/**
     * 初始化Redis连接池
     */
    protected void initRedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(getMaxConn());
        config.setMaxIdle(getMaxIdle());
        config.setMaxWaitMillis(getMaxWaitMillis());
        config.setTestOnBorrow(isTestOnBorrow());
        
        if(StrUtils.isTrimEmpty(getPassword())) {
        	this.pool = new JedisPool(config, getIp(), getPort(), getTimeout());
        	
        } else {
        	this.pool = new JedisPool(config, 
            		getIp(), getPort(), getTimeout(), getPassword());
        }
    }
    
    /**
     * 从Redis连接池从获取连接
     * @return
     */
    public Jedis getConn() {
    	Jedis Jedis = null;
        if(pool != null) {
        	synchronized (pool) {
        		Jedis = pool.getResource();
			}
        }
        return Jedis;
    }
    
    /**
     * 归还连接到Redis连接池
     * @param jedis
     */
    public void close(Jedis jedis) {
        if(jedis != null) {
//        	pool.returnResource(jedis);
            jedis.close();
        }
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = (password != null ? password :
			(this.password != null ? this.password : DEFAULT_AUTH));
	}
	
	public String getAuthentication() {
		return getPassword();
	}
	
	public void setAuthentication(String password) {
		setPassword(password);
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	
	@Override
	public String toString() {
		return ObjUtils.toBeanInfo(this);
	}

}
