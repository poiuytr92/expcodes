package exp.zk.demo.lock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;


/**
 * <PRE>
 * 【场景】分布式共享锁：多个客户端，需要同时访问同一个资源，但同一时间只允许一个客户端进行访问。 
 * 【思路】多个客户端都去父 znode 下写入一个子znode，能写入成功的去执行访问， 写入不成功的等待
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DistributeLock {
	
	private static final String PARENT_NODE = "/parent_locks";
	
	private static final String SUB_NODE = "/sub_client";
	
	private String zkConnStr;
	
	private int sessTimeout;
	
	private ZooKeeper zk;
	
	private LockWatcher lockWatcher;
	
	private Handler handler;
	
	public DistributeLock(String zkConnStr, int sessTimeout, Handler handler) {
		this.zkConnStr = (zkConnStr == null ? "" : zkConnStr);
		this.sessTimeout = (sessTimeout < 0 ? 0 : sessTimeout);
		this.handler = (handler == null ? new _DefaultHandler() : handler);
	}

	/**
	 * 拿到 zookeeper 集群的链接
	 */
	public boolean conn() {
		boolean isOk = false;
		try {
			this.zk = new ZooKeeper(zkConnStr, sessTimeout, 
					(lockWatcher = new LockWatcher(this, PARENT_NODE, handler)));
			isOk = true;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isOk;
	}
	
	public boolean init() {
		boolean isOk = false;
		
		// 2、查看父节点是否存在，不存在则创建
		if(zk != null) {
			try {
				Stat exists = zk.exists(PARENT_NODE, false);
				if (exists == null) {
					zk.create(PARENT_NODE, PARENT_NODE.getBytes(), Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);
				}
				isOk = true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isOk;
	}
	
	// 4、往父节点下注册节点，注册临时节点，好处就是，当宕机或者断开链接时该节点自动删除
	public boolean listenLock() {
		boolean isOk = false;
		if(zk != null && lockWatcher != null) {
			
			
			isOk = lockWatcher.initLock();
			
			// 3、监听父节点
			try {
				zk.getChildren(PARENT_NODE, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isOk;
	}
	
	public void close() {
		if(zk != null) {
			try {
				zk.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected String getLock() {
		String keepLock = "";
		if(zk != null) {
			try {
				keepLock = zk.create(PARENT_NODE + SUB_NODE, SUB_NODE.getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (keepLock == null ? "" : keepLock);
	}
	
	protected boolean isGetLock(String keepLock) {
		boolean isGetLock = false;
		if(zk != null) {
			try {
				
				// 获取父节点的所有子节点, 并继续监听
				List<String> childrenNodes = zk.getChildren(PARENT_NODE, true);
				Collections.sort(childrenNodes);	// 匹配当前创建的 znode 是不是最小的 znode
				String minLock = PARENT_NODE + "/" + childrenNodes.get(0);
				isGetLock = minLock.equals(keepLock);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isGetLock;
	}
	
	protected void releaseLock(String keepLock) {
		if(zk != null && keepLock != null) {
			try {
				zk.delete(keepLock, -1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ZooKeeper ZOOKEEPER() {
		return zk;
	}
	
}
