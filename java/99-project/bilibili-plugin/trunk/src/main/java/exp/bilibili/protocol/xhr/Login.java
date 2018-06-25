package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.envm.HttpHead;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 登陆
 * ===========================
 * 	B站XHR登陆分析参考(原文所说的方法已失效, 此处做过修正)：
 * 		http://www.cnblogs.com/-E6-/p/6953590.html
 * 	
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Login extends __XHR {

	/** B站主站首�? */
	private final static String MAIN_HOME = Config.getInstn().MAIN_HOME();
	
	/** 登陆主机服务�? */
	private final static String LOGIN_HOST = Config.getInstn().LOGIN_HOST();
	
	/** 获取二维码图片信息的URL */
	private final static String QRCODE_URL = Config.getInstn().QRCODE_URL();
	
	/** 检测二维码是否被扫码登陆的URL */
	private final static String QRCHECK_URL = Config.getInstn().QRCHECK_URL();
	
	/** 二维码扫码登陆URL */
	private final static String QRLOGIN_URL = Config.getInstn().QRLOGIN_URL();
	
	/** 下载验证码图片的URL */
	private final static String VCCODE_URL = Config.getInstn().VCCODE_URL();
	
	/** 验证码图片配套cookie中的SID */
	private final static String SID = "sid";
	
	/** 验证码图片配套cookie中的JSESSIONID */
	private final static String JSESSIONID = "JSESSIONID";
	
	/** 获取RSA公钥URL */
	private final static String RSA_KEY_URL = Config.getInstn().RSA_URL();
	
	/** 使用帐密+验证码登陆的URL */
	private final static String VCLOGIN_URL = Config.getInstn().VCLOGIN_URL();
	
	/** 私有化构造函�? */
	protected Login() {}
	
	/**
	 * 从Http会话的响应报文中提取cookie信息
	 * @param client Http会话客户�?
	 * @param cookie cookie对象容器
	 */
	private static void takeCookies(HttpClient client, BiliCookie cookie) {
		HttpMethod method = client.getHttpMethod();
		if(method != null) {
			Header[] outHeaders = method.getResponseHeaders();
			for(Header outHeader : outHeaders) {
				if(HttpHead.KEY.SET_COOKIE.equals(outHeader.getName())) {
					cookie.add(outHeader.getValue());
				}
			}
		}
	}
	
	/**
	 * 获取二维码登陆信�?(用于在本地生成二维码图片)
	 * @return https://passport.bilibili.com/qrcode/h5/login?oauthKey=b2fd47ca9a9fcb5a5943782d54ac3022
	 */
	public static String getQrcodeInfo() {
		Map<String, String> header = getHeader();
		String response = HttpURLUtils.doGet(QRCODE_URL, header, null);
		
		String url = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
			url = JsonUtils.getStr(data, BiliCmdAtrbt.url);
			
		} catch(Exception e) {
			log.error("获取二维码登陆信息失�?: {}", response, e);
		}
		return url;
	}
	
	/**
	 * 检测二维码是否扫码登陆成功
	 * @param oauthKey 二维码登陆信息中提取的oauthKey
	 * @return 若扫码登陆成�?, 则返回有效Cookie
	 */
	public static BiliCookie toLogin(String oauthKey) {
		BiliCookie cookie = new BiliCookie();
		HttpClient client = new HttpClient();
		
		Map<String, String> header = getHeader();
		Map<String, String> request = getRequest(oauthKey);
		String response = client.doPost(QRCHECK_URL, header, request);

		try {
			JSONObject json = JSONObject.fromObject(response);
			String status = JsonUtils.getStr(json, BiliCmdAtrbt.status);
			if("true".equalsIgnoreCase(status)) {
				takeCookies(client, cookie);
			} else {
				cookie = BiliCookie.NULL;
			}
		} catch(Exception e) {
			cookie = BiliCookie.NULL;
			log.error("获取二维码登陆信息失�?: {}", response, e);
		}
		client.close();
		return cookie;
	}
	
	/**
	 * 生成二维码登陆用的请求头
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> getHeader() {
		Map<String, String> header = POST_HEADER("");
		header.put(HttpHead.KEY.HOST, LOGIN_HOST);
		header.put(HttpHead.KEY.ORIGIN, QRLOGIN_URL);
		header.put(HttpHead.KEY.REFERER, QRLOGIN_URL);
		return header;
	}
	
	/**
	 * 生成二维码登陆用的请求参�?
	 * @param username 账号
	 * @param password 密码（RSA公钥加密密文�?
	 * @param vccode 图片验证�?
	 * @return
	 */
	private static Map<String, String> getRequest(String oauthKey) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.oauthKey, oauthKey);
		request.put(BiliCmdAtrbt.gourl, MAIN_HOME);
		return request;
	}
	
	/**
	 * 下载登陆用的验证码图�?
	 * @param imgPath 验证码图片保存路�?
	 * @return 与该验证码配套的cookies
	 */
	public static String downloadVccode(String imgPath) {
		final String sid = StrUtils.concat(SID, "=", genSID());
		HttpClient client = new HttpClient();
		
		// 下载验证码图片（该验证码图片需要使用一个随机sid去请求）
		Map<String, String> inHeaders = new HashMap<String, String>();
		inHeaders.put(HttpHead.KEY.COOKIE, sid);
		boolean isOk = client.downloadByGet(imgPath, VCCODE_URL, inHeaders, null);
		
		// 服务端返回验证码的同时，会返回一个与之绑定的JSESSIONID
		String jsessionId = "";
		HttpMethod method = client.getHttpMethod();
		if(isOk && method != null) {
			Header outHeader = method.getResponseHeader(HttpHead.KEY.SET_COOKIE);
			if(outHeader != null) {
				jsessionId = RegexUtils.findFirst(outHeader.getValue(), 
						StrUtils.concat("(", JSESSIONID, "=[^;]+)"));
			}
		}
		client.close();
		
		// SID与JSESSIONID绑定了该验证码图�?, 在登陆时需要把这个信息一起POST
		return StrUtils.concat(sid, "; ", jsessionId);
	}
	
	/**
	 * 生成随机SID (sid是由长度�?8的由a-z0-9字符组成的字符串)
	 * @return 随机SID
	 */
	private static String genSID() {
		StringBuilder sid = new StringBuilder();
		for(int i = 0; i < 8; i++) {	// sid长度�?8
			int n = RandomUtils.genInt(36);	// a-z, 0-9
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
	 * 通过帐密+验证码方式登�?
	 * @param username 账号
	 * @param password 密码
	 * @param vccode 验证�?
	 * @param vcCookies 与验证码配套的登陆用cookie
	 * @return 
	 */
	public static BiliCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		BiliCookie cookie = new BiliCookie();
		HttpClient client = new HttpClient();
		
		try {
			// 从服务器获取RSA公钥(公钥是固定的)和随机hash�?, 然后使用公钥对密码进行RSA加密
			String sJson = client.doGet(RSA_KEY_URL, getHeader(""), null);
			JSONObject json = JSONObject.fromObject(sJson);
			String hash = JsonUtils.getStr(json, BiliCmdAtrbt.hash);
			String pubKey = JsonUtils.getStr(json, BiliCmdAtrbt.key);
			password = CryptoUtils.toRSAByPubKey(hash.concat(password), pubKey);
			
			// 把验证码、验证码配套的cookie、账号、RSA加密后的密码 提交到登陆服务器
			Map<String, String> header = getHeader(vcCookies);
			Map<String, String> request = getRequest(username, password, vccode);
			sJson = client.doPost(VCLOGIN_URL, header, request);
			
			// 若登陆成功，则提取返回的登陆cookie, 以便下次使用
			json = JSONObject.fromObject(sJson);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {	
				takeCookies(client, cookie);
			} else {
				cookie = BiliCookie.NULL;
			}
		} catch(Exception e) {
			cookie = BiliCookie.NULL;
			log.error("登陆失败", e);
		}
		client.close();
		return cookie;
	}
	
	/**
	 * 生成验证码登陆用的请求头
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> getHeader(String cookie) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpHead.KEY.HOST, LOGIN_HOST);
		header.put(HttpHead.KEY.ORIGIN, LINK_HOME);
		header.put(HttpHead.KEY.REFERER, LINK_HOME.concat("/p/center/index"));
		return header;
	}
	
	/**
	 * 生成验证码登陆用的请求参�?
	 * @param username 账号
	 * @param password 密码（RSA公钥加密密文�?
	 * @param vccode 图片验证�?
	 * @return
	 */
	private static Map<String, String> getRequest(
			String username, String password, String vccode) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.cType, "2");
		request.put(BiliCmdAtrbt.vcType, "1");		// 1:验证码校验方�?;  2:二维码校验方�?
		request.put(BiliCmdAtrbt.captcha, vccode);	// 图片验证�?
		request.put(BiliCmdAtrbt.user, username);	// 账号（明文）
		request.put(BiliCmdAtrbt.pwd, password);	// 密码（RSA公钥加密密文�?
		request.put(BiliCmdAtrbt.keep, "true");
		request.put(BiliCmdAtrbt.gourl, MAIN_HOME);	// 登录后的跳转页面
		return request;
	}
	
}
