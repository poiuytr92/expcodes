package exp.bilibili.protocol.bean.other;

import exp.bilibili.plugin.envm.LotteryType;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 抽奖房间
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LotteryRoom {

	/** 抽奖房间�? */
	private int roomId;
	
	/** 抽奖编号 */
	private String raffleId;
	
	/** 抽奖类型 */
	private LotteryType type;
	
	public LotteryRoom(int roomId) {
		this(roomId, "", LotteryType.ENGERY);
	}
	
	public LotteryRoom(int roomId, String raffleId, LotteryType type) {
		this.roomId = roomId;
		this.raffleId = (StrUtils.isEmpty(raffleId) ? "" : raffleId);
		this.type = (type == null ? LotteryType.ENGERY : type);
	}

	public int getRoomId() {
		return roomId;
	}

	public String getRaffleId() {
		return raffleId;
	}
	
	public LotteryType TYPE() {
		return type;
	}

}
