package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 版聊弹幕/私信消息
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Chat extends __XHR {

	/** 弹幕URL */
	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	/** 私信主机 */
	private final static String MSG_HOME = Config.getInstn().MSG_HOME();
	
	/** 私信URL */
	private final static String MSG_URL = Config.getInstn().MSG_URL();
	
	/** 私有化构造函数 */
	protected Chat() {}
	
	/**
	 * 发送弹幕消息
	 * @param cookie 发送弹幕的账号cookie
	 * @param roomId 目标直播间房号
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @return
	 */
	public static boolean sendDanmu(BiliCookie cookie, int roomId, String msg, ChatColor color) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), sRoomId);
		Map<String, String> request = getRequest(msg, sRoomId, color.RGB());
		String response = HttpURLUtils.doPost(CHAT_URL, header, request);
		return analyse(response);
	}
	
	/**
	 * 弹幕请求参数
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
	 * 发送私信
	 * @param cookie 发送账号的cookie
	 * @param sendId 发送账号的用户ID
	 * @param recvId 接收账号的用户ID
	 * @param msg 发送消息
	 * @return
	 */
	public static boolean sendPM(BiliCookie cookie, String recvId, String msg) {
		Map<String, String> header = getHeader(cookie.toNVCookie());
		Map<String, String> request = getRequest(cookie.CSRF(), cookie.UID(), recvId, msg);
		String response = HttpURLUtils.doPost(MSG_URL, header, request);
		return analyse(response);
	}
	
	/**
	 * 私信头参数
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> getHeader(String cookie) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, LINK_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, MSG_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, MSG_HOME);
		return header;
	}
	
	/**
	 * 私信请求参数
	 * @param csrf
	 * @param sendId
	 * @param recvId
	 * @param msg
	 * @return
	 */
	private static Map<String, String> getRequest(String csrf, 
			String sendId, String recvId, String msg) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("csrf_token", csrf);
		request.put("platform", "pc");
		request.put("msg[sender_uid]", sendId);
		request.put("msg[receiver_id]", recvId);
		request.put("msg[receiver_type]", "1");
		request.put("msg[msg_type]", "1");
		request.put("msg[content]", StrUtils.concat("{\"content\":\"", msg, "\"}"));
		request.put("msg[timestamp]", String.valueOf(System.currentTimeMillis() / 1000));
		return request;
	}
	
	/**
	 * 
	 * @param response  
	 * 		弹幕: {"code":-101,"msg":"请先登录","data":[]}
	 * 		私信: {"code":0,"msg":"ok","message":"ok","data":{"msg_key":6510413634042085687,"_gt_":0}}
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
				log.warn("发送消息失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("发送消息失败: {}", response, e);
		}
		return isOk;
	}
	
}
