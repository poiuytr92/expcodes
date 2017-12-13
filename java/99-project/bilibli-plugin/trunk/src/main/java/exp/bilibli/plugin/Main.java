package exp.bilibli.plugin;

import java.net.URISyntaxException;

import exp.bilibli.plugin.core.gift.WebSockMonitor;
import exp.libs.utils.other.LogUtils;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> xxxxxx
 * <B>SUPPORT：</B> xxxxxx
 * @version   xxxxxx
 * @author    xxxxxx
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		WebSockMonitor.getInstn()._start();
	}
	
	
}
