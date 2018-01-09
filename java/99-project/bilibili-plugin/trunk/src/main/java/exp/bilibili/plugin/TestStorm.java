package exp.bilibili.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//		Browser.init(false);
//		Browser.open(Config.getInstn().LOGIN_URL());
//		Browser.recoveryCookies();
//		Browser.backupCookies();
//		final String cookies = Browser.COOKIES();
//		Browser.quit();
		
		final String cookies = "fts=1515473099; sid=5761i729; DedeUserID__ckMd5=7a2868581681a219; SESSDATA=b21f4571%2C1518065128%2Ca4a2c821; DedeUserID=31796320; LIVE_BUVID=AUTO1815154731332941; bili_jct=50d8d66fabbc3342e1266dbf8cc91f9e; buvid3=2A29CCC2-A148-4D18-BB26-9A0AD3AF3F4044870infoc; finger=540129ae; _dfcaptcha=51859f72f98239ffd4a3ccd051259b9f; LIVE_PLAYER_TYPE=1";
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
