package exp.bilibli.plugin.core.back;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.bean.pdm.ChatMsg;
import exp.bilibli.plugin.bean.pdm.EnergyLottery;
import exp.bilibli.plugin.bean.pdm.GuardBuyMsg;
import exp.bilibli.plugin.bean.pdm.LiveMsg;
import exp.bilibli.plugin.bean.pdm.SendGift;
import exp.bilibli.plugin.bean.pdm.SysGift;
import exp.bilibli.plugin.bean.pdm.SysMsg;
import exp.bilibli.plugin.bean.pdm.TvLottery;
import exp.bilibli.plugin.bean.pdm.WelcomeGuardMsg;
import exp.bilibli.plugin.bean.pdm.WelcomeMsg;
import exp.bilibli.plugin.cache.ChatMgr;
import exp.bilibli.plugin.cache.KeywordMgr;
import exp.bilibli.plugin.cache.OnlineUserMgr;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.envm.BiliCmd;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * B站json命令报文解析器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MsgAnalyser {

	private final static Logger log = LoggerFactory.getLogger(MsgAnalyser.class);
	
	protected MsgAnalyser() {}
	
	public static boolean toMsgBean(JSONObject json) {
		boolean isOk = true;
		String cmd = JsonUtils.getStr(json, BiliCmdAtrbt.cmd);
		BiliCmd biliCmd = BiliCmd.toCmd(cmd);
		
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
			
		} else if(biliCmd == BiliCmd.WELCOME) {
			toDo(new WelcomeMsg(json));
			
		} else if(biliCmd == BiliCmd.WELCOME_GUARD) {
			toDo(new WelcomeGuardMsg(json));
			
		} else if(biliCmd == BiliCmd.GUARD_BUY) {
			toDo(new GuardBuyMsg(json));
			
		} else if(biliCmd == BiliCmd.LIVE) {
			toDo(new LiveMsg(json));
			
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
		ChatMgr.getInstn().addNight(msgBean.getUsername(), msgBean.getMsg());
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
	}
	
	/**
	 * 系统消息
	 * @param msgBean
	 */
	private static void toDo(SysMsg msgBean) {
		UIUtils.notify(msgBean.getMsg());	// 系统公告的消息体里面自带了 [系统公告: ]
		log.info(msgBean.getMsg());
	}
	
	/**
	 * 小电视通知
	 * @param msgBean
	 */
	private static void toDo(TvLottery msgBean) {
		String msg = StrUtils.concat("直播间 [", msgBean.ROOM_ID(), "] 正在小电视抽奖中!!!");
		UIUtils.notify(msg);
		log.info(msg);
		
		RoomMgr.getInstn().addGiftRoom(msgBean.ROOM_ID());
		RoomMgr.getInstn().relate(msgBean.getRoomId(), msgBean.getRealRoomId());
	}
	
	/**
	 * 全频道礼物通知
	 * @param msgBean
	 */
	private static void toDo(SysGift msgBean) {
		String msg = StrUtils.concat("礼物公告：", msgBean.getMsgText());
		UIUtils.notify(msg);
		log.info(msg);
	}
	
	/**
	 * 高能礼物抽奖消息
	 * @param msgBean
	 */
	private static void toDo(EnergyLottery msgBean) {
		String msg = StrUtils.concat("直播间 [", msgBean.ROOM_ID(), "] 正在高能抽奖中!!!");
		UIUtils.notify(msg);
		log.info(msg);
		
		RoomMgr.getInstn().addGiftRoom(msgBean.ROOM_ID());
		RoomMgr.getInstn().relate(msgBean.getRoomId(), msgBean.getRealRoomId());
	}
	
	/**
	 * 欢迎老爷消息
	 * @param msgBean
	 */
	private static void toDo(WelcomeMsg msgBean) {
		String msg = StrUtils.concat("[", msgBean.getVipDesc(), "][", 
				msgBean.getUsername(), "] ", KeywordMgr.getAdj(), "溜进了直播间"
		);
		UIUtils.chat(msg);
		log.info(msg);
	}
	
	/**
	 * 欢迎船员消息
	 * @param msgBean
	 */
	private static void toDo(WelcomeGuardMsg msgBean) {
		String msg = StrUtils.concat(
				"[", msgBean.getUid(), "][", msgBean.getGuardDesc(), "][", 
				msgBean.getUsername(), "] ", KeywordMgr.getAdj(), "溜进了直播间"
		);
		UIUtils.chat(msg);
		log.info(msg);
	}
	
	/**
	 * 上船消息
	 * @param msgBean
	 */
	private static void toDo(GuardBuyMsg msgBean) {
		String msg = StrUtils.concat(
				"[", msgBean.getUid(), "][", msgBean.getGuardDesc(), "][", 
				msgBean.getUsername(), "] ", KeywordMgr.getAdj(), "上了贼船"
		);
		UIUtils.chat(msg);
		log.info(msg);
		
		ChatMgr.getInstn().addThxGuard(msg);
		OnlineUserMgr.getInstn().add(msgBean.getUsername());
	}

	/**
	 * 开播通知
	 * @param msgBean
	 */
	private static void toDo(LiveMsg msgBean) {
		String msg = StrUtils.concat("您关注的直播间 [", msgBean.getRoomId(), "] 开播啦!!!");
		UIUtils.chat(msg);
		log.info(msg);
		
		ChatMgr.getInstn().helloLive(msgBean.getRoomId());
	}
	
	/**
	 * 获取抽奖房间号
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
