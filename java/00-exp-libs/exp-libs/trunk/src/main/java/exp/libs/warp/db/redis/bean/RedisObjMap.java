package exp.libs.warp.db.redis.bean;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import exp.libs.warp.db.redis.RedisClient;

public class RedisObjMap<OBJ extends Serializable> extends RedisMap {

	/**
	 * 构造函数
	 * @param mapName 哈希表在redis中的名称（需确保唯一）
	 * @param redis redi客户端对象
	 */
	public RedisObjMap(String mapName, RedisClient redis) {
		super(mapName, redis);
	}

	public boolean putObj(String key, OBJ object) {
		boolean isOk = false;
		try {
			isOk = redis.addToMap(MAP_NAME, key, object);
			
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	public boolean putAllObjs(Map<String, OBJ> map) {
		boolean isOk = false;
		if(map != null) {
			isOk = true;
			Iterator<String> keys = map.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				OBJ obj = map.get(key);
				isOk &= putObj(key, obj);
			}
		}
		return isOk;
	}
	
	@SuppressWarnings("unchecked")
	public OBJ getObj(String key) {
		OBJ obj = null;
		try {
			Object object = redis.getMapObj(MAP_NAME, key);
			if(object != null) {
				obj = (OBJ) object;
			}
			
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return obj;
	}
	
}
