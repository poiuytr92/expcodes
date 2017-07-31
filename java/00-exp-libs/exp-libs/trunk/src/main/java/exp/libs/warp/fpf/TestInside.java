package exp.libs.warp.fpf;



public class TestInside {

	public static void main(String[] args) {
		int localListenPort = 9999;
		String remoteIP = "172.168.10.63";
		int remotePort = 3306;
		String srDir = "C:\\Users\\Administrator\\Desktop\\test";
		int overtime = 10000;
		int maxConn = 100;
		
		FPFConfig config = new FPFConfig("test", 
				localListenPort, remoteIP, remotePort, overtime, maxConn);
		FPFAgents agents = new FPFAgents(srDir, config);
		agents._start();
	}
	
}
