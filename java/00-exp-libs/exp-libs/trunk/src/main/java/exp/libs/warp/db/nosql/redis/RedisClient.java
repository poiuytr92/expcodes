package exp.libs.warp.db.nosql.redis;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.ObjUtils;
import exp.libs.warp.db.nosql.RedisPool;

//对于 Jedis 是唯一的对象，若连接超时则自动根据ip端口密码重连
// 对于 JedisPool 用完就放回池再重新申请新的 Jedis 对象
// 对于 JedisCluster 是唯一的对象（即使JedisCluster使用池也仅仅是内部节点用连接池，这个对象也是不变的）


public class RedisClient {

	/** （适用于Redis单机模式） */
	/**
	 * Redis连接接口, 其实现类有两个：
	 * 	_Jedis : 适用于Redis单机模式
	 *  _JedisCluster : 适用于Redis集群模式
	 */
	private _IJedis iJedis;
	
	/** Redis连接池（适用于Redis单机模式） */
	private RedisPool pool;
	
	/** Redis集群连接（适用于Redis主从/哨兵/集群模式） */
	private JedisCluster cluster;
	
	public RedisClient(JedisPoolConfig poolConfig, int timeout, 
			String ip, int port, String password) {
		iJedis = new _Jedis();
	}
	
	/**
	 * 
	 * @param poolConfig
	 * @param clusterSocket 集群连接Socket串（格式为 ip:port，  如 127.0.0.1:6739）
	 */
	public RedisClient(GenericObjectPoolConfig poolConfig, int timeout, 
			String... clusterSocket) {
		
	}

	@SuppressWarnings("unchecked")
	public RedisClient(GenericObjectPoolConfig poolConfig, int timeout, 
			HostAndPort... clusterNodes) {
//		iJedis = new _JedisCluster(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
	}
	
	
	public static void main(String[] args) throws IOException {
		test();
	}
	
	public static void test() throws IOException {
		JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(10);
        config.setMaxWaitMillis(1000);
        config.setTestOnBorrow(true);
		
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		nodes.add(new HostAndPort("172.25.241.43", 16389));
		JedisCluster jc = new JedisCluster(nodes, config);
		System.out.println(jc.get("hello"));
//		jc = new JedisCluster(nodes, poolConfig);
		jc.close();
		
		System.out.println(jc.get("hello"));
		
//		Jedis conn = RedisUtils.getConn("172.25.241.43", 16379);
//		conn.getClient().getHost();
//		conn.getClient().getPort();
//		conn.getClient().
//		System.out.println(conn.clusterInfo());
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
	 * @param jedis redis连接对象
	 * @param keys 指定的键集
	 * @return 删除成功的个数
	 */
	public static long delKeys(Jedis jedis, String... keys) {
		long size = 0;
		if(jedis != null && keys != null) {
			size = jedis.del(keys);
		}
		return size;
	}
	
	/**
	 * 新增一个键值对
	 * @param jedis redis连接对象
	 * @param key 新的键
	 * @param value 新的值
	 * @return true:新增成功; false:新增失败
	 */
	public static boolean addKV(Jedis jedis, String key, String value) {
		boolean isOk = false;
		if(jedis != null && key != null && value != null) {
			isOk = OK.equalsIgnoreCase(jedis.set(key, value));
		}
		return isOk;
	}
	
	/**
	 * 在已有的键key的原值的末尾附加value（仅针对键值对使用）
	 * @param jedis redis连接对象
	 * @param key 已有/新的键
	 * @param value 附加的值
	 * @return 附加值后，该键上最新的值的总长度
	 */
	public static long appendKV(Jedis jedis, String key, String value) {
		long len = -1;
		if(jedis != null && key != null && value != null) {
			len = jedis.append(key, value);
		}
		return len;
	}
	
	/**
	 * 获取指定键的值
	 * @param jedis redis连接对象
	 * @param key 指定的键
	 * @return 对应的值
	 */
	public static String getVal(Jedis jedis, String key) {
		String value = "";
		if(jedis != null && key != null) {
			value = jedis.get(key);
		}
		return value;
	}
	
	/**
	 * <pre>
	 * 新增一个对象（该对象须实现Serializable接口）。
	 * 该对象会以序列化形式存储到Redis。
	 * </pre>
	 * @param jedis redis连接对象
	 * @param key 指定的键
	 * @param object 新增的对象（须实现Serializable接口）
	 * @return
	 */
	public static boolean addObj(Jedis jedis, String key, Serializable object) {
		boolean isOk = false;
		if(jedis != null && key != null && object != null) {
			try {
				isOk = OK.equalsIgnoreCase(jedis.set(
						key.getBytes(CHARSET), ObjUtils.toSerializable(object)));
			} catch (UnsupportedEncodingException e) {}
		}
		return isOk;
	}
	
	/**
	 * <pre>
	 * 获取指定键的对象。
	 * 该对象会从Redis反序列化。
	 * </pre>
	 * @param jedis redis连接对象
	 * @param key 指定的键
	 * @return 反序列化的对象（若失败则返回null）
	 */
	public static Object getObj(Jedis jedis, String key) {
		Object object = null;
		if(jedis != null && key != null) {
			try {
				object = ObjUtils.unSerializable(
						jedis.get(key.getBytes(CHARSET)));
			} catch (UnsupportedEncodingException e) {}
		}
		return object;
	}
	
	/**
	 * 新增一个 键->哈希表
	 * @param jedis redis连接对象
	 * @param key 键值
	 * @param map 哈希表
	 * @return true:新增成功; false:新增失败
	 */
	public static boolean addMap(Jedis jedis, String key, Map<String, String> map) {
		boolean isOk = false;
		if(jedis != null && key != null && map != null) {
			isOk = OK.equalsIgnoreCase(jedis.hmset(key, map));
		}
		return isOk;
	}
	
	/**
	 * 新增一个 键值对 到 指定哈希表
	 * @param jedis redis连接对象
	 * @param key 哈希表的键
	 * @param mapKey 新增到哈希表的键
	 * @param mapValue 新增到哈希表的值
	 * @return true:新增成功; false:新增失败
	 */
	public static boolean addToMap(Jedis jedis, String key, 
			String mapKey, String mapValue) {
		boolean isOk = false;
		if(jedis != null && key != null && 
				mapKey != null && mapValue != null) {
			isOk = jedis.hset(key, mapKey, mapValue) >= 0;
		}
		return isOk;
	}
	
	/**
	 * 新增一个 键值对 到 指定哈希表
	 * @param jedis redis连接对象
	 * @param key 哈希表的键
	 * @param mapKey 新增到哈希表的键
	 * @param mapValue 新增到哈希表的值
	 * @return true:新增成功; false:新增失败
	 */
	public static boolean addToMap(Jedis jedis, String key, 
			String mapKey, Serializable mapValue) {
		boolean isOk = false;
		if(jedis != null && key != null && 
				mapKey != null && mapValue != null) {
			try {
				isOk = jedis.hset(key.getBytes(CHARSET), 
						mapKey.getBytes(CHARSET), 
						ObjUtils.toSerializable(mapValue)) >= 0;
			} catch (UnsupportedEncodingException e) {}
		}
		return isOk;
	}
	
	/**
	 * 获取某个哈希表中的某个键的值
	 * @param jedis redis连接对象
	 * @param mapKey 哈希表的键
	 * @param inMapKey 哈希表中的某个键
	 * @return 哈希表中对应的值（若不存在返回null）
	 */
	public static String getMapVal(Jedis jedis, String mapKey, String inMapKey) {
		String value = null;
		if(jedis != null && mapKey != null && inMapKey != null) {
			List<String> values = jedis.hmget(mapKey, inMapKey);
			if(ListUtils.isNotEmpty(values)) {
				value = values.get(0);
			}
		}
		return value;
	}
	
	/**
	 * 获取某个哈希表中的若干个键的值
	 * @param jedis redis连接对象
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 哈希表中对应的一些值
	 */
	public static List<String> getMapVals(Jedis jedis, String mapKey, String... inMapKeys) {
		List<String> values = null;
		if(jedis != null && mapKey != null && inMapKeys != null) {
			values = jedis.hmget(mapKey, inMapKeys);
		}
		return (values == null ? new LinkedList<String>() : values);
	}
	
	/**
	 * 获取某个哈希表中的某个键的值对象（反序列化对象）
	 * @param jedis redis连接对象
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的某个键
	 * @return 哈希表中对应的值对象（反序列化对象，若不存在返回null）
	 */
	public static Object getMapObj(Jedis jedis, String mapKey, String inMapKey) {
		Object value = null;
		if(jedis != null && mapKey != null && inMapKey != null) {
			try {
				byte[] key = mapKey.getBytes(CHARSET);
				List<byte[]> byteVals = jedis.hmget(key, inMapKey.getBytes(CHARSET));
				if(ListUtils.isNotEmpty(byteVals)) {
					value = ObjUtils.unSerializable(byteVals.get(0));
				}
			} catch (UnsupportedEncodingException e) {}
		}
		return value;
	}
	
	/**
	 * 获取某个哈希表中的若干个键的值对象（反序列化对象）
	 * @param jedis redis连接对象
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 哈希表中对应的一些值对象（反序列化对象）
	 */
	public static List<Object> getMapObjs(Jedis jedis, String mapKey, String... inMapKeys) {
		List<Object> values = new LinkedList<Object>();
		if(jedis != null && mapKey != null && inMapKeys != null) {
			try {
				byte[] key = mapKey.getBytes(CHARSET);
				for(String inMapKey : inMapKeys) {
					List<byte[]> byteVals = jedis.hmget(key, inMapKey.getBytes(CHARSET));
					if(ListUtils.isEmpty(byteVals)) {
						values.add(null);
						
					} else {
						values.add(ObjUtils.unSerializable(byteVals.get(0)));
					}
				}
			} catch (UnsupportedEncodingException e) {}
		}
		return (values == null ? new LinkedList<Object>() : values);
	}
	
	/**
	 * 删除某个哈希表中的若干个键（及其对应的值）
	 * @param jedis redis连接对象
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 删除成功的个数
	 */
	public static long delMapKeys(Jedis jedis, String mapKey, String... inMapKeys) {
		long size = 0;
		if(jedis != null && mapKey != null && inMapKeys != null) {
			size = jedis.hdel(mapKey, inMapKeys);
		}
		return size;
	}
	
	/**
	 * 添加一些值到列表
	 * @param jedis redis连接对象
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public static long addToList(Jedis jedis, String listKey, String... listValues) {
		return addToListHead(jedis, listKey, listValues);
	}
	
	/**
	 * 添加一些值到列表头部
	 * @param jedis redis连接对象
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public static long addToListHead(Jedis jedis, String listKey, String... listValues) {
		long size = 0;
		if(jedis != null && listKey != null && listValues != null) {
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				size = jedis.lpush(listKey, value);
			}
		}
		return size;
	}
	
	/**
	 * 添加一些值到列表尾部
	 * @param jedis redis连接对象
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public static long addToListTail(Jedis jedis, String listKey, String... listValues) {
		long size = 0;
		if(jedis != null && listKey != null && listValues != null) {
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				size = jedis.rpush(listKey, value);
			}
		}
		return size;
	}
	
	/**
	 * 获取列表中的所有值
	 * @param jedis redis连接对象
	 * @param listKey 列表的键
	 * @return 列表中的所有值
	 */
	public static List<String> getListVals(Jedis jedis, String listKey) {
		List<String> values = new LinkedList<String>();
		if(jedis != null && listKey != null) {
			values = jedis.lrange(listKey, 0, -1);
		}
		return values;
	}
	
	/**
	 * 添加一些值到集合
	 * @param jedis redis连接对象
	 * @param setKey 集合的键
	 * @param setValues 添加的值
	 * @return 成功添加到该集合的值个数
	 */
	public static long addToSet(Jedis jedis, String setKey, String... setValues) {
		long addNum = 0;
		if(jedis != null && setKey != null && setValues != null) {
			addNum = jedis.sadd(setKey, setValues);
		}
		return addNum;
	}
	
	/**
	 * 获取集合中的值
	 * @param jedis redis连接对象
	 * @param setKey 集合的键
	 * @return 集合中的值
	 */
	public static Set<String> getSetVals(Jedis jedis, String setKey) {
		Set<String> values = new HashSet<String>();
		if(jedis != null && setKey != null) {
			values = jedis.smembers(setKey);
		}
		return values;
	}
	
	/**
	 * 检测某个值是否在指定集合中
	 * @param jedis redis连接对象
	 * @param setKey 集合的键
	 * @param setValue 被检测的值
	 * @return true:在集合中; false:不在集合中
	 */
	public static boolean inSet(Jedis jedis, String setKey, String setValue) {
		boolean isExist = false;
		if(jedis != null && setKey != null && setValue != null) {
			isExist = jedis.sismember(setKey, setValue);
		}
		return isExist;
	}
	
	/**
	 * 获取集合的大小
	 * @param jedis redis连接对象
	 * @param setKey 集合的键
	 * @return 集合的大小
	 */
	public static long getSetSize(Jedis jedis, String setKey) {
		long size = 0;
		if(jedis != null && setKey != null) {
			size = jedis.scard(setKey);
		}
		return size;
	}
	
	/**
	 * 删除集合中的一些值
	 * @param jedis redis连接对象
	 * @param setKey 集合的键
	 * @param setValues 被删除的值
	 * @return 成功从改集合中删除的值个数
	 */
	public static long delSetVals(Jedis jedis, String setKey, String... setValues) {
		long size = 0;
		if(jedis != null && setKey != null && setValues != null) {
			size = jedis.srem(setKey, setValues);
		}
		return size;
	}
	
}
