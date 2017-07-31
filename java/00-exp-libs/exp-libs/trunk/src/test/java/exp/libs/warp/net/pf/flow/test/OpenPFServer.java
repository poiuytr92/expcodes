package exp.libs.warp.net.pf.flow.test;

import exp.libs.warp.net.pf.flow.PFAgent;


public class OpenPFServer extends Thread {
	
	public static void main(String[] args) {
		// 获取本地监听端口、远程IP和远程端口
		int localPort = 9999;
		String remoteIp = "172.168.10.63";
		int remotePort = 3306;
		int overtime = 10000;
		int maxConn = 100;
		
		PFAgent server = new PFAgent(localPort, remoteIp, remotePort, 
				overtime, maxConn);
		server._start();
	}
	
}
