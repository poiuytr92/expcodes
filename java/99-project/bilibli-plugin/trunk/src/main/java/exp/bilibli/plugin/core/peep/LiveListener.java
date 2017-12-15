package exp.bilibli.plugin.core.peep;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.AppUI;
import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.core.site.LoginMgr;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

// 无需登陆
public class LiveListener extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(LoginMgr.class);
	
	private final static String DEFAULT_LIVE_URL = "http://live.bilibili.com/438";
	
	private final static long WAIT_ELEMENT_TIME = 30;
	
	private final static String CHAT_LIST_DIV_ID = "chat-history-list";
	
	private final static String CHAT_ITEM_DIV_CLASS = "chat-item";
	
	private BrowserDriver browser;
	
	private WebDriver driver;
	
	private String liveURL;
	
	public LiveListener() {
		super("直播间监听器");
		this.browser = BrowserDriver.PHANTOMJS;
		this.driver = browser.getWebDriver(WAIT_ELEMENT_TIME);
		this.liveURL = DEFAULT_LIVE_URL;
	}

	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
		driver.get(liveURL);
	}

	@Override
	protected void _loopRun() {
		WebElement chatList = driver.findElement(By.id(CHAT_LIST_DIV_ID));
		List<WebElement> chatItems = chatList.findElements(By.className(CHAT_ITEM_DIV_CLASS));
		for(WebElement chatItem : chatItems) {
			UserMgr.getInstn().analyse(chatItem);
		}
//		AppUI.getInstn().updateChatDatas();
		browser.refresh(driver);	// FIXME： 目前刷新就是休眠功能
	}

	@Override
	protected void _after() {
		browser.close(driver);
		log.info("{} 已停止", getName());
	}
	
	public void linkToLive(String liveURL) {
		if(!this.liveURL.equals(liveURL)) {
			this.liveURL = (StrUtils.isEmpty(liveURL) ? DEFAULT_LIVE_URL : liveURL);
			driver.navigate().to(this.liveURL);
		}
	}
	
}
