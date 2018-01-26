package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 友爱社
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Assn extends __Protocol {

	/** 友爱社URL */
	private final static String ASSN_URL = Config.getInstn().ASSN_URL();
	
	protected Assn() {}
	
	/**
	 * 友爱社签到
	 * @return 是否需要持续测试签到
	 */
	public static boolean toSign(HttpCookie cookie) {
		Map<String, String> header = getHeader(cookie.toNVCookie());
		Map<String, String> request = getRequest(cookie.CSRF());
		String response = HttpURLUtils.doPost(ASSN_URL, header, request);
		return analyseResponse(response);
	}
	
	private static Map<String, String> getHeader(String cookie) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, SSL_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_URL);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_URL.concat("/p/center/index"));
		return header;
	}
	
	private static Map<String, String> getRequest(String csrf) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("task_id", "double_watch_task");
		request.put("csrf_token", csrf);
		return request;
	}
	
	/**
	 * 
	 * @param response  {"code":0,"msg":"","message":"","data":[]}
	 * @return
	 */
	private static boolean analyseResponse(String response) {
		boolean goOn = true;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				goOn = false;	// 已签到成功，不需要继续签到
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				if(reason.contains("已领取")) {
					goOn = false;	// 已签到过，不需要继续签到
					
				} else {
					log.debug("友爱社签到失败: {}", reason);
				}
			}
		} catch(Exception e) {
			log.error("友爱社签到失败: {}", response, e);
		}
		return goOn;
	}
	
}
