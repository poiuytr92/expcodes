package exp.libs.warp.db.nosql;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Redis数据库工具.
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedisUtils {

	private final static String OK = "OK";
	
	private final static String PONG = "PONG";
	
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
	
	public static boolean existKey(Jedis jedis, String key) {
		boolean isExist = false;
		if(jedis != null && key != null) {
			isExist = jedis.exists(key);
		}
		return isExist;
	}
	
	/**
	 * 新增一个键值对
	 * @param jedis
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean addKV(Jedis jedis, String key, String value) {
		boolean isOk = false;
		if(jedis != null && key != null && value != null) {
			isOk = OK.equalsIgnoreCase(jedis.set(key, value));
		}
		return isOk;
	}
	
	/**
	 * 在已有的键key的原值的末尾附加value
	 * @param jedis
	 * @param key
	 * @param value
	 * @return
	 */
	public static long appendKV(Jedis jedis, String key, String value) {
		long len = -1;
		if(jedis != null && key != null && value != null) {
			len = jedis.append(key, value);
		}
		return len;
	}
	
	public static String getVal(Jedis jedis, String key) {
		String value = "";
		if(jedis != null && key != null) {
			value = jedis.get(key);
		}
		return value;
	}
	
	/**
	 * 删除若干个键
	 * @param jedis
	 * @param keys
	 * @return
	 */
	public static long delKeys(Jedis jedis, String... keys) {
		long size = 0;
		if(jedis != null && keys != null) {
			size = jedis.del(keys);
		}
		return size;
	}
	
	public static boolean addMap(Jedis jedis, String key, Map<String, String> map) {
		boolean isOk = false;
		if(jedis != null && key != null && map != null) {
			isOk = OK.equalsIgnoreCase(jedis.hmset(key, map));
		}
		return isOk;
	}
	
	public static List<String> getMapVals(Jedis jedis, String mapKey, String... inMapKeys) {
		List<String> values = new LinkedList<String>();
		if(jedis != null && mapKey != null && inMapKeys != null) {
			values = jedis.hmget(mapKey, inMapKeys);
		}
		return values;
	}
	
	public static long delMapKeys(Jedis jedis, String mapKey, String... inMapKeys) {
		long size = 0;
		if(jedis != null && mapKey != null && inMapKeys != null) {
			size = jedis.hdel(mapKey, inMapKeys);
		}
		return size;
	}
	
	/**
	 * 
	 * @param jedis
	 * @param listKey
	 * @param listValues
	 * @return 所操作的队列的总长度
	 */
	public static long addToList(Jedis jedis, String listKey, String... listValues) {
		return addToListHead(jedis, listKey, listValues);
	}
	
	/**
	 * 
	 * @param jedis
	 * @param listKey
	 * @param listValues
	 * @return 所操作的队列的总长度
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
	 * 
	 * @param jedis
	 * @param listKey
	 * @param listValues
	 * @return 所操作的队列的总长度
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
	
	public static List<String> getListVals(Jedis jedis, String listKey) {
		List<String> values = new LinkedList<String>();
		if(jedis != null && listKey != null) {
			values = jedis.lrange(listKey, 0, -1);
		}
		return values;
	}
	
	/**
	 * 
	 * @param jedis
	 * @param setKey
	 * @param setValues
	 * @return 成功添加到所操作的set的值个数
	 */
	public static long addToSet(Jedis jedis, String setKey, String... setValues) {
		long addNum = 0;
		if(jedis != null && setKey != null && setValues != null) {
			addNum = jedis.sadd(setKey, setValues);
		}
		return addNum;
	}
	
	public static Set<String> getSetVals(Jedis jedis, String setKey) {
		Set<String> values = new HashSet<String>();
		if(jedis != null && setKey != null) {
			values = jedis.smembers(setKey);
		}
		return values;
	}
	
	public static boolean inSet(Jedis jedis, String setKey, String setValue) {
		boolean isExist = false;
		if(jedis != null && setKey != null && setValue != null) {
			isExist = jedis.sismember(setKey, setValue);
		}
		return isExist;
	}
	
	public static long getSetSize(Jedis jedis, String setKey) {
		long size = 0;
		if(jedis != null && setKey != null) {
			size = jedis.scard(setKey);
		}
		return size;
	}
	
	/**
	 * 
	 * @param jedis
	 * @param setKey
	 * @param setValues
	 * @return 从所操作的set中删除的值个数
	 */
	public static long delSetVals(Jedis jedis, String setKey, String... setValues) {
		long size = 0;
		if(jedis != null && setKey != null && setValues != null) {
			size = jedis.srem(setKey, setValues);
		}
		return size;
	}
	
	public static void main(String[] args) {
		String ip = "192.168.177.131";
		int port = 6379;
		String pswd = "123456";
		RedisPool pool = new RedisPool(ip, port, pswd);
		Jedis jedis = pool.getConn();
		
		System.out.println(addToSet(jedis, "set1", "dff", "Sdfa"));
		
		System.out.println(jedis.sadd("set1", "xyz", "tte", "sada"));
		System.out.println(delSetVals(jedis, "set1", "sada"));
		System.out.println(getSetVals(jedis, "set1"));
		System.out.println(jedis.scard("set1"));
		
		jedis.close();
	}
	
}
