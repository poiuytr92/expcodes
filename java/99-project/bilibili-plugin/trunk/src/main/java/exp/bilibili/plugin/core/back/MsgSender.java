package exp.bilibili.plugin.core.back;

import java.util.Iterator;
import java.util.List;

import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.xhr.Assn;
import exp.bilibili.protocol.xhr.Certificate;
import exp.bilibili.protocol.xhr.Chat;
import exp.bilibili.protocol.xhr.DailyTasks;
import exp.bilibili.protocol.xhr.EnergyLottery;
import exp.bilibili.protocol.xhr.Login;
import exp.bilibili.protocol.xhr.PrivateMsg;
import exp.bilibili.protocol.xhr.Redbag;
import exp.bilibili.protocol.xhr.Sign;
import exp.bilibili.protocol.xhr.StormLottery;
import exp.bilibili.protocol.xhr.TvLottery;
import exp.bilibili.protocol.xhr.UserInfo;

// FIXME 移植回去每个协议内部， 不需要总协议？
public class MsgSender {

	public static String queryCertTags(final String BILIBILI_URL) {
		return Certificate.queryTags(BILIBILI_URL);
	}
	
	public static HttpCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		return Login.toLogin(username, password, vccode, vcCookies);
	}
	
	public static String queryUsername(HttpCookie cookie) {
		return UserInfo.queryUsername(cookie.toNVCookie());
	}

	public static boolean toAssn() {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();	// FIXME 仅主号签到有爱社?
		return Assn.toAssn(cookie.toNVCookie(), cookie.CSRF());
	}
	
	public static void toSign() {
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			Sign.toSign(cookie.toNVCookie());
		}
	}
	
	public static long doDailyTasks() {
		long maxNextTaskTime = 0;
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			long nextTaskTime = DailyTasks.doDailyTasks(cookie.toNVCookie());
			maxNextTaskTime = (maxNextTaskTime < nextTaskTime ? nextTaskTime : maxNextTaskTime);
		}
		return maxNextTaskTime;
	}
	
	public static List<Integer> queryTopLiveRoomIds(
			final int MAX_PAGES, final int MIN_ONLINE) {
		HttpCookie cookie = CookiesMgr.INSTN().VEST();	// 使用马甲号扫描
		return StormLottery.queryTopLiveRoomIds(
				cookie.toNVCookie(), MAX_PAGES, MIN_ONLINE);
	}
	
	public static int scanAndJoinStorms(List<Integer> roomIds, long scanInterval) {
		return StormLottery.scanStorms(roomIds, scanInterval);
	}
	
	/**
	 * 节奏风暴抽奖
	 * @param roomId
	 * @return
	 */
	public static boolean toStormLottery(int roomId, String raffleId) {
		boolean isOk = true;
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			isOk &= StormLottery.toStormLottery(
					cookie.toNVCookie(), cookie.CSRF(), roomId, raffleId);
		}
		return isOk;
	}
	
	public static String toTvLottery(int roomId, String raffleId) {
		String errDesc = "";	// FIXME
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			errDesc = TvLottery.toTvLottery(cookie.toNVCookie(), roomId, raffleId);
		}
		return errDesc;
	}
	
	public static int toEgLottery(int roomId) {
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			EnergyLottery.toEgLottery(cookie.toNVCookie(), roomId);
		}
		return 0; // FIXME
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
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @return
	 */
	public static boolean sendChat(String msg) {
		return Chat.sendChat(msg);
	}
	
	/**
	 * 
	 * @param msg
	 * @param color
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color) {
		return Chat.sendChat(msg, color);
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param roomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, int roomId) {
		return Chat.sendChat(msg, roomId);
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @param roomId 目标直播间
	 * @return
	 */
	public static boolean sendChat(String msg, ChatColor color, int roomId) {
		return Chat.sendChat(msg, color, roomId);
	}
	
	public static boolean sendChat(String msg, ChatColor color, 
			int roomId, HttpCookie cookie) {
		return Chat.sendChat(msg, color, roomId, cookie.toNVCookie());
	}
	
	/**
	 * 发送私信
	 * @param sendId 发送账号的用户ID
	 * @param recvId 接收账号的用户ID
	 * @param msg 发送消息
	 * @return
	 */
	public static boolean sendPrivateMsg(String sendId, String recvId, String msg) {
		return PrivateMsg.sendPrivateMsg(sendId, recvId, msg);
	}
}
