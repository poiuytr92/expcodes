package exp.qw;

import exp.libs.utils.log.LogUtils;
import exp.qw.bean.BrowserDriver;
import exp.qw.utils.UIUtils;

public class Main {

	public static void main(String[] args) {
		LogUtils.loadLogBackConfig(Config.LOG_CONFIG_PATH);
		UIUtils.BeautySwing();
		AppUI.createInstn(BrowserDriver.CHROME);
	}
	
}
