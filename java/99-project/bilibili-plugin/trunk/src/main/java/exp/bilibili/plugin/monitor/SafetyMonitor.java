package exp.bilibili.plugin.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.cache.LoginMgr;
import exp.bilibili.plugin.utils.SafetyUtils;
import exp.certificate.bean.App;
import exp.certificate.core.Convertor;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.thread.LoopThread;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 软件授权监控线程
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SafetyMonitor extends LoopThread {

	public static void main(String[] args) {
		BeautyEyeUtils.init();
		SafetyMonitor.getInstn()._start();
	}
	
	private final static Logger log = LoggerFactory.getLogger(SafetyMonitor.class);
	
	/** 软件授权页 */
	private final static String URL = CryptoUtils.deDES(
			"610BEF99CF948F0DB1542314AC977291892B30802EC5BF3B2DCDD5538D66DDA67467CE4082C2D0BC56227128E753555C");
	
	/** 免检原因 */
	private final static String UNCHECK_CAUSE = "UNCHECK";
	
	/** 允许授权页连续无响应的上限次数 */
	private final static int NO_RESPONSE_LIMIT = 3;
	
	/** 校验授权间隔 */
	private final static long CHECK_TIME = 5000;
	
	/** 线程轮询间隔 */
	private final static long LOOP_TIME = 1000;
	
	/** 校验行为的累计周期(达到周期则触发校验) */
	private final static int LOOP_LIMIT = (int) (CHECK_TIME / LOOP_TIME);
	
	private int noResponseCnt;
	
	private int loopCnt;
	
	private String cause;
	
	private String appName;

	private String appVersion;
	
	private static volatile SafetyMonitor instance;
	
	private SafetyMonitor() {
		super("软件授权监控线程");
		
		this.noResponseCnt = 0;
		this.loopCnt = LOOP_LIMIT;
		this.cause = UNCHECK_CAUSE;
		
		String verInfo =  VersionMgr.exec("-p");
		this.appName = RegexUtils.findFirst(verInfo, "项目名称[ |]*([a-z|\\-]+)");
		this.appVersion = RegexUtils.findFirst(verInfo, "版本号[ |]*([\\d|\\.]+)");
	}
	
	public static SafetyMonitor getInstn() {
		if(instance == null) {
			synchronized (SafetyMonitor.class) {
				if(instance == null) {
					instance = new SafetyMonitor();
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		_sleep(LOOP_TIME);
		
		try {
			if(check() == false) {
				_stop();
			}
		} catch(Exception e) {
			log.error("{} 异常", getName(), e);
			
			if(++noResponseCnt >= NO_RESPONSE_LIMIT) {
				cause = "监控异常, 无法确认授权信息";
				_stop();
			}
		}
	}
	
	@Override
	protected void _after() {
		log.info("{} 已停止, CAUSE: {}", getName(), cause);
		
		// 若非免检原因导致的终止, 则需要弹出提示面板
		if(!UNCHECK_CAUSE.equals(cause)) {
			
			// 使用渐隐自动关闭的提示窗口, 可避免用户卡着提示窗口导致程序不退出的问题
			_ExitNoticeUI exit = new _ExitNoticeUI(cause);
			exit._show();
			exit._join();
			
			System.exit(0);
		}
	}
	
	/**
	 * 软件授权校验
	 * @return 是否继续校验
	 */
	private boolean check() {
		boolean isContinue = true;
		if(++loopCnt >= LOOP_LIMIT) {
			loopCnt = 0;
			
			String pageSource = HttpUtils.getPageSource(URL);
			App app = Convertor.toApp(pageSource, appName);
			if(app == null) {
				if(++noResponseCnt >= NO_RESPONSE_LIMIT) {
					cause = "网络异常, 无法确认授权信息";
					isContinue = false;
				}
				
			} else {
				noResponseCnt = 0;
				
				if(checkInWhitelist(app.getWhitelist())) {
					cause = UNCHECK_CAUSE;	// 白名单用户, 启动后则免检
					isContinue = false;
					
				} else if(!checkVersions(app.getVersions())) {
					cause = "版本已失效";
					isContinue = false;
					
				} else if(!checkNotInBlacklist(app.getBlacklist())) {
					cause = "孩子, 你被管理员关小黑屋了";
					isContinue = false;
					
				} else if(!checkInTime(app.getTime())) {
					cause = "授权已过期";
					isContinue = false;
				}
			}
		}
		return isContinue;
	}
	
	/**
	 * 检查使用软件的用户是否在白名单内（白名单内用户可无视所有校验）
	 * @param whitelist 白名单列表（格式: aUser,bUser,cUser,......）
	 * @return true:在白名单内; false:不在白名单
	 */
	private boolean checkInWhitelist(String whitelist) {
		boolean isIn = false;
		String loginUser = LoginMgr.getInstn().getLoginUser();
		if(StrUtils.isNotEmpty(whitelist, loginUser)) {
			isIn = whitelist.contains(loginUser);
		}
		return isIn;
	}
	
	/**
	 * 检查软件的当前版本是否大于等于授权版本
	 * @param versions 授权版本(格式: major.minor ，如: 1.9)
	 * @return true:当前版本在授权范围内; false:当前版本已失效
	 */
	private boolean checkVersions(String versions) {
		String[] appVers = appVersion.split("\\.");
		String[] cerVers = versions.split("\\.");
		
		boolean isOk = false;
		int appMajor = NumUtils.toInt(appVers[0], -1);
		int cerMajor = NumUtils.toInt(cerVers[0], 0);
		if(appMajor > cerMajor) {
			isOk = true;
			
		} else if(appMajor == cerMajor) {
			int appMinor = NumUtils.toInt(appVers[1], -1);
			int cerMinor = NumUtils.toInt(cerVers[1], 0);
			isOk = (appMinor >= cerMinor);
		}
		return isOk;
	}
	
	/**
	 * 检查使用软件的用户是否不在黑名单内
	 * @param blacklist 黑名单列表（格式: aUser,bUser,cUser,......）
	 * @return true:不在黑名单; false:在黑名单内
	 */
	private boolean checkNotInBlacklist(String blacklist) {
		boolean isNotIn = true;
		String loginUser = LoginMgr.getInstn().getLoginUser();
		if(StrUtils.isNotEmpty(blacklist, loginUser)) {
			isNotIn = !blacklist.contains(loginUser);
		}
		return isNotIn;
	}
	
	/**
	 * 检查对公时间是否过期.
	 * 	若对公时间已过期, 则检查对私时间是否过期.
	 * @param time 对公授权时间(格式： yyyy-MM-dd HH:mm:ss)
	 * @return true:对公或对私时间未过期; false:对公与对私时间均过期
	 */
	private boolean checkInTime(String time) {
		long now = System.currentTimeMillis();
		long publicTime = TimeUtils.toMillis(time);
		long privateTime = SafetyUtils.fileToCertificate();
		return !(now > publicTime && now > privateTime);
	}

}
