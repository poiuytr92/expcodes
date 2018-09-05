package exp.libs.warp.db.redis.bean;

import java.io.Serializable;

import exp.libs.warp.db.redis.RedisClient;

public class RedisKO<OBJ extends Serializable> extends RedisKV {

	public RedisKO(String kvName, RedisClient redis) {
		super(kvName, redis);
	}

	public boolean addObj(OBJ object) {
		boolean isOk = false;
		try {
			isOk = redis.addObj(KV_NAME, object);
			
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}

	@SuppressWarnings("unchecked")
	public OBJ getObj() {
		OBJ obj = null;
		try {
			Object object = redis.getObj(KV_NAME);
			if(object != null) {
				obj = (OBJ) object;
			}
			
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return obj;
	}
	
}
