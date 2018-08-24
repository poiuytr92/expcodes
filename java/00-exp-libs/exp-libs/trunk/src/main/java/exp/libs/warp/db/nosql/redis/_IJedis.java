package exp.libs.warp.db.nosql.redis;

interface _IJedis {

	/**
	 * 断开Redis连接
	 */
	public void destory();
	
	/**
	 * 清空Redis库中所有数据(此方法在集群模式下无效)
	 * @param jedis redis连接对象
	 * @return true:清空成功; false:清空失败
	 */
	public boolean clearAll();
	
	/**
	 * 检查Redis库里面是否存在某个键值
	 * @param jedis redis连接对象
	 * @param key 被检查的键值
	 * @return true:存在; false:不存在
	 */
	public boolean existKey(String key);
	
	
}
