package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 其他协议
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Other extends __XHR {

	/**
	 * 软件授权页(Bilibili-备用)
	 * 	实则上是管理员的个人LINK中心
	 */
	private final static String ADMIN_URL = CryptoUtils.deDES(
			"EECD1D519FEBFDE5EF68693278F5849E8068123647103E9D1644539B452D8DE870DD36BBCFE2C2A8E5A16D58A0CA752D3D715AF120F89F10990A854A386B95631E7C60D1CFD77605");
	
	private final static String BLACK_URL = Config.getInstn().BLACK_URL();
	
	/** 私有化构造函数 */
	protected Other() {}
	
	/**
	 * 获取管理员在B站link中心针对本插件的授权校验标签
	 * @return {"code":0,"msg":"OK","message":"OK","data":["W:M-亚絲娜","B:","T:20180301","V:2.0"]}
	 */
	public static String queryCertificateTags() {
		Map<String, String> header = getHeader();
		return HttpURLUtils.doGet(ADMIN_URL, header, null);
	}
	
	/**
	 * 查询校验标签的请求头
	 * @return
	 */
	private static Map<String, String> getHeader() {
		Map<String, String> header = GET_HEADER("");
		header.put(HttpUtils.HEAD.KEY.HOST, LINK_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_HOME.concat("/p/world/index"));
		return header;
	}
	
	/**
	 * 把用户关小黑屋
	 * @param cookie 房管cookie
	 * @param roomId 
	 * @param username 被关用户的用户名
	 * @param hour
	 * @return
	 */
	public static boolean blockUser(BiliCookie cookie, int roomId, String username, int hour) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), sRoomId);
		Map<String, String> request = getRequest(cookie.CSRF(), sRoomId, username, hour);
		String response = HttpURLUtils.doPost(BLACK_URL, header, request);
		
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
				log.info("直播间 [{}]: 用户 [{}] 被关小黑屋 [{}] 小时", sRoomId, username, hour);
			} else {
				log.info("直播间 [{}]: 把用户 [{}] 关小黑屋失败", sRoomId, username);
			}
		} catch(Exception e) {
			log.info("直播间 [{}]: 把用户 [{}] 关小黑屋异常", sRoomId, username, e);
		}
		return isOk;
	}
	
	private static Map<String, String> getRequest(String csrf, String roomId, String username, int hour) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("roomid", roomId);
		request.put("type", "1");
		request.put("content", username);
		request.put("hour", String.valueOf(hour));
		request.put("token", "");
		request.put("csrf_token", csrf);
		return request;
	}
	
}
