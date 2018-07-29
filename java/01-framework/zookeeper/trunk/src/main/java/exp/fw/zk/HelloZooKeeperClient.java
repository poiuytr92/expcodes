package exp.fw.zk;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

import com.github.zkclient.ZkClient;

public class HelloZooKeeperClient {
	 
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
    	ZkClient zkClient = new ZkClient("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183");
        String node = "/server/hadoop030000000000";
        if (!zkClient.exists(node)) {
            zkClient.createPersistent(node, "hello hadoop03 ...".getBytes());
        }
        System.out.println(new String(zkClient.readData(node)));
    }
 
}
