package exp.blp;

import exp.blp.utils.UIUtils;
import exp.libs.utils.log.LogUtils;


public class Main {

	public static void main(String[] args) {
		LogUtils.loadLogBackConfig(Config.LOG_CONFIG_PATH);
		UIUtils.BeautySwing();
		AppUI.createInstn();
	}
	
}
