package exp.bilibli.plugin.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpsUtils;

public class ImgDN {

	// 获取一次任务后，可以在任务结束时间点之后获取验证码图片
	public static void main(String[] args) {
		Browser.init(false);
		Browser.open("http://live.bilibili.com/438");
		Browser.backupCookies();
		String roomId = "390480";
		
		String[] times = checkTask(roomId);
		if(times.length == 2) {
			String errDesc = "";
			do {
				int answer = 0;
				do {
					answer = getAnswer(roomId);
					ThreadUtils.tSleep(1000);
				} while(answer <= 0);
				
				errDesc = toDo(roomId, times[0], times[1], answer);
				System.out.println(errDesc);
			} while(StrUtils.isNotEmpty(errDesc));
		}
		
		Browser.quit();
	}
	
//	// {"code":0,"msg":"","data":{"minute":6,"silver":80,"time_start":1514015075,"time_end":1514015435,"times":3,"max_times":5}}
//	// {"code":0,"msg":"","data":{"minute":6,"silver":80,"time_start":1514015075,"time_end":1514015435,"times":3,"max_times":5}}
//	// {"code":0,"msg":"","data":{"minute":6,"silver":80,"time_start":1514015075,"time_end":1514015435,"times":3,"max_times":5}}
//	// {"code":0,"msg":"","data":{"minute":9,"silver":190,"time_start":1514036545,"time_end":1514037085,"times":3,"max_times":5}}
//	System.out.println(TimeUtils.toStr(1514045525000L));
//	System.out.println(TimeUtils.toStr(1514037085000L));
//	System.out.println(TimeUtils.toStr(1514036221738L));
	private static String[] checkTask(String roomId) {
		String taskURL = "http://api.live.bilibili.com/FreeSilver/getCurrentTask";
		Map<String, String> header = toGetHeadParams(Browser.COOKIES(), roomId);
		String response = HttpsUtils.doGet(taskURL, header, null);
		
		String[] times = new String[0];
		JSONObject json = JSONObject.fromObject(response);
		int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
		if(code == 0) {
			JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
			long bgnTime = JsonUtils.getLong(data, BiliCmdAtrbt.time_start, 0);
			long endTime = JsonUtils.getLong(data, BiliCmdAtrbt.time_end, 0);
			if(bgnTime > 0 && endTime > 0) {
				times = new String[] { String.valueOf(bgnTime), String.valueOf(endTime) };
			}
		}
		return times;
	}
	
	private static int getAnswer(String roomId) {
		final String imgPath = "./data/img/vercode.jpg";
		String imgURL = "http://api.live.bilibili.com/freeSilver/getCaptcha";
		Map<String, String> header = toGetHeadParams(Browser.COOKIES(), roomId);
		Map<String, String> request = new HashMap<String, String>();
		request.put("ts", String.valueOf(System.currentTimeMillis()));
		boolean isOk = HttpsUtils.download(imgPath, imgURL, header, request);
		int answer = (isOk ? VerCodeUtils.calculateImage(imgPath) : 0);
		return answer;
	}
	
// {"code":0,"msg":"ok","data":{"silver":7266,"awardSilver":80,"isEnd":0}}
//	{"code":0,"msg":"","data":{"minute":3,"silver":30,"time_start":1514044802,"time_end":1514044982,"times":1,"max_times":5}}
//	{"code":-903,"msg":"已经领取过这个宝箱","data":{"surplus":-25234082.15}}
	private static String toDo(String roomId, String bgnTime, String endTime, int answer) {
		String sumitURL = "http://api.live.bilibili.com/FreeSilver/getAward";
		Map<String, String> header = toGetHeadParams(Browser.COOKIES(), roomId);
		Map<String, String> request = new HashMap<String, String>();
		request.put("time_start", bgnTime);
		request.put("end_time", endTime);
		request.put("captcha", String.valueOf(answer));
		String response = HttpsUtils.doGet(sumitURL, header, request);
		
		String errDesc = "";
		JSONObject json = JSONObject.fromObject(response);
		int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
		if(code != 0) {
			errDesc = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
		} else {
			System.out.println(response);
		}
		return errDesc;
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
	
}
