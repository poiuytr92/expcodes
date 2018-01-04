package exp.bilibli.plugin;

import java.util.HashMap;
import java.util.Map;

import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Test {

	public static void main(String[] args) {
		
//		postDict = {
//		        'userid'      :username,
//		        'pwd'      : password,
//		        'vdcode'   : vdcode
//		    }
		
		Map<String, String> head = new HashMap<String, String>();
		head.put(HttpUtils.HEAD.KEY.COOKIE, "JSESSIONID=297299D97A724BDE56EBFB7B983EA9D0; sid=jelg7ubk; ");
		
		Map<String, String> rp = new HashMap<String, String>();
		rp.put("user", "272629724@qq.com");
		rp.put("pwd", "");
		rp.put("captcha", "FXZFG");
		String json = HttpURLUtils.doPost("https://passport.bilibili.com/web/login", head, rp);
		System.out.println(json);
		
//		boolean isOk = HttpURLUtils.downloadByGet("./log/captcha.png", "https://passport.bilibili.com/captcha?t=", null, null);
//		System.out.println(isOk);
		
//		Browser.init(true);
//		
//		
//		
////		Browser.open("https://passport.bilibili.com/captcha?t=");
//		
//		
//		
//		Browser.open("https://passport.bilibili.com/login");
//		
//		WebElement username = Browser.findElement(By.id("login-username"));
//		username.sendKeys("272629724@qq.com");
//		
//		WebElement password = Browser.findElement(By.id("login-passwd"));
//		password.sendKeys("liao5422");
//		
//		Browser.screenshot("./log/1.jpg");
//		
//		WebElement btn = Browser.findElement(By.className("btn-login"));
//		btn.click();
//		
////		WebElement we = Browser.findElement(By.id("gc-box"));
////		we.click();
//		
////		FileUtils.write("./log/ps.html", Browser.getPageSource());
//		Browser.screenshot("./log/2.jpg");
//		Browser.quit();
	}
	
}
