package exp.bilibli.plugin.core.back;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.bean.ldm.DailyTask;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.bilibli.plugin.envm.ChatColor;
import exp.bilibli.plugin.envm.LotteryType;
import exp.bilibli.plugin.utils.UIUtils;
import exp.bilibli.plugin.utils.VercodeUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

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
	
	private final static long SLEEP_TIME = 1000;
	
	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	private final static String SIGN_URL = Config.getInstn().SIGN_URL();
	
	private final static String CHAT_URL = Config.getInstn().CHAT_URL();
	
	private final static String STORM_CHECK_URL = Config.getInstn().STORM_CHECK_URL();
	
	private final static String STORM_JOIN_URL = Config.getInstn().STORM_JOIN_URL();
	
	private final static String EG_CHECK_URL = Config.getInstn().EG_CHECK_URL();
	
	private final static String EG_JOIN_URL = Config.getInstn().EG_JOIN_URL();
	
	private final static String TV_JOIN_URL = Config.getInstn().TV_JOIN_URL();
	
	private final static String CHECK_TASK_URL = Config.getInstn().CHECK_TASK_URL();
	
	private final static String VERCODE_URL = Config.getInstn().VERCODE_URL();
	
	private final static String DO_TASK_URL = Config.getInstn().DO_TASK_URL();
	
	private final static String VERCODE_PATH = Config.getInstn().IMG_DIR().concat("/vercode.jpg");
	
	/** 已抽奖过的礼物编号集 */
	private final static Set<String> OT_RAFFLEIDS = new HashSet<String>();
	
	/** 私有化构造函数 */
	protected MsgSender() {}
	
	/**
	 * 
	 * @param cookies
	 * @param realRoomId
	 * @return
	 */
	private static Map<String, String> toPostHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpUtils.HEAD.VAL.POST_FORM.concat(Config.DEFAULT_CHARSET));
		params.put(HttpUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpUtils.HEAD.KEY.HOST, Config.getInstn().SSL_URL());
		params.put(HttpUtils.HEAD.KEY.ORIGIN, Config.getInstn().LIVE_URL());
		params.put(HttpUtils.HEAD.KEY.REFERER, Config.getInstn().LIVE_URL().concat(realRoomId));	// 发送/接收消息的直播间地址
		params.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
	private static Map<String, String> toGetHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = toGetHeadParams(cookies);
		params.put(HttpUtils.HEAD.KEY.HOST, Config.getInstn().SSL_URL());
		params.put(HttpUtils.HEAD.KEY.ORIGIN, Config.getInstn().LIVE_URL());
		params.put(HttpUtils.HEAD.KEY.REFERER, Config.getInstn().LIVE_URL().concat(realRoomId));	// 发送/接收消息的直播间地址
		return params;
	}
	
	private static Map<String, String> toGetHeadParams(String cookies) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/plain, */*");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
	/**
	 * 查询账号信息
	 * {"code":0,"status":true,"data":{"level_info":{"current_level":4,"current_min":4500,"current_exp":7480,"next_exp":10800},"bCoins":0,"coins":464,"face":"http:\/\/i2.hdslb.com\/bfs\/face\/bbfd1b5cafe4719e3a57154ac1ff16a9e4d9c6b3.jpg","nameplate_current":"http:\/\/i1.hdslb.com\/bfs\/face\/54f4c31ab9b1f1fa2c29dbbc967f66535699337e.png","pendant_current":"","uname":"M-\u4e9a\u7d72\u5a1c","userStatus":"","vipType":1,"vipStatus":1,"official_verify":-1,"pointBalance":0}}
	 * @return username
	 */
	public static String queryUsername() {
		final String cookies = Browser.COOKIES();
		Map<String, String> headParams = toGetHeadParams(cookies);
		String response = HttpURLUtils.doGet(ACCOUNT_URL, headParams, null, Config.DEFAULT_CHARSET);
		
		String username = "unknow";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				username = JsonUtils.getStr(data, BiliCmdAtrbt.uname);
			}
		} catch(Exception e) {
			log.error("查询账号信息失败: {}", response, e);
		}
		return username;
	}
	
	/**
	 * 自动签到
	 * @return
	 */
	public static void toSign() {
		final String cookies = Browser.COOKIES();
		String roomId = Config.getInstn().SIGN_ROOM_ID();
		roomId = (StrUtils.isEmpty(roomId) ? UIUtils.getCurRoomId() : roomId);
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			String response = HttpURLUtils.doGet(SIGN_URL, headParams, null, Config.DEFAULT_CHARSET);
			_analyseSignResponse(response);
			
		} else {
			log.warn("自动签到失败: 无效的房间号 [{}]", roomId);
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
				UIUtils.log("每日签到失败: ", errDesc);
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
			String response = HttpURLUtils.doPost(CHAT_URL, headParams, requestParams, Config.DEFAULT_CHARSET);
			isOk = _analyseChatResponse(response);
			
		} else {
			log.warn("发送弹幕失败: 无效的房间号 [{}]", roomId);
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
		return joinLottery(TV_JOIN_URL, roomId, raffleId, Browser.COOKIES(), LotteryType.TV);
	}
	
	/**
	 * 节奏风暴抽奖
	 * @param roomId
	 * @return
	 */
	public static String toStormLottery(String roomId, String raffleId) {
		return joinLottery(STORM_JOIN_URL, roomId, raffleId, Browser.COOKIES(), LotteryType.STORM);
	}
	
	/**
	 * 高能礼物抽奖
	 * @param roomId
	 * @return
	 */
	public static int toEgLottery(String roomId) {
		return toLottery(roomId, EG_CHECK_URL, EG_JOIN_URL);
	}
	
	/**
	 * 高能礼物抽奖
	 * @param roomId
	 * @param checkUrl
	 * @param joinUrl
	 * @return
	 */
	private static int toLottery(String roomId, String checkUrl, String joinUrl) {
		int cnt = 0;
		final String cookies = Browser.COOKIES();
		List<String> raffleIds = checkLottery(checkUrl, roomId, cookies);
		
		if(ListUtils.isNotEmpty(raffleIds)) {
			for(String raffleId : raffleIds) {
				
				if(!OT_RAFFLEIDS.contains(raffleIds)) {
					OT_RAFFLEIDS.add(raffleId);
					String errDesc = joinLottery(joinUrl, roomId, raffleId, cookies, LotteryType.OTHER);
					if(StrUtils.isEmpty(errDesc)) {
						cnt++;
					} else {
						if(!errDesc.contains("你已加入抽奖")) {
							UIUtils.statistics("失败(", errDesc, "): 抽奖直播间 [", roomId, "]");
						}
						log.info("参与直播间 [{}] 抽奖失败: {}", roomId, errDesc);
					}
				}
			}
			
			if(OT_RAFFLEIDS.size() > 200) {
				OT_RAFFLEIDS.clear();
			}
		}
		return cnt;
	}
	
	/**
	 * 检查是否存在抽奖
	 * @param url
	 * @param roomId
	 * @param cookies
	 * @return
	 */
	private static List<String> checkLottery(String url, String roomId, String cookies) {
		List<String> raffleIds = new LinkedList<String>();
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = _toLotteryRequestParams(sRoomId);
			String response = HttpURLUtils.doGet(url, headParams, requestParams, Config.DEFAULT_CHARSET);
			raffleIds = _getRaffleId(response);
		} else {
			log.warn("获取礼物编号失败: 无效的房间号 [{}]", roomId);
		}
		return raffleIds;
	}
	
	/**
	 * 加入抽奖
	 * @param url
	 * @param roomId
	 * @param raffleId
	 * @param cookies
	 * @param type
	 * @return
	 */
	private static String joinLottery(String url, String roomId, String raffleId, 
			String cookies, LotteryType type) {
		String errDesc = "";
		int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId > 0) {
			String sRoomId = String.valueOf(realRoomId);
			Map<String, String> headParams = toGetHeadParams(cookies, sRoomId);
			Map<String, String> requestParams = (LotteryType.STORM != type ? 
					_toStormRequestParams(sRoomId, raffleId) : 
					_toLotteryRequestParams(sRoomId, raffleId));
			
			String response = HttpURLUtils.doPost(url, headParams, requestParams, Config.DEFAULT_CHARSET);
			errDesc = _analyseLotteryResponse(response);
			
			// 系统繁忙哟，请再试一下吧
			if(errDesc.contains("系统繁忙")) {
				ThreadUtils.tSleep(1000);
				response = HttpURLUtils.doPost(url, headParams, requestParams, Config.DEFAULT_CHARSET);
				errDesc = _analyseLotteryResponse(response);
			}
		} else {
			log.warn("参加抽奖失败: 无效的房间号 [{}]", roomId);
		}
		return errDesc;
	}
	
	/**
	 * 获取礼物编号
	 * @param response {"code":0,"msg":"success","message":"success","data":[{"raffleId":46506,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46507,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46508,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46509,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1}]}
	 * @return
	 */
	private static List<String> _getRaffleId(String response) {
		List<String> raffleIds = new LinkedList<String>();
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONArray array = JsonUtils.getArray(json, BiliCmdAtrbt.data);
				for(int i = 0; i < array.size(); i++) {
					JSONObject obj = array.getJSONObject(i);
					int raffleId = JsonUtils.getInt(obj, BiliCmdAtrbt.raffleId, 0);
					if(raffleId > 0) {
						raffleIds.add(String.valueOf(raffleId));
					}
				}
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("获取礼物编号失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("获取礼物编号异常: {}", response, e);
		}
		return raffleIds;
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
	
	private static Map<String, String> _toStormRequestParams(String realRoomId, String raffleId) {
		Map<String, String> params = _toLotteryRequestParams(realRoomId);
		params.put("id", raffleId);	// 礼物编号
		params.put("color", ChatColor.WHITE.CODE());
		params.put("captcha_token", "");
		params.put("captcha_phrase", "");
		params.put("token", "");
		params.put("csrf_token", "d15e5fbe89123bf8904a106f75e6527d");
		return params;
	}
	
	/**
	 * 
	 * @param response 
	 *   小电视     {"code":0,"msg":"加入成功","message":"加入成功","data":{"3392133":"small","511589":"small","8536920":"small","raffleId":"46506","1275939":"small","20177919":"small","12768615":"small","1698233":"small","4986301":"small","102015208":"small","40573511":"small","4799261":"small","from":"喵熊°","time":59,"30430088":"small","558038":"small","5599305":"small","8068250":"small","16293951":"small","7294374":"small","type":"openfire","7384826":"small","2229668":"small","7828145":"small","2322836":"small","915804":"small","86845000":"small","3076423":"small","roomid":"97835","5979210":"small","16345975":"small","7151219":"small","1479304":"small","19123719":"small","29129155":"small","7913373":"small","17049098":"small","9008673":"small","23406718":"small","141718":"small","27880394":"small","942837":"small","107844643":"small","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg","31437943":"small","34810599":"small","102994056":"small","31470791":"small","26643554":"small","29080508":"small","14709391":"small","14530810":"small","46520094":"small","2142310":"small","status":2,"77959868":"small","76979807":"small"}}
	 *   节奏风暴 {"code":0,"msg":"","message":"","data":{"gift_id":39,"title":"节奏风暴","content":"<p>你是前 35 位跟风大师<br />恭喜你获得一个亿圆(7天有效期)</p>","mobile_content":"你是前 35 位跟风大师","gift_img":"http://static.hdslb.com/live-static/live-room/images/gift-section/gift-39.png?2017011901","gift_num":1,"gift_name":"亿圆"}}
	 * @return
	 */
	private static String _analyseLotteryResponse(String response) {
		String errDesc = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code != 0) {
				errDesc = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("参加抽奖失败: {}", errDesc);
			}
		} catch(Exception e) {
			log.error("参加抽奖失败: {}", response, e);
		}
		return errDesc;
	}
	
	
	/**
	 * 执行小学数学日常任务
	 * @param roomId
	 * @return 返回下次任务的时间点
	 */
	public static long doDailyTasks() {
		long nextTaskTime = -1;
		final String roomId = UIUtils.getCurRoomId();
		final int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId <= 0) {
			return nextTaskTime;
		}
		final Map<String, String> header = toGetHeadParams(
				Browser.COOKIES(), String.valueOf(realRoomId));
		
		DailyTask task = checkTask(header);
		if(task != DailyTask.NULL) {
			nextTaskTime = task.getEndTime() * 1000;
			
			// 已到达任务执行时间
			if(nextTaskTime <= System.currentTimeMillis()) {
				if(!_doDailyTasks(header, task)) {
					nextTaskTime = -1;	// 标记不存在下一轮任务
				}
			}
		}
		return nextTaskTime;
	}
	
	/**
	 * 执行小学数学日常任务
	 * @param header
	 * @param task
	 * @return 是否存在下一轮任务
	 */
	private static boolean _doDailyTasks(Map<String, String> header, DailyTask task) {
		boolean isDone = false;
		do {
			int answer = 0;
			do {
				ThreadUtils.tSleep(SLEEP_TIME);
				answer = getAnswer(header);
			} while(answer <= 0);	// 若解析二维码图片失败, 则重新解析
			
			ThreadUtils.tSleep(SLEEP_TIME);
			isDone = doTask(header, task, answer);
		} while(!isDone);	// 若计算二维码结果错误, 则重新计算
		
		return task.existNext();
	}
	
	/**
	 * 提取当前的小学数学日常任务
	 * 
	 * {"code":0,"msg":"","data":{"minute":6,"silver":80,"time_start":1514015075,"time_end":1514015435,"times":3,"max_times":5}}
	 * {"code":0,"msg":"","data":{"minute":9,"silver":190,"time_start":1514036545,"time_end":1514037085,"times":3,"max_times":5}}
	 * @param header
	 * @return
	 */
	private static DailyTask checkTask(Map<String, String> header) {
		DailyTask task = DailyTask.NULL;
		String response = HttpURLUtils.doGet(CHECK_TASK_URL, header, null);
		
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				task = new DailyTask(json);
			}
		} catch(Exception e) {
			log.error("获取日常任务失败: {}", response, e);
		}
		return task;
	}
	
	/**
	 * 计算验证码图片的小学数学
	 * @param header
	 * @return
	 */
	private static int getAnswer(Map<String, String> header) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("ts", String.valueOf(System.currentTimeMillis()));
		
		boolean isOk = HttpURLUtils.downloadByGet(VERCODE_PATH, VERCODE_URL, header, request);
		int answer = (isOk ? VercodeUtils.calculateImage(VERCODE_PATH) : 0);
		return answer;
	}
	
	/**
	 * 提交小学数学日常任务
	 * 
	 * {"code":0,"msg":"ok","data":{"silver":7266,"awardSilver":80,"isEnd":0}}
	 * {"code":-902,"msg":"验证码错误","data":[]}
	 * {"code":-903,"msg":"已经领取过这个宝箱","data":{"surplus":-25234082.15}}
	 * 
	 * @param header
	 * @param task
	 * @param answer
	 * @return
	 */
	private static boolean doTask(Map<String, String> header, DailyTask task, int answer) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("time_start", String.valueOf(task.getBgnTime()));
		request.put("end_time", String.valueOf(task.getEndTime()));
		request.put("captcha", String.valueOf(answer));
		String response = HttpURLUtils.doGet(DO_TASK_URL, header, request);
		
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			isOk = (code == 0);
			if(isOk == true) {
				UIUtils.log("已完成小学数学任务: ", task.getCurRound(), "/", 
						task.getMaxRound(), "轮-", task.getStep(), "分钟");
			}
		} catch(Exception e) {
			log.error("执行日常任务失败: {}", response, e);
		}
		return isOk;
	}
	
}
