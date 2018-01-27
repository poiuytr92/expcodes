package exp.bilibili.plugin.core.back;

import java.util.Iterator;
import java.util.List;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.xhr.Chat;
import exp.bilibili.protocol.xhr.DailyTasks;
import exp.bilibili.protocol.xhr.Login;
import exp.bilibili.protocol.xhr.LotteryEnergy;
import exp.bilibili.protocol.xhr.LotteryStorm;
import exp.bilibili.protocol.xhr.LotteryTV;
import exp.bilibili.protocol.xhr.Other;
import exp.bilibili.protocol.xhr.Redbag;

// FIXME 移植回去每个协议内部， 不需要总协议？
// 最终状态
public class MsgSender {

	public static String queryCertTags() {
		return Other.queryCertificateTags();
	}
	
	public static HttpCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		return Login.toLogin(username, password, vccode, vcCookies);
	}
	
	public static boolean queryUserInfo(HttpCookie cookie) {
		return Login.queryUserInfo(cookie);
	}

	public static boolean toAssn() {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();	// FIXME 仅主号签到有爱社?
		return DailyTasks.toAssn(cookie);
	}
	
	public static void toSign() {
		int roomId = Config.getInstn().SIGN_ROOM_ID();
		roomId = (roomId <= 0 ? UIUtils.getCurRoomId() : roomId);
		
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			DailyTasks.toSign(cookie, roomId);
		}
	}
	
	public static long doMathTasks() {
		long maxNextTaskTime = 0;
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			long nextTaskTime = DailyTasks.doMathTasks(cookie.toNVCookie());
			maxNextTaskTime = (maxNextTaskTime < nextTaskTime ? nextTaskTime : maxNextTaskTime);
		}
		return maxNextTaskTime;
	}
	
	public static List<Integer> queryTopLiveRoomIds(
			final int MAX_PAGES, final int MIN_ONLINE) {
		return Other.queryHotLiveRoomIds(MAX_PAGES, MIN_ONLINE);
	}
	
	public static int scanAndJoinStorms(List<Integer> roomIds) {
		return LotteryStorm.toDo(roomIds);
	}
	
	/**
	 * 节奏风暴抽奖
	 * @param roomId
	 * @return
	 */
	public static boolean toStormLottery(int roomId, String raffleId) {
		return LotteryStorm.toDo(roomId, raffleId);
	}
	
	public static String toTvLottery(int roomId, String raffleId) {
		return LotteryTV.toDo(roomId, raffleId);
	}
	
	public static int toEgLottery(int roomId) {
		return LotteryEnergy.toDo(roomId);
	}
	
	public static String queryRedbagPool() {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();	// FIXME 仅主号
		return Redbag.queryRedbagPool(cookie.toNVCookie());
	}
	
	/**
	 * 2018春节活动：兑换红包
	 * @param id 奖品编号
	 * @param num 兑换数量
	 * @return 
	 * 	{"code":0,"msg":"OK","message":"OK","data":{"award_id":"stuff-3","red_bag_num":1695}}
	 * 	{"code":-404,"msg":"这个奖品已经兑换完啦，下次再来吧","message":"这个奖品已经兑换完啦，下次再来吧","data":[]}
	 */
	public static String exchangeRedbag(String id, int num) {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();	// FIXME 仅主号
		return Redbag.exchangeRedbag(cookie.toNVCookie(), id, num);
	}
	
	public static boolean sendDanmu(String msg) {
		ChatColor color = ChatColor.RANDOM();
		return sendDanmu(msg, color);
	}
	
	public static boolean sendDanmu(String msg, int roomId) {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();
		ChatColor color = ChatColor.RANDOM();
		return Chat.sendDanmu(cookie, roomId, msg, color);
	}
	
	public static boolean sendDanmu(String msg, ChatColor color) {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();
		int roomId = UIUtils.getCurRoomId();
		return Chat.sendDanmu(cookie, roomId, msg, color);
	}
	
	/**
	 * 发送私信
	 * @param sendId 发送账号的用户ID
	 * @param recvId 接收账号的用户ID
	 * @param msg 发送消息
	 * @return
	 */
	public static boolean sendPM(String recvId, String msg) {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();
		return Chat.sendPM(cookie, recvId, msg);
	}
}
