package exp.bilibili.protocol.xhr;

import java.util.Iterator;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;

/**
 * <PRE>
 * 小电视抽奖
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LotteryTV extends _Lottery {
	
	/** 小电视抽奖URL */
	private final static String TV_JOIN_URL = Config.getInstn().TV_JOIN_URL();
	
	/** 私有化构造函数 */
	protected LotteryTV() {}
	
	/**
	 * 小电视抽奖 FIXME 某个用户是否成功
	 * @param roomId
	 * @param raffleId
	 * @return
	 */
	public static String toDo(int roomId, String raffleId) {
		String errDesc = "";
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			errDesc = join(LotteryType.TV, cookie, TV_JOIN_URL, roomId, raffleId);
		}
		return errDesc;
	}
	
}
