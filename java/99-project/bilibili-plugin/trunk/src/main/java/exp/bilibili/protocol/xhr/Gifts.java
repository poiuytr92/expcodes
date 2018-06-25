package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.bean.xhr.BagGift;
import exp.bilibili.protocol.bean.xhr.Medal;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.envm.HttpHead;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;

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

	/** 查询勋章亲密度URL */
	private final static String MEDAL_URL = Config.getInstn().MEDAL_URL();
	
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
	 * 获取包裹礼物列表
	 * @param cookie
	 * @param roomId
	 * @return
	 */
	public static List<BagGift> queryBagList(BiliCookie cookie, int roomId) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), sRoomId);
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
	public static int querySilver(BiliCookie cookie) {
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
				log.info("[{}] 持有银瓜�?: {}", cookie.NICKNAME(), silver);
				
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
		header.put(HttpHead.KEY.HOST, LIVE_HOST);
		header.put(HttpHead.KEY.ORIGIN, LINK_HOME);
		header.put(HttpHead.KEY.REFERER, LINK_HOME.concat("/p/center/index"));
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
	public static void feed(BiliCookie cookie, int roomId, 
			String upUID, List<BagGift> bagGifts) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), sRoomId);
		Map<String, String> request = _getRequest(cookie, sRoomId, upUID);
		
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
				log.info("[{}] 持有扭蛋�?: {}", cookie.NICKNAME(), coin);
				
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
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), 
				"pages/playground/index?visit_id=".concat(getVisitId()));
		Map<String, String> request = getRequest(cookie.CSRF(), coin);
		String response = HttpURLUtils.doPost(OPEN_CAPSULE_URL, header, request);
		
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONArray text = JsonUtils.getArray(data, BiliCmdAtrbt.text);
				UIUtils.log("[", cookie.NICKNAME(), "] 打开�? [", coin, 
						"] 个扭�?, 获得: ", text.toString());
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
	private static Map<String, String> getRequest(String csrf, int coin) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.type, "normal");
		request.put(BiliCmdAtrbt.count, String.valueOf(coin));
		request.put(BiliCmdAtrbt.csrf_token, csrf);
		return request;
	}
	
	/**
	 * 查询持有的勋章列�?
	 * {"code":0,"msg":"获取成功","data":{"medalCount":20,"count":12,"fansMedalList":[{"id":"571612","uid":1650868,"target_id":14266048,"medal_id":204,"score":210,"level":2,"intimacy":9,"next_intimacy":300,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-05-03 18:49:23","mtime":"2017-07-30 17:42:20","ctime":"2017-05-03 18:49:23","reserve1":"0","reserve2":"","medal_name":"小可�?","master_available":"1","target_name":"","target_face":"","rank":">10�?","medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"小可�?","roomid":"46716","anchorInfo":{"uid":14266048,"uname":"语梨","rank":10000,"mobile_verify":1,"platform_user_level":6,"official_verify":{"type":0,"desc":"bilibili直播签约主播"}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"105454","uid":1650868,"target_id":13173681,"medal_id":470,"score":129642,"level":16,"intimacy":29741,"next_intimacy":50000,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2016-05-19 18:55:56","mtime":"2018-03-08 00:06:41","ctime":"2016-05-19 18:55:56","reserve1":"0","reserve2":"","medal_name":"高达","master_available":"1","target_name":"","target_face":"","rank":1,"medal_color":16746162,"today_feed":"49","day_limit":2000,"todayFeed":"49","dayLimit":2000,"medalName":"高达","roomid":"51108","anchorInfo":{"uid":13173681,"uname":"M斯文败类","rank":10000,"mobile_verify":1,"platform_user_level":4,"official_verify":{"type":-1,"desc":""}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"1096726","uid":1650868,"target_id":803870,"medal_id":1918,"score":109,"level":1,"intimacy":109,"next_intimacy":201,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2018-01-06 22:41:04","mtime":"2018-01-06 22:41:06","ctime":"2018-01-06 22:41:04","reserve1":"0","reserve2":"","medal_name":"狐宝","master_available":"1","target_name":"","target_face":"","rank":">10�?","medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"狐宝","roomid":"70270","anchorInfo":{"uid":803870,"uname":"爱吃橘子の狐�?","rank":10000,"mobile_verify":1,"platform_user_level":5,"official_verify":{"type":0,"desc":"bilibili直播签约主播"}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"571614","uid":1650868,"target_id":116683,"medal_id":2361,"score":30,"level":1,"intimacy":30,"next_intimacy":201,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-05-03 18:50:26","mtime":"2017-06-30 05:04:00","ctime":"2017-05-03 18:50:26","reserve1":"0","reserve2":"","medal_name":"猫酱","master_available":"1","target_name":"","target_face":"","rank":5170,"medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"猫酱","roomid":"5294","anchorInfo":{"uid":116683,"uname":"=咬人�?=","rank":10000,"mobile_verify":1,"platform_user_level":6,"official_verify":{"type":0,"desc":"bilibili 知名舞见"}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"572148","uid":1650868,"target_id":733055,"medal_id":3239,"score":1492,"level":4,"intimacy":491,"next_intimacy":700,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-05-04 00:15:29","mtime":"2017-08-20 22:15:23","ctime":"2017-05-04 00:15:29","reserve1":"0","reserve2":"","medal_name":"璇咕�?","master_available":"1","target_name":"","target_face":"","rank":1059,"medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"璇咕�?","roomid":"482156","anchorInfo":{"uid":733055,"uname":"璇咩","rank":10000,"mobile_verify":1,"platform_user_level":5,"official_verify":{"type":0,"desc":"bilibili直播签约主播"}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"755777","uid":1650868,"target_id":10278125,"medal_id":3365,"score":99,"level":1,"intimacy":99,"next_intimacy":201,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-08-05 16:27:40","mtime":"2017-08-05 16:27:41","ctime":"2017-08-05 16:27:40","reserve1":"0","reserve2":"","medal_name":"猫饼","master_available":"1","target_name":"","target_face":"","rank":3549,"medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"猫饼","roomid":"149608","anchorInfo":{"uid":10278125,"uname":"香菜猫饼","rank":10000,"mobile_verify":1,"platform_user_level":6,"official_verify":{"type":0,"desc":"bilibili直播签约主播"}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"1096677","uid":1650868,"target_id":36330559,"medal_id":3742,"score":2549,"level":5,"intimacy":848,"next_intimacy":1000,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2018-01-06 22:32:29","mtime":"2018-02-15 17:45:45","ctime":"2018-01-06 22:32:29","reserve1":"0","reserve2":"","medal_name":"消嘤�?","master_available":"1","target_name":"","target_face":"","rank":192,"medal_color":5805790,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"消嘤�?","roomid":"847617","anchorInfo":{"uid":36330559,"uname":"鼠二三三","rank":10000,"mobile_verify":1,"platform_user_level":4,"official_verify":{"type":0,"desc":"bilibili直播签约主播\r\n"}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"1055596","uid":1650868,"target_id":915804,"medal_id":3835,"score":131,"level":1,"intimacy":131,"next_intimacy":201,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-12-22 19:40:50","mtime":"2017-12-22 19:42:59","ctime":"2017-12-22 19:40:50","reserve1":"0","reserve2":"","medal_name":"亚丝�?","master_available":"1","target_name":"","target_face":"","rank":719,"medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"亚丝�?","roomid":"521525","anchorInfo":{"uid":915804,"uname":"艾米莉亚EMT","rank":10000,"mobile_verify":1,"platform_user_level":5,"official_verify":{"type":-1,"desc":""}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"571993","uid":1650868,"target_id":6970675,"medal_id":6415,"score":1915,"level":5,"intimacy":214,"next_intimacy":1000,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-05-03 22:40:09","mtime":"2017-06-30 05:04:05","ctime":"2017-05-03 22:40:09","reserve1":"0","reserve2":"","medal_name":"喵侍","master_available":"1","target_name":"","target_face":"","rank":342,"medal_color":5805790,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"喵侍","roomid":"423227","anchorInfo":{"uid":6970675,"uname":"Yuri_喵四","rank":10000,"mobile_verify":1,"platform_user_level":4,"official_verify":{"type":0,"desc":"bilibili直播签约主播\r\n"}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"571606","uid":1650868,"target_id":20872515,"medal_id":8922,"score":154634,"level":17,"intimacy":4733,"next_intimacy":100000,"status":1,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-05-03 18:43:13","mtime":"2018-03-07 23:42:19","ctime":"2017-05-03 18:43:13","reserve1":"0","reserve2":"","medal_name":"翘李�?","master_available":"1","target_name":"","target_face":"","rank":3,"medal_color":16752445,"today_feed":0,"day_limit":3000,"todayFeed":0,"dayLimit":3000,"medalName":"翘李�?","roomid":"390480","anchorInfo":{"uid":20872515,"uname":"苏乔o_o","rank":10000,"mobile_verify":1,"platform_user_level":4,"official_verify":{"type":0,"desc":"bilibili直播签约主播"}},"guard_level":3,"guard_medal_title":"舰长buff：上限提升至150%"},{"id":"1051969","uid":1650868,"target_id":56465669,"medal_id":36374,"score":99,"level":1,"intimacy":99,"next_intimacy":201,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2017-12-20 21:35:10","mtime":"2017-12-20 21:35:55","ctime":"2017-12-20 21:35:10","reserve1":"0","reserve2":"","medal_name":"小雏�?","master_available":"1","target_name":"","target_face":"","rank":593,"medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"小雏�?","roomid":"1942272","anchorInfo":{"uid":56465669,"uname":"宝贝乔w","rank":10000,"mobile_verify":1,"platform_user_level":3,"official_verify":{"type":-1,"desc":""}},"guard_level":0,"guard_medal_title":"未开启加�?"},{"id":"1153657","uid":1650868,"target_id":23658843,"medal_id":43934,"score":99,"level":1,"intimacy":99,"next_intimacy":201,"status":0,"source":1,"receive_channel":1,"is_receive":1,"master_status":0,"receive_time":"2018-02-07 20:07:13","mtime":"2018-02-07 20:07:14","ctime":"2018-02-07 20:07:13","reserve1":"0","reserve2":"","medal_name":"小丶�?","master_available":"1","target_name":"","target_face":"","rank":439,"medal_color":6406234,"today_feed":0,"day_limit":500,"todayFeed":0,"dayLimit":500,"medalName":"小丶�?","roomid":"5450114","anchorInfo":{"uid":23658843,"uname":"小丶琪w","rank":10000,"mobile_verify":1,"platform_user_level":3,"official_verify":{"type":0,"desc":"bilibili直播签约主播"}},"guard_level":0,"guard_medal_title":"未开启加�?"}],"pageinfo":{"totalpages":1,"curPage":1}}}
	 * @param cookie
	 * @return 真实房间�?->勋章信息
	 */
	public static Map<Integer, Medal> queryMedals(BiliCookie cookie) {
		Map<String, String> header = getHeader(cookie.toNVCookie());
		Map<String, String> request = getRequest();
		String response = HttpURLUtils.doGet(MEDAL_URL, header, request);
		
		Map<Integer, Medal> medals = new HashMap<Integer, Medal>();
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONArray fansMedalList = JsonUtils.getArray(data, BiliCmdAtrbt.fansMedalList);
				for(int i = 0; i < fansMedalList.size(); i++) {
					JSONObject fansMedal = fansMedalList.getJSONObject(i);
					Medal medal = new Medal(fansMedal);
					if(medal.getRoomId() > 0) {
						medals.put(medal.getRoomId(), medal);
					}
				}
				log.info("[{}] 持有勋章�?: {}", cookie.NICKNAME(), medals.size());
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("[{}] 查询勋章列表失败: {}", cookie.NICKNAME(), reason);
			}
		} catch(Exception e) {
			log.error("[{}] 查询勋章列表失败: {}", cookie.NICKNAME(), response, e);
		}
		return medals;
	}
	
	/**
	 * 生成查询勋章信息的请求头
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> getHeader(String cookie) {
		Map<String, String> header = GET_HEADER(cookie);
		header.put(HttpHead.KEY.HOST, LIVE_HOST);
		header.put(HttpHead.KEY.ORIGIN, LINK_HOME);
		header.put(HttpHead.KEY.REFERER, LINK_HOME.concat("/p/center/index"));
		return header;
	}
	
	/**
	 * 生成查询勋章信息的请求参�?
	 * @return
	 */
	private static Map<String, String> getRequest() {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.page, "1");
		request.put(BiliCmdAtrbt.pageSize, "50");	// 每页显示的勋章数（B站一般用户最多拥�?20个勋章）
		return request;
	}
	
}
