package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 2018春节红包活动
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Redbag extends __XHR {

	private final static String GET_BUCKET_URL = Config.getInstn().GET_BUCKET_URL();
	
	private final static String EX_BUCKET_URL = Config.getInstn().EX_BUCKET_URL();
	
	/** 查询红包奖池URL */
	private final static String GET_REDBAG_URL = Config.getInstn().GET_REDBAG_URL();
	
	/** 兑换红包礼物URL */
	private final static String EX_REDBAG_URL = Config.getInstn().EX_REDBAG_URL();
	
	/** 私有化构造函�? */
	protected Redbag() {}
	
	public static int queryBucketRoomId(BiliCookie cookie) {
		int roomId = 0;
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), "pages/1703/spring-2018.html");
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.underline, String.valueOf(System.currentTimeMillis()));
		String response = HttpURLUtils.doGet(GET_BUCKET_URL, header, request);
		try {
			JSONObject json = JSONObject.fromObject(response);
			JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
			JSONObject bucketRoom = JsonUtils.getObject(data, BiliCmdAtrbt.bucketRoom);
			roomId = JsonUtils.getInt(bucketRoom, BiliCmdAtrbt.roomid, 0);
			
		} catch(Exception e) {
			log.error("查询上上签直播间号失�?: {}", response, e);
		}
		return roomId;
	}
	
	public static long exchangeBucket(BiliCookie cookie, int roomId) {
		long nextTime = 300000L;
		if(roomId <= 0) {
			return nextTime;
		}
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), String.valueOf(roomId));
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.roomid, String.valueOf(roomId));
		String response = HttpURLUtils.doGet(EX_BUCKET_URL, header, request);
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				nextTime = -1;
				UIUtils.log("[", cookie.NICKNAME(), "] 领取上上签完�?");
				
			} else if(code == 402) {
				nextTime = -1;
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("[{}] 领取上上签失�?: {}", cookie.NICKNAME(), reason);
			}
		} catch(Exception e) {
			log.error("[{}] 领取上上签失�?: {}", cookie.NICKNAME(), response, e);
		}
		return nextTime;
	}
	
	/**
	 * 2018春节活动：查询当前红包奖�?
	 * @return {"code":0,"msg":"success","message":"success","data":{"red_bag_num":2290,"round":70,"pool_list":[{"award_id":"guard-3","award_name":"舰长体验券（1个月�?","stock_num":0,"exchange_limit":5,"user_exchange_count":5,"price":6699},{"award_id":"gift-113","award_name":"新春抽奖","stock_num":2,"exchange_limit":0,"user_exchange_count":0,"price":23333},{"award_id":"danmu-gold","award_name":"金色弹幕特权�?1天）","stock_num":19,"exchange_limit":42,"user_exchange_count":42,"price":2233},{"award_id":"uname-gold","award_name":"金色昵称特权�?1天）","stock_num":20,"exchange_limit":42,"user_exchange_count":42,"price":8888},{"award_id":"stuff-2","award_name":"经验曜石","stock_num":0,"exchange_limit":10,"user_exchange_count":10,"price":233},{"award_id":"title-89","award_name":"爆竹头衔","stock_num":0,"exchange_limit":10,"user_exchange_count":10,"price":888},{"award_id":"gift-3","award_name":"B坷垃","stock_num":0,"exchange_limit":1,"user_exchange_count":1,"price":450},{"award_id":"gift-109","award_name":"红灯�?","stock_num":0,"exchange_limit":500,"user_exchange_count":500,"price":15}],"pool":{"award_id":"award-pool","award_name":"刷新兑换�?","stock_num":99999,"exchange_limit":0,"price":6666}}}
	 */
	public static String queryRedbagPool(BiliCookie cookie) {
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), "pages/1703/spring-2018.html");
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.underline, String.valueOf(System.currentTimeMillis()));
		return HttpURLUtils.doGet(GET_REDBAG_URL, header, request);
	}
	
	/**
	 * 2018春节活动：兑换红�?
	 * @param id 奖品编号
	 * @param num 兑换数量
	 * @return 
	 * 	{"code":0,"msg":"OK","message":"OK","data":{"award_id":"stuff-3","red_bag_num":1695}}
	 * 	{"code":-404,"msg":"这个奖品已经兑换完啦，下次再来吧","message":"这个奖品已经兑换完啦，下次再来吧","data":[]}
	 */
	public static String exchangeRedbag(BiliCookie cookie, String id, int num) {
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), "pages/1703/spring-2018.html");
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.award_id, id);
		request.put(BiliCmdAtrbt.exchange_num, String.valueOf(num));
		return HttpURLUtils.doPost(EX_REDBAG_URL, header, request);
	}
	
}
