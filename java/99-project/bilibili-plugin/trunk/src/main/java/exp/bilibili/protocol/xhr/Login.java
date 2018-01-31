package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.utils.RSAUtils;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.envm.LoginType;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 登陆
 * 	FIXME : 补充二维码登陆方式， 不再用webkit
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Login extends __XHR {

	/** B站首页 */
	private final static String MAIN_HOME = Config.getInstn().MAIN_HOME();
	
	/** 登陆服务器 */
	private final static String LOGIN_HOST = Config.getInstn().LOGIN_HOST();
	
	/** 登陆URL */
	private final static String QRLOGIN_URL = Config.getInstn().QRLOGIN_URL();
	
	/** RSA公钥URL */
	private final static String RSA_KEY_URL = Config.getInstn().RSA_URL();
	
	/** 下载验证码图片的URL */
	private final static String VCCODE_URL = Config.getInstn().VCCODE_URL();
	
	/** 验证码图片配套cookie中的SID */
	private final static String SID = "sid";
	
	/** 验证码图片配套cookie中的JSESSIONID */
	private final static String JSESSIONID = "JSESSIONID";
	
	/** 使用帐密+验证码登陆的URL */
	private final static String VCLOGIN_URL = Config.getInstn().VCLOGIN_URL();
	
	/** 获取二维码图片信息的URL */
	private final static String QRCODE_URL = Config.getInstn().QRCODE_URL();
	
	/** 检测二维码是否被扫码登陆的URL */
	private final static String QRCHECK_URL = Config.getInstn().QRCHECK_URL();
	
	/** 查询账号信息URL */
	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	/** 私有化构造函数 */
	protected Login() {}
	
	/**
	 * 从Http会话的响应报文中提取cookie信息
	 * @param client Http会话客户端
	 * @param cookie cookie对象容器
	 */
	private static void takeCookies(HttpClient client, HttpCookie cookie) {
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
	
	/**
	 * 获取二维码登陆信息(用于在本地生成二维码图片)
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
			log.error("获取二维码登陆信息失败: {}", response, e);
		}
		return url;
	}
	
	/**
	 * 检测二维码是否扫码登陆成功
	 * @param oauthKey 二维码登陆信息中提取的oauthKey
	 * @return 若扫码登陆成功, 则返回有效Cookie
	 */
	public static HttpCookie toLogin(String oauthKey) {
		HttpCookie cookie = new HttpCookie();
		HttpClient client = new HttpClient();
		
		Map<String, String> header = getHeader();
		Map<String, String> request = getRequest(oauthKey);
		String response = client.doPost(QRCHECK_URL, header, request);
		// {"status":true,"ts":1516932980,"data":{"url":"https://passport.biligame.com/crossDomain?DedeUserID=1650868&DedeUserID__ckMd5=686caa22740f2663&Expires=84600&SESSDATA=e6e4328c%2C1517017580%2Cc9bf14ac&bili_jct=2be210c9dbaa157359be2ca2d9e50188&gourl=http%3A%2F%2Fwww.bilibili.com"}}

		try {
			JSONObject json = JSONObject.fromObject(response);
			String status = JsonUtils.getStr(json, BiliCmdAtrbt.status);
			if("true".equalsIgnoreCase(status)) {
				takeCookies(client, cookie);
			} else {
				cookie = HttpCookie.NULL;
			}
		} catch(Exception e) {
			cookie = HttpCookie.NULL;
			log.error("获取二维码登陆信息失败: {}", response, e);
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
		header.put(HttpUtils.HEAD.KEY.HOST, LOGIN_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, QRLOGIN_URL);
		header.put(HttpUtils.HEAD.KEY.REFERER, QRLOGIN_URL);
		return header;
	}
	
	/**
	 * 生成二维码登陆用的请求参数
	 * @param username 账号
	 * @param password 密码（RSA公钥加密密文）
	 * @param vccode 图片验证码
	 * @return
	 */
	private static Map<String, String> getRequest(String oauthKey) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("oauthKey", oauthKey);
		request.put("gourl", MAIN_HOME);
		return request;
	}
	
	/**
	 * 下载登陆用的验证码图片
	 * @param imgPath 验证码图片保存路径
	 * @return 与该验证码配套的cookies
	 */
	public static String downloadVccode(String imgPath) {
		final String sid = StrUtils.concat(SID, "=", randomSID());
		HttpClient client = new HttpClient();
		
		// 下载验证码图片（该验证码图片需要使用一个随机sid去请求）
		Map<String, String> inHeaders = new HashMap<String, String>();
		inHeaders.put(HttpUtils.HEAD.KEY.COOKIE, sid);
		boolean isOk = client.downloadByGet(imgPath, VCCODE_URL, inHeaders, null);
		
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
		
		// SID与JSESSIONID绑定了该验证码图片, 在登陆时需要把这个信息一起POST
		return StrUtils.concat(sid, "; ", jsessionId);
	}
	
	/**
	 * 生成随机SID (sid是由长度为8的由a-z0-9字符组成的字符串)
	 * @return 随机SID
	 */
	private static String randomSID() {
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
	 * 通过帐密+验证码方式登陆
	 * @param username 账号
	 * @param password 密码
	 * @param vccode 验证码
	 * @param vcCookies 与验证码配套的登陆用cookie
	 * @return 
	 */
	public static HttpCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		HttpCookie cookie = new HttpCookie();
		HttpClient client = new HttpClient();
		
		try {
			// 从服务器获取RSA公钥(公钥是固定的)和随机hash码, 然后使用公钥对密码进行RSA加密
			String sJson = client.doGet(RSA_KEY_URL, getHeader("", LOGIN_HOST), null);
			JSONObject json = JSONObject.fromObject(sJson);
			String hash = JsonUtils.getStr(json, BiliCmdAtrbt.hash);
			String pubKey = JsonUtils.getStr(json, BiliCmdAtrbt.key);
			password = RSAUtils.encrypt(hash.concat(password), pubKey);
			
			// 把验证码、验证码配套的cookie、账号、RSA加密后的密码 提交到登陆服务器
			Map<String, String> headers = getHeader(vcCookies, LOGIN_HOST);
			Map<String, String> request = getRequest(username, password, vccode);
			sJson = client.doPost(VCLOGIN_URL, headers, request);
			
			// 若登陆成功，则提取返回的登陆cookie, 以便下次使用
			json = JSONObject.fromObject(sJson);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {	
				takeCookies(client, cookie);
			} else {
				cookie = HttpCookie.NULL;
			}
		} catch(Exception e) {
			cookie = HttpCookie.NULL;
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
	private static Map<String, String> getHeader(String cookie, String host) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, host);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_HOME.concat("/p/center/index"));
		return header;
	}
	
	/**
	 * 生成验证码登陆用的请求参数
	 * @param username 账号
	 * @param password 密码（RSA公钥加密密文）
	 * @param vccode 图片验证码
	 * @return
	 */
	private static Map<String, String> getRequest(
			String username, String password, String vccode) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("cType", "2");
		request.put("vcType", "1");		// 1:验证码校验方式;  2:二维码校验方式
		request.put("captcha", vccode);	// 图片验证码
		request.put("user", username);	// 账号（明文）
		request.put("pwd", password);	// 密码（RSA公钥加密密文）
		request.put("keep", "true");
		request.put("gourl", MAIN_HOME);	// 登录后的跳转页面
		return request;
	}
	
	public static void main(String[] args) {
		CookiesMgr.INSTN().load(LoginType.VEST);
		queryUserInfo(CookiesMgr.INSTN().VEST());
	}
	
	/**
	 * 查询账号信息(写入cookie内)
	 * {"code":0,"msg":"\u83b7\u53d6\u6210\u529f","data":{"achieves":960,"userInfo":{"uid":1650868,"uname":"M-\u4e9a\u7d72\u5a1c","face":"https:\/\/i1.hdslb.com\/bfs\/face\/bbfd1b5cafe4719e3a57154ac1ff16a9e4d9c6b3.jpg","rank":10000,"identification":1,"mobile_verify":1,"platform_user_level":4,"official_verify":{"type":-1,"desc":""}},"roomid":"269706","userCoinIfo":{"uid":1650868,"vip":1,"vip_time":"2018-12-12 21:56:04","svip":1,"svip_time":"2018-12-06 21:56:04","cost":63781395,"rcost":2481900,"user_score":440323260,"silver":"29902","gold":"72009","iap_gold":0,"score":24819,"master_level":{"level":10,"current":[6300,18060],"next":[9100,27160]},"user_current_score":504104655,"user_level":45,"user_next_level":46,"user_intimacy":4104655,"user_next_intimacy":50000000,"user_level_rank":4325,"bili_coins":0,"coins":475},"vipViewStatus":false,"discount":false,"svip_endtime":"2018-12-06","vip_endtime":"2018-12-12","year_price":233000,"month_price":20000,"action":"index","liveTime":0,"master":{"level":10,"current":6759,"next":9100,"medalInfo":{"id":"25072","uid":"1650868","medal_name":"\u795e\u624b","live_status":"1","master_status":"1","status":1,"reason":"0","last_rename_time":"0","time_able_change":0,"rename_status":1,"charge_num":50,"coin_num":20,"platform_status":"2"}},"san":12,"count":{"guard":2,"fansMedal":11,"title":24,"achieve":0}}}
	 * @param cookie
	 * @return username
	 */
	public static boolean queryUserInfo(HttpCookie cookie) {
		Map<String, String> headers = getHeader(cookie.toNVCookie(), LIVE_HOST);
		String response = HttpURLUtils.doGet(ACCOUNT_URL, headers, null);
		
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject userInfo = JsonUtils.getObject(data, BiliCmdAtrbt.userInfo);
//				String uid = JsonUtils.getStr(userInfo, BiliCmdAtrbt.uid);
				String username = JsonUtils.getStr(userInfo, BiliCmdAtrbt.uname);
				
//				cookie.setUid(uid);	// 登陆cookie中已自带，此处可不再写入
				cookie.setNickName(username);
			}
		} catch(Exception e) {
			log.error("查询账号信息异常: {}", response, e);
		}
		return cookie.isVaild();
	}
	
}
