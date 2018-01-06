package exp.bilibli.plugin.envm;


/**
 * <PRE>
 * B站Json交互报文命令类型
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BiliCmd {
	
	/** 未知消息 */
	public final static BiliCmd UNKNOW = new BiliCmd("UNKNOW");
	
	/** 欢迎老爷消息 */
	private final static String _WELCOME = "WELCOME";
	public final static BiliCmd WELCOME = new BiliCmd(_WELCOME);
	
	/** 欢迎船员消息 */
	private final static String _WELCOME_GUARD = "WELCOME_GUARD";
	public final static BiliCmd WELCOME_GUARD = new BiliCmd(_WELCOME_GUARD);
	
	/** (直播间内)新船员上船消息 */
	private final static String _GUARD_BUY = "GUARD_BUY";
	public final static BiliCmd GUARD_BUY = new BiliCmd(_GUARD_BUY);
	
	/** (全频道)登船消息 */
	private final static String _GUARD_MSG = "GUARD_MSG";
	public final static BiliCmd GUARD_MSG = new BiliCmd(_GUARD_MSG);
	
	/** 系统消息：小电视抽奖、全频道公告等 */
	private final static String _SYS_MSG = "SYS_MSG";
	public final static BiliCmd SYS_MSG = new BiliCmd(_SYS_MSG);
	
	/** 系统礼物: 高能抽奖、高能公告等 */
	private final static String _SYS_GIFT = "SYS_GIFT";
	public final static BiliCmd SYS_GIFT = new BiliCmd(_SYS_GIFT);
	
	/** 投喂消息 */
	private final static String _SEND_GIFT = "SEND_GIFT";
	public final static BiliCmd SEND_GIFT = new BiliCmd(_SEND_GIFT);
	
	/** 弹幕消息 */
	private final static String _DANMU_MSG = "DANMU_MSG";
	public final static BiliCmd DANMU_MSG = new BiliCmd(_DANMU_MSG);
	
	/** 开播通知消息 */
	private final static String _LIVE = "LIVE";
	public final static BiliCmd LIVE = new BiliCmd(_LIVE);
	
	/** 特殊礼物：(直播间内)节奏风暴消息 */
	private final static String _SPECIAL_GIFT = "SPECIAL_GIFT";
	public final static BiliCmd SPECIAL_GIFT = new BiliCmd(_SPECIAL_GIFT);
	
	/** (直播间内)高能抽奖开始消息 */
	private final static String _RAFFLE_START = "RAFFLE_START";
	public final static BiliCmd RAFFLE_START = new BiliCmd(_RAFFLE_START);
	
	/** (直播间内)高能抽奖结束消息 */
	private final static String _RAFFLE_END = "RAFFLE_END";
	public final static BiliCmd RAFFLE_END = new BiliCmd(_RAFFLE_END);
	
	private String cmd;
	
	private BiliCmd(String cmd) {
		this.cmd = cmd;
	}
	
	public String CMD() {
		return cmd;
	}
	
	public static BiliCmd toCmd(String cmd) {
		BiliCmd biliCMD = UNKNOW;
		if(_WELCOME.equals(cmd)) {
			biliCMD = WELCOME;
			
		} else if(_WELCOME_GUARD.equals(cmd)) {
			biliCMD = WELCOME_GUARD;
			
		} else if(_GUARD_BUY.equals(cmd)) {
			biliCMD = GUARD_BUY;
			
		} else if(_SYS_MSG.equals(cmd)) {
			biliCMD = SYS_MSG;
			
		} else if(_SYS_GIFT.equals(cmd)) {
			biliCMD = SYS_GIFT;
			
		} else if(_SEND_GIFT.equals(cmd)) {
			biliCMD = SEND_GIFT;
			
		} else if(_DANMU_MSG.equals(cmd)) {
			biliCMD = DANMU_MSG;
		
		} else if(_LIVE.equals(cmd)) {
			biliCMD = LIVE;
			
		}
		return biliCMD;
	}
}
