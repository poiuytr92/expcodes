package exp.blp.bean;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import exp.libs.utils.os.CmdUtils;
import exp.libs.utils.pub.StrUtils;

final public class BrowserDriver {

	private final static String DRIVER_DIR = "./lib/driver/";
	
	// JS属于内存浏览器，对JQuery支持不好
	private final static String PhantomJS = "phantomjs-driver.exe";
	public final static BrowserDriver PHANTOMJS = new BrowserDriver(PhantomJS);
	
	// chrom属于真实浏览器，对JS、JQuery支持很好，但是无法正常关闭进行，必须通过命令行关闭
	private final static String Chrome = "chrome-driver.exe";
	public final static BrowserDriver CHROME = new BrowserDriver(Chrome);
	
	private String driverName;
	
	private BrowserDriver(String driverName) {
		this.driverName = driverName;
		init();
	}
	
	private void init() {
		String property = "";
		if(PhantomJS.equals(driverName)) {
			property = "phantomjs.binary.path";
			
		} else if(Chrome.equals(driverName)) {
			property = "webdriver.chrome.driver";
			
		} else {
			// Undo
		}
		
		if(StrUtils.isNotEmpty(property)) {
			System.setProperty(property, StrUtils.concat(DRIVER_DIR, driverName));
		}
	}
	
	private DesiredCapabilities getCapabilities() {
		DesiredCapabilities capabilities = null;
		
		if(PhantomJS.equals(driverName)) {
			capabilities = DesiredCapabilities.phantomjs();
			capabilities.setJavascriptEnabled(true);
			
		} else if(Chrome.equals(driverName)) {
			Map<String, Object> defaultContentSettings = new HashMap<String, Object>();
			defaultContentSettings.put("images", 2);	// 不显示图片, 不知为何不生效

			Map<String, Object> profile = new HashMap<String, Object>();
			profile.put("profile.default_content_settings", defaultContentSettings);

			capabilities = DesiredCapabilities.chrome();
			capabilities.setJavascriptEnabled(true);
			capabilities.setCapability("chrome.prefs", profile);
			
		} else {
			capabilities = DesiredCapabilities.htmlUnit();
			capabilities.setJavascriptEnabled(true);
		}
		return capabilities;
	}
	
	public WebDriver getWebDriver() {
		WebDriver webDriver = null;
		if(PhantomJS.equals(driverName)) {
			webDriver = new PhantomJSDriver(getCapabilities());
			
		} else if(Chrome.equals(driverName)) {
			webDriver = new ChromeDriver(getCapabilities());
			
		} else {
			webDriver = new HtmlUnitDriver(true);
		}
		return webDriver;
	}
	
	public void refresh(WebDriver webDriver) {
		if(webDriver == null) {
			return;
		}
		
		// PhantomJS浏览器不会持续执行JS脚本，只能刷新页面实现
		if(PhantomJS.equals(driverName)) {
			webDriver.navigate().refresh();	
			
		} else if(Chrome.equals(driverName)) {
			// Undo 
			
		} else {
			// Undo 
		}
	}
	
	public void close(WebDriver webDriver) {
		if(webDriver == null) {
			return;
		}
		
		if(PhantomJS.equals(driverName)) {
			CmdUtils.kill(driverName);
			webDriver.close();
			
		// Chrome 浏览器进程需要用系统命令终止
		} else if(Chrome.equals(driverName)) {
			CmdUtils.kill(driverName);
			webDriver.close();
			
		} else {
			webDriver.close();
		}
	}
	
}
