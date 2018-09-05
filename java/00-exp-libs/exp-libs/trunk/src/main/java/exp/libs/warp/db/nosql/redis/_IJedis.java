package exp.libs.warp.db.nosql.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <PRE>
 * Redis连接接口
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
interface _IJedis {

	/**
	 * 测试Redis连接是否有效(集群模式不支持此操作)
	 * @return true:连接成功; false:连接失败
	 */
	public boolean isVaild();
	
	/**
	 * <pre>
	 * 设置是否自动提交.
	 * 在使用连接池的情况下, redis的操作默认均为短连接.
	 * 此方法可临时切换操作模式为长连接/短连接.
	 * 在调用{@link #commit}方法后恢复为短连接模式.
	 * -----------------
	 * 此方法非多线程安全，集群模式不支持此操作
	 * </pre>
	 * @param autoCommit true:自动提交; false:手动提交(需调用{@link #commit}方法)
	 */
	public void setAutoCommit(boolean autoCommit);
	
	/**
	 * <pre>
	 * 在使用连接池的情况下, redis的操作默认均为短连接.
	 * 此方法可临时切换操作模式为长连接, 在调用{@link #commit}方法后恢复为短连接模式.
	 * -----------------
	 * 此方法非多线程安全，集群模式不支持此操作
	 * </pre>
	 */
	public void closeAutoCommit();
	
	/**
	 * 把redis操作模式切换为默认的短连接模式
	 * -----------------
	 * 此方法非多线程安全，集群模式不支持此操作
	 */
	public void commit();
	
	/**
	 * 断开Redis连接
	 */
	public void destory();
	
	/**
	 * 清空Redis库中所有数据(集群模式不支持此操作)
	 * @return true:清空成功; false:清空失败
	 */
	public boolean clearAll();
	
	/**
	 * 检查Redis库里面是否存在某个键值
	 * @param redisKey 被检查的键值
	 * @return true:存在; false:不存在
	 */
	public boolean existKey(String redisKey);
	
	/**
	 * 删除Redis中的若干个键（及其对应的内容）
	 * @param redisKeys 指定的键集
	 * @return 删除成功的个数
	 */
	public long delKeys(String... redisKeys);
	
	/**
	 * 新增一个键值对
	 * @param redisKey 新的键
	 * @param value 新的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addKV(String redisKey, String value);
	
	/**
	 * 在已有的键key的原值的末尾附加value（仅针对键值对使用）
	 * @param redisKey 已有/新的键
	 * @param value 附加的值
	 * @return 附加值后，该键上最新的值的总长度
	 */
	public long appendKV(String redisKey, String value);
	
	/**
	 * 获取指定键的值
	 * @param redisKey 指定的键
	 * @return 对应的值（若不存在键则返回null）
	 */
	public String getVal(String redisKey);
	
	/**
	 * <pre>
	 * 新增一个对象（该对象须实现Serializable接口）。
	 * 该对象会以序列化形式存储到Redis。
	 * </pre>
	 * @param redisKey 指定的键
	 * @param object 新增的对象（须实现Serializable接口）
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addObj(String redisKey, Serializable object);
	
	/**
	 * <pre>
	 * 获取指定键的对象。
	 * 该对象会从Redis反序列化。
	 * </pre>
	 * @param redisKey 指定的键
	 * @return 反序列化的对象（若失败则返回null）
	 */
	public Object getObj(String redisKey);
	
	/**
	 * 新增一个 键->哈希表.
	 * 	若已存在同键的哈希表，则两个哈希表合并, 表内同键的值会被覆盖.
	 * @param redisKey 哈希表的键
	 * @param map 哈希表
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addMap(String redisKey, Map<String, String> map);
	
	/**
	 * 取出完整的一个哈希表
	 * @param redisKey 哈希表的键
	 * @return 哈希表（若不存在则返回空表，不会返回null）
	 */
	public Map<String, String> getMap(String redisKey);
	
	/**
	 * 新增一个 键->哈希表.
	 * 	若已存在同键的哈希表，则两个哈希表合并, 表内同键的值会被覆盖.
	 * @param redisKey 哈希表的键
	 * @param map 哈希表（其值须实现Serializable接口）
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addObjMap(String redisKey, Map<String, Serializable> map);
	
	/**
	 * 取出完整的一个哈希表（反序列化对象）
	 * @param redisKey 哈希表的键
	 * @return 哈希表（若不存在则返回空表，不会返回null）
	 */
	public Map<String, Object> getObjMap(String redisKey);
	
	/**
	 * 新增一个 键值对 到 指定哈希表
	 * @param redisKey 哈希表的键
	 * @param key 新增到哈希表内的键
	 * @param value 新增到哈希表内的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addToMap(String redisKey, String key, String value);
	
	/**
	 * 新增一个 键值对像 到 指定哈希表
	 * @param redisKey 哈希表的键
	 * @param key 新增到哈希表内的键
	 * @param object 新增到哈希表内的值对象（须实现Serializable接口）
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addToMap(String redisKey, String key, Serializable object);
	
	/**
	 * 获取某个哈希表中的某个键的值
	 * @param redisKey 哈希表的键
	 * @param key 哈希表中的某个键
	 * @return 哈希表中对应的值（若不存在返回null）
	 */
	public String getMapVal(String redisKey, String key);
	
	/**
	 * 获取某个哈希表中的若干个键的值
	 * @param redisKey 哈希表的键
	 * @param keys 哈希表中的一些键
	 * @return 哈希表中对应的一些值
	 */
	public List<String> getMapVals(String redisKey, String... keys);
	
	/**
	 * 获取哈希表中所有值
	 * @param redisKey 哈希表的键
	 * @return 若不存在该哈希表或哈希表为空，则返回空集（不会返回null）
	 */
	public List<String> getMapVals(String redisKey);
	
	/**
	 * 获取某个哈希表中的某个键的值对象（反序列化对象）
	 * @param redisKey 哈希表的键
	 * @param keys 哈希表中的某个键
	 * @return 哈希表中对应的值对象（反序列化对象，若不存在返回null）
	 */
	public Object getMapObj(String redisKey, String key);
	
	/**
	 * 获取某个哈希表中的若干个键的值对象（反序列化对象）
	 * @param redisKey 哈希表的键
	 * @param keys 哈希表中的一些键
	 * @return 哈希表中对应的一些值对象（反序列化对象）
	 */
	public List<Object> getMapObjs(String redisKey, String... keys);
	
	/**
	 * 获取哈希表中所有值对象（反序列化对象）
	 * @param redisKey 哈希表的键
	 * @return 若不存在该哈希表或哈希表为空，则返回空集（不会返回null）
	 */
	public List<Object> getMapObjs(String redisKey);
	
	/**
	 * 检查某个哈希表中是否存在某个键
	 * @param redisKey 哈希表的键
	 * @param key 哈希表中的某个键
	 * @return true:存在; false:不存在
	 */
	public boolean existMapKey(String redisKey, String key);
	
	/**
	 * 获取哈希表中所有键
	 * @param redisKey 哈希表的键
	 * @return 若不存在该哈希表或哈希表为空，则返回空集（不会返回null）
	 */
	public Set<String> getMapKeys(String redisKey);
	
	/**
	 * 删除某个哈希表中的若干个键（及其对应的值）
	 * @param redisKey 哈希表的键
	 * @param keys 哈希表中的一些键
	 * @return 删除成功的个数
	 */
	public long delMapKeys(String redisKey, String... keys);
	
	/**
	 * 获取哈希表的大小
	 * @param redisKey 哈希表的键
	 * @return 哈希表的大小
	 */
	public long getMapSize(String redisKey);
	
	/**
	 * 添加一些值到列表
	 * @param redisKey 列表的键
	 * @param values 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToList(String redisKey, String... values);
	
	/**
	 * 添加一些值到列表头部
	 * @param redisKey 列表的键
	 * @param values 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToListHead(String redisKey, String... values);
	
	/**
	 * 添加一些值到列表尾部
	 * @param redisKey 列表的键
	 * @param values 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToListTail(String redisKey, String... values);
	
	/**
	 * 获取列表中的所有值
	 * @param redisKey 列表的键
	 * @return 列表中的所有值
	 */
	public List<String> getListVals(String redisKey);
	
	/**
	 * 添加一些值到集合
	 * @param redisKey 集合的键
	 * @param values 添加的值
	 * @return 成功添加到该集合的值个数
	 */
	public long addToSet(String redisKey, String... values);
	
	/**
	 * 获取集合中的值
	 * @param redisKey 集合的键
	 * @return 集合中的值
	 */
	public Set<String> getSetVals(String redisKey);
	
	/**
	 * 检测某个值是否在指定集合中
	 * @param redisKey 集合的键
	 * @param value 被检测的值
	 * @return true:在集合中; false:不在集合中
	 */
	public boolean inSet(String redisKey, String value);
	
	/**
	 * 获取集合的大小
	 * @param redisKey 集合的键
	 * @return 集合的大小
	 */
	public long getSetSize(String redisKey);
	
	/**
	 * 删除集合中的一些值
	 * @param redisKey 集合的键
	 * @param values 被删除的值
	 * @return 成功从改集合中删除的值个数
	 */
	public long delSetVals(String redisKey, String... values);
	
}
