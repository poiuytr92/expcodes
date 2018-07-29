package exp.fw.zk.dy_notify;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 用来模拟用户端的操作：
 * 连上 zookeeper 进群，实时获取服务器动态上下线的节点信息 
 * 总体思路就是每次该 server节点下有增加或者减少节点数，
 * 我就打印出来该 server 节点 下的所有节点
 */
public class DistributeClient {

	public static void main(String[] args) throws Exception {
		DistributeClient dc = new DistributeClient();
		dc.getZookeeperConnect();
		Thread.sleep(Long.MAX_VALUE);	// 避免客户顿主线程退出
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
				try {
					// 获取父节点 server 节点下所有子节点，即是所有正上线服务的服务器节点
					List<String> children = zk.getChildren(PARENT_NODE, true);
					List<String> servers = new ArrayList<String>();
					for (String child : children) {
						// 取出每个节点的数据，放入到 list 里
						String server = new String(zk.getData(PARENT_NODE + "/"
								+ child, false, null), "UTF-8");
						servers.add(server);
					}
					// 打印 list 里面的元素
					System.out.println(servers);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("Client is online, start Working......");
	}
}
