package exp.libs.warp.db.nosql.redis;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import exp.libs.envm.Charset;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

class _JedisCluster extends JedisCluster implements _IJedis {

	/** 默认字符集编码 */
	private final static String CHARSET = Charset.UTF8;
	
	/** Redis部分接口的返回值 */
	private final static String OK = "OK";

	/** 测试Redis连接有效性的返回值 */
	private final static String PONG = "PONG";
	
	public _JedisCluster(Set<HostAndPort> jedisClusterNode,
			int connectionTimeout, int soTimeout, int maxAttempts,
			String password, GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password,
				poolConfig);
	}

	@Override
	public void destory() {
		try {
			super.close();
		} catch (IOException e) {}
	}
	
	@Override
	public boolean clearAll() {
		return false;	// 集群模式不支持此操作
	}
	
	@Override
	public boolean existKey(String key) {
		return super.exists(key);
	}

	
}
