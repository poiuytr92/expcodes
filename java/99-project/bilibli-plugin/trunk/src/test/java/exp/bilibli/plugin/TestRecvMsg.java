package exp.bilibli.plugin;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

public class TestRecvMsg {

	public static void main(String[] args) {
		String hex = "00-00-00-FB-00-10-00-00-00-00-00-05-00-00-00-00-7B-22-69-6E-66-6F-22-3A-5B-5B-30-2C-31-2C-32-35-2C-31-36-37-37-32-34-33-31-2C-31-35-31-33-36-35-34-37-37-35-2C-22-31-35-31-33-36-35-34-37-33-37-22-2C-30-2C-22-35-33-64-37-63-37-62-63-22-2C-30-5D-2C-22-62-E6-B5-8B-E8-AF-95-22-2C-5B-31-36-35-30-38-36-38-2C-22-4D-2D-E4-BA-9A-E7-B5-B2-E5-A8-9C-22-2C-30-2C-31-2C-31-2C-31-30-30-30-30-2C-31-5D-2C-5B-31-36-2C-22-E9-AB-98-E8-BE-BE-22-2C-22-4D-E6-96-AF-E6-96-87-E8-B4-A5-E7-B1-BB-22-2C-22-35-31-31-30-38-22-2C-31-36-37-34-36-31-36-32-2C-22-22-5D-2C-5B-34-33-2C-30-2C-31-36-37-34-36-31-36-32-2C-34-39-30-32-5D-2C-5B-22-74-61-73-6B-2D-79-65-61-72-22-2C-22-74-69-74-6C-65-2D-32-39-2D-31-22-5D-2C-32-2C-30-5D-2C-22-63-6D-64-22-3A-22-44-41-4E-4D-55-5F-4D-53-47-22-7D".replace("-", "");
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
