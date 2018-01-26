package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class Chat extends __Protocol {

	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @return
	 */
	public static boolean sendChat(String msg) {
		final int roomId = UIUtils.getCurRoomId();
		return sendChat(msg, roomId);
	}
	
	/**
	 * 
	 * @param msg
	 * @param color
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color) {
		final int roomId = UIUtils.getCurRoomId();
		return sendChat(msg, color, roomId);
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param roomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, int roomId) {
		return sendChat(msg, ChatColor.WHITE, roomId);
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @param roomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color, int roomId) {
		return sendChat(msg, color, roomId, 
				CookiesMgr.INSTN().MAIN().toNVCookie());
	}

	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @param roomId 目标直播间房号
	 * @param cookie 发送用户的cookie
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color, 
			int roomId, String cookie) {
		boolean isOk = false;
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headers = toPostHeadParams(cookie, sRoomId);
			Map<String, String> requests = _toChatRequestParams(msg, sRoomId, color.CODE());
			String response = HttpURLUtils.doPost(CHAT_URL, headers, requests, Config.DEFAULT_CHARSET);
			isOk = _analyseChatResponse(response);
			
		} else {
			log.warn("发送弹幕失败: 无效的房间号 [{}]", roomId);
		}
		return isOk;
	}
	
	/**
	 * 
	 * @param msg
	 * @param realRoomId
	 * @param chatColor
	 * @return
	 */
	private static Map<String, String> _toChatRequestParams(
			String msg, String realRoomId, String color) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("rnd", String.valueOf(System.currentTimeMillis() / 1000));	// 时间戳
		params.put("msg", msg);		// 弹幕内容
		params.put("color", color);	// 弹幕颜色
		params.put("roomid", realRoomId);	// 接收消息的房间号
		params.put("fontsize", "25");
		params.put("mode", "1");
		return params;
	}
	
	/**
	 * 
	 * @param response  {"code":-101,"msg":"请先登录","data":[]}
	 * @return
	 */
	private static boolean _analyseChatResponse(String response) {
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				UIUtils.log("发送弹幕失败: ", reason);
			}
		} catch(Exception e) {
			UIUtils.log("发送弹幕失败: 服务器无响应");
			log.error("发送弹幕失败: {}", response, e);
		}
		return isOk;
	}
	
}
