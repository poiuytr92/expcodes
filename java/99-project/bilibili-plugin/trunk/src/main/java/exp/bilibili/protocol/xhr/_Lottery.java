package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.plugin.envm.LotteryType;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class _Lottery extends _MsgSender {

	/**
	 * 加入抽奖
	 * @param url
	 * @param roomId
	 * @param raffleId
	 * @param cookie
	 * @param type
	 * @return
	 */
	protected static String joinLottery(String url, int roomId, String raffleId, 
			String cookie, String csrf, LotteryType type) {
		String errDesc = "";
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headers = toGetHeadParams(cookie, sRoomId);
			Map<String, String> requests = (LotteryType.STORM == type ? 
					_toStormRequestParams(sRoomId, raffleId, csrf) : 
					_toLotteryRequestParams(sRoomId, raffleId));
			
			String response = HttpURLUtils.doPost(url, headers, requests, Config.DEFAULT_CHARSET);
			errDesc = _analyseLotteryResponse(response);
			
			// 系统繁忙哟，请再试一下吧
			if(errDesc.contains("系统繁忙")) {
				ThreadUtils.tSleep(1000);
				response = HttpURLUtils.doPost(url, headers, requests, Config.DEFAULT_CHARSET);
				errDesc = _analyseLotteryResponse(response);
			}
		} else {
			log.warn("参加抽奖失败: 无效的房间号 [{}]", roomId);
		}
		return errDesc;
	}
	
	protected static Map<String, String> _toLotteryRequestParams(String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("roomid", realRoomId);	// 正在抽奖的房间号
		return params;
	}
	
	private static Map<String, String> _toLotteryRequestParams(String realRoomId, String raffleId) {
		Map<String, String> params = _toLotteryRequestParams(realRoomId);
		params.put("raffleId", raffleId);	// 礼物编号
		return params;
	}
	
	private static Map<String, String> _toStormRequestParams(String realRoomId, String raffleId, String csrf) {
		Map<String, String> params = _toLotteryRequestParams(realRoomId);
		params.put("id", raffleId);	// 礼物编号
		params.put("color", ChatColor.WHITE.CODE());
		params.put("captcha_token", "");
		params.put("captcha_phrase", "");
		params.put("token", "");
		params.put("csrf_token", csrf);
		return params;
	}
	
	/**
	 * 
	 * @param response 
	 *   小电视     {"code":0,"msg":"加入成功","message":"加入成功","data":{"3392133":"small","511589":"small","8536920":"small","raffleId":"46506","1275939":"small","20177919":"small","12768615":"small","1698233":"small","4986301":"small","102015208":"small","40573511":"small","4799261":"small","from":"喵熊°","time":59,"30430088":"small","558038":"small","5599305":"small","8068250":"small","16293951":"small","7294374":"small","type":"openfire","7384826":"small","2229668":"small","7828145":"small","2322836":"small","915804":"small","86845000":"small","3076423":"small","roomid":"97835","5979210":"small","16345975":"small","7151219":"small","1479304":"small","19123719":"small","29129155":"small","7913373":"small","17049098":"small","9008673":"small","23406718":"small","141718":"small","27880394":"small","942837":"small","107844643":"small","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg","31437943":"small","34810599":"small","102994056":"small","31470791":"small","26643554":"small","29080508":"small","14709391":"small","14530810":"small","46520094":"small","2142310":"small","status":2,"77959868":"small","76979807":"small"}}
	 *   节奏风暴 {"code":0,"msg":"","message":"","data":{"gift_id":39,"title":"节奏风暴","content":"<p>你是前 35 位跟风大师<br />恭喜你获得一个亿圆(7天有效期)</p>","mobile_content":"你是前 35 位跟风大师","gift_img":"http://static.hdslb.com/live-static/live-room/images/gift-section/gift-39.png?2017011901","gift_num":1,"gift_name":"亿圆"}}
	 * @return
	 */
	private static String _analyseLotteryResponse(String response) {
		String errDesc = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code != 0) {
				errDesc = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("参加抽奖失败: {}", errDesc);
			}
		} catch(Exception e) {
			log.error("参加抽奖失败: {}", response, e);
		}
		return errDesc;
	}
	
}
