package exp.bilibli.plugin.core.lottery;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.cache.BrowserMgr;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.utils.UIUtils;
import exp.bilibli.plugin.utils.WebUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

public class LotteryMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(LotteryMgr.class);
	
	private final static String LIVE_URL_PREFIX = "http://live.bilibili.com/";
	
	private final static long SLEEP_TIME = 1000;
	
	/** phantomjs浏览器每一段时间要刷新一下, 否则会被释放掉 */
	private final static long REFRESH_TIME = 60000;
	
	private final static long LOOP_TIME = 1000;
	
	private final static int LOOP_CNT = (int) (REFRESH_TIME / LOOP_TIME);
	
	private int loopCnt;
	
	private BrowserDriver browser;
	
	private WebDriver driver;
	
	private Actions action;
	
	private String lastRoomId;
	
	private static volatile LotteryMgr instance;
	
	private LotteryMgr() {
		super("自动抽奖姬");
		this.browser = BrowserMgr.getInstn().getBrowser();
		this.driver = browser.getDriver();
		this.action = new Actions(driver);
		this.lastRoomId = "";
		this.loopCnt = 0;
	}
	
	public static LotteryMgr getInstn() {
		if(instance == null) {
			synchronized (LotteryMgr.class) {
				if(instance == null) {
					instance = new LotteryMgr();
				}
			}
		}
		return instance;
	}

	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
		driver.navigate().to(LIVE_URL_PREFIX);
		RoomMgr.getInstn().clear();
	}

	@Override
	protected void _loopRun() {
		String roomId = RoomMgr.getInstn().get();
		
		// 保持页面一段时间后刷新, 避免被管理器终止进程
		if(roomId == null) {
			if(loopCnt++ >= LOOP_CNT) {
				loopCnt = 0;
				driver.navigate().refresh();
				log.info("页面心跳保活...");
			}
			
			
		// 若上一次的抽奖也是同样的房间, 则不再切换页面（以优化连续抽奖的情况）
		} else {
			if(!lastRoomId.equals(roomId)) {
				lastRoomId = roomId;
				String url = StrUtils.concat(LIVE_URL_PREFIX, roomId);
				driver.navigate().to(url);
				_sleep(SLEEP_TIME);
			}
			log.info("参与直播间 [{}] 抽奖{}", roomId, (lottery() ? "成功" : "失败"));
		}
		_sleep(SLEEP_TIME);	// 避免连续抽奖时，过频点击导致页面抽奖器无反应
	}

	@Override
	protected void _after() {
		log.info("{} 已停止", getName());
	}
	
	private boolean lottery() {
		boolean isOk = false;
		try {
			if(_lottery()) {
				UIUtils.statistics("成功: 抽奖直播间 [", lastRoomId, "]");
				UIUtils.updateLotteryCnt();
				
			} else {
				UIUtils.statistics("超时: 抽奖直播间 [", lastRoomId, "]");
			}
			
		} catch(Throwable e) {
			UIUtils.statistics("辣鸡服务器poorguy了: 抽奖直播间 [", lastRoomId, "] ");
		}
		return isOk;
	}
	
	private boolean _lottery() {
		boolean isOk = false;
		WebElement vm = driver.findElement(By.id("chat-popup-area-vm"));
		By element = By.className("lottery-box");
		if(WebUtils.exist(driver, element)) {
			WebElement lotteryBox = vm.findElement(element);
			action.click(lotteryBox).perform();	// 点击并提交（抽奖）
			_sleep(SLEEP_TIME);	// 等待抽奖结果
			
			WebElement rst = lotteryBox.findElement(By.className("next-loading"));
			isOk = rst.getText().contains("参与成功");
		}
		return isOk;
	}

}
