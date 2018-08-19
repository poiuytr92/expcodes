package exp.zk.demo.lock;

import java.util.Random;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

class _DefaultHandler implements Handler {

	@Override
	public void handle(ZooKeeper zk, WatchedEvent event, String keepLockNode) {
		System.out.println(keepLockNode + " is working......");
		
		// 模拟业务处理
		try {
			Thread.sleep(new Random().nextInt(4000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		System.out.println(keepLockNode + " is done ......");	
	}

}
