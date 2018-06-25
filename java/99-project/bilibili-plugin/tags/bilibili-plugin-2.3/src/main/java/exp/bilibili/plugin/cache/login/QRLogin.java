package exp.bilibili.plugin.cache.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.HttpCookies;
import exp.bilibili.plugin.cache.Browser;
import exp.bilibili.plugin.envm.LoginType;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 二维码登陆.
 *  可用于登陆主号、小号、马甲号
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class QRLogin extends LoopThread {

	private final static String LOGIN_URL = Config.getInstn().LOGIN_URL();
	
	private final static String IMG_DIR = Config.getInstn().IMG_DIR();
	
	private final static String QRIMG_NAME = "qrcode";
	
	/** B站二维码有效时间�?180s, 这里设置120s, 避免边界问题 */
	private final static long UPDATE_TIME = 120000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_LIMIT = (int) (UPDATE_TIME / LOOP_TIME);
	
	private int loopCnt;
	
	private boolean isLogined;
	
	private HttpCookies mainCookies;
	
	private QRLoginUI qrUI;
	
	protected QRLogin(QRLoginUI qrUI) {
		super("二维码登陆器");
		this.loopCnt = LOOP_LIMIT;
		this.isLogined = false;
		
		this.mainCookies = HttpCookies.NULL;
		this.qrUI = qrUI;
	}
	
	@Override
	protected void _before() {
		log.info("正在尝试使用Cookies自动登陆...");
		
		isLogined = autoLogin();	// 尝试使用cookies自动登陆
		if(isLogined == false) {
			Browser.init(true);		// 使用加载图片的浏览器（首次登陆需要扫描二维码图片/验证码图片）
		}
	}
	
	@Override
	protected void _loopRun() {
		if(isLogined == true) {
			_stop();	// 若登陆成功则退出轮�?
			
		} else {
			
			// 在二维码失效前更新图�?
			if(loopCnt >= LOOP_LIMIT) {
				if(downloadQrcode(IMG_DIR, QRIMG_NAME)) {
					qrUI.updateQrcodeImg(IMG_DIR, QRIMG_NAME);
					loopCnt = 0;
				}
			}
			
			// 若当前页面不再是登陆页（扫码成功会跳转到主页�?, 说明登陆成功
			if(isSwitch() == true) {
				mainCookies = new HttpCookies(Browser.getCookies());
				if(LoginMgr.checkLogined(mainCookies)) {
					isLogined = true;
					
				} else {
					isLogined = false;
					loopCnt = LOOP_LIMIT;	// 登陆失败, 下一个轮询直接刷新二维码
				}
			}
		}
		
		// 更新二维码有效期
		qrUI.updateQrcodeTips(LOOP_LIMIT - (loopCnt++));
		_sleep(LOOP_TIME);
	}

	@Override
	protected void _after() {
		if(isLogined == true) {
			LoginMgr.INSTN().add(mainCookies, LoginType.MAIN);
		}
		Browser.quit();
		qrUI._hide();
		
		log.info("登陆{}: {}", (isLogined ? "成功" : "失败"), 
				(isLogined ? mainCookies.getNickName() : "Unknow"));
	}
	
	/**
	 * 尝试使用cookies自动登陆
	 * @return
	 */
	private boolean autoLogin() {
		boolean isOk = LoginMgr.INSTN().load(LoginType.MAIN);
		if(isOk == true) {
			mainCookies = LoginMgr.INSTN().getMainCookies();
			isOk = LoginMgr.checkLogined(mainCookies);
		}
		return isOk;
	}
	
	/**
	 * 下载登陆二维�?
	 * @param imgDir 下载二维码目�?
	 * @param qrImgName 二维码文件名称（不含后缀�?
	 * @return
	 */
	private boolean downloadQrcode(String imgDir, String qrImgName) {
		boolean isOk = false;
		log.info("正在更新登陆二维�?...");
		
		Browser.open(LOGIN_URL);
		WebElement img = Browser.findElement(By.xpath("//div[@class='qrcode-img'][1]/img"));
		if(img != null) {
			String imgUrl = img.getAttribute("src");
			isOk = HttpUtils.convertBase64Img(imgUrl, imgDir, qrImgName);
			
			log.info("更新登陆二维码{}", (isOk ? "成功, 请打开 [哔哩哔哩手机客户端] 扫码登陆..." : "失败"));
		}
		return isOk;
	}
	
	/**
	 * 检查页面是否发生了跳转, 以判定是否登陆成�?
	 * @return
	 */
	private boolean isSwitch() {
		String curURL = Browser.getCurURL();
		return (StrUtils.isNotEmpty(curURL) && !curURL.startsWith(LOGIN_URL));
	}
	
}