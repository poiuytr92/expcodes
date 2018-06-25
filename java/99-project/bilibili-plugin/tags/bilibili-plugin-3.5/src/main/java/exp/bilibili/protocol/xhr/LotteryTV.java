package exp.bilibili.protocol.xhr;

import java.util.Set;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 小电视抽奖
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LotteryTV extends _Lottery {
	
	/** 小电视抽奖URL */
	private final static String TV_JOIN_URL = Config.getInstn().TV_JOIN_URL();
	
	/** 私有化构造函�? */
	protected LotteryTV() {}
	
	/**
	 * 参加小电视抽�?
	 * @param roomId
	 * @param raffleId
	 * @return
	 */
	public static void toLottery(int roomId, String raffleId) {
		int cnt = 0;
		Set<BiliCookie> cookies = CookiesMgr.ALL();
		for(BiliCookie cookie : cookies) {
			String reason = join(LotteryType.TV, cookie, TV_JOIN_URL, roomId, raffleId);
			if(StrUtils.isEmpty(reason)) {
				log.info("[{}] 参与直播�? [{}] 抽奖成功(小电�?)", cookie.NICKNAME(), roomId);
				cnt++;
				
			} else {
				log.info("[{}] 参与直播�? [{}] 抽奖失败(小电�?)", cookie.NICKNAME(), roomId);
				UIUtils.statistics("失败(", reason, "): 直播�? [", roomId, 
						"],账号[", cookie.NICKNAME(), "]");
			}
		}
		
		if(cnt > 0) {
			UIUtils.statistics("成功(小电视x", cnt, "): 直播�? [", roomId, "]");
			UIUtils.updateLotteryCnt(cnt);
		}
	}
	
}
