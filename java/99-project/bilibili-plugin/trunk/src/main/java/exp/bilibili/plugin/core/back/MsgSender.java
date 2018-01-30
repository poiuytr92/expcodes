package exp.bilibili.plugin.core.back;

import java.util.List;

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
	
	public static String getQrcodeInfo() {
		return Login.getQrcodeInfo();
	}
	
	public static HttpCookie toLogin(String oauthKey) {
		return Login.toLogin(oauthKey);
	}
	
	/**
	 * 下载登陆用的验证码图片
	 * @param imgPath 图片保存路径
	 * @return 与该验证码配套的cookies
	 */
	public static String downloadVccode(String imgPath) {
		return Login.downloadVccode(imgPath);
	}
	
	public static HttpCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		return Login.toLogin(username, password, vccode, vcCookies);
	}
	
	public static boolean queryUserInfo(HttpCookie cookie) {
		return Login.queryUserInfo(cookie);
	}

	public static long toAssn(HttpCookie cookie) {
		return DailyTasks.toAssn(cookie);
	}
	
	public static long toSign(HttpCookie cookie) {
		return DailyTasks.toSign(cookie);
	}
	
	public static long doMathTask(HttpCookie cookie) {
		return DailyTasks.doMathTask(cookie);
	}
	
	public static List<Integer> queryTopLiveRoomIds(
			final int MAX_PAGES, final int MIN_ONLINE) {
		return Other.queryHotLiveRoomIds(MAX_PAGES, MIN_ONLINE);
	}
	
	public static void scanAndJoinStorms(List<Integer> roomIds) {
		LotteryStorm.toDo(roomIds);
	}
	
	/**
	 * 节奏风暴抽奖
	 * @param roomId
	 * @return
	 */
	public static void toStormLottery(int roomId, String raffleId) {
		LotteryStorm.toDo(roomId, raffleId);
	}
	
	public static void toTvLottery(int roomId, String raffleId) {
		LotteryTV.toDo(roomId, raffleId);
	}
	
	public static void toEgLottery(int roomId) {
		LotteryEnergy.toDo(roomId);
	}
	
	public static String queryRedbagPool() {
		HttpCookie cookie = CookiesMgr.INSTN().MAIN();	// FIXME 仅主号
		return Redbag.queryRedbagPool(cookie);
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
		return Redbag.exchangeRedbag(cookie, id, num);
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
