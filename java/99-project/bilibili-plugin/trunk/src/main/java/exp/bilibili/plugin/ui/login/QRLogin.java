package exp.bilibili.plugin.ui.login;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.envm.CookieType;
import exp.bilibili.protocol.XHRSender;
import exp.libs.utils.img.QRCodeUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
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

	private final static String IMG_DIR = Config.getInstn().IMG_DIR();
	
	private final static String QRIMG_PATH = IMG_DIR.concat("/qrcode.png");
	
	private final static int WIDTH = 140;
	
	private final static int HEIGHT = 140;
	
	private final static String RGX_OAUTH = "oauthKey=([^&]+)";
	
	/** B站二维码有效时间�?180s, 这里设置120s, 避免边界问题 */
	private final static long UPDATE_TIME = 120000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_LIMIT = (int) (UPDATE_TIME / LOOP_TIME);
	
	private int loopCnt;
	
	private String oauthKey;
	
	private boolean isLogined;
	
	private CookieType type;
	
	private BiliCookie cookie;
	
	private QRLoginUI qrUI;
	
	protected QRLogin(QRLoginUI qrUI, CookieType type) {
		super("二维码登陆器");
		this.loopCnt = LOOP_LIMIT;
		this.oauthKey = "";
		this.isLogined = false;
		this.type = type;
		this.cookie = BiliCookie.NULL;
		this.qrUI = qrUI;
	}
	
	@Override
	protected void _before() {
		// Undo
	}
	
	@Override
	protected void _loopRun() {
		if(isLogined == true) {
			_stop();	// 若登陆成功则退出轮�?
			
		} else {
			
			// 在二维码失效前更新图�?
			if(loopCnt >= LOOP_LIMIT) {
				if(downloadQrcode(QRIMG_PATH)) {
					qrUI.updateQrcodeImg(QRIMG_PATH);
					loopCnt = 0;
				}
			}
			
			// 检测是否已扫码登陆成功
			cookie = XHRSender.toLogin(oauthKey);
			if(BiliCookie.NULL != cookie) {
				if(CookiesMgr.checkLogined(cookie)) {
					isLogined = true;
					
				} else {
					isLogined = false;
					loopCnt = LOOP_LIMIT;	// 登陆失败, 下一次轮询直接刷新二维码
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
			CookiesMgr.getInstn().add(cookie, type);
		}
		qrUI._hide();
	}
	
	/**
	 * 下载登陆二维�?
	 * @param imgPath 下载二维码路�?
	 * @return 
	 */
	private boolean downloadQrcode(String imgPath) {
		String url = XHRSender.getQrcodeInfo();
		oauthKey = RegexUtils.findFirst(url, RGX_OAUTH);
		
		boolean isOk = false;
		if(StrUtils.isNotEmpty(oauthKey)) {
			isOk = QRCodeUtils.toQRCode(url, WIDTH, HEIGHT, imgPath);
		}
		return isOk;
	}
	
	public BiliCookie getCookie() {
		return cookie;
	}
	
}