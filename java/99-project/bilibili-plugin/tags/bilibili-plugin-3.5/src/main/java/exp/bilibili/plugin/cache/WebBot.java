package exp.bilibili.plugin.cache;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.TimeUtils;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.XHRSender;
import exp.bilibili.protocol.bean.other.LotteryRoom;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * Web行为模拟器（仿真机器人）
 * 
 * 	主要功能:
 *   1.全平台礼物抽奖管理器（小电视/高能礼物/节奏风暴）
 *   2.日常任务(签到/友爱社/小学数学)
 *   3.自动扭蛋、投喂
 *   4.自动领取成就奖励
 *   5.检查cookie有效期
 *   6.打印版权信息
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebBot extends LoopThread {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(WebBot.class);
	
	/** 单位时间：天 */
	private final static long DAY_UNIT = 86400000L;
	
	/** 单位时间：小�? */
	private final static long HOUR_UNIT = 3600000L;
	
	/** 北京时间时差 */
	private final static int HOUR_OFFSET = 8;
	
	/** 延迟时间 */
	private final static long DELAY_TIME = 120000L;
	
	/** 轮询间隔 */
	private final static long LOOP_TIME = 1000L;
	
	/** 定时触发事件的间�? */
	private final static long EVENT_TIME = 3600000L;
	
	/** 定时触发事件的周�? */
	private final static int EVENT_LIMIT = (int) (EVENT_TIME / LOOP_TIME);
	
	/** 轮询次数 */
	private int loopCnt;
	
	/** 已完成当天任务的cookies */
	private Set<BiliCookie> finCookies;
	
	/** 最近一次添加过cookie的时间点 */
	private long lastAddCookieTime;
	
	/** 执行下次日常任务的时间点 */
	private long nextTaskTime;
	
	/** 上次重置每日任务的时间点 */
	private long resetTaskTime;
	
	/** 单例 */
	private static volatile WebBot instance;
	
	/**
	 * 构造函�?
	 */
	private WebBot() {
		super("Web行为模拟�?");
		this.loopCnt = 0;
		this.finCookies = new HashSet<BiliCookie>();
		this.lastAddCookieTime = System.currentTimeMillis();
		this.nextTaskTime = System.currentTimeMillis() + DELAY_TIME;	// 首次打开软件�?, 延迟一点时间再执行任务
		initResetTaskTime();
	}
	
	/**
	 * 把上次任务重置时间初始化为当�?0�?
	 */
	private void initResetTaskTime() {
		resetTaskTime = System.currentTimeMillis() / DAY_UNIT * DAY_UNIT;
		resetTaskTime -= HOUR_UNIT * HOUR_OFFSET;
		resetTaskTime += DELAY_TIME;	// 避免临界点时�?, 后延一点时�?
	}
	
	/**
	 * 获取单例
	 * @return
	 */
	public static WebBot getInstn() {
		if(instance == null) {
			synchronized (WebBot.class) {
				if(instance == null) {
					instance = new WebBot();
				}
			}
		}
		return instance;
	}

	@Override
	protected void _before() {
		log.info("{} 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		try {
			toDo();
		} catch(Exception e) {
			log.error("模拟Web行为异常", e);
		}
		_sleep(LOOP_TIME);
	}

	@Override
	protected void _after() {
		finCookies.clear();
		log.info("{} 已停�?", getName());
	}
	
	private void toDo() {
		
		// 优先参与直播间抽�?
		LotteryRoom room = RoomMgr.getInstn().getGiftRoom();
		if(room != null) {
			toLottery(room);
			
		// 无抽奖操作则做其他事�?
		} else {
			doDailyTasks();	// 执行每日任务
			doEvent();		// 定时触发事件
		}
	}
	
	/**
	 * 通过后端注入服务器参与抽�?
	 * @param room
	 */
	private void toLottery(LotteryRoom room) {
		final int roomId = room.getRoomId();
		final String raffleId = room.getRaffleId();
		
		// 小电视抽�?
		if(room.TYPE() == LotteryType.TV) {
			XHRSender.toTvLottery(roomId, raffleId);
			
		// 节奏风暴抽奖
		} else if(room.TYPE() == LotteryType.STORM) {
			XHRSender.toStormLottery(roomId, raffleId);
			
		// 高能抽奖
		} else {
			XHRSender.toEgLottery(roomId);
		}
	}
	
	/**
	 * 执行每日任务
	 */
	private void doDailyTasks() {
		resetDailyTasks();	// 满足某个条件则重置每日任�?
		
		if(nextTaskTime > 0 && nextTaskTime <= System.currentTimeMillis()) {
			Set<BiliCookie> cookies = CookiesMgr.ALL();
			for(BiliCookie cookie : cookies) {
				if(finCookies.contains(cookie)) {
					continue;
				}
				
				long max = -1;
				max = NumUtils.max(XHRSender.toSign(cookie), max);		// 每日签到
				if(cookie.isBindTel()) {	// 仅绑定了手机的账号才能参�?
					max = NumUtils.max(XHRSender.toAssn(cookie), max);		// 友爱�?
					max = NumUtils.max(XHRSender.doMathTask(cookie), max);	// 小学数学
				}
				nextTaskTime = NumUtils.max(nextTaskTime, max);
				
				if(max <= 0) {
					finCookies.add(cookie);
				}
			}
		}
	}
	
	/**
	 * 当cookies发生变化�?, 重置每日任务
	 */
	private void resetDailyTasks() {
		
		// 当跨天时, 重置任务时间, 且清空完成任务的cookie标记
		long now = System.currentTimeMillis();
		if(now - resetTaskTime > DAY_UNIT) {
			resetTaskTime = now;
			nextTaskTime = now;
			finCookies.clear();
			
		// 当cookie发生变化�?, 仅重置任务时�?
		} else if(nextTaskTime <= 0 && 
				lastAddCookieTime != CookiesMgr.getInstn().getLastAddCookieTime()) {
			lastAddCookieTime = CookiesMgr.getInstn().getLastAddCookieTime();
			nextTaskTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * 触发事件
	 */
	private void doEvent() {
		if(loopCnt++ >= EVENT_LIMIT) {
			loopCnt = 0;
			
			toCapsule();	// 自动扭蛋
			toAutoFeed();	// 自动投喂
			takeFinishAchieve();	// 领取成就奖励
			checkCookieExpires();	// 检查Cookie有效�?
			
			// 打印心跳
			log.info("{} 活动�?...", getName());
			UIUtils.printVersionInfo();
		}
	}
	
	/**
	 * 自动扭蛋机（仅小号）
	 */
	private void toCapsule() {
		if(UIUtils.isAutoFeed() == false) {
			return;
		}
		
		Set<BiliCookie> cookies = CookiesMgr.MINIs();
		for(BiliCookie cookie : cookies) {
			if(cookie.isAutoFeed()) {
				XHRSender.toCapsule(cookie);
			}
		}
	}
	
	/**
	 * 自动投喂（仅小号�?
	 */
	private void toAutoFeed() {
		if(UIUtils.isAutoFeed() == false) {
			return;	// 总开�?
		}
		
		int defaultRoomId = UIUtils.getFeedRoomId();
		Set<BiliCookie> cookies = CookiesMgr.MINIs();
		for(BiliCookie cookie : cookies) {
			if(cookie.isAutoFeed()) {
				int roomId = cookie.getFeedRoomId();
				roomId = RoomMgr.getInstn().isExist(roomId) ? roomId : defaultRoomId;
				XHRSender.toFeed(cookie, roomId);
			}
		}
	}
	
	/**
	 * 领取已完成的任务奖励
	 */
	private void takeFinishAchieve() {
		Set<BiliCookie> cookies = CookiesMgr.ALL();
		for(BiliCookie cookie : cookies) {
			XHRSender.toAchieve(cookie);
		}
	}
	
	/**
	 * 检查Cookie有效�?
	 */
	private void checkCookieExpires() {
		final long WARN_MILLIS = 48 * HOUR_UNIT;	// 有效期到期前48小时开始警�?
		final long now = System.currentTimeMillis();
		
		// 检查小号的登陆有效�?
		Set<BiliCookie> cookies = CookiesMgr.MINIs();
		for(BiliCookie cookie : cookies) {
			long expires = TimeUtils.toMillis(cookie.EXPIRES());
			long diff = expires - now;
			if(diff <= WARN_MILLIS) {
				if(diff > HOUR_UNIT) {
					UIUtils.log("小号 [", cookie.NICKNAME(), "] 剩余的登陆有效期: ", 
							(diff / HOUR_UNIT), "小时 (到期自动注销)");
				} else {
					CookiesMgr.getInstn().del(cookie);
					UIUtils.log("小号 [", cookie.NICKNAME(), "] 登陆已过�?: 请重新登�?");
				}
			}
		}
		
		// 检查主号和马甲号的登陆有效�?(取两者最小值作为共同有效期)
		long mainExpires = TimeUtils.toMillis(CookiesMgr.MAIN().EXPIRES());
		if(CookiesMgr.VEST() != BiliCookie.NULL) {
			long vestExpires = TimeUtils.toMillis(CookiesMgr.VEST().EXPIRES());
			mainExpires = (mainExpires < vestExpires ? mainExpires : vestExpires);
		}
		long diff = mainExpires - now;
		if(diff <= WARN_MILLIS) {
			if(diff > HOUR_UNIT) {
				UIUtils.log("主号 [", CookiesMgr.MAIN().NICKNAME(), "] 剩余的登陆有效期: ", 
						(diff / HOUR_UNIT), "小时 (到期自动注销并退出程�?)");
				
			} else {
				String msg = StrUtils.concat("主号 [", CookiesMgr.MAIN().NICKNAME(), "] 登陆已过�?: 重启后请重新登陆");
				CookiesMgr.getInstn().del(CookiesMgr.MAIN());
				CookiesMgr.getInstn().del(CookiesMgr.VEST());
				
				UIUtils.log(msg);
				UIUtils.notityExit(msg);
			}
		}
	}
	
}
