package exp.zk.demo.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

/**
 * <PRE>
 * 分布式共享锁：业务处理器接口
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface Handler {

	/**
	 * 业务处理逻辑
	 * @param zk 当前连接到的zookeeper集群
	 * @param event 所监听的锁事件
	 * @param keepLockNode 当前持有的锁节点
	 */
	public void handle(ZooKeeper zk, WatchedEvent event, String keepLockNode);
	
}
