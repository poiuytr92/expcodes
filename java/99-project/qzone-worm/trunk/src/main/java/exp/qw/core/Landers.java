package exp.qw.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.webkit.WebUtils;
import exp.qw.cache.Browser;

/**
 * <PRE>
 * 登陆器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Landers {
	
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(Landers.class);
	
	/** QQ空间登陆页面 */
	private final static String LOGIN_URL = "http://qzone.qq.com/";
	
	/** 行为休眠间隔 */
	private final static long SLEEP_TIME = 50;
	
	/** 登陆超时 */
	private final static long TIMEOUT = 5000;
	
	/**
	 * 登陆QQ空间
	 * @param username
	 * @param password
	 * @param QZONE_URL
	 * @return
	 */
	public static boolean toLogin(String username, String password, final String QZONE_URL) {
		boolean isOk = false;
		try {
			isOk = _toLogin(username, password, QZONE_URL);
			log.info("登陆QQ [{}] {}", username, (isOk ? "成功" : "失败: 账号或密码错误"));
			
		} catch(Exception e) {
			log.error("登陆QQ [{}] 异常", username, e);
		}
		return isOk;
	}
	
	/**
	 * 登陆QQ空间
	 * @param username
	 * @param password
	 * @param QZONE_URL
	 * @return
	 */
	private static boolean _toLogin(String username, String password, final String QZONE_URL) {
		Browser.open(LOGIN_URL);
		switchLoginMode();	// 获取帐密登陆frame的WEB驱动
		ThreadUtils.tSleep(SLEEP_TIME);
		
		fill("u", username);
		fill("p", password);
		ThreadUtils.tSleep(SLEEP_TIME);
		
		boolean isOk = clickLoginBtn();
		if(isOk == true) {
			Browser.open(QZONE_URL);
			skipTitle();
//			skipTips();
		}
		return isOk;
	}
	
	/**
	 * 切换登陆方式
	 * 	。
	 * 
	 * @return frame驱动
	 */
	private static void switchLoginMode() {
		
		// QQ空间的【登陆操作界面】是通过【iframe】嵌套在【登陆页面】中的子页面
		WebUtils.switchToFrame(Browser.DRIVER(), By.id("login_frame"));
		
		// 切换帐密登陆方式为 [帐密登陆]
		WebElement switchBtn = Browser.findElement(By.id("switcher_plogin"));
		switchBtn.click();
	}
	
	/**
	 * 填写输入框的值
	 * @param name 输入框名称
	 * @param value 填写到输入框的值
	 */
	private static void fill(String name, String value) {
		WebElement input = Browser.findElement(By.id(name));
		input.clear();
		input.sendKeys(value);
	}
	
	/**
	 * 点击登陆按钮
	 * @param frame
	 * @return
	 */
	private static boolean clickLoginBtn() {
		final String URL = Browser.getCurURL();	// 登录前URL
		
		// 点击登陆按钮
		WebElement loginBtn = Browser.findElement(By.id("login_button"));
		loginBtn.click();
		
		// 轮询是否登陆成功（发生页面切换）
		boolean isOk = true;
		long bgnTime = System.currentTimeMillis();
		while(URL.equals(Browser.getCurURL())) {
			ThreadUtils.tSleep(SLEEP_TIME);
			
			if(System.currentTimeMillis() - bgnTime >= TIMEOUT) {
				isOk = false;
				break;
			}
		}
		
		// 备份登陆cookies
		if(isOk == true) {
			Browser.backupCookies();
			isOk = StrUtils.isNotEmpty(Browser.GTK());
		}
		return isOk;
	}
	
	/**
	 * 跳过片头动画(第一次打开时会触发)
	 */
	private static void skipTitle() {
		try {
			WebElement welFlash = Browser.findElement(By.name("welFlash"));
			WebElement span = welFlash.findElement(By.tagName("span"));
			Browser.click(span);
			
		} catch(Exception e) {
			log.error("跳过QQ空间片头动画失败", e);
		}
	}
	
	/**
	 * 跳过操作提示(第一次打开时会触发)
	 */
	private static void skipTips() {
		try {
			WebElement guide = Browser.findElement(By.className("fs-guide-overlay"));
			WebElement top = guide.findElement(By.className("top"));
			WebElement m = top.findElement(By.className("m"));
			WebElement a = m.findElement(By.tagName("a"));
			Browser.click(a);
			
		} catch(Exception e) {
			log.error("跳过QQ空间操作提示失败", e);
		}
	}
	
}
