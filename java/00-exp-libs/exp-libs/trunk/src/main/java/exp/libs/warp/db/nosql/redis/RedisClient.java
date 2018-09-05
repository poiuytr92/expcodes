package exp.libs.warp.db.nosql.redis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.db.nosql.bean.RedisBean;

/**
 * <PRE>
 * Redis连接客户端.
 * 内部自带连接池, 适用于Redis单机/主从/哨兵/集群模式 
 * (根据实际Redis的配置，使用不同的构造函数即可，已屏蔽到集群/非集群的连接/操作方式差异性)
 * --------------------------------------------------
 * <br/>
 * 科普：
 * 	对于 单机/主从/哨兵 模式，连接方式都是一样的，使用{@link #Jedis}实例连接
 * 	对于 集群 模式，则需要使用{@link #JedisCluster}实例连接
 * 
 * 	一般情况下，对于 主从/哨兵 模式，只需要连接到主机即可（或者连接从机亦可，但一般不建议）
 * 	特别地，对于 哨兵模式，一定不能连接到 哨兵机（哨兵机是用于监控主从机器，当主机挂掉的时候重新选举主机的，不做数据业务）
 * 
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedisClient implements _IJedis {

	/** 默认redis IP */
	public final static String DEFAULT_IP = "127.0.0.1";
	
	/** 默认redis 端口 */
	public final static int DEFAULT_PORT = 6379;
	
	/**
	 * Redis连接接口, 其实现类有两个：
	 * 	_Jedis : 适用于Redis单机/主从/哨兵模式
	 *  _JedisCluster : 适用于Redis集群模式
	 */
	private _IJedis iJedis;
	
	/**
	 * 构造函数
	 * @param rb redis配置对象（通过{@link RedisBean#isCluster()}方法自动切换集群/非集群模式）
	 */
	public RedisClient(RedisBean rb) {
		
		// 默认模式
		if(rb == null) {
			this.iJedis = new _Jedis(DEFAULT_IP, DEFAULT_PORT);
			
		// 非集群模式
		} else if(!rb.isCluster()) {
			HostAndPort hp = new HostAndPort(DEFAULT_IP, DEFAULT_PORT);
			Iterator<String> sockets = rb.getSockets().iterator();
			if(sockets.hasNext()) {
				hp = toHostAndPort(sockets.next());
			}
			this.iJedis = new _Jedis(rb.toPoolConfig(), rb.getTimeout(), 
					rb.getPassword(), hp.getHost(), hp.getPort());
			
		// 集群模式
		} else {
			List<HostAndPort> clusterNodes = new LinkedList<HostAndPort>();
			Set<String> sockets = rb.getSockets();
			for(String socket : sockets) {
				HostAndPort node = toHostAndPort(socket);
				if(node != null) {
					clusterNodes.add(node);
				}
			}
			this.iJedis = new _JedisCluster(rb.toPoolConfig(), rb.getTimeout(), 
					rb.getPassword(), toArray(clusterNodes));
		}
	}
	
	/**
	 * 构造函数（单机模式）
	 * 使用默认的IP端口： 127.0.0.1:6379
	 */
	public RedisClient() {
		this(DEFAULT_IP, DEFAULT_PORT);
	}
	
	/**
	 * 构造函数（适用单机/主从/哨兵模式） 
	 * @param ip redis IP
	 * @param port redis端口
	 */
	public RedisClient(String ip, int port) {
		this.iJedis = new _Jedis(ip, port);
	}

	/**
	 * 构造函数（适用单机/主从/哨兵模式） 
	 * @param ip redis IP
	 * @param port redis端口
	 * @param timeout 超时时间(ms)
	 */
	public RedisClient(String ip, int port, int timeout) {
		this.iJedis = new _Jedis(timeout, ip, port);
	}

	/**
	 * 构造函数（适用单机/主从/哨兵模式） 
	 * @param ip redis IP
	 * @param port redis端口
	 * @param password redis密码
	 */
	public RedisClient(String ip, int port, String password) {
		this.iJedis = new _Jedis(password, ip, port);
	}

	/**
	 * 构造函数（适用单机/主从/哨兵模式） 
	 * @param ip redis IP
	 * @param port redis端口
	 * @param timeout 超时时间(ms)
	 * @param password redis密码
	 */
	public RedisClient(String ip, int port, int timeout, String password) {
		this.iJedis = new _Jedis(timeout, password, ip, port);
	}
	
	/**
	 * 构造函数（适用单机/主从/哨兵模式） 
	 * @param ip redis IP
	 * @param port redis端口
	 * @param timeout 超时时间(ms)
	 * @param poolConfig 连接池配置
	 */
	public RedisClient(String ip, int port, int timeout, 
			GenericObjectPoolConfig poolConfig) {
		this.iJedis = new _Jedis(poolConfig, timeout, ip, port);
	}
	
	/**
	 * 构造函数（适用单机/主从/哨兵模式） 
	 * @param ip redis IP
	 * @param port redis端口
	 * @param password redis密码
	 * @param poolConfig 连接池配置
	 */
	public RedisClient(String ip, int port, String password, 
			GenericObjectPoolConfig poolConfig) {
		this.iJedis = new _Jedis(poolConfig, password, ip, port);
	}
	
	/**
	 * 构造函数（适用单机/主从/哨兵模式） 
	 * @param ip redis IP
	 * @param port redis端口
	 * @param timeout 超时时间(ms)
	 * @param password redis密码
	 * @param poolConfig 连接池配置
	 */
	public RedisClient(String ip, int port, int timeout, String password, 
			GenericObjectPoolConfig poolConfig) {
		this.iJedis = new _Jedis(poolConfig, timeout, password, ip, port);
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param clusterNodes 集群节点
	 */
	public RedisClient(HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(removeDuplicate(clusterNodes));
	}

	/**
	 * 构造函数（适用集群模式） 
	 * @param timeout 超时时间(ms)
	 * @param clusterNodes 集群节点
	 */
	public RedisClient(int timeout, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(timeout, 
				removeDuplicate(clusterNodes));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param password redis密码
	 * @param clusterNodes 集群节点
	 */
	public RedisClient(String password, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(password, 
				removeDuplicate(clusterNodes));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param timeout 超时时间(ms)
	 * @param password redis密码
	 * @param clusterNodes 集群节点
	 */
	public RedisClient(int timeout, String password, 
			HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(timeout, password, 
				removeDuplicate(clusterNodes));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param poolConfig 连接池配置
	 * @param timeout 超时时间(ms)
	 * @param clusterNodes 集群节点
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(poolConfig, timeout, 
				removeDuplicate(clusterNodes));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param poolConfig 连接池配置
	 * @param password redis密码
	 * @param clusterNodes 集群节点
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			String password, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(poolConfig, password, 
				removeDuplicate(clusterNodes));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param poolConfig 连接池配置
	 * @param timeout 超时时间(ms)
	 * @param password redis密码
	 * @param clusterNodes 集群节点
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, String password, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(
				poolConfig, timeout, password, clusterNodes);
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param clusterSockets 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(String... clusterSockets) {
		this(toHostAndPorts(clusterSockets));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param timeout 超时时间(ms)
	 * @param clusterSockets 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(int timeout, String... clusterSockets) {
		this(timeout, toHostAndPorts(clusterSockets));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param password redis密码
	 * @param clusterSockets 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(String password, String... clusterSockets) {
		this(password, toHostAndPorts(clusterSockets));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param timeout 超时时间(ms)
	 * @param password redis密码
	 * @param clusterSockets 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(int timeout, String password, String... clusterSockets) {
		this(timeout, password, toHostAndPorts(clusterSockets));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param poolConfig 连接池配置
	 * @param timeout 超时时间(ms)
	 * @param clusterSockets 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, String... clusterSockets) {
		this(poolConfig, timeout, toHostAndPorts(clusterSockets));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param poolConfig 连接池配置
	 * @param password redis密码
	 * @param clusterSockets 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			String password, String... clusterSockets) {
		this(poolConfig, password, toHostAndPorts(clusterSockets));
	}
	
	/**
	 * 构造函数（适用集群模式） 
	 * @param poolConfig 连接池配置
	 * @param timeout 超时时间(ms)
	 * @param password redis密码
	 * @param clusterSockets 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, String password, String... clusterSockets) {
		this(poolConfig, timeout, password, toHostAndPorts(clusterSockets));
	}

	/**
	 * 集群节点去重
	 * @param clusterNodes 集群节点
	 * @return 去重后的集群节点
	 */
	private static HostAndPort[] removeDuplicate(HostAndPort[] clusterNodes) {
		List<HostAndPort> list = new LinkedList<HostAndPort>();
		if(clusterNodes != null) {
			Set<String> sockets = new HashSet<String>();
			for(HostAndPort node : clusterNodes) {
				if(sockets.add(node.toString())) {
					list.add(node);
				}
			}
		}
		return toArray(list);
	}
	
	/**
	 * 把socket字符串格式的集群节点转换成HostAndPort格式
	 * @param clusterSockets socket字符串格式的集群节点
	 * @return HostAndPort格式的集群节点
	 */
	private static HostAndPort[] toHostAndPorts(String[] clusterSockets) {
		List<HostAndPort> list = new LinkedList<HostAndPort>();
		if(clusterSockets != null) {
			for(String socket : clusterSockets) {
				HostAndPort hp = toHostAndPort(socket);
				if(hp != null) {
					list.add(hp);
				}
			}
		}
		return toArray(list);
	}
	
	/**
	 * 把socket字符串格式的字符串转换成HostAndPort格式
	 * @param socket socket字符串格式
	 * @return HostAndPort格式
	 */
	private static HostAndPort toHostAndPort(String socket) {
		HostAndPort hp = null;
		if(StrUtils.isNotTrimEmpty(socket)) {
			String[] rst = HostAndPort.extractParts(socket);
			if(rst.length == 2) {
				String host = rst[0];
				int port = NumUtils.toInt(rst[1], DEFAULT_PORT);
				hp = new HostAndPort(host, port);
			}
		}
		return hp;
	}
	
	/**
	 * 把HostAndPort链表转换成数组
	 * @param clusterNodes HostAndPort链表
	 * @return HostAndPort数组
	 */
	private static HostAndPort[] toArray(List<HostAndPort> clusterNodes) {
		HostAndPort[] array = new HostAndPort[clusterNodes.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = clusterNodes.get(i);
		}
		return array;
	}

	@Override
	public boolean isVaild() {
		return iJedis.isVaild();
	}

	@Override
	public void setAutoCommit(boolean autoCommit) {
		iJedis.setAutoCommit(autoCommit);
	}

	@Override
	public void closeAutoCommit() {
		iJedis.closeAutoCommit();
	}

	@Override
	public void commit() {
		iJedis.commit();
	}

	@Override
	public void destory() {
		iJedis.destory();
	}
	
	/**
	 * 断开redis连接
	 */
	public void close() {
		destory();
	}

	@Override
	public boolean clearAll() {
		return iJedis.clearAll();
	}

	@Override
	public boolean existKey(String redisKey) {
		return iJedis.existKey(redisKey);
	}

	@Override
	public long delKeys(String... redisKeys) {
		return iJedis.delKeys(redisKeys);
	}

	@Override
	public boolean addKV(String redisKey, String value) {
		return iJedis.addKV(redisKey, value);
	}

	@Override
	public long appendKV(String redisKey, String value) {
		return iJedis.appendKV(redisKey, value);
	}

	@Override
	public String getVal(String redisKey) {
		return iJedis.getVal(redisKey);
	}

	@Override
	public boolean addObj(String redisKey, Serializable object) {
		return iJedis.addObj(redisKey, object);
	}

	@Override
	public Object getObj(String redisKey) {
		return iJedis.getObj(redisKey);
	}

	@Override
	public boolean addMap(String redisKey, Map<String, String> map) {
		return iJedis.addMap(redisKey, map);
	}

	@Override
	public Map<String, String> getMap(String redisKey) {
		return iJedis.getMap(redisKey);
	}

	@Override
	public boolean addObjMap(String redisKey, Map<String, Serializable> map) {
		return iJedis.addObjMap(redisKey, map);
	}
	
	@Override
	public Map<String, Object> getObjMap(String redisKey) {
		return iJedis.getObjMap(redisKey);
	}

	@Override
	public boolean addToMap(String redisKey, String key, String value) {
		return iJedis.addToMap(redisKey, key, value);
	}

	@Override
	public boolean addToMap(String redisKey, String key, Serializable object) {
		return iJedis.addToMap(redisKey, key, object);
	}

	@Override
	public String getMapVal(String redisKey, String key) {
		return iJedis.getMapVal(redisKey, key);
	}

	@Override
	public List<String> getMapVals(String redisKey, String... keys) {
		return iJedis.getMapVals(redisKey, keys);
	}

	@Override
	public List<String> getMapVals(String redisKey) {
		return iJedis.getMapVals(redisKey);
	}
	
	@Override
	public Object getMapObj(String redisKey, String key) {
		return iJedis.getMapObj(redisKey, key);
	}

	@Override
	public List<Object> getMapObjs(String redisKey, String... keys) {
		return iJedis.getMapObjs(redisKey, keys);
	}

	@Override
	public List<Object> getMapObjs(String redisKey) {
		return iJedis.getMapObjs(redisKey);
	}

	@Override
	public boolean existMapKey(String redisKey, String key) {
		return iJedis.existMapKey(redisKey, key);
	}
	
	@Override
	public Set<String> getMapKeys(String redisKey) {
		return iJedis.getMapKeys(redisKey);
	}
	
	@Override
	public long delMapKeys(String redisKey, String... keys) {
		return iJedis.delMapKeys(redisKey, keys);
	}

	@Override
	public long getMapSize(String redisKey) {
		return iJedis.getMapSize(redisKey);
	}
	
	@Override
	public long addToList(String redisKey, String... values) {
		return iJedis.addToList(redisKey, values);
	}

	@Override
	public long addToListHead(String redisKey, String... values) {
		return iJedis.addToListHead(redisKey, values);
	}

	@Override
	public long addToListTail(String redisKey, String... values) {
		return iJedis.addToListTail(redisKey, values);
	}

	@Override
	public List<String> getListVals(String redisKey) {
		return iJedis.getListVals(redisKey);
	}

	@Override
	public long addToSet(String redisKey, String... values) {
		return iJedis.addToSet(redisKey, values);
	}

	@Override
	public Set<String> getSetVals(String redisKey) {
		return iJedis.getSetVals(redisKey);
	}

	@Override
	public boolean inSet(String redisKey, String value) {
		return iJedis.inSet(redisKey, value);
	}

	@Override
	public long getSetSize(String redisKey) {
		return iJedis.getSetSize(redisKey);
	}

	@Override
	public long delSetVals(String redisKey, String... values) {
		return iJedis.delSetVals(redisKey, values);
	}

}
