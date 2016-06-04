package service.pool;

import org.apache.thrift.transport.TSocket;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2015-12-30 上午09:33:58 
 * 实现连接池
 */
public class GenericConnectionProvider implements ConnectionProvider {
	
	public static final Logger logger = LoggerFactory.getLogger(GenericConnectionProvider.class);
	/** 服务的IP地址 */
	private String serviceIP = "127.0.0.1";
	/** 服务的端口 */
	private int servicePort = 9813;
	/** 连接超时配置 */
	private int conTimeOut = 3000;
	/** 链接池中最大连接数,默认为8 */
	private int maxActive = 5;
	/** 链接池中最大空闲的连接数,默认为8 */
	private int maxIdle = 5;
	/** 连接池中最少空闲的连接数,默认为0 */
	private int minIdle = 2;
	/** 当连接池资源耗尽时，调用者最大阻塞的时间，超时将跑出异常。单位，毫秒数;默认为-1.表示永不超时 */
	private long maxWait = 3000;
	/** 向调用者输出“链接”资源时，是否检测是有有效，如果无效则从连接池中移除，并尝试获取继续获取。默认为false。建议保持默认值,是否执行PoolableObjectFactory.validateObject方法*/
	private boolean testOnBorrow = false;
	/** 向连接池“归还”链接时，是否检测“链接”对象的有效性。默认为false。建议保持默认值 */
	private boolean testOnReturn = false;
	/** 向调用者输出“链接”对象时，是否检测它的空闲超时；默认为false。如果“链接”空闲超时，将会被移除 */
	private boolean testWhileIdle = false;
	/** 当“连接池”中active数量达到阀值时，即“链接”资源耗尽时，连接池需要采取的手段, 默认为1：
       -> 0 : 抛出异常，
       -> 1 : 阻塞，直到有可用链接资源
       -> 2 : 强制创建新的链接资源 */
	private byte whenExhaustedAction = 1;
	/** 对象缓存池 */
	private ObjectPool objectPool = null;
	
	private static GenericConnectionProvider gcp = null;
	
	private GenericConnectionProvider() {
		ThriftPoolableObjectFactory thriftPoolableObjectFactory = new ThriftPoolableObjectFactory(serviceIP, servicePort, conTimeOut);	
		GenericObjectPool.Config config = new GenericObjectPool.Config();
		config.maxActive = maxActive;
		config.maxIdle = maxIdle;
		config.minIdle = minIdle;
		config.maxWait = maxWait;
		config.testOnBorrow = testOnBorrow;
		config.testOnReturn = testOnReturn;
		config.testWhileIdle = testWhileIdle;
		config.whenExhaustedAction = whenExhaustedAction;
		objectPool = new GenericObjectPool(thriftPoolableObjectFactory, config);
	}
	
	public static GenericConnectionProvider init() {
		if(gcp == null) {
			gcp = new GenericConnectionProvider();
		}
		return gcp;
	}

	public TSocket getConnection() {
		try {
			System.out.println(objectPool.getNumActive() + "/" + objectPool.getNumIdle());
            TSocket socket = (TSocket)objectPool.borrowObject();
            System.out.println(objectPool.getNumActive() + "/" + objectPool.getNumIdle() + "\n");
            return socket;
        }catch(Exception e) {
            throw new RuntimeException("error getConnection()", e);
        }
	}

	public void closeConnection(TSocket socket) {
		try {
            objectPool.returnObject(socket);
        }catch(Exception e) {
            throw new RuntimeException("error closeConnection()", e);
        }
	}
	
	public void destroyPool() {
		try {
			objectPool.close();
		}catch(Exception e) {
            throw new RuntimeException("error destroyPool()", e);
        }
	}

}