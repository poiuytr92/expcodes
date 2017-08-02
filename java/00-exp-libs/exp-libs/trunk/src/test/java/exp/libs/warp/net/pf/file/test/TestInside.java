package exp.libs.warp.net.pf.file.test;

import exp.libs.warp.net.pf.file.FPFAgent;
import exp.libs.warp.net.pf.file.FPFConfig;



public class TestInside {

	public static void main(String[] args) {
		int localListenPort = 9999;
		String remoteIP = "172.168.10.63";
		int remotePort = 3306;
		String sendDir = "C:\\Users\\Administrator\\Desktop\\server\\send";
		String recvDir = "C:\\Users\\Administrator\\Desktop\\server\\recv";
		int overtime = 10000;
		int maxConn = 100;
		
		FPFConfig config = new FPFConfig("test", 
				localListenPort, remoteIP, remotePort, maxConn);
		FPFAgent agents = new FPFAgent(sendDir, recvDir, overtime, config);
		agents._start();
	}
	
}
