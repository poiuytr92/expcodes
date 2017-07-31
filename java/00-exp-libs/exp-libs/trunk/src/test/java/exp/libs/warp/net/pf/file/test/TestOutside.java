package exp.libs.warp.net.pf.file.test;

import exp.libs.warp.net.pf.file.FPFAgents;



public class TestOutside {

	public static void main(String[] args) {
		String srDir = "C:\\Users\\Administrator\\Desktop\\test";
		FPFAgents agents = new FPFAgents(srDir);
		agents._start();
	}
	
}
