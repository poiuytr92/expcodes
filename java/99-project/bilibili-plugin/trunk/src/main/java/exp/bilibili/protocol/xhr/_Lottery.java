package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 抽奖协议
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _Lottery extends __Protocol {

	/** 私有化构造函数 */
	protected _Lottery() {}
	
	/**
	 * 加入抽奖
	 * @param url
	 * @param roomId
	 * @param raffleId
	 * @param cookie
	 * @param type
	 * @return
	 */
	protected static String join(LotteryType type, HttpCookie cookie, 
			String url, int roomId, String raffleId) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), sRoomId);
		Map<String, String> request = (LotteryType.STORM == type ? 
				getRequest(sRoomId, raffleId, cookie.CSRF()) : 
				getRequest(sRoomId, raffleId));
		
		String response = HttpURLUtils.doPost(url, header, request);
		String reason = analyse(response);
		
		// 重试一次: [系统繁忙哟，请再试一下吧]
		if(reason.contains("系统繁忙")) {
			ThreadUtils.tSleep(1000);
			response = HttpURLUtils.doPost(url, header, request);
			reason = analyse(response);
		}
		return reason;
	}
	
	/**
	 * 高能抽奖请求参数
	 * @param roomId
	 * @return
	 */
	protected static Map<String, String> getRequest(String roomId) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("roomid", roomId);	// 正在抽奖的房间号
		return request;
	}
	
	/**
	 * 小电视抽奖请求参数
	 * @param roomId
	 * @param raffleId
	 * @return
	 */
	private static Map<String, String> getRequest(String roomId, String raffleId) {
		Map<String, String> request = getRequest(roomId);
		request.put("raffleId", raffleId);	// 礼物编号
		return request;
	}
	
	/**
	 * 节奏风暴抽奖请求参数
	 * @param roomId
	 * @param raffleId
	 * @param csrf
	 * @return
	 */
	private static Map<String, String> getRequest(String roomId, String raffleId, String csrf) {
		Map<String, String> request = getRequest(roomId);
		request.put("id", raffleId);	// 礼物编号
		request.put("color", ChatColor.WHITE.RGB());
		request.put("captcha_token", "");
		request.put("captcha_phrase", "");
		request.put("token", "");
		request.put("csrf_token", csrf);
		return request;
	}
	
	/**
	 * 
	 * @param response 
	 *   小电视     {"code":0,"msg":"加入成功","message":"加入成功","data":{"3392133":"small","511589":"small","8536920":"small","raffleId":"46506","1275939":"small","20177919":"small","12768615":"small","1698233":"small","4986301":"small","102015208":"small","40573511":"small","4799261":"small","from":"喵熊°","time":59,"30430088":"small","558038":"small","5599305":"small","8068250":"small","16293951":"small","7294374":"small","type":"openfire","7384826":"small","2229668":"small","7828145":"small","2322836":"small","915804":"small","86845000":"small","3076423":"small","roomid":"97835","5979210":"small","16345975":"small","7151219":"small","1479304":"small","19123719":"small","29129155":"small","7913373":"small","17049098":"small","9008673":"small","23406718":"small","141718":"small","27880394":"small","942837":"small","107844643":"small","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg","31437943":"small","34810599":"small","102994056":"small","31470791":"small","26643554":"small","29080508":"small","14709391":"small","14530810":"small","46520094":"small","2142310":"small","status":2,"77959868":"small","76979807":"small"}}
	 *   节奏风暴 {"code":0,"msg":"","message":"","data":{"gift_id":39,"title":"节奏风暴","content":"<p>你是前 35 位跟风大师<br />恭喜你获得一个亿圆(7天有效期)</p>","mobile_content":"你是前 35 位跟风大师","gift_img":"http://static.hdslb.com/live-static/live-room/images/gift-section/gift-39.png?2017011901","gift_num":1,"gift_name":"亿圆"}}
	 * @return
	 */
	private static String analyse(String response) {
		String reason = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code != 0) {
				reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				
				reason = StrUtils.isEmpty(reason) ? "unknow" : reason;
//				log.warn("参加抽奖失败: {}", reason);	// FIXME: 节奏风暴抽奖失败时， 原因为空
				log.warn("参加抽奖失败: {}", response);
			}
		} catch(Exception e) {
			log.error("参加抽奖失败: {}", response, e);
		}
		return reason;
	}
	
}
