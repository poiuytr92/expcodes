package exp.crawler.qq.utils;

import java.util.HashMap;
import java.util.Map;

import exp.crawler.qq.bean.QQCookie;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpUtils;

public class XHRUtils {

	protected XHRUtils() {}
	
	public static Map<String, String> getHeader(QQCookie cookie) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "image/webp,image/*,*/*;q=0.8");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.COOKIE, cookie.toNVCookie());
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, HttpUtils.HEAD.VAL.USER_AGENT);
		return header;
	}
	
	public static String toJson(String callback) {
		return RegexUtils.findFirst(callback.replace("\\/", "/"), "_Callback\\(([\\s\\S]*)\\);$");
	}
	
	public static String toHost(String url) {
		return RegexUtils.findFirst(url, "http://([^/]*)/");
	}
	
}
