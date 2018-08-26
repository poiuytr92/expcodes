package exp.libs.warp.db.nosql;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import exp.libs.envm.Charset;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.ObjUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Redis数据库工具（仅适用于Redis单机模式）
 * ----------------------------------
 *  若Redis是以集群模式部署, 使用此连接池虽然可以连接, 但只是连接到集群的其中一台机器.
 *  换而言之，此时只能在这台特定的机器上面进行数据读写.
 *  若在机器A上面写, 再在机器B上面读, 就会因为不是使用集群连接而报错: JedisMovedDataException: MOVED 866
 *  解决方式是改用 JedisCluster 连接到集群.
 *  若redis不是集群部署模式，不能使用JedisCluster，否则报错：ERR This instance has cluster support disabled 
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedisUtils {

	/** 默认字符集编码 */
	private final static String CHARSET = Charset.UTF8;
	
	/** Redis部分接口的返回值 */
	private final static String OK = "OK";

	/** 测试Redis连接有效性的返回值 */
	private final static String PONG = "PONG";
	
	/** 私有化构造函数 */
	protected RedisUtils() {}
	
	/**
	 * 测试Redis连接
	 * @param ip redis IP
	 * @param port redis端口
	 * @return true:连接成功; false:连接失败
	 */
	public static boolean testConn(String ip, int port) {
		return testConn(ip, port, null);
	}
	
	/**
	 * 测试Redis连接
	 * @param ip redis IP
	 * @param port redis端口
	 * @param password redis密码
	 * @return true:连接成功; false:连接失败
	 */
	public static boolean testConn(String ip, int port, String password) {
		Jedis jedis = new Jedis(ip, port);
		boolean isOk = testConn(jedis, password);
		jedis.close();
		return isOk;
	}
	
	/**
	 * 测试Redis连接
	 * @param jedis redis连接对象
	 * @return true:连接成功; false:连接失败
	 */
	public static boolean testConn(Jedis jedis) {
		return testConn(jedis, null);
	}
	
	/**
	 * 测试Redis连接
	 * @param jedis redis连接对象
	 * @param password redis密码
	 * @return true:连接成功; false:连接失败
	 */
	public static boolean testConn(Jedis jedis, String password) {
		boolean isOk = false;
		if(jedis != null) {
			if(StrUtils.isNotTrimEmpty(password)) {
				jedis.auth(password);
			}
			isOk = PONG.equalsIgnoreCase(jedis.ping());
		}
		return isOk;
	}
	
	/**
	 * 获取Redis连接
	 * @param ip redis IP
	 * @param port redis端口
	 * @return
	 */
	public static Jedis getConn(String ip, int port) {
		return getConn(ip, port, "");
	}
	
	/**
	 * 获取Redis连接
	 * @param ip redis IP
	 * @param port redis端口
	 * @param password redis密码
	 * @return
	 */
	public static Jedis getConn(String ip, int port, String password) {
		Jedis jedis = new Jedis(ip, port);
		if(StrUtils.isNotTrimEmpty(password)) {
			jedis.auth(password);
		}
		return jedis;
	}
	
	/**
	 * 清空Redis库中所有数据
	 * @param jedis redis连接对象
	 * @return true:清空成功; false:清空失败
	 */
	public static boolean clearAllDatas(Jedis jedis) {
		boolean isOk = false;
		if(jedis != null) {
			isOk = OK.equalsIgnoreCase(jedis.flushAll());
		}
		return isOk;
	}
	
	/**
	 * 检查Redis库里面是否存在某个键值
	 * @param jedis redis连接对象
	 * @param key 被检查的键值
	 * @return true:存在; false:不存在
	 */
	public static boolean existKey(Jedis jedis, String key) {
		boolean isExist = false;
		if(jedis != null && key != null) {
			isExist = jedis.exists(key);
		}
		return isExist;
	}
	
	/**
	 * 删除若干个键（及其对应的内容）
	 * @param jedis redis连接对象
	 * @param keys 指定的键集
	 * @return 删除成功的个数
	 */
	public static long delKeys(Jedis jedis, String... keys) {
		long num = 0;
		if(jedis != null && keys != null) {
			num = jedis.del(keys);
		}
		return num;
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
	 * @return 对应的值（若不存在键则返回null）
	 */
	public static String getVal(Jedis jedis, String key) {
		String value = null;
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
	 * @return true:新增成功; false:新增失败
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
		long num = 0;
		if(jedis != null && mapKey != null && inMapKeys != null) {
			num = jedis.hdel(mapKey, inMapKeys);
		}
		return num;
	}
	
	/**
	 * 添加一些值到列表
	 * @param jedis redis连接对象
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public static long addToList(Jedis jedis, String listKey, String... listValues) {
		return addToListTail(jedis, listKey, listValues);
	}
	
	/**
	 * 添加一些值到列表头部
	 * @param jedis redis连接对象
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public static long addToListHead(Jedis jedis, String listKey, String... listValues) {
		long num = 0;
		if(jedis != null && listKey != null && listValues != null) {
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				num = jedis.lpush(listKey, value);
			}
		}
		return num;
	}
	
	/**
	 * 添加一些值到列表尾部
	 * @param jedis redis连接对象
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public static long addToListTail(Jedis jedis, String listKey, String... listValues) {
		long num = 0;
		if(jedis != null && listKey != null && listValues != null) {
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				num = jedis.rpush(listKey, value);
			}
		}
		return num;
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
		long num = 0;
		if(jedis != null && setKey != null && setValues != null) {
			num = jedis.sadd(setKey, setValues);
		}
		return num;
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
		long num = 0;
		if(jedis != null && setKey != null && setValues != null) {
			num = jedis.srem(setKey, setValues);
		}
		return num;
	}
	
}
