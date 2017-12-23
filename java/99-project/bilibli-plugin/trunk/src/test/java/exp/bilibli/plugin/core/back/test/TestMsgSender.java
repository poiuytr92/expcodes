package exp.bilibli.plugin.core.back.test;

import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.core.back.MsgSender;
import exp.bilibli.plugin.envm.ChatColor;

public class TestMsgSender {

	public static void main(String[] args) {
		final String roomId = "269706";
		Browser.init(false);
		Browser.open("http://live.bilibili.com/".concat(roomId));
		Browser.backupCookies();
		System.out.println(Browser.COOKIES());
		
		boolean isOk = MsgSender.sendChat("测试999", ChatColor.YELLOW, roomId, Browser.COOKIES());
		System.out.println(isOk);
		Browser.quit();
	}
	
}
