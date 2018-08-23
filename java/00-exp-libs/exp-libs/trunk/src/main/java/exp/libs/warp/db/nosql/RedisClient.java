package exp.libs.warp.db.nosql;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import exp.libs.utils.other.ListUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

public class RedisClient {

	private boolean isCluster;
	
	public RedisClient(String ip, int port) {
		
	}
	
	public RedisClient(String ip, int port, String password) {
		
	}

	public RedisClient(Jedis jedis) {
	}
	
	public RedisClient(DataSourceBean ds) {
		this(new RedisPool(ds));
	}

	public RedisClient(RedisPool redisPool) {
		
		
		isCluster = false;
	}
	
	/**
	 * 
	 * @param redisClusterConn 集群连接串, 
	 * 		格式为 ip:port，  如 127.0.0.1:6739
	 */
	public RedisClient(String... redisClusterConn) {
		
	}

	@SuppressWarnings("unchecked")
	public RedisClient(HostAndPort... redisClusterNodes) {
		this(new JedisCluster(new HashSet<HostAndPort>(
				ListUtils.asList(redisClusterNodes))));
	}

	public RedisClient(JedisCluster jedisCluster) {
		
		isCluster = true;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		test();
	}
	
	public static void test() throws IOException {
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		nodes.add(new HostAndPort("172.25.241.43", 16389));
		JedisCluster jc = new JedisCluster(nodes);
		System.out.println(jc.get("hello"));
		jc.close();
		
		Jedis conn = RedisUtils.getConn("172.25.241.43", 16379);
//		conn.getClient().getHost();
//		conn.getClient().getPort();
//		conn.getClient().
		System.out.println(conn.clusterInfo());
	}
}
