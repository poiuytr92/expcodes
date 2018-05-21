package exp.crawler.qq.monitor;

import exp.certificate.bean.AppInfo;
import exp.certificate.core.Convertor;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 软件授权监控
 * </PRE>
 * <B>PROJECT：</B> qzone-crawler
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-29
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SafetyMonitor {

	/** 软件授权页(Github) TLS协议版本问题，需JDK1.8编译的程序才能访问此网址 */
	private final static String GITHUB_URL = CryptoUtils.deDES(
			"610BEF99CF948F0DB1542314AC977291892B30802EC5BF3B2DCDD5538D66DDA67467CE4082C2D0BC56227128E753555C");
	
	/** 软件授权页(Gitee) */
	private final static String GITEE_URL = CryptoUtils.deDES(
			"4C3B7319D21E23D468926AD72569DDF8408E193F3B526A6F5EE2A5699BCCA673DC22BC762A1F149B03E39422823B4BF0");
	
	/** 软件名称 */
	private String appName;

	/** 单例 */
	private static volatile SafetyMonitor instance;
	
	/**
	 * 构造函数
	 */
	private SafetyMonitor() {
		this.appName = RegexUtils.findFirst(VersionMgr.getAppName(), "([a-zA-Z\\-]+)");
	}
	
	/**
	 * 获取单例
	 * @return
	 */
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
	
	/**
	 * 检查使用软件的QQ是否在白名单内
	 * @param QQ 使用软件的QQ
	 * @return true:在白名单内; false:不在白名单内
	 */
	public boolean isInWhitelist(String QQ) {
		AppInfo appInfo = getAppInfo();	// 提取软件授权信息
		return (appInfo != null && appInfo.getWhitelist().contains(QQ));
	}
	
	/**
	 * 检查被爬取数据的QQ是否在黑名单内
	 * @param QQ 被爬取数据的QQ
	 * @return true:在黑名单内; false:不在黑名单内
	 */
	public boolean isInBlacklist(String QQ) {
		AppInfo appInfo = getAppInfo();	// 提取软件授权信息
		return (appInfo != null && appInfo.getBlacklist().contains(QQ));
	}
	
	/**
	 * 提取软件授权信息
	 * @return
	 */
	private AppInfo getAppInfo() {
		
		// 先尝试用Gitee(国内)获取授权页, 若失败则从GitHub(国际)获取授权页
		String pageSource = HttpURLUtils.doGet(GITEE_URL);
		if(StrUtils.isEmpty(pageSource)) {
			pageSource = HttpURLUtils.doGet(GITHUB_URL);
		}
		
		AppInfo appInfo = Convertor.toAppInfo(pageSource, appName);
		return appInfo;
	}
	
}
