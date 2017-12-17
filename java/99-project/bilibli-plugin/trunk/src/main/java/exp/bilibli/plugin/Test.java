package exp.bilibli.plugin;

import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.cache.BrowserMgr;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;

public class Test {

	
	public static void main(String[] args) {
		BrowserDriver browser = BrowserMgr.getInstn().getBrowser();
		browser.getDriver().navigate().to(Config.getInstn().LOGIN_URL());
		
		ThreadUtils.tSleep(3000);
		FileUtils.write("./log/1.html", browser.getDriver().getPageSource(), false);
		
		browser = BrowserMgr.getInstn().reCreate(true);
		browser.getDriver().navigate().to(Config.getInstn().LOGIN_URL());
		ThreadUtils.tSleep(3000);
		FileUtils.write("./log/2.html", browser.getDriver().getPageSource(), false);
		
	}
}
