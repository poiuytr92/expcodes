package exp.crawler.qq.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.utils.UIUtils;

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
			Browser.reset(false);
			Browser.clearCookies();
			
			isOk = _toLogin(username, password, QZONE_URL);
			UIUtils.log("登陆QQ [", username, "] ", (isOk ? "成功" : "失败: 账号或密码错误"));
			
		} catch(Exception e) {
			UIUtils.log("仿真浏览器异常: 若一直失败请重启软件");
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
		UIUtils.log("正在打开QQ登陆页面: ", URL.QZONE_LOGIN);
		Browser.open(URL.QZONE_LOGIN);
		switchLoginMode();	// 切换为帐密登陆模式
		ThreadUtils.tSleep(SLEEP_TIME);
		
		fill("u", username);
		fill("p", password);
		ThreadUtils.tSleep(SLEEP_TIME);
		
		UIUtils.log("正在登陆QQ [", username, "] ...");
		boolean isOk = clickLoginBtn();
		if(isOk == true) {
			Browser.open(QZONE_URL);
			skipTitle();
			skipTips();
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
		Browser.switchToFrame(By.id("login_frame"));
		
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
		Browser.fill(input, value);
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
			log.warn("跳过QQ空间片头动画失败");
		}
	}
	
	/**
	 * 跳过操作提示(第一次打开时可能会触发)
	 */
	private static void skipTips() {
		try {
			WebElement guide = Browser.findElement(By.className("fs-guide-overlay"));
			WebElement top = guide.findElement(By.className("top"));
			WebElement m = top.findElement(By.className("m"));
			WebElement a = m.findElement(By.tagName("a"));
			Browser.click(a);
			
		} catch(Exception e) {
			log.warn("跳过QQ空间操作提示失败");
		}
	}
	
}
