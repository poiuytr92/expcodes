package exp.qw.cache;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import exp.libs.warp.net.webkit.BrowserDriver;
import exp.libs.warp.net.webkit.WebDriverType;
import exp.qw.bean.QQCookie;


/**
 * <PRE>
 * 浏览器驱动管理器
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Browser {
	
	private BrowserDriver browser;
	
	private QQCookie qqCookie;
	
	private static volatile Browser instance;
	
	private Browser() {
		this.qqCookie = new QQCookie();
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
		_backupCookies();
		_quit();
		browser = new BrowserDriver(WebDriverType.PHANTOMJS, loadImages);
		_recoveryCookies();
	}
	
	public static WebDriver DRIVER() {
		return INSTN()._DRIVER();
	}
	
	private WebDriver _DRIVER() {
		return (browser == null ? null : browser.getDriver());
	}
	
	public static QQCookie COOKIE() {
		return INSTN()._COOKIE();
	}
	
	private QQCookie _COOKIE() {
		return qqCookie;
	}
	
	public static String GTK() {
		return INSTN()._GTK();
	}
	
	private String _GTK() {
		return qqCookie.GTK();
	}
	
	public static void open(String url) {
		INSTN()._open(url);
	}
	
	private void _open(String url) {
		if(browser == null){
			_reset(true);
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
	
	public static void backupCookies() {
		INSTN()._backupCookies();
	}
	
	private void _backupCookies() {
		if(browser != null) {
			qqCookie.clear();
			qqCookie.add(browser.getCookies());
		}
	}
	
	public static int recoveryCookies() {
		return INSTN()._recoveryCookies();
	}
	
	private int _recoveryCookies() {
		int cnt = 0;
		if(browser != null) {
			cnt = qqCookie.size();
			
			browser.clearCookies();
			browser.addCookies(qqCookie.toSeleniumCookies());
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

	/**
	 * 保存当前页面（包括页面截图和页面源码）
	 * @param saveDir 保存目录
	 * @param saveName 保存名称
	 */
	public static void saveCurPage(String saveDir, String saveName) {
		INSTN()._saveCurPage(saveDir, saveName);
	}
	
	/**
	 * 保存当前页面（包括页面截图和页面源码）
	 * @param saveDir 保存目录
	 * @param saveName 保存名称
	 */
	private void _saveCurPage(String saveDir, String saveName) {
		if(browser != null) {
			browser.saveCurPage(saveDir, saveName);
		}
	}
	
}
