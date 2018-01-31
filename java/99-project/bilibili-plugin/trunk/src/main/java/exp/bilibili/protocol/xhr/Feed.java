package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.Gift;
import exp.bilibili.protocol.bean.BagGift;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

// FIXME
public class Feed extends __XHR {

	private final static String UID_URL = "http://api.live.bilibili.com/room/v1/Room/room_init";
	
	private final static String BAG_URL = "http://api.live.bilibili.com/gift/v2/gift/bag_list";
	
	private final static String FEED_URL = "http://api.live.bilibili.com/gift/v2/live/bag_send";
	
	/** SSL服务器主机 */
	protected final static String LIVE_HOST = Config.getInstn().LIVE_HOST();
	
	/** 查询账号信息URL */
	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	/**
	 * 
	 * @param cookie
	 * @param roomId
	 */
	public static void toFeed(HttpCookie cookie, int roomId) {
		String sRoomId = getRealRoomId(roomId);
		String upUID = queryUpliveUID(sRoomId);
		
		List<BagGift> bagGifts = queryBagList(cookie, sRoomId);
		int silver = querySilver(cookie);
		int giftNum = silver / Gift.HOT_STRIP.COST();
		if(giftNum > 0) {
			BagGift bagGift = new BagGift(Gift.HOT_STRIP.ID(), Gift.HOT_STRIP.NAME(), giftNum);
			bagGifts.add(bagGift);
		}
		
		feed(cookie, sRoomId, upUID, bagGifts);
	}
	
	private static String queryUpliveUID(String roomId) {
		Map<String, String> header = GET_HEADER("", roomId);
		Map<String, String> request = new HashMap<String, String>();
		request.put("id", roomId);
		String response = HttpURLUtils.doGet(UID_URL, header, request);
		// {"code":0,"msg":"ok","message":"ok","data":{"room_id":51108,"short_id":0,"uid":13173681,"need_p2p":0,"is_hidden":false,"is_locked":false,"is_portrait":false,"live_status":0,"hidden_till":0,"lock_till":0,"encrypted":false,"pwd_verified":false}}
		
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
	
	private static List<BagGift> queryBagList(HttpCookie cookie, String roomId) {
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), roomId);
		String response = HttpURLUtils.doGet(BAG_URL, header, null);
		// {"code":0,"msg":"success","message":"success","data":{"list":[{"bag_id":60043379,"gift_id":1,"gift_name":"辣条","gift_num":8,"gift_type":0,"expire_at":1519833600},{"bag_id":17041846,"gift_id":3,"gift_name":"B坷垃","gift_num":5,"gift_type":0,"expire_at":0}],"time":1517324236}}

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
	
	private static int querySilver(HttpCookie cookie) {
		Map<String, String> headers = getHeader(cookie.toNVCookie());
		String response = HttpURLUtils.doGet(ACCOUNT_URL, headers, null);
		// {"code":0,"msg":"\u83b7\u53d6\u6210\u529f","data":{"achieves":0,"userInfo":{"uid":247058029,"uname":"qpfh1185","face":"https:\/\/static.hdslb.com\/images\/member\/noface.gif","rank":5000,"identification":0,"mobile_verify":0,"platform_user_level":0,"official_verify":{"type":-1,"desc":""}},"roomid":false,"userCoinIfo":{"uid":247058029,"vip":0,"vip_time":"0000-00-00 00:00:00","svip":0,"svip_time":"0000-00-00 00:00:00","cost":"8800","rcost":"0","user_score":"3000","silver":"0","gold":"0","iap_gold":0,"face":"https:\/\/static.hdslb.com\/images\/member\/noface.gif","uname":"qpfh1185","platform_user_level":0,"score":0,"master_level":{"level":1,"current":[0,0],"next":[50,50]},"user_current_score":11800,"user_level":0,"user_next_level":1,"user_intimacy":11800,"user_next_intimacy":100000,"user_level_rank":">50000","bili_coins":0,"coins":0},"vipViewStatus":true,"discount":false,"svip_endtime":0,"vip_endtime":0,"year_price":233000,"month_price":20000,"action":"index","liveTime":0,"master":{"level":1,"current":0,"next":50,"medalInfo":null},"san":12,"count":{"guard":false,"fansMedal":0,"title":0,"achieve":0}}}

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
	
	private static Map<String, String> getHeader(String cookie) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, LIVE_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_HOME.concat("/p/center/index"));
		return header;
	}
	
	private static boolean feed(HttpCookie cookie, String roomId, String upUID, List<BagGift> bagGifts) {
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), roomId);
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
		
		for(BagGift bagGift : bagGifts) {
			request.put("bag_id", bagGift.getBagId());
			request.put("gift_id", bagGift.getGiftId());
			request.put("gift_num", String.valueOf(bagGift.getGiftNum()));
			String response = HttpURLUtils.doPost(FEED_URL, header, request);
			// {"code":0,"msg":"success","message":"success","data":{"tid":"247058029@15173267098104729866","uid":247058029,"uname":"qpfh1185","ruid":20872515,"rcost":3177637,"gift_id":1,"gift_type":0,"gift_name":"辣条","gift_num":78,"gift_action":"喂食","gift_price":100,"coin_type":"silver","total_coin":7800,"metadata":"","fulltext":null,"rnd":"1517326708","effect_block":1,"extra":{"gift_bag":{"bag_id":60502126,"gift_num":0},"top_list":[],"follow":null,"medal":null,"title":null,"event":{"event_score":0},"capsule":{"normal":{"coin":0,"change":0,"progress":{"now":8000,"max":10000}},"colorful":{"coin":0,"change":0,"progress":{"now":0,"max":5000}}}},"gift_effect":{"super":0,"broadcast_msg_list":[],"small_tv_list":[],"beat_storm":null}}}
			
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
		return true;
	}
	
}
