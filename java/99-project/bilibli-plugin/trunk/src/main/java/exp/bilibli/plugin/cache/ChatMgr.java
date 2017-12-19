package exp.bilibli.plugin.cache;

import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.other.StrUtils;

public class ChatMgr {

	private final static String NIGHT_KEY = "晚安^o^";
	
	private final static String[] NIGHT_KEYS = {
		"晚安", "好梦", "早点休息", "我先睡了", "我先睡觉了"
	};
	
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
	
	public void addThxGift(String msg) {
		if(!isAutoThankYou()) {
			UIUtils.chat(msg);
			
		} else {
			// FIXME 压缩数量
			addMsg(msg);
		}
	}
	
	public void addThxGuard(String msg) {
		if(!isAutoThankYou()) {
			UIUtils.chat(msg);
			
		} else {
			addMsg(msg);
		}
	}

	public void addNight(String username, String msg) {
		if(!isAutoThankYou() || msg.startsWith(NIGHT_KEY)) { // 避免自己跟自己对话
			return;
		}
		
		for(String key : NIGHT_KEYS) {
			if(msg.contains(key)) {
				addMsg(StrUtils.concat(NIGHT_KEY, " [", username, "]"));
				break;
			}
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
