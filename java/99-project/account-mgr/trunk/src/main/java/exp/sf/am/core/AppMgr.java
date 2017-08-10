package exp.sf.am.core;

import exp.libs.warp.ui.SwingUtils;

public class AppMgr {

	
	// TODO: 获取备份到。。。
	// 从...导入备份
	
	private AppMgr() {}
	
	public static void createInstn() {
		if(DBMgr.initEnv()) {
			new _LoginWin();
			
		} else {
			SwingUtils.warn("程序无法启动: 初始化失败");
		}
	}
	
}
