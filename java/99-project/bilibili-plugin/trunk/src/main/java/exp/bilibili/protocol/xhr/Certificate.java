package exp.bilibili.protocol.xhr;

import java.util.Map;

import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Certificate extends _MsgSender {

	/**
	 * 获取B站link中心针对本插件的授权校验标签
	 * @param BILIBILI_URL B站link中心
	 * @return {"code":0,"msg":"OK","message":"OK","data":["W:M-亚絲娜","B:","T:20180301","V:2.0"]}
	 */
	public static String queryTags(final String BILIBILI_URL) {
		Map<String, String> headers = toGetHeadParams("");
		headers.put(HttpUtils.HEAD.KEY.HOST, LINK_HOST);
		headers.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_URL);
		headers.put(HttpUtils.HEAD.KEY.REFERER, LINK_URL.concat("/p/world/index"));
		
		return HttpURLUtils.doGet(BILIBILI_URL, headers, null);
	}
	
}
