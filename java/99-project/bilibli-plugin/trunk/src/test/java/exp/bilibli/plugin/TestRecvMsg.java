package exp.bilibli.plugin;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.verify.RegexUtils;

public class TestRecvMsg {

	public static void main(String[] args) {
		String hex = "000000360010000100000007000000017B22756964223A302C22726F6F6D6964223A3339303438302C2270726F746F766572223A317D";
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
