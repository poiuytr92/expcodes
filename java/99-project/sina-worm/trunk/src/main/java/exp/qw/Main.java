package exp.qw;

import org.openqa.selenium.WebDriver;

import exp.libs.utils.other.LogUtils;
import exp.qw.bean.BrowserDriver;
import exp.qw.utils.SinaWeiboUtils;


public class Main {

	public static void main(String[] args) {
		LogUtils.loadLogBackConfig(Config.LOG_CONFIG_PATH);
		BrowserDriver bDriver = BrowserDriver.PHANTOMJS;
		WebDriver webDriver = bDriver.getWebDriver();
		
		String loginPage = "https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fpad.weibo.cn%2F";
		String albumIndexUrl = "http://photo.weibo.com/3064280845/albums";	// 相册专辑地址
		String username = "play00002@126.com";
		String password = "test00002";
		SinaWeiboUtils.login(webDriver, loginPage, username, password);
		SinaWeiboUtils.wormAlbums(webDriver, albumIndexUrl);
	}
	
}
