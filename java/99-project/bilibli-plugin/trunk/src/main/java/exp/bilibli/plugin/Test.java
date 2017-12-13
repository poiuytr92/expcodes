package exp.bilibli.plugin;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import exp.bilibli.plugin.bean.ldm.BrowserDriver;

public class Test {

	private final static String URL = "http://live.bilibili.com/438";
	
	public static void main(String[] args) {
		BrowserDriver browser = BrowserDriver.PHANTOMJS;
		WebDriver driver = browser.getWebDriver();
		
		
//		// 获取所有cookie个数
//		System.out.println(driver.manage().getCookies().size());
//		
//		// 增加cookie
//		Cookie username = new Cookie("username", "289065406@qq.com", "/", null);
//		Cookie password = new Cookie("password", "liao5422", "/", null);
//		
//		driver.manage().addCookie(username);
//		driver.manage().addCookie(password);
//		
		driver.get(URL);
		
	}
}
