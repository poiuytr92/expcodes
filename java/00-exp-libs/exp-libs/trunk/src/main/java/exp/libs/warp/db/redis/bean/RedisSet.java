package exp.libs.warp.db.redis.bean;

import java.io.Serializable;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.db.redis.RedisClient;

public class RedisSet<OBJ extends Serializable> {

	private final static Logger log = LoggerFactory.getLogger(RedisList.class);
	
	private final static int TYPE_NONE = 0, TYPE_STR = 1, TYPE_OBJ = 2;
	
	private int type;
	
	private final static String DEFAULT_SET_NAME = "REDIS_SET";
	
	private final String SET_NAME;
	
	private RedisClient redis;
	
	/**
	 * 构造函数
	 * @param setName 集合在redis中的名称（需确保不为空）
	 * @param redis redi客户端对象
	 */
	public RedisSet(String setName, RedisClient redis) {
		this.type = TYPE_NONE;
		this.SET_NAME = (setName == null ? DEFAULT_SET_NAME : setName);
		this.redis = (redis == null ? new RedisClient() : redis);
	}
	
	@Override
	public long addToSet(String redisKey, String... values) {
		return iJedis.addToSet(redisKey, values);
	}

	@Override
	public Set<String> getSetVals(String redisKey) {
		return iJedis.getSetVals(redisKey);
	}

	@Override
	public boolean inSet(String redisKey, String value) {
		return iJedis.inSet(redisKey, value);
	}

	@Override
	public long getSetSize(String redisKey) {
		return iJedis.getSetSize(redisKey);
	}

	@Override
	public long delSetVals(String redisKey, String... values) {
		return iJedis.delSetVals(redisKey, values);
	}

}
