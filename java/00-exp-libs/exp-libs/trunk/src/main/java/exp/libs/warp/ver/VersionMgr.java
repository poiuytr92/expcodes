package exp.libs.warp.ver;

import exp.libs.utils.os.OSUtils;
import exp.libs.warp.ui.BeautyEyeUtils;

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
	 * @param args main函数入参: 
	 * 		[-p] 打印最后的版本信息（DOS界面）
	 * 		[-m] 版本管理（UI界面）
	 */
	public static void exec(String[] args) {
		getInstn()._exec(args);
	}
	
	/**
	 * @param args main函数入参: 
	 * 		[-p] 打印最后的版本信息（DOS界面）
	 * 		[-m] 版本管理（UI界面）
	 */
	private void _exec(String[] args) {
		boolean manage = true;
		if(args != null && args.length >= 1) {
			manage = "-m".equals(args[0]);
			if(!OSUtils.isWin()) {
				manage = false;
			}
		}
		
		if(manage == true) {
			manage();
		} else {
			print();
		}
	}
	
	protected void manage() {
		_VerMgrUI.getInstn();
	}
	
	protected void print() {
		// 打印DOS
	}
	
}
