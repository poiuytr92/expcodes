package exp.bilibli.plugin;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.verify.RegexUtils;

public class TestRecvMsg {

	public static void main(String[] args) {
		String hex = "000000690010000000000005000000007B22636D64223A2257454C434F4D455F4755415244222C2264617461223A7B22756964223A313635303836382C22757365726E616D65223A224D2DE4BA9AE7B5B2E5A89C222C2267756172645F6C6576656C223A2232227D7D";
		foo(hex);
	}
	
	public static void foo(String hex) {
		byte[] bytes = BODHUtils.toBytes(hex);
		String msg = new String(bytes);
		System.out.println(msg);
		
		String sJson = RegexUtils.findFirst(msg, "[^{]*(.*)");
		System.out.println(sJson);
	}
}
