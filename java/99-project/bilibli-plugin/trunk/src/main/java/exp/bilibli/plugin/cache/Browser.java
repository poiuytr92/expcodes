package exp.bilibli.plugin.cache;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.envm.WebDriverType;
import exp.libs.utils.other.ObjUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 浏览器驱动管理器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Browser {

	private final static String COOKIE_DIR = Config.getInstn().COOKIE_DIR();
	
	private final static String LIVE_URL = Config.getInstn().LIVE_URL();
	
	private final static int WAIT_ELEMENT_TIME = Config.getInstn().WAIT_ELEMENT_TIME();
	
	private String uid;
	
	private String sCookies;
	
	private BrowserDriver browser;
	
	private static volatile Browser instance;
	
	private Browser() {
		this.uid = "";
		this.sCookies = "";
	}
	
	private static Browser INSTN() {
		if(instance == null) {
			synchronized (Browser.class) {
				if(instance == null) {
					instance = new Browser();
				}
			}
		}
		return instance;
	}
	
	public static void init(boolean loadImages) {
		INSTN()._reset(loadImages);
	}
	
	public static void reset(boolean loadImages) {
		INSTN()._reset(loadImages);
	}
	
	/**
	 * 重置浏览器驱动
	 * @param loadImages
	 * @return
	 */
	private void _reset(boolean loadImages) {
		backupCookies();
		quit();
		browser = new BrowserDriver(WebDriverType.PHANTOMJS, 
				loadImages, WAIT_ELEMENT_TIME);
		recoveryCookies();
	}
	
	public static void open(String url) {
		INSTN()._open(url);
	}
	
	private void _open(String url) {
		if(browser == null){
			_reset(false);
		}
		browser.open(url);
	}
	
	public static void refresh() {
		INSTN()._refresh();
	}
	
	private void _refresh() {
		if(browser != null){
			browser.refresh();
		}
	}
	
//	public static void close() {
//		INSTN()._close();
//	}
//	
//	/**
//	 * 关闭当前页面(若是最后一个页面, 则会关闭浏览器)
//	 */
//	private void _close() {
//		if(browser != null) {
//			browser.close();
//		}
//	}
	
	public static void quit() {
		INSTN()._quit();
	}
	
	/**
	 * 退出浏览器
	 */
	private void _quit() {
		if(browser != null) {
			browser.quit();
			browser = null;
		}
	}
	
	public static String getCurURL() {
		return INSTN()._getCurURL();
	}
	
	private String _getCurURL() {
		return (browser == null ? "" : browser.getCurURL());
	}
	
	public static String getPageSource() {
		return INSTN()._getPageSource();
	}
	
	private String _getPageSource() {
		return (browser == null ? "" : browser.getPageSource());
	}
	
	public static void clearCookies() {
		INSTN()._clearCookies();
	}
	
	private void _clearCookies() {
		if(browser != null) {
			browser.clearCookies();
		}
	}
	
	public static boolean addCookie(Cookie cookie) {
		return INSTN()._addCookie(cookie);
	}
	
	private boolean _addCookie(Cookie cookie) {
		return (browser == null ? false : browser.addCookie(cookie));
	}
	
	public static Set<Cookie> getCookies() {
		return INSTN()._getCookies();
	}
	
	private Set<Cookie> _getCookies() {
		return (browser == null ? new HashSet<Cookie>() : browser.getCookies());
	}
	
	public static void backupCookies() {
		INSTN()._backupCookies();
	}
	
	private void _backupCookies() {
		if(browser != null) {
			int idx = 0;
			Set<Cookie> cookies = browser.getCookies();
			for(Cookie cookie : cookies) {
				String sIDX = StrUtils.leftPad(String.valueOf(idx++), '0', 2);
				String savePath = StrUtils.concat(COOKIE_DIR, "/cookie-", sIDX, ".dat");
				ObjUtils.toSerializable(cookie, savePath);
			}
		}
	}
	
	public static int recoveryCookies() {
		return INSTN()._recoveryCookies();
	}
	
	private int _recoveryCookies() {
		int cnt = 0;
		if(browser != null) {
			File dir = new File(COOKIE_DIR);
			File[] files = dir.listFiles();
			for(File file : files) {
				try {
					Cookie cookie = (Cookie) ObjUtils.unSerializable(file.getPath());
					cnt += (browser.addCookie(cookie) ? 1 : 0);
				} catch(Throwable e) {}
			}
		}
		return cnt;
	}
	
	public static boolean existElement(By by) {
		return INSTN()._existElement(by);
	}
	
	private boolean _existElement(By by) {
		return (_findElement(by) != null);
	}
	
	public static WebElement findElement(By by) {
		return INSTN()._findElement(by);
	}
	
	private WebElement _findElement(By by) {
		return (browser == null ? null : browser.findElement(by));
	}
	
	public static void click(WebElement element) {
		INSTN()._click(element);
	}
	
	private void _click(WebElement element) {
		if(browser != null) {
			browser.click(element);
		}
	}
	
	/**
	 * 使浏览器跳转到指定页面后截图
	 * @param driver 浏览器驱动
	 * @param url 跳转页面
	 * @param imgPath 图片保存路径
	 */
	public static void screenshot(String imgPath) {
		INSTN()._screenshot(imgPath);
	}
	
	/**
	 * 对浏览器的当前页面截图
	 * @param imgPath 图片保存路径
	 */
	private void _screenshot(String imgPath) {
		if(browser != null) {
			browser.screenshot(imgPath);
		}
	}
	
	public static boolean toLiveChat(String msg) {
		return INSTN()._toLiveChat(msg);
	}
	
	/**
	 * 向直播间发送一条消息
	 * @param msg 消息
	 * @return 是否发送成功
	 */
	private boolean _toLiveChat(String msg) {
		boolean isOk = false;
		if(browser == null || !browser.getCurURL().startsWith(LIVE_URL)) {
			return isOk;
		}
		
//		// FIXME 暂时无法发送到服务器
//		if(true) {
//			UIUtils.chat(msg + " -- offline");
//			return isOk;
//		}
		
//		String js = "var danmu=document.getElementsByTagName('textarea'); danmu.value='" + msg + "';";
//		((JavascriptExecutor) browser.getDriver()).executeScript(js);
		
		WebElement ctrl = findElement(By.id("chat-control-panel-vm"));
		if(ctrl != null) {
			isOk = true;
			WebElement input = ctrl.findElement(By.className("chat-input-ctnr"));
			WebElement textarea = input.findElement(By.tagName("textarea"));
//			browser.send(textarea, msg);
			textarea.clear();
			textarea.sendKeys(msg);
			textarea.sendKeys(Keys.ENTER);	// 按下回车
			textarea.sendKeys(Keys.NULL);	// 释放回车
			
//			WebElement button = ctrl.findElement(By.className("bottom-actions"));
//			WebElement btn = button.findElement(By.tagName("button"));
//			btn.click();
		}
		return isOk;
	}
	
	public static String printCookies() {
		return INSTN()._printCookies();
	}
	
	/**
	 * Cookie: 
	 * DedeUserID=31796320; 
	 * DedeUserID__ckMd5=7a2868581681a219; 
	 * JSESSIONID=C3FB41FD0469F79CCA68F21B0D49CCD0; 
	 * SESSDATA=b21f4571%2C1516346159%2C790f3250; 
	 * bili_jct=2eec061dc11ee2ff6474b678f8bd3882; 
	 * sid=b6lege44\r\n
	 */
	private String _printCookies() {
		StringBuilder sb = new StringBuilder();
		Set<Cookie> cookies = browser.getCookies();
		for(Cookie cookie : cookies) {
//			System.out.println("name=" + cookie.getName());
//			System.out.println("value=" + cookie.getValue());
			sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
//			System.out.println("domain=" + cookie.getDomain());
//			System.out.println("path=" + cookie.getPath());
//			System.out.println("expiry=" + cookie.getExpiry());
//			System.out.println("====");
		}
		if(sb.length() > 0) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
	}
	
}
