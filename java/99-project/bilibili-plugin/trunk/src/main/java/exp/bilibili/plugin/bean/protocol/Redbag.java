package exp.bilibili.plugin.bean.protocol;

import java.util.HashMap;
import java.util.Map;

import exp.bilibili.plugin.Config;
import exp.libs.warp.net.http.HttpURLUtils;

public class Redbag extends _MsgSender {

	private final static String GET_REDBAG_URL = Config.getInstn().GET_REDBAG_URL();
	
	private final static String EX_REDBAG_URL = Config.getInstn().EX_REDBAG_URL();
	
	/**
	 * 2018春节活动：查询当前红包奖池
	 * @return {"code":0,"msg":"success","message":"success","data":{"red_bag_num":2290,"round":70,"pool_list":[{"award_id":"guard-3","award_name":"舰长体验券（1个月）","stock_num":0,"exchange_limit":5,"user_exchange_count":5,"price":6699},{"award_id":"gift-113","award_name":"新春抽奖","stock_num":2,"exchange_limit":0,"user_exchange_count":0,"price":23333},{"award_id":"danmu-gold","award_name":"金色弹幕特权（1天）","stock_num":19,"exchange_limit":42,"user_exchange_count":42,"price":2233},{"award_id":"uname-gold","award_name":"金色昵称特权（1天）","stock_num":20,"exchange_limit":42,"user_exchange_count":42,"price":8888},{"award_id":"stuff-2","award_name":"经验曜石","stock_num":0,"exchange_limit":10,"user_exchange_count":10,"price":233},{"award_id":"title-89","award_name":"爆竹头衔","stock_num":0,"exchange_limit":10,"user_exchange_count":10,"price":888},{"award_id":"gift-3","award_name":"B坷垃","stock_num":0,"exchange_limit":1,"user_exchange_count":1,"price":450},{"award_id":"gift-109","award_name":"红灯笼","stock_num":0,"exchange_limit":500,"user_exchange_count":500,"price":15}],"pool":{"award_id":"award-pool","award_name":"刷新兑换池","stock_num":99999,"exchange_limit":0,"price":6666}}}
	 */
	public static String queryRedbagPool(String cookie) {
		Map<String, String> headers = toGetHeadParams(cookie, "pages/1703/spring-2018.html");
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("_", String.valueOf(System.currentTimeMillis()));
		String response = HttpURLUtils.doGet(GET_REDBAG_URL, headers, requests);
		return response;
	}
	
	/**
	 * 2018春节活动：兑换红包
	 * @param id 奖品编号
	 * @param num 兑换数量
	 * @return 
	 * 	{"code":0,"msg":"OK","message":"OK","data":{"award_id":"stuff-3","red_bag_num":1695}}
	 * 	{"code":-404,"msg":"这个奖品已经兑换完啦，下次再来吧","message":"这个奖品已经兑换完啦，下次再来吧","data":[]}
	 */
	public static String exchangeRedbag(String cookie, String id, int num) {
		Map<String, String> headers = toPostHeadParams(cookie, "pages/1703/spring-2018.html");
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("award_id", id);
		requests.put("exchange_num", String.valueOf(num));
		String response = HttpURLUtils.doPost(EX_REDBAG_URL, headers, requests);
		return response;
	}
	
}
