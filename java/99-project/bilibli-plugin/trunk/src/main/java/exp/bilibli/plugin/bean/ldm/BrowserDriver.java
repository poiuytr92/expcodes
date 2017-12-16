package exp.bilibli.plugin.bean.ldm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.envm.WebDriverType;
import exp.libs.utils.os.CmdUtils;
import exp.libs.utils.other.StrUtils;

final public class BrowserDriver {

	private final static Logger log = LoggerFactory.getLogger(BrowserDriver.class);
	
	private final static long WAIT_ELEMENT_TIME = 5;
	
	private WebDriverType type;
	
	private WebDriver webDriver;
	
	public BrowserDriver(WebDriverType type) {
		this(type, WAIT_ELEMENT_TIME);
	}
	
	public BrowserDriver(WebDriverType type, long waitElementSecond) {
		this.type = type;
		waitElementSecond = (waitElementSecond <= 0 ? 
				WAIT_ELEMENT_TIME : waitElementSecond);
		
		initProperty();
		initWebDriver();
		setWaitElementTime(waitElementSecond);
	}
	
	private void initProperty() {
		String property = "";
		if(WebDriverType.PHANTOMJS == type) {
			property = "phantomjs.binary.path";
			
		} else if(WebDriverType.CHROME == type) {
			property = "webdriver.chrome.driver";
			
		} else {
			// Undo
		}
		
		if(StrUtils.isNotEmpty(property)) {
			System.setProperty(property, type.DRIVER_PATH());
		}
	}
	
	public void initWebDriver() {
		if(WebDriverType.PHANTOMJS == type) {
			this.webDriver = new PhantomJSDriver(getCapabilities());
			
		} else if(WebDriverType.CHROME == type) {
			this.webDriver = new ChromeDriver(getCapabilities());
			
		} else {
			this.webDriver = new HtmlUnitDriver(true);
		}
	}
	
	// {"XSSAuditingEnabled":false,"javascriptCanCloseWindows":true,"javascriptCanOpenWindows":true,"javascriptEnabled":true,"loadImages":true,"localToRemoteUrlAccessEnabled":false,"userAgent":"Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1","webSecurityEnabled":true}
	private DesiredCapabilities getCapabilities() {
		DesiredCapabilities capabilities = null;
		
		if(WebDriverType.PHANTOMJS == type) {
			capabilities = DesiredCapabilities.phantomjs();
			capabilities.setJavascriptEnabled(true);
			capabilities.setCapability("XSSAuditingEnabled", true);
			capabilities.setCapability("loadImages", false);
			capabilities.setCapability("localToRemoteUrlAccessEnabled", true);
			
			// 假装是谷歌，避免被反爬
			capabilities.setCapability("userAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36");
			
		} else if(WebDriverType.CHROME == type) {
			Map<String, Object> defaultContentSettings = new HashMap<String, Object>();
			defaultContentSettings.put("images", 2);	// 不显示图片, 不知为何不生效

			Map<String, Object> profile = new HashMap<String, Object>();
			profile.put("profile.default_content_settings", defaultContentSettings);

			capabilities = DesiredCapabilities.chrome();
			capabilities.setJavascriptEnabled(true);
			capabilities.setCapability("loadImages", false);
			capabilities.setCapability("chrome.prefs", profile);
			
		} else {
			capabilities = DesiredCapabilities.htmlUnit();
			capabilities.setJavascriptEnabled(true);
		}
		return capabilities;
	}
	
	public WebDriver getDriver() {
		return webDriver;
	}
	
	public void open(String url) {
		webDriver.navigate().to(url);
	}
	
	/**
	 * 隐式等待期望的元素出现
	 * @param second 最长的等待秒数
	 */
	public void setWaitElementTime(long second) {
		webDriver.manage().timeouts().implicitlyWait(second, TimeUnit.SECONDS);	
	}
	
	/**
	 * 刷新页面
	 */
	public void refresh() {
		webDriver.navigate().refresh();
	}
	
	/**
	 * 关闭浏览器
	 */
	public void close() {
		// FXIME: 正常退出的方法？？？
		if(WebDriverType.PHANTOMJS == type) {
			CmdUtils.kill(type.DRIVER_NAME());
			
		// Chrome 浏览器进程需要用系统命令终止
		} else if(WebDriverType.CHROME == type) {
			CmdUtils.kill(type.DRIVER_NAME());
			
		} else {
			// Undo
		}
		
		try {
			webDriver.close();
		} catch(Throwable e) {
			log.error("关闭浏览器失败.", e);
		}
	}
	
}
