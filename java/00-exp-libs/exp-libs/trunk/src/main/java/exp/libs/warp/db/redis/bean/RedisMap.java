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

public class RedisMap<OBJ extends Serializable> {

	private final static Logger log = LoggerFactory.getLogger(RedisMap.class);
	
	private final static String DEFAULT_MAP_NAME = "REDIS_MAP";
	
	private final String MAP_NAME;
	
	private RedisClient redis;
	
	private boolean typeIsStr;
	
	/**
	 * 构造函数
	 * @param mapName 哈希表在redis中的名称（需确保不为空）
	 * @param redis redi客户端对象
	 */
	public RedisMap(String mapName, RedisClient redis) {
		this.MAP_NAME = (mapName == null ? DEFAULT_MAP_NAME : mapName);
		this.redis = (redis == null ? new RedisClient() : redis);
		this.typeIsStr = true;
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
	
	public boolean put(String key, OBJ value) {
		boolean isOk = false;
		if(key == null || value == null) {
			return isOk;
		}
		
		try {
			if(typeIsStr || value instanceof String) {
				typeIsStr = true;
				isOk = redis.addToMap(MAP_NAME, key, (String) value);
				
			} else {
				typeIsStr = false;
				isOk = redis.addToMap(MAP_NAME, key, value);
			}
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
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
	
	@SuppressWarnings("unchecked")
	public OBJ get(String key) {
		OBJ value = null;
		if(isEmpty()) {
			return value;
		}
		
		try {
			if(typeIsStr == true) {
				String str = redis.getMapVal(MAP_NAME, key);
				if(str != null) {
					value = (OBJ) str;
				}
				
			} else {
				Object obj = redis.getMapObj(MAP_NAME, key);
				if(obj != null) {
					value = (OBJ) obj;
				}
			}
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return value;
	}
	
	public Map<String, OBJ> getAll() {
		redis.closeAutoCommit();
		Map<String, OBJ> map = new HashMap<String, OBJ>();
		Set<String> keys = keySet();
		for(String key : keys) {
			map.put(key, get(key));
		}
		redis.commit();
		return map;
	}
	
	public Set<String> keySet() {
		Set<String> keys = null;
		try {
			keys = redis.getMapKeys(MAP_NAME);
			
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return (keys == null ? new HashSet<String>() : keys);
	}
	
	public Collection<OBJ> values() {
		return getAll().values();
	}
	
	public boolean remove(String key) {
		boolean isOk = false;
		try {
			isOk = redis.delMapKeys(MAP_NAME, key) >= 0;
			
		} catch(Exception e) {
			log.error("删除redis缓存失败", e);
		}
		return isOk;
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
