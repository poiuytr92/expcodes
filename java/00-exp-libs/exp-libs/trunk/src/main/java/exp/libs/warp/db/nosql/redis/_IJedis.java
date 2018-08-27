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
	 * (集群模式不支持此操作)
	 * 非集群且使用连接池的情况下, redis的操作默认均为短连接.
	 * 此方法可邻接切换操作模式为长连接, 在调用 @link{ commit() } 方法后恢复为短连接模式.
	 * </pre>
	 * @param autoCommit true:自动提交; false:手动提交(需调用 @link{ commit() } 方法)
	 */
	public void autoCommit(boolean autoCommit);
	
	/**
	 * 把redis操作模式切换为默认的短连接模式(集群模式不支持此操作)
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
	 * @param key 被检查的键值
	 * @return true:存在; false:不存在
	 */
	public boolean existKey(String key);
	
	/**
	 * 删除若干个键（及其对应的内容）
	 * @param keys 指定的键集
	 * @return 删除成功的个数
	 */
	public long delKeys(String... keys);
	
	/**
	 * 新增一个键值对
	 * @param key 新的键
	 * @param value 新的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addKV(String key, String value);
	
	/**
	 * 在已有的键key的原值的末尾附加value（仅针对键值对使用）
	 * @param key 已有/新的键
	 * @param value 附加的值
	 * @return 附加值后，该键上最新的值的总长度
	 */
	public long appendKV(String key, String value);
	
	/**
	 * 获取指定键的值
	 * @param key 指定的键
	 * @return 对应的值（若不存在键则返回null）
	 */
	public String getVal(String key);
	
	/**
	 * <pre>
	 * 新增一个对象（该对象须实现Serializable接口）。
	 * 该对象会以序列化形式存储到Redis。
	 * </pre>
	 * @param key 指定的键
	 * @param object 新增的对象（须实现Serializable接口）
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addObj(String key, Serializable object);
	
	/**
	 * <pre>
	 * 获取指定键的对象。
	 * 该对象会从Redis反序列化。
	 * </pre>
	 * @param key 指定的键
	 * @return 反序列化的对象（若失败则返回null）
	 */
	public Object getObj(String key);
	
	/**
	 * 新增一个 键->哈希表
	 * @param key 键值
	 * @param map 哈希表
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addMap(String key, Map<String, String> map);
	
	/**
	 * 新增一个 键值对 到 指定哈希表
	 * @param key 哈希表的键
	 * @param mapKey 新增到哈希表的键
	 * @param mapValue 新增到哈希表的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addToMap(String key, String mapKey, String mapValue);
	
	/**
	 * 新增一个 键值对 到 指定哈希表
	 * @param key 哈希表的键
	 * @param mapKey 新增到哈希表的键
	 * @param mapValue 新增到哈希表的值
	 * @return true:新增成功; false:新增失败
	 */
	public boolean addToMap(String key, String mapKey, Serializable mapValue);
	
	/**
	 * 获取某个哈希表中的某个键的值
	 * @param mapKey 哈希表的键
	 * @param inMapKey 哈希表中的某个键
	 * @return 哈希表中对应的值（若不存在返回null）
	 */
	public String getMapVal(String mapKey, String inMapKey);
	
	/**
	 * 获取某个哈希表中的若干个键的值
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 哈希表中对应的一些值
	 */
	public List<String> getMapVals(String mapKey, String... inMapKeys);
	
	/**
	 * 获取某个哈希表中的某个键的值对象（反序列化对象）
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的某个键
	 * @return 哈希表中对应的值对象（反序列化对象，若不存在返回null）
	 */
	public Object getMapObj(String mapKey, String inMapKey);
	
	/**
	 * 获取某个哈希表中的若干个键的值对象（反序列化对象）
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 哈希表中对应的一些值对象（反序列化对象）
	 */
	public List<Object> getMapObjs(String mapKey, String... inMapKeys);
	
	/**
	 * 删除某个哈希表中的若干个键（及其对应的值）
	 * @param mapKey 哈希表的键
	 * @param inMapKeys 哈希表中的一些键
	 * @return 删除成功的个数
	 */
	public long delMapKeys(String mapKey, String... inMapKeys);
	
	/**
	 * 添加一些值到列表
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToList(String listKey, String... listValues);
	
	/**
	 * 添加一些值到列表头部
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToListHead(String listKey, String... listValues);
	
	/**
	 * 添加一些值到列表尾部
	 * @param listKey 列表的键
	 * @param listValues 添加的值
	 * @return 添加后，该的队列的总长度
	 */
	public long addToListTail(String listKey, String... listValues);
	
	/**
	 * 获取列表中的所有值
	 * @param listKey 列表的键
	 * @return 列表中的所有值
	 */
	public List<String> getListVals(String listKey);
	
	/**
	 * 添加一些值到集合
	 * @param setKey 集合的键
	 * @param setValues 添加的值
	 * @return 成功添加到该集合的值个数
	 */
	public long addToSet(String setKey, String... setValues);
	
	/**
	 * 获取集合中的值
	 * @param setKey 集合的键
	 * @return 集合中的值
	 */
	public Set<String> getSetVals(String setKey);
	
	/**
	 * 检测某个值是否在指定集合中
	 * @param setKey 集合的键
	 * @param setValue 被检测的值
	 * @return true:在集合中; false:不在集合中
	 */
	public boolean inSet(String setKey, String setValue);
	
	/**
	 * 获取集合的大小
	 * @param setKey 集合的键
	 * @return 集合的大小
	 */
	public long getSetSize(String setKey);
	
	/**
	 * 删除集合中的一些值
	 * @param setKey 集合的键
	 * @param setValues 被删除的值
	 * @return 成功从改集合中删除的值个数
	 */
	public long delSetVals(String setKey, String... setValues);
	
}
