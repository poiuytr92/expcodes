package exp.bilibili.plugin.bean.protocol;

import java.util.Iterator;

import exp.bilibili.plugin.bean.cookie.HttpCookies;
import exp.bilibili.plugin.bean.cookie.HttpCookie;

public class Protocol {

	public static HttpCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		return Login.toLogin(username, password, vccode, vcCookies);
	}
	
	public static String queryUsername(HttpCookie cookie) {
		return UserInfo.queryUsername(cookie.toNVCookie());
	}

	// 仅主号签到有爱社
	public static boolean toAssn(HttpCookie cookie) {
		return Assn.toAssn(cookie.toNVCookie(), cookie.CSRF());
	}
	
	public static void toSign(HttpCookies cookies) {
		Iterator<HttpCookie> cookieIts = cookies.ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			Sign.toSign(cookie.toNVCookie());
		}
	}
	
}
