package exp.bilibili.plugin.core.front;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.LotteryRoom;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.other.StrUtils;
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
	
	/** 单次浏览器行为的轮询间隔 */
	private final static long SLEEP_TIME = 1000;
	
	/** 浏览器非活动时的保持时间 */
	private final static long KEEP_TIME = 60000;
	
	/** 累计的行为周期(达到周期则关闭浏览器, 避免内存占用过大) */
	private final static int LOOP_LIMIT = (int) (KEEP_TIME / SLEEP_TIME);
	
	/** 测试有爱社签到间隔 */
	private final static long ASSN_TIME = 60000;
	
	/** 友爱社签到行为周期 */
	private final static int ASSN_LIMIT = (int) (ASSN_TIME / SLEEP_TIME);
	
	/** 累计60次空闲, 则打印版本信息提示 */
	private final static int TIP_LIMIT = 60;
	
	/** 行为轮询次数 */
	private int loopCnt;
	
	/** 提示累计次数 */
	private int tipCnt;
	
	/** 是否需要签到友爱社 */
	private boolean signAssn;
	
	private int assnCnt;
	
	/** 执行下次日常任务的时间点 */
	private long nextTaskTime;
	
	/** 上次重置每日任务的时间点 */
	private long resetTaskTime;
	
	private static volatile WebBot instance;
	
	private WebBot() {
		super("Web行为模拟器");
		this.loopCnt = 0;
		this.tipCnt = 0;
		this.assnCnt = 0;
		this.signAssn = true;
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
		MsgSender.toSign();	// 自动签到
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
			toSignAssn();	// 友爱社签到
			doMathTasks();	// 日常小学数学任务
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
			String errDesc = MsgSender.toTvLottery(roomId, raffleId);
			if(StrUtils.isEmpty(errDesc)) {
				log.info("参与直播间 [{}] 抽奖成功", roomId);
				UIUtils.statistics("成功(小电视): 抽奖直播间 [", roomId, "]");
				UIUtils.updateLotteryCnt();
				
			} else {
				log.info("参与直播间 [{}] 抽奖失败: {}", roomId, errDesc);
				UIUtils.statistics("失败(", errDesc, "): 抽奖直播间 [", roomId, "]");
			}
			
		// 节奏风暴抽奖
		} else if(room.TYPE() == LotteryType.STORM) {
			MsgSender.toStormLottery(roomId, raffleId);
			
		// 高能抽奖
		} else {
			int cnt = MsgSender.toEgLottery(roomId);
			if(cnt > 0) {
				log.info("参与直播间 [{}] 抽奖成功(连击x{})", roomId, cnt);
				UIUtils.statistics("成功(连击x", cnt, "): 抽奖直播间 [", roomId, "]");
				UIUtils.updateLotteryCnt(cnt);
				
			} else {
				log.info("请勿重复操作: 抽奖直播间 [{}]", roomId);
			}
		}
		
		// 后端抽奖过快， 需要限制， 不然连续抽奖时会取不到礼物编号
		_sleep(SLEEP_TIME);
	}
	
	/**
	 * 友爱社日常签到
	 */
	private void toSignAssn() {
		if(signAssn == false || (assnCnt++ <= ASSN_LIMIT)) {
			return;
		}
		assnCnt = 0;
		
		boolean isGoOn = MsgSender.toAssn();
		if(isGoOn == false) {
			signAssn = false;
			UIUtils.log("今天已在友爱社签到");
		}
	}
	
	/**
	 * 执行日常小学数学任务
	 */
	private void doMathTasks() {
		resetDailyTasks();
		if(nextTaskTime <= 0 || nextTaskTime > System.currentTimeMillis()) {
			return;
		}
		
		nextTaskTime = MsgSender.doMathTasks();
		if(nextTaskTime <= 0) {
			UIUtils.log("今天所有小学数学任务已完成");
		}
	}
	
	/**
	 * 当跨天后，自动重置每日任务
	 */
	private void resetDailyTasks() {
		long now = System.currentTimeMillis();
		if(nextTaskTime > 0 || (now - resetTaskTime <= DAY_UNIT)) {
			return;
		}
		
		long hms = now % DAY_UNIT;	// 取时分秒
		long hour = hms / HOUR_UNIT;	// 取小时
		hour = (hour + HOUR_OFFSET) % 24;		// 校正时差
		
		// 凌晨时重置每日任务时间
		if(hour == 0) {
			resetTaskTime = now;
			nextTaskTime = now;
			MsgSender.toSign();	// 重新每日签到
			signAssn = true;	// 标记友爱社可以签到
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
