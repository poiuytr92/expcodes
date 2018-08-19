package exp.fw.zk.dy_notify;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 用来模拟服务器的动态上线下线 
 * 总体思路就是服务器上线就上 zookeeper 集群创建一个临时节点，
 * 然后监听了该数据节 点的个数变化的客户端都收到通知下线，
 * 则该临时节点自动删除，监听了该数据节点的个数变化的客户端也都收到通知
 */
public class DistributeServer {
	
	public static void main(String[] args) throws Exception {
		DistributeServer distributeServer = new DistributeServer();
		distributeServer.getZookeeperConnect();
		distributeServer.registeServer("hadoop03");
		Thread.sleep(Long.MAX_VALUE);	// 避免服务端主线程断开
	}
	
	
	private static final String connectStr = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
	private static final int sessionTimeout = 4000;
	private static final String PARENT_NODE = "/server";
	static ZooKeeper zk = null;

	/**
	 * 拿到 zookeeper 进群的链接
	 */
	public void getZookeeperConnect() throws Exception {
		zk = new ZooKeeper(connectStr, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
			}
		});
	}

	/**
	 * 服务器上线就注册，掉线就自动删除，所以创建的是临时顺序节点
	 */
	public void registeServer(String hostname) throws Exception {
		Stat exists = zk.exists(PARENT_NODE, false);
		if (exists == null) {
			zk.create(PARENT_NODE, "server_parent_node".getBytes(),
					Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		zk.create(PARENT_NODE + "/" + hostname, hostname.getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);	// 非持久化节点
		System.out.println(hostname + " is online, start working......");
	}
}
