package exp.bilibili.protocol.xhr;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class EnergyLottery extends _Lottery {
	
	private final static String EG_CHECK_URL = Config.getInstn().EG_CHECK_URL();
	
	private final static String EG_JOIN_URL = Config.getInstn().EG_JOIN_URL();
	
	/** 最上一次抽奖过的礼物编号(礼物编号是递增的) */
	private static int LAST_RAFFLEID = 0;
	
	/**
	 * 高能礼物抽奖
	 * @param roomId
	 * @return
	 */
	public static int toEgLottery(String cookie, int roomId) {
		return toLottery(cookie, "", roomId, EG_CHECK_URL, EG_JOIN_URL);
	}
	
	/**
	 * 高能礼物抽奖
	 * @param roomId
	 * @param checkUrl
	 * @param joinUrl
	 * @return
	 */
	private static int toLottery(String cookie, String csrf, int roomId, String checkUrl, String joinUrl) {
		int cnt = 0;
		List<String> raffleIds = checkLottery(checkUrl, roomId, cookie);
		
		if(ListUtils.isNotEmpty(raffleIds)) {
			for(String raffleId : raffleIds) {
				int id = NumUtils.toInt(raffleId, 0);
				if(id > LAST_RAFFLEID) {	// 礼物编号是递增
					LAST_RAFFLEID = id;
					String errDesc = joinLottery(
							joinUrl, roomId, raffleId, cookie, csrf, LotteryType.OTHER);
					if(StrUtils.isEmpty(errDesc)) {
						cnt++;
					} else {
						if(!errDesc.contains("你已加入抽奖")) {
							UIUtils.statistics("失败(", errDesc, "): 抽奖直播间 [", roomId, "]");
						}
						log.info("参与直播间 [{}] 抽奖失败: {}", roomId, errDesc);
					}
				}
			}
		}
		return cnt;
	}
	
	/**
	 * 检查是否存在抽奖
	 * @param url
	 * @param roomId
	 * @param cookie
	 * @return
	 */
	private static List<String> checkLottery(String url, int roomId, String cookie) {
		List<String> raffleIds = new LinkedList<String>();
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headers = toGetHeadParams(cookie, sRoomId);
			Map<String, String> requests = _toLotteryRequestParams(sRoomId);
			String response = HttpURLUtils.doGet(url, headers, requests, Config.DEFAULT_CHARSET);
			raffleIds = _getRaffleId(response);
		} else {
			log.warn("获取礼物编号失败: 无效的房间号 [{}]", roomId);
		}
		return raffleIds;
	}
	
	
	
	/**
	 * 获取礼物编号
	 * @param response {"code":0,"msg":"success","message":"success","data":[{"raffleId":46506,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46507,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46508,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46509,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1}]}
	 * @return
	 */
	private static List<String> _getRaffleId(String response) {
		List<String> raffleIds = new LinkedList<String>();
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONArray array = JsonUtils.getArray(json, BiliCmdAtrbt.data);
				for(int i = 0; i < array.size(); i++) {
					JSONObject obj = array.getJSONObject(i);
					int raffleId = JsonUtils.getInt(obj, BiliCmdAtrbt.raffleId, 0);
					if(raffleId > 0) {
						raffleIds.add(String.valueOf(raffleId));
					}
				}
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("获取礼物编号失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("获取礼物编号异常: {}", response, e);
		}
		return raffleIds;
	}
	
}
