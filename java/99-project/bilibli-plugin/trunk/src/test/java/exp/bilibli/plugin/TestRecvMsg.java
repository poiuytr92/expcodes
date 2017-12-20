package exp.bilibli.plugin;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

public class TestRecvMsg {

	public static void main(String[] args) {
		String hex = "0000001400100001000000030000000100000008";
		foo(hex);
	}
	
	public static void foo(String hex) {
		byte[] bytes = BODHUtils.toBytes(hex);
		String msg = new String(bytes);
		System.out.println(StrUtils.view(msg));
		System.out.println("====");
		
		String sJson = RegexUtils.findFirst(msg, "[^{]*(.*)");
		System.out.println(sJson);
	}
}
