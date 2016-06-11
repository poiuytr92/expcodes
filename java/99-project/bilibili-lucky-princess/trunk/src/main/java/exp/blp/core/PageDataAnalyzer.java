package exp.blp.core;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.blp.Config;
import exp.blp.bean.BrowserDriver;
import exp.blp.utils.UIUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.pub.FileUtils;
import exp.libs.utils.pub.StrUtils;

public class PageDataAnalyzer extends Thread {

	private final static Logger log = LoggerFactory.getLogger(PageDataAnalyzer.class);
	
	private final static String CHAT_MSG_LIST_NAME = "chat-msg-list";
	
	private final static long COLLECT_INTERVAL = 5000;
	
	private BrowserDriver browserDriver;
	
	private String httpUrl;
	
	private UserDataAnalyzer analyzer;
	
	private boolean isStop;
	
	public PageDataAnalyzer(String httpUrl) {
		this.browserDriver = BrowserDriver.CHROME;
		this.httpUrl = httpUrl;
		this.analyzer = new UserDataAnalyzer();
		this.isStop = true;
	}
	
	public void _start() {
		init();
		this.start();
	}
	
	@Override
	public void run() {
		collectOnlineData();
	}
	
	public void _stop() {
		this.isStop = true;
	}
	
	public boolean isStop() {
		return isStop;
	}
	
	private void init() {
		FileUtils.delete(Config.CHAT_MSG_LIST_FILE_PATH);
		FileUtils.copyFile(Config.HOME_PAGE_PATH_BAK, Config.HOME_PAGE_PATH);
	}
	
	public void collectOnlineData() {
		if(StrUtils.isEmpty(httpUrl)) {
			UIUtils.log("无效的HTTP, 终止操作.");
			return;
		}
		
		isStop = false;
		UIUtils.log("正在开启浏览器捕获数据...");
		WebDriver driver = null;
		try {
			driver = browserDriver.getWebDriver();
			
			UIUtils.log("正在打开网页 [" + httpUrl + "] ...");
			driver.get(httpUrl);
			UIUtils.openHomePage();
			
			UIUtils.log("统计在线用户数据开始...");
			int lastSize = 0;
			while(isStop() == false) {
				WebElement chatMsgList = driver.findElement(By.id(CHAT_MSG_LIST_NAME));
				String data = chatMsgList.getText();
				
				int curSize = data.length();
				if(lastSize != curSize) {
					lastSize = curSize;
					
					analyzer.statistics(data);
					log.info("已刷新在线用户数据.");
					
				} else {
					log.info("在线用户数据无变化.");
				}
				
				ThreadUtils.tSleep(COLLECT_INTERVAL);
				browserDriver.refresh(driver);
			}
			UIUtils.log("统计在线用户数据结束.");
			
		} catch (NoSuchElementException e) {
			log.error("加载网页元素失败: [{}].", httpUrl, e);
			
		} catch (Throwable e) {
			log.error("统计在线用户数据异常: [{}].", httpUrl, e);
			
		} finally {
			UIUtils.log("浏览器已关闭.");
			isStop = true;
			browserDriver.close(driver);
			System.exit(0);
		}
	}
	
	public UserDataAnalyzer getAnalyzer() {
		return analyzer;
	}
	
}
