package exp.libs.warp.ver;

import exp.libs.envm.Delimiter;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.StrUtils;
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
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-22
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final public class VersionMgr {

	/** 单例 */
	private static volatile VersionMgr instance;
	
	/**
	 * 构造函数
	 */
	private VersionMgr() {
		BeautyEyeUtils.init();
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
	 * @param args 入口参数（win下默认为-m, linux下强制为-p）
	 * 		[-p] 打印最后的版本信息（DOS界面）
	 * 		[-m] 版本管理（UI界面）
	 * @return 当前版本信息
	 */
	public static String exec(String... args) {
		return getInstn()._exec(args);
	}
	
	/**
	 * @param args main函数入参: 
	 * 		[-p] 打印最后的版本信息（DOS界面）
	 * 		[-m] 版本管理（UI界面）
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
	 * @return 最新版本信息
	 */
	protected String manage() {
		_VerMgrUI.getInstn()._view();
		
		String curVerInfo = _VerMgrUI.getInstn().getCurVerInfo();
		System.out.println(curVerInfo);
		return curVerInfo;
	}
	
	/**
	 * 打印最新版本信息
	 * @return 最新版本信息
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
	 * @param onlyCurVersion 仅当前版本(即最新版本)
	 * @param detaiHistoty 是否打印历史版本升级内容详单 (仅onlyCurVersion=false时有效)
	 * @return 版本信息
	 */
	public static String getVersionInfo(boolean onlyCurVersion, boolean detaiHistoty) {
		StringBuilder verInfo = new StringBuilder();
		if(_VerDBMgr.getInstn().initVerDB()) {
			verInfo.append(_VerDBMgr.getInstn().getCurVerInfo());
			
			if(onlyCurVersion == false) {
				verInfo.append(Delimiter.CRLF).append(Delimiter.CRLF);
				verInfo.append(_VerDBMgr.getInstn().toHisVerInfos(detaiHistoty));
			}
		}
		return verInfo.toString();
	}
	
}
