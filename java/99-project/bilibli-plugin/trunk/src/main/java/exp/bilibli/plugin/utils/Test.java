package exp.bilibli.plugin.utils;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.core.gift.GiftRoomMgr;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

public class Test {

	public static void main(String[] args) {
		String hex = "000000EC0010000000000005000000007B22636D64223A225359535F47494654222C226D7367223A22E69CA8E69CA8E4B88DE698AFE998BFE5A4B4E69CA83A3F2020E59CA8E6B7B3E889B2E79A843A3FE79BB4E692ADE997B4313034303A3FE58685E8B5A0E980813A3F333A3FE585B13436E4B8AA222C22726E64223A22373837323035333032222C22756964223A33383032303231362C226D73675F74657874223A22E69CA8E69CA8E4B88DE698AFE998BFE5A4B4E69CA8E59CA8E6B7B3E889B2E79A84E79BB4E692ADE997B431303430E58685E8B5A0E9808142E59DB7E59E83E585B13436E4B8AA227D";
		foo(hex);
	}
	
	public static void foo(String hex) {
		byte[] bytes = BODHUtils.toBytes(hex);
		String msg = new String(bytes);
		System.out.println(msg);
		
		String sJson = RegexUtils.findFirst(msg, "[^{]*(.*)");
		if(JsonUtils.isVaild(sJson)) {
			JSONObject json = JSONObject.fromObject(sJson);
			String roomId = JsonUtils.getStr(json, "real_roomid");
			if(StrUtils.isEmpty(roomId)) {
				roomId = JsonUtils.getStr(json, "roomid");
			}
			
			if(StrUtils.isNotEmpty(roomId)) {
				GiftRoomMgr.getInstn().add(roomId);
				System.out.println("直播间 [" + roomId + "] 正在高能抽奖中!!!");
				
			} else {
				String msgText = JsonUtils.getStr(json, "msg_text");
				System.out.println("全频道公告: " + msgText);
			}
		}
	}
}
