package exp.bilibli.plugin.core.back;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.core.front.AppUI;
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

	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	private final static String CHECK_URL = Config.getInstn().CHECK_URL();
	
	private final static String JOIN_URL = Config.getInstn().JOIN_URL();
	
	protected MsgSender() {}
	
	/**
	 * 
	 * @param cookies
	 * @param realRoomId
	 * @return
	 */
	private static Map<String, String> toPostHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = toGetHeadParams(cookies, realRoomId);
		params.put(HttpsUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpsUtils.HEAD.VAL.CONTENT_TYPE_FORM.concat(Config.DEFAULT_CHARSET));	
		return params;
	}
	
	private static Map<String, String> toGetHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpsUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
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
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @return
	 */
	public static boolean sendChat(String msg) {
		String roomId = AppUI.getInstn().getRoomId();
		return sendChat(msg, roomId);
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
			Map<String, String> requestParams = toChatRequestParams(msg, sRoomId, color.CODE());
			String response = HttpsUtils.doPost(CHAT_URL, headParams, requestParams);
			isOk = analyse(response);
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
	private static Map<String, String> toChatRequestParams(
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
	 * FIXME
	 * @param response  {"code":-101,"msg":"请先登录","data":[]}
	 * @return
	 */
	private static boolean analyse(String response) {
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
		}
		return isOk;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean lottery(String roomId) {
		boolean isOk = false;
		String raffleId = checkLottery(roomId, Browser.COOKIES());
		if(StrUtils.isNotEmpty(raffleId)) {
			isOk = joinLottery(roomId, raffleId, Browser.COOKIES());
		}
		return isOk;
	}
	
	public static String checkLottery(String roomId, String cookies) {
		String raffleId = "";
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = toLotteryRequestParams(sRoomId);
			String response = HttpsUtils.doGet(CHECK_URL, headParams, requestParams);
			raffleId = analyseCheck(response);
		}
		return raffleId;
	}
	
	private static Map<String, String> toLotteryRequestParams(String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("roomid", realRoomId);	// 正在抽奖的房间号
		return params;
	}
	
	/**
	 * {
  "code": 0,
  "msg": "success",
  "message": "success",
  "data": [
    {
      "raffleId": 46292,
      "type": "openfire",
      "from": "yiyo............",
      "from_user": {
        "uname": "yiyo............",
        "face": "http://i0.hdslb.com/bfs/face/deca98f5c7eadab74cd4a8013eca41da078ef00d.jpg"
      },
      "time": 54,
      "status": 1
    }
  ]
}
	 * @param response
	 * @return
	 */
	private static String analyseCheck(String response) {
		System.out.println(response);
		String raffleId = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONArray array = JsonUtils.getArray(json, BiliCmdAtrbt.data);
				JSONObject obj = array.getJSONObject(0);
				raffleId = String.valueOf(JsonUtils.getInt(obj, BiliCmdAtrbt.raffleId, 0));
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				UIUtils.log("抽签失败: ", reason);
			}
		} catch(Exception e) {
			UIUtils.log("抽签失败: 服务器无响应");
			e.printStackTrace();
		}
		return raffleId;
	}
	
	
	public static boolean joinLottery(String roomId, String raffleId, String cookies) {
		boolean isOk = false;
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = toLotteryRequestParams(sRoomId, raffleId);
			String response = HttpsUtils.doGet(JOIN_URL, headParams, requestParams);
			isOk = analyseJoin(response);
		}
		return isOk;
	}
	
	private static Map<String, String> toLotteryRequestParams(String realRoomId, String raffleId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("roomid", realRoomId);	// 正在抽奖的房间号
		params.put("raffleId", raffleId);	// 抽奖签号
		return params;
	}
	
	// {"code":0,"msg":"............","message":"............","data":{"23439558":"small","roomid":"40764","5599305":"small","558038":"small","86845000":"small","8582388":"small","4399471":"small","767530":"small","817458":"small","511589":"small","4799261":"small","14475718":"small","27880394":"small","time":55,"2142310":"small","44511314":"small","19729408":"small","19878806":"small","14881238":"small","11655829":"small","34810599":"small","15205065":"small","86515134":"small","23406718":"small","1323899":"small","raffleId":"46292","8536920":"small","31437943":"small","16293951":"small","3834215":"small","31470791":"small","76979807":"small","903874":"small","9008673":"small","face":"http://i0.hdslb.com/bfs/face/deca98f5c7eadab74cd4a8013eca41da078ef00d.jpg","102015208":"small","19123719":"small","20360157":"small","from":"yiyo............","77959868":"small","8042724":"small","7384826":"small","25480252":"small","20177919":"small","40573511":"small","11487015":"small","59812113":"small","13730008":"small","status":2,"31050298":"small","3076423":"small","1698233":"small","7294374":"small","31879634":"small","3392133":"small","type":"openfire","2322836":"small"}}
	private static boolean analyseJoin(String response) {
		boolean isOk = false;
		try {
			System.out.println("en:" + URLEncoder.encode(response, Config.DEFAULT_CHARSET));
			System.out.println("===");
			System.out.println("de:" + URLDecoder.decode(response, Config.DEFAULT_CHARSET));
			
			
			
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				UIUtils.log("抽奖失败: ", reason);
			}
		} catch(Exception e) {
			UIUtils.log("抽奖失败: 服务器无响应");
			e.printStackTrace();
		}
		return isOk;
	}
	
	private static Map<String, String> toHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpsUtils.HEAD.KEY.ACCEPT, "application/json, text/plain, */*");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate");
		params.put(HttpsUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,en,*");
		params.put(HttpsUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpsUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpsUtils.HEAD.KEY.HOST, Config.getInstn().SSL_URL());
		params.put(HttpsUtils.HEAD.KEY.ORIGIN, Config.getInstn().LIVE_URL());
		params.put(HttpsUtils.HEAD.KEY.REFERER, Config.getInstn().LIVE_URL().concat(realRoomId));	// 发送/接收消息的直播间地址
		params.put(HttpsUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
}
