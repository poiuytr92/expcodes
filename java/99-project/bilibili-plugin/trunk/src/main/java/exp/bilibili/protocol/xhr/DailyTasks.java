package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.envm.CookieType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.plugin.utils.VercodeUtils;
import exp.bilibili.protocol.bean.other.User;
import exp.bilibili.protocol.bean.xhr.MathTask;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 日常任务
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DailyTasks extends __XHR {

	/** 日常签到URL */
	private final static String SIGN_URL = Config.getInstn().SIGN_URL();
	
	/** 友爱社签到URL */
	private final static String ASSN_URL = Config.getInstn().ASSN_URL();
	
	/** 检查小学数学任务URL */
	private final static String MATH_CHECK_URL = Config.getInstn().MATH_CHECK_URL();
	
	/** 执行小学数学任务URL */
	private final static String MATH_EXEC_URL = Config.getInstn().MATH_EXEC_URL();
	
	/** 获取小学数学任务验证码URL */
	private final static String MATH_CODE_URL = Config.getInstn().MATH_CODE_URL();
	
	/** 小学数学任务验证码图片的下载保存路径 */
	private final static String VERCODE_PATH = Config.getInstn().IMG_DIR().concat("/vercode.png");
	
	/** 小学数学任务重试间隔(验证码计算成功率只有90%左右, 失败后需重试) */
	private final static long SLEEP_TIME = 500L;
	
	/** 模拟PC端在线观看直播的心跳URL */
	private final static String PC_WATCH_URL = Config.getInstn().PC_WATCH_URL();
	
	/** 模拟手机端在线观看直播的心跳URL */
	private final static String APP_WATCH_URL = Config.getInstn().APP_WATCH_URL();
	
	/** 执行下次任务的延迟时间点（5分钟后） */
	private final static long NEXT_TASK_DELAY = 300000L;
	
	/** 私有化构造函数 */
	protected DailyTasks() {}
	
	/**
	 * 友爱社签到
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long toAssn(BiliCookie cookie) {
		Map<String, String> header = getHeader(cookie.toNVCookie());
		Map<String, String> request = getRequest(cookie.CSRF());
		String response = HttpURLUtils.doPost(ASSN_URL, header, request);
		return analyse(response, cookie.NICKNAME(), true);
	}
	
	/**
	 * 有爱社请求头
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
	 * 友爱社请求参数
	 * @param csrf
	 * @return
	 */
	private static Map<String, String> getRequest(String csrf) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.task_id, "double_watch_task");
		request.put(BiliCmdAtrbt.csrf_token, csrf);
		return request;
	}
	
	/**
	 * 每日签到
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long toSign(BiliCookie cookie) {
		String roomId = getRealRoomId();
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), roomId);
		String response = HttpURLUtils.doGet(SIGN_URL, header, null);
		return analyse(response, cookie.NICKNAME(), false);
	}
	
	/**
	 * （友爱社/每日）签到结果解析
	 * @param response  {"code":0,"msg":"","message":"","data":[]}
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	private static long analyse(String response, String username, boolean assn) {
		long nextTaskTime = -1;
		String signType = (assn ? "友爱社" : "每日");
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
			if(code == 0) {
				UIUtils.log("[", username, "] ", signType, "签到完成");
				
			} else if(!reason.contains("已签到") && !reason.contains("已领取")) {
				log.warn("[{}] {}签到失败: {}", username, signType, reason);
				if(!reason.contains("需要绑定手机号")) {
					nextTaskTime = System.currentTimeMillis() + NEXT_TASK_DELAY;
				}
			}
		} catch(Exception e) {
			nextTaskTime = System.currentTimeMillis() + NEXT_TASK_DELAY;
			log.error("[{}] {}签到失败: {}", username, signType, response, e);
		}
		return nextTaskTime;
	}
	
	/**
	 * 模拟PC端在线观看直播 (需5分钟发送一次心跳)
	 * @param cookie
	 * @param roomId
	 * @return 返回执行下次任务的时间点(固定返回5分钟后)
	 */
	public static long toWatchPCLive(BiliCookie cookie, int roomId) {
		Map<String, String> header = POST_HEADER(cookie.toNVCookie(), getRealRoomId(roomId));
		String response = HttpURLUtils.doGet(PC_WATCH_URL, header, null);
		
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				log.info("[{}] 正在模拟PC端在线观看直播...", cookie.NICKNAME());
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.error("[{}] 模拟PC端在线观看直播失败: {}", cookie.NICKNAME(), reason);
			}
		} catch(Exception e) {
			log.error("[{}] 模拟PC端在线观看直播失败: {}", cookie.NICKNAME(), response, e);
		}
		return (System.currentTimeMillis() + NEXT_TASK_DELAY);
	}
	
	public static void main(String[] args) {
		System.out.println(HttpUtils.decodeURL("http%3A%2F%2Flive-play.acgvideo.com%2Flive%2F358%2Flive_20872515_1403835.flv%3FwsSecret%3D2543f09b0ed159cf4e099502519aa36f%26wsTime%3D5a577b50"));
		System.out.println(HttpUtils.decodeURL("http%3A%2F%2Flive-play.acgvideo.com%2Flive%2F414%2Flive_20872515_1403835.flv%3FwsSecret%3D67ddf24003c2ec748b3f0c05e560daed%26wsTime%3D5a578101"));;
		
//		CookiesMgr.getInstn().load(CookieType.VEST);
//		BiliCookie cookie = CookiesMgr.VEST();
//		System.out.println(cookie.toHeaderCookie());
//		
//		long bgnTime = System.currentTimeMillis() / 1000;
//		for(int i = 0 ; i < 10000; i++) {
//			int cnt = (i * 15);
//			toWatchAppLive(cookie, 438, bgnTime, cnt);
//			ThreadUtils.tSleep(15000);
//		}
	}
	
	/**
	 * 模拟手机端在线观看直播 (需5分钟发送一次心跳)
	 * @param cookie
	 * @return 返回执行下次任务的时间点(固定返回5分钟后)
	 */
	public static long toWatchAppLive(BiliCookie cookie, int roomId, long bgnSecond, int cnt) {
		Map<String, String> header = getHeader(cookie, bgnSecond);
		Map<String, String> request = getRequest(cookie.UID(), roomId, cnt);
		System.out.println(request);
		String response = HttpURLUtils.doPost(APP_WATCH_URL, header, request);
		System.out.println(response);
		if("ok".equals(response)) {
			log.info("[{}] 正在模拟手机端在线观看直播...", cookie.NICKNAME());
			
		} else {
			log.error("[{}] 模拟手机端在线观看直播失败: {}", cookie.NICKNAME(), response);
		}
		return (System.currentTimeMillis() + NEXT_TASK_DELAY);
	}
	
	private static Map<String, String> getHeader(BiliCookie cookie, long bgnSecond) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpUtils.HEAD.VAL.POST_FORM.concat(Config.DEFAULT_CHARSET));
		header.put(HttpUtils.HEAD.KEY.COOKIE, cookie.toNVCookie());
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, "Mozilla/5.0 BiliDroid/5.22.1 (bbcallen@gmail.com)");
		header.put(HttpUtils.HEAD.KEY.HOST, "live-trace.bilibili.com");
		header.put("Device-ID", "SilMKRkvHSwfe04rVyseKxNxSH4aKWkLbAJmVSdbOghiVjUEMgMyAzMDMQE2Ag");
		header.put("Buvid", "52EBE497-0DEC-4056-8FD3-FDB2F69690877229infoc");
		header.put("Display-ID", StrUtils.concat(cookie.UID(), "-", bgnSecond));
		return header;
	}
	
	private static Map<String, String> getRequest(String uid, int roomId, int cnt) {
		long now = System.currentTimeMillis();
		String sRoomId = getRealRoomId(roomId);
		User up = Other.queryUpInfo(roomId);
		
		Map<String, String> request = new HashMap<String, String>();
		request.put("c_time", String.valueOf(now));
		request.put("ts", String.valueOf(now / 1000));
		request.put("delta_ts", String.valueOf(cnt));	// 每次调用+15
		request.put("mid", uid);
		request.put("room_id", sRoomId);
		request.put("up_id", up.ID());
		request.put("up_level", String.valueOf(up.LV()));
//		request.put("playurl", "http://live-play.acgvideo.com/live/640/live_20872515_1403835.flv?wsSecret=4c9ded5f264f7f2e881f729dc0e3b11b&wsTime=5a562f29");
//		request.put("guid", "080207a6b4d5dee3f0d18f18e8641ee1");	// 每次打开直播间都随机生成一个固定值
//		request.put("sign", "1cb2e9066b1fe22a94360c0f6dd96fd0");	// 每次变化值
//		request.put("data_behavior_id", "97c3fbc28cbefd7:97c3fbc28cbefd7:0:0");
//		request.put("data_source_id", "system");
		request.put("appkey", "1d8b6e7d45233436");	// 固定值
		request.put("area", "21");	// 每个直播间固定
		request.put("build", "5220001");	// 临时固定值(APP版本号)
		request.put("version", "5.22.1");	// 临时固定值(APP版本号)
		request.put("jumpfrom", "21000");	// 相对固定值（打开直播间的入口， 从我的关注打开）
		request.put("mobi_app", "android");
		request.put("parent_area", "1");	// 固定值
		request.put("pid", "13");	// 固定值
		request.put("platform", "android");
		request.put("play_type", "1");	// 固定值
		return request;
	}

	/**
	 * 执行小学数学任务
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long doMathTask(BiliCookie cookie) {
		long nextTaskTime = -1;
		String roomId = getRealRoomId();
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), roomId);
		
		MathTask task = checkMathTask(header);
		if(task != MathTask.NULL) {
			nextTaskTime = task.getEndTime() * 1000;
			
			// 已到达任务执行时间
			if(nextTaskTime <= System.currentTimeMillis()) {
				if(!doMathTask(header, cookie.NICKNAME(), task)) {
					nextTaskTime = -1;	// 标记不存在下一轮任务
				}
			}
		}
		return nextTaskTime;
	}
	
	/**
	 * 执行小学数学任务
	 * @param header
	 * @param username
	 * @param task
	 * @return 是否存在下一轮任务
	 */
	private static boolean doMathTask(Map<String, String> header, 
			String username, MathTask task) {
		boolean isRedone = false;
		do {
			int answer = 0;
			do {
				ThreadUtils.tSleep(SLEEP_TIME);
				answer = calculateAnswer(header);
			} while(answer <= 0);	// 若解析二维码图片失败, 则重新解析
			
			ThreadUtils.tSleep(SLEEP_TIME);
			isRedone = execMathTask(header, username, task, answer);
		} while(isRedone == true);	// 若计算二维码结果错误, 则重新计算
		
		return task.existNext();
	}
	
	/**
	 * 提取小学数学日常任务
	 * {"code":0,"msg":"","data":{"minute":6,"silver":80,"time_start":1514015075,"time_end":1514015435,"times":3,"max_times":5}}
	 * {"code":0,"msg":"","data":{"minute":9,"silver":190,"time_start":1514036545,"time_end":1514037085,"times":3,"max_times":5}}
	 * @param header
	 * @return
	 */
	private static MathTask checkMathTask(Map<String, String> header) {
		MathTask task = MathTask.NULL;
		String response = HttpURLUtils.doGet(MATH_CHECK_URL, header, null);
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				task = new MathTask(json);
			}
		} catch(Exception e) {
			log.error("获取小学数学任务失败: {}", response, e);
		}
		return task;
	}
	
	/**
	 * 计算验证码图片的小学数学题
	 * @param header
	 * @return
	 */
	private static int calculateAnswer(Map<String, String> header) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("ts", String.valueOf(System.currentTimeMillis()));
		
		boolean isOk = HttpURLUtils.downloadByGet(VERCODE_PATH, MATH_CODE_URL, header, request);
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
	private static boolean execMathTask(Map<String, String> header, 
			String username, MathTask task, int answer) {
		Map<String, String> request = getRequest(task, answer);
		String response = HttpURLUtils.doGet(MATH_EXEC_URL, header, request);
		
		boolean isRedone = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
			if(code == 0) {
				UIUtils.log("[", username, "] 小学数学任务进度: ", task.getCurRound(), "/", 
						task.getMaxRound(), "轮-", task.getStep(), "分钟");
				
			} else if(reason.contains("验证码错误")) {
				isRedone = true;
				
			} else if(reason.contains("未绑定手机")) {
				task.setExistNext(false);	// 标记不存在下一轮任务
			}
		} catch(Exception e) {
			log.error("[{}] 执行小学数学任务失败: {}", username, response, e);
		}
		return isRedone;
	}
	
	/**
	 * 执行小学数学任务请求参数
	 * @param task
	 * @param answer
	 * @return
	 */
	private static Map<String, String> getRequest(MathTask task, int answer) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.time_start, String.valueOf(task.getBgnTime()));
		request.put(BiliCmdAtrbt.end_time, String.valueOf(task.getEndTime()));
		request.put(BiliCmdAtrbt.captcha, String.valueOf(answer));
		return request;
	}
	
}
