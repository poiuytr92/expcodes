package exp.bilibli.plugin.core.back;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.bilibli.plugin.envm.ChatColor;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpsUtils;

/**
 * <PRE>
 * B站直播版聊消息发送器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MsgSender {

	private final static Logger log = LoggerFactory.getLogger(MsgSender.class);
	
	private final static String SIGN_URL = Config.getInstn().SIGN_URL();
	
	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	private final static String EG_CHECK_URL = Config.getInstn().EG_CHECK_URL();
	
	private final static String EG_JOIN_URL = Config.getInstn().EG_JOIN_URL();
	
	private final static String TV_JOIN_URL = Config.getInstn().TV_JOIN_URL();
	
	protected MsgSender() {}
	
	/**
	 * 
	 * @param cookies
	 * @param realRoomId
	 * @return
	 */
	private static Map<String, String> toPostHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpsUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpsUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpsUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpsUtils.HEAD.VAL.POST_FORM.concat(Config.DEFAULT_CHARSET));
		params.put(HttpsUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpsUtils.HEAD.KEY.HOST, Config.getInstn().SSL_URL());
		params.put(HttpsUtils.HEAD.KEY.ORIGIN, Config.getInstn().LIVE_URL());
		params.put(HttpsUtils.HEAD.KEY.REFERER, Config.getInstn().LIVE_URL().concat(realRoomId));	// 发送/接收消息的直播间地址
		params.put(HttpsUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
	private static Map<String, String> toGetHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpsUtils.HEAD.KEY.ACCEPT, "application/json, text/plain, */*");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpsUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpsUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpsUtils.HEAD.KEY.HOST, Config.getInstn().SSL_URL());
		params.put(HttpsUtils.HEAD.KEY.ORIGIN, Config.getInstn().LIVE_URL());
		params.put(HttpsUtils.HEAD.KEY.REFERER, Config.getInstn().LIVE_URL().concat(realRoomId));	// 发送/接收消息的直播间地址
		params.put(HttpsUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
	/**
	 * 自动签到
	 * @return
	 */
	public static void toSign() {
		final String cookies = Browser.COOKIES();
		final String roomId = UIUtils.getCurRoomId();
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			String response = HttpsUtils.doGet(SIGN_URL, headParams, null, Config.DEFAULT_CHARSET);
			_analyseSignResponse(response);
			
		} else {
			log.warn("自动签到失败: 无效的房间号");
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
				log.warn("每日签到失败: {}", errDesc);
			}
		} catch(Exception e) {
			log.error("每日签到失败: {}", response, e);
		}
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @return
	 */
	public static boolean sendChat(String msg) {
		final String roomId = UIUtils.getCurRoomId();
		return sendChat(msg, roomId);
	}
	
	/**
	 * 
	 * @param msg
	 * @param color
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color) {
		final String roomId = UIUtils.getCurRoomId();
		return sendChat(msg, color, roomId);
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param roomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, String roomId) {
		return sendChat(msg, ChatColor.WHITE, roomId);
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @param roomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color, String roomId) {
		return sendChat(msg, color, roomId, Browser.COOKIES());
	}

	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @param roomId 目标直播间房号
	 * @param cookies 发送用户的cookies
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color, 
			String roomId, String cookies) {
		boolean isOk = false;
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toPostHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = _toChatRequestParams(msg, sRoomId, color.CODE());
			String response = HttpsUtils.doPost(CHAT_URL, headParams, requestParams, Config.DEFAULT_CHARSET);
			isOk = _analyseChatResponse(response);
		}
		return isOk;
	}
	
	/**
	 * 
	 * @param msg
	 * @param realRoomId
	 * @param chatColor
	 * @return
	 */
	private static Map<String, String> _toChatRequestParams(
			String msg, String realRoomId, String color) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("rnd", String.valueOf(System.currentTimeMillis() / 1000));	// 时间戳
		params.put("msg", msg);		// 弹幕内容
		params.put("color", color);	// 弹幕颜色
		params.put("roomid", realRoomId);	// 接收消息的房间号
		params.put("fontsize", "25");
		params.put("mode", "1");
		return params;
	}
	
	/**
	 * 
	 * @param response  {"code":-101,"msg":"请先登录","data":[]}
	 * @return
	 */
	private static boolean _analyseChatResponse(String response) {
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				UIUtils.log("发送弹幕失败: ", reason);
			}
		} catch(Exception e) {
			UIUtils.log("发送弹幕失败: 服务器无响应");
			log.error("发送弹幕失败: {}", response, e);
		}
		return isOk;
	}
	
	/**
	 * 小电视抽奖
	 * @param roomId
	 * @param raffleId
	 * @return
	 */
	public static String toTvLottery(String roomId, String raffleId) {
		return joinLottery(TV_JOIN_URL, roomId, raffleId, Browser.COOKIES());
	}
	
	/**
	 * 高能礼物抽奖
	 * @param roomId
	 * @return
	 */
	public static String toEgLottery(String roomId) {
		String errDesc = "";
		final String cookies = Browser.COOKIES();
		String raffleId = checkLottery(EG_CHECK_URL, roomId, cookies);
		if(StrUtils.isNotEmpty(raffleId)) {
			errDesc = joinLottery(EG_JOIN_URL, roomId, raffleId, cookies);
		} else {
			errDesc = "已超时";	// 提取礼物编号失败
		}
		return errDesc;
	}
	
	/**
	 * 
	 * @param url
	 * @param roomId
	 * @param cookies
	 * @return
	 */
	private static String checkLottery(String url, String roomId, String cookies) {
		String raffleId = "";
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = _toLotteryRequestParams(sRoomId);
			String response = HttpsUtils.doGet(url, headParams, requestParams, Config.DEFAULT_CHARSET);
			raffleId = _getRaffleId(response);
		}
		return raffleId;
	}
	
	/**
	 * 
	 * @param url
	 * @param roomId
	 * @param raffleId
	 * @param cookies
	 * @return
	 */
	private static String joinLottery(String url, String roomId, String raffleId, String cookies) {
		String errDesc = "";
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = _toLotteryRequestParams(sRoomId, raffleId);
			String response = HttpsUtils.doGet(url, headParams, requestParams, Config.DEFAULT_CHARSET);
			errDesc = _analyseLotteryResponse(response);
		}
		return errDesc;
	}
	
	/**
	 * 
	 * @param response {"code":0,"msg":"success","message":"success","data":[{"raffleId":46506,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46507,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46508,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46509,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1}]}
	 * @return
	 */
	private static String _getRaffleId(String response) {
		String raffleId = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				Object data = json.get(BiliCmdAtrbt.data);
				JSONObject obj = null;
				if(data instanceof JSONArray) {
					obj = ((JSONArray) data).getJSONObject(0);
				} else {
					obj = (JSONObject) data;
				}
				
				raffleId = String.valueOf(JsonUtils.getInt(obj, BiliCmdAtrbt.raffleId, 0));
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("获取礼物编号失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("获取礼物编号异常: {}", response, e);
		}
		return raffleId;
	}
	
	private static Map<String, String> _toLotteryRequestParams(String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("roomid", realRoomId);	// 正在抽奖的房间号
		return params;
	}
	
	private static Map<String, String> _toLotteryRequestParams(String realRoomId, String raffleId) {
		Map<String, String> params = _toLotteryRequestParams(realRoomId);
		params.put("raffleId", raffleId);	// 礼物编号
		return params;
	}
	
	/**
	 * 
	 * @param response {"code":0,"msg":"加入成功","message":"加入成功","data":{"3392133":"small","511589":"small","8536920":"small","raffleId":"46506","1275939":"small","20177919":"small","12768615":"small","1698233":"small","4986301":"small","102015208":"small","40573511":"small","4799261":"small","from":"喵熊°","time":59,"30430088":"small","558038":"small","5599305":"small","8068250":"small","16293951":"small","7294374":"small","type":"openfire","7384826":"small","2229668":"small","7828145":"small","2322836":"small","915804":"small","86845000":"small","3076423":"small","roomid":"97835","5979210":"small","16345975":"small","7151219":"small","1479304":"small","19123719":"small","29129155":"small","7913373":"small","17049098":"small","9008673":"small","23406718":"small","141718":"small","27880394":"small","942837":"small","107844643":"small","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg","31437943":"small","34810599":"small","102994056":"small","31470791":"small","26643554":"small","29080508":"small","14709391":"small","14530810":"small","46520094":"small","2142310":"small","status":2,"77959868":"small","76979807":"small"}}
	 * @return
	 */
	private static String _analyseLotteryResponse(String response) {
		String errDesc = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code != 0) {
				errDesc = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("参与抽奖失败: {}", errDesc);
			}
		} catch(Exception e) {
			log.error("参与抽奖失败: {}", response, e);
		}
		return errDesc;
	}
	
}
