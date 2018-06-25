package exp.libs.warp.ver;

import exp.libs.envm.Delimiter;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.ui.BeautyEyeUtils;

/**
 * <PRE>
 * 程序版本管理.
 * 直接在程序版本类的main方法调用即可.
 * 
 * 使用示例:
 * 	public class Version {
 * 		public static void main(String[] args) {
 * 			VersionMgr.exec(args);
 * 		}
 * 	}
 * 
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-22
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final public class VersionMgr {

	private final static String REGEX = "\\s*\\|([^\\|]*)";
	
	private String curVerInfo;
	
	private String appName;
	
	private String appDesc;
	
	private String version;
	
	private String releaseTime;
	
	private String author;
	
	/** 单例 */
	private static volatile VersionMgr instance;
	
	/**
	 * 构造函�?
	 */
	private VersionMgr() {
		BeautyEyeUtils.init();
		
		this.curVerInfo = "";
		this.appName = "";
		this.appDesc = "";
		this.version = "";
		this.releaseTime = "";
		this.author = "";
	}
	
	/**
	 * 获取单例
	 * @return
	 */
	private static VersionMgr getInstn() {
		if(instance == null) {
			synchronized (VersionMgr.class) {
				if(instance == null) {
					instance = new VersionMgr();
				}
			}
		}
		return instance;
	}
	
	/**
	 * @param args 入口参数（win下默认为-m, linux下强制为-p�?
	 * 		[-p] 打印最后的版本信息（DOS界面�?
	 * 		[-m] 版本管理（UI界面�?
	 * @return 当前版本信息
	 */
	public static String exec(String... args) {
		return getInstn()._exec(args);
	}
	
	/**
	 * @param args main函数入参: 
	 * 		[-p] 打印最后的版本信息（DOS界面�?
	 * 		[-m] 版本管理（UI界面�?
	 * @return 当前版本信息
	 */
	private String _exec(String... args) {
		boolean manage = true;
		if(args != null && args.length >= 1) {
			manage = "-m".equals(args[0]);
			if(!OSUtils.isWin()) {
				manage = false;
			}
		}
		return (manage ? manage() : print());
	}
	
	/**
	 * 管理版本信息
	 * @return 最新版本信�?
	 */
	protected String manage() {
		_VerMgrUI.getInstn()._view();
		
		String curVerInfo = _VerMgrUI.getInstn().getCurVerInfo();
		System.out.println(curVerInfo);
		return curVerInfo;
	}
	
	/**
	 * 打印最新版本信�?
	 * @return 最新版本信�?
	 */
	protected String print() {
		String curVerInfo = getVersionInfo(true, false);
		if(StrUtils.isNotEmpty(curVerInfo)) {
			System.out.println(curVerInfo);
			
		} else {
			System.err.println("获取当前版本信息失败");
		}
		return curVerInfo;
	}
	
	/**
	 * 获取版本信息
	 * @param onlyCurVersion 仅当前版�?(即最新版�?)
	 * @param detaiHistoty 是否打印历史版本升级内容详单 (仅onlyCurVersion=false时有�?)
	 * @return 版本信息
	 */
	public static String getVersionInfo(boolean onlyCurVersion, boolean detaiHistoty) {
		return getInstn()._getVersionInfo(onlyCurVersion, detaiHistoty);
	}
	
	/**
	 * 获取版本信息
	 * @param onlyCurVersion 仅当前版�?(即最新版�?)
	 * @param detaiHistoty 是否打印历史版本升级内容详单 (仅onlyCurVersion=false时有�?)
	 * @return 版本信息
	 */
	private String _getVersionInfo(boolean onlyCurVersion, boolean detaiHistoty) {
		if(StrUtils.isNotEmpty(curVerInfo)) {
			return curVerInfo;
		}
		
		StringBuilder verInfo = new StringBuilder();
		if(_VerDBMgr.getInstn().initVerDB()) {
			verInfo.append(_VerDBMgr.getInstn().getCurVerInfo());
			
			if(onlyCurVersion == false) {
				verInfo.append(Delimiter.CRLF).append(Delimiter.CRLF);
				verInfo.append(_VerDBMgr.getInstn().toHisVerInfos(detaiHistoty));
			}
		}
		curVerInfo = verInfo.toString();
		return curVerInfo;
	}
	
	private String getValue(final String TAG) {
		String verInfo = _getVersionInfo(true, false);
		return RegexUtils.findFirst(verInfo, TAG.concat(REGEX)).trim();
	}
	
	public static String getAppName() {
		return getInstn()._getAppName();
	}
	
	private String _getAppName() {
		if(StrUtils.isEmpty(appName)) {
			appName = getValue(_VerDBMgr.APP_NAME);
		}
		return appName;
	}
	
	public static String getAppDesc() {
		return getInstn()._getAppDesc();
	}

	private String _getAppDesc() {
		if(StrUtils.isEmpty(appDesc)) {
			appDesc = getValue(_VerDBMgr.APP_DESC);
		}
		return appDesc;
	}

	public static String getVersion() {
		return getInstn()._getVersion();
	}
	
	private String _getVersion() {
		if(StrUtils.isEmpty(version)) {
			version = getValue(_VerDBMgr.LAST_VER);
		}
		return version;
	}

	public static String getReleaseTime() {
		return getInstn()._getReleaseTime();
	}
	
	private String _getReleaseTime() {
		if(StrUtils.isEmpty(releaseTime)) {
			releaseTime = getValue(_VerDBMgr.RELEASE);
		}
		return releaseTime;
	}

	public static String getAuthor() {
		return getInstn()._getAuthor();
	}
	
	private String _getAuthor() {
		if(StrUtils.isEmpty(author)) {
			author = getValue(_VerDBMgr.AUTHOR);
		}
		return author;
	}
	
}
