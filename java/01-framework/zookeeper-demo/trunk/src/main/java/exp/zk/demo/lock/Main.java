package exp.zk.demo.lock;


public class Main {

	public static void main(String[] args) throws Exception {
		
		// 1、拿到 zookeeper 链接
		DistributeLock dLock = new DistributeLock();
		dLock.conn();
		System.out.println("conn");
		
		dLock.init();
		System.out.println("init");
		
		// 4、往父节点下注册节点，注册临时节点，好处就是，当宕机或者断开链接时该节点自动删除
		
		// 5、关闭 zk 链接
		Thread.sleep(Long.MAX_VALUE);
		dLock.close();
	}
	
}
