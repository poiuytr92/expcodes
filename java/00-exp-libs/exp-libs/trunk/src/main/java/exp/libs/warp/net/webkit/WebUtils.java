package exp.libs.warp.net.webkit;

import java.io.File;

import org.openqa.selenium.By;
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
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(WebUtils.class);
	
	/** 私有化构造函数 */
	protected WebUtils() {} 
	
	/**
	 * 对浏览器的当前页面截图
	 * @param driver 浏览器驱动
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
	 * @param driver 浏览器驱动
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
	 * 保存当前页面（包括页面截图和页面源码）
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
			log.error("保存当前页面 [{}] 到 [{}] 失败: {}", URL, saveDir, e);
		}
	}
	
	/**
	 * 切换到frame嵌套页面的driver
	 * @param driver
	 * @param frame
	 * @return
	 */
	public static WebDriver toFrameDriver(WebDriver driver, By frame) {
		WebDriver frameDriver = null;
		if(driver != null) {
			try {
				frameDriver = driver.switchTo().frame(
						driver.findElement(frame));
				
			} catch(Exception e) {
				log.error("获取到嵌套页面驱动失败: [{}]", frame.toString(), e);
			}
		}
		return frameDriver;
	}
	
	/**
	 * 测试页面元素是否存在
	 * @param driver
	 * @param element
	 * @return
	 */
	public static boolean exist(WebDriver driver, By element) {
		boolean exist = true;
		try {
			driver.findElement(element);
		} catch(Throwable e) {
			exist = false;
		}
		return exist;
	}
	
	/**
	 * 点击按钮并提交
	 * @param button 页面可点击元素（按钮或超链接）
	 */
	public static void click(WebDriver driver, WebElement button) {
		if(driver != null && button != null) {
			Actions action = new Actions(driver);
			action.click(button).perform();	// 点击并提交
		}
	}
	
}
