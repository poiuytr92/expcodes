package exp.bilibili.protocol.xhr;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.LotteryType;

public class TvLottery extends _Lottery {
	
	private final static String TV_JOIN_URL = Config.getInstn().TV_JOIN_URL();
	
	/**
	 * 小电视抽奖
	 * @param roomId
	 * @param raffleId
	 * @return
	 */
	public static String toTvLottery(String cookie, int roomId, String raffleId) {
		return joinLottery(TV_JOIN_URL, roomId, raffleId, cookie, "", LotteryType.TV);
	}
	
}
