package exp.bilibili.protocol.xhr;

import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class UserInfo extends _MsgSender {

	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	/**
	 * 查询账号信息
	 * {"code":0,"status":true,"data":{"level_info":{"current_level":4,"current_min":4500,"current_exp":7480,"next_exp":10800},"bCoins":0,"coins":464,"face":"http:\/\/i2.hdslb.com\/bfs\/face\/bbfd1b5cafe4719e3a57154ac1ff16a9e4d9c6b3.jpg","nameplate_current":"http:\/\/i1.hdslb.com\/bfs\/face\/54f4c31ab9b1f1fa2c29dbbc967f66535699337e.png","pendant_current":"","uname":"M-\u4e9a\u7d72\u5a1c","userStatus":"","vipType":1,"vipStatus":1,"official_verify":-1,"pointBalance":0}}
	 * @param cookie
	 * @return username
	 */
	public static String queryUsername(String cookie) {
		Map<String, String> headers = toGetHeadParams(cookie);
		String response = HttpURLUtils.doGet(ACCOUNT_URL, headers, null, Config.DEFAULT_CHARSET);
		
		String username = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				username = JsonUtils.getStr(data, BiliCmdAtrbt.uname);
			}
		} catch(Exception e) {
			log.error("查询账号信息失败: {}", response, e);
		}
		return username;
	}
	
}
