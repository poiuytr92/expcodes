package exp.libs.warp.db.nosql.redis;

import redis.clients.jedis.Jedis;
import exp.libs.envm.Charset;
import exp.libs.warp.db.nosql.RedisPool;

class _Jedis extends Jedis implements _IJedis {

	/** 默认字符集编码 */
	private final static String CHARSET = Charset.UTF8;
	
	/** Redis部分接口的返回值 */
	private final static String OK = "OK";

	/** 测试Redis连接有效性的返回值 */
	private final static String PONG = "PONG";

	private RedisPool pool;
	
	@Override
	public void destory() {
		super.close();
	}
	
	@Override
	public boolean clearAll() {
		return OK.equalsIgnoreCase(this.flushAll());
	}
	
	@Override
	public boolean existKey(String key) {
		return super.exists(key);
	}
	
}
