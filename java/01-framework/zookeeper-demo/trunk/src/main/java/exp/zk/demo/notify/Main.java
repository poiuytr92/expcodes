package exp.zk.demo.notify;

import java.util.LinkedList;
import java.util.List;

/**
 * <PRE>
 * 【场景】集群状态动态感知：某分布式集群系统中有多台服务器，可以动态上下线。 任意一台客户端都能实时感知 到任意一台服务器的上下线。 
 * 【思路】
 * 	在分布式集群中指定一个[固有节点 znode]，专门用于记录集群中所有服务器信息.
 *  所有充当 [服务器角色] 的节点，在连接到集群之后，首先去 [固有节点 znode] 下创建一个 [临时子 znode]， 并存储自身的信息.
 *  所有充当 [客户端角色] 的节点，在连接到集群之后，首先去 [固有节点 znode] 进行监听其所有 [子 znode] 的变化.
 *  
 *  这样, [客户端节点] 在首次连接到集群后，可以获取集群中当前在线的所有服务器列表.
 *  当某个 [服务器节点] 下线后， 它注册到 [固有节点 znode] 下的 [临时子 znode] 也会随之消失，
 *  此时，所有 [客户端节点] 会监听到 [子 znode] 列表发生变化，再次重新获取集群中当前在线的所有服务器列表.
 *  同理，当新的 [服务器节点] 上线后， 所有 [客户端节点] 也会监听到 [子 znode] 列表发生变化，再次重新获取集群中当前在线的所有服务器列表.
 * 
 * ------------------------------------------------
 * 
 * 其实这就是 Dubbo 注册中心的简化场景：
 * 
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {

	private static final String NODE_SERVERS_INFO = "/serversInfo";
	
	private final static String ZK_CONN_STR = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
	
	private final static int SESS_TIMEOUT = 10000;
	
	public static void main(String[] args) throws Exception {
		
		// 在分布式集群注册5台服务器
		List<DistributeServer> servers = new LinkedList<DistributeServer>();
		for(int i = 0; i < 5; i++) {
			String name = "server-" + i;
			DistributeServer server = new DistributeServer(name, ZK_CONN_STR, SESS_TIMEOUT);
			if(server.connZK()) {
				if(server.registe(NODE_SERVERS_INFO)) {	// 注册服务到信息节点
					servers.add(server);
				}
			}
		}
		
		// 用2台客户端连接到分布式集群，并监听服务器信息节点
		List<DistributeClient> clients = new LinkedList<DistributeClient>();
		for(int i = 0; i < 2; i++) {
			String name = "client-" + i;
			DistributeClient client = new DistributeClient(name, ZK_CONN_STR, SESS_TIMEOUT);
			if(client.connZK(NODE_SERVERS_INFO)) {	// 连接到集群并监听服务器信息节点
				clients.add(client);
			}
			
		}
		
		// 模拟两台服务器下线
		Thread.sleep(3000);
		servers.get(1).close();
		servers.get(3).close();
		
		// 模拟新服务器上线，并在分布式集群注册
		Thread.sleep(3000);
		DistributeServer newServer = new DistributeServer("new-server", ZK_CONN_STR, SESS_TIMEOUT);
		if(newServer.connZK()) {
			newServer.registe(NODE_SERVERS_INFO);
		}
		
		// 所有节点下线
		Thread.sleep(3000);
		for(DistributeServer server : servers) {
			server.close();
		}
		for(DistributeClient client : clients) {
			client.close();
		}
	}
	
}
