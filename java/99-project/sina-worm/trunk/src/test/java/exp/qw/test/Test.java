package exp.qw.test;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import exp.libs.utils.os.ThreadUtils;
import exp.qw.bean.BrowserDriver;

public class Test {

	public static void main(String[] args) {
		BrowserDriver bDriver = BrowserDriver.CHROME;
		WebDriver webDriver = bDriver.getWebDriver();
		
		webDriver.get("https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fm.weibo.cn%2F%3Fsudaref%3Dwww.baidu.com%26retcode%3D6102");
		
		String username = "xxxx";
		((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginName').setAttribute('value', '" + username + "')");
		ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
		
		String password = "xxxx";
		((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginPassword').setAttribute('value', '" + password + "')");
		ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
		
		((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginAction').click()");
		ThreadUtils.tSleep(500);	// 避免过快操作

		webDriver.get("http://photo.weibo.com/1915189502/albums");
		System.out.println(webDriver.getPageSource());
	}
	
	
}
