package exp.bilibili.plugin.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.LotteryRoom;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.envm.LotteryType;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.warp.io.flow.FileFlowReader;


/**
 * <PRE>
 * 直播房间管理器
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RoomMgr {

	/** 记录房间号（前端用）�? 真实房号（后台用）的映射 */
	private final static String ROOM_PATH = Config.getInstn().ROOM_PATH();
	
	/**
	 * 房间号（前端用）�? 真实房号（后台用）的映射
	 * real_room_id/room_id -> real_room_id
	 * 
	 * 连接websocket后台只能用real_room_id,
	 * room_id 是签约主播才有的房间�?, 若通过real_room_id打开页面，会自动修正为room_id.
	 * 
	 * 目前数据来源是房间抽奖时推送过来的通知消息.
	 */
	private Map<Integer, Integer> realRoomIds;
	
	/** 按时序记录的可以抽奖礼物房间�? */
	private PCQueue<LotteryRoom> giftRoomIds;
	
	private static volatile RoomMgr instance;
	
	private RoomMgr() {
		this.realRoomIds = new HashMap<Integer, Integer>();
		this.giftRoomIds = new PCQueue<LotteryRoom>(128);
		
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
	
	/**
	 * 添加高能礼物房间
	 * @param roomId 礼物房间�?
	 */
	public void addGiftRoom(int roomId) {
		giftRoomIds.add(new LotteryRoom(roomId));
	}
	
	/**
	 * 添加节奏风暴礼物房间
	 * @param roomId 礼物房间�?
	 */
	public void addStormRoom(int roomId, String stormId) {
		
		// 节奏风暴 因为对点击速度要求很高, 不放到抽奖房间队列排�?, 直接抽奖
//		giftRoomIds.add(new LotteryRoom(roomId, stormId, LotteryType.STORM));
		
		MsgSender.toStormLottery(roomId, stormId);
	}
	
	/**
	 * 添加小电视房�?
	 * @param roomId 小电视房间号
	 * @param tvId 小电视编�?
	 */
	public void addTvRoom(int roomId, String tvId) {
		giftRoomIds.add(new LotteryRoom(roomId, tvId, LotteryType.TV));
	}
	
	/**
	 * 获取抽奖房间
	 * @return 若无房间号则马上返回null
	 */
	public LotteryRoom getGiftRoom() {
		return giftRoomIds.getQuickly();
	}
	
	/**
	 * 获取剩余的礼物房数量
	 * @return
	 */
	public int getGiftRoomCount() {
		return giftRoomIds.size();
	}
	
	/**
	 * 清空礼物房间记录
	 */
	public void clearGiftRooms() {
		giftRoomIds.clear();
	}
	
	/**
	 * 关联 房间�? �? 真实房间�?
	 * @param roomId 房间号（限签约主播）
	 * @param readRoomId 真实房间号（签约主播 �? 非签约主�? 均拥有）
	 */
	public void relate(int roomId, int readRoomId) {
		if(getRealRoomId(roomId) > 0) {
			return;
		}
		
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
	
	/**
	 * 提取真实房间�?
	 * @param roomId 房间号（限签约主播）
	 * @return 真实房间号（签约主播 �? 非签约主�? 均拥有）, 若未收集到该房间则返�?0
	 */
	public int getRealRoomId(int roomId) {
		int realRoomId = 0;
		Integer rrId = realRoomIds.get(roomId);
		if(rrId != null) {
			realRoomId = rrId.intValue();
		}
		return realRoomId;
	}
	
	/**
	 * 从外存读取真实房号记�?
	 */
	private void readRoomIds() {
		FileFlowReader ffr = new FileFlowReader(ROOM_PATH, Charset.ISO);
		while(ffr.hasNextLine()) {
			String line = ffr.readLine().trim();
			String[] kv = line.split("=");
			if(kv.length == 2) {
				realRoomIds.put(NumUtils.toInt(kv[0]), NumUtils.toInt(kv[1]));
			}
		}
	}
	
	/**
	 * 把内存的真实房号记录保存到外�?
	 */
	private void writeRoomIds() {
		StringBuilder sb = new StringBuilder();
		Iterator<Integer> keyIts = realRoomIds.keySet().iterator();
		while(keyIts.hasNext()) {
			Integer key = keyIts.next();
			Integer val = realRoomIds.get(key);
			sb.append(key).append("=").append(val).append("\r\n");
		}
		FileUtils.write(ROOM_PATH, sb.toString(), Charset.ISO, false);
	}
	
	public Set<Integer> getRealRoomIds() {
		return new HashSet<Integer>(realRoomIds.values());
	}
	
}
