package exp.crawler.qq.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.crawler.qq.Config;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <PRE>
 * QQ空间登陆器
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
	
	/** 私有化构造函数 */
	protected Landers() {}
	
	/**
	 * 登陆QQ空间
	 * @param username 
	 * @param password
	 * @param targetQQ 目标QQ
	 * @return
	 */
	public static boolean toLogin(String username, String password) {
		boolean isOk = false;
		try {
			isOk = _toLogin(username, password);
			UIUtils.log("登陆QQ [", username, "] ", (isOk ? "成功" : "失败: 账号或密码错误"));
			
		} catch(Exception e) {
			UIUtils.log("仿真浏览器异常: 若一直失败请重启软件");
			log.error("登陆QQ [{}] 异常", username, e);
		}
		return isOk;
	}
	
	/**
	 * 登陆QQ空间
	 * @param username QQ登陆账号
	 * @param password QQ登陆密码
	 * @return 是否登陆成功
	 */
	private static boolean _toLogin(String username, String password) {
		UIUtils.log("正在打开QQ登陆页面: ", URL.QZONE_LOGIN_URL);
		Browser.open(URL.QZONE_LOGIN_URL);
		boolean isOk = switchLoginMode();	// 切换为帐密登陆模式
		if(isOk == true) {
			fill("u", username);	// 填写账号
			fill("p", password);	// 填写密码
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			
			UIUtils.log("正在登陆QQ [", username, "] ...");
			isOk = clickLoginBtn(username);	// 点击登陆并检测是否登成功
		}
		return isOk;
	}
	
	/**
	 * 切换登陆方式为[帐密登陆]
	 * return 是否切换成功
	 */
	private static boolean switchLoginMode() {
		
		// QQ空间的【登陆操作界面】是通过【iframe】嵌套在【登陆页面】中的子页面
		Browser.switchToFrame(By.id("login_frame"));
		ThreadUtils.tSleep(Config.SLEEP_TIME);
		
		// 切换帐密登陆方式为 [帐密登陆]
		boolean isOk = true;
		for(int retry = 1; retry <= 3; retry++) {
			try {
				WebElement switchBtn = Browser.findElement(By.id("switcher_plogin"));
				switchBtn.click();
				break;
				
			} catch(Exception e) {
				isOk = false;	// 有时操作过快可能会报元素不存在异常
				ThreadUtils.tSleep(Config.SLEEP_TIME);
			}
		}
		return isOk;
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
	 * 点击登陆按钮并进入QQ空间
	 * @param QQ
	 * @return
	 */
	private static boolean clickLoginBtn(String QQ) {
		final String UNLOGIN_URL = Browser.getCurURL();	// 登录前URL
		
		// 点击登陆按钮
		WebElement loginBtn = Browser.findElement(By.id("login_button"));
		loginBtn.click();
		
		// 轮询是否登陆成功（发生页面切换）
		boolean isOk = true;
		long bgnTime = System.currentTimeMillis();
		while(UNLOGIN_URL.equals(Browser.getCurURL())) {
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			
			if(System.currentTimeMillis() - bgnTime >= Config.TIMEOUT) {
				isOk = false;	// 超时未切换页面则认为登陆失败
				break;
			}
		}
		
		// 备份cookies（含gtk） 与 qzoneToken
		if(isOk == true) {
			Browser.open(URL.QZONE_HOMR_URL(QQ));
			String qzoneToken = getQzoneToken(Browser.getPageSource());
			Browser.setQzoneToken(qzoneToken);
			Browser.backupCookies();
			
			isOk = StrUtils.isNotEmpty(Browser.GTK(), Browser.QZONE_TOKEN());
			Browser.quit();
		}
		return isOk;
	}
	
	/**
	 * 从QQ空间首页首页源码中提取 qzonetoken.
	 * 	类似于gtk, qzonetoken 在每次登陆时自动生成一个固定值, 但是生成算法相对复杂（需要jother解码）, 
	 *  因此此处取巧, 直接在页面源码中提取明文
	 * @param qzoneHomePageSource QQ空间首页源码
	 * @return qzonetoken
	 */
	private static String getQzoneToken(String qzoneHomePageSource) {
		final String RGX_QZONE_TOKEN = "window\\.g_qzonetoken[^\"]+\"([^\"]+)\"";
		return RegexUtils.findFirst(qzoneHomePageSource, RGX_QZONE_TOKEN);
	}
	
}
