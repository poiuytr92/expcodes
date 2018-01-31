package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.HttpCookie;
import exp.bilibili.plugin.envm.Gift;
import exp.bilibili.protocol.bean.xhr.BagGift;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 投喂主播
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Feed extends __XHR {

	private final static String ROOM_URL = Config.getInstn().ROOM_URL();
	
	private final static String BAG_URL = Config.getInstn().BAG_URL();
	
	private final static String FEED_URL = Config.getInstn().FEED_URL();
	
	protected final static String LIVE_HOST = Config.getInstn().LIVE_HOST();
	
	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	/**
	 * 投喂主播
	 * @param cookie 投喂用户cookie
	 * @param roomId 房间号
	 */
	public static void toFeed(HttpCookie cookie, int roomId) {
		String sRoomId = getRealRoomId(roomId);
		String upUID = queryUpUID(sRoomId);
		
		List<BagGift> bagGifts = queryBagList(cookie, sRoomId);
		int silver = querySilver(cookie);
		int giftNum = silver / Gift.HOT_STRIP.COST();
		if(giftNum > 0) {
			BagGift bagGift = new BagGift(Gift.HOT_STRIP.ID(), Gift.HOT_STRIP.NAME(), giftNum);
			bagGifts.add(bagGift);
		}
		
		feed(cookie, sRoomId, upUID, bagGifts);
	}
	
	/**
	 * 查询直播间主播的用户ID
	 * @param roomId
	 * @return
	 */
	private static String queryUpUID(String roomId) {
		Map<String, String> header = GET_HEADER("", roomId);
		Map<String, String> request = _getRequest(roomId);
		String response = HttpURLUtils.doGet(ROOM_URL, header, request);
		
		String uid = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				uid = JsonUtils.getStr(data, BiliCmdAtrbt.uid);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("获取主播ID失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("获取主播ID异常: {}", response, e);
		}
		return uid;
	}
	
	private static Map<String, String> _getRequest(String roomId) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("id", roomId);
		return request;
	}
	
	/**
	 * 获取包裹礼物列表
	 * @param cookie
	 * @param roomId
	 * @return
	 */
	private static List<BagGift> queryBagList(HttpCookie cookie, String roomId) {
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), roomId);
		String response = HttpURLUtils.doGet(BAG_URL, header, null);

		List<BagGift> bagGifts = new LinkedList<BagGift>();
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONArray array = JsonUtils.getArray(data, BiliCmdAtrbt.list);
				for(int i = 0; i < array.size(); i++) {
					BagGift bagGift = new BagGift(array.getJSONObject(i));
					bagGifts.add(bagGift);
				}
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("获取包裹礼物失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("获取包裹礼物异常: {}", response, e);
		}
		return bagGifts;
	}
	
	/**
	 * 查询账户银瓜子数量
	 * @param cookie
	 * @return
	 */
	private static int querySilver(HttpCookie cookie) {
		Map<String, String> headers = _getHeader(cookie.toNVCookie());
		String response = HttpURLUtils.doGet(ACCOUNT_URL, headers, null);

		int silver = 0;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject userCoinIfo = JsonUtils.getObject(data, BiliCmdAtrbt.userCoinIfo);
				silver = JsonUtils.getInt(userCoinIfo, BiliCmdAtrbt.silver, 0);
				log.warn("[{}] 持有银瓜子: {}", cookie.NICKNAME(), silver);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("查询 [{}] 持有银瓜子失败: {}", cookie.NICKNAME(), reason);
			}
		} catch(Exception e) {
			log.error("查询 [{}] 持有银瓜子异常: {}", response, e);
		}
		return silver;
	}
	
	private static Map<String, String> _getHeader(String cookie) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, LIVE_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_HOME.concat("/p/center/index"));
		return header;
	}
	
	/**
	 * 投喂主播
	 * @param cookie
	 * @param roomId
	 * @param upUID
	 * @param bagGifts
	 * @return
	 */
	private static void feed(HttpCookie cookie, String roomId, String upUID, List<BagGift> bagGifts) {
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), roomId);
		Map<String, String> request = _getRequest(cookie, roomId, upUID);
		
		for(BagGift bagGift : bagGifts) {
			request.put("bag_id", bagGift.getBagId());
			request.put("gift_id", bagGift.getGiftId());
			request.put("gift_num", String.valueOf(bagGift.getGiftNum()));
			String response = HttpURLUtils.doPost(FEED_URL, header, request);
			
			try {
				JSONObject json = JSONObject.fromObject(response);
				int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
				if(code == 0) {
					log.info("[{}] 投喂直播间 [{}] 成功: [{}x{}]", cookie.NICKNAME(), roomId, 
							bagGift.getGiftName(), bagGift.getGiftNum());
					
				} else {
					String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
					log.warn("[{}] 投喂直播间 [{}] 失败: {}", cookie.NICKNAME(), roomId, reason);
				}
			} catch(Exception e) {
				log.error("[{}] 投喂直播间 [{}] 异常: {}", cookie.NICKNAME(), roomId, response, e);
			}
		}
	}
	
	private static Map<String, String> _getRequest(
			HttpCookie cookie, String roomId, String upUID) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("uid", cookie.UID());
		request.put("ruid", upUID);
		request.put("platform", "pc");
		request.put("biz_code", "live");
		request.put("biz_id", roomId);
		request.put("rnd", String.valueOf(System.currentTimeMillis() / 1000));
		request.put("storm_beat_id", "0");
		request.put("metadata", "");
		request.put("token", "");
		request.put("csrf_token", cookie.CSRF());
		request.put("coin_type", "silver");
		return request;
	}
	
}
