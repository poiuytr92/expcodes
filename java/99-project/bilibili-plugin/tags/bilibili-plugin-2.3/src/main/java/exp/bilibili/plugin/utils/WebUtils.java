package exp.bilibili.plugin.utils;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import exp.libs.utils.io.FileUtils;

/**
 * <PRE>
 * Web驱动工具类
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebUtils {

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
	
	
	public static boolean exist(WebDriver driver, By element) {
		boolean exist = true;
		try {
			driver.findElement(element);
		} catch(Throwable e) {
			exist = false;
		}
		return exist;
	}
	
}
