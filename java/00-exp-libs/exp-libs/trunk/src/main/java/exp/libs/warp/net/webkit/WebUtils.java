package exp.libs.warp.net.webkit;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Web驱动工具类
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(WebUtils.class);
	
	/** 私有化构造函�? */
	protected WebUtils() {} 
	
	/**
	 * 对浏览器的当前页面截�?
	 * @param driver 浏览器驱�?
	 * @param imgPath 图片保存路径
	 */
	public static void screenshot(WebDriver driver, String imgPath) {
		if(driver == null) {
			return;
		}
		
		driver.manage().window().maximize(); //浏览器窗口最大化
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);  
        FileUtils.copyFile(srcFile, new File(imgPath));
	}
	
	/**
	 * 使浏览器跳转到指定页面后截图
	 * @param driver 浏览器驱�?
	 * @param url 跳转页面
	 * @param imgPath 图片保存路径
	 */
	public static void screenshot(WebDriver driver, String url, String imgPath) {
		if(driver == null) {
			return;
		}
		
		driver.navigate().to(url);
		screenshot(driver, imgPath);
	}
	
	/**
	 * 保存当前页面（包括页面截图和页面源码�?
	 * @param saveDir 保存目录
	 * @param saveName 保存名称
	 */
	public static void saveCurPage(WebDriver driver, String saveDir, String saveName) {
		if(driver == null) {
			return;
		}
		
		final String URL = driver.getCurrentUrl();
		try {
			String html = StrUtils.concat(URL, "\r\n\r\n", driver.getPageSource());
			FileUtils.write(StrUtils.concat(saveDir, saveName, ".html"), html);
			screenshot(driver, StrUtils.concat(saveDir, saveName, ".png"));
			
		} catch(Exception e) {
			log.error("保存当前页面 [{}] �? [{}] 失败: {}", URL, saveDir, e);
		}
	}
	
	/**
	 * 切换到嵌套页面的frame
	 * @param driver 
	 * @param frame
	 * @return
	 */
	public static void switchToFrame(WebDriver driver, By frame) {
		try {
			driver.switchTo().frame(findElement(driver, frame));
			
		} catch(Exception e) {
			log.error("切换到嵌套页面驱动失�?: [{}]", frame.toString(), e);
		}
	}
	
	/**
	 * 切换到上层frame
	 * @param driver
	 */
	public static void switchToParentFrame(WebDriver driver) {
		try {
			driver.switchTo().parentFrame();
			
		} catch(Exception e) {
			log.error("切换到上层页面驱动失�?: [{}]", e);
		}
	}
	
	/**
	 * 切换到顶层frame（默认层�?
	 * @param driver
	 */
	public static void switchToTopFrame(WebDriver driver) {
		try {
			driver.switchTo().defaultContent();
			
		} catch(Exception e) {
			log.error("切换到上层页面驱动失�?: [{}]", e);
		}
	}
	
	/**
	 * 测试页面元素是否存在
	 * @param driver
	 * @param element
	 * @return
	 */
	public static boolean exist(WebDriver driver, By by) {
		return (findElement(driver, by) != null);
	}
	
	/**
	 * 查找页面元素
	 * @param by 元素位置
	 * @return 若不存在返回null
	 */
	public static WebElement findElement(WebDriver driver, By by) {
		WebElement element = null;
		try {
			element = driver.findElement(by);
		} catch(Throwable e) {
			log.error("查找页面元素失败", e);
		}
		return element;
	}
	
	/**
	 * 查找页面元素列表
	 * @param by 元素位置
	 * @return 若不存在返回空队�?
	 */
	public static List<WebElement> findElements(WebDriver driver, By by) {
		List<WebElement> elements = null;
		try {
			elements = driver.findElements(by);
			
		} catch(Throwable e) {
			elements = new LinkedList<WebElement>();
			log.error("查找页面元素失败", e);
		}
		return elements;
	}
	
	/**
	 * 填写数据到页面输入框元素
	 * @param input 页面输入框元�?
	 * @param data 数据
	 */
	public static void fill(WebElement input, String data) {
		try {
			input.clear();
			input.sendKeys(data);
			
		} catch(Exception e) {
			log.error("输入数据失败", e);
		}
	}
	
	/**
	 * 填写数据到页面表单元�?
	 * @param form 页面表单元素
	 * @param data 表单数据
	 */
	public static void fill(WebDriver driver, WebElement form, String data) {
		try {
			Actions action = new Actions(driver);
			form.clear();
			action.sendKeys(form, data, Keys.ENTER, Keys.NULL).perform();	// 填写并提�?
			
		} catch(Exception e) {
			log.error("填写表单数据失败", e);
		}
	}
	
	/**
	 * 点击按钮并提�?
	 * @param button 页面按钮元素
	 */
	public static void click(WebDriver driver, WebElement button) {
		try {
			Actions action = new Actions(driver);
			action.click(button).perform();	// 点击并提�?
			
		} catch(Exception e) {
			log.error("点击页面元素失败", e);
		}
	}
	
	/**
	 * 点击超链�?
	 * @param aLink 页面超链接元�?
	 */
	public static void click(WebElement aLink) {
		try {
			aLink.click();
			
		} catch(Exception e) {
			log.error("点击页面元素失败", e);
		}
	}
	
	/**
	 * 滚动到页面顶�?
	 * @param driver
	 */
	public static void scrollToTop(WebDriver driver) {
		scroll(driver, false);
	}
	
	/**
	 * 滚动到页面底�?
	 * @param driver
	 */
	public static void scrollToBottom(WebDriver driver) {
		scroll(driver, true);
	}
	
	private static void scroll(WebDriver driver, boolean toBottom) {
		try {
			String js = "document.documentElement.scrollTop=".
					concat(toBottom ? "10000" : "0");
			((JavascriptExecutor) driver).executeScript(js);  
			
		} catch(Exception e) {
			log.error("滚动页面到{}失败", (toBottom ? "底部" : "顶部"), e);
		}
	}
	
}
