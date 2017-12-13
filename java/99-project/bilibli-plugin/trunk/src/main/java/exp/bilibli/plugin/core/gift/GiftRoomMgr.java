package exp.bilibli.plugin.core.gift;

import exp.libs.algorithm.struct.queue.pc.PCQueue;

/**
 * 
小电视抽奖：
{
"cmd": "SYS_MSG",
"msg": "\u3010\u745f\u60c5\u7684\u74f6\u5b50\u83cc\u3011:?\u5728\u76f4\u64ad\u95f4:?\u30103779462\u3011:?\u8d60\u9001 \u5c0f\u7535\u89c6\u4e00\u4e2a\uff0c\u8bf7\u524d\u5f80\u62bd\u5956",
"msg_text": "\u3010\u745f\u60c5\u7684\u74f6\u5b50\u83cc\u3011:?\u5728\u76f4\u64ad\u95f4:?\u30103779462\u3011:?\u8d60\u9001 \u5c0f\u7535\u89c6\u4e00\u4e2a\uff0c\u8bf7\u524d\u5f80\u62bd\u5956",
"rep": 1,
"styleType": 2,
"url": "http:\/\/live.bilibili.com\/3779462",
"roomid": 3779462,
"real_roomid": 3779462,
"rnd": 1822599641,
"tv_id": "31572"
}

火力抽奖：
{
"cmd": "SYS_GIFT",
"msg": "00\u515c\u515c00\u5728\u76f4\u64ad\u95f45279\u706b\u529b\u5168\u5f00\uff0c\u55e8\u7ffb\u5168\u573a\uff0c\u901f\u53bb\u56f4\u89c2\uff0c\u8fd8\u80fd\u514d\u8d39\u9886\u53d6\u706b\u529b\u7968\uff01",
"msg_text": "00\u515c\u515c00\u5728\u76f4\u64ad\u95f45279\u706b\u529b\u5168\u5f00\uff0c\u55e8\u7ffb\u5168\u573a\uff0c\u901f\u53bb\u56f4\u89c2\uff0c\u8fd8\u80fd\u514d\u8d39\u9886\u53d6\u706b\u529b\u7968\uff01",
"tips": "00\u515c\u515c00\u5728\u76f4\u64ad\u95f45279\u706b\u529b\u5168\u5f00\uff0c\u55e8\u7ffb\u5168\u573a\uff0c\u901f\u53bb\u56f4\u89c2\uff0c\u8fd8\u80fd\u514d\u8d39\u9886\u53d6\u706b\u529b\u7968\uff01",
"url": "http:\/\/live.bilibili.com\/5279",
"roomid": 5279,
"real_roomid": 5279,
"giftId": 106,
"msgTips": 0
}

公告
{
"cmd": "SYS_GIFT",
"msg": "茕茕茕茕孑立丶:?  在裕刺Fy的:?直播间447:?内赠送:?105:?共367个",
"rnd": "0",
"uid": 8277884,
"msg_text": "茕茕茕茕孑立丶在裕刺Fy的直播间447内赠送火力票共367个"
}
 */
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
