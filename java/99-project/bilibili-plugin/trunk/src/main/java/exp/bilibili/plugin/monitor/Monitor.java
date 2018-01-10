package exp.bilibili.plugin.monitor;

import exp.libs.utils.encode.CryptoUtils;


public class Monitor {

	/** 软件授权页 */
	private final static String URL = CryptoUtils.deDES(
			"610BEF99CF948F0DB1542314AC977291892B30802EC5BF3B2DCDD5538D66DDA67467CE4082C2D0BC56227128E753555C");
		
	private String appName;

	private String appVersion = "";
	
	private static volatile Monitor instance;
	
	public static Monitor getInstn() {
		if(instance == null) {
			synchronized (Monitor.class) {
				if(instance == null) {
					instance = new Monitor();
				}
			}
		}
		return instance;
	}
	
	public static void main(String[] args) {
		System.out.println(URL);
	}
	
}
