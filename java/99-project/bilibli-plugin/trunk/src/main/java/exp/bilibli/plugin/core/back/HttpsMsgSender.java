package exp.bilibli.plugin.core.back;

import java.util.HashMap;
import java.util.Map;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.envm.ChatColor;
import exp.bilibli.plugin.https.HttpsUtils;

public class HttpsMsgSender {

	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	protected HttpsMsgSender() {}
	
	public static boolean sendChat(String msg, String cookies, String realRoomId) {
		return sendChat(msg, cookies, realRoomId, ChatColor.WHITE);
	}

	public static boolean sendChat(String msg, String cookies, String roomId, String color) {
		boolean isOk = false;
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = toRequestParams(msg, sRoomId, color);
			String response = HttpsUtils.doPost(CHAT_URL, headParams, requestParams);
			
			// FIXME
		}
		return isOk;
	}
	
	private static Map<String, String> toHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("Accept", "application/json, text/javascript, */*; q=0.01");
		params.put("Accept-Encoding", "gzip, deflate, br");
		params.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
		params.put("Connection", "keep-alive");
		params.put("Content-Type", HttpsUtils.TYPE_FORM);	// POST的是表单
		params.put("Cookie", cookies);
		params.put("Host", Config.getInstn().SSL_URL());
		params.put("Origin", Config.getInstn().LIVE_URL());
		params.put("Referer", Config.getInstn().LIVE_URL().concat(realRoomId));	// 发送/接收消息的直播间地址
		params.put("User-Agent", Config.USER_AGENT);
		return params;
	}
	
	private static Map<String, String> toRequestParams(String msg, String realRoomId, String color) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("rnd", String.valueOf(System.currentTimeMillis() / 1000));	// 时间戳
		params.put("msg", msg);		// 弹幕内容
		params.put("color", color);	// 弹幕颜色
		params.put("roomid", realRoomId);	// 接收消息的房间号
		params.put("fontsize", "25");
		params.put("mode", "1");
		return params;
	}
	
	
}
