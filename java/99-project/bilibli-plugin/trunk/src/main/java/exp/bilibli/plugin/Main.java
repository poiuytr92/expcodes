package exp.bilibli.plugin;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.core.gift.GiftRoomMgr;
import exp.bilibli.plugin.core.gift.WebSockMonitor;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.LogUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;


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
