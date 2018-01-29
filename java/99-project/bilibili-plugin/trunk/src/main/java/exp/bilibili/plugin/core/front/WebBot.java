package exp.bilibili.plugin.core.front;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.LotteryRoom;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * Web行为模拟器（仿真机器人）
 * 
 * 	主要功能:
 *   1.全平台礼物抽奖管理器（小电视/高能礼物）
 *   2.打印版权信息
 *   3.浏览器保活
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class WebBot extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebBot.class);
	
	private final static long DAY_UNIT = 86400000L;
	
	private final static long HOUR_UNIT = 3600000L;
	
	private final static int HOUR_OFFSET = 8;
	
	/** 轮询间隔 */
	private final static long SLEEP_TIME = 1000;
	
	/** 心跳间隔 */
	private final static long LOOP_TIME = 60000;
	
	/** 打印心跳信息周期 */
	private final static int LOOP_LIMIT = (int) (LOOP_TIME / SLEEP_TIME);
	
	/** 签到间隔 */
	private final static long SIGN_TIME = 300000;
	
	/** 签到行为周期 */
	private final static int SIGN_LIMIT = (int) (SIGN_TIME / SLEEP_TIME);
	
	/** 累计60次空闲, 则打印版本信息提示 */
	private final static int TIP_LIMIT = 60;
	
	/** 行为轮询次数 */
	private int loopCnt;
	
	/** 提示累计次数 */
	private int tipCnt;
	
	private int signCnt;
	
	/** 执行下次日常任务的时间点 */
	private long nextTaskTime;
	
	/** 上次重置每日任务的时间点 */
	private long resetTaskTime;
	
	private static volatile WebBot instance;
	
	private WebBot() {
		super("Web行为模拟器");
		this.loopCnt = 0;
		this.tipCnt = 0;
		this.signCnt = 0;
		this.nextTaskTime = System.currentTimeMillis();
		this.resetTaskTime = System.currentTimeMillis();
		
		// 把上次任务重置时间设为为当天0点
		resetTaskTime = resetTaskTime / DAY_UNIT * DAY_UNIT;
		resetTaskTime -= HOUR_UNIT * HOUR_OFFSET;
		resetTaskTime += 300000;	// 避免临界点时差, 后延5分钟
	}
	
	protected static WebBot getInstn() {
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
		log.info("{} 已停止", getName());
	}
	
	/**
	 * 模拟web行为
	 */
	private void toDo() {
		
		// 优先参与直播间抽奖
		LotteryRoom room = RoomMgr.getInstn().getGiftRoom();
		if(room != null) {
			toLottery(room);
			
		// 无抽奖操作则做其他事情
		} else {
			resetDailyTasks();	// 满足某个时间点则重置每日任务
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
		
		// 后端抽奖过快， 需要限制， 不然连续抽奖时会取不到礼物编号
		_sleep(SLEEP_TIME);
	}
	
	/**
	 * 每小时自动重置每日任务(目的是针对可能新添加的小号)
	 */
	private void resetDailyTasks() {
		long now = System.currentTimeMillis();
		if(nextTaskTime > 0 || (now - resetTaskTime <= HOUR_UNIT)) {
			return;
		}
		
		resetTaskTime = now;
		nextTaskTime = now;
	}
	
	/**
	 * 执行每日任务
	 */
	private void doDailyTasks() {
		toSign();		// 签到(每日+友爱社)
		doMathTasks();	// 日常小学数学任务
	}
	
	private void toSign() {
		if(signCnt++ > SIGN_LIMIT) {
			signCnt = 0;
			MsgSender.toSign();
			MsgSender.toAssn();
		}
	}
	
	/**
	 * 执行日常小学数学任务
	 */
	private void doMathTasks() {
		if(nextTaskTime > 0 && nextTaskTime <= System.currentTimeMillis()) {
			nextTaskTime = MsgSender.doMathTasks();
		}
	}
	
	/**
	 * 打印心跳消息
	 */
	private void toHeartbeat() {
		if(loopCnt++ >= LOOP_LIMIT) {
			tipCnt++;
			loopCnt = 0;
			log.info("{} 活动中...", getName());
		}
		
		if(tipCnt >= TIP_LIMIT) {
			tipCnt = 0;
			UIUtils.printVersionInfo();
		}
	}
	
}
