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
public class RedisClient {

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
	 * 使用默认的IP端口： 127.0.01:6379
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
	
	/**
	 * 测试Redis连接是否有效(集群模式不支持此操作)
	 * @return true:连接成功; false:连接失败
	 */
	public boolean isVaild() {
		return iJedis.isVaild();
	}
	
	/**
	 * <pre>
	 * 在用连接池的情况下, redis的操作默认均为短连接.
	 * 此方法可邻接切换操作模式为长连接, 在调用{@link #commit}方法后恢复为短连接模式.
	 * -----------------
	 * 此方法非多线程安全, 集群模式不支持此操作
	 * </pre>
	 * @param autoCommit true:自动提交; false:手动提交(需调用{@link #commit}方法)
	 */
	public void setAutoCommit(boolean autoCommit) {
		iJedis.setAutoCommit(autoCommit);
	}
	
	/**
	 * 把redis操作模式切换为默认的短连接模式
	 * -----------------
	 * 此方法非多线程安全, 集群模式不支持此操作
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
