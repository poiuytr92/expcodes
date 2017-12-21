package exp.bilibli.plugin.core.back;

import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.envm.ChatColor;

public class TestMsgSender {

	public static void main(String[] args) {
		Browser.init(false);
		Browser.open("http://live.bilibili.com/269706");
		Browser.backupCookies();
		System.out.println(Browser.COOKIES());
		
		boolean isOk = MsgSender.sendChat("测试999", Browser.COOKIES(), "269706", ChatColor.GOLDEN);
		System.out.println(isOk);
		Browser.quit();
	}
	
}
