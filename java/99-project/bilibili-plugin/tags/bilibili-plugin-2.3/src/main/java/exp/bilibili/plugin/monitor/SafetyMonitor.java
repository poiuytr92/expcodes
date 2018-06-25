package exp.bilibili.plugin.monitor;

import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.cache.LoginMgr;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.utils.SafetyUtils;
import exp.bilibili.plugin.utils.UIUtils;
import exp.certificate.bean.App;
import exp.certificate.core.Convertor;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.thread.LoopThread;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 软件授权监控线程
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SafetyMonitor extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(SafetyMonitor.class);
	
	/** 软件授权�?(Github) */
	private final static String GITHUB_URL = CryptoUtils.deDES(
			"610BEF99CF948F0DB1542314AC977291892B30802EC5BF3B2DCDD5538D66DDA67467CE4082C2D0BC56227128E753555C");
	
	/** 软件授权�?(Gitee) */
	private final static String GITEE_URL = CryptoUtils.deDES(
			"4C3B7319D21E23D468926AD72569DDF8408E193F3B526A6F5EE2A5699BCCA673DC22BC762A1F149B03E39422823B4BF0");
	
	/** 软件授权�?(Bilibili-备用) */
	private final static String BILIBILI_URL = CryptoUtils.deDES(
			"EECD1D519FEBFDE5EF68693278F5849E8068123647103E9D1644539B452D8DE870DD36BBCFE2C2A8E5A16D58A0CA752D3D715AF120F89F10990A854A386B95631E7C60D1CFD77605");
	
	/** 免检原因 */
	private final static String UNCHECK_CAUSE = "UNCHECK";
	
	/** 允许授权页连续无响应的上限次�? */
	private final static int NO_RESPONSE_LIMIT = 3;
	
	/** 校验授权间隔 */
	private final static long CHECK_TIME = 120000;
	
	/** 线程轮询间隔 */
	private final static long LOOP_TIME = 1000;
	
	/** 校验行为的累计周�?(达到周期则触发校�?) */
	private final static int LOOP_LIMIT = (int) (CHECK_TIME / LOOP_TIME);
	
	private int noResponseCnt;
	
	private int loopCnt;
	
	private String cause;
	
	private String appName;

	private String appVersion;
	
	private String certificateTime;
	
	private static volatile SafetyMonitor instance;
	
	private SafetyMonitor() {
		super("软件授权监控线程");
		
		this.noResponseCnt = 0;
		this.loopCnt = LOOP_LIMIT;
		this.cause = UNCHECK_CAUSE;
		this.appName = "";
		this.appVersion = "";
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
		String verInfo =  VersionMgr.exec("-p");
		appName = RegexUtils.findFirst(verInfo, "项目名称[ |]*([a-z|\\-]+)");
		appVersion = RegexUtils.findFirst(verInfo, "版本号[ |]*([\\d|\\.]+)");
		updateCertificateTime(SafetyUtils.fileToCertificate());
		
		log.info("{} 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		_sleep(LOOP_TIME);
		
		try {
			if(checkByGit() == false) {
				_stop();
			}
		} catch(Exception e) {
			log.error("{} 异常", getName(), e);
			
			if(++noResponseCnt >= NO_RESPONSE_LIMIT) {
				if(checkByBilibili() == true) {
					noResponseCnt = 0;
					
				} else {
					cause = "监控异常, 无法确认授权信息";
					_stop();
				}
			}
		}
	}
	
	@Override
	protected void _after() {
		log.info("{} 已停�?, CAUSE: {}", getName(), cause);
		
		// 若非免检原因导致的终�?, 则需要弹出提示面�?
		if(!UNCHECK_CAUSE.equals(cause)) {
			
			// 使用渐隐自动关闭的提示窗�?, 可避免用户卡着提示窗口导致程序不退出的问题
			_ExitNoticeUI exit = new _ExitNoticeUI(cause);
			exit._view();
			exit._join();
			
			System.exit(0);
		}
	}
	
	/**
	 * 软件授权校验（通过GitHub/Gitee授权页）
	 * @return 是否继续校验
	 */
	private boolean checkByGit() {
		boolean isContinue = true;
		if(++loopCnt >= LOOP_LIMIT) {
			loopCnt = 0;
			
			// 先尝试用Gitee(国内)获取授权�?, 若失败则从GitHub(国际)获取授权�?
			String pageSource = HttpUtils.getPageSource(GITEE_URL);
			if(StrUtils.isEmpty(pageSource)) {
				pageSource = HttpUtils.getPageSource(GITHUB_URL);
			}
			
			App app = Convertor.toApp(pageSource, appName);	// 提取软件授权信息
			if(app == null) {
				if(++noResponseCnt >= NO_RESPONSE_LIMIT) {
					if(checkByBilibili() == true) {	// Github或Gitee网络不通时, 转B站校�?
						noResponseCnt = 0;
						
					} else {
						cause = "网络异常, 无法确认授权信息";
						isContinue = false;
					}
				}
			} else {
				noResponseCnt = 0;
				isContinue = check(app);
			}
			
			updateCertificateTime(app);	// 更新授权时间
			UIUtils.updateAppTitle(certificateTime); // 把授权时间更新到标题
		}
		return isContinue;
	}
	
	/**
	 * 软件授权校验（通过Bilibili授权信息-作为备用校验�?
	 * @return 是否继续校验
	 */
	private boolean checkByBilibili() {
		boolean isOk = false;
		String response = MsgSender.queryCertTags(BILIBILI_URL);
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			
			if(code == 0) {
				JSONArray data = JsonUtils.getArray(json, BiliCmdAtrbt.data);
				App app = _toApp(data);	// 生成软件授权信息
				if(app != null) {
					isOk = check(app);
				}
			}
		} catch(Exception e) {
			log.error("从B站提取应�? [{}] 信息失败", appName, e);
		}
		return isOk;
	}
	
	/**
	 * 生成软件授权信息
	 * @param data
	 * @return
	 */
	private App _toApp(JSONArray data) {
		App app = null;
		if(data == null || data.size() <= 0) {
			return app;
		}
		
		String versions = "";
		String time = "";
		StringBuilder blacklist = new StringBuilder();
		StringBuilder whitelist = new StringBuilder();
		
		for(int i = 0; i < data.size(); i++) {
			String tag = data.getString(i).trim();
			if(StrUtils.isEmpty(tag)) {
				continue;
				
			} else if(tag.startsWith("V:")) {
				versions = tag.replace("V:", "");
				
			} else if(tag.startsWith("T:")) {
				time = tag.replace("T:", "");
				Date date = TimeUtils.toDate(time, "yyyyMMdd");
				time = TimeUtils.toStr(date);
				
			} else if(tag.startsWith("B:")) {
				blacklist.append(tag.replace("B:", "")).append(",");
				
			} else if(tag.startsWith("W:")) {
				whitelist.append(tag.replace("W:", "")).append(",");
			}
		}
		
		if(blacklist.length() > 0) { blacklist.setLength(blacklist.length() - 1); }
		if(whitelist.length() > 0) { whitelist.setLength(whitelist.length() - 1); }
		app = new App(appName, versions, time, blacklist.toString(), whitelist.toString());
		return app;
	}
	
	/**
	 * 校验当前软件是否匹配授权信息
	 * @param app 软件授权信息
	 * @return true:匹配; false:不匹�?
	 */
	private boolean check(App app) {
		boolean isOk = true;
		if(checkInWhitelist(app.getWhitelist())) {
			cause = UNCHECK_CAUSE;	// 白名单用�?, 启动后则免检
			isOk = false;
			
		} else if(!checkVersions(app.getVersions())) {
			cause = "版本已失�?";
			isOk = false;
			
		} else if(!checkNotInBlacklist(app.getBlacklist())) {
			cause = "孩子, 你被管理员关小黑屋了";
			isOk = false;
			
		} else if(!checkInTime(app.getTime())) {
			cause = "授权已过�?";
			isOk = false;
		}
		return isOk;
	}
	
	/**
	 * 检查使用软件的用户是否在白名单内（白名单内用户可无视所有校验）
	 * @param whitelist 白名单列表（格式: aUser,bUser,cUser,......�?
	 * @return true:在白名单�?; false:不在白名�?
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
	 * @return true:当前版本在授权范围内; false:当前版本已失�?
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
	 * @param blacklist 黑名单列表（格式: aUser,bUser,cUser,......�?
	 * @return true:不在黑名�?; false:在黑名单�?
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
	 * 检查对公时间是否过�?.
	 * 	若对公时间已过期, 则检查对私时间是否过�?.
	 * @param time 对公授权时间(格式�? yyyy-MM-dd HH:mm:ss)
	 * @return true:对公或对私时间未过期; false:对公与对私时间均过期
	 */
	private boolean checkInTime(String time) {
		long now = System.currentTimeMillis();
		long publicTime = TimeUtils.toMillis(time);
		long privateTime = SafetyUtils.fileToCertificate();
		
		// 更新授权时间
		long maxTime = (publicTime > privateTime ? publicTime : privateTime);
		updateCertificateTime(maxTime);
		
		return !(now > publicTime && now > privateTime);
	}
	
	/**
	 * 更新授权时间
	 */
	private void updateCertificateTime(long millis) {
		this.certificateTime = TimeUtils.toStr(millis, "yyyy-MM-dd");
	}
	
	/**
	 * 更新授权时间
	 */
	private void updateCertificateTime(App app) {
		if(app != null) {
			checkInTime(app.getTime());
		}
	}
	
}
