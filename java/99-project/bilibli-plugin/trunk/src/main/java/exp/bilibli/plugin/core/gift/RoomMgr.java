package exp.bilibli.plugin.core.gift;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.io.flow.FileFlowReader;


public class RoomMgr {

	private final static String LIVE_URL_PREFIX = "http://live.bilibili.com/";
	
	private final static String ROOM_ID_PATH = "./data/room/roomids.dat";
	
	/**
	 * 房间号（前端用）到 真实房号（后台用）的映射
	 * real_room_id/room_id -> real_room_id
	 */
	private Map<Integer, Integer> realRoomIds;
	
	private PCQueue<String> roomIds;
	
	private static volatile RoomMgr instance;
	
	private RoomMgr() {
		this.realRoomIds = new HashMap<Integer, Integer>();
		this.roomIds = new PCQueue<String>(1024);
		
		readRoomIds();
	}
	
	public static RoomMgr getInstn() {
		if(instance == null) {
			synchronized (RoomMgr.class) {
				if(instance == null) {
					instance = new RoomMgr();
				}
			}
		}
		return instance;
	}
	
	public void add(String roomId) {
		roomIds.add(roomId);
		
		// FIXME 连续抽奖合并
		UIUtils.statistics("已参与直播间 [", roomId, "] 的抽奖.");
	}
	
	public String get() {
		return StrUtils.concat(LIVE_URL_PREFIX, roomIds.get());
	}
	
	public int size() {
		return roomIds.size();
	}
	
	public void add(String roomId, String readRoomId) {
		add(NumUtils.toInt(roomId, 0), NumUtils.toInt(readRoomId, 0));
	}
	
	public void add(int roomId, int readRoomId) {
		if(roomId <= 0 && readRoomId <= 0) {
			return;
			
		} else if(roomId > 0 && readRoomId > 0) {
			realRoomIds.put(roomId, readRoomId);
			realRoomIds.put(readRoomId, readRoomId);
			
		} else if(roomId > 0) {
			realRoomIds.put(roomId, roomId);
			
		} else if(readRoomId > 0) {
			realRoomIds.put(readRoomId, readRoomId);
		}
		
		writeRoomIds();
	}
	
	public int getRealRoomId(String roomId) {
		return getRealRoomId(NumUtils.toInt(roomId, 0));
	}
	
	public int getRealRoomId(int roomId) {
		int realRoomId = 0;
		Integer rrId = realRoomIds.get(roomId);
		if(rrId != null) {
			realRoomId = rrId.intValue();
		}
		return realRoomId;
	}
	
	private void readRoomIds() {
		FileFlowReader ffr = new FileFlowReader(ROOM_ID_PATH, Charset.ISO);
		while(ffr.hasNextLine()) {
			String line = ffr.readLine().trim();
			String[] kv = line.split("=");
			if(kv.length == 2) {
				realRoomIds.put(NumUtils.toInt(kv[0]), NumUtils.toInt(kv[1]));
			}
		}
	}
	
	private void writeRoomIds() {
		StringBuilder sb = new StringBuilder();
		Iterator<Integer> keyIts = realRoomIds.keySet().iterator();
		while(keyIts.hasNext()) {
			Integer key = keyIts.next();
			Integer val = realRoomIds.get(key);
			sb.append(key).append("=").append(val).append("\r\n");
		}
		FileUtils.write(ROOM_ID_PATH, sb.toString(), Charset.ISO, false);
	}
	
}
