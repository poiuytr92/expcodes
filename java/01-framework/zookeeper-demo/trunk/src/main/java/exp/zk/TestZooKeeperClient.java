package exp.zk;

import com.github.zkclient.ZkClient;

/**
 * <PRE>
 * zkClient示例
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestZooKeeperClient {
	 
    public static void main(String[] args) throws Exception {
    	
    	/* 连接到zookeeper集群 */
    	final String ZK_CONN_STR = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    	ZkClient zkClient = new ZkClient(ZK_CONN_STR);
    	
    	/* 若zookeeper节点不存在，则创建之 */
        String nodePath = "/zk-test-node";	// 节点位置
        if (!zkClient.exists(nodePath)) {
        	String nodeData = "http://exp-blog.com";	// 节点数据
        	
        	// 创建一个持久化节点(即在zookeeper服务停止后依然可以保存该节点数据)
        	// 与之相对的则是 createEphemeral 临时节点(即在zookeeper服务停止后该节点数据丢失)
            zkClient.createPersistent(nodePath, nodeData.getBytes());
        }
        
        /* 从zookeeper节点上读取数据 */
        String nodeData = new String(zkClient.readData(nodePath));
        System.out.println(nodeData);
        
        /* 断开zookeeper连接 */
        zkClient.close();
    }
 
}
