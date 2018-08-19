package exp.zk.demo.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

public interface Handler {

	public void handle(ZooKeeper zk, WatchedEvent event, String keepLockNode);
	
}
