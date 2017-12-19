package exp.bilibli.plugin.cache;

import exp.libs.algorithm.struct.queue.pc.PCQueue;

public class ChatMgr {

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
