package exp.zk.demo.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * <PRE>
 * 锁事件监听器
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _LockWatcher implements Watcher {
	
	private DistributeNode dLocker;
	
	private final String LOCK_NODE;
	
	private String keepLock;
	
	private Handler handler;
	
	protected _LockWatcher(DistributeNode dLocker, String LOCK_NODE, Handler handler) {
		this.dLocker = dLocker;
		this.LOCK_NODE = LOCK_NODE;
		this.keepLock = "";
		this.handler = handler;
	}
	
	protected boolean registerLock() {
		this.keepLock = dLocker.lockToLineup();
		return (keepLock != null && !"".equals(keepLock));
	}
	
	private void lockToLineup() {
		this.keepLock = dLocker.lockToLineup();
	}
	
	private boolean isGetLock() {
		return dLocker.isGetLock(keepLock);
	}
	
	private void releaseLock() {
		dLocker.releaseLock(keepLock);
	}

    @Override
    public void process(WatchedEvent event) {
    	
		// 节点并发事件
		if (event.getType() == EventType.NodeChildrenChanged
				&& event.getPath().equals(LOCK_NODE)) {
			
			if(isGetLock()) {
				handler.handle(dLocker.ZOOKEEPER(), event, keepLock);	// 处理业务
				
				releaseLock();	// 释放当前锁
				lockToLineup();	// 生成下一把锁进行排队
			}
		}
	}
    
}
