package exp.bilibli.plugin;

import exp.bilibli.plugin.cache.Browser;
import exp.libs.utils.os.ThreadUtils;


public class Test {

	public static void main(String[] args) {
		Browser.init(false);
		Browser.open("http://live.bilibili.com/269706");
		
		ThreadUtils.tSleep(5000);
		Browser.screenshot("./log/1.png");
		Browser.toLiveChat("晚上好");
		
		ThreadUtils.tSleep(5000);
		Browser.screenshot("./log/3.png");
		
		System.out.println("finish");
		
		Browser.quit();
	}
	
}
