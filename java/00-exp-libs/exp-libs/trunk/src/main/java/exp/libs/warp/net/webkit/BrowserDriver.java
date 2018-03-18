package exp.libs.warp.net.webkit;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import exp.libs.utils.os.CmdUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 浏览器驱动
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BrowserDriver {

	/** 浏览器代理头 */
	private final static String USER_AGENT = 
//			"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
	
	/** 等待动态元素的加载时间(单位:s) */
	private final static long WAIT_ELEMENT_SECOND = 5;
	
	/** WEB驱动类型 */
	private WebDriverType type;
	
	/** WEB驱动 */
	private WebDriver webDriver;
	
	/** 针对页面事件进行操作的行为对象 */
	private Actions action;
	
	/**
	 * 构造函数
	 * @param type WEB驱动类型
	 */
	public BrowserDriver(WebDriverType type) {
		this(type, false, WAIT_ELEMENT_SECOND);
	}
	
	/**
	 * 构造函数
	 * @param type WEB驱动类型
	 * @param loadImages 是否加载图片(默认不加载)
	 */
	public BrowserDriver(WebDriverType type, boolean loadImages) {
		this(type, loadImages, WAIT_ELEMENT_SECOND);
	}

	/**
	 * 构造函数
	 * @param type WEB驱动类型
	 * @param loadImages 是否加载图片(默认不加载)
	 * @param waitElementSecond 等待动态元素的加载时间(单位:s)
	 */
	public BrowserDriver(WebDriverType type, boolean loadImages, long waitElementSecond) {
		this.type = type;
		waitElementSecond = (waitElementSecond <= 0 ? 
				WAIT_ELEMENT_SECOND : waitElementSecond);
		
		setWebPropertyToSysEnv();
		initWebDriver(loadImages);
		setWaitElementTime(waitElementSecond);
	}
	
	/**
	 * 设置WEB属性到系统环境
	 */
	private void setWebPropertyToSysEnv() {
		String property = "";
		if(WebDriverType.PHANTOMJS == type) {
			property = PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY;
			
		} else if(WebDriverType.CHROME == type) {
			property = ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY;
			
		} else {
			// Undo
		}
		
		if(StrUtils.isNotEmpty(property)) {
			System.setProperty(property, type.DRIVER_PATH());
		}
	}
	
	/**
	 * 初始化WEB驱动参数
	 * @param loadImages 是否加载图片(默认不加载)
	 */
	public void initWebDriver(boolean loadImages) {
		if(WebDriverType.PHANTOMJS == type) {
			this.webDriver = new PhantomJSDriver(getCapabilities(loadImages));
			
		} else if(WebDriverType.CHROME == type) {
			this.webDriver = new ChromeDriver(getCapabilities(loadImages));
			
		} else {
			this.webDriver = new HtmlUnitDriver(getCapabilities(loadImages));
		}
		this.action = new Actions(webDriver);
	}
	
	/**
	 * 获取WEB驱动的能力参数
	 * @param loadImages
	 * @return
	 */
	private DesiredCapabilities getCapabilities(boolean loadImages) {
		DesiredCapabilities capabilities = null;
		
		if(WebDriverType.PHANTOMJS == type) {
			capabilities = DesiredCapabilities.phantomjs();
			capabilities.setJavascriptEnabled(true);	// 执行页面js脚本
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);	// SSL证书支持
			
			final String PAGE_SETTINGS = PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX;
			capabilities.setCapability(PAGE_SETTINGS.concat("loadImages"), loadImages);		// 加载图片
			capabilities.setCapability(PAGE_SETTINGS.concat("XSSAuditingEnabled"), false);	// 跨域请求监控
			capabilities.setCapability(PAGE_SETTINGS.concat("localToRemoteUrlAccessEnabled"), false);	// 本地资源是否可以访问远程URL
			capabilities.setCapability(PAGE_SETTINGS.concat("userAgent"), USER_AGENT);	// 伪装浏览器
			
//			final String HERDER = PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX;
//			capabilities.setCapability(HERDER.concat("Accept"), "application/json, text/javascript, */*; q=0.01");
//			capabilities.setCapability(HERDER.concat("Content-Type"), "application/x-www-form-urlencoded; charset=UTF-8");
//			capabilities.setCapability(HERDER.concat("Accept-Encoding"), "gzip, deflate, br");
//			capabilities.setCapability(HERDER.concat("Accept-Language"), "zh-CN,zh;q=0.8");
			
		} else if(WebDriverType.CHROME == type) {
			capabilities = DesiredCapabilities.chrome();
			capabilities.setJavascriptEnabled(true);
			capabilities.setCapability("loadImages", loadImages);
			
		} else {
			capabilities = DesiredCapabilities.htmlUnit();
			capabilities.setJavascriptEnabled(true);
		}
		return capabilities;
	}
	
	/**
	 * 隐式等待期望的元素出现
	 * @param second 最长的等待秒数
	 */
	public void setWaitElementTime(long second) {
		webDriver.manage().timeouts().implicitlyWait(second, TimeUnit.SECONDS);	
	}
	
	/**
	 * 获取WEB驱动
	 * @return
	 */
	public WebDriver getDriver() {
		return webDriver;
	}
	
	/**
	 * 获取页面事件的行为操作对象
	 * @return
	 */
	public Actions getAction() {
		return action;
	}
	
	/**
	 * 关闭浏览器（退出浏览器进程）
	 */
	public void quit() {
		try {
			webDriver.quit();
		} catch(Throwable e) {}
		
		// 以防万一, 使用系统命令杀掉驱动进程 （Chrome只能通过此方法）
		if(WebDriverType.PHANTOMJS != type) {
			CmdUtils.kill(type.DRIVER_NAME());
		}
	}
	
	/**
	 * 关闭当前页面（若所有页面都被关闭，则自动退出浏览器进程）
	 */
	public void close() {
		try {
			webDriver.close();
		} catch(Throwable e) {}
	}
	
	/**
	 * 打开页面
	 * @param url 页面URL
	 */
	public void open(String url) {
		webDriver.navigate().to(url);
	}
	
	/**
	 * 刷新当前页面
	 */
	public void refresh() {
		webDriver.navigate().refresh();
	}
	
	/**
	 * 获取当前页面路径
	 * @return
	 */
	public String getCurURL() {
		String url = "";
		try {
			url = webDriver.getCurrentUrl();
		} catch(Throwable e) {}
		return url;
	}
	
	/**
	 * 获取页面源码
	 * @return
	 */
	public String getPageSource() {
		String ps = "";
		try {
			ps = webDriver.getPageSource();
		} catch(Throwable e) {}
		return ps;
	}
	
	/**
	 * 清空cookies
	 */
	public void clearCookies() {
		try {
			webDriver.manage().deleteAllCookies();
		} catch(Throwable e) {}
	}
	
	/**
	 * 添加cookie
	 * @param cookie
	 * @return
	 */
	public boolean addCookie(Cookie cookie) {
		boolean isOk = true;
		try {
			webDriver.manage().addCookie(cookie);
		} catch(Exception e) {
			isOk = false;
		}
		return isOk;
	}
	
	/**
	 * 添加cookie集
	 * @param cookies cookie集
	 * @return
	 */
	public boolean addCookies(Set<Cookie> cookies) {
		boolean isOk = true;
		if(cookies == null) {
			isOk = false;
			
		} else {
			for(Cookie cookie : cookies) {
				isOk &= addCookie(cookie);
			}
		}
		return isOk;
	}
	
	/**
	 * 获取cookie集
	 * @return
	 */
	public Set<Cookie> getCookies() {
		return webDriver.manage().getCookies();
	}
	
	/**
	 * 获取页面元素
	 * @param by 元素位置
	 * @return 若不存在返回null
	 */
	public WebElement findElement(By by) {
		WebElement element = null;
		try {
			element = webDriver.findElement(by);
		} catch(Throwable e) {}
		return element;
	}

	/**
	 * 点击按钮并提交
	 * @param button 页面按钮元素
	 */
	public void click(WebElement button) {
		action.click(button).perform();	// 点击并提交
	}
	
	/**
	 * 提交数据到页面表单元素
	 * @param form 页面表单元素
	 * @param msg 表单数据
	 */
	public void send(WebElement form, String msg) {
		action.sendKeys(form, msg, Keys.ENTER, Keys.NULL).perform();
	}
	
	/**
	 * 对浏览器的当前页面截图
	 * @param imgPath 图片保存路径
	 */
	public void screenshot(String imgPath) {
		WebUtils.screenshot(webDriver, imgPath);
	}
	
	/**
	 * 保存当前页面（包括页面截图和页面源码）
	 * @param saveDir 保存目录
	 * @param saveName 保存名称
	 */
	public void saveCurPage(String saveDir, String saveName) {
		WebUtils.saveCurPage(webDriver, saveDir, saveName);
	}
	
}
