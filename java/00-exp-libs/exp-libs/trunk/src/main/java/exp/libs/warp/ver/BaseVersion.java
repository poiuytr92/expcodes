package exp.libs.warp.ver;

import exp.libs.utils.os.OSUtils;

public class BaseVersion extends _VerMgr {

	/**
	 * 项目的Version类继承此类后，  通过Version类的main函数调用此方法.
	 * 
	 * @param args main函数入参: 
	 * 		[-p] 打印最后的版本信息（DOS界面）
	 * 		[-m] 版本管理（UI界面）
	 */
	protected void exec(String[] args) {
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
