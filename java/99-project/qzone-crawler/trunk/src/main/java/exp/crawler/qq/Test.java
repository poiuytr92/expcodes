package exp.crawler.qq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import exp.crawler.qq.bean.QQCookie;
import exp.libs.utils.other.JSUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

// https://blog.csdn.net/M_S_W/article/details/70193899
public class Test {

	public static void main(String[] args) {
		String qq = "272629724";
		String pwd = "dfmlLMH5222";
		
		QQCookie cookie = step1();
		String[] rst = step2(cookie, qq);
		String enPwd = step3(rst[0], rst[1], qq, pwd);
		login(cookie, qq, enPwd, rst[0], rst[1]);
	}
	
	// 获取初始COOKIE和SIG
	// 注：获取返回的cookie和cookie里面的pt_login_sig值（以后用到）
	private static QQCookie step1() {
		String url = "https://xui.ptlogin2.qq.com/cgi-bin/xlogin";
		Map<String, String> request = new HashMap<String, String>();
		request.put("proxy_url", "https://qzs.qq.com/qzone/v6/portal/proxy.html");
		request.put("daid", "5");
		request.put("hide_title_bar", "1");
		request.put("low_login", "0");
		request.put("qlogin_auto_login", "1");
		request.put("no_verifyimg", "1");
		request.put("link_target", "blank");
		request.put("appid", "549000912");
		request.put("style", "22");
		request.put("target", "self");
		request.put("s_url", "https://qzs.qzone.qq.com/qzone/v5/loginsucc.html?para=izone&from=iqq");
		request.put("pt_qr_app", "手机QQ空间");
		request.put("pt_qr_link", "http://z.qzone.com/download.html");
		request.put("self_regurl", "https://qzs.qq.com/qzone/v6/reg/index.html");
		request.put("pt_qr_help_link", "http://z.qzone.com/download.html");
		request.put("pt_no_auth", "0");
		
		
		HttpClient client = new HttpClient();
		String response = client.doGet(url, null, request);
//		System.out.println(response);
		
		QQCookie cookie = new QQCookie();
		HttpMethod method = client.getHttpMethod();
		if(method != null) {
			
			/**
			 *   pt_user_id=338691778658345467; EXPIRES=Mon, 20-Mar-2028 07:41:40 GMT; PATH=/; DOMAIN=ui.ptlogin2.qq.com;
			 * , pt_login_sig=AO-f3u2Yh0jYtjSSoTp9dHgzJWWrE8*C4f-V0T1gzginpaQFl9lcsCCmNRwbwEWP; PATH=/; DOMAIN=ptlogin2.qq.com;
			 * , pt_clientip=f1fddf683f4c9834; PATH=/; DOMAIN=ptlogin2.qq.com;
			 * , pt_serverip=c5510ab19b5dc49f; PATH=/; DOMAIN=ptlogin2.qq.com;
			 * , pt_local_token=240484663; PATH=/; DOMAIN=ptlogin2.qq.com;
			 * , uikey=90ac463826f1fd3a352dbbbd3debefcb6437ed89a91bae36a674065dbdef2f35; PATH=/; DOMAIN=ptlogin2.qq.com;
			 * , pt_guid_sig=c42d3952d08bbf7221217b6437915795729c9d6cdb01e9c186a1e5aa70302d12; EXPIRES=Sun, 22-Apr-2018 07:41:40 GMT; PATH=/; DOMAIN=ptlogin2.qq.com;
			 * , ptui_identifier=000D50ECA596CA04BED01EC088C8C0D0C987F313BEBDC4EF29913265; PATH=/; DOMAIN=ui.ptlogin2.qq.com;
			 */
			
			Header[] outHeaders = method.getResponseHeaders();
			for(Header outHeader : outHeaders) {
				if(HttpUtils.HEAD.KEY.SET_COOKIE.equals(outHeader.getName())) {
					cookie.add(outHeader.getValue());
				}
			}
		}
		client.close();
		return cookie;
	}
	
	// 判断是否需要验证码
	// 注：如果不需要验证码则返回和下面类似的信息，否则就等一会再登陆直到不需要验证码时登陆
	//	（我太懒就不抓验证码的包了，主要是我登陆十来次都不需要验证码抓不到啊）
	//	ptui_checkVC('0','!WCC','\x00\x00\x00\x00\x99\x07\xcc\x00','d4d6dc3ea578f17560481d67ce9070daec5bd11573a1b9cf0de8204a363b5acec30d204e240327532a59464371f08d6adb93188c8930e007','2');
	// 在注：如果不需要验证码，则上面的!WCC就相当于验证码（取出来），
	//	 后面的d4d6dc3ea578f17560481d67ce9070daec5bd11573a1b9cf0de8204a363b5acec30d204e240327532a59464371f08d6adb93188c8930e007
	// 也需要取出来构造登陆网址中的pt_verifysession_v1
	private static String[] step2(QQCookie cookie, String loginQQ) {
		String url = "https://ssl.ptlogin2.qq.com/check";
		Map<String, String> request = new HashMap<String, String>();
		request.put("regmaster", "");
		request.put("pt_tea", "2");
		request.put("pt_vcode", "1");
		request.put("uin", loginQQ);
		request.put("appid", "549000912");
		request.put("js_ver", "10215");
		request.put("js_type", "1");
		request.put("login_sig", cookie.SIG());
		request.put("u1", "https://qzs.qzone.qq.com/qzone/v5/loginsucc.html?para=izone&from=iqq&r=0.7018623383003015&pt_uistyle=40");
		
		String response = HttpURLUtils.doGet(url, null, request);
		System.out.println(response);
		// ptui_checkVC('0','!VAB','\x00\x00\x00\x00\x10\x3f\xff\xdc','cefb41782ce53f614e7665b5519f9858c80ab8925b8060d7a790802212da7205be1916ac4d45a77618c926c6a5fb330520b741d749519f33','2')
		
		List<String> rst = RegexUtils.findBrackets(response, "'([^']*)'");
		return new String[] { rst.get(1), rst.get(3) };
	}
	
	// 加密算法
	// 直接调用js中的jiami函数就可以了，参数分别为 [密码|QQ号|验证码|这个填空白文本就行]
	// QQ空间登录加密JS获取分析
	// https://baijiahao.baidu.com/s?id=1570118073573921&wfr=spider&for=pc
	private static String step3(String vccode, String pt_verifysession_v1, String qq, String pwd) {
		String jsPath = "./conf/MD5-RSA.js";
		String method = "toRSA";
		
		Object rst = JSUtils.executeJS(jsPath, method, pwd, qq, vccode, "");
		return (rst == null ? "" : rst.toString());	// 加密后的密码
	}
	
	private static void login(QQCookie cookie, String qq, String enPwd, 
			String vccode, String pt_verifysession_v1) {
		System.out.println(qq);
		System.out.println(enPwd);
		System.out.println(vccode);
		System.out.println(pt_verifysession_v1);
		
		String url = "https://ssl.ptlogin2.qq.com/login";
		Map<String, String> request = new HashMap<String, String>();
		request.put("u", qq);
		request.put("verifycode", vccode);
		request.put("pt_vcode_v1", pt_verifysession_v1);
		request.put("p", enPwd);
		request.put("pt_randsalt", "2");
		request.put("u1", "https://qzs.qzone.qq.com/qzone/v5/loginsucc.html?para=izone%26from=iqq&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=3-16-1492259455546&js_ver=10215&js_type=1");
		request.put("login_sig", cookie.SIG());
		request.put("pt_uistyle", "4");
		request.put("aid", "549000912");
		request.put("daid", "5");
		
		String response = HttpURLUtils.doGet(url, null, request);
		System.out.println(response);
	}
	
}
