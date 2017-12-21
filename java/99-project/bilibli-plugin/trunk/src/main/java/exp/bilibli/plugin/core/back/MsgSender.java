package exp.bilibli.plugin.core.back;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.bilibli.plugin.envm.ChatColor;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpsUtils;

/**
 * <PRE>
 * B站直播版聊消息发送器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MsgSender {

	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	protected MsgSender() {}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param realRoomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, String roomId) {
		return sendChat(msg, ChatColor.WHITE, roomId, Browser.COOKIES());
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param chatColor 弹幕颜色
	 * @param realRoomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, String chatColor, String roomId) {
		return sendChat(msg, chatColor, roomId, Browser.COOKIES());
	}

	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param chatColor 弹幕颜色
	 * @param roomId 目标直播间房号
	 * @param cookies 发送用户的cookies
	 * @return
	 */
	public static boolean sendChat(String msg, String chatColor, 
			String roomId, String cookies) {
		boolean isOk = false;
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = toRequestParams(msg, sRoomId, chatColor);
			String response = HttpsUtils.doPost(CHAT_URL, headParams, requestParams);
			isOk = analyse(response);
		}
		return isOk;
	}
	
	/**
	 * 
	 * @param cookies
	 * @param realRoomId
	 * @return
	 */
	private static Map<String, String> toHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpsUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpsUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpsUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpsUtils.HEAD.VAL.CONTENT_TYPE_FORM.concat(Config.DEFAULT_CHARSET));	
		params.put(HttpsUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpsUtils.HEAD.KEY.HOST, Config.getInstn().SSL_URL());
		params.put(HttpsUtils.HEAD.KEY.ORIGIN, Config.getInstn().LIVE_URL());
		params.put(HttpsUtils.HEAD.KEY.REFERER, Config.getInstn().LIVE_URL().concat(realRoomId));	// 发送/接收消息的直播间地址
		params.put(HttpsUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
	/**
	 * 
	 * @param msg
	 * @param realRoomId
	 * @param chatColor
	 * @return
	 */
	private static Map<String, String> toRequestParams(
			String msg, String realRoomId, String chatColor) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("rnd", String.valueOf(System.currentTimeMillis() / 1000));	// 时间戳
		params.put("msg", msg);		// 弹幕内容
		params.put("color", chatColor);	// 弹幕颜色
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
	private static boolean analyse(String response) {
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				UIUtils.log("发送弹幕失败: {}", reason);
			}
		} catch(Exception e) {
			UIUtils.log("发送弹幕失败: 服务器无响应");
		}
		return isOk;
	}
	
}
