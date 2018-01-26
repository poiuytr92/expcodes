package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 版聊弹幕
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Chat extends __Protocol {

	/** 弹幕URL */
	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	/**
	 * 发送弹幕消息
	 * @param cookie 发送用户的cookie
	 * @param roomId 目标直播间房号
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @return
	 */
	public static boolean send(HttpCookie cookie, int roomId, String msg, ChatColor color) {
		boolean isOk = false;
		if(roomId > 0) {
			String sRoomId = String.valueOf(roomId);
			Map<String, String> header = POST_HEADER(cookie.toNVCookie(), sRoomId);
			Map<String, String> request = getRequest(msg, sRoomId, color.RGB());
			String response = HttpURLUtils.doPost(CHAT_URL, header, request);
			isOk = analyseResponse(response);
			
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
	private static Map<String, String> getRequest(String msg, String roomId, String color) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("rnd", String.valueOf(System.currentTimeMillis() / 1000));	// 时间戳
		params.put("msg", msg);			// 弹幕内容
		params.put("color", color);		// 弹幕颜色
		params.put("roomid", roomId);	// 接收消息的房间号
		params.put("fontsize", "25");
		params.put("mode", "1");
		return params;
	}
	
	/**
	 * 
	 * @param response  {"code":-101,"msg":"请先登录","data":[]}
	 * @return
	 */
	private static boolean analyseResponse(String response) {
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
