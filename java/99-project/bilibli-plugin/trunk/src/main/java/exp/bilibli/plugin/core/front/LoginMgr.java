package exp.bilibli.plugin.core.front;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 登陆管理器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class LoginMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(LoginMgr.class);
	
	/** B站登陆页面 */
	private final static String LOGIN_URL = Config.getInstn().LOGIN_URL();
	
	private final static String COOKIE_DIR = Config.getInstn().COOKIE_DIR();
	
	protected final static String IMG_DIR = Config.getInstn().IMG_DIR();
	
	protected final static String IMG_NAME = "qrcode";
	
	/** B站二维码有效时间是180s, 这里设置120s, 避免边界问题 */
	private final static long UPDATE_TIME = 120000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_LIMIT = (int) (UPDATE_TIME / LOOP_TIME);
	
	private int loopCnt;
	
	private boolean isLogined;
	
	private static volatile LoginMgr instance;
	
	private LoginMgr() {
		super("登陆二维码刷新器");
		this.loopCnt = LOOP_LIMIT;
		this.isLogined = false;
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
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
		Browser.init(true);				// 使用加载图片的浏览器（首次登陆需要扫描二维码图片）
		Browser.open(LOGIN_URL);		// 打开登陆页面
		isLogined = loginByCookies();	// 轮询二维码前先尝试cookies登陆
		
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
				UIUtils.log("扫码成功, 正在屏蔽B站拦截脚本...");
				skipUpdradeTips();	// 跳过B站的升级教程（该教程若不屏蔽会妨碍点击抽奖）
				Browser.setCookiesTime(Config.getInstn().COOKIE_TIME()); // 修正cookies有效时间
			}
		}
		
		AppUI.getInstn().updateQrcodeTime(LOOP_LIMIT - (loopCnt++));
		_sleep(LOOP_TIME);
	}

	@Override
	protected void _after() {
		UIUtils.log("正在保存cookies(下次登陆可无需扫码)");
		Browser.backupCookies();	// 保存登录成功的cookies到外存, 以备下次使用
		Browser.quit();	// 退出浏览器(此浏览器是加载图片的, 不加载图片的浏览器后面再延迟启动)
		
		AppUI.getInstn().markLogin();	// 在界面标记已登陆
		log.info("{} 已停止", getName());
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
		log.info("正在更新登陆二维码...");
		Browser.open(LOGIN_URL);
		WebElement img = Browser.findElement(By.xpath("//div[@class='qrcode-img'][1]/img"));
		if(img != null) {
			String imgUrl = img.getAttribute("src");
			isOk = HttpUtils.convertBase64Img(imgUrl, IMG_DIR, IMG_NAME);
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
		return !Browser.getCurURL().startsWith(LOGIN_URL);
	}
	
	/**
	 * 切到当前直播间, 把第一次打开直播室时的升级教程提示屏蔽掉
	 */
	private void skipUpdradeTips() {
		Browser.open(AppUI.getInstn().getLiveUrl());
		By upgrade = By.className("upgrade-intro-component");
		if(Browser.existElement(upgrade)) {
			WebElement upgrapTips = Browser.findElement(upgrade);
			WebElement skipBtn = upgrapTips.findElement(By.className("skip"));
			skipBtn.click();
		}
	}
}
