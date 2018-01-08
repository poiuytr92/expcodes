package exp.bilibili.plugin.bean.ldm;

import exp.bilibili.plugin.envm.LotteryType;

/**
 * <PRE>
 * 抽奖房间
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LotteryRoom {

	/** 抽奖房间号 */
	private String roomId;
	
	/** 抽奖编号 */
	private String raffleId;
	
	/** 抽奖类型 */
	private LotteryType type;
	
	public LotteryRoom(String roomId) {
		this(roomId, "", LotteryType.OTHER);
	}
	
	public LotteryRoom(String roomId, String raffleId, LotteryType type) {
		this.roomId = roomId;
		this.raffleId = (raffleId == null ? "" : raffleId);
		this.type = (type == null ? LotteryType.OTHER : type);
	}

	public String getRoomId() {
		return roomId;
	}

	public String getRaffleId() {
		return raffleId;
	}
	
	public LotteryType TYPE() {
		return type;
	}

}
