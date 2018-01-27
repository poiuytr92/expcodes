package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpClient;

/**
 * <PRE>
 * 节奏风暴抽奖
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LotteryStorm extends _Lottery {

	/** 扫描每个房间的间隔(风险行为， 频率需要控制，太快可能被查出来，太慢成功率太低) */
	private final static long SCAN_INTERVAL = 50;
	
	private final static String STORM_CHECK_URL = Config.getInstn().STORM_CHECK_URL();
	
	private final static String STORM_JOIN_URL = Config.getInstn().STORM_JOIN_URL();
	
	/** 最上一次抽奖过的节奏风暴编号(礼物编号是递增的) */
	private static int LAST_STORMID = 0;
	
	/**
	 * 扫描并加入节奏风暴
	 */
	public static int toDo(List<Integer> roomIds) {
		int sum = 0;
		HttpClient client = new HttpClient();
		Map<String, String> requests = new HashMap<String, String>();
		for(Integer roomId : roomIds) {
			String sRoomId = String.valueOf(roomId);
			requests.put("roomid", sRoomId);
			Map<String, String> headers = GET_HEADER(
					CookiesMgr.INSTN().VEST().toNVCookie(), sRoomId);
			
			int cnt = 0;
			do {
				cnt = 0;
				String response = client.doGet(STORM_CHECK_URL, headers, requests);
				List<String> raffleIds = getStormIds(roomId, response);
				cnt = join(roomId, raffleIds);
				sum += cnt;
			} while(cnt > 0);	// 对于存在节奏风暴的房间, 再扫描一次(可能有人连续送节奏风暴)
			ThreadUtils.tSleep(SCAN_INTERVAL);
		}
		client.close();
		return sum;
	}
	
	/**
	 * 获取节奏风暴的礼物ID
	 * @param roomId
	 * @param response {"code":0,"msg":"","message":"","data":{"id":157283,"roomid":2717660,"num":100,"time":50,"content":"康康胖胖哒……！","hasJoin":0}}
	 * @return
	 */
	private static List<String> getStormIds(int roomId, String response) {
		List<String> raffleIds = new LinkedList<String>();
		try {
			JSONObject json = JSONObject.fromObject(response);
			Object data = json.get(BiliCmdAtrbt.data);
			if(data instanceof JSONObject) {
				JSONObject room = (JSONObject) data;
				int raffleId = JsonUtils.getInt(room, BiliCmdAtrbt.id, 0);
				if(raffleId > LAST_STORMID) {
					LAST_STORMID = raffleId;
					raffleIds.add(String.valueOf(raffleId));
				}
						
			} else if(data instanceof JSONArray) {
				JSONArray array = (JSONArray) data;
				for(int i = 0 ; i < array.size(); i++) {
					JSONObject room = array.getJSONObject(i);
					int raffleId = JsonUtils.getInt(room, BiliCmdAtrbt.id, 0);
					if(raffleId > LAST_STORMID) {
						LAST_STORMID = raffleId;
						raffleIds.add(String.valueOf(raffleId));
					}
				}
			}
		} catch(Exception e) {
			log.error("提取直播间 [{}] 的节奏风暴信息失败: {}", roomId, response, e);
		}
		return raffleIds;
	}
	
	/**
	 * 加入节奏风暴抽奖
	 * @param roomId
	 * @param raffleIds
	 * @return
	 */
	private static int join(int roomId, List<String> raffleIds) {
		int cnt = 0;
		if(raffleIds.size() > 0) {
			String msg = StrUtils.concat("直播间 [", roomId, 
					"] 开启了节奏风暴 [x", raffleIds.size(), "] !!!");
			UIUtils.notify(msg);
			
			for(String raffleId : raffleIds) {
				cnt += (toDo(roomId, raffleId) ? 1 : 0);
			}
		}
		return cnt;
	}
	
	/**
	 * 节奏风暴抽奖  // FIXME
	 * @param roomId
	 * @return
	 */
	public static boolean toDo(int roomId, String raffleId) {
		boolean isOk = true;
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			
			String errDesc = join(LotteryType.STORM, cookie, STORM_JOIN_URL, roomId, raffleId);
			if(StrUtils.isEmpty(errDesc)) {
				log.info("参与直播间 [{}] 抽奖成功", roomId);
				UIUtils.statistics("成功(节奏风暴): 抽奖直播间 [", roomId, "]");
				UIUtils.updateLotteryCnt();
				
			} else {
				log.info("参与直播间 [{}] 抽奖失败: {}", roomId, errDesc);
				UIUtils.statistics("失败(", errDesc, "): 抽奖直播间 [", roomId, "]");
				isOk = false;
			}
		}
		return isOk;
	}
	
}
