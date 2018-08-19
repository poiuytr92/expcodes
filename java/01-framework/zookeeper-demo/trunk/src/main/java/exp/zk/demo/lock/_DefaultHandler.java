package exp.zk.demo.lock;

import java.util.Random;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

/**
 * <PRE>
 * 分布式共享锁：默认业务处理器
 * </PRE>
 * <br/><B>PROJECT : </B> zookeeper
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-08-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _DefaultHandler implements Handler {

	@Override
	public void handle(ZooKeeper zk, WatchedEvent event, String keepLockNode) {
		System.out.println("+++++++++++++++++++++");
		System.out.println("获得锁：" + keepLockNode);
		
		
		// 模拟业务处理
		try {
			int sleepTime = new Random().nextInt(4000);
			System.out.println("正在模拟业务逻辑... (" + sleepTime + "ms)");
			Thread.sleep(sleepTime);
			
		} catch (InterruptedException e) {}	
		
		
		System.out.println("释放锁：" + keepLockNode);
		System.out.println("---------------------");
	}

}
