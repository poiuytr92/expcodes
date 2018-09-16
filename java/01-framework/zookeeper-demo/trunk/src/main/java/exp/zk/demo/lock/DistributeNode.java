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
 * 在分布式集群中参与竞争锁的节点
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DistributeNode {
	
	private static final String NODE_PARENT_LOCK = "/parent_lock";
	
	private static final String NODE_SUB_LOCK = "/sub_lock";
	
	private String name;
	
	private String zkConnStr;
	
	private int sessTimeout;
	
	private ZooKeeper zk;
	
	private _LockWatcher lockWatcher;
	
	private Handler handler;
	
	/**
	 * 构造函数
	 * @param name 节点名称
	 * @param zkConnStr zookeeper集群连接串
	 * @param sessTimeout zookeeper会话超时(ms)
	 * @param handler 业务处理器
	 */
	public DistributeNode(String name, String zkConnStr, int sessTimeout, Handler handler) {
		this.name = (name == null ? "" : name);
		this.zkConnStr = (zkConnStr == null ? "" : zkConnStr);
		this.sessTimeout = (sessTimeout < 0 ? 0 : sessTimeout);
		this.handler = (handler == null ? new _DefaultHandler(name) : handler);
	}

	/**
	 * 连接到zookeeper集群并监听分布式并发锁
	 * @return
	 */
	public boolean conn() {
		boolean isOk = false;
		if(connZK()) {
			System.out.println("节点 [" + name + "] 连接到分布式集群成功");
			if(registerLock()) {
				System.out.println("节点 [" + name + "] 注册分布式并发锁成功");
				isOk = listenLock();
				if(isOk == true) {
					System.out.println("节点 [" + name + "] 监听分布式并发锁成功");
				}
			}
		}
		return isOk;
	}
	
	/**
	 * 连接到 zookeeper 集群
	 */
	private boolean connZK() {
		boolean isOk = true;
		try {
			this.zk = new ZooKeeper(zkConnStr, sessTimeout, 
					(lockWatcher = new _LockWatcher(this, NODE_PARENT_LOCK, handler)));
			
		} catch (IOException e) {
			System.err.println("节点 [" + name + "] 连接到zookeeper集群失败");
			e.printStackTrace();
			isOk = false;
		}
		return isOk;
	}
	
	/**
	 * 注册分布式共享锁节点
	 * @return
	 */
	private boolean registerLock() {
		boolean isOk = false;
		if(zk != null && lockWatcher != null) {
			try {
				
				// 若[父节点锁]不存在则创建
				Stat exists = zk.exists(NODE_PARENT_LOCK, false);
				if (exists == null) {
					zk.create(NODE_PARENT_LOCK, 
							NODE_PARENT_LOCK.getBytes(), 
							Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);	// 持久化节点
				} else {
					// UNDO [父节点锁]已经被其它客户端创建
				}
				
				// 首次申请当前客户端会话的[子节点锁]
				isOk = lockWatcher.registerLock();
				
			} catch (Exception e) {
				System.err.println("节点 [" + name + "] 注册分布式并发锁失败");
				e.printStackTrace();
			}
		}
		return isOk;
	}
	
	/**
	 * 开始监听 [父节点锁] 下的子节点事件
	 * @return
	 */
	private boolean listenLock() {
		boolean isOk = false;
		if(zk != null) {
			
			try {
				zk.getChildren(NODE_PARENT_LOCK, 
						true);	// 启用节点监听
				isOk = true;
				
			} catch (Exception e) {
				System.err.println("节点 [" + name + "] 监听分布式并发锁失败");
				e.printStackTrace();
			}
		}
		return isOk;
	}
	
	/**
	 * 生成一把锁到父节点下排队.
	 * 
	 *  由于所有客户端客户端在父节点下创建的子节点都是同名的，且后面有自增序列.
	 *  因此实际上此操作会在父子点下创建一个id为n的子节点进行排队.
	 * @return
	 */
	protected String lockToLineup() {
		String keepLock = "";
		if(zk != null) {
			try {
				keepLock = zk.create(
						NODE_PARENT_LOCK + NODE_SUB_LOCK, 
						NODE_SUB_LOCK.getBytes(),
						Ids.OPEN_ACL_UNSAFE, 
						CreateMode.EPHEMERAL_SEQUENTIAL);	// 临时节点，且有自增序列(当客户端拓机后自动删除)
				
			} catch (Exception e) {
				System.out.println("节点 [" + name + "] 获取并发誓共享锁失败");
				e.printStackTrace();
			}
		}
		return (keepLock == null ? "" : keepLock);
	}
	
	/**
	 * 判断是否成功获取到锁.
	 * 
	 * 	当父节点下的子节点发生变化时会触发此方法。
	 * 	此方法会获取所有子节点，并根据其序列id进行排序，
	 * 	若最小的一个id与当前客户端之前生成用于排队的锁id一致，说明获得锁。
	 * @param keepLock 之前生成用于排队的锁id（即当前持有的锁）
	 * @return
	 */
	protected boolean isGetLock(String keepLock) {
		boolean isGetLock = false;
		if(zk != null) {
			try {
				
				// 获取父节点的所有 [子节点锁], 并继续监听
				List<String> childs = zk.getChildren(NODE_PARENT_LOCK, true);
				
				// 对所有 [子节点锁] 按id排序，若最小的id与此客户端之前生成用于排队的锁的id一致，则获得锁
				Collections.sort(childs);	
				String minLock = NODE_PARENT_LOCK + "/" + childs.get(0);
				isGetLock = minLock.equals(keepLock);
				
			} catch (Exception e) {
				System.err.println("节点 [" + name + "] 判断是否获得分布式并发锁异常");
				e.printStackTrace();
			}
		}
		return isGetLock;
	}
	
	/**
	 * 释放锁
	 * @param keepLock 当前持有的锁
	 */
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
	
	public void close() {
		if(zk != null) {
			try {
				zk.close();
				zk = null;
				System.out.println("节点 [" + name + "] 已断开分布式集群连接");
				
			} catch (Exception e) {
				System.err.println("节点 [" + name + "] 断开分布式集群连接失败");
				e.printStackTrace();
			}
		}
	}
	
	public ZooKeeper ZOOKEEPER() {
		return zk;
	}
	
}
