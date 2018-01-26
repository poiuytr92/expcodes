package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.utils.RSAUtils;
import exp.bilibili.protocol.bean.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.libs.warp.net.http.HttpUtils;

public class Login extends _MsgSender {

	private final static String HOME_URL = Config.getInstn().HOME_URL();
	
	private final static String LOGIN_HOST = Config.getInstn().LOGIN_HOST();
	
	private final static String MINI_LOGIN_URL = Config.getInstn().MINI_LOGIN_URL();
	
	private final static String RSA_KEY_URL = Config.getInstn().RSA_URL();
	
	/**
	 * 从后台秘密通道登陆B站
	 * @param username 账号
	 * @param password 密码
	 * @param vccode 验证码
	 * @param vcCookies 与验证码配套的登陆用cookie
	 * @return Cookie集合
	 */
	public static HttpCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		HttpCookie cookie = new HttpCookie();
		HttpClient client = new HttpClient();
		
		try {
			// 从服务器获取RSA公钥(公钥是固定的)和随机hash码, 然后使用公钥对密码进行RSA加密
			String sJson = client.doGet(RSA_KEY_URL, _toLoginHeadParams(""), null);
			JSONObject json = JSONObject.fromObject(sJson);
			String hash = JsonUtils.getStr(json, BiliCmdAtrbt.hash);
			String pubKey = JsonUtils.getStr(json, BiliCmdAtrbt.key);
			password = RSAUtils.encrypt(hash.concat(password), pubKey);
			
			// 把验证码、验证码配套的cookie、账号、RSA加密后的密码 提交到登陆服务器
			Map<String, String> headers = _toLoginHeadParams(vcCookies);
			Map<String, String> requests = _toLoginRequestParams(username, password, vccode);
			sJson = client.doPost(MINI_LOGIN_URL, headers, requests);
			
			// 若登陆成功，则提取返回的登陆cookie, 以便下次使用
			json = JSONObject.fromObject(sJson);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {	
				HttpMethod method = client.getHttpMethod();
				if(method != null) {
					Header[] outHeaders = method.getResponseHeaders();
					for(Header outHeader : outHeaders) {
						if(HttpUtils.HEAD.KEY.SET_COOKIE.equals(outHeader.getName())) {
							cookie.add(outHeader.getValue());
						}
					}
				}
			}
		} catch(Exception e) {
			log.error("登陆失败", e);
		}
		client.close();
		return cookie;
	}
	
	/**
	 * 生成登陆用的请求头参数
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> _toLoginHeadParams(String cookie) {
		Map<String, String> params = toGetHeadParams(cookie);
		params.put(HttpUtils.HEAD.KEY.HOST, LOGIN_HOST);
		return params;
	}
	
	/**
	 * 生成登陆用的请求参数
	 * @param username 账号
	 * @param password 密码（RSA公钥加密密文）
	 * @param vccode 图片验证码
	 * @return
	 */
	private static Map<String, String> _toLoginRequestParams(
			String username, String password, String vccode) {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("cType", "2");
		requests.put("vcType", "1");		// 1:验证码校验方式;  2:二维码校验方式
		requests.put("captcha", vccode);	// 图片验证码
		requests.put("user", username);	// 账号（明文）
		requests.put("pwd", password);	// 密码（RSA公钥加密密文）
		requests.put("keep", "true");
		requests.put("gourl", HOME_URL);	// 登录后的跳转页面
		return requests;
	}
	
}
