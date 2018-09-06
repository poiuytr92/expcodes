package exp.libs.warp.db.redis.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.db.redis.RedisClient;

/**
 * <PRE>
 * Redis-Map对象（其键类型固定为String，值类型通过泛型动态声明）.
 * 模仿HashMap的使用习惯进行封装.
 * ------------------------------
 * 
 * 使用样例:
 * 
 * final String MAP_IN_REDIS_KEY = "map在Redis中的键名（自定义且需唯一）";
 * RedisClient redis = new RedisClient("127.0.0.1", 6379);	// redis连接客户端（支持单机/集群）
 * 
 * RedisMap&lt;自定义 对象&gt; map = new RedisMap&lt;自定义 对象&gt;(MAP_IN_REDIS_KEY, redis);
 * map.put(key, val);
 * map.putAll(otherMap);
 * map.get(key);
 * map.containsKey(key);
 * map.isEmpty();
 * map.size();
 * map.keySet();
 * map.values();
 * map.getAll();
 * map.remove(key);
 * map.clear();
 * 
 * redis.close();	// 断开redis连接
 * 
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedisMap<OBJ extends Serializable> {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(RedisMap.class);
	
	/** 此哈希表的默认键名 */
	private final static String DEFAULT_MAP_NAME = "REDIS_MAP";
	
	/** 此哈希表在redis中的键名（需确保不为空） */
	private final String MAP_NAME;
	
	/** Redis连接客户端对象 */
	private RedisClient redis;
	
	/** 所声明的泛型是否为String类型（否则为自定义类型） */
	private boolean typeIsStr;
	
	/**
	 * 构造函数
	 * @param mapName 此哈希表在redis中的键名（需确保不为空）
	 * @param redis redis客户端连接对象（需确保可用）
	 */
	public RedisMap(String mapName, RedisClient redis) {
		this.MAP_NAME = (mapName == null ? DEFAULT_MAP_NAME : mapName);
		this.redis = (redis == null ? new RedisClient() : redis);
		this.typeIsStr = false;
	}
	
	/**
	 * 实时在redis缓存中检查此哈希表是否包含某个键
	 * @param key 被检查的键
	 * @return true:包含; false:不包含
	 */
	public boolean containsKey(String key) {
		boolean isExist = false;
		try {
			isExist = redis.existKeyInMap(MAP_NAME, key);
			
		} catch(Exception e) {
			log.error("查询redis缓存失败", e);
		}
		return isExist;
	}
	
	/**
	 * 实时在redis缓存中检查此哈希表是否为空
	 * @return true:为空; false:非空
	 */
	public boolean isEmpty() {
		return size() <= 0;
	}
	
	/**
	 * 实时在redis缓存中查询此哈希表的大小
	 * @return 哈希表的大小
	 */
	public long size() {
		long size = 0L;
		try {
			size = redis.getMapSize(MAP_NAME);
			
		} catch(Exception e) {
			log.error("查询redis缓存失败", e);
		}
		return size;
	}
	
	/**
	 * 实时在redis缓存中向此哈希表添加一个键值对
	 * @param key 键
	 * @param value 值
	 * @return true:添加成功; false:添加失败
	 */
	public boolean put(String key, OBJ value) {
		boolean isOk = false;
		if(key == null || value == null) {
			return isOk;
		}
		
		try {
			if(typeIsStr || value instanceof String) {
				typeIsStr = true;
				isOk = redis.addStrValToMap(MAP_NAME, key, (String) value);
				
			} else {
				typeIsStr = false;
				isOk = redis.addSerialObjToMap(MAP_NAME, key, value);
			}
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	/**
	 * 实时在redis缓存中向此哈希表添加一个哈希表
	 * @param map 哈希表
	 * @return true:添加成功; false:添加失败
	 */
	public boolean putAll(Map<String, OBJ> map) {
		boolean isOk = false;
		if(map == null || map.isEmpty()) {
			return isOk;
		}
		
		isOk = true;
		redis.closeAutoCommit();
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			OBJ obj = map.get(key);
			isOk &= put(key, obj);
		}
		redis.commit();
		return isOk;
	}
	
	/**
	 * 实时在redis缓存中查询此哈希表中的一个键值
	 * @param key 被查询的键
	 * @return 对应的值（若不存在返回null）
	 */
	@SuppressWarnings("unchecked")
	public OBJ get(String key) {
		OBJ value = null;
		if(isEmpty()) {
			return value;
		}
		
		try {
			if(typeIsStr == true) {
				String str = redis.getStrValInMap(MAP_NAME, key);
				if(str != null) {
					value = (OBJ) str;
				}
				
			} else {
				Object obj = redis.getSerialObjInMap(MAP_NAME, key);
				if(obj != null) {
					value = (OBJ) obj;
				}
			}
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return value;
	}
	
	/**
	 * 实时在redis缓存中查询此哈希表的所有键值对
	 * @return 所有键值对（即使为空也只会返回空表，不会返回null）
	 */
	public Map<String, OBJ> getAll() {
		Map<String, OBJ> map = new HashMap<String, OBJ>();
		
		redis.closeAutoCommit();
		Set<String> keys = keySet();
		for(String key : keys) {
			map.put(key, get(key));
		}
		redis.commit();
		return map;
	}
	
	/**
	 * 实时在redis缓存中查询此哈希表的所有键集
	 * @return 所有键集（即使为空也只会返回空表，不会返回null）
	 */
	public Set<String> keySet() {
		Set<String> keys = null;
		try {
			keys = redis.getAllKeysInMap(MAP_NAME);
			
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return (keys == null ? new HashSet<String>() : keys);
	}
	
	/**
	 * 实时在redis缓存中查询此哈希表的所有值集
	 * @return 所有值集（即使为空也只会返回空表，不会返回null）
	 */
	public Collection<OBJ> values() {
		return getAll().values();
	}
	
	/**
	 * 实时在redis缓存中删除此哈希表的若干个键值对
	 * @param keys 被删除的键集
	 * @return 已成功删除的数量
	 */
	public long remove(String... keys) {
		long num = 0L;
		try {
			num = redis.delKeysInMap(MAP_NAME, keys);
			
		} catch(Exception e) {
			log.error("删除redis缓存失败", e);
		}
		return num;
	}
	
	/**
	 * 实时在redis缓存中删除此哈希表
	 * @return true:删除成功; false:删除失败
	 */
	public boolean clear() {
		boolean isOk = false;
		try {
			isOk = redis.delKeys(MAP_NAME) >= 0;
			
		} catch(Exception e) {
			log.error("删除redis缓存失败", e);
		}
		return isOk;
	}
	
}
