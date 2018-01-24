package exp.bilibili.plugin.tmp;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.libs.warp.net.http.HttpUtils;

// 用于登录主号、小号、马甲号
public class LoginByVC {

	private final static String VCCODE_URL = Config.getInstn().VCCODE_URL();
	
	public final static String IMG_DIR = Config.getInstn().IMG_DIR();
	
	private final static String VCCODE_PATH = IMG_DIR.concat("/vccode.jpg");
	
	private final static String SID = "sid";
	
	private final static String JSESSIONID = "JSESSIONID";
	
	/**
	 * 下载登陆用的验证码
	 * @return 与该验证码配套的cookies
	 */
	public String downloadVccode() {
		final String sid = StrUtils.concat(SID, "=", randomSID());
		HttpClient client = new HttpClient();
		
		// 下载验证码图片（该验证码图片需要使用一个随机sid去请求）
		Map<String, String> inHeaders = new HashMap<String, String>();
		inHeaders.put(HttpUtils.HEAD.KEY.COOKIE, sid);
		boolean isOk = client.downloadByGet(VCCODE_PATH, VCCODE_URL, inHeaders, null);
		
		// 服务端返回验证码的同时，会返回一个与之绑定的JSESSIONID
		String jsessionId = "";
		HttpMethod method = client.getHttpMethod();
		if(isOk && method != null) {
			Header outHeader = method.getResponseHeader(HttpUtils.HEAD.KEY.SET_COOKIE);
			if(outHeader != null) {
				jsessionId = RegexUtils.findFirst(outHeader.getValue(), 
						StrUtils.concat("(", JSESSIONID, "=[^;]+)"));
			}
		}
		client.close();
		
		// SID与JSESSIONID绑定了该二维码图片, 在登陆时需要把这个信息一起POST
		return StrUtils.concat(sid, "; ", jsessionId);
	}
	
	/**
	 * 生成随机SID (sid是由长度为8的由a-z0-9字符组成的字符串)
	 * @return 随机SID
	 */
	private String randomSID() {
		StringBuilder sid = new StringBuilder();
		for(int i = 0; i < 8; i++) {	// sid长度为8
			int n = RandomUtils.randomInt(36);	// a-z, 0-9
			if(n < 26) {	// a-z
				sid.append((char) (n + 'a'));
				
			} else {	// 0-9
				n = n - 26;
				sid.append((char) (n + '0'));
			}
		}
		return sid.toString();
	}
	
	/**
	 * 使用帐密+验证码的方式登录
	 * @param username 账号
	 * @param password 密码
	 * @param vccode 验证码
	 * @param vcCookies 与验证码配套的cookies
	 * @return
	 */
	public HttpCookies toLogin(String username, String password, 
			String vccode, String vcCookies) {
		HttpCookies cookies = MsgSender.toLogin(username, password, vccode, vcCookies);
		if(cookies.isVaild() == true) {
			String user = MsgSender.queryUsername(cookies.toStrCookies());
			cookies.setUser(user);
		}
		return cookies;
	}
	
}
