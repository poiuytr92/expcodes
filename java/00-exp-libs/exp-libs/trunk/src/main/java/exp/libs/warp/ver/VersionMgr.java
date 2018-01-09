package exp.libs.warp.ver;

import exp.libs.utils.os.OSUtils;
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

	private static volatile VersionMgr instance;
	
	private VersionMgr() {
		BeautyEyeUtils.init();
	}
	
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
	public static String exec(String[] args) {
		return getInstn()._exec(args);
	}
	
	/**
	 * @param args main函数入参: 
	 * 		[-p] 打印最后的版本信息（DOS界面）
	 * 		[-m] 版本管理（UI界面）
	 * @return 当前版本信息
	 */
	private String _exec(String[] args) {
		boolean manage = true;
		if(args != null && args.length >= 1) {
			manage = "-m".equals(args[0]);
			if(!OSUtils.isWin()) {
				manage = false;
			}
		}
		return (manage ? manage() : print());
	}
	
	protected String manage() {
		_VerMgrUI.getInstn()._view();
		
		String curVerInfo = _VerMgrUI.getInstn().getCurVerInfo();
		System.out.println(curVerInfo);
		return curVerInfo;
	}
	
	protected String print() {
		String curVerInfo = "";
		
		if(_VerDBMgr.getInstn().initVerDB()) {
			curVerInfo = _VerDBMgr.getInstn().getCurVerInfo();
			System.out.println(curVerInfo);
			
		} else {
			System.err.println("获取当前版本信息失败");
		}
		return curVerInfo;
	}
	
}
