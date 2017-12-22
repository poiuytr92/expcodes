package exp.bilibli.plugin.bean.ldm;

/**
 * <PRE>
 * 抽奖房间
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LotteryRoom {

	/** 抽奖房间号 */
	private String roomId;
	
	/** 小电视编号(只有是小电视房间时才有效) */
	private String tvId;
	
	public LotteryRoom(String roomId) {
		this(roomId, "");
	}
	
	public LotteryRoom(String roomId, String tvId) {
		this.roomId = roomId;
		this.tvId = (tvId == null ? "" : tvId);
	}

	public String getRoomId() {
		return roomId;
	}

	public String getTvId() {
		return tvId;
	}
	
}
