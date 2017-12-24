package exp.qw.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.utils.format.ESCUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.JSUtils;
import exp.libs.utils.other.StrUtils;
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
		
		// QQ空间的【登陆操作界面】是通过【iframe】嵌套在【登陆页面】中的子页面
		// 此处需要跳转到嵌套页面
		webDriver.switchTo().defaultContent();
		WebDriver frameDriver = webDriver.switchTo().frame(
				webDriver.findElement(By.id("login_frame")));
		ThreadUtils.tSleep(500);	// 避免过快操作
		
		log.info("切换帐密登陆方式为 [帐密登陆]");
//		((JavascriptExecutor) frameDriver).executeScript("document.getElementById('switcher_plogin').click()");
		WebElement switchBtn = frameDriver.findElement(By.id("switcher_plogin"));
		switchBtn.click();
		ThreadUtils.tSleep(500);	// 避免过快操作
		
		log.info("填写用户名 [{}]", username);
//		((JavascriptExecutor) frameDriver).executeScript("document.getElementById('u').setAttribute('value', '" + username + "')");
		WebElement un = frameDriver.findElement(By.id("u"));
		un.clear();
		un.sendKeys(username);
		ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
		
		log.info("填写密码 [{}]", StrUtils.multiChar('*', password.length()));
//		((JavascriptExecutor) frameDriver).executeScript("document.getElementById('p').setAttribute('value', '" + password + "')");
		WebElement pw = frameDriver.findElement(By.id("p"));
		pw.clear();
		pw.sendKeys(password);
		ThreadUtils.tSleep(500);	// 避免过快操作导致焦点选择不当而填错位置
		
		log.info("点击 [登陆按钮], 等待登陆...");
//		((JavascriptExecutor) frameDriver).executeScript("document.getElementById('login_button').click()");
		WebElement loginBtn = frameDriver.findElement(By.id("login_button"));
		loginBtn.click();
		ThreadUtils.tSleep(500);	// 避免过快操作
		
		log.info("正在保存本次登陆的Cookies...");
		String skey = storeCookies(webDriver);
		
		log.info("获得本次登陆的 [p_skey = {}], 准备生成 [g_tk] 码...", skey);
		gtk = getGTK(skey);
		return gtk;
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
