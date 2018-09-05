package exp.libs.warp.db.nosql.bean;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.db.nosql.redis.RedisClient;

public class RedisMap {

	protected final static Logger log = LoggerFactory.getLogger(RedisMap.class);
	
	protected final static String DEFAULT_MAP_NAME = "REDIS_MAP";
	
	protected final String MAP_NAME;
	
	protected RedisClient redis;
	
	/**
	 * 构造函数
	 * @param mapName 哈希表在redis中的名称（需确保不为空）
	 * @param redis redi客户端对象
	 */
	public RedisMap(String mapName, RedisClient redis) {
		this.MAP_NAME = (mapName == null ? DEFAULT_MAP_NAME : mapName);
		this.redis = (redis == null ? new RedisClient() : redis);
	}

	public boolean containsKey(String key) {
		boolean isExist = false;
		try {
			isExist = redis.existMapKey(MAP_NAME, key);
			
		} catch(Exception e) {
			log.error("查询redis缓存失败", e);
		}
		return isExist;
	}
	
	public boolean isEmpty() {
		return size() <= 0;
	}
	
	public long size() {
		long size = 0L;
		try {
			size = redis.getMapSize(MAP_NAME);
			
		} catch(Exception e) {
			log.error("查询redis缓存失败", e);
		}
		return size;
	}
	
	public boolean put(String key, String value) {
		boolean isOk = false;
		try {
			isOk = redis.addToMap(MAP_NAME, key, value);
			
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	public boolean putAll(Map<String, String> map) {
		boolean isOk = false;
		try {
			isOk = redis.addMap(MAP_NAME, map);
			
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	public String get(String key) {
		String value = null;
		try {
			value = redis.getMapVal(MAP_NAME, key);
			
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return value;
	}
	
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
