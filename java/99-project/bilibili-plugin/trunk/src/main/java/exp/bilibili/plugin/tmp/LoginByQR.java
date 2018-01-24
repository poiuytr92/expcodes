package exp.bilibili.plugin.tmp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.Browser;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.core.front.AppUI;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.thread.LoopThread;

// 用于登录主号
public class LoginByQR extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(LoginByQR.class);
	
	/** B站登陆页面 */
	private final static String LOGIN_URL = Config.getInstn().LOGIN_URL();
	
	public final static String IMG_DIR = Config.getInstn().IMG_DIR();
	
	public final static String QRIMG_NAME = "qrcode";
	
	private final static String COOKIE_DIR = Config.getInstn().COOKIE_DIR();
	
	public final static String MINI_COOKIE_PATH = "./data/cookie-mini.dat";
	
	/** B站二维码有效时间是180s, 这里设置120s, 避免边界问题 */
	private final static long UPDATE_TIME = 120000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_LIMIT = (int) (UPDATE_TIME / LOOP_TIME);
	
	private int loopCnt;
	
	private boolean isLogined;
	
	private String loginUser;
	
	private static volatile LoginByQR instance;
	
	private LoginByQR() {
		super("登陆二维码刷新器");
		this.loopCnt = LOOP_LIMIT;
		this.isLogined = false;
		this.loginUser = "";
	}
	
	public static LoginByQR getInstn() {
		if(instance == null) {
			synchronized (LoginByQR.class) {
				if(instance == null) {
					instance = new LoginByQR();
				}
			}
		}
		return instance;
	}
	
	public String getLoginUser() {
		return loginUser;
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
		autoLogin();	// 尝试使用上一次登陆的cookies自动登陆
	}

	@Override
	protected void _loopRun() {
		if(isLogined == true) {
			_stop();	// 若登陆成功则退出轮询
			
		} else {
			
			// 在二维码失效前更新
			if(loopCnt >= LOOP_LIMIT) {
				if(downloadQrcode()) {
					loopCnt = 0;
					AppUI.getInstn().updateQrcode();
				}
			}
			
			// 若当前页面不再是登陆页（扫码成功会跳转到主页）, 说明登陆成功
			isLogined = isSwitch();
			if(isLogined == true) {
				skipUpdradeTips();	// 跳过B站的升级教程（该教程若不屏蔽会妨碍点击抽奖）
			}
		}
		
		AppUI.getInstn().updateQrcodeTime(LOOP_LIMIT - (loopCnt++));
		_sleep(LOOP_TIME);
	}

	@Override
	protected void _after() {
		saveLoginInfo();
		log.info("{} 已停止", getName());
	}
	
	/**
	 * 尝试使用cookies自动登陆
	 */
	public boolean autoLogin() {
		UIUtils.log("正在尝试使用cookies自动登陆...");
		Browser.init(true);				// 使用加载图片的浏览器（首次登陆需要扫描二维码图片/验证码图片）
		Browser.open(LOGIN_URL);		// 打开登陆页面
		isLogined = loginByCookies();	// 先尝试cookies登陆
		if(isLogined == false) {
			clearCookies();
		}
		return isLogined;
	}
	
	public boolean clearCookies() {
		boolean isOk = true;
		isOk &= FileUtils.delete(COOKIE_DIR);
		isOk &= (FileUtils.createDir(COOKIE_DIR) != null);
		return isOk;
	}
	
	/**
	 * 从外存读取上次登陆成功的cookies
	 * @return
	 */
	private boolean loginByCookies() {
		if(FileUtils.isEmpty(COOKIE_DIR)) {
			return false;
		}
		Browser.clearCookies();
		Browser.recoveryCookies();
		return checkIsLogin();
	}
	
	/**
	 * 下载登陆二维码
	 * @return
	 */
	private boolean downloadQrcode() {
		boolean isOk = false;
		UIUtils.log("正在下载登陆二维码, 请打开 [哔哩哔哩手机客户端] 扫码登陆...");
		log.info("正在更新登陆二维码...");
		Browser.open(LOGIN_URL);
		WebElement img = Browser.findElement(By.xpath("//div[@class='qrcode-img'][1]/img"));
		if(img != null) {
			String imgUrl = img.getAttribute("src");
			isOk = HttpUtils.convertBase64Img(imgUrl, IMG_DIR, QRIMG_NAME);
			log.info("更新登陆二维码{}", (isOk ? "成功, 请打开 [哔哩哔哩手机客户端] 扫码登陆..." : "失败"));
		}
		return isOk;
	}
	
	/**
	 * 通过再次打开登陆页面，根据是否会发生跳转判断是否登陆成功.
	 * 	若已登陆成功,会自动跳转到首页; 否则会停留在登陆页面
	 * @return true: 登陆成功; false:登陆失败
	 */
	private boolean checkIsLogin() {
		Browser.open(LOGIN_URL);
		ThreadUtils.tSleep(LOOP_TIME);	// 等待以确认是否会发生跳转
		return isSwitch();
	}
	
	/**
	 * 检查页面是否发生了跳转
	 * @return
	 */
	private boolean isSwitch() {
		String curURL = Browser.getCurURL();
		return (StrUtils.isNotEmpty(curURL) && !curURL.startsWith(LOGIN_URL));
	}
	
	/**
	 * 切到当前直播间, 把第一次打开直播室时的升级教程提示屏蔽掉
	 */
	private void skipUpdradeTips() {
		UIUtils.log("首次登陆成功, 正在屏蔽B站拦截脚本...");
		Browser.open(AppUI.getInstn().getLiveUrl());
		By upgrade = By.className("upgrade-intro-component");
		if(Browser.existElement(upgrade)) {
			WebElement upgrapTips = Browser.findElement(upgrade);
			WebElement skipBtn = upgrapTips.findElement(By.className("skip"));
			skipBtn.click();
		}
	}
	
	/**
	 * 保存登陆信息
	 */
	public void saveLoginInfo() {
		UIUtils.log("正在保存cookies(用于下次自动登陆)");
		Browser.backupCookies();	// 保存登录成功的cookies到外存, 以备下次使用
		Browser.quit();	// 退出浏览器(此浏览器是加载图片的, 不加载图片的浏览器后面再延迟启动)
		
		loginUser = MsgSender.queryUsername(Browser.COOKIES());	// 获取当前登陆的用户名
		AppUI.getInstn().markLogin(loginUser);	// 在界面标记已登陆
	}
	
}