package exp.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * <PRE>
 * apache-zookeeper示例
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestZooKeeper {
	
    public static void main(String[] args) throws Exception {
    	new TestZooKeeper().test();
    }
    
    
    
    public void test() throws Exception {
    	final String CHARSET = "UTF-8";
    	
    	/* 连接到zookeeper集群 */
    	final String ZK_CONN_STR = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    	final int SESS_TIMEOUT = 300000;
    	NodeWatcher nodeWatcher = new NodeWatcher(); // zookeeper节点监视器(当节点发生变化时, 会触发此监视器)
        ZooKeeper zk = new ZooKeeper(ZK_CONN_STR, SESS_TIMEOUT, nodeWatcher);
        
        /*
         * 阻塞等待连接到zookeeper集群.
         * 若zookeeper已经启动一段时间是不需要循环检测的，此方法目的是兼容zookeeper刚刚启动的情况.
         */
        while(!zk.getState().equals(ZooKeeper.States.CONNECTED)) {
        	Thread.sleep(1000);
        }
        
        /* 若zookeeper节点不存在，则创建之 */
        String nodePath = "/zk-test-node";	// 节点位置
        Stat stat = zk.exists(nodePath, false);
        if(stat == null) {
        	String nodeData = "http://exp-blog.com";	// 节点数据
        	
        	// 创建一个持久化节点(即在zookeeper服务停止后依然可以保存该节点数据)
        	// 与之相对的则是 CreateMode.EPHEMERAL 临时节点(即在zookeeper服务停止后该节点数据丢失)
            zk.create(nodePath, nodeData.getBytes(CHARSET), 
            		ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        
        /* 从zookeeper节点上读取数据 */
        byte[] bytes = zk.getData(nodePath, false, stat);
        String nodeData = new String(bytes, CHARSET);
        System.out.println(nodeData);
        
        /* 断开zookeeper连接 */
        zk.close();
    }
    
    /**
     * <PRE>
     * zookeeper节点监视器(当节点发生变化时, 会触发此监视器)
     * </PRE>
     * <br/><B>PROJECT : </B> zookeeper
     * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
     * @version   2018-08-02
     * @author    EXP: 272629724@qq.com
     * @since     jdk版本：jdk1.6
     */
    private class NodeWatcher implements Watcher {
    	
        @Override
        public void process(WatchedEvent event) {
            System.out.println("-----------");
            System.out.println("path:" + event.getPath());
            System.out.println("type:" + event.getType());
            System.out.println("stat:" + event.getState());
            System.out.println("-----------");
        }
    }
}
