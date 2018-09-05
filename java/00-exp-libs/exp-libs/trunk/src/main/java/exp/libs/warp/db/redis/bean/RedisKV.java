package exp.libs.warp.db.redis.bean;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.db.redis.RedisClient;

public class RedisKV<OBJ extends Serializable> {
	
	private final static Logger log = LoggerFactory.getLogger(RedisKV.class);
	
	private final static int TYPE_NONE = 0, TYPE_STR = 1, TYPE_OBJ = 2;
	
	private int type;
	
	private final static String DEFAULT_KV_NAME = "REDIS_KV";
	
	private final String KV_NAME;
	
	private RedisClient redis;
	
	public RedisKV(String kvName, RedisClient redis) {
		this.type = TYPE_NONE;
		this.KV_NAME = (kvName == null ? DEFAULT_KV_NAME : kvName);
		this.redis = (redis == null ? new RedisClient() : redis);
	}
	
	public boolean set(OBJ value) {
		boolean isOk = false;
		if(value == null) {
			return isOk;
		}
		
		try {
			if(type == TYPE_STR || value instanceof String) {
				type = TYPE_STR;
				isOk = redis.addVal(KV_NAME, (String) value);
				
			} else {
				type = TYPE_OBJ;
				isOk = redis.addObj(KV_NAME, value);
			}
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	public boolean append(String value) {
		boolean isOk = false;
		if(value == null || type != TYPE_STR) {
			return isOk;
		}
		
		try {
			isOk = redis.appendVal(KV_NAME, value) >= 0;
			
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	@SuppressWarnings("unchecked")
	public OBJ get() {
		OBJ value = null;
		try {
			if(type == TYPE_STR) {
				String str = redis.getVal(KV_NAME);
				if(str != null) {
					value = (OBJ) str;
				}
				
			} else if(type == TYPE_OBJ) {
				Object obj = redis.getObj(KV_NAME);
				if(obj != null) {
					value = (OBJ) obj;
				}
				
			} else {
				Object obj = redis.getObj(KV_NAME);
				if(obj != null) {
					type = TYPE_OBJ;
					value = (OBJ) obj;
					
				} else {
					String str = redis.getVal(KV_NAME);
					if(str != null) {
						type = TYPE_STR;
						value = (OBJ) str;
					}
				}
			}
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return value;
	}
	
	public boolean clear() {
		boolean isOk = false;
		try {
			isOk = redis.delKeys(KV_NAME) >= 0;
			
		} catch(Exception e) {
			log.error("删除redis缓存失败", e);
		}
		return isOk;
	}

}
