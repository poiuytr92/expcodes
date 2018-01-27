package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.plugin.utils.VercodeUtils;
import exp.bilibili.protocol.bean.MathTask;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
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
public class DailyTasks extends __Protocol {

	/** 日常签到URL */
	private final static String SIGN_URL = Config.getInstn().SIGN_URL();
	
	/** 友爱社签到URL */
	private final static String ASSN_URL = Config.getInstn().ASSN_URL();
	
	/** 检查小学数学任务URL */
	private final static String CHECK_TASK_URL = Config.getInstn().CHECK_TASK_URL();
	
	/** 执行小学数学任务URL */
	private final static String DO_TASK_URL = Config.getInstn().DO_TASK_URL();
	
	/** 小学数学验证码URL */
	private final static String VERCODE_URL = Config.getInstn().VERCODE_URL();
	
	/** 小学数学验证码下载路径 */
	private final static String VERCODE_PATH = Config.getInstn().IMG_DIR().concat("/vercode.jpg");
	
	/** 小学数学验证码重试间隔 */
	private final static long SLEEP_TIME = 500;
	
	/**
	 * 友爱社签到
	 * @return 是否需要持续测试签到
	 */
	public static boolean toAssn(HttpCookie cookie) {
		Map<String, String> header = getHeader(cookie.toNVCookie());
		Map<String, String> request = getRequest(cookie.CSRF());
		String response = HttpURLUtils.doPost(ASSN_URL, header, request);
		return !analyse(response);
	}
	
	/**
	 * 有爱社请求头
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> getHeader(String cookie) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, SSL_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_URL);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_URL.concat("/p/center/index"));
		return header;
	}
	
	/**
	 * 有爱社请求参数
	 * @param csrf
	 * @return
	 */
	private static Map<String, String> getRequest(String csrf) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("task_id", "double_watch_task");
		request.put("csrf_token", csrf);
		return request;
	}
	
	/**
	 * 每日签到
	 * @return 是否签到成功
	 */
	public static void toSign(HttpCookie cookie, int roomId) {
		roomId = RoomMgr.getInstn().getRealRoomId(roomId);
		String sRoomId = String.valueOf(roomId);
		Map<String, String> headers = GET_HEADER(cookie.toNVCookie(), sRoomId);
		String response = HttpURLUtils.doGet(SIGN_URL, headers, null);
		analyse(response);
	}

	/**
	 * 
	 * @param response  {"code":0,"msg":"","message":"","data":[]}
	 * @return
	 */
	private static boolean analyse(String response) {
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
			if(code == 0 || reason.contains("已签到") || reason.contains("已领取")) {
				isOk = true;
			} else {
				log.warn("签到失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("签到失败: {}", response, e);
		}
		return isOk;
	}
	
	/**
	 * 执行小学数学日常任务
	 * @param roomId
	 * @return 返回下次任务的时间点
	 */
	public static long doMathTasks(String cookie) {
		long nextTaskTime = -1;
		final int roomId = UIUtils.getCurRoomId();
		final int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
		if(realRoomId <= 0) {
			return nextTaskTime;
		}
		final Map<String, String> header = GET_HEADER(
				cookie, String.valueOf(realRoomId));
		
		MathTask task = checkTask(header);
		if(task != MathTask.NULL) {
			nextTaskTime = task.getEndTime() * 1000;
			
			// 已到达任务执行时间
			if(nextTaskTime <= System.currentTimeMillis()) {
				if(!_doMathTasks(header, task)) {
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
	private static boolean _doMathTasks(Map<String, String> header, MathTask task) {
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
	private static MathTask checkTask(Map<String, String> header) {
		MathTask task = MathTask.NULL;
		String response = HttpURLUtils.doGet(CHECK_TASK_URL, header, null);
		
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				task = new MathTask(json);
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
	private static boolean doTask(Map<String, String> header, MathTask task, int answer) {
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
