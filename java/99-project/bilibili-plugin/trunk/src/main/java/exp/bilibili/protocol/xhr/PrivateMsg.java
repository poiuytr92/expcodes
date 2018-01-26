package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.protocol.bean.HttpCookie;
import exp.bilibili.protocol.bean.HttpCookies;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class PrivateMsg extends _MsgSender {

	private final static String MSG_HOST = Config.getInstn().MSG_HOST();
	
	private final static String MSG_URL = Config.getInstn().MSG_URL();
	
	/**
	 * 发送私信
	 * @param sendId 发送账号的用户ID
	 * @param recvId 接收账号的用户ID
	 * @param msg 发送消息
	 * @return
	 */
	public static boolean sendPrivateMsg(String sendId, String recvId, String msg) {
		HttpCookie cookie = HttpCookies.INSTN().MAIN();
		
		Map<String, String> headers = toPostHeadParams(cookie.toNVCookie());
		headers.put(HttpUtils.HEAD.KEY.HOST, LINK_HOST);
		headers.put(HttpUtils.HEAD.KEY.ORIGIN, MSG_HOST);
		headers.put(HttpUtils.HEAD.KEY.REFERER, MSG_HOST);
		
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("csrf_token", cookie.CSRF());
		requests.put("platform", "pc");
		requests.put("msg[sender_uid]", sendId);
		requests.put("msg[receiver_id]", recvId);
		requests.put("msg[receiver_type]", "1");
		requests.put("msg[msg_type]", "1");
		requests.put("msg[content]", StrUtils.concat("{\"content\":\"", msg, "\"}"));
		requests.put("msg[timestamp]", String.valueOf(System.currentTimeMillis() / 1000));
		
		String response = HttpURLUtils.doPost(MSG_URL, headers, requests);
		return _analyseMsgResponse(response);
	}
	
	/**
	 * 
	 * @param response  {"code":0,"msg":"ok","message":"ok","data":{"msg_key":6510413634042085687,"_gt_":0}}
	 * @return
	 */
	private static boolean _analyseMsgResponse(String response) {
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("发送私信失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("发送私信失败: {}", response, e);
		}
		return isOk;
	}
	
}
