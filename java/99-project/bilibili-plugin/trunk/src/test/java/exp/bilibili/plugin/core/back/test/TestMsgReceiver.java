package exp.bilibili.plugin.core.back.test;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

public class TestMsgReceiver {

	public static void main(String[] args) {
		String hex = "0000003F0010000000000005000000007B22636D64223A22505245504152494E47222C22726F756E64223A312C22726F6F6D6964223A22333930343830227D000000450010000000000005000000007B22636D64223A22524F4F4D5F53494C454E545F4F4646222C2264617461223A5B5D2C22726F6F6D6964223A22333930343830227D";
		foo(hex);
	}
	
	public static void foo(String hex) {
		byte[] bytes = BODHUtils.toBytes(hex);
		String msg = new String(bytes);
		System.out.println(StrUtils.view(msg));
		System.out.println("====");
		
		String sJson = RegexUtils.findFirst(msg.substring(15), "[^{]*([^\0]*)");
		System.out.println(sJson);
	}
	
}
