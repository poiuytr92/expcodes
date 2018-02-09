package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.envm.Danmu;
import exp.bilibili.protocol.bean.other.User;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 其他协议
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Other extends __XHR {

	/**
	 * 软件授权页(Bilibili-备用)
	 * 	实则上是管理员的个人LINK中心
	 */
	private final static String ADMIN_URL = CryptoUtils.deDES(
			"EECD1D519FEBFDE5EF68693278F5849E8068123647103E9D1644539B452D8DE870DD36BBCFE2C2A8E5A16D58A0CA752D3D715AF120F89F10990A854A386B95631E7C60D1CFD77605");
	
	/** 查询账号信息URL */
	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	/** 查询账号安全信息URL */
	private final static String SAFE_URL = Config.getInstn().SAFE_URL();
	
	/** 查询房间信息URL */
	private final static String ROOM_URL = Config.getInstn().ROOM_URL();
	
	/** 查询账号在特定房间内的信息URL */
	private final static String PLAYER_URL = Config.getInstn().PLAYER_URL();
	
	/** 查询房管列表URL */
	private final static String MANAGE_URL = Config.getInstn().MANAGE_URL();
	
	/** 小黑屋URL */
	private final static String BLACK_URL = Config.getInstn().BLACK_URL();
	
	/** 私有化构造函数 */
	protected Other() {}
	
	/**
	 * 获取管理员在B站link中心针对本插件的授权校验标签
	 * @return {"code":0,"msg":"OK","message":"OK","data":["W:M-亚絲娜","B:","T:20180301","V:2.0"]}
	 */
	public static String queryCertificateTags() {
		Map<String, String> header = getHeader();
		return HttpURLUtils.doGet(ADMIN_URL, header, null);
	}
	
	/**
	 * 查询校验标签的请求头
	 * @return
	 */
	private static Map<String, String> getHeader() {
		Map<String, String> header = GET_HEADER("");
		header.put(HttpUtils.HEAD.KEY.HOST, LINK_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_HOME.concat("/p/world/index"));
		return header;
	}
	
	/**
	 * 查询账号信息(并写入cookie内)
	 *	 主要用于检测账号id、昵称、是否为老爷
	 * {"code":0,"msg":"\u83b7\u53d6\u6210\u529f","data":{"achieves":960,"userInfo":{"uid":1650868,"uname":"M-\u4e9a\u7d72\u5a1c","face":"https:\/\/i1.hdslb.com\/bfs\/face\/bbfd1b5cafe4719e3a57154ac1ff16a9e4d9c6b3.jpg","rank":10000,"identification":1,"mobile_verify":1,"platform_user_level":4,"official_verify":{"type":-1,"desc":""}},"roomid":"269706","userCoinIfo":{"uid":1650868,"vip":1,"vip_time":"2018-12-12 21:56:04","svip":1,"svip_time":"2018-12-06 21:56:04","cost":63781395,"rcost":2481900,"user_score":440323260,"silver":"29902","gold":"72009","iap_gold":0,"score":24819,"master_level":{"level":10,"current":[6300,18060],"next":[9100,27160]},"user_current_score":504104655,"user_level":45,"user_next_level":46,"user_intimacy":4104655,"user_next_intimacy":50000000,"user_level_rank":4325,"bili_coins":0,"coins":475},"vipViewStatus":false,"discount":false,"svip_endtime":"2018-12-06","vip_endtime":"2018-12-12","year_price":233000,"month_price":20000,"action":"index","liveTime":0,"master":{"level":10,"current":6759,"next":9100,"medalInfo":{"id":"25072","uid":"1650868","medal_name":"\u795e\u624b","live_status":"1","master_status":"1","status":1,"reason":"0","last_rename_time":"0","time_able_change":0,"rename_status":1,"charge_num":50,"coin_num":20,"platform_status":"2"}},"san":12,"count":{"guard":2,"fansMedal":11,"title":24,"achieve":0}}}
	 * @param cookie
	 * @return 
	 */
	public static boolean queryUserInfo(BiliCookie cookie) {
		Map<String, String> header = getHeader(cookie.toNVCookie());
		String response = HttpURLUtils.doGet(ACCOUNT_URL, header, null);
		
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject userCoinIfo = JsonUtils.getObject(data, BiliCmdAtrbt.userCoinIfo);
				String uid = JsonUtils.getStr(userCoinIfo, BiliCmdAtrbt.uid);
				String username = JsonUtils.getStr(userCoinIfo, BiliCmdAtrbt.uname);
				int vip = JsonUtils.getInt(userCoinIfo, BiliCmdAtrbt.vip, 0);	// 月费老爷
				int svip = JsonUtils.getInt(userCoinIfo, BiliCmdAtrbt.svip, 0);	// 年费老爷
				
				cookie.setUid(uid);
				cookie.setNickName(username);
				cookie.setVip(vip + svip > 0);
			}
		} catch(Exception e) {
			log.error("查询账号信息异常: {}", response, e);
		}
		return cookie.isVaild();
	}
	
	/**
	 * 生成查询用户信息的请求头
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> getHeader(String cookie) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, LIVE_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_HOME.concat("/p/center/index"));
		return header;
	}
	
	/**
	 * 查询账号安全信息(并写入cookie内)
	 *  主要用于检测账号是否已绑定手机
	 *  {"code":0,"data":{"safe_question":0,"hide_email":"272****@qq.com","hide_tel_phone":"139*****412","safe_rank":{"score":80,"level":2,"bind_tel":1,"bind_email":1,"email_veri":1,"tel_veri":1,"real_name":1,"pwd_level":3},"aso_account_sns":{"sina_bind":2,"qq_bind":2},"skipVerify":false}}
	 * @param cookie
	 * @return
	 */
	public static boolean queryUserSafeInfo(BiliCookie cookie) {
		Map<String, String> header = GET_HEADER(cookie.toNVCookie());
		String response = HttpURLUtils.doGet(SAFE_URL, header, null);
		
		boolean isOk = true;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject safeRank = JsonUtils.getObject(data, BiliCmdAtrbt.safe_rank);
				int bindTel = JsonUtils.getInt(safeRank, BiliCmdAtrbt.bind_tel, -1);
				cookie.setBindTel(bindTel > 0);
			}
		} catch(Exception e) {
			isOk = false;
			log.error("查询账号安全信息异常: {}", response, e);
		}
		return isOk;
	}
	
	/**
	 * 查询用户在指定房间内的授权信息(并写入cookie内)
	 * 	主要检测是否为房管、是否为老爷（弹幕长度上限临时+10）、是否为提督/总督老爷（弹幕长度上限临时+10）
	 * @param cookie
	 * @param roomId
	 * @return
	 */
	public static boolean queryUserAuthorityInfo(BiliCookie cookie, int roomId) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), sRoomId);
		Map<String, String> request = getRequest(sRoomId);
		String response = HttpURLUtils.doGet(PLAYER_URL, header, request);
		
		boolean isOk = true;
		try {
			String xml = StrUtils.concat("<root>", response, "</root>");
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			int isAdmin = NumUtils.toInt(root.elementTextTrim(BiliCmdAtrbt.isadmin), 0); // 房管
//			int vip = NumUtils.toInt(root.elementTextTrim(BiliCmdAtrbt.vip), 0); // 老爷(此值不准)
			int danmuLen = NumUtils.toInt(root.elementTextTrim(BiliCmdAtrbt.msg_length), Danmu.LEN); // 弹幕长度
			
			cookie.setRoomAdmin(isAdmin > 0);
			cookie.setVip(danmuLen >= Danmu.LEN_VIP);
			cookie.setGuard(danmuLen >= Danmu.LEN_GUARD);
			
		} catch(Exception e) {
			isOk = false;
			log.error("查询账号授权信息异常: {}", response, e);
		}
		return isOk;
	}
	
	/**
	 * 查询用户在指定房间内的授权信息的请求参数
	 * @param roomId
	 * @return
	 */
	private static Map<String, String> getRequest(String roomId) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.id, BiliCmdAtrbt.cid.concat(roomId));
		request.put(BiliCmdAtrbt.ts, BODHUtils.decToHex(System.currentTimeMillis()));
		request.put(BiliCmdAtrbt.platform, "pc");
		request.put(BiliCmdAtrbt.player_type, "web");
		return request;
	}
	
	/**
	 * 查询直播间主播的用户信息
	 * @param roomId
	 * @return 主播的用户信息
	 */
	public static User queryUpInfo(int roomId) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = GET_HEADER("", sRoomId);
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.roomid, sRoomId);
		
		String response = HttpURLUtils.doGet(ROOM_URL, header, request);
		User up = User.NULL;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject info = JsonUtils.getObject(data, BiliCmdAtrbt.info);
				String uid = JsonUtils.getStr(info, BiliCmdAtrbt.uid);
				String uname = JsonUtils.getStr(info, BiliCmdAtrbt.uname);
				up = new User(uid, uname);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("获取主播ID失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("获取主播ID异常: {}", response, e);
		}
		return up;
	}
	
	/**
	 * 查询直播间的房管（含主播）
	 * @param roomId 直播间ID
	 * @return 房管列表
	 */
	public static Set<User> queryManagers(int roomId) {
		Set<User> managers = new HashSet<User>();
		
		// 查询主播昵称
		User up = queryUpInfo(roomId);
		if(up != User.NULL) {
			managers.add(up);
		}
		
		// 查询房管列表
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = GET_HEADER("", sRoomId);
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.anchor_id, up.ID());
		String response = HttpURLUtils.doGet(MANAGE_URL, header, request);
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONArray data = JsonUtils.getArray(json, BiliCmdAtrbt.data);
				for(int i = 0; i < data.size(); i++) {
					JSONObject userinfo = JsonUtils.getObject(data.getJSONObject(i), BiliCmdAtrbt.userinfo);
					String uid = JsonUtils.getStr(userinfo, BiliCmdAtrbt.uid);
					String uname = JsonUtils.getStr(userinfo, BiliCmdAtrbt.uname);
					if(StrUtils.isNotEmpty(uid, uname)) {
						managers.add(new User(uid, uname));
					}
				}
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("查询房管列表失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("查询房管列表异常: {}", response, e);
		}
		return managers;
	}
	
	/**
	 * 把用户关小黑屋
	 * @param cookie 房管cookie
	 * @param roomId 
	 * @param username 被关用户的用户名
	 * @param hour
	 * @return
	 */
	public static boolean blockUser(BiliCookie cookie, int roomId, String username, int hour) {
		String sRoomId = getRealRoomId(roomId);
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), sRoomId);
		Map<String, String> request = getRequest(cookie.CSRF(), sRoomId, username, hour);
		String response = HttpURLUtils.doPost(BLACK_URL, header, request);
		
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
				log.info("直播间 [{}]: 用户 [{}] 被关小黑屋 [{}] 小时", sRoomId, username, hour);
			} else {
				log.info("直播间 [{}]: 把用户 [{}] 关小黑屋失败", sRoomId, username);
			}
		} catch(Exception e) {
			log.info("直播间 [{}]: 把用户 [{}] 关小黑屋异常", sRoomId, username, e);
		}
		return isOk;
	}
	
	/**
	 * 关小黑屋的请求参数
	 * @param csrf
	 * @param roomId
	 * @param username
	 * @param hour
	 * @return
	 */
	private static Map<String, String> getRequest(String csrf, String roomId, String username, int hour) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.roomid, roomId);
		request.put(BiliCmdAtrbt.type, "1");
		request.put(BiliCmdAtrbt.content, username);
		request.put(BiliCmdAtrbt.hour, String.valueOf(hour));
		request.put(BiliCmdAtrbt.token, "");
		request.put(BiliCmdAtrbt.csrf_token, csrf);
		return request;
	}
	
}
