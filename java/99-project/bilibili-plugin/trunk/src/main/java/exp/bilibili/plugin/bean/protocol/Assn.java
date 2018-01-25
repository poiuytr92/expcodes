package exp.bilibili.plugin.bean.protocol;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Assn extends _MsgSender {

	private final static String ASSN_URL = Config.getInstn().ASSN_URL();
	
	/**
	 * 友爱社签到
	 * @return 是否需要持续测试签到
	 */
	public static boolean toAssn(String cookie, String csrf) {
		Map<String, String> headers = toPostHeadParams(cookie);
		headers.put(HttpUtils.HEAD.KEY.HOST, SSL_HOST);
		headers.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_URL);
		headers.put(HttpUtils.HEAD.KEY.REFERER, LINK_URL.concat("/p/center/index"));
		
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("task_id", "double_watch_task");
		requests.put("csrf_token", csrf);
		
		String response = HttpURLUtils.doPost(ASSN_URL, headers, requests);
		return _analyseAssnResponse(response);
	}
	
	/**
	 * 
	 * @param response  {"code":0,"msg":"","message":"","data":[]}
	 * @return
	 */
	private static boolean _analyseAssnResponse(String response) {
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
