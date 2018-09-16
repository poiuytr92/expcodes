package exp.zk.demo.notify;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * <PRE>
 * 在分布式集群中扮演【服务器角色】的节点
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DistributeServer {
	
	private String name;
	
	private String zkConnStr;
	
	private int sessTimeout;
	
	private ZooKeeper zk;
	
	/**
	 * 构造函数
	 * @param name 服务器名称
	 * @param zkConnStr zookeeper集群连接串
	 * @param sessTimeout zookeeper会话超时(ms)
	 */
	public DistributeServer(String name, String zkConnStr, int sessTimeout) {
		this.name = (name == null ? "NULL" : name);
		this.zkConnStr = (zkConnStr == null ? "" : zkConnStr);
		this.sessTimeout = (sessTimeout < 0 ? 0 : sessTimeout);
	}
	
	/**
	 * 连接到 zookeeper 集群
	 */
	public boolean connZK() {
		boolean isOk = true;
		try {
			this.zk = new ZooKeeper(zkConnStr, sessTimeout, new Watcher() {

				@Override
				public void process(WatchedEvent arg0) {
					// UNDO 无需监听节点
				}
				
			});
			System.out.println("服务器 [" + name + "] 已连接到分布式集群");
			
		} catch (IOException e) {
			System.err.println("服务器 [" + name + "] 连接到分布式集群失败");
			e.printStackTrace();
			isOk = false;
		}
		return isOk;
	}
	
	/**
	 * 注册服务到【记录了整个集群中所有服务器信息】的节点
	 * @param NODE_SERVERS_INFO 【记录了整个集群中所有服务器信息】的节点
	 */
	public boolean registe(final String NODE_SERVERS_INFO) {
		boolean isOk = false;
		if(zk != null) {
			try {
				
				// 若【记录了整个集群中所有服务器信息】的节点不存在，则先创建之
				Stat exists = zk.exists(NODE_SERVERS_INFO, false);
				if (exists == null) {
					zk.create(NODE_SERVERS_INFO, 
							NODE_SERVERS_INFO.getBytes(),
							Ids.OPEN_ACL_UNSAFE, 
							CreateMode.PERSISTENT);	// 此信息节点持久化
				}
				
				// 在信息节点下方注册当前的服务器信息
				zk.create(NODE_SERVERS_INFO + "/" + name, 
						name.getBytes(),
						Ids.OPEN_ACL_UNSAFE, 
						CreateMode.EPHEMERAL_SEQUENTIAL);	// 当前服务器节点非持久化
				
				System.out.println("服务器 [" + name + "] 上线： 已注册服务到集群的信息节点 [" + NODE_SERVERS_INFO + "]");
				isOk = true;
				
			} catch(Exception e) {
				System.err.println("服务端 [" +  name + "] 注册服务失败");
				e.printStackTrace();
			}
		}
		return isOk;
	}
	
	/**
	 * <pre>
	 * 断开服务器与集群的连接.
	 * 此时之前注册到信息节点的临时节点会自动删除.
	 * </pre>
	 */
	public void close() {
		if(zk != null) {
			try {
				zk.close();
				zk = null;
				System.out.println("服务器 [" + name + "] 下线");
				
			} catch (Exception e) {
				System.err.println("服务器 [" + name + "] 下线失败");
				e.printStackTrace();
			}
		}
	}
	
}
