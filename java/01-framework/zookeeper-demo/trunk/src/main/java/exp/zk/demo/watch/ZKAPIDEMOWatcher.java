package exp.zk.demo.watch;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

public class ZKAPIDEMOWatcher {

	// 获取zookeeper连接时所需要的服务器连接信息，格式为主机名：端口号
	private static final String ConnectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";

	// 请求了解的会话超时时长
	private static final int SessionTimeout = 5000;

	private static ZooKeeper zk = null;
	static Watcher w = null;
	static Watcher watcher = null;

	public static void main(String[] args) throws Exception {

		watcher = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.getPath() + "\t-----"
						+ event.getType());
				List<String> children;
				try {
					if (event.getPath().equals("/spark")
							&& event.getType() == EventType.NodeChildrenChanged) {
						// zk.setData("/spark", "spark-sql".getBytes(), -1);
						System.out.println("数据更改成功 ~~~~~~~~~~~~~~~~~~");
						children = zk.getChildren("/spark", watcher);
					}
					if (event.getPath().equals("/spark")
							&& event.getType() == EventType.NodeDataChanged) {
						// zk.setData("/spark", "spark-sql".getBytes(), -1);
						System.out.println("数据更改成功 ￥##########");
						zk.getData("/spark", watcher, null);
					}
					if (event.getPath().equals("/mx")
							&& event.getType() == EventType.NodeChildrenChanged) {
						// zk.setData("/mx", "spark-sql".getBytes(), -1);
						System.out.println("数据更改成功  ---------");
						children = zk.getChildren("/mx", watcher);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		zk = new ZooKeeper(ConnectString, SessionTimeout, watcher);

		zk.getData("/spark", true, null);
		zk.getChildren("/spark", true);
		zk.getChildren("/mx", true);
		zk.exists("/spark", true);

		// 自定义循环自定义
		w = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					zk.getData("/hive", w, null);
					System.out.println("hive shuju bianhua ");
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		zk.getData("/hive", w, null);

		// zk.setData(path, data, version);

		// 表示给znode /ghgj 的数据变化事件加了监听
		// 第二个参数使用true还是false的意义就是是否使用拿zookeeper链接时指定的监听器
		// zk.getData("/ghgj", true, null);
		// zk.setData("/ghgj", "hadoophdfs2".getBytes(), -1);

		/*
		 * zk.getData("/sqoop", new Watcher(){
		 * 
		 * @Override public void process(WatchedEvent event) {
		 * System.out.println("**************");
		 * System.out.println(event.getPath()+"\t"+event.getType()); } }, null);
		 */
		// zk.setData("/sqoop", "hadoophdfs3".getBytes(), -1); //
		// NodeDataChanged
		// zk.delete("/sqoop", -1); // NodeDeleted
		// zk.create("/sqoop/s1", "s1".getBytes(), Ids.OPEN_ACL_UNSAFE,
		// CreateMode.PERSISTENT);

		// zk.exists("/hivehive", new Watcher(){
		// @Override
		// public void process(WatchedEvent event) {
		// System.out.println("**************");
		// System.out.println(event.getPath()+"\t"+event.getType());
		// }
		// });

		// create方法
		// zk.create("/hivehive", "hivehive".getBytes(), Ids.OPEN_ACL_UNSAFE,
		// CreateMode.PERSISTENT);
		// zk.delete("/hivehive", -1);
		// zk.setData("/hivehive", "hadoop".getBytes(), -1);

		// 需求：有一个父节点叫做/spark，数据是spark,当父节点/spark下有三个子节点，
		// 那么就把该父节点的数据改成spark-sql
		// zk.create("/spark", "spark".getBytes(), Ids.OPEN_ACL_UNSAFE,
		// CreateMode.PERSISTENT);

		/*
		 * zk.getChildren("/spark", new Watcher() {
		 * 
		 * @Override public void process(WatchedEvent event) { try {
		 * List<String> children = zk.getChildren("/spark", true);
		 * if(children.size() == 3){
		 * 
		 * } zk.setData("/spark", "spark-sql".getBytes(), -1);
		 * System.out.println("数据更改成功"); } catch (KeeperException |
		 * InterruptedException e) { e.printStackTrace(); } } });
		 */

		Thread.sleep(Long.MAX_VALUE);

		zk.close();
	}
}
