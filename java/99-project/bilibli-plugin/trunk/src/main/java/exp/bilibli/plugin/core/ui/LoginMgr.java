package exp.bilibli.plugin.core.ui;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.utils.UIUtils;
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
class LoginMgr {

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
	
	private boolean isLoading;
	
	private static volatile LoginMgr instance;
	
	private LoginMgr() {
		this.browser = BrowserDriver.PHANTOMJS;
		this.driver = browser.getWebDriver(WAIT_ELEMENT_TIME);
		this.isLoading = false;
	}
	
	protected static LoginMgr getInstn() {
		if(instance == null) {
			synchronized (LoginMgr.class) {
				if(instance == null) {
					instance = new LoginMgr();
				}
			}
		}
		return instance;
	}
	
	protected BrowserDriver getBrowser() {
		return browser;
	}

	protected WebDriver getDriver() {
		return driver;
	}
	
	protected boolean loginByQrcode() {
		if(isLoading == true) {
			return false;
		}
		
		log.info("正在下载登陆二维码...");
		UIUtils.log("正在下载登陆二维码...");
		isLoading = true;
		
		FileUtils.delete(COOKIE_DIR);
		FileUtils.createDir(COOKIE_DIR);
		
		driver.navigate().to(LOGIN_URL);
		WebElement img = driver.findElement(By.xpath("//div[@class='qrcode-img'][1]/img"));
		String imgUrl = img.getAttribute("src");
		boolean isOk = HttpUtils.convertBase64Img(imgUrl, IMG_DIR, IMG_NAME);
		
		if(isOk == true) {
			log.info("登陆二维码下载成功, 请打开 [哔哩哔哩动画手机客户端] 扫码登陆...");
			AppUI.getInstn().updateQrcode();
			
		} else {
			log.info("登陆二维码下载失败.");
		}
		UIUtils.log("下载登陆二维码", (isOk ? "成功" : "失败"));
		isLoading = false;
		return isOk;
	}
	
	// 扫码登陆成功后，切到主页，保存当前cookies到本地
	protected boolean saveCookies() {
		boolean isOk = false;
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
	
	protected boolean loginByCookies() {
		boolean isOk = false;
		if(FileUtils.isEmpty(COOKIE_DIR)) {
			return isOk;
		}
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
	
	/**
	 * 通过再次打开登陆页面，根据是否会发生跳转判断是否登陆成功.
	 * 	若已登陆成功,会自动跳转到首页; 否则会停留在登陆页面
	 * @return true: 登陆成功; false:登陆失败
	 */
	private boolean checkLogin() {
		driver.navigate().to(LOGIN_URL);
		ThreadUtils.tSleep(2000);	// 等待2秒以确认是否会发生跳转
		return !(driver.getCurrentUrl().startsWith(LOGIN_URL));
	}
	
	protected boolean isLoading() {
		return isLoading;
	}
	
}
