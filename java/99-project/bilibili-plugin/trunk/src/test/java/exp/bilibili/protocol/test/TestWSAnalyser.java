package exp.bilibili.protocol.test;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 测试WebSocket的接收报文
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestWSAnalyser {

	public static void main(String[] args) {
		String hex = "000000EE0010000000000005000000007B22636D64223A22434F4D424F5F454E44222C2264617461223A7B22756E616D65223A225C75356262655C75396533665C75356266625C7537666264222C22725F756E616D65223A225C75353366365C75383433645C75383361625C7538613030222C22636F6D626F5F6E756D223A322C227072696365223A313030302C22676966745F6E616D65223A225C75356537335C75356539355C7539353035222C22676966745F6964223A33303030372C2273746172745F74696D65223A313532393535303036352C22656E645F74696D65223A313532393535303037317D7D000000680010000000000005000000007B22636D64223A2257454C434F4D45222C2264617461223A7B22756964223A31393536363232332C22756E616D65223A22E7919EE6B3A2E6B3A263222C2269735F61646D696E223A66616C73652C2273766970223A317D7D";
		alalyseMsg(hex);
	}
	
	private static void alalyseMsg(String hexMsg) {
		byte[] bytes = BODHUtils.toBytes(hexMsg);
		String msg = new String(bytes);
		System.out.println(StrUtils.view(msg));
		System.out.println("====");
		
		int len = 0;
		do {
			len = getLen(hexMsg);
			if(len <= 32) {
				break;
			}
			String subHexMsg = hexMsg.substring(32, len);
			msg = new String(BODHUtils.toBytes(subHexMsg));
			System.out.println(StrUtils.view(msg));
			
			
			hexMsg = hexMsg.substring(len);
		} while(StrUtils.isNotEmpty(hexMsg));
	}
	
	private static int getLen(String hexMsg) {
		String hexLen = hexMsg.substring(0, 8);	// 消息的前8位是本条消息长度
		long len = BODHUtils.hexToDec(hexLen);
		return (int) (len * 2);
	}
	
}
