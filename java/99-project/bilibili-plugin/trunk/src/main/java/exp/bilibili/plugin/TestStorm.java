package exp.bilibili.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.cache.Browser;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class TestStorm {

	private final static Logger log = LoggerFactory.getLogger(TestStorm.class);
	
	// 每10分钟更新当前top50房间
	// 只扫描那些
	
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		Browser.init(false);
		Browser.open(Config.getInstn().LOGIN_URL());
		Browser.recoveryCookies();
		Browser.backupCookies();
		final String cookies = Browser.COOKIES();
		System.out.println(cookies);
		Browser.quit();
		
//		final String cookies = "fts=1515566327; sid=i1ullm4k; DedeUserID__ckMd5=d1a0e260e1010310; SESSDATA=e764cf2a%2C1518158344%2Cbd05661e; DedeUserID=247056833; buvid3=A82B9293-C27E-4B59-8C14-A6B87C4EDC9531028infoc; bili_jct=17753d82b12a4306f50befd1de3de6a0; LIVE_BUVID=AUTO2815155663477917; finger=540129ae; _dfcaptcha=2735e43373995297a27f1d434756e4b1";
		final String url = Config.getInstn().STORM_CHECK_URL();
		Map<String, String> requests = new HashMap<String, String>();
		
		while(true) {
			Set<Integer> roomIds = RoomMgr.getInstn().getRealRoomIds();
			for(Integer roomId : roomIds) {
				String sRoomId = roomId.toString();
				Map<String, String> headers = toGetHeadParams(cookies, sRoomId);
				requests.put("roomid", sRoomId);
				
				String rst = HttpURLUtils.doGet(url, headers, requests);
				log.info("{}:{}", sRoomId, rst);
				
				ThreadUtils.tSleep(100);
			}
			
			log.info("============================");
			ThreadUtils.tSleep(5000);
		}
	}
	
	private static Map<String, String> toGetHeadParams(String cookies, String realRoomId) {
		Map<String, String> params = toGetHeadParams(cookies);
		params.put(HttpUtils.HEAD.KEY.HOST, Config.getInstn().SSL_HOST());
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
	
	
}
