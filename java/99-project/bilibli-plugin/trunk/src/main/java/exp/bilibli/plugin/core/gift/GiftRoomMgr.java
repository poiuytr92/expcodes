package exp.bilibli.plugin.core.gift;

import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.other.StrUtils;


public class GiftRoomMgr {

	private final static String LIVE_URL_PREFIX = "http://live.bilibili.com/";
	
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
		return StrUtils.concat(LIVE_URL_PREFIX, roomIds.get());
	}
	
	public int size() {
		return roomIds.size();
	}
	
}
