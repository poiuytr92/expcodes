package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.envm.Gift;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.bean.other.User;
import exp.bilibili.protocol.bean.xhr.BagGift;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 礼物协议
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Gifts extends __XHR {

	/** 查询包裹礼物列表URL */
	private final static String BAG_URL = Config.getInstn().BAG_URL();
	
	/** 投喂URL */
	private final static String FEED_URL = Config.getInstn().FEED_URL();
	
	/** 查询账号信息URL */
	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	/** 查询扭蛋币URL */
	private final static String GET_CAPSULE_URL = Config.getInstn().GET_CAPSULE_URL();
	
	/** 打开扭蛋URL */
	private final static String OPEN_CAPSULE_URL = Config.getInstn().OPEN_CAPSULE_URL();
	
	/**
	 * 投喂主播
	 * @param cookie 投喂用户cookie
	 * @param roomId 主播所在房间号
	 */
	public static void toFeed(BiliCookie cookie, int roomId) {
		String sRoomId = getRealRoomId(roomId);
		User up = Other.queryUpInfo(roomId);
		
		List<BagGift> bagGifts = queryBagList(cookie, sRoomId);
		int silver = querySilver(cookie);
		int giftNum = silver / Gift.HOT_STRIP.COST();
		if(giftNum > 0) {
			BagGift bagGift = new BagGift(Gift.HOT_STRIP.ID(), Gift.HOT_STRIP.NAME(), giftNum);
			bagGifts.add(bagGift);
		}
		
		feed(cookie, sRoomId, up.ID(), bagGifts);
	}
	
	/**
	 * 获取包裹礼物列表
	 * @param cookie
	 * @param roomId
	 * @return
	 */
	private static List<BagGift> queryBagList(BiliCookie cookie, String roomId) {
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
	 * 查询账户银瓜子数�?
	 * @param cookie
	 * @return
	 */
	private static int querySilver(BiliCookie cookie) {
		Map<String, String> header = _getHeader(cookie.toNVCookie());
		String response = HttpURLUtils.doGet(ACCOUNT_URL, header, null);

		int silver = 0;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject userCoinIfo = JsonUtils.getObject(data, BiliCmdAtrbt.userCoinIfo);
				silver = JsonUtils.getInt(userCoinIfo, BiliCmdAtrbt.silver, 0);
				log.warn("[{}] 持有银瓜�?: {}", cookie.NICKNAME(), silver);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("查询 [{}] 持有银瓜子失�?: {}", cookie.NICKNAME(), reason);
			}
		} catch(Exception e) {
			log.error("查询 [{}] 持有银瓜子异�?: {}", response, e);
		}
		return silver;
	}
	
	/**
	 * 查询银瓜子的请求�?
	 * @param cookie
	 * @return
	 */
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
	private static void feed(BiliCookie cookie, String roomId, String upUID, List<BagGift> bagGifts) {
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), roomId);
		Map<String, String> request = _getRequest(cookie, roomId, upUID);
		
		for(BagGift bagGift : bagGifts) {
			request.put(BiliCmdAtrbt.bag_id, bagGift.getBagId());
			request.put(BiliCmdAtrbt.gift_id, bagGift.getGiftId());
			request.put(BiliCmdAtrbt.gift_num, String.valueOf(bagGift.getGiftNum()));
			String response = HttpURLUtils.doPost(FEED_URL, header, request);
			
			try {
				JSONObject json = JSONObject.fromObject(response);
				int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
				if(code == 0) {
					UIUtils.log("[", cookie.NICKNAME(), "] 已投喂直播间 [", roomId, 
							"] 礼物: [", bagGift.getGiftName(), "x", bagGift.getGiftNum(), "]");
				} else {
					String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
					log.warn("[{}] 投喂直播�? [{}] 失败: {}", cookie.NICKNAME(), roomId, reason);
				}
			} catch(Exception e) {
				log.error("[{}] 投喂直播�? [{}] 异常: {}", cookie.NICKNAME(), roomId, response, e);
			}
		}
	}
	
	/**
	 * 投喂主播的请求参�?
	 * @param cookie
	 * @param roomId
	 * @param upUID
	 * @return
	 */
	private static Map<String, String> _getRequest(
			BiliCookie cookie, String roomId, String upUID) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.uid, cookie.UID());
		request.put(BiliCmdAtrbt.ruid, upUID);
		request.put(BiliCmdAtrbt.platform, "pc");
		request.put(BiliCmdAtrbt.biz_code, "live");
		request.put(BiliCmdAtrbt.biz_id, roomId);
		request.put(BiliCmdAtrbt.rnd, String.valueOf(System.currentTimeMillis() / 1000));
		request.put(BiliCmdAtrbt.storm_beat_id, "0");
		request.put(BiliCmdAtrbt.metadata, "");
		request.put(BiliCmdAtrbt.token, "");
		request.put(BiliCmdAtrbt.csrf_token, cookie.CSRF());
		request.put(BiliCmdAtrbt.coin_type, "silver");	// 银瓜�?
		return request;
	}
	
	/**
	 * 查询持有扭蛋�?(仅普通扭蛋机)
	 * {"code":0,"msg":"OK","data":{"normal":{"status":true,"coin":241,"change":0,"progress":{"now":3974,"max":10000},"rule":"使用价值累计达�?10000瓜子的礼物（包含直接使用瓜子购买、道具包裹，但不包括产生梦幻扭蛋币的活动道具），可以获得1枚扭蛋币。使用扭蛋币可以参与抽奖�?","gift":[{"id":"22","name":"经验曜石"},{"id":"21","name":"经验原石"},{"id":"35","name":"秋田君头�?"},{"id":"36","name":"小红�?"},{"id":"30","name":"爱心便当"},{"id":"b","name":"小号小电�?"},{"id":"4","name":"蓝白胖次"},{"id":"3","name":"B坷垃"},{"id":"2","name":"亿圆"},{"id":"1","name":"辣条"}],"list":[{"num":"1","gift":"经验原石","date":"2018-02-25","name":"日常脱宅"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"我不喝开�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"我不喝开�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"4528514"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"世纪佳宇"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"婊气女孩101"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"世纪佳宇"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"世纪佳宇"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"杉生有杏"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"淑杉压力�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"阿骑�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"影儿の辣鸡欧尼酱"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"刃无�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"小祖宗杨�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"小祖宗杨�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"林渊的菠萝宇宙无敌甜"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"影儿の辣鸡欧尼酱"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"影儿の辣鸡欧尼酱"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"影儿の辣鸡欧尼酱"},{"num":"1","gift":"经验曜石","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"南羽靑璃"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"不离青莲"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"范佳�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"潜不了水的水�?"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"晚风与鱼"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"熊の骚年"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"杉生有杏"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"爱儿的花"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"微尘呀"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"听说名字只能取这么长"}]},"colorful":{"status":true,"coin":2,"change":0,"progress":{"now":0,"max":5000},"rule":"详细规则请前往<a href=\"http:\/\/live.bilibili.com\/pages\/1703\/spring-2018.html\" target=\"_blank\">活动页面<\/a>","gift":[{"id":"n","name":"提督体验"},{"id":"k","name":"舰长体验"},{"id":"23","name":"贤者之�?"},{"id":"22","name":"经验曜石"},{"id":"21","name":"经验原石"},{"id":"35","name":"秋田君头�?"},{"id":"36","name":"小红�?"},{"id":"3","name":"B坷垃"},{"id":"11","name":"喵娘"},{"id":"2","name":"亿圆"}],"list":[{"num":"1","gift":"亿圆","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"100","gift":"小红�?","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"1","gift":"经验曜石","date":"2018-02-25","name":"刃无�?"},{"num":"1","gift":"秋田君头�?","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"50","gift":"小红�?","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"100","gift":"小红�?","date":"2018-02-25","name":"顾寒的穆穆女票ALex"},{"num":"2","gift":"亿圆","date":"2018-02-25","name":"不正常的游宅"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"200","gift":"小红�?","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"50","gift":"小红�?","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"50","gift":"小红�?","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"50","gift":"小红�?","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"亿圆","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"1","gift":"经验原石","date":"2018-02-25","name":"亲鱼啾啾"},{"num":"100","gift":"小红�?","date":"2018-02-25","name":"亲鱼啾啾"}]}}}
	 * @param cookie
	 * @return 扭蛋币个�?
	 */
	public static int queryCapsuleCoin(BiliCookie cookie) {
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), "pages/playground/index");
		String response = HttpURLUtils.doGet(GET_CAPSULE_URL, header, null);
		
		int coin = 0;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject normal = JsonUtils.getObject(data, BiliCmdAtrbt.normal);
				coin = JsonUtils.getInt(normal, BiliCmdAtrbt.coin, 0);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("[{}] 查询持有扭蛋币失�?: {}", cookie.NICKNAME(), reason);
			}
		} catch(Exception e) {
			log.error("[{}] 查询持有扭蛋币异�?: {}", cookie.NICKNAME(), response, e);
		}
		return coin;
	}
	
	/**
	 * 打开扭蛋(仅普通扭蛋机)
	 * {"code":0,"msg":"OK","data":{"status":true,"text":["小红�? X 8","辣条 X 1","经验原石 X 1"],"info":{"normal":{"coin":27,"change":0,"progress":{"now":9300,"max":10000}},"colorful":{"coin":0,"change":0,"progress":{"now":0,"max":5000}}},"showTitle":"","isEntity":false}}
	 * @param cookie
	 * @param coin 扭蛋币数量（一个扭蛋币扭一�?, 可选值为 1/10/100�?
	 * @return
	 */
	public static boolean openCapsuleCoin(BiliCookie cookie, int coin) {
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), "pages/playground/index");
		Map<String, String> request = getRequest(coin);
		String response = HttpURLUtils.doPost(OPEN_CAPSULE_URL, header, request);
		
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONArray text = JsonUtils.getArray(data, BiliCmdAtrbt.text);
				
				String msg = StrUtils.concat("[", cookie.NICKNAME(), "] 打开�? [", 
						coin, "] 个扭�?, 获得: ", text.toString());
				UIUtils.log(msg);
				log.info(msg);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("[{}] 打开 [{}] 个扭蛋失�?: {}", cookie.NICKNAME(), coin, reason);
			}
		} catch(Exception e) {
			log.error("[{}] 打开 [{}] 个扭蛋异�?: {}", cookie.NICKNAME(), coin, response, e);
		}
		return isOk;
	}
	
	/**
	 * 打开扭蛋请求参数
	 * @param coin
	 * @return
	 */
	private static Map<String, String> getRequest(int coin) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.type, "normal");
		request.put(BiliCmdAtrbt.count, String.valueOf(coin));
		request.put(BiliCmdAtrbt.token, "");
		return request;
	}
	
}
