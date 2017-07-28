package exp.libs.warp.fpf;



public class Test {

	public static void main(String[] args) {
		int localListenPort = 9999;
		String remoteIP = "172.168.10.63";
		int remotePort = 3306;
		String srDir = "C:\\Users\\Administrator\\Desktop\\test";
		int overtime = 10000;
		int maxConn = 100;
		
		FPFAgentConfig config = new FPFAgentConfig("test", 
				localListenPort, remoteIP, remotePort, srDir, overtime, maxConn);
		
		_FPFServer server = new _FPFServer(config);
		server._start();
		
		_FPFClient cilent = new _FPFClient(srDir, overtime);
		cilent._start();
	}
	
}
