package exp.zk.demo.notify;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * <PRE>
 * 在分布式集群中扮演【客户端角色】的节点
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DistributeClient {
	
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
	public DistributeClient(String name, String zkConnStr, int sessTimeout) {
		this.name = (name == null ? "NULL" : name);
		this.zkConnStr = (zkConnStr == null ? "" : zkConnStr);
		this.sessTimeout = (sessTimeout < 0 ? 0 : sessTimeout);
	}
	
	/**
	 * <pre>
	 * 连接到 zookeeper 集群, 
	 * 并监听【记录了整个集群中所有服务器信息】的节点
	 * </pre>
	 * @param NODE_SERVERS_INFO 【记录了整个集群中所有服务器信息】的节点
	 * @return
	 */
	public boolean connZK(final String NODE_SERVERS_INFO) {
		boolean isOk = true;
		try {
			zk = new ZooKeeper(zkConnStr, sessTimeout, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					try {
						
						// 获取【记录了整个集群中所有服务器信息】节点下所有子节点
						// 即获取所有已上线的并注册到这个节点的服务器节点
						List<String> childs = zk.getChildren(NODE_SERVERS_INFO, true);
						
						// 取出每个子节点的服务器数据
						List<String> serverInfos = new LinkedList<String>();
						for (String child : childs) {
							String serverInfo = new String(
									zk.getData(NODE_SERVERS_INFO + "/" + child, false, null), 
									"UTF-8");
							serverInfos.add(serverInfo);
						}
						System.out.println("客户端 [" + name + "] 已刷新当前在线的服务器信息 : " + serverInfos);
						
					} catch (Exception e) {
						System.out.println("客户端  [" + name + "] 监听已注册服务到集群的信息节点 [" + NODE_SERVERS_INFO + "] 异常");
						e.printStackTrace();
					}
				}
			});
			System.out.println("客户端 [" + name + "] 上线");
			
		} catch (IOException e) {
			System.err.println("客户端 [" + name + "] 连接到分布式集群失败");
			e.printStackTrace();
			isOk = false;
		}
		return isOk;
	}
	
	/**
	 * <pre>
	 * 断开客户端与集群的连接.
	 * </pre>
	 */
	public void close() {
		if(zk != null) {
			try {
				zk.close();
				zk = null;
				System.out.println("客户端 [" + name + "] 下线");
				
			} catch (Exception e) {
				System.err.println("客户端 [" + name + "] 从分布式集群断开失败");
				e.printStackTrace();
			}
		}
	}
	
}
