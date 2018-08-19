package exp.zk.demo.lock;


public class Main {

	private final static String ZK_CONN_STR = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
	
	private final static int SESS_TIMEOUT = 300000;
	
	public static void main(String[] args) throws Exception {
		_DefaultHandler handler = new _DefaultHandler();
		DistributeLock dLock = new DistributeLock(ZK_CONN_STR, SESS_TIMEOUT, handler);
		
		dLock.conn();
		dLock.init();
		dLock.listenLock();
		
		Thread.sleep(Long.MAX_VALUE);
		dLock.close();
	}
	
}
