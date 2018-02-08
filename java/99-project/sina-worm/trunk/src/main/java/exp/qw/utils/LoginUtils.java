package exp.qw.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.utils.format.ESCUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.JSUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.qw.Config;

public class LoginUtils {

	private final static Logger log = LoggerFactory.getLogger(LoginUtils.class);
	
	/**
	 * 模拟真实行为进行登陆
	 * @param webDriver
	 * @param loginPage
	 * @param username
	 * @param password
	 * @return g_tk: 每次登陆时动态生成，g_tk码用于QQ空间的其他提交操作
	 */
	public static String login(WebDriver webDriver, 
			String loginPage, String username, String password) {
		String gtk = "";
		if(webDriver == null || StrUtils.isEmpty(loginPage) || 
				StrUtils.isEmpty(username) || StrUtils.isEmpty(password)) {
			return gtk;
		}
		
		log.info("正在打开登陆页面: [{}]", loginPage);
		
		// 打开【登陆页面】
		webDriver.get(loginPage);
		
		ThreadUtils.tSleep(500);	// 避免过快操作
		
		log.info("填写用户名 [{}]", username);
		((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginName').setAttribute('value', '" + username + "')");
		ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
		
		log.info("填写密码 [{}]", StrUtils.multiChar('*', password.length()));
		((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginPassword').setAttribute('value', '" + password + "')");
		ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
		
		log.info("点击 [登陆按钮], 等待登陆...");
		((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginAction').click()");
		ThreadUtils.tSleep(5000);	// 避免过快操作，等待登陆成功
		
		log.info("正在保存本次登陆的Cookies...");
		String skey = storeCookies(webDriver);	// FIXME: 用cookies登陆
		ThreadUtils.tSleep(2000);	// 避免过快操作
		
		
		log.info("正在获取相册地址...");
		Map<String, String> albumUrls = getAlbums(webDriver);
		Iterator<String> nameIts = albumUrls.keySet().iterator();
		while(nameIts.hasNext()) {
			String albumName = nameIts.next();
			String albumUrl = albumUrls.get(albumName);
			List<String> picUrls = getPhotos(webDriver, albumUrl);
			
			System.out.println(albumName);
			for(String picUrl : picUrls) {
				String bigPage = getBigPicUrl(webDriver, picUrl);
				webDriver.get(bigPage);
				WebElement we = webDriver.findElement(By.id("pic"));
				String url = we.getAttribute("src");
				System.out.println(url);
				HttpURLUtils.downloadByGet("./downloads/" + albumName + "/" + System.currentTimeMillis() + ".jpg", url, null, null);
			}
		}
		
		ThreadUtils.tSleep(2000);	// 避免过快操作
		return gtk;
	}
	
	private static String getBigPicUrl(WebDriver webDriver, String picUrl) {
		webDriver.get(picUrl);
		WebElement we = webDriver.findElement(By.className("mark_box"));
		WebElement a = we.findElement(By.tagName("a"));
		return a.getAttribute("href");
	}
	/**
	 * *** Element info: {Using=class name, value=mark_box}
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:408)
	at org.openqa.selenium.remote.ErrorHandler.createThrowable(ErrorHandler.java:206)
	at org.openqa.selenium.remote.ErrorHandler.throwIfResponseFailed(ErrorHandler.java:158)
	at org.openqa.selenium.remote.RemoteWebDriver.execute(RemoteWebDriver.java:678)
	at org.openqa.selenium.remote.RemoteWebDriver.findElement(RemoteWebDriver.java:363)
	at org.openqa.selenium.remote.RemoteWebDriver.findElementByClassName(RemoteWebDriver.java:477)
	at org.openqa.selenium.By$ByClassName.findElement(By.java:391)
	at org.openqa.selenium.remote.RemoteWebDriver.findElement(RemoteWebDriver.java:355)
	at exp.qw.utils.LoginUtils.getBigPicUrl(LoginUtils.java:102)
	at exp.qw.utils.LoginUtils.login(LoginUtils.java:87)
	at exp.qw.Main.main(Main.java:18)
	 * @param webDriver
	 * @return
	 */

	public static Map<String, String> getAlbums(WebDriver webDriver) {
		Map<String, String> albumUrls = new HashMap<String, String>();
		webDriver.get("http://photo.weibo.com/1915189502/albums");
		
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver webDriver) {
				System.out.println("等待相册地址出现。。。");
				return webDriver.findElement(By.className("m_user_albumlist")).
						findElement(By.tagName("ul")).isDisplayed();
			}
			
		});
		
		WebElement we = webDriver.findElement(By.className("m_user_albumlist"));
		WebElement ul = we.findElement(By.tagName("ul"));
		List<WebElement> lis = ul.findElements(By.tagName("li"));
		for(WebElement li : lis) {
			WebElement a = li.findElement(By.xpath("div/div/div/a"));
			String name = a.getAttribute("title");
			String url = a.getAttribute("href");
			
			WebElement span = a.findElement(By.tagName("span"));
			if(!"0 张".equals(span.getText())) {
				albumUrls.put(name, url);
				System.out.println(url);
			} else {
				System.out.println(name + " 相册内无图片.");
			}
			
		}
		return albumUrls;
	}
	
	public static List<String> getPhotos(WebDriver webDriver, String albumUrl) {
		List<String> picUrls = new LinkedList<String>(); 
		getPhotoUrls(picUrls, webDriver, albumUrl, 1);
		return picUrls;
	}
	
	private static void getPhotoUrls(List<String> picUrls, 
			WebDriver webDriver, String albumUrl, int pageNum) {
		String curUrl = albumUrl + "#!/mode/1/page/" + pageNum;
		webDriver.get(curUrl);
		ThreadUtils.tSleep(1000);	// 避免过快操作
		webDriver.get(curUrl);	// 换页要两次回车
		ThreadUtils.tSleep(1000);	// 避免过快操作
		
//		if(pageNum > 1) {
//			WebDriverWait wait = new WebDriverWait(webDriver, 60);
//			wait.until(new ExpectedCondition<Boolean>() {
//
//				@Override
//				public Boolean apply(WebDriver webDriver) {
//					System.out.println("等待页面加载完成");
//					String pageText = webDriver.findElement(By.className("M_linke")).findElement(By.tagName("span")).getText();
//					return ("第 " + pageNum + " 页").equals(pageText);
//				}
//				
//			});
//		}
		
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver webDriver) {
				System.out.println("等待图片地址出现。。。");
				return webDriver.findElement(By.className("m_photo_list_a")).
						findElement(By.tagName("ul")).isDisplayed();
			}
			
		});
		
		WebElement we = webDriver.findElement(By.className("m_photo_list_a"));
		WebElement ul = we.findElement(By.tagName("ul"));
		List<WebElement> lis = ul.findElements(By.tagName("li"));
		for(WebElement li : lis) {
			WebElement a = li.findElement(By.xpath("dl/dt/a"));
			String url = a.getAttribute("href");
			picUrls.add(url);
			System.out.println(url);
		}
		
		// 取下一页
		if(hasNextUrl(webDriver)) {
			getPhotoUrls(picUrls, webDriver, albumUrl, pageNum + 1);
		}
	}
	
	private static boolean hasNextUrl(WebDriver webDriver) {
		boolean isHas = false;
		WebElement we = webDriver.findElement(By.className("m_pages"));
		try {
			if(we.findElement(By.xpath("a[@action-data='page=next']")).isDisplayed()) {
				isHas = true;
			}
		} catch(Exception e) {
			log.error("找不到下一页", e);
		}
		return isHas;
	}
	
	
	/**
	 * 存储本次登陆的 cookies， 并返回 p_skey
	 * @param webDriver
	 * @return p_skey：每次登陆动态生成，用于生成 g_tk码， g_tk码用于QQ空间的其他提交操作
	 */
	public static String storeCookies(WebDriver webDriver) {
		List<List<Object>> table = new LinkedList<List<Object>>();
		List<Object> header = Arrays.asList(new Object[] {
				"name", "value", "domain", "path", "expiry", "isSecure", "isHttpOnly"
		});
		table.add(header);
		
		String pSkey = "";
		Set<Cookie> cookies = webDriver.manage().getCookies();
		for(Cookie cookie : cookies) {
			List<Object> row = new LinkedList<Object>();
			row.add(cookie.getName() == null ? "" : cookie.getName());
			row.add(cookie.getValue() == null ? "" : cookie.getValue());
			row.add(cookie.getDomain() == null ? "" : cookie.getDomain());
			row.add(cookie.getPath() == null ? "" : cookie.getPath());
			row.add(cookie.getExpiry() == null ? "" : cookie.getExpiry().getTime());
			row.add(cookie.isSecure() ? "true" : "false");
			row.add(cookie.isHttpOnly() ? "true" : "false");
			table.add(row);
			
			// 最新的 g_tk算法 是通过 [名为p_skey的cookies的值] 去生成的，
			// 旧算法是用  [名为skey的cookies的值] 生成，已确认失效
			if("p_skey".equals(cookie.getName())) {
				pSkey = cookie.getValue();
			}
		}
		
		String cookiesData = ESCUtils.toTXT(table, true);
		FileUtils.write(Config.COOKIES_PATH, cookiesData, Charset.UTF8, false);
		return pSkey; 
	}

	/**
	 * 通过 skey 计算GTK码.
	 * 
	 * 先用 外置的JS算法 计算 GTK， 当使用 JS计算失败 时，才使用内置算法计算。
	 * 外置JS算法主要是为了在QQ更新了GTK算法情况下，可以对应灵活修改。
	 * 
	 * QQ计算GTK的JS函数获取方法：
	 * 	在登陆页面点击【登陆后】，按F12打开开发者工具，
	 * 	通过ctrl+shift+f全局搜索 【g_tk】，可以找到这个js函数
	 * 
	 * @param skey
	 * @return
	 */
	public static String getGTK(String skey) {
		String gtk = "";
		try {
			gtk = String.valueOf(JSUtils.executeJS(
					Config.GTK_JS_PATH, Config.GTK_METHOD, skey));
			
		} catch (Throwable e) {
			gtk = _getGTK(skey);
		}
		return gtk;
	}
	
	/**
	 * 内置GTK算法
	 * @param skey
	 * @return
	 */
	private static String _getGTK(String skey) {
		String gtk = "";
		int hash = 5381;
		for (int i = 0; i < skey.length(); ++i) {
			hash += (hash << 5) + (int) skey.charAt(i);
		}
		gtk = String.valueOf(hash & 0x7fffffff);
		return gtk;
	}
	
}
