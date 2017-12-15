package exp.bilibli.plugin.envm;


public class BiliCmd {
	
	/** 未知消息 */
	public final static BiliCmd UNKNOW = new BiliCmd("UNKNOW");
	
	/** 欢迎消息 */
	private final static String _WELCOME_GUARD = "WELCOME_GUARD";
	public final static BiliCmd WELCOME_GUARD = new BiliCmd(_WELCOME_GUARD);
	
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
	
	private String cmd;
	
	private BiliCmd(String cmd) {
		this.cmd = cmd;
	}
	
	public String CMD() {
		return cmd;
	}
	
	public static BiliCmd toCmd(String cmd) {
		BiliCmd biliCMD = UNKNOW;
		if(_WELCOME_GUARD.equals(cmd)) {
			biliCMD = WELCOME_GUARD;
			
		} else if(_SYS_MSG.equals(cmd)) {
			biliCMD = SYS_MSG;
			
		} else if(_SYS_GIFT.equals(cmd)) {
			biliCMD = SYS_GIFT;
			
		} else if(_SEND_GIFT.equals(cmd)) {
			biliCMD = SEND_GIFT;
			
		} else if(_DANMU_MSG.equals(cmd)) {
			biliCMD = DANMU_MSG;
			
		}
		return biliCMD;
	}
}
