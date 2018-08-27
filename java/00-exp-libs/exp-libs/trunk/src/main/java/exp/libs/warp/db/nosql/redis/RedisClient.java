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
 * 内部自带连接池, 适用于Redis单机/主从/哨兵/集群模式 (自动根据实际配置切换到集群/非集群的连接方式)
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedisClient {

	public final static String DEFAULT_IP = "127.0.0.1";
	
	public final static int DEFAULT_PORT = 6379;
	
	/**
	 * Redis连接接口, 其实现类有两个：
	 * 	_Jedis : 适用于Redis单机/主从/哨兵模式
	 *  _JedisCluster : 适用于Redis集群模式
	 */
	private _IJedis iJedis;
	
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
	
	public RedisClient() {
		this(DEFAULT_IP, DEFAULT_PORT);
	}
	
	public RedisClient(String ip, int port) {
		this.iJedis = new _Jedis(ip, port);
	}

	public RedisClient(String ip, int port, int timeout) {
		this.iJedis = new _Jedis(timeout, ip, port);
	}

	public RedisClient(String ip, int port, String password) {
		this.iJedis = new _Jedis(password, ip, port);
	}

	public RedisClient(String ip, int port, int timeout, String password) {
		this.iJedis = new _Jedis(timeout, password, ip, port);
	}
	
	public RedisClient(String ip, int port, int timeout, 
			GenericObjectPoolConfig poolConfig) {
		this.iJedis = new _Jedis(poolConfig, timeout, ip, port);
	}
	
	public RedisClient(String ip, int port, String password, 
			GenericObjectPoolConfig poolConfig) {
		this.iJedis = new _Jedis(poolConfig, password, ip, port);
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param timeout
	 * @param password
	 * @param poolConfig
	 */
	public RedisClient(String ip, int port, int timeout, String password, 
			GenericObjectPoolConfig poolConfig) {
		this.iJedis = new _Jedis(poolConfig, timeout, password, ip, port);
	}
	
	public RedisClient(HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(removeDuplicate(clusterNodes));
	}

	public RedisClient(int timeout, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(timeout, 
				removeDuplicate(clusterNodes));
	}
	
	public RedisClient(String password, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(password, 
				removeDuplicate(clusterNodes));
	}
	
	public RedisClient(int timeout, String password, 
			HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(timeout, password, 
				removeDuplicate(clusterNodes));
	}
	
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(poolConfig, timeout, 
				removeDuplicate(clusterNodes));
	}
	
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			String password, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(poolConfig, password, 
				removeDuplicate(clusterNodes));
	}
	
	/**
	 * 
	 * @param poolConfig
	 * @param timeout
	 * @param password
	 * @param clusterNodes
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, String password, HostAndPort... clusterNodes) {
		this.iJedis = new _JedisCluster(
				poolConfig, timeout, password, clusterNodes);
	}
	
	public RedisClient(String... clusterSockets) {
		this(toHostAndPorts(clusterSockets));
	}
	
	public RedisClient(int timeout, String... clusterSockets) {
		this(timeout, toHostAndPorts(clusterSockets));
	}
	
	public RedisClient(String password, String... clusterSockets) {
		this(password, toHostAndPorts(clusterSockets));
	}
	
	public RedisClient(int timeout, String password, String... clusterSockets) {
		this(timeout, password, toHostAndPorts(clusterSockets));
	}
	
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, String... clusterSockets) {
		this(poolConfig, timeout, toHostAndPorts(clusterSockets));
	}
	
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			String password, String... clusterSockets) {
		this(poolConfig, password, toHostAndPorts(clusterSockets));
	}
	
	/**
	 * 
	 * @param poolConfig
	 * @param timeout
	 * @param clusterSocket 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, 
			int timeout, String password, String... clusterSockets) {
		this(poolConfig, timeout, password, toHostAndPorts(clusterSockets));
	}

	/**
	 * 集群节点去重
	 * @param clusterNodes
	 * @return
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
	
	private static HostAndPort[] toArray(List<HostAndPort> clusterNodes) {
		HostAndPort[] array = new HostAndPort[clusterNodes.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = clusterNodes.get(i);
		}
		return array;
	}
	
	/**
	 * 测试Redis连接是否有效(集群模式不支持此操作)
	 * @return true:连接成功; false:连接失败
	 */
	public boolean isVaild() {
		return iJedis.isVaild();
	}
	
	/**
	 * <pre>
	 * (集群模式不支持此操作)
	 * 非集群且使用连接池的情况下, redis的操作默认均为短连接.
	 * 此方法可邻接切换操作模式为长连接, 在调用 @link{ commit() } 方法后恢复为短连接模式.
	 * </pre>
	 * @param autoCommit true:自动提交; false:手动提交(需调用 @link{ commit() } 方法)
	 */
	public void autoCommit(boolean autoCommit) {
		iJedis.autoCommit(autoCommit);
	}
	
	/**
	 * 把redis操作模式切换为默认的短连接模式(集群模式不支持此操作)
	 */
	public void commit() {
		iJedis.commit();
	}
	
	/**
	 * 断开Redis连接
	 */
	public void close() {
		iJedis.destory();
	}
	
	/**
	 * 清空Redis库中所有数据(此方法在集群模式下无效)
	 * @return true:清空成功; false:清空失败
	 */
	public boolean clearAll() {
		return iJedis.clearAll();
	}
	
	/**
	 * 检查Redis库里面是否存在某个键值
	 * @param key 被检查的键值
	 * @return true:存在; false:不存在
	 */
	public boolean existKey(String key) {
		return iJedis.existKey(key);
	}
	
	/**
	 * 删除若干个键（及其对应的内容）
	 * @param keys 指定的键集
	 * @return 删除成功的个数
	 */
	public long delKeys(String... keys) {
		return iJedis.delKeys(keys);
	}
	
	/**
	 * 新增一个键值对
	 * @param key 新的键
	 * @param value 新的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addKV(String key, String value) {
		return iJedis.addKV(key, value);
	}
	
	/**
	 * 在已有的键key的原值的末尾附加value（仅针对键值对使用）
	 * @param key 已有/新的键
	 * @param value 附加的值
	 * @return 附加值后，该键上最新的值的总长度
	 */
	public long appendKV(String key, String value) {
		return iJedis.appendKV(key, value);
	}
	
	/**
	 * 获取指定键的值
	 * @param key 指定的键
	 * @return 对应的值（若不存在键则返回null）
	 */
	public String getVal(String key) {
		return iJedis.getVal(key);
	}
	
	/**
	 * <pre>
	 * 新增一个对象（该对象须实现Serializable接口）。
	 * 该对象会以序列化形式存储到Redis。
	 * </pre>
	 * @param key 指定的键
	 * @param object 新增的对象（须实现Serializable接口）
	 * @return
	 */
	public boolean addObj(String key, Serializable object) {
		return iJedis.addObj(key, object);
	}
	
	/**
	 * <pre>
	 * 获取指定键的对象。
	 * 该对象会从Redis反序列化。
	 * </pre>
	 * @param key 指定的键
	 * @return 反序列化的对象（若失败则返回null）
	 */
	public Object getObj(String key) {
		return iJedis.getObj(key);
	}
	
	/**
	 * 新增一个 键->哈希表
	 * @param key 键值
	 * @param map 哈希表
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addMap(String key, Map<String, String> map) {
		return iJedis.addMap(key, map);
	}
	
	/**
	 * 新增一个 键值对 到 指定哈希表
	 * @param key 哈希表的键
	 * @param mapKey 新增到哈希表的键
	 * @param mapValue 新增到哈希表的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addToMap(String key, String mapKey, String mapValue) {
		return iJedis.addToMap(key, mapKey, mapValue);
	}
	
	/**
	 * 新增一个 键值对 到 指定哈希表
	 * @param key 哈希表的键
	 * @param mapKey 新增到哈希表的键
	 * @param mapValue 新增到哈希表的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addToMap(String key, String mapKey, Serializable mapValue) {
		return iJedis.addToMap(key, mapKey, mapValue);
	}
	
	/**
	 * 获取某个哈希表中的某个键的值
	 * @param mapKey 哈希表的键
	 * @param inMapKey 哈希表中的某个键
	 * @return 哈希表中对应的值（若不存在返回null）
	 */
	public String getMapVal(String mapKey, String inMapKey) {
		return iJedis.getMapVal(mapKey, inMapKey);
	}
	
	/**
	 * 获取某个哈希表中的若干个键的值
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 哈希表中对应的一些值
	 */
	public List<String> getMapVals(String mapKey, String... inMapKeys) {
		return iJedis.getMapVals(mapKey, inMapKeys);
	}
	
	/**
	 * 获取某个哈希表中的某个键的值对象（反序列化对象）
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的某个键
	 * @return 哈希表中对应的值对象（反序列化对象，若不存在返回null）
	 */
	public Object getMapObj(String mapKey, String inMapKey) {
		return iJedis.getMapObj(mapKey, inMapKey);
	}
	
	/**
	 * 获取某个哈希表中的若干个键的值对象（反序列化对象）
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 哈希表中对应的一些值对象（反序列化对象）
	 */
	public List<Object> getMapObjs(String mapKey, String... inMapKeys) {
		return iJedis.getMapObjs(mapKey, inMapKeys);
	}
	
	/**
	 * 删除某个哈希表中的若干个键（及其对应的值）
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 删除成功的个数
	 */
	public long delMapKeys(String mapKey, String... inMapKeys) {
		return iJedis.delMapKeys(mapKey, inMapKeys);
	}
	
	/**
	 * 添加一些值到列表
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToList(String listKey, String... listValues) {
		return iJedis.addToList(listKey, listValues);
	}
	
	/**
	 * 添加一些值到列表头部
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToListHead(String listKey, String... listValues) {
		return iJedis.addToListHead(listKey, listValues);
	}
	
	/**
	 * 添加一些值到列表尾部
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToListTail(String listKey, String... listValues) {
		return iJedis.addToListTail(listKey, listValues);
	}
	
	/**
	 * 获取列表中的所有值
	 * @param listKey 列表的键
	 * @return 列表中的所有值
	 */
	public List<String> getListVals(String listKey) {
		return iJedis.getListVals(listKey);
	}
	
	/**
	 * 添加一些值到集合
	 * @param setKey 集合的键
	 * @param setValues 添加的值
	 * @return 成功添加到该集合的值个数
	 */
	public long addToSet(String setKey, String... setValues) {
		return iJedis.addToSet(setKey, setValues);
	}
	
	/**
	 * 获取集合中的值
	 * @param setKey 集合的键
	 * @return 集合中的值
	 */
	public Set<String> getSetVals(String setKey) {
		return iJedis.getSetVals(setKey);
	}
	
	/**
	 * 检测某个值是否在指定集合中
	 * @param setKey 集合的键
	 * @param setValue 被检测的值
	 * @return true:在集合中; false:不在集合中
	 */
	public boolean inSet(String setKey, String setValue) {
		return iJedis.inSet(setKey, setValue);
	}
	
	/**
	 * 获取集合的大小
	 * @param setKey 集合的键
	 * @return 集合的大小
	 */
	public long getSetSize(String setKey) {
		return iJedis.getSetSize(setKey);
	}
	
	/**
	 * 删除集合中的一些值
	 * @param setKey 集合的键
	 * @param setValues 被删除的值
	 * @return 成功从改集合中删除的值个数
	 */
	public long delSetVals(String setKey, String... setValues) {
		return iJedis.delSetVals(setKey, setValues);
	}
	
}
