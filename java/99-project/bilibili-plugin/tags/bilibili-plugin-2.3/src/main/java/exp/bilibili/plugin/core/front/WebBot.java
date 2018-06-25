package exp.bilibili.plugin.core.front;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.LotteryRoom;
import exp.bilibili.plugin.cache.Browser;
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
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class WebBot extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebBot.class);
	
	private final static String LIVE_URL = Config.getInstn().LIVE_URL();
	
	private final static String HOME_URL = Config.getInstn().HOME_URL();
	
	private final static long DAY_UNIT = 86400000L;
	
	private final static long HOUR_UNIT = 3600000L;
	
	private final static int HOUR_OFFSET = 8;
	
	/** 单次浏览器行为的轮询间隔 */
	private final static long SLEEP_TIME = 1000;
	
	/** 浏览器非活动时的保持时间 */
	private final static long KEEP_TIME = 60000;
	
	/** 累计的行为周�?(达到周期则关闭浏览器, 避免内存占用过大) */
	private final static int LOOP_LIMIT = (int) (KEEP_TIME / SLEEP_TIME);
	
	/** 测试有爱社签到间�? */
	private final static long ASSN_TIME = 60000;
	
	/** 友爱社签到行为周�? */
	private final static int ASSN_LIMIT = (int) (ASSN_TIME / SLEEP_TIME);
	
	/** 浏览器打开后限制可以抽奖的次数(超过次数则关闭浏览器, 避免内存占用过大) */
	private final static int LOTTERY_LIMIT = Config.getInstn().CLEAR_CACHE_CYCLE();
	
	/** 累计60次空�?, 则打印版本信息提�? */
	private final static int TIP_LIMIT = 60;
	
	/** 行为轮询次数 */
	private int loopCnt;
	
	/** 抽奖累计次数 */
	private int lotteryCnt;
	
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
		super("Web行为模拟�?");
		this.loopCnt = 0;
		this.lotteryCnt = 0;
		this.tipCnt = 0;
		this.assnCnt = 0;
		this.signAssn = true;
		this.nextTaskTime = System.currentTimeMillis();
		this.resetTaskTime = System.currentTimeMillis();
		
		// 把上次任务重置时间设为为当天0�?
		resetTaskTime = resetTaskTime / DAY_UNIT * DAY_UNIT;
		resetTaskTime -= HOUR_UNIT * HOUR_OFFSET;
		resetTaskTime += 300000;	// 避免临界点时�?, 后延5分钟
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

	private void closeBrowser() {
		Browser.quit();
		lotteryCnt = 0;	// 关闭浏览器后则重置这个浏览器累计的抽奖次�?
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启�?", getName());
//		RoomMgr.getInstn().clearGiftRooms();	// 可以尝试对登录前的抽奖房间抽�?, 不一定要清空
		MsgSender.toSign();	// 自动签到
	}

	@Override
	protected void _loopRun() {
		try {
			toDo();
		} catch(Exception e) {
			log.error("模拟Web操作异常, 自动重启Web驱动", e);
			closeBrowser();
		}
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		log.info("{} 已停�?", getName());
	}
	
	/**
	 * 模拟web行为
	 */
	@SuppressWarnings("unused")
	private void toDo() {
		
		// 参与直播间抽�?
		LotteryRoom room = RoomMgr.getInstn().getGiftRoom();
		if(room != null) {
			
			// 后台注入式抽�?
			if(true) {
				toLottery(room);
				
			// 前端仿真式抽�?(效率问题已废�?, 仅留代码参�?)
			} else if(room.TYPE() != LotteryType.STORM) {	// 节奏风暴的抽奖位置不一�?
				toLottery(room.getRoomId());
			}
			
		// 长时间无抽奖操作则做其他事情
		} else {
			toSignAssn();	// 友爱社签�?
			doDailyTasks();	// 日常小学数学任务
			toSleep();		// 休眠
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
			String errDesc = MsgSender.toTvLottery(roomId, raffleId);
			if(StrUtils.isEmpty(errDesc)) {
				log.info("参与直播�? [{}] 抽奖成功", roomId);
				UIUtils.statistics("成功(小电�?): 抽奖直播�? [", roomId, "]");
				UIUtils.updateLotteryCnt();
				
			} else {
				log.info("参与直播�? [{}] 抽奖失败: {}", roomId, errDesc);
				UIUtils.statistics("失败(", errDesc, "): 抽奖直播�? [", roomId, "]");
			}
			
		// 节奏风暴抽奖
		} else if(room.TYPE() == LotteryType.STORM) {
			MsgSender.toStormLottery(roomId, raffleId);
			
		// 高能抽奖
		} else {
			int cnt = MsgSender.toEgLottery(roomId);
			if(cnt > 0) {
				log.info("参与直播�? [{}] 抽奖成功(连击x{})", roomId, cnt);
				UIUtils.statistics("成功(连击x", cnt, "): 抽奖直播�? [", roomId, "]");
				UIUtils.updateLotteryCnt(cnt);
				
			} else {
				log.info("请勿重复操作: 抽奖直播�? [{}]", roomId);
			}
		}
		
		// 后端抽奖过快�? 需要限制， 不然连续抽奖时会取不到礼物编�?
		_sleep(SLEEP_TIME);
	}
	
	/**
	 * 通过前端模拟浏览器行为参与抽�?
	 * @param roomId
	 */
	@Deprecated
	private void toLottery(int roomId) {
		String url = StrUtils.concat(LIVE_URL, roomId);
		Browser.open(url);	// 打开/重开直播�?(可屏蔽上一次抽奖结果提�?)
		_sleep(SLEEP_TIME);
		boolean isOk = _lottery(roomId);
		log.info("参与直播�? [{}] 抽奖{}", roomId, (isOk ? "成功" : "失败"));
		
		// 连续抽奖超过一定次�?, 重启浏览器释放缓�?
		if(lotteryCnt++ >= LOTTERY_LIMIT) {
			closeBrowser();
			UIUtils.log("已释放无效的内存空间");
			
		// 若无后续抽奖则马上跳回去首页, 避免接收太多直播间数据浪费内�?
		} else if(RoomMgr.getInstn().getGiftRoomCount() <= 0){
			Browser.open(HOME_URL);
		}
	}
	
	@Deprecated
	private boolean _lottery(int roomId) {
		boolean isOk = false;
		try {
			if(_lottery()) {
				UIUtils.statistics("成功: 抽奖直播�? [", roomId, "]");
				UIUtils.updateLotteryCnt();
				isOk = true;
				
			} else {
				UIUtils.statistics("超时: 抽奖直播�? [", roomId, "]");
			}
			
		} catch(Throwable e) {
			UIUtils.statistics("挤不进去: 抽奖直播�? [", roomId, "] ");
			UIUtils.log("辣鸡B站炸�?, 自动重连");
		}
		return isOk;
	}
	
	@Deprecated
	private boolean _lottery() {
		boolean isOk = false;
		WebElement vm = Browser.findElement(By.id("chat-popup-area-vm"));
		By element = By.className("lottery-box");
		if(Browser.existElement(element)) {
			WebElement lotteryBox = vm.findElement(element);
			WebElement rst = lotteryBox.findElement(By.className("next-loading"));
			
			isOk = _clickArea(lotteryBox, rst);
			if(isOk == false) {	// 重试一�?
				_sleep(SLEEP_TIME);
				isOk = _clickArea(lotteryBox, rst);
			}
		}
		return isOk;
	}
	
	/**
	 * 点击抽奖区域
	 * @param lotteryBox
	 * @param rst
	 * @return
	 */
	@Deprecated
	private boolean _clickArea(WebElement lotteryBox, WebElement rst) {
		Browser.click(lotteryBox);	// 点击抽奖
		_sleep(SLEEP_TIME);	// 等待抽奖结果
		return rst.getText().contains("成功");
	}

	/**
	 * 友爱社日常签�?
	 */
	private void toSignAssn() {
		if(signAssn == false || (assnCnt++ <= ASSN_LIMIT)) {
			return;
		}
		assnCnt = 0;
		
		boolean isGoOn = MsgSender.toAssn();
		if(isGoOn == false) {
			signAssn = false;
			UIUtils.log("今天已在友爱社签�?");
		}
	}
	
	/**
	 * 执行日常小学数学任务
	 */
	private void doDailyTasks() {
		resetDailyTasks();
		if(nextTaskTime <= 0 || nextTaskTime > System.currentTimeMillis()) {
			return;
		}
		
		nextTaskTime = MsgSender.doDailyTasks();
		if(nextTaskTime <= 0) {
			UIUtils.log("今天所有小学数学任务已完成");
		}
	}
	
	/**
	 * 当跨天后，自动重置每日任�?
	 */
	private void resetDailyTasks() {
		long now = System.currentTimeMillis();
		if(nextTaskTime > 0 || (now - resetTaskTime <= DAY_UNIT)) {
			return;
		}
		
		long hms = now % DAY_UNIT;	// 取时分秒
		long hour = hms / HOUR_UNIT;	// 取小�?
		hour = (hour + HOUR_OFFSET) % 24;		// 校正时差
		
		// 凌晨时重置每日任务时�?
		if(hour == 0) {
			resetTaskTime = now;
			nextTaskTime = now;
			MsgSender.toSign();	// 重新每日签到
			signAssn = true;	// 标记友爱社可以签�?
		}
	}
	
	/**
	 * 计数器累计达到一个心跳周期后, 关闭浏览�?(等待有其他事件时再自动重�?)
	 */
	private void toSleep() {
		if(loopCnt++ >= LOOP_LIMIT) {
			tipCnt++;
			loopCnt = 0;
			closeBrowser();
			log.info("{} 活动�?...", getName());
		}
		
		if(tipCnt >= TIP_LIMIT) {
			tipCnt = 0;
			UIUtils.printVersionInfo();
		}
	}
	
}
