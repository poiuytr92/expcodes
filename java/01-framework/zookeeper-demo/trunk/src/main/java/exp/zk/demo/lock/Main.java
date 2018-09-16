package exp.zk.demo.lock;

import java.util.LinkedList;
import java.util.List;

/**
 * <PRE>
 * 【场景】分布式共享锁：多个客户端，需要同时访问同一个资源，但同一时间只允许一个客户端进行访问。 
 * 【思路】
 * 	多个客户端都去同一个[父 znode]下写入一个[同名子znode]，能写入成功的获得锁， 写入不成功的等待.
 * 	获得锁的客户端可以执行本地的业务逻辑，执行完成后则删除该[子znode](即释放锁)
 * 	其他客户端循环这个锁竞争
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {

	private final static String ZK_CONN_STR = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
	
	private final static int SESS_TIMEOUT = 10000;
	
	public static void main(String[] args) throws Exception {
		
		// 在集群中创建3个节点参与锁竞争
		List<DistributeNode> nodes = new LinkedList<DistributeNode>();
		for(int i = 0; i < 3; i++) {
			String name = "node-" + i;
			DistributeNode node = new DistributeNode(name, ZK_CONN_STR, SESS_TIMEOUT, null);
			if(node.conn()) {	// 连接到zookeeper集群并监听锁
				nodes.add(node);
			}
		}
		
		
		// 通过休眠使得客户端线程保活
		Thread.sleep(60000);
		for(DistributeNode node : nodes) {
			node.close();
		}
	}
	
}
