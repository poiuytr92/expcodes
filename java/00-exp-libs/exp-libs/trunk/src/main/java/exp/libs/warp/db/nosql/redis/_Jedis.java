package exp.libs.warp.db.nosql.redis;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import exp.libs.envm.Charset;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.ObjUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Redis连接池（仅适用于Redis单机模式）
 * ----------------------------------
 *  若Redis是以主从/哨兵/集群模式部署, 使用此连接池虽然可以连接, 但只是连接到集群的其中一台机器.
 *  换而言之，此时只能在这台特定的机器上面进行数据读写.
 *  若在机器A上面写, 再在机器B上面读, 就会因为不是使用集群连接而报错: JedisMovedDataException: MOVED 866
 *  解决方式是改用 JedisCluster 连接到集群. 
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _Jedis implements _IJedis {

	/** 默认字符集编码 */
	private final static String CHARSET = Charset.UTF8;
	
	/** Redis部分接口的返回值 */
	private final static String OK = "OK";

	/** 测试Redis连接有效性的返回值 */
	private final static String PONG = "PONG";

	/** Jedis连接池 */
	private JedisPool pool;
	
	protected _Jedis(String ip, int port) {
		this(null, Protocol.DEFAULT_TIMEOUT, null, ip, port);
	}
	
	protected _Jedis(int timeout, String ip, int port) {
		this(null, timeout, null, ip, port);
	}
	
	protected _Jedis(String password, String ip, int port) {
		this(null, Protocol.DEFAULT_TIMEOUT, password, ip, port);
	}
	
	protected _Jedis(int timeout, String password, String ip, int port) {
		this(null, timeout, password, ip, port);
	}
	
	protected _Jedis(GenericObjectPoolConfig poolConfig, String ip, int port) {
		this(poolConfig, Protocol.DEFAULT_TIMEOUT, null, ip, port);
	}
	
	protected _Jedis(GenericObjectPoolConfig poolConfig, 
			int timeout, String ip, int port) {
		this(poolConfig, timeout, null, ip, port);
	}
	
	protected _Jedis(GenericObjectPoolConfig poolConfig, 
			String password, String ip, int port) {
		this(poolConfig, Protocol.DEFAULT_TIMEOUT, password, ip, port);
	}
	
	protected _Jedis(GenericObjectPoolConfig poolConfig, 
			int timeout, String password, String ip, int port) {
		if(poolConfig == null) {
			poolConfig = new JedisPoolConfig();
		}
		
		this.pool = StrUtils.isTrimEmpty(password) ? 
				new JedisPool(poolConfig, ip, port, timeout) : 
				new JedisPool(poolConfig, ip, port, timeout, password);
	}
	
	/**
	 * 从连接池获取Redis连接
	 * @return
	 */
	private Jedis _getJedis() {
		return pool.getResource();
	}
	
	/**
	 * 把Redis连接返回连接池
	 * @param jedis
	 */
	private void _close(Jedis jedis) {
//		pool.returnResource(jedis);
		jedis.close();
	}
	
	/**
	 * 测试Redis连接是否有效
	 * @return true:连接成功; false:连接失败
	 */
	public boolean isVaild() {
		Jedis jedis = _getJedis();
		boolean isOk = PONG.equalsIgnoreCase(jedis.ping());
		_close(jedis);
		return isOk;
	}
	
	@Override
	public void destory() {
		pool.close();
	}
	
	@Override
	public boolean clearAll() {
		Jedis jedis = _getJedis();
		boolean isOk = OK.equalsIgnoreCase(jedis.flushAll());
		_close(jedis);
		return isOk;
	}
	
	@Override
	public boolean existKey(String key) {
		Jedis jedis = _getJedis();
		boolean isOk = jedis.exists(key);
		_close(jedis);
		return isOk;
	}
	
	@Override
	public long delKeys(String... keys) {
		long num = 0;
		if(keys != null) {
			Jedis jedis = _getJedis();
			num = jedis.del(keys);
			_close(jedis);
		}
		return num;
	}
	
	@Override
	public boolean addKV(String key, String value) {
		boolean isOk = false;
		if(key != null && value != null) {
			Jedis jedis = _getJedis();
			isOk = OK.equalsIgnoreCase(jedis.set(key, value));
			_close(jedis);
		}
		return isOk;
	}
	
	@Override
	public long appendKV(String key, String value) {
		long len = -1;
		if(key != null && value != null) {
			Jedis jedis = _getJedis();
			len = jedis.append(key, value);
			_close(jedis);
		}
		return len;
	}
	
	@Override
	public String getVal(String key) {
		String value = "";
		if(key != null) {
			Jedis jedis = _getJedis();
			value = jedis.get(key);
			_close(jedis);
		}
		return value;
	}
	
	@Override
	public boolean addObj(String key, Serializable object) {
		boolean isOk = false;
		if(key != null && object != null) {
			Jedis jedis = _getJedis();
			try {
				isOk = OK.equalsIgnoreCase(jedis.set(
						key.getBytes(CHARSET), ObjUtils.toSerializable(object)));
			} catch (UnsupportedEncodingException e) {}
			_close(jedis);
		}
		return isOk;
	}
	
	@Override
	public Object getObj(String key) {
		Object object = null;
		if(key != null) {
			Jedis jedis = _getJedis();
			try {
				object = ObjUtils.unSerializable(
						jedis.get(key.getBytes(CHARSET)));
			} catch (UnsupportedEncodingException e) {}
			_close(jedis);
		}
		return object;
	}
	
	@Override
	public boolean addMap(String key, Map<String, String> map) {
		boolean isOk = false;
		if(key != null && map != null) {
			Jedis jedis = _getJedis();
			isOk = OK.equalsIgnoreCase(jedis.hmset(key, map));
			_close(jedis);
		}
		return isOk;
	}
	
	@Override
	public boolean addToMap(String key, String mapKey, String mapValue) {
		boolean isOk = false;
		if(key != null && mapKey != null && mapValue != null) {
			Jedis jedis = _getJedis();
			isOk = jedis.hset(key, mapKey, mapValue) >= 0;
			_close(jedis);
		}
		return isOk;
	}
	
	@Override
	public boolean addToMap(String key, String mapKey, Serializable mapValue) {
		boolean isOk = false;
		if(key != null && mapKey != null && mapValue != null) {
			Jedis jedis = _getJedis();
			try {
				isOk = jedis.hset(key.getBytes(CHARSET), 
						mapKey.getBytes(CHARSET), 
						ObjUtils.toSerializable(mapValue)) >= 0;
			} catch (UnsupportedEncodingException e) {}
			_close(jedis);
		}
		return isOk;
	}
	
	@Override
	public String getMapVal(String mapKey, String inMapKey) {
		String value = null;
		if(mapKey != null && inMapKey != null) {
			Jedis jedis = _getJedis();
			List<String> values = jedis.hmget(mapKey, inMapKey);
			if(ListUtils.isNotEmpty(values)) {
				value = values.get(0);
			}
			_close(jedis);
		}
		return value;
	}
	
	@Override
	public List<String> getMapVals(String mapKey, String... inMapKeys) {
		List<String> values = null;
		if(mapKey != null && inMapKeys != null) {
			Jedis jedis = _getJedis();
			values = jedis.hmget(mapKey, inMapKeys);
			_close(jedis);
		}
		return (values == null ? new LinkedList<String>() : values);
	}
	
	@Override
	public Object getMapObj(String mapKey, String inMapKey) {
		Object value = null;
		if(mapKey != null && inMapKey != null) {
			Jedis jedis = _getJedis();
			try {
				byte[] key = mapKey.getBytes(CHARSET);
				List<byte[]> byteVals = jedis.hmget(key, inMapKey.getBytes(CHARSET));
				if(ListUtils.isNotEmpty(byteVals)) {
					value = ObjUtils.unSerializable(byteVals.get(0));
				}
			} catch (UnsupportedEncodingException e) {}
			_close(jedis);
		}
		return value;
	}
	
	@Override
	public List<Object> getMapObjs(String mapKey, String... inMapKeys) {
		List<Object> values = new LinkedList<Object>();
		if(mapKey != null && inMapKeys != null) {
			Jedis jedis = _getJedis();
			try {
				byte[] key = mapKey.getBytes(CHARSET);
				for(String inMapKey : inMapKeys) {
					List<byte[]> byteVals = jedis.hmget(key, inMapKey.getBytes(CHARSET));
					if(ListUtils.isEmpty(byteVals)) {
						values.add(null);
						
					} else {
						values.add(ObjUtils.unSerializable(byteVals.get(0)));
					}
				}
			} catch (UnsupportedEncodingException e) {}
			_close(jedis);
		}
		return (values == null ? new LinkedList<Object>() : values);
	}
	
	@Override
	public long delMapKeys(String mapKey, String... inMapKeys) {
		long num = 0;
		if(mapKey != null && inMapKeys != null) {
			Jedis jedis = _getJedis();
			num = jedis.hdel(mapKey, inMapKeys);
			_close(jedis);
		}
		return num;
	}
	
	@Override
	public long addToList(String listKey, String... listValues) {
		return addToListTail(listKey, listValues);
	}
	
	@Override
	public long addToListHead(String listKey, String... listValues) {
		long num = 0;
		if(listKey != null && listValues != null) {
			Jedis jedis = _getJedis();
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				num = jedis.lpush(listKey, value);
			}
			_close(jedis);
		}
		return num;
	}
	
	@Override
	public long addToListTail(String listKey, String... listValues) {
		long num = 0;
		if(listKey != null && listValues != null) {
			Jedis jedis = _getJedis();
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				num = jedis.rpush(listKey, value);
			}
			_close(jedis);
		}
		return num;
	}
	
	@Override
	public List<String> getListVals(String listKey) {
		List<String> values = new LinkedList<String>();
		if(listKey != null) {
			Jedis jedis = _getJedis();
			values = jedis.lrange(listKey, 0, -1);
			_close(jedis);
		}
		return values;
	}
	
	@Override
	public long addToSet(String setKey, String... setValues) {
		long num = 0;
		if(setKey != null && setValues != null) {
			Jedis jedis = _getJedis();
			num = jedis.sadd(setKey, setValues);
			_close(jedis);
		}
		return num;
	}
	
	@Override
	public Set<String> getSetVals(String setKey) {
		Set<String> values = new HashSet<String>();
		if(setKey != null) {
			Jedis jedis = _getJedis();
			values = jedis.smembers(setKey);
			_close(jedis);
		}
		return values;
	}
	
	@Override
	public boolean inSet(String setKey, String setValue) {
		boolean isExist = false;
		if(setKey != null && setValue != null) {
			Jedis jedis = _getJedis();
			isExist = jedis.sismember(setKey, setValue);
			_close(jedis);
		}
		return isExist;
	}
	
	@Override
	public long getSetSize(String setKey) {
		long size = 0;
		if(setKey != null) {
			Jedis jedis = _getJedis();
			size = jedis.scard(setKey);
			_close(jedis);
		}
		return size;
	}
	
	@Override
	public long delSetVals(String setKey, String... setValues) {
		long num = 0;
		if(setKey != null && setValues != null) {
			Jedis jedis = _getJedis();
			num = jedis.srem(setKey, setValues);
			_close(jedis);
		}
		return num;
	}
	
}
