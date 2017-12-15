package exp.bilibli.plugin.core.site;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.ObjUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * 
 * <PRE>
 * 首次登陆通过APP客户端扫码, 然后保存cookie信息.
 * 之后登陆通过cookie, 以回避校验码问题.
 * </PRE>
 */
public class LoginMgr {

	private final static Logger log = LoggerFactory.getLogger(LoginMgr.class);
	
	/** B站登陆页面 */
	private final static String LOGIN_URL = "https://passport.bilibili.com/login";
	
	/** B站主页 */
	private final static String HOME_URL = "https://www.bilibili.com/";
	
	private final static long WAIT_ELEMENT_TIME = 30;
	
	private final static String COOKIE_DIR = "./data/cookies";
	
	public final static String IMG_DIR = "./data/img";
	
	public final static String IMG_NAME = "qrcode";
	
	private BrowserDriver browser;
	
	private WebDriver driver;
	
	private static volatile LoginMgr instance;
	
	private LoginMgr() {
		this.browser = BrowserDriver.PHANTOMJS;
		this.driver = browser.getWebDriver(WAIT_ELEMENT_TIME);
	}
	
	public static LoginMgr getInstn() {
		if(instance == null) {
			synchronized (LoginMgr.class) {
				if(instance == null) {
					instance = new LoginMgr();
				}
			}
		}
		return instance;
	}
	
	public BrowserDriver getBrowser() {
		return browser;
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	public boolean login() {
		boolean isOk = false;
		
		// 首次登陆: 使用二维码扫码登陆
		if(FileUtils.isEmpty(COOKIE_DIR)) {
			isOk = loginByQrcode();
			
		// 二次登陆: 通过cookies登陆
		} else {
			isOk = loginByCookies();
			
			// 若cookies登陆失败(可能是已过期或被篡改过文件), 则使用二维码重新登陆
			if(isOk == false) {
				isOk = loginByQrcode();
			}
		}
		return isOk;
	}
	
	// FIXME: 需要通过界面交互
	// FIXME: 每分钟刷新一次图片
	// 打开登陆页面, 获取登陆二维码
	private boolean loginByQrcode() {
		FileUtils.delete(COOKIE_DIR);
		FileUtils.createDir(COOKIE_DIR);
		
		log.info("正在下载登陆二维码, 稍后请打开 [哔哩哔哩动画手机客户端APP] 扫码登陆...");
		
		
		driver.get(LOGIN_URL);
		WebElement img = driver.findElement(By.xpath("//div[@class='qrcode-img'][1]/img"));
		String imgUrl = img.getAttribute("src");
		boolean isOk = HttpUtils.convertBase64Img(imgUrl, IMG_DIR, IMG_NAME);
		if(isOk == true) {
			System.out.println("登陆二维码图片保存:" + isOk);
			
			System.out.println("等待登陆");
			ThreadUtils.tSleep(30000);
			System.out.println("登陆成功，正在切换页面");	// FIXME: 可改成让用户扫码后确认
		}
		
		// 扫码登陆成功后，切到主页，保存当前cookies到本地
		driver.navigate().to(HOME_URL);
		if(driver.manage().getCookies().size() > 0) {
			isOk = true;
			int idx = 0;
			for(Cookie cookie : driver.manage().getCookies()) {
				String sIDX = StrUtils.leftPad(String.valueOf(idx++), '0', 2);
				String savePath = StrUtils.concat(COOKIE_DIR, "/cookie-", sIDX, ".dat");
				isOk &= ObjUtils.toSerializable(cookie, savePath);
			}
		}
		
		if(isOk = true) {
			isOk = checkLogin();
		}
		return isOk;
	}
	
	private boolean loginByCookies() {
		boolean isOk = false;
		driver.manage().deleteAllCookies();
		
		File dir = new File(COOKIE_DIR);
		File[] files = dir.listFiles();
		for(File file : files) {
			try {
				Cookie cookie = (Cookie) ObjUtils.unSerializable(file.getPath());
				driver.manage().addCookie(cookie);
				
			} catch(Exception e) {
				log.error("加载cookie失败: {}", file.getPath(), e);
			}
		}
		
		if(driver.manage().getCookies().size() > 0) {
			isOk = checkLogin();
		}
		return isOk;
	}
	
	private boolean checkLogin() {
		// 通过检测页面是否存在登陆后的标识, 判断是否真正登陆成功
		return true;
	}
	
}
