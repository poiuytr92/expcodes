package exp.crawler.qq.core.xhr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exp.crawler.qq.Config;
import exp.crawler.qq.bean.QQCookie;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.core.interfaze.BaseLander;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.envm.XHRAtrbt;
import exp.crawler.qq.utils.EncryptUtils;
import exp.crawler.qq.utils.PicUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.crawler.qq.utils.XHRUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.ui.SwingUtils;

/**
 * <PRE>
 * QQ空间登陆器.
 * ========================================================
 * 	QQ空间XHR登陆分析参考(原文所说的方法已失效, 此处做过修正)：
 * 		登陆流程拆解：https://blog.csdn.net/M_S_W/article/details/70193899
 * 		登陆参数分析：https://blog.csdn.net/zhujunxxxxx/article/details/29412297
 * 		登陆参数分析：http://www.vuln.cn/6454
 * 		加密脚本抓取： https://baijiahao.baidu.com/s?id=1570118073573921&wfr=spider&for=pc
 * 		重定向BUG修正: http://jingpin.jikexueyuan.com/article/13992.html
 * 
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-03-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Lander extends BaseLander {

	/** 登陆成功后保存的cookie */
	private QQCookie cookie;
	
	/**
	 * 构造函�?
	 * @param QQ 所登陆的QQ
	 * @param password 所登陆的QQ密码
	 */
	public Lander(String QQ, String password) {
		super(QQ, password);
	}
	
	/**
	 * 初始�?
	 */
	@Override
	protected void init() {
		this.cookie = new QQCookie();
	}
	
	/**
	 * 执行登陆操作
	 * @return true:登陆成功; false:登陆失败
	 */
	@Override
	public boolean execute() {
		boolean isOk = false;
		try {
			initCookieEnv();	// 获得本次登陆的SIG
			String[] rst = takeVcode();	// 获得验证码与校验�?
			String vcode = rst[0];
			String verify = rst[1];
			
			String rsaPwd = encryptPassword(vcode);	// 加密登陆密码
			String callback = login(rsaPwd, vcode, verify);	// 登陆
			isOk = callback.toLowerCase().startsWith("http");
			if(isOk == true) {
				isOk = takeGTKAndToken(callback);	// 生成GTK与QzoneToken
				if(isOk == true) {
					Browser.updateCookie(cookie);	// 保存本次登陆的cookie
					UIUtils.log("登陆QQ [", QQ, "] 成功: ", cookie.NICKNAME());
					
				} else {
					UIUtils.log("登陆QQ [", QQ, "] 失败: 无法提取GTK或QzoneToken");
				}
			} else {
				UIUtils.log("登陆QQ [", QQ, "] 失败: ".concat(callback));
			}
		} catch(Exception e) {
			UIUtils.log(e, "登陆QQ [", QQ, "] 失败: XHR协议异常");
		}
		return isOk;
	}
	
	/**
	 * 初始化登陆用的Cookie环境参数.
	 * 	主要提取SIG值（属性名�?:pt_login_sig�?
	 */
	private void initCookieEnv() {
		UIUtils.log("正在初始化登陆环�?...");
		
		HttpClient client = new HttpClient();
		client.doGet(URL.SIG_URL, null, _getSigRequest());
		XHRUtils.takeResponseCookies(client, cookie);	// 提取响应头中的Set-Cookie参数(含SIG)
		client.close();
		
		UIUtils.log("已获得本次登陆的SIG�?: ", cookie.SIG());
	}
	
	/**
	 * 获取SIG的请求参�?
	 * @return
	 */
	private Map<String, String> _getSigRequest() {
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.proxy_url, "https://qzs.qq.com/qzone/v6/portal/proxy.html");
		request.put(XHRAtrbt.s_url, "https://qzs.qzone.qq.com/qzone/v5/loginsucc.html?para=izone&from=iqq");
		request.put(XHRAtrbt.pt_qr_link, "http://z.qzone.com/download.html");
		request.put(XHRAtrbt.self_regurl, "https://qzs.qq.com/qzone/v6/reg/index.html");
		request.put(XHRAtrbt.pt_qr_help_link, "http://z.qzone.com/download.html");
		request.put(XHRAtrbt.qlogin_auto_login, "1");
		request.put(XHRAtrbt.low_login, "0");
		request.put(XHRAtrbt.no_verifyimg, "1");
		request.put(XHRAtrbt.daid, "5");
		request.put(XHRAtrbt.appid, "549000912");	// 目前是固定�?
		request.put(XHRAtrbt.hide_title_bar, "1");
		request.put(XHRAtrbt.style, "22");
		request.put(XHRAtrbt.target, "self");
		request.put(XHRAtrbt.pt_no_auth, "0");
		request.put(XHRAtrbt.link_target, "blank");
		return request;
	}
	
	/**
	 * 提取登陆用的验证�?.
	 * 
	 * -----------------------------
	 * 一般情况下, 不需要输入图片验�?, 此时服务器的回调函数是：
	 * 	ptui_checkVC('0','!VAB','\x00\x00\x00\x00\x10\x3f\xff\xdc','cefb41782ce53f614e7665b5519f9858c80ab8925b8060d7a790802212da7205be1916ac4d45a77618c926c6a5fb330520b741d749519f33','2')
	 * 
	 * 其中: 0 表示不需要验证码
	 *      !VAB 为伪验证�?
	 * 		cefb41782ce53f614e7665b5519f9858c80ab8925b8060d7a790802212da7205be1916ac4d45a77618c926c6a5fb330520b741d749519f33
	 * 			则为验证码的校验�?
	 * 
	 * -----------------------------
	 * 但有时需要输入图片验证码(一般是输入了无效的QQ号导致的), 此时服务器的回调函数是：
	 *  ptui_checkVC('1','FLQ8ymCigFmw30P7YaLP6iVCZHuyzjJWN2lH4M_OMFBndsUiMY9idQ**','\x00\x00\x00\x00\x00\x12\xd6\x87','','2')
	 *  
	 * 其中: 1 表示需要验证码
	 * 		FLQ8ymCigFmw30P7YaLP6iVCZHuyzjJWN2lH4M_OMFBndsUiMY9idQ** 是用于获取验证码图片的参数（随机生成�?
	 * 
	 * 		然后代入参数访问以下地址得到验证码图片：
	 * 		https://ssl.captcha.qq.com/getimage?uin={QQ号}&cap_cd=FLQ8ymCigFmw30P7YaLP6iVCZHuyzjJWN2lH4M_OMFBndsUiMY9idQ**
	 * 
	 * 		同时该地址的Response Header中带有了该验证码的校验码�?
	 * 		Set-Cookie:verifysession=h02iEMnHmjdBoYn7eDlj7AX37Lk7ORMFwJnJSlMufnESimC64Uqa2jz4gHI3ws5jlmiGq5Hg5lfs-2aMkVQ_Gu-vyR7aflns97t
	 * 
	 * @return new String[] { 验证�?, 校验�? }
	 */
	private String[] takeVcode() {
		String response = HttpURLUtils.doGet(URL.VCODE_URL, null, _getVcodeRequest());
		List<String> groups = RegexUtils.findBrackets(response, "'([^']*)'");
		String[] rst = { "", "" };
		if(groups.size() >= 4) {
			
			// 不需要输入验证码(直接使用伪验证码)
			if("0".equals(groups.get(0))) {
				rst[0] = groups.get(1);	// 验证�?
				rst[1] = groups.get(3);	// 校验�?
				
			// 需要输入验证码(下载验证码图�?)
			} else if("1".equals(groups.get(0))) {
				rst = takeVcode(groups.get(1));
			}
		}
		
		UIUtils.log("已获得本次登陆的验证�?: ", rst[0]);
		UIUtils.log("已获得本次登陆的校验�?: ", rst[1]);
		return rst;
	}
	
	/**
	 * 获取验证码的请求参数
	 * @return
	 */
	private Map<String, String> _getVcodeRequest() {
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.u1, "https://qzs.qzone.qq.com/qzone/v5/loginsucc.html?para=izone&from=iqq&r=0.7018623383003015&pt_uistyle=40");
		request.put(XHRAtrbt.uin, QQ);
		request.put(XHRAtrbt.login_sig, cookie.SIG());
		request.put(XHRAtrbt.pt_vcode, "1");
		request.put(XHRAtrbt.regmaster, "");
		request.put(XHRAtrbt.pt_tea, "2");
		request.put(XHRAtrbt.appid, "549000912");
		request.put(XHRAtrbt.js_ver, "10215");
		request.put(XHRAtrbt.js_type, "1");
		return request;
	}
	
	/**
	 * 下载验证码图片及其校验码, 同时返回人工输入的验证码
	 * @param vcodeId 用于下载验证码图片的ID
	 * @return new String[] { 验证�?, 校验�? }
	 */
	private String[] takeVcode(String vcodeId) {
		HttpClient client = new HttpClient();
		boolean isOk = client.downloadByGet(Config.VCODE_IMG_PATH, 
				URL.VCODE_IMG_URL, null, _getVcodeRequest(vcodeId));
		XHRUtils.takeResponseCookies(client, cookie);
		client.close();
		
		String[] rst = { "", "" };
		if(isOk == true) {
			rst[0] = SwingUtils.input("请输入登陆验证码: ", Config.VCODE_IMG_PATH);
			rst[1] = cookie.VERIFYSESSION();
		}
		return rst;
	}
	
	/**
	 * 下载验证码图片的请求参数
	 * @return
	 */
	private Map<String, String> _getVcodeRequest(String vcodeId) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.uin, QQ);
		request.put(XHRAtrbt.cap_cd, vcodeId);
		return request;
	}
	
	/**
	 * 对QQ密码做RSA加密
	 * @param vcode	本次登陆的验证码
	 * @return RSA加密后的密码
	 */
	private String encryptPassword(String vcode) {
		String rsaPwd = EncryptUtils.toRSA(QQ, password, vcode);
		UIUtils.log("已加密登陆密�?: ", rsaPwd);
		return rsaPwd;
	}
	
	/**
	 * 登陆.
	 * -----------------
	 * 	登陆成功, 服务器响应：
	 * 		ptuiCB('0','0','https://ptlogin2.qzone.qq.com/check_sig?pttype=1&uin=272629724&service=login&nodirect=0&ptsigx=be9afd54dc7c9b05caf879056d01bff9520c147e19953b9577bf32a4a15b19f1cdfd7ceb17a27939d7596593032d4bcebfb57a4f58ae3ac6d9f078797ad04cd3&s_url=https%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&f_url=&ptlang=2052&ptredirect=100&aid=549000912&daid=5&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=1&pt_aid=0&pt_aaid=0&pt_light=0&pt_3rd_aid=0','0','登录成功�?', 'EXP')
	 * 
	 * 	登陆失败, 服务器响应：
	 * 		ptuiCB('3','0','','0','你输入的帐号或密码不正确，请重新输入�?', '')
	 * 		ptuiCB('4','0','','0','你输入的验证码不正确，请重新输入�?', '')
	 * 		ptuiCB('7','0','','0','提交参数错误，请检查�?(1552982056)', '')
	 * 		ptuiCB('24','0','','0','很遗憾，网络连接出现异常，请你检查是否禁用cookies�?(1479543040)', '')
	 * 
	 * @param rsaPwd RSA加密后的密码
	 * @param vccode 本次登陆的验证码
	 * @param verify 本次登陆的验证码的校验码
	 * @return 	若登陆成�?, 则返回可提取p_skey的回调地址
	 * 			若登陆失败， 则返回失败原�?(或回调函�?)
	 */
	private String login(String rsaPwd, String vcode, String verify) {
		UIUtils.log("正在登陆QQ [", QQ, "] ...");
		
		HttpClient client = new HttpClient();
		Map<String, String> request = _getLoginRequest(rsaPwd, vcode, verify);
		String response = client.doGet(URL.XHR_LOGIN_URL, null, request);
		
		String rst = "";
		List<String> groups = RegexUtils.findBrackets(response, "'([^']*)'");
		if(groups.size() >= 6) {
			int code = NumUtils.toInt(groups.get(0), -1);
			if(code == 0) {
				XHRUtils.takeResponseCookies(client, cookie);
				cookie.setNickName(groups.get(5));
				rst = groups.get(2);	// 登陆成功: 提取p_skey的回调地址
				
			} else {
				rst = groups.get(4);	// 登陆失败原因
			}
		} else {
			rst = response;	// 登陆失败的回调函�?
		}
		client.close();
		return rst;
	}
	
	/**
	 * 获取登陆请求参数
	 * @param rsaPwd
	 * @param vcode
	 * @param verify
	 * @return
	 */
	private Map<String, String> _getLoginRequest(String rsaPwd, String vcode, String verify) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.login_sig, cookie.SIG());
		request.put(XHRAtrbt.u, QQ);
		request.put(XHRAtrbt.p, rsaPwd);
		request.put(XHRAtrbt.verifycode, vcode);
		request.put(XHRAtrbt.pt_verifysession_v1, verify);
		request.put(XHRAtrbt.pt_vcode_v1, PicUtils.isFalsuVcode(vcode) ? "0" : "1");
		request.put(XHRAtrbt.from_ui, "1");		// 重要参数
		request.put(XHRAtrbt.pt_uistyle, "40");	// 重要参数
		request.put(XHRAtrbt.u1, "https://qzs.qq.com/qzone/v5/loginsucc.html?para=izone");
		request.put(XHRAtrbt.pt_randsalt, "2");
		request.put(XHRAtrbt.aid, "549000912");
		request.put(XHRAtrbt.daid, "5");
		request.put(XHRAtrbt.ptredirect, "0");
		request.put(XHRAtrbt.h, "1");
		request.put(XHRAtrbt.t, "1");
		request.put(XHRAtrbt.g, "1");
		request.put(XHRAtrbt.ptlang, "2052");
		request.put(XHRAtrbt.js_ver, "10270");
		request.put(XHRAtrbt.js_type, "1");
		return request;
	}

	/**
	 * 提取本次登陆的GTK与QzoneToken
	 * @param callbackURL 用于提取p_skey的回调地址(p_skey用于计算GTK, GTK用于获取QzoneToken)
	 */
	@Override
	protected boolean takeGTKAndToken(String callbackURL) {
		UIUtils.log("正在提取本次登陆�? GTK �? QzoneToken ...");
		
		// 提取p_skey，并计算GTK:
		// callbackURL是一个存在重定向页面, 一旦访问后会马上重定向到QQ空间首页
		// 但是p_skey只存在于重定向前的页�?
		// 因此要提取p_skey�?, 要么禁止HTTP重定�?, 要么把重定向过程中的所有cookie都记录下�?(此处用的是第2种方�?)
		HttpClient client = new HttpClient();
		Map<String, String> header = XHRUtils.getHeader(cookie);
		client.doGet(callbackURL, header, null);
		XHRUtils.takeResponseCookies(client, cookie);
		UIUtils.log("本次登陆�? GTK: ", cookie.GTK());
		
		// 从QQ空间首页的页面源码中提取QzoneToken
		header = XHRUtils.getHeader(cookie);
		String pageSource = client.doGet(URL.QZONE_HOMR_URL(QQ), header, null);
		String qzoneToken = EncryptUtils.getQzoneToken(pageSource);
		cookie.setQzoneToken(qzoneToken);
		UIUtils.log("本次登陆�? QzoneToken: ", cookie.QZONE_TOKEN());
		
		return StrUtils.isNotEmpty(cookie.GTK(), cookie.QZONE_TOKEN());
	}

}
