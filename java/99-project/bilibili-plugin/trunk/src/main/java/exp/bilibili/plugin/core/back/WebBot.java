package exp.bilibili.plugin.core.back;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.LotteryRoom;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.xhr.DailyTasks;
import exp.libs.utils.num.NumUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * Web行为模拟器（仿真机器人）
 * 
 * 	主要功能:
 *   1.全平台礼物抽奖管理器（小电视/高能礼物）
 *   2.打印版权信息
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebBot extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebBot.class);
	
	private final static long DAY_UNIT = 86400000L;
	
	private final static long HOUR_UNIT = 3600000L;
	
	private final static int HOUR_OFFSET = 8;
	
	/** 轮询间隔 */
	private final static long SLEEP_TIME = 1000;
	
	/** 心跳间隔 */
	private final static long HB_TIME = 3600000;
	
	/** 打印心跳信息周期 */
	private final static int HB_LIMIT = (int) (HB_TIME / SLEEP_TIME);
	
	/** 轮询次数 */
	private int loopCnt;
	
	/** 已完成当天任务的cookies */
	private Set<HttpCookie> finCookies;
	
	/** 最近一次添加过cookie的时间点 */
	private long lastAddCookieTime;
	
	/** 执行下次日常任务的时间点 */
	private long nextTaskTime;
	
	/** 上次重置每日任务的时间点 */
	private long resetTaskTime;
	
	/** 单例 */
	private static volatile WebBot instance;
	
	/**
	 * 构造函数
	 */
	private WebBot() {
		super("Web行为模拟器");
		this.loopCnt = 0;
		this.finCookies = new HashSet<HttpCookie>();
		this.lastAddCookieTime = System.currentTimeMillis();
		this.nextTaskTime = System.currentTimeMillis();
		initResetTaskTime();
	}
	
	/**
	 * 把上次任务重置时间初始化为当天0点
	 */
	private void initResetTaskTime() {
		resetTaskTime = System.currentTimeMillis() / DAY_UNIT * DAY_UNIT;
		resetTaskTime -= HOUR_UNIT * HOUR_OFFSET;
		resetTaskTime += 300000;	// 避免临界点时差, 后延5分钟
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
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		try {
			toDo();
		} catch(Exception e) {
			log.error("模拟Web行为异常", e);
		}
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		finCookies.clear();
		log.info("{} 已停止", getName());
	}
	
	private void toDo() {
		
		// 优先参与直播间抽奖
		LotteryRoom room = RoomMgr.getInstn().getGiftRoom();
		if(room != null) {
			toLottery(room);
			
		// 无抽奖操作则做其他事情
		} else {
			resetDailyTasks();	// 满足某个条件则重置每日任务
			doDailyTasks();		// 执行每日任务
			
			toHeartbeat();	// 心跳
		}
	}
	
	/**
	 * 通过后端注入服务器参与抽奖
	 * @param room
	 */
	private void toLottery(LotteryRoom room) {
		final int roomId = room.getRoomId();
		final String raffleId = room.getRaffleId();
		
		// 小电视抽奖
		if(room.TYPE() == LotteryType.TV) {
			MsgSender.toTvLottery(roomId, raffleId);
			
		// 节奏风暴抽奖
		} else if(room.TYPE() == LotteryType.STORM) {
			MsgSender.toStormLottery(roomId, raffleId);
			
		// 高能抽奖
		} else {
			MsgSender.toEgLottery(roomId);
		}
	}
	
	/**
	 * 当cookies发生变化时, 重置每日任务
	 */
	private void resetDailyTasks() {
		if(nextTaskTime > 0){
			return;	// 当天任务还在执行中, 无需重置任务时间点
		}
		
		// 当cookie发生变化时, 重置任务时间
		if(lastAddCookieTime != CookiesMgr.INSTN().getLastAddCookieTime()) {
			lastAddCookieTime = CookiesMgr.INSTN().getLastAddCookieTime();
			nextTaskTime = System.currentTimeMillis();
			
		// 当跨天时, 重置任务时间
		} else {
			long now = System.currentTimeMillis();
			if(now - resetTaskTime > DAY_UNIT) {
				resetTaskTime = now;
				nextTaskTime = now;
				finCookies.clear();	// 跨天后, 已完成任务的cookie标记也需要重置
			}
		}
	}
	
	/**
	 * 执行每日任务
	 */
	private void doDailyTasks() {
		if(nextTaskTime > 0 && nextTaskTime <= System.currentTimeMillis()) {
			
			long max = -1;
			Iterator<HttpCookie> cookies = CookiesMgr.INSTN().ALL();
			while(cookies.hasNext()) {
				HttpCookie cookie = cookies.next();
				if(finCookies.contains(cookie)) {
					continue;
				}
				
				max = NumUtils.max(DailyTasks.toSign(cookie), max);
				max = NumUtils.max(DailyTasks.toAssn(cookie), max);
				max = NumUtils.max(DailyTasks.doMathTask(cookie), max);
				
				if(max <= 0) {
					finCookies.add(cookie);
				}
			}
			nextTaskTime = max;
		}
	}
	
	/**
	 * 打印心跳消息
	 */
	private void toHeartbeat() {
		if(loopCnt++ >= HB_LIMIT) {
			loopCnt = 0;
			log.info("{} 活动中...", getName());
			UIUtils.printVersionInfo();
		}
	}
	
}
