package exp.bilibili.protocol.xhr;

import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class Sign extends __Protocol {

	private final static String SIGN_URL = Config.getInstn().SIGN_URL();
	
	/**
	 * 每日签到
	 * @return
	 */
	public static void toSign(String cookie) {
		int roomId = Config.getInstn().SIGN_ROOM_ID();
		roomId = (roomId <= 0 ? UIUtils.getCurRoomId() : roomId);
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headers = toGetHeadParams(cookie, sRoomId);
			String response = HttpURLUtils.doGet(SIGN_URL, headers, null, Config.DEFAULT_CHARSET);
			_analyseSignResponse(response);
			
		} else {
			log.warn("自动签到失败: 无效的房间号 [{}]", roomId);
		}
	}
	
	private static void _analyseSignResponse(String response) {
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				UIUtils.log("每日签到成功");
				
			} else {
				String errDesc = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				UIUtils.log("每日签到失败: ", errDesc);
			}
		} catch(Exception e) {
			log.error("每日签到失败: {}", response, e);
		}
	}
	
}
