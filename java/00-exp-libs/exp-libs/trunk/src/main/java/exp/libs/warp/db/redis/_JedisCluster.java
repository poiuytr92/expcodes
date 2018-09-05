package exp.libs.warp.db.redis;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.ObjUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Redis集群连接（仅适用于Redis集群模式）
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _JedisCluster extends JedisCluster implements _IJedis {

	/** 默认字符集编码 */
	private final static String CHARSET = Charset.UTF8;
	
	/** Redis部分接口的返回值 */
	private final static String OK = "OK";

	protected _JedisCluster(HostAndPort... clusterNodes) {
		this(null, DEFAULT_TIMEOUT, null, clusterNodes);
	}
	
	protected _JedisCluster(int timeout, HostAndPort... clusterNodes) {
		this(null, timeout, null, clusterNodes);
	}
	
	protected _JedisCluster(String password, HostAndPort... clusterNodes) {
		this(null, DEFAULT_TIMEOUT, password, clusterNodes);
	}
	
	protected _JedisCluster(int timeout, String password, 
			HostAndPort... clusterNodes) {
		this(null, timeout, password, clusterNodes);
	}
	
	protected _JedisCluster(GenericObjectPoolConfig poolConfig, 
			HostAndPort... clusterNodes) {
		this(poolConfig, DEFAULT_TIMEOUT, null, clusterNodes);
	}
	
	protected _JedisCluster(GenericObjectPoolConfig poolConfig, 
			int timeout, HostAndPort... clusterNodes) {
		this(poolConfig, timeout, null, clusterNodes);
	}
	
	protected _JedisCluster(GenericObjectPoolConfig poolConfig, 
			String password, HostAndPort... clusterNodes) {
		this(poolConfig, DEFAULT_TIMEOUT, password, clusterNodes);
	}
	
	@SuppressWarnings("unchecked")
	protected _JedisCluster(GenericObjectPoolConfig poolConfig, 
			int timeout, String password, HostAndPort... clusterNodes) {
		super(new HashSet<HostAndPort>(ListUtils.asList(clusterNodes)), timeout, 
				timeout, DEFAULT_MAX_REDIRECTIONS, 
				(StrUtils.isEmpty(password) ? null : password), 
				(poolConfig == null ? new GenericObjectPoolConfig() : poolConfig));
	}

	/**
	 * 对Redis键统一转码，使得Jedis的 String接口 和 byte[]接口 所产生的键值最终一致。
	 * (若不转码, 在redis编码与程序编码不一致的情况下, 即使键值相同, 
	 * 	但使用String接口与byte[]接口存储到Redis的是两个不同的哈希表)
	 * @param redisKey redis键
	 * @return 统一转码后的redis键
	 */
	private String _transcode(String redisKey) {
		return CharsetUtils.transcode(redisKey, CHARSET);
	}
	
	/**
	 * 对Redis键统一转码，使得Jedis的 String接口 和 byte[]接口 所产生的键值最终一致。
	 * (若不转码, 在redis编码与程序编码不一致的情况下, 即使键值相同, 
	 * 	但使用String接口与byte[]接口存储到Redis的是两个不同的哈希表)
	 * @param redisKey redis键
	 * @return 统一转码后的redis键(字节数组)
	 */
	private byte[] _transbyte(String redisKey) {
		return CharsetUtils.toBytes(redisKey, CHARSET);
	}
	
	@Deprecated
	@Override
	public boolean isVaild() {
		return false;	// 集群模式不支持此操作
	}
	
	@Deprecated
	@Override
	public void setAutoCommit(boolean autoCommit) {
		// Undo 集群模式不支持此操作
	}

	@Override
	public void closeAutoCommit() {
		// Undo 集群模式不支持此操作
	}

	@Deprecated
	@Override
	public void commit() {
		// Undo 集群模式不支持此操作
	}
	
	@Override
	public void destory() {
		try {
			super.close();
		} catch (IOException e) {}
	}
	
	@Override
	public boolean clearAll() {
		return false;	// 集群模式不支持此操作
	}

	@Override
	public boolean existKey(String redisKey) {
		boolean isExist = false;
		if(redisKey != null) {
			isExist = super.exists(_transcode(redisKey));
		}
		return isExist;
	}

	@Override
	public long delKeys(String... redisKeys) {
		long num = 0;
		if(redisKeys != null) {
			for(String redisKey : redisKeys) {
				if(redisKey == null) {
					continue;
				}
				num += super.del(_transcode(redisKey));
			}
		}
		return num;
	}

	@Override
	public boolean addVal(String redisKey, String value) {
		boolean isOk = false;
		if(redisKey != null && value != null) {
			isOk = OK.equalsIgnoreCase(super.set(_transcode(redisKey), value));
		}
		return isOk;
	}

	@Override
	public long appendVal(String redisKey, String value) {
		long len = -1;
		if(redisKey != null && value != null) {
			len = super.append(_transcode(redisKey), value);
		}
		return len;
	}

	@Override
	public String getVal(String redisKey) {
		String value = "";
		if(redisKey != null) {
			value = super.get(_transcode(redisKey));
		}
		return value;
	}

	@Override
	public boolean addObj(String redisKey, Serializable object) {
		boolean isOk = false;
		if(redisKey != null && object != null) {
			isOk = OK.equalsIgnoreCase(super.set(
					_transbyte(redisKey), 
					ObjUtils.toSerializable(object))
			);
		}
		return isOk;
	}

	@Override
	public Object getObj(String redisKey) {
		Object object = null;
		if(redisKey != null) {
			object = ObjUtils.unSerializable(super.get(_transbyte(redisKey)));
		}
		return object;
	}

	@Override
	public boolean addMap(String redisKey, Map<String, String> map) {
		boolean isOk = false;
		if(redisKey != null && map != null) {
			isOk = OK.equalsIgnoreCase(super.hmset(_transcode(redisKey), map));
		}
		return isOk;
	}
	
	@Override
	public Map<String, String> getMap(String redisKey) {
		Map<String, String> map = null;
		if(redisKey != null) {
			map = super.hgetAll(_transcode(redisKey));
		}
		return (map == null ? new HashMap<String, String>() : map);
	}

	@Override
	public boolean addObjMap(String redisKey, Map<String, Serializable> map) {
		boolean isOk = false;
		if(redisKey != null && map != null) {
			isOk = true;
			Iterator<String> keys = map.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				Serializable object = map.get(key);
				isOk &= super.hset(_transbyte(redisKey), _transbyte(key), 
						ObjUtils.toSerializable(object)) >= 0;
			}
		}
		return isOk;
	}
	
	@Override
	public Map<String, Object> getObjMap(String redisKey) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(redisKey != null) {
			Map<byte[], byte[]> byteMap = super.hgetAll(_transbyte(redisKey));
			Iterator<byte[]> keys = byteMap.keySet().iterator();
			while(keys.hasNext()) {
				byte[] key = keys.next();
				byte[] val = byteMap.get(key);
				map.put(CharsetUtils.toStr(key, CHARSET), ObjUtils.unSerializable(val));
			}
		}
		return map;
	}

	@Override
	public boolean addToMap(String redisKey, String key, String value) {
		boolean isOk = false;
		if(redisKey != null && key != null && value != null) {
			isOk = super.hset(_transcode(redisKey), key, value) >= 0;
		}
		return isOk;
	}

	@Override
	public boolean addToMap(String redisKey, String key, Serializable object) {
		boolean isOk = false;
		if(redisKey != null && key != null && object != null) {
			isOk = super.hset(_transbyte(redisKey), _transbyte(key), 
					ObjUtils.toSerializable(object)) >= 0;
		}
		return isOk;
	}

	@Override
	public String getMapVal(String redisKey, String key) {
		String value = null;
		if(redisKey != null && key != null) {
			List<String> values = super.hmget(_transcode(redisKey), key);
			if(ListUtils.isNotEmpty(values)) {
				value = values.get(0);
			}
		}
		return value;
	}

	@Override
	public List<String> getMapVals(String redisKey, String... keys) {
		List<String> values = null;
		if(redisKey != null && keys != null) {
			values = super.hmget(_transcode(redisKey), keys);
		}
		return (values == null ? new LinkedList<String>() : values);
	}

	@Override
	public List<String> getMapAllVals(String redisKey) {
		List<String> values = null;
		if(redisKey != null) {
			values = super.hvals(_transcode(redisKey));
		}
		return (values == null ? new LinkedList<String>() : values);
	}
	
	@Override
	public Object getMapObj(String redisKey, String key) {
		Object value = null;
		if(redisKey != null && key != null) {
			List<byte[]> values = super.hmget(
					_transbyte(redisKey), _transbyte(key));
			if(ListUtils.isNotEmpty(values)) {
				value = ObjUtils.unSerializable(values.get(0));
			}
		}
		return value;
	}

	@Override
	public List<Object> getMapObjs(String redisKey, String... keys) {
		List<Object> values = new LinkedList<Object>();
		if(redisKey != null && keys != null) {
			byte[] byteKey = _transbyte(redisKey);
			for(String key : keys) {
				List<byte[]> byteVals = super.hmget(byteKey, _transbyte(key));
				if(ListUtils.isEmpty(byteVals)) {
					values.add(null);
					
				} else {
					values.add(ObjUtils.unSerializable(byteVals.get(0)));
				}
			}
		}
		return (values == null ? new LinkedList<Object>() : values);
	}

	@Override
	public List<Object> getMapAllObjs(String redisKey) {
		List<Object> values = new LinkedList<Object>();
		if(redisKey != null) {
			byte[] byteKey =_transbyte(redisKey);
			Collection<byte[]> byteVals = super.hvals(byteKey);
			for(byte[] byteVal : byteVals) {
				values.add(ObjUtils.unSerializable(byteVal));
			}
		}
		return (values == null ? new LinkedList<Object>() : values);
	}

	@Override
	public boolean existMapKey(String redisKey, String key) {
		boolean isExist = false;
		if(redisKey != null && key != null) {
			isExist = super.hexists(_transcode(redisKey), key);
		}
		return isExist;
	}
	
	@Override
	public Set<String> getMapKeys(String redisKey) {
		Set<String> keys = new HashSet<String>();
		if(redisKey != null) {
			keys = super.hkeys(_transcode(redisKey));
		}
		return (keys == null ? new HashSet<String>() : keys);
	}
	
	@Override
	public long delMapKeys(String redisKey, String... keys) {
		long num = 0;
		if(redisKey != null && keys != null) {
			num = super.hdel(_transcode(redisKey), keys);
		}
		return num;
	}
	
	@Override
	public long getMapSize(String redisKey) {
		long size = 0L;
		if(redisKey != null) {
			size = super.hlen(_transcode(redisKey)); 
		}
		return size;
	}

	@Override
	public long addToList(String redisKey, String... values) {
		return addToListTail(redisKey, values);
	}

	@Override
	public long addToListHead(String redisKey, String... values) {
		long num = 0;
		if(redisKey != null && values != null) {
			redisKey = _transcode(redisKey);
			for(String value : values) {
				if(value == null) {
					continue;
				}
				num = super.lpush(redisKey, value);
			}
		}
		return num;
	}

	@Override
	public long addToListTail(String redisKey, String... values) {
		long num = 0;
		if(redisKey != null && values != null) {
			redisKey = _transcode(redisKey);
			for(String value : values) {
				if(value == null) {
					continue;
				}
				num = super.rpush(redisKey, value);
			}
		}
		return num;
	}

	@Override
	public List<String> getListAllVals(String redisKey) {
		List<String> values = new LinkedList<String>();
		if(redisKey != null) {
			values = super.lrange(_transcode(redisKey), 0, -1);
		}
		return values;
	}

	@Override
	public long addToSet(String redisKey, String... values) {
		long addNum = 0;
		if(redisKey != null && values != null) {
			addNum = super.sadd(_transcode(redisKey), values);
		}
		return addNum;
	}

	@Override
	public Set<String> getSetVals(String redisKey) {
		Set<String> values = new HashSet<String>();
		if(redisKey != null) {
			values = super.smembers(_transcode(redisKey));
		}
		return values;
	}

	@Override
	public boolean inSet(String redisKey, String value) {
		boolean isExist = false;
		if(redisKey != null && value != null) {
			isExist = super.sismember(_transcode(redisKey), value);
		}
		return isExist;
	}

	@Override
	public long getSetSize(String redisKey) {
		long size = 0;
		if(redisKey != null) {
			size = super.scard(_transcode(redisKey));
		}
		return size;
	}

	@Override
	public long delSetVals(String redisKey, String... values) {
		long num = 0;
		if(redisKey != null && values != null) {
			num = super.srem(_transcode(redisKey), values);
		}
		return num;
	}
	
}
