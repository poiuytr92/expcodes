package exp.fw.zk.lock;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;


/**
 * <PRE>
 * 【场景】分布式共享锁：多个客户端，需要同时访问同一个资源，但同一时间只允许一个客户端进行访问。 
 * 【思路】多个客户端都去父 znode 下写入一个子znode，能写入成功的去执行访问， 写入不成功的等待
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MyDistributeLock {
	
	public static void main(String[] args) throws Exception {
		MyDistributeLock mdc = new MyDistributeLock();
		// 1、拿到 zookeeper 链接
		mdc.getZookeeperConnect();
		// 2、查看父节点是否存在，不存在则创建
		Stat exists = zk.exists(PARENT_NODE, false);
		if (exists == null) {
			zk.create(PARENT_NODE, PARENT_NODE.getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
		}
		// 3、监听父节点
		zk.getChildren(PARENT_NODE, true);
		// 4、往父节点下注册节点，注册临时节点，好处就是，当宕机或者断开链接时该节点自动删除
		currentPath = zk.create(PARENT_NODE + SUB_NODE, SUB_NODE.getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		// 5、关闭 zk 链接
		Thread.sleep(Long.MAX_VALUE);
		zk.close();
	}
	
	private static final String connectStr = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
	private static final int sessionTimeout = 4000;
	private static final String PARENT_NODE = "/parent_locks";
	private static final String SUB_NODE = "/sub_client";
	static ZooKeeper zk = null;
	private static String currentPath = "";

	/**
	 * 拿到 zookeeper 集群的链接
	 */
	public void getZookeeperConnect() throws Exception {
		zk = new ZooKeeper(connectStr, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				
				// 当节点发生变化时，判断当前最小的节点是不是自己创建的，若是则说明获得锁并可以执行业务操作
				
				// 匹配看是不是子节点变化，并且监听的路径也要对
				if (event.getType() == EventType.NodeChildrenChanged
						&& event.getPath().equals(PARENT_NODE)) {
					try {
						// 获取父节点的所有子节点, 并继续监听
						List<String> childrenNodes = zk.getChildren(
								PARENT_NODE, true);
						
						
						// 匹配当前创建的 znode 是不是最小的 znode
						Collections.sort(childrenNodes);
						if ((PARENT_NODE + "/" + childrenNodes.get(0))
								.equals(currentPath)) {
							// 处理业务
							handleBusiness(currentPath);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void handleBusiness(String create) throws Exception {
		System.out.println(create + " is working......");
		Thread.sleep(new Random().nextInt(4000));	// 模拟业务处理
		zk.delete(create, -1);	// 释放锁
		System.out.println(create + " is done ......");
		
		// 重新获取锁
		currentPath = zk.create(PARENT_NODE + SUB_NODE, SUB_NODE.getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
}
