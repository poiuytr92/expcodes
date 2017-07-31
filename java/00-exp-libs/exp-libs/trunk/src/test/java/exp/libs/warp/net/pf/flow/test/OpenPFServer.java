package exp.libs.warp.net.pf.flow.test;

import exp.libs.warp.net.pf.flow.PFAgent;
import exp.libs.warp.net.pf.flow.PFConfig;


public class OpenPFServer extends Thread {
	
	public static void main(String[] args) {
		// 获取本地监听端口、远程IP和远程端口
		int localPort = 9999;
		String remoteIP = "172.168.10.63";
		int remotePort = 3306;
		int overtime = 10000;
		int maxConn = 100;
		
		PFConfig config = new PFConfig("test", localPort, remoteIP, remotePort);
		PFAgent agent = new PFAgent(config);
		agent._start();
	}
	
}
