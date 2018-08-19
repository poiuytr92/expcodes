package exp.zk.demo.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

class _LockWatcher implements Watcher {
	
	private DistributeLock dLocker;
	
	private final String LOCK_NODE;
	
	private String keepLock;
	
	private Handler handler;
	
	protected _LockWatcher(DistributeLock dLocker, String LOCK_NODE, Handler handler) {
		this.dLocker = dLocker;
		this.LOCK_NODE = LOCK_NODE;
		this.keepLock = "";
		this.handler = handler;
	}
	
	protected boolean initLock() {
		this.keepLock = dLocker.getLock();
		return (keepLock != null && !"".equals(keepLock));
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
				handler.handle(dLocker.ZOOKEEPER(), event, keepLock);	// 处理业务
				releaseLock();
				getLock();
			}
		}
	}
    
}
