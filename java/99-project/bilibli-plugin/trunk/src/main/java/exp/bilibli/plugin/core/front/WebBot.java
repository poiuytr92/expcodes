package exp.bilibli.plugin.core.front;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.ChatMgr;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * Web行为模拟器（仿真机器人）
 * 
 * 	主要功能:
 *   1.全平台礼物抽奖管理器（小电视/高能礼物）
 *   2.自动聊天
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class WebBot extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebBot.class);
	
	private final static String LIVE_URL = Config.getInstn().LIVE_URL();
	
	private final static String HOME_URL = Config.getInstn().HOME_URL();
	
	private final static long SLEEP_TIME = 1000;
	
	/** 浏览器非活动时的保持时间 */
	private final static long REFRESH_TIME = 60000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_LIMIT = (int) (REFRESH_TIME / LOOP_TIME);
	
	private final static int LOTTERY_LIMIT = Config.getInstn().CLEAR_CACHE_CYCLE();
	
	private int loopCnt;
	
	private int lotteryCnt;
	
	private static volatile WebBot instance;
	
	private WebBot() {
		super("Web行为模拟器");
		this.loopCnt = 0;
		this.lotteryCnt = 0;
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
		RoomMgr.getInstn().clearGiftRooms();
	}

	@Override
	protected void _loopRun() {
		try {
			toDo();
		} catch(Exception e) {
			log.error("模拟Web操作异常, 自动重启Web驱动", e);
			Browser.quit();
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
		
		// 参与直播间抽奖
		String roomId = RoomMgr.getInstn().getGiftRoom();
		if(roomId != null) {
			toLottery(roomId);
			
		} else {
			
			// 利用抽奖间歇参与直播间发言
			String msg = ChatMgr.getInstn().getMsg();
			if(msg != null) {
				toChat(msg);
				
			// 长时间无操作则休眠
			} else {
				toSleep();
			}
		}
	}
	
	private void toLottery(String roomId) {
		String url = StrUtils.concat(LIVE_URL, roomId);
		Browser.open(url);	// 打开直播间(若浏览器已关闭会先打开)
		_sleep(SLEEP_TIME);
		boolean isOk = _lottery(roomId);
		log.info("参与直播间 [{}] 抽奖{}", roomId, (isOk ? "成功" : "失败"));
		
		// 连续抽奖超过一定次数, 重启浏览器释放缓存
		if(lotteryCnt++ >= LOTTERY_LIMIT) {
			lotteryCnt = 0;
			Browser.quit();
			UIUtils.log("已释放无效的内存空间");
			
		// 抽奖后马上跳回去首页, 避免接收太多直播间数据浪费内存
		} else {
			Browser.open(HOME_URL);	
		}
	}
	
	private boolean _lottery(String roomId) {
		boolean isOk = false;
		try {
			if(_lottery()) {
				UIUtils.statistics("成功: 抽奖直播间 [", roomId, "]");
				UIUtils.updateLotteryCnt();
				isOk = true;
				
			} else {
				UIUtils.statistics("超时: 抽奖直播间 [", roomId, "]");
			}
			
		} catch(Throwable e) {
			UIUtils.statistics("挤不进去: 抽奖直播间 [", roomId, "] ");
			UIUtils.log("辣鸡B站炸了, 尝试重连...");
		}
		return isOk;
	}
	
	private boolean _lottery() {
		boolean isOk = false;
		WebElement vm = Browser.findElement(By.id("chat-popup-area-vm"));
		By element = By.className("lottery-box");
		if(Browser.existElement(element)) {
			WebElement lotteryBox = vm.findElement(element);
			WebElement rst = lotteryBox.findElement(By.className("next-loading"));
			
			isOk = _clickArea(lotteryBox, rst);
			if(isOk == false) {	// 重试一次
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
	private boolean _clickArea(WebElement lotteryBox, WebElement rst) {
		Browser.click(lotteryBox);	// 点击抽奖
		_sleep(SLEEP_TIME);	// 等待抽奖结果
		return rst.getText().contains("参与成功");
	}

	/**
	 * 参与聊天室发言
	 * @param msg
	 */
	private void toChat(String msg) {
		
		// 若当前页面不是所监听的聊天室, 则跳转到所监听的聊天室
		String liveUrl = AppUI.getInstn().getLiveUrl();
		if(!liveUrl.equals(Browser.getCurURL())) {
			Browser.open(liveUrl);
		}
		
		// 参与聊天室发言
		Browser.toLiveChat(msg);
	}
	
	/**
	 * 计数器累计达到一个心跳周期后, 关闭浏览器(等待有其他事件时再自动重启)
	 */
	private void toSleep() {
		if(loopCnt++ >= LOOP_LIMIT) {
			loopCnt = 0;
			log.info("{} 活动中...", getName());
			Browser.quit();
		}
	}
}
