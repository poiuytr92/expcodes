package exp.libs.warp.ver;

import exp.libs.utils.os.OSUtils;

final public class BaseVersion extends _VerMgr {

	private static volatile BaseVersion instance;
	
	private BaseVersion() {}
	
	private static BaseVersion getInstn() {
		if(instance == null) {
			synchronized (BaseVersion.class) {
				if(instance == null) {
					instance = new BaseVersion();
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
	
}
