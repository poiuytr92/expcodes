package exp.libs.warp.db.redis.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.db.redis.RedisClient;

public class RedisKV {
	
	protected final static Logger log = LoggerFactory.getLogger(RedisKV.class);
	
	protected final static String DEFAULT_KV_NAME = "REDIS_KV";
	
	protected final String KV_NAME;
	
	protected RedisClient redis;
	
	public RedisKV(String kvName, RedisClient redis) {
		this.KV_NAME = (kvName == null ? DEFAULT_KV_NAME : kvName);
		this.redis = (redis == null ? new RedisClient() : redis);
	}
	
	public boolean set(String value) {
		boolean isOk = false;
		try {
			isOk = redis.addKV(KV_NAME, value);
			
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	public boolean append(String value) {
		boolean isOk = false;
		try {
			isOk = redis.appendKV(KV_NAME, value) >= 0;
			
		} catch(Exception e) {
			log.error("写入redis缓存失败", e);
		}
		return isOk;
	}
	
	public String get() {
		String value = null;
		try {
			value = redis.getVal(KV_NAME);
			
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return value;
	}

}
