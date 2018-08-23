package exp.fpf.test;

import exp.fpf.nat.FPFAgent;



public class TestOutside {

	public static void main(String[] args) {
		int overtime = 10000;
		String sendDir = "C:\\Users\\Administrator\\Desktop\\client\\send";
		String recvDir = "C:\\Users\\Administrator\\Desktop\\client\\recv";
		FPFAgent agents = new FPFAgent(sendDir, recvDir, overtime);
		agents._start();
	}
	
}
