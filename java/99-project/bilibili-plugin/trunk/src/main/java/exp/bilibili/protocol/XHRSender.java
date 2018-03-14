package exp.bilibili.protocol;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.ChatColor;
import exp.bilibili.plugin.envm.Gift;
import exp.bilibili.plugin.utils.TimeUtils;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.bean.other.User;
import exp.bilibili.protocol.bean.xhr.Achieve;
import exp.bilibili.protocol.bean.xhr.BagGift;
import exp.bilibili.protocol.bean.xhr.Medal;
import exp.bilibili.protocol.xhr.Chat;
import exp.bilibili.protocol.xhr.DailyTasks;
import exp.bilibili.protocol.xhr.Gifts;
import exp.bilibili.protocol.xhr.Login;
import exp.bilibili.protocol.xhr.LotteryEnergy;
import exp.bilibili.protocol.xhr.LotteryStorm;
import exp.bilibili.protocol.xhr.LotteryTV;
import exp.bilibili.protocol.xhr.Other;
import exp.bilibili.protocol.xhr.Redbag;
import exp.bilibili.protocol.xhr.WatchLive;

/**
 * <PRE>
 * XHR请求发送器
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XHRSender {

	/**
	 * 获取管理员在B站link中心针对本插件的授权校验标签
	 * @return
	 */
	public static String queryCertTags() {
		return Other.queryCertificateTags();
	}
	
	/**
	 * 查询直播间的房管(含主播)
	 * @param roomId 直播间ID
	 * @return 房管列表
	 */
	public static Set<User> queryManagers() {
		int roomId = UIUtils.getLiveRoomId();
		return Other.queryManagers(roomId);
	}
	
	/**
	 * 临时把用户关小黑屋1小时
	 * @param username
	 * @return
	 */
	public static boolean blockUser(String username) {
		BiliCookie cookie = CookiesMgr.MAIN();
		int roomId = UIUtils.getLiveRoomId();
		final int hour = 1;
		return Other.blockUser(cookie, roomId, username, hour);
	}
	
	/**
	 * 获取二维码登陆信息(用于在本地生成二维码图片)
	 * @return
	 */
	public static String getQrcodeInfo() {
		return Login.getQrcodeInfo();
	}
	
	/**
	 * 检测二维码是否扫码登陆成功
	 * @param oauthKey 二维码登陆信息中提取的oauthKey
	 * @return 若扫码登陆成功, 则返回有效Cookie
	 */
	public static BiliCookie toLogin(String oauthKey) {
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
	
	/**
	 * 通过帐密+验证码方式登陆
	 * @param username 账号
	 * @param password 密码
	 * @param vccode 验证码
	 * @param vcCookies 与验证码配套的登陆用cookie
	 * @return 
	 */
	public static BiliCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		return Login.toLogin(username, password, vccode, vcCookies);
	}
	
	/**
	 * 查询账号信息(并写入cookie内)
	 * @param cookie
	 * @return username
	 */
	public static boolean queryUserInfo(BiliCookie cookie) {
		boolean isOk = Other.queryUserInfo(cookie);	// 普通信息: 用户ID+昵称
		isOk &= Other.queryUserSafeInfo(cookie);	// 安全信息: 是否绑定手机号
		return isOk;
	}
	
	/**
	 * 查询账号在当前直播间的授权信息(并写入cookie内)
	 * @param cookie
	 * @return
	 */
	public static boolean queryUserAuthorityInfo(BiliCookie cookie) {
		int roomId = UIUtils.getLiveRoomId();
		return Other.queryUserAuthorityInfo(cookie, roomId);
	}

	/**
	 * 每日签到
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long toSign(BiliCookie cookie) {
		return DailyTasks.toSign(cookie);
	}
	
	/**
	 * 友爱社签到
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long toAssn(BiliCookie cookie) {
		long nextTaskTime = DailyTasks.toAssn(cookie);
		
		// 若有爱社签到失败, 则模拟双端观看直播
		if(nextTaskTime > 0) {
			int roomId = UIUtils.getLiveRoomId();
			WatchLive.toWatchPCLive(cookie, roomId);	// PC端
//			WatchLive.toWatchAppLive(cookie, roomId);	// 手机端 (FIXME: 暂时无效)
		}
		return nextTaskTime;
	}
	
	/**
	 * 领取日常/周常的勋章/友爱社礼物
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long receiveDailyGift(BiliCookie cookie) {
		return DailyTasks.receiveDailyGift(cookie);
	}
	
	/**
	 * 领取活动心跳礼物（每在线10分钟领取一个xxx）
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long receiveHBGift(BiliCookie cookie) {
		return DailyTasks.receiveHeartbeatGift(cookie);
	}
	
	/**
	 * 执行小学数学日常任务
	 * @param cookie
	 * @return 返回执行下次任务的时间点(<=0表示已完成该任务)
	 */
	public static long doMathTask(BiliCookie cookie) {
		return DailyTasks.doMathTask(cookie);
	}
	
	/**
	 * 扫描当前的人气直播间房号列表
	 * @param cookie 扫描用的cookie
	 * @return
	 */
	public static List<Integer> queryTopLiveRoomIds() {
		BiliCookie cookie = CookiesMgr.VEST();
		return LotteryStorm.queryHotLiveRoomIds(cookie);
	}
	
	/**
	 * 扫描并加入节奏风暴
	 * @param hotRoomIds 热门房间列表
	 * @param scanInterval 扫描房间间隔
	 */
	public static void scanAndJoinStorms(List<Integer> hotRoomIds, long scanInterval) {
		LotteryStorm.toLottery(hotRoomIds, scanInterval);
	}
	
	/**
	 * 节奏风暴抽奖
	 * @param roomId
	 * @return
	 */
	public static void toStormLottery(int roomId, String raffleId) {
		LotteryStorm.toLottery(roomId, raffleId);
	}
	
	/**
	 * 小电视抽奖
	 * @param roomId
	 * @param raffleId
	 * @return
	 */
	public static void toTvLottery(int roomId, String raffleId) {
		LotteryTV.toLottery(roomId, raffleId);
	}
	
	/**
	 * 高能礼物抽奖
	 * @param roomId
	 * @return
	 */
	public static void toEgLottery(int roomId) {
		LotteryEnergy.toLottery(roomId);
	}
	
	/**
	 * 投喂主播
	 * @param cookie 投喂用户cookie
	 * @param roomId 房间号
	 */
	public static void toFeed(BiliCookie cookie, int roomId) {
		
		// 查询持有的所有礼物（包括银瓜子可以兑换的辣条数）
		List<BagGift> allGifts = Gifts.queryBagList(cookie, roomId);
		int silver = Gifts.querySilver(cookie);
		int giftNum = silver / Gift.HOT_STRIP.COST();
		if(giftNum > 0) {	// 银瓜子转换为虚拟的永久辣条
			BagGift bagGift = new BagGift(
					Gift.HOT_STRIP.ID(), Gift.HOT_STRIP.NAME(), giftNum);
			allGifts.add(bagGift);
		}
		
		// 查询用户当前持有的勋章
		Map<Integer, Medal> medals = Gifts.queryMedals(cookie);
		Medal medal = medals.get(RoomMgr.getInstn().getRealRoomId(roomId));
		
		// 筛选可以投喂的礼物列表
		List<BagGift> feedGifts = filterGifts(cookie, allGifts, medal);
		
		// 投喂主播
		User up = Other.queryUpInfo(roomId);
		Gifts.feed(cookie, roomId, up.ID(), feedGifts);
	}
	
	/**
	 * 过滤可投喂的礼物
	 * @param cookie 
	 * @param allGifts 当前持有的所有礼物
	 * @param medal 当前房间的勋章
	 * @return 可投喂的礼物列表
	 */
	private static List<BagGift> filterGifts(BiliCookie cookie, 
			List<BagGift> allGifts, Medal medal) {
		List<BagGift> feedGifts = new LinkedList<BagGift>();
		final long TOMORROW = TimeUtils.getZeroPointMillis() + TimeUtils.DAY_UNIT; // 今天24点之前
		
		// 对于已绑定手机或实名的账号，移除受保护礼物（即不投喂）
		if(cookie.isRealName() || 
				(cookie.isBindTel() && Config.getInstn().PROTECT_FEED())) {
			Iterator<BagGift> giftIts = allGifts.iterator();
			while(giftIts.hasNext()) {
				BagGift gift = giftIts.next();
				if(gift.getExpire() <= 0) {
					giftIts.remove(); 	// 永久礼物
					
				} else if(gift.getIntimacy() <= 0) {
					giftIts.remove(); 	// 亲密度<=0 的礼物(可能是某些活动礼物)
					
				} else if(Gift.B_CLOD.ID().equals(gift.getGiftId()) && 
						gift.getExpire() > TOMORROW) {
					giftIts.remove();	// 未过期的B坷垃
				}
			}
		}
		
		// 用户没有持有当前投喂的房间的勋章
		if(medal == null) {
			
			// 检查是否持有B坷垃
			BagGift bClod = null;
			for(BagGift gift : allGifts) {
				if(Gift.B_CLOD.ID().equals(gift.getGiftId())) {
					bClod = gift;
					bClod.setGiftNum(1);
					break;
					
				} else {
					feedGifts.add(gift);	// 当没有B坷垃时, 默认所有礼物均可投喂
				}
			}
			
			// 若持有B坷垃，则先只投喂1个B坷垃(下一轮自动投喂再根据亲密度选择礼物)
			if(bClod != null) {
				feedGifts.clear();
				feedGifts.add(bClod);
			}
			
		// 用户持有当前投喂的房间的勋章, 则需筛选礼物
		} else {
			int todayIntimacy = medal.getDayLimit() - medal.getTodayFeed();	// 今天可用亲密度
			for(BagGift gift : allGifts) {
				
				// 今天内到期的礼物, 全部选择(无视亲密度和实名)
				if(gift.getExpire() > 0 && gift.getExpire() <= TOMORROW) {
					feedGifts.add(gift);
					todayIntimacy -= gift.getIntimacy() * gift.getGiftNum();
					
				// 在不溢出亲密度的前提下选择礼物
				} else {
					int num = todayIntimacy / gift.getIntimacy();
					num = (num > gift.getGiftNum() ? gift.getGiftNum() : num);
					if(num > 0) {
						gift.setGiftNum(num);
						feedGifts.add(gift);
						todayIntimacy -= gift.getIntimacy() * num;
					}
				}
			}
		}
		return feedGifts;
	}
	
	/**
	 * 扭蛋
	 * @param cookie
	 */
	public static void toCapsule(BiliCookie cookie) {
		final int MAX_COIN = 100; // 每次打开扭蛋上限
		int coin = Gifts.queryCapsuleCoin(cookie);
		
		// 满100个扭蛋币才执行, 可提高奖品质量
		while(coin >= MAX_COIN) {
			boolean isOk = Gifts.openCapsuleCoin(cookie, MAX_COIN);
			if(isOk == false) {
				break;
			}
			coin -= MAX_COIN;
		}
	}
	
	/**
	 * 领取已完成的成就奖励
	 * @param cookie
	 */
	public static void toAchieve(BiliCookie cookie) {
		List<Achieve> achieves = Other.queryAchieve(cookie);
		Other.doAchieve(cookie, achieves);
	}
	
	/**
	 * 发送弹幕消息到当前监听的直播间
	 * @param msg 弹幕消息
	 * @return
	 */
	public static boolean sendDanmu(String msg) {
		ChatColor color = ChatColor.RANDOM();
		return sendDanmu(msg, color);
	}
	
	/**
	 * 发送弹幕消息到当前监听的直播间
	 * @param msg 弹幕消息
	 * @param color 弹幕颜色
	 * @return
	 */
	public static boolean sendDanmu(String msg, ChatColor color) {
		BiliCookie cookie = CookiesMgr.MAIN();
		int roomId = UIUtils.getLiveRoomId();
		return Chat.sendDanmu(cookie, roomId, msg, color);
	}
	
	/**
	 * 发送弹幕消息
	 * @param msg 弹幕消息
	 * @param roomId 接收弹幕的直播间
	 * @return
	 */
	public static boolean sendDanmu(String msg, int roomId) {
		BiliCookie cookie = CookiesMgr.MAIN();
		ChatColor color = ChatColor.RANDOM();
		return Chat.sendDanmu(cookie, roomId, msg, color);
	}
	
	/**
	 * 发送私信
	 * @param recvId 接收账号的用户ID
	 * @param msg 私信消息
	 * @return
	 */
	public static boolean sendPM(String recvId, String msg) {
		BiliCookie cookie = CookiesMgr.MAIN();
		return Chat.sendPM(cookie, recvId, msg);
	}
	
	public static long toBucket(BiliCookie cookie) {
		int roomId = Redbag.queryBucketRoomId(cookie);
		return Redbag.exchangeBucket(cookie, roomId);
	}
	
	/**
	 * 2018春节活动：查询当前红包奖池
	 * @return {"code":0,"msg":"success","message":"success","data":{"red_bag_num":2290,"round":70,"pool_list":[{"award_id":"guard-3","award_name":"舰长体验券（1个月）","stock_num":0,"exchange_limit":5,"user_exchange_count":5,"price":6699},{"award_id":"gift-113","award_name":"新春抽奖","stock_num":2,"exchange_limit":0,"user_exchange_count":0,"price":23333},{"award_id":"danmu-gold","award_name":"金色弹幕特权（1天）","stock_num":19,"exchange_limit":42,"user_exchange_count":42,"price":2233},{"award_id":"uname-gold","award_name":"金色昵称特权（1天）","stock_num":20,"exchange_limit":42,"user_exchange_count":42,"price":8888},{"award_id":"stuff-2","award_name":"经验曜石","stock_num":0,"exchange_limit":10,"user_exchange_count":10,"price":233},{"award_id":"title-89","award_name":"爆竹头衔","stock_num":0,"exchange_limit":10,"user_exchange_count":10,"price":888},{"award_id":"gift-3","award_name":"B坷垃","stock_num":0,"exchange_limit":1,"user_exchange_count":1,"price":450},{"award_id":"gift-109","award_name":"红灯笼","stock_num":0,"exchange_limit":500,"user_exchange_count":500,"price":15}],"pool":{"award_id":"award-pool","award_name":"刷新兑换池","stock_num":99999,"exchange_limit":0,"price":6666}}}
	 */
	public static String queryRedbagPool(BiliCookie cookie) {
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
	public static String exchangeRedbag(BiliCookie cookie, String id, int num) {
		return Redbag.exchangeRedbag(cookie, id, num);
	}
	
}
