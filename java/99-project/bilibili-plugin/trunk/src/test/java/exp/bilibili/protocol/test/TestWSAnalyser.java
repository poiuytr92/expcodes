package exp.bilibili.protocol.test;

import exp.bilibili.plugin.Config;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;

public class TestWSAnalyser {

	public static void main(String[] args) {
		String hex = "7B22636D64223A22524F4F4D5F424C4F434B5F4D5347222C22756964223A22323437303536383333222C22756E616D65223A226867686937343332222C22726F6F6D6964223A3339303438307D";
		alalyseMsg(hex);
	}
	
	public static void alalyseMsg(String hexMsg) {
		byte[] bytes = BODHUtils.toBytes(hexMsg);
		String msg = new String(bytes);
		System.out.println(StrUtils.view(msg));
		System.out.println("====");
		
		String split = hexMsg.substring(0, 32);	// 消息的前32个字节(即16个字符)为分隔符
		String[] hexs = hexMsg.split(split);
		for(String hex : hexs) {
			if(StrUtils.isEmpty(hex)) {
				continue;
			}
			
			msg = CharsetUtils.toStr(BODHUtils.toBytes(hex), Config.DEFAULT_CHARSET);
			System.out.println(msg);
		}
	}
	
}
