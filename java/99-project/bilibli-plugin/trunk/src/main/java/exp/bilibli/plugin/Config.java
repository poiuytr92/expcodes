package exp.bilibli.plugin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.envm.WebDriverType;
import exp.bilibli.plugin.utils.WebUtils;

public class Config {

	private final static String LIVE_URL = "http://live.bilibili.com/438";
	
	public static void main(String[] args) throws InterruptedException {
		BrowserDriver browser = new BrowserDriver(WebDriverType.PHANTOMJS);
		WebDriver driver = browser.getDriver();
		
		driver.navigate().to(LIVE_URL);
		Thread.sleep(10000);
		
		WebUtils.screenshot(driver, "./log/1.png");
		System.out.println("cookies1:" + driver.manage().getCookies().size());
		
		WebElement upgrapTips = driver.findElement(By.className("upgrade-intro-component"));
		WebElement skipBtn = upgrapTips.findElement(By.className("skip"));
		skipBtn.click();
		
		WebUtils.screenshot(driver, "./log/2.png");
		System.out.println("cookies2:" + driver.manage().getCookies().size());
	}
	
}
