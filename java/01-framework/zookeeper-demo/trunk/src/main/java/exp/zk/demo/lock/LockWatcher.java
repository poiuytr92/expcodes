package exp.zk.demo.lock;

import java.util.Random;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

class LockWatcher implements Watcher {
	
	private DistributeLock dLocker;
	
	private final String LOCK_NODE;
	
	private String keepLock;
	
	protected LockWatcher(DistributeLock dLocker, String LOCK_NODE) {
		this.dLocker = dLocker;
		this.LOCK_NODE = LOCK_NODE;
		this.keepLock = "";
	}
	
	protected void initLock() {
		this.keepLock = dLocker.initLock();
	}
	
	private void getLock() {
		this.keepLock = dLocker.getLock();
	}
	
	private boolean isGetLock() {
		return dLocker.isGetLock(keepLock);
	}
	
	private void releaseLock() {
		dLocker.releaseLock(keepLock);
	}

	/**
	 * 当节点发生变化时，判断当前最小的节点是不是自己创建的，若是则说明获得锁并可以执行业务操作
	 */
    @Override
    public void process(WatchedEvent event) {
		
		// 匹配看是不是子节点变化，并且监听的路径也要对
		if (event.getType() == EventType.NodeChildrenChanged
				&& event.getPath().equals(LOCK_NODE)) {
			
			if(isGetLock()) {
				handle();	// 处理业务
				releaseLock();
				getLock();
			}
		}
	}
    
    public void handle() {
		System.out.println(keepLock + " is working......");
		
		// 模拟业务处理
		try {
			Thread.sleep(new Random().nextInt(4000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		System.out.println(keepLock + " is done ......");
	}
    
	
}
