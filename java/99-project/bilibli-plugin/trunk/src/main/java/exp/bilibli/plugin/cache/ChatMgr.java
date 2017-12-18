package exp.bilibli.plugin.cache;

import exp.libs.algorithm.struct.queue.pc.PCQueue;

public class ChatMgr {

	/** 按时序记录待发送的消息 */
	private PCQueue<String> chatMsgs;
	
	private static volatile ChatMgr instance;
	
	private ChatMgr() {
		this.chatMsgs = new PCQueue<String>(1024);
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
	
}
