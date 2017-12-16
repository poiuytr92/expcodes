package exp.bilibli.plugin.core.ui;

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
import exp.libs.warp.thread.LoopThread;

/**
 * 
 * <PRE>
 * 首次登陆通过APP客户端扫码, 然后保存cookie信息.
 * 之后登陆通过cookie, 以回避校验码问题.
 * </PRE>
 */
public class LoginMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(LoginMgr.class);
	
	/** B站登陆页面 */
	private final static String LOGIN_URL = "https://passport.bilibili.com/login";
	
	/** B站主页 */
	private final static String HOME_URL = "https://www.bilibili.com/";
	
	private final static String COOKIE_DIR = "./data/cookies";
	
	protected final static String IMG_DIR = "./data/img";
	
	protected final static String IMG_NAME = "qrcode";
	
	/** B站二维码有效时间是180s, 这里设置120s, 避免边界问题 */
	private final static long UPDATE_TIME = 120000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_CNT = (int) (UPDATE_TIME / LOOP_TIME);
	
	private int loopCnt;
	
	private BrowserDriver browser;
	
	private WebDriver driver;
	
	private boolean isLogined;
	
	private static volatile LoginMgr instance;
	
	private LoginMgr() {
		super("页面登陆管理器");
		this.browser = BrowserDriver.PHANTOMJS;
		this.driver = browser.getWebDriver();
		this.loopCnt = LOOP_CNT;
		this.isLogined = false;
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
	
	@Override
	protected void _before() {
		driver.navigate().to(LOGIN_URL);	// 打开登陆页面
		isLogined = loginByCookies();		// 轮询二维码前先尝试cookies登陆
		
		if(isLogined == false) {
			FileUtils.delete(COOKIE_DIR);
			FileUtils.createDir(COOKIE_DIR);
		}
	}

	@Override
	protected void _loopRun() {
		if(isLogined == true) {
			_stop();	// 若登陆成功则退出轮询
			
		} else {
			
			// 每30秒更新一次登陆二维码
			if(loopCnt >= LOOP_CNT) {
				if(downloadQrcode()) {
					loopCnt = 0;
					AppUI.getInstn().updateQrcode();
				}
			}
			
			// 若当前页面不再是登陆页（扫码成功会跳转到主页）, 说明登陆成功
			isLogined = isSwitch();
		}
		log.info("curPage:{}", driver.getCurrentUrl());
		log.info("cookies:{}", driver.manage().getCookies().size());
		
		AppUI.getInstn().updateQrcodeTime(LOOP_CNT - (loopCnt++));
		_sleep(LOOP_TIME);
	}

	@Override
	protected void _after() {
		AppUI.getInstn().disableLogin();
		saveCookies();
	}
	
	private boolean loginByCookies() {
		boolean isOk = false;
		if(FileUtils.isEmpty(COOKIE_DIR)) {
			return isOk;
		}
		driver.manage().deleteAllCookies();
		
		isOk = true;
		File dir = new File(COOKIE_DIR);
		File[] files = dir.listFiles();
		for(File file : files) {
			try {
				Cookie cookie = (Cookie) ObjUtils.unSerializable(file.getPath());
				driver.manage().addCookie(cookie);
				
			} catch(Exception e) {
				log.error("加载cookie失败: {}", file.getPath(), e);
				isOk = false;
				break;
			}
		}
		
		if(isOk == true) {
			isOk = checkIsLogin();
		}
		return isOk;
	}
	
	private boolean downloadQrcode() {
		log.info("正在更新登陆二维码...");
		driver.navigate().to(LOGIN_URL);
		WebElement img = driver.findElement(By.xpath("//div[@class='qrcode-img'][1]/img"));
		String imgUrl = img.getAttribute("src");
		boolean isOk = HttpUtils.convertBase64Img(imgUrl, IMG_DIR, IMG_NAME);
		log.info("更新登陆二维码{}", (isOk ? "成功, 请打开 [哔哩哔哩手机客户端] 扫码登陆..." : "失败"));
		return isOk;
	}
	
	// 扫码登陆成功后，保存当前cookies到本地
	private void saveCookies() {
		if(isLogined == true) {
			int idx = 0;
			for(Cookie cookie : driver.manage().getCookies()) {
				String sIDX = StrUtils.leftPad(String.valueOf(idx++), '0', 2);
				String savePath = StrUtils.concat(COOKIE_DIR, "/cookie-", sIDX, ".dat");
				ObjUtils.toSerializable(cookie, savePath);
			}
		}
	}
	
	/**
	 * 通过再次打开登陆页面，根据是否会发生跳转判断是否登陆成功.
	 * 	若已登陆成功,会自动跳转到首页; 否则会停留在登陆页面
	 * @return true: 登陆成功; false:登陆失败
	 */
	private boolean checkIsLogin() {
		driver.navigate().to(LOGIN_URL);
		ThreadUtils.tSleep(LOOP_TIME);	// 等待以确认是否会发生跳转
		return isSwitch();
	}
	
	/**
	 * 检查页面是否发生了跳转
	 * @return
	 */
	private boolean isSwitch() {
		return !driver.getCurrentUrl().startsWith(LOGIN_URL);
	}
	
}
