package exp.libs.warp.net.pf.file.test;

import exp.libs.warp.net.pf.file.FPFAgent;



public class TestOutside {

	public static void main(String[] args) {
		int overtime = 10000;
		String srDir = "C:\\Users\\Administrator\\Desktop\\test";
		FPFAgent agents = new FPFAgent(srDir, overtime);
		agents._start();
	}
	
}
