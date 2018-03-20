package exp.crawler.qq;

import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.crawler.qq.ui.AppUI;

public class Main {

	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		BeautyEyeUtils.init();
		
		AppUI.createInstn(args);
	}
	
}
