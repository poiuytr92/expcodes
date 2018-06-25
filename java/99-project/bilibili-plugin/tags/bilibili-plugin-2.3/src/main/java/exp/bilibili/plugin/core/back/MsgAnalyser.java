package exp.bilibili.plugin.core.back;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.pdm.ActivityEvent;
import exp.bilibili.plugin.bean.pdm.ChatMsg;
import exp.bilibili.plugin.bean.pdm.EnergyLottery;
import exp.bilibili.plugin.bean.pdm.GuardBuy;
import exp.bilibili.plugin.bean.pdm.GuardMsg;
import exp.bilibili.plugin.bean.pdm.LiveMsg;
import exp.bilibili.plugin.bean.pdm.Preparing;
import exp.bilibili.plugin.bean.pdm.RaffleEnd;
import exp.bilibili.plugin.bean.pdm.RaffleStart;
import exp.bilibili.plugin.bean.pdm.SendGift;
import exp.bilibili.plugin.bean.pdm.SpecialGift;
import exp.bilibili.plugin.bean.pdm.SysGift;
import exp.bilibili.plugin.bean.pdm.SysMsg;
import exp.bilibili.plugin.bean.pdm.TvLottery;
import exp.bilibili.plugin.bean.pdm.WelcomeGuard;
import exp.bilibili.plugin.bean.pdm.WelcomeMsg;
import exp.bilibili.plugin.bean.pdm.WishBottle;
import exp.bilibili.plugin.cache.ActivityMgr;
import exp.bilibili.plugin.cache.ChatMgr;
import exp.bilibili.plugin.cache.MsgKwMgr;
import exp.bilibili.plugin.cache.OnlineUserMgr;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.BiliCmd;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * B站json命令报文解析器
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MsgAnalyser {

	private final static Logger log = LoggerFactory.getLogger(MsgAnalyser.class);
	
	protected MsgAnalyser() {}
	
	public static boolean toMsgBean(BiliCmd biliCmd, JSONObject json) {
		boolean isOk = true;
		if(biliCmd == BiliCmd.DANMU_MSG) {
			toDo(new ChatMsg(json));
			
		} else if(biliCmd == BiliCmd.SEND_GIFT) {
			toDo(new SendGift(json));
			
		} else if(biliCmd == BiliCmd.SYS_MSG) {
			if(StrUtils.isNotEmpty(_getRoomId(json))) {
				toDo(new TvLottery(json));
				
			} else {
				toDo(new SysMsg(json));
			}
			
		} else if(biliCmd == BiliCmd.SYS_GIFT) {
			if(StrUtils.isNotEmpty(_getRoomId(json))) {
				toDo(new EnergyLottery(json));
				
			} else {
				toDo(new SysGift(json));
			}
			
		} else if(biliCmd == BiliCmd.SPECIAL_GIFT) {
			toDo(new SpecialGift(json));
			
		} else if(biliCmd == BiliCmd.RAFFLE_START) {
			toDo(new RaffleStart(json));
			
		} else if(biliCmd == BiliCmd.RAFFLE_END) {
			toDo(new RaffleEnd(json));
			
		} else if(biliCmd == BiliCmd.WELCOME) {
			toDo(new WelcomeMsg(json));
			
		} else if(biliCmd == BiliCmd.WELCOME_GUARD) {
			toDo(new WelcomeGuard(json));
			
		} else if(biliCmd == BiliCmd.GUARD_BUY) {
			toDo(new GuardBuy(json));
			
		} else if(biliCmd == BiliCmd.GUARD_MSG) {
			toDo(new GuardMsg(json));
			
		} else if(biliCmd == BiliCmd.LIVE) {
			toDo(new LiveMsg(json));
			
		} else if(biliCmd == BiliCmd.PREPARING) {
			toDo(new Preparing(json));
			
		} else if(biliCmd == BiliCmd.WISH_BOTTLE) {
			toDo(new WishBottle(json));
			
		} else if(biliCmd == BiliCmd.ACTIVITY_EVENT) {
			toDo(new ActivityEvent(json));
			
		} else {
			isOk = false;
		}
		return isOk;
	}
	
	/**
	 * 用户发言消息
	 * @param msgBean
	 */
	private static void toDo(ChatMsg msgBean) {
		String msg = StrUtils.concat(
				"[", msgBean.getMedal(), "][LV", msgBean.getLevel(), "][",
				msgBean.getUsername(), "]: ", msgBean.getMsg()
		);
		UIUtils.chat(msg);
		log.info(msg);
		
		OnlineUserMgr.getInstn().add(msgBean.getUsername());
		ActivityMgr.getInstn().add(msgBean);
		ChatMgr.getInstn().addNight(msgBean.getUsername(), msgBean.getMsg());
		ChatMgr.getInstn().countChatCnt(msgBean.getUsername());
	}
	
	/**
	 * 礼物投喂消息
	 * @param msgBean
	 */
	private static void toDo(SendGift msgBean) {
		String msg = StrUtils.concat(
				"[", msgBean.getUname(), "] ", msgBean.getAction(), 
				" [", msgBean.getGiftName(), "] x", msgBean.getNum()
		);
		UIUtils.chat(msg);
		log.info(msg);
		
		ChatMgr.getInstn().addThxGift(msgBean);
		OnlineUserMgr.getInstn().add(msgBean.getUname());
		ActivityMgr.getInstn().add(msgBean);
	}
	
	/**
	 * 系统消息
	 * @param msgBean
	 */
	private static void toDo(SysMsg msgBean) {
		UIUtils.notify(msgBean.getMsg());	// 系统公告的消息体里面自带�? [系统公告: ]
		log.info(msgBean.getMsg());
	}
	
	/**
	 * 小电视通知
	 * @param msgBean
	 */
	private static void toDo(TvLottery msgBean) {
		String msg = StrUtils.concat("直播�? [", msgBean.ROOM_ID(), "] 正在小电视抽奖中!!!");
		UIUtils.notify(msg);
		log.info(msg);
		
		RoomMgr.getInstn().addTvRoom(msgBean.ROOM_ID(), msgBean.getTvId());
		RoomMgr.getInstn().relate(msgBean.getRoomId(), msgBean.getRealRoomId());
	}
	
	/**
	 * 全频道礼物通知
	 * @param msgBean
	 */
	private static void toDo(SysGift msgBean) {
		String msg = StrUtils.concat("礼物公告�?", msgBean.getMsgText());
		UIUtils.notify(msg);
		log.info(msg);
	}
	
	/**
	 * 高能礼物抽奖消息
	 * @param msgBean
	 */
	private static void toDo(EnergyLottery msgBean) {
		String msg = StrUtils.concat("直播�? [", msgBean.ROOM_ID(), "] 正在高能抽奖�?!!!");
		UIUtils.notify(msg);
		log.info(msg);
		
		RoomMgr.getInstn().addGiftRoom(msgBean.ROOM_ID());
		RoomMgr.getInstn().relate(msgBean.getRoomId(), msgBean.getRealRoomId());
	}
	
	/**
	 * 特殊礼物�?(直播间内)节奏风暴消息
	 * @param msgBean
	 */
	private static void toDo(SpecialGift msgBean) {
		String msg = StrUtils.concat("直播�? [", msgBean.getRoomId(), "] 开启了节奏风暴!!!");
		UIUtils.notify(msg);
		log.info(msg);
		
		RoomMgr.getInstn().addStormRoom(msgBean.getRoomId(), msgBean.getRaffleId());
	}

	/**
	 * (直播间内)高能抽奖开始消�?
	 * @param msgBean
	 */
	private static void toDo(RaffleStart msgBean) {
		String msg = StrUtils.concat("感谢 [", msgBean.getFrom(), "] 的嗨翻全�?!!!");
		log.info(msg);
		
		ChatMgr.getInstn().sendThxEnergy(msg);
		RoomMgr.getInstn().addGiftRoom(msgBean.getRoomId());
	}

	/**
	 * (直播间内)高能抽奖结束消息
	 * @param msgBean
	 */
	private static void toDo(RaffleEnd msgBean) {
		String msg = StrUtils.concat("恭喜非酉[", msgBean.getWinner(), 
				"]竟然抽到了[", msgBean.getGiftName(), "]x", msgBean.getGiftNum());
		log.info(msg);
		
		ChatMgr.getInstn().sendThxEnergy(msg);
	}
	
	/**
	 * 欢迎老爷消息
	 * @param msgBean
	 */
	private static void toDo(WelcomeMsg msgBean) {
		String msg = StrUtils.concat("[", msgBean.getVipDesc(), "][", 
				msgBean.getUsername(), "] ", MsgKwMgr.getAdj(), "溜进了直播间"
		);
		UIUtils.chat(msg);
		log.info(msg);
	}
	
	/**
	 * 欢迎船员消息
	 * @param msgBean
	 */
	private static void toDo(WelcomeGuard msgBean) {
		String msg = StrUtils.concat("[", msgBean.getGuardDesc(), "][", 
				msgBean.getUsername(), "] ", MsgKwMgr.getAdj(), "溜进了直播间"
		);
		UIUtils.chat(msg);
		log.info(msg);
	}
	
	/**
	 * (直播间内)新船员上船消�?
	 * @param msgBean
	 */
	private static void toDo(GuardBuy msgBean) {
		String msg = StrUtils.concat("[", msgBean.getGuardDesc(), "][", 
				msgBean.getUsername(), "] ", MsgKwMgr.getAdj(), "上了贼船,活跃+",
				ActivityMgr.showCost(msgBean.getGuardDesc(), 1)
		);
		UIUtils.chat(msg);
		log.info(msg);
			
		ChatMgr.getInstn().sendThxGuard(msg);
		OnlineUserMgr.getInstn().add(msgBean.getUsername());
		ActivityMgr.getInstn().add(msgBean);
	}

	/**
	 * (全频�?)登船消息
	 * @param msgBean
	 */
	private static void toDo(GuardMsg msgBean) {
		UIUtils.chat(msgBean.getMsg());
		log.info(msgBean.getMsg());
	}
	
	/**
	 * 开播通知
	 * @param msgBean
	 */
	private static void toDo(LiveMsg msgBean) {
		String msg = StrUtils.concat("您关注的直播�? [", msgBean.getRoomId(), "] 开播啦!!!");
		UIUtils.chat(msg);
		log.info(msg);
		
		ChatMgr.getInstn().helloLive(msgBean.getRoomId());
		UIUtils.notityLive(msgBean.getRoomId());
	}
	
	/**
	 * 关播通知
	 * @param msgBean
	 */
	private static void toDo(Preparing msgBean) {
		String msg = StrUtils.concat("直播�? [", msgBean.getRoomId(), "] 主播已下�?.");
		UIUtils.chat(msg);
		log.info(msg);
	}
	
	/**
	 * (直播间内)许愿瓶实现进度消�?
	 * @param wishBottle
	 */
	private static void toDo(WishBottle msgBean) {
		// Undo
	}
	
	/**
	 * 2018春节活动(新春�?)触发事件
	 * @param wishBottle
	 */
	private static void toDo(ActivityEvent msgBean) {
		// Undo
	}
	
	/**
	 * 获取抽奖房间�?
	 * @param json
	 * @return
	 */
	private static String _getRoomId(JSONObject json) {
		String roomId = JsonUtils.getStr(json, BiliCmdAtrbt.real_roomid);
		if(StrUtils.isEmpty(roomId)) {
			roomId = JsonUtils.getStr(json, BiliCmdAtrbt.roomid);
		}
		return roomId;
	}
	
}
