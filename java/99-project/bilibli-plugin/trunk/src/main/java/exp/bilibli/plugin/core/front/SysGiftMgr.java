package exp.bilibli.plugin.core.front;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 全平台礼物抽奖管理器（小电视/高能礼物）
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class SysGiftMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(SysGiftMgr.class);
	
	private final static String LIVE_URL = Config.getInstn().LIVE_URL();
	
	private final static String HOME_URL = Config.getInstn().HOME_URL();
	
	private final static long SLEEP_TIME = 1000;
	
	/** phantomjs浏览器每一段时间要刷新一下, 否则会被释放掉 */
	private final static long REFRESH_TIME = 60000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_LIMIT = (int) (REFRESH_TIME / LOOP_TIME);
	
	private final static int LOTTERY_LIMIT = Config.getInstn().CLEAR_CACHE_CYCLE();
	
	private int loopCnt;
	
	private int lotteryCnt;
	
	private static volatile SysGiftMgr instance;
	
	private SysGiftMgr() {
		super("自动抽奖姬");
		this.loopCnt = 0;
		this.lotteryCnt = 0;
	}
	
	protected static SysGiftMgr getInstn() {
		if(instance == null) {
			synchronized (SysGiftMgr.class) {
				if(instance == null) {
					instance = new SysGiftMgr();
				}
			}
		}
		return instance;
	}

	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
		Browser.open(HOME_URL);
		RoomMgr.getInstn().clearGiftRooms();
	}

	@Override
	protected void _loopRun() {
		String roomId = RoomMgr.getInstn().getGiftRoom();
		try {
			toLottery(roomId);	// 参与直播间抽奖
			
		} catch(Exception e) {
			log.error("在直播间 [{}] 抽奖异常", roomId, e);
			Browser.reset(false);	// 重启浏览器
		}
		_sleep(SLEEP_TIME);	// 避免连续抽奖时，过频点击导致页面抽奖器无反应
	}

	@Override
	protected void _after() {
		log.info("{} 已停止", getName());
	}
	
	private void toLottery(String roomId) {
		
		// 长时间无抽奖, 关闭页面, 释放缓存
		if(roomId == null) {
			if(loopCnt++ >= LOOP_LIMIT) {
				loopCnt = 0;
				log.info("{} 活动中...", getName());
				Browser.quit();
			}
			
		// 打开直播间抽奖
		} else {
			String url = StrUtils.concat(LIVE_URL, roomId);
			Browser.open(url);	// 打开直播间
			_sleep(SLEEP_TIME);
			log.info("参与直播间 [{}] 抽奖{}", roomId, (lottery(roomId) ? "成功" : "失败"));
			
			if(lotteryCnt++ >= LOTTERY_LIMIT) {
				lotteryCnt = 0;
				Browser.quit();	// 连续抽奖超过一定次数, 关闭浏览器释放缓存
				UIUtils.log("已释放无效的内存空间");
				
			} else {
				Browser.open(HOME_URL);	// 马上跳回去首页, 避免接收太多直播间数据
			}
		}
	}
	
	private boolean lottery(String roomId) {
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
			UIUtils.log("辣鸡B站炸了, 正在重连...");
		}
		return isOk;
	}
	
	private boolean _lottery() {
		log.info("cookies:{}", Browser.backupCookies().size());
		Browser.screenshot("./log/lbox.png");
		
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

}
