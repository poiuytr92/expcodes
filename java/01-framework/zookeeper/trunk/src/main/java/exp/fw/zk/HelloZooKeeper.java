package exp.fw.zk;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class HelloZooKeeper {
	 
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
    	
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", 
        		300000, new DemoWatcher());//连接zk server集群
        
        // 阻塞等待连接到zookeeper集群
        if (!zk.getState().equals(ZooKeeper.States.CONNECTED)) {
            while (true) {
                if (zk.getState().equals(ZooKeeper.States.CONNECTED)) {
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        String node = "/app2";
        Stat stat = zk.exists(node, false);//检测/app1是否存在
        if (stat == null) {
            //创建节点
            String createResult = zk.create(node, "hello zk".getBytes(), 
            		ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);	// 持久化节点
            System.out.println(createResult);
        }
        //获取节点的值
        byte[] b = zk.getData(node, false, stat);
        System.out.println(new String(b));
        zk.close();
    }
 
    static class DemoWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            System.out.println("----------->");
            System.out.println("path:" + event.getPath());
            System.out.println("type:" + event.getType());
            System.out.println("stat:" + event.getState());
            System.out.println("<-----------");
        }
    }
}
