package exp.bilibli.plugin.cache;

import exp.bilibli.plugin.bean.pdm.SendGift;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 在线聊天管理器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ChatMgr {

	private final static String NIGHT_KEY = "晚安^o^";
	
	/** 按时序记录待发送的消息 */
	private PCQueue<String> chatMsgs;
	
	/** 自动答谢 */
	private boolean autoThankYou;
	
	/** 自动晚安 */
	private boolean autoGoodNight;
	
	private static volatile ChatMgr instance;
	
	private ChatMgr() {
		this.chatMsgs = new PCQueue<String>(1024);
		this.autoThankYou = false;
		this.autoGoodNight = false;
	}
	
	public static ChatMgr getInstn() {
		if(instance == null) {
			synchronized (ChatMgr.class) {
				if(instance == null) {
					instance = new ChatMgr();
				}
			}
		}
		return instance;
	}
	
	public void addMsg(String msg) {
		chatMsgs.add(msg);
	}
	
	public void addThxGift(SendGift msgBean) {
		if(!isAutoThankYou()) {
			return;
		}
		
		// 小于1个的辣条或亿圆就不感谢了，避免刷屏
		int num = NumUtils.toInt(msgBean.getNum(), 0);
		if(num <= 1) {
			if("辣条".equals(msgBean.getGiftName()) || 
					"亿圆".equals(msgBean.getGiftName())) {
				return;
			}
		}
		
		String msg = StrUtils.concat(
				"感谢 [", msgBean.getUname(), "] ", KeywordMgr.getAdj(), 
				msgBean.getAction(), " [", msgBean.getGiftName(), 
				"] x", msgBean.getNum());
		addMsg(msg);
	}
	
	public void addThxGuard(String msg) {
		if(!isAutoThankYou()) {
			return;
		}
		
		addMsg("感谢 ".concat(msg));
	}

	public void addNight(String username, String msg) {
		if(!isAutoThankYou() || msg.startsWith(NIGHT_KEY)) { // 避免自己跟自己对话
			return;
		}
		
		if(KeywordMgr.containsNight(msg)) {
			addMsg(StrUtils.concat(NIGHT_KEY, ", ", username));
		}
	}
	
	public String getMsg() {
		return chatMsgs.getQuickly();
	}
	
	public void clear() {
		chatMsgs.clear();
	}
	
	public void setAutoThankYou() {
		autoThankYou = !autoThankYou;
	}
	
	public boolean isAutoThankYou() {
		return autoThankYou;
	}
	
	public void setAutoGoodNight() {
		autoGoodNight = !autoGoodNight;
	}
	
	public boolean isAutoGoodNight() {
		return autoGoodNight;
	}
	
}
