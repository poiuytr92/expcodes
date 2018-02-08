package exp.qw.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpClient;
import exp.qw.Config;

public class SinaWeiboUtils {

	private final static Logger log = LoggerFactory.getLogger(SinaWeiboUtils.class);
	
	private final static String DOWNLOAD_DIR = "./downloads/";
	
	private final static String PIC_SUFFIX = ".jpg";
	
	private final static long WAIT_LIMIT = 60;
	
	public static boolean login(WebDriver webDriver, 
			String loginPage, String username, String password) {
		boolean isOk = false;
		if(webDriver == null || StrUtils.isEmpty(loginPage) || 
				StrUtils.isEmpty(username) || StrUtils.isEmpty(password)) {
			return isOk;
		}
		
		log.info("正在打开登陆页面: [{}]", loginPage);
		webDriver.get(loginPage);
		ThreadUtils.tSleep(500);	// 避免过快操作
		
		Set<Cookie> cookies = getCookies();
		if(cookies.size() > 0) {
			isOk = login(webDriver, loginPage, cookies);
			
		} else {
			isOk = login(webDriver, username, password);
		}
		
		// 保存本次登陆的cookies
		if(isOk == true) {
			log.info("正在保存本次登陆的Cookies...");
			storeCookies(webDriver);
		}
		return isOk;
	}
	
	private static boolean login(WebDriver webDriver, 
			String loginPage, Set<Cookie> cookies) {
		// TODO Auto-generated method stub
		// 使用cookies登陆需要重新打开登陆页面做跳转？ 直接webDriver刷新可以吗？
		return false;
	}

	/**
	 * 模拟真实行为进行登陆
	 * @param webDriver
	 * @param loginPage
	 * @param username
	 * @param password
	 * @return g_tk: 每次登陆时动态生成，g_tk码用于QQ空间的其他提交操作
	 */
	private static boolean login(WebDriver webDriver, 
			String username, String password) {
		boolean isOk = true;
		try {
			log.info("填写用户名 [{}]", username);
			((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginName').setAttribute('value', '" + username + "')");
			ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
			
			log.info("填写密码 [{}]", StrUtils.multiChar('*', password.length()));
			((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginPassword').setAttribute('value', '" + password + "')");
			ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
			
			log.info("点击 [登陆按钮], 等待登陆...");
			((JavascriptExecutor) webDriver).executeScript("document.getElementById('loginAction').click()");
			ThreadUtils.tSleep(1000);	// 避免过快操作，等待登陆成功
			
		} catch(Exception e) {
			isOk = false;
			log.error("登陆异常.", e);
		}
		return isOk;
	}
	
	/**
	 * 爬取微博相册专辑中所有相册
	 * @param webDriver
	 * @param albumIndexUrl
	 */
	public static void wormAlbums(WebDriver webDriver, String albumIndexUrl) {
		log.info("开始爬取微博相册专辑: [{}].", albumIndexUrl);
		Map<String, String> albumUrls = getAlbumUrls(webDriver, albumIndexUrl);
		
		int cnt = 0;
		Iterator<String> nameIts = albumUrls.keySet().iterator();
		while(nameIts.hasNext()) {
			String albumName = nameIts.next();
			String albumUrl = albumUrls.get(albumName);
			cnt += wormAlbum(webDriver, albumName, albumUrl);
			
			log.info("爬取相册 [{}] 完成, 避免反爬, 休眠30秒.", albumName);
			ThreadUtils.tSleep(30000);	
		}
		log.info("爬取微博相册专辑完成, 共爬取 [{}] 张照片.", albumIndexUrl, cnt);
	}
	
	/**
	 * 爬取单个微博相册
	 * @param webDriver
	 * @param albumName
	 * @param albumUrl
	 * @return
	 */
	protected static int wormAlbum(WebDriver webDriver, String albumName, String albumUrl) {
		int cnt = 0;
		if(StrUtils.isEmpty(albumUrl) || !"微博配图".equals(albumName)) {
			return cnt;
		}
		
		log.info("开始爬取相册 [{}] : [{}].", albumName, albumUrl);
		List<String> picUrls = getPicUlrs(webDriver, albumName, albumUrl);
		
		HttpClient client = new HttpClient();
		for(String picUrl : picUrls) {
			if(++cnt <= 408) {
				continue;
			}
			
			String bigPicUrl = getBigPicUrl(webDriver, picUrl);
			webDriver.get(bigPicUrl);
			WebElement we = webDriver.findElement(By.id("pic"));
			String url = we.getAttribute("src");
			
			System.out.println(url);
			String savePath = StrUtils.concat(DOWNLOAD_DIR, albumName, "/", System.currentTimeMillis(), PIC_SUFFIX);
			client.downloadByGet(savePath, url, null, null);
			
//			if(++cnt % 100 == 0) {
//				log.info("爬取相册 [{}] 中... 避免反爬, 休眠30秒. 当前已爬取 [{}] 张照片.", albumName, cnt);
//				ThreadUtils.tSleep(30000);
//			}
		}
		client.close();
		log.info("爬取相册 [{}] 完成, 共爬取 [{}] 张照片.", albumName, cnt);
		return cnt;
	}
	
	/**
	 * 获取微博专辑下所有相册地址
	 * @param webDriver
	 * @param albumIndexUrl
	 * @return
	 */
	protected static Map<String, String> getAlbumUrls(WebDriver webDriver, String albumIndexUrl) {
		log.info("开始获取专辑下所有相册地址...");
		final String DIV_CLASSNAME = "m_user_albumlist";
		
		Map<String, String> albumUrls = new HashMap<String, String>();
		webDriver.get(albumIndexUrl);
		
		WebDriverWait wait = new WebDriverWait(webDriver, WAIT_LIMIT);
		wait.until(new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver webDriver) {
				log.info("正在等待页面加载相册地址...");
				return webDriver.findElement(By.className(DIV_CLASSNAME)).
						findElement(By.tagName("ul")).isDisplayed();
			}
		});
		
		log.info("正在获取相册地址...");
		int cnt = 0;
		WebElement div = webDriver.findElement(By.className(DIV_CLASSNAME));
		WebElement ul = div.findElement(By.tagName("ul"));
		List<WebElement> lis = ul.findElements(By.tagName("li"));
		for(WebElement li : lis) {
			WebElement a = li.findElement(By.xpath("div/div/div/a"));
			String name = a.getAttribute("title");
			String url = a.getAttribute("href");
			
			log.info("相册 [{}] 地址: [{}].", name, url);
			albumUrls.put(name, url);
			FileUtils.createDir(StrUtils.concat(DOWNLOAD_DIR, name));
			cnt++;
			
			WebElement span = a.findElement(By.tagName("span"));
			if("0 张".equals(span.getText())) {
				albumUrls.put(name, "");
				log.warn(" - 相册 [{}] 内无图片.", name);
			}
		}
		log.info("获取专辑下所有相册地址完成, 共 [{}] 个相册.", cnt);
		return albumUrls;
	}
	
	/**
	 * 获取相册下所有相片地址
	 * @param webDriver
	 * @param albumUrl
	 * @return
	 */
	protected static List<String> getPicUlrs(WebDriver webDriver, 
			String albumName, String albumUrl) {
		List<String> picUrls = new LinkedList<String>(); 
		getPicUlrs(picUrls, webDriver, albumName, albumUrl, 1);
		return picUrls;
	}
	
	/**
	 * 获取相册下第N页中所有相片地址
	 * @param picUrls
	 * @param webDriver
	 * @param albumUrl
	 * @param pageNum
	 */
	private static void getPicUlrs(List<String> picUrls, WebDriver webDriver, 
			String albumName, String albumUrl, int pageNum) {
		log.info("正在扫描相册 [{}] 第 [{}] 页中的所有图片地址...", albumName, pageNum);
		String curUrl = StrUtils.concat(albumUrl, "#!/mode/1/page/", pageNum);
		
		// 翻页需要两次回车
		webDriver.get(curUrl);
		ThreadUtils.tSleep(1000);	// 避免过快操作
		webDriver.get(curUrl);
		ThreadUtils.tSleep(1000);	// 避免过快操作
		
		WebDriverWait wait = new WebDriverWait(webDriver, WAIT_LIMIT);
		wait.until(new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver webDriver) {
				log.info("正在等待页面加载图片地址...");
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
		}
		
		log.info("扫描相册 [{}] 第 [{}] 页中的所有图片地址完成.", albumName, pageNum);
		if(hasNextUrl(webDriver, albumName)) {
			
			log.info("正在翻到相册 [{}] 下一页...", albumName);
			ThreadUtils.tSleep(10000);	// 避免过快操作
			
			getPicUlrs(picUrls, webDriver, albumName, albumUrl, pageNum + 1);
		}
	}
	
	/**
	 * 检查相册中是否存在下一页
	 * @param webDriver
	 * @return
	 */
	private static boolean hasNextUrl(WebDriver webDriver, String albumName) {
		boolean isHas = false;
		WebElement we = webDriver.findElement(By.className("m_pages"));
		try {
			if(we.findElement(By.xpath("a[@action-data='page=next']")).isDisplayed()) {
				isHas = true;
			}
		} catch(Exception e) {
			log.warn("相册 [{}] 已到最后一页.", albumName);
		}
		return isHas;
	}
	
	/**
	 * 获取照片的大图地址
	 * @param webDriver
	 * @param picUrl
	 * @return
	 */
	protected static String getBigPicUrl(WebDriver webDriver, String picUrl) {
		webDriver.get(picUrl);
		WebElement we = webDriver.findElement(By.className("mark_box"));
		WebElement a = we.findElement(By.tagName("a"));
		return a.getAttribute("href");
	}
	
	private static Set<Cookie> getCookies() {
		String data = FileUtils.read(Config.COOKIES_PATH, Charset.UTF8);
		// TODO
		return new HashSet<Cookie>();
	}
	
	/**
	 * 存储本次登陆的 cookies， 并返回 p_skey
	 * @param webDriver
	 * @return p_skey：每次登陆动态生成，用于生成 g_tk码， g_tk码用于QQ空间的其他提交操作
	 */
	private static String storeCookies(WebDriver webDriver) {
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

}
