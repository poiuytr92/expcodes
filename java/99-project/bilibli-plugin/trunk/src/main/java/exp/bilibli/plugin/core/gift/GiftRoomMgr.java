package exp.bilibli.plugin.core.gift;

import exp.libs.algorithm.struct.queue.pc.PCQueue;


public class GiftRoomMgr {

	private PCQueue<String> roomIds;
	
	private static volatile GiftRoomMgr instance;
	
	private GiftRoomMgr() {
		this.roomIds = new PCQueue<String>(1024);
	}
	
	public static GiftRoomMgr getInstn() {
		if(instance == null) {
			synchronized (GiftRoomMgr.class) {
				if(instance == null) {
					instance = new GiftRoomMgr();
				}
			}
		}
		return instance;
	}
	
	public void add(String roomId) {
		roomIds.add(roomId);
	}
	
	public String get() {
		return roomIds.get();
	}
	
	public int size() {
		return roomIds.size();
	}
	
}
