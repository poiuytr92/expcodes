package exp.bilibli.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import Decoder.BASE64Decoder;
import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.ObjUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Test {

	private final static String LOGIN_URL = "https://passport.bilibili.com/login";
	
	private final static String HOME_URL = "https://www.bilibili.com/";
	
	private final static String LIVE_URL = "http://live.bilibili.com/438";
	
	public static void main(String[] args) {
		BrowserDriver browser = BrowserDriver.PHANTOMJS;
		WebDriver driver = browser.getWebDriver(30);
		
		String cookiePath = "./data/cookies/cookie-0.dat";
		if(!FileUtils.exists(cookiePath)) {
			driver.get(LOGIN_URL);	
			login(driver);
			
		} else {
			// FIXME 检查cookie是否还有效
			System.out.println(driver.manage().getCookies().size());
			driver.manage().deleteAllCookies();
		}
		
		
		System.out.println("正在加载cookies");
		File dir = new File("./data/cookies");
		File[] cFiles = dir.listFiles();
		for(File cFile : cFiles) {
			Cookie cookie = (Cookie) ObjUtils.unSerializable(cFile.getAbsolutePath());
			driver.manage().addCookie(cookie);
		}
		System.out.println("加载cookies完成");
		driver.navigate().to(LIVE_URL);
	}

	private static void login(WebDriver driver) {
		WebElement img = driver.findElement(By.xpath("//div[@class='qrcode-img'][1]/img"));
		String imgUrl = img.getAttribute("src");
		boolean isOk = HttpUtils.convertBase64Img(imgUrl, "./log", "qrcode");
		System.out.println("登陆二维码图片保存:" + isOk);
		
		System.out.println("等待登陆");
		ThreadUtils.tSleep(30000);
		System.out.println("登陆成功，正在切换页面");	// FIXME: 可改成让用户扫码后确认
		
		driver.navigate().to(HOME_URL);
		System.out.println(driver.manage().getCookies().size());
		
		System.out.println("正在保存cookies");
		int cnt = 0;
		for(Cookie cookie : driver.manage().getCookies()) {
			System.out.println(cookie.getDomain());
			System.out.println(cookie.getName());
			System.out.println(cookie.getPath());
			System.out.println(cookie.getValue());
			System.out.println(cookie.getExpiry());
			
			ObjUtils.toSerializable(cookie, StrUtils.concat("./data/cookies/cookie-", cnt++, ".dat"));
		}
       System.out.println("保存完成");
	}
	
	

}
