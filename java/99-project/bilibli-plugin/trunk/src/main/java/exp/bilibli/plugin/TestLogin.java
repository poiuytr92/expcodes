package exp.bilibli.plugin;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import exp.bilibli.plugin.utils.RSAUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.libs.warp.net.http.HttpUtils;

public class TestLogin {

	private final static String VCCODE_URL = "https://passport.bilibili.com/captcha";
	
	private final static String RSA_KEY_URL = "https://passport.bilibili.com/login?act=getkey";
	
	private final static String LOGIN_URL = "https://passport.bilibili.com/web/login";
	
	private final static String SID = "sid";
	
	private final static String JSESSIONID = "JSESSIONID";
	
	private final static String VCCODE_PATH = "./data/img/vccode.jpg";
	
	public static void main(String[] args) {
//		String cookies = downloadVccode(VCCODE_PATH);
//		System.out.println(cookies);
		
		String cookies = "sid=3w4lfv2m; JSESSIONID=63BE06A2D1C14BAA072FE77501F30D7C";
		String vccode = "N633X";
		loginTest(cookies, vccode);
	}
	
	public static void loginTest(String cookies, String vccode) {
		String username = "272629724@qq.com";
		String password = "liao5422";
		
		HttpClient client = new HttpClient();
		String sJson = client.doGet(RSA_KEY_URL, toLoginHeadParams(""), null);
		System.out.println(sJson);
		
		JSONObject json = JSONObject.fromObject(sJson);
		String hash = JsonUtils.getStr(json, "hash");
		String pubKey = JsonUtils.getStr(json, "key");
		
		password = RSAUtils.encrypt(hash + password, pubKey);
		System.out.println(password);
		
		Map<String, String> head = toLoginHeadParams(cookies);
		Map<String, String> request = _toLoginRequestParams(username, password, vccode);
		
		String rst = client.doPost(LOGIN_URL, head, request);
		System.out.println(rst);
//		{"code":0,"data":"https://passport.biligame.com/crossDomain?DedeUserID=31796320&DedeUserID__ckMd5=7a2868581681a219&Expires=2592000&SESSDATA=b21f4571%2C1517901333%2Cba70690c&bili_jct=7136e7aefb2385048cd77cc93ce25d56&gourl=https%3A%2F%2Fwww.bilibili.com"}

		
		HttpMethod method = client.getHttpMethod();
		if(method != null) { // FIXME && 登陆成功
			Header[] outHeaders = method.getResponseHeaders();
			for(Header outHeader : outHeaders) {
				System.out.println("KEY = " + outHeader.getName());
				System.out.println("VAL = " + outHeader.getValue());
				System.out.println("===");
			}
		}
		
		
		client.close();
	}
	
	
	private static Map<String, String> toLoginHeadParams(String cookies) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpUtils.HEAD.KEY.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8");
		params.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		params.put(HttpUtils.HEAD.KEY.HOST, "passport.bilibili.com");
		return params;
	}
	
	private static Map<String, String> _toLoginRequestParams(String username, String password, String vccode) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("cType", "2");
		request.put("vcType", "1");	// 1:验证码校验方式;  2:二维码校验方式
		request.put("captcha", vccode);	// 图片验证码
		request.put("user", username);	// 账号（明文）
		request.put("pwd", password);		// 密码（RSA公钥加密密文）
		request.put("keep", "true");
		request.put("gourl", "");	// 登录号跳转页面	http://www.bilibili.com/
		return request;
	}
	
}
