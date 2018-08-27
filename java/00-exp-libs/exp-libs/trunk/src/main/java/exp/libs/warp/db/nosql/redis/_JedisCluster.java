package exp.libs.warp.db.nosql.redis;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import exp.libs.envm.Charset;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.ObjUtils;

/**
 * <PRE>
 * Redis集群连接（适用于Redis集群模式）
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
				timeout, DEFAULT_MAX_REDIRECTIONS, password, 
				(poolConfig == null ? new GenericObjectPoolConfig() : poolConfig));
	}

	@Deprecated
	@Override
	public boolean isVaild() {
		return false;	// 集群模式不支持此操作
	}
	
	@Deprecated
	@Override
	public void autoCommit(boolean autoCommit) {
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
	public boolean existKey(String key) {
		return super.exists(key);
	}

	@Override
	public long delKeys(String... keys) {
		long size = 0;
		if(keys != null) {
			size = super.del(keys);
		}
		return size;
	}
	
	@Override
	public boolean addKV(String key, String value) {
		boolean isOk = false;
		if(key != null && value != null) {
			isOk = OK.equalsIgnoreCase(super.set(key, value));
		}
		return isOk;
	}
	
	@Override
	public long appendKV(String key, String value) {
		long len = -1;
		if(key != null && value != null) {
			len = super.append(key, value);
		}
		return len;
	}
	
	@Override
	public String getVal(String key) {
		String value = "";
		if(key != null) {
			value = super.get(key);
		}
		return value;
	}
	
	@Override
	public boolean addObj(String key, Serializable object) {
		boolean isOk = false;
		if(key != null && object != null) {
			try {
				isOk = OK.equalsIgnoreCase(super.set(
						key.getBytes(CHARSET), ObjUtils.toSerializable(object)));
			} catch (UnsupportedEncodingException e) {}
		}
		return isOk;
	}
	
	@Override
	public Object getObj(String key) {
		Object object = null;
		if(key != null) {
			try {
				object = ObjUtils.unSerializable(
						super.get(key.getBytes(CHARSET)));
			} catch (UnsupportedEncodingException e) {}
		}
		return object;
	}
	
	@Override
	public boolean addMap(String key, Map<String, String> map) {
		boolean isOk = false;
		if(key != null && map != null) {
			isOk = OK.equalsIgnoreCase(super.hmset(key, map));
		}
		return isOk;
	}
	
	@Override
	public boolean addToMap(String key, String mapKey, String mapValue) {
		boolean isOk = false;
		if(key != null && mapKey != null && mapValue != null) {
			isOk = super.hset(key, mapKey, mapValue) >= 0;
		}
		return isOk;
	}
	
	@Override
	public boolean addToMap(String key, String mapKey, Serializable mapValue) {
		boolean isOk = false;
		if(key != null && mapKey != null && mapValue != null) {
			try {
				isOk = super.hset(key.getBytes(CHARSET), 
						mapKey.getBytes(CHARSET), 
						ObjUtils.toSerializable(mapValue)) >= 0;
			} catch (UnsupportedEncodingException e) {}
		}
		return isOk;
	}
	
	@Override
	public String getMapVal(String mapKey, String inMapKey) {
		String value = null;
		if(mapKey != null && inMapKey != null) {
			List<String> values = super.hmget(mapKey, inMapKey);
			if(ListUtils.isNotEmpty(values)) {
				value = values.get(0);
			}
		}
		return value;
	}
	
	@Override
	public List<String> getMapVals(String mapKey, String... inMapKeys) {
		List<String> values = null;
		if(mapKey != null && inMapKeys != null) {
			values = super.hmget(mapKey, inMapKeys);
		}
		return (values == null ? new LinkedList<String>() : values);
	}
	
	@Override
	public Object getMapObj(String mapKey, String inMapKey) {
		Object value = null;
		if(mapKey != null && inMapKey != null) {
			try {
				byte[] key = mapKey.getBytes(CHARSET);
				List<byte[]> byteVals = super.hmget(key, inMapKey.getBytes(CHARSET));
				if(ListUtils.isNotEmpty(byteVals)) {
					value = ObjUtils.unSerializable(byteVals.get(0));
				}
			} catch (UnsupportedEncodingException e) {}
		}
		return value;
	}
	
	@Override
	public List<Object> getMapObjs(String mapKey, String... inMapKeys) {
		List<Object> values = new LinkedList<Object>();
		if(mapKey != null && inMapKeys != null) {
			try {
				byte[] key = mapKey.getBytes(CHARSET);
				for(String inMapKey : inMapKeys) {
					List<byte[]> byteVals = super.hmget(key, inMapKey.getBytes(CHARSET));
					if(ListUtils.isEmpty(byteVals)) {
						values.add(null);
						
					} else {
						values.add(ObjUtils.unSerializable(byteVals.get(0)));
					}
				}
			} catch (UnsupportedEncodingException e) {}
		}
		return (values == null ? new LinkedList<Object>() : values);
	}
	
	@Override
	public long delMapKeys(String mapKey, String... inMapKeys) {
		long size = 0;
		if(mapKey != null && inMapKeys != null) {
			size = super.hdel(mapKey, inMapKeys);
		}
		return size;
	}
	
	@Override
	public long addToList(String listKey, String... listValues) {
		return addToListTail(listKey, listValues);
	}
	
	@Override
	public long addToListHead(String listKey, String... listValues) {
		long size = 0;
		if(listKey != null && listValues != null) {
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				size = super.lpush(listKey, value);
			}
		}
		return size;
	}
	
	@Override
	public long addToListTail(String listKey, String... listValues) {
		long size = 0;
		if(listKey != null && listValues != null) {
			for(String value : listValues) {
				if(value == null) {
					continue;
				}
				size = super.rpush(listKey, value);
			}
		}
		return size;
	}
	
	@Override
	public List<String> getListVals(String listKey) {
		List<String> values = new LinkedList<String>();
		if(listKey != null) {
			values = super.lrange(listKey, 0, -1);
		}
		return values;
	}
	
	@Override
	public long addToSet(String setKey, String... setValues) {
		long addNum = 0;
		if(setKey != null && setValues != null) {
			addNum = super.sadd(setKey, setValues);
		}
		return addNum;
	}
	
	@Override
	public Set<String> getSetVals(String setKey) {
		Set<String> values = new HashSet<String>();
		if(setKey != null) {
			values = super.smembers(setKey);
		}
		return values;
	}
	
	@Override
	public boolean inSet(String setKey, String setValue) {
		boolean isExist = false;
		if(setKey != null && setValue != null) {
			isExist = super.sismember(setKey, setValue);
		}
		return isExist;
	}
	
	@Override
	public long getSetSize(String setKey) {
		long size = 0;
		if(setKey != null) {
			size = super.scard(setKey);
		}
		return size;
	}
	
	@Override
	public long delSetVals(String setKey, String... setValues) {
		long size = 0;
		if(setKey != null && setValues != null) {
			size = super.srem(setKey, setValues);
		}
		return size;
	}
	
}