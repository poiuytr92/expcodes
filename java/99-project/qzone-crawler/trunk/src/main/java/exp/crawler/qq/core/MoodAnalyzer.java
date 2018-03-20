package exp.crawler.qq.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.utils.UIUtils;

/**
 * <PRE>
 * 【说说】解析器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MoodAnalyzer {
	
	/** 行为休眠间隔 */
	private final static long SLEEP_TIME = 50;
	
	/** 说说保存目录 */
	private final static String MOOD_DIR = "./data/mood/";
	
	/**
	 * 下载所有说说
	 * @param QZONE_URL 目标QQ空间的首页地址
	 */
	public static void downloadMoods(final String QZONE_URL) {
		try {
			if(!switchToMoodPage(QZONE_URL)) {
				UIUtils.log("切换到【说说列表】失败");
				
			} else {
				
				UIUtils.log("任务完成, 图文数据已保存到: ", MOOD_DIR);
			}
		} catch(Exception e) {
			UIUtils.log("下载QQ空间的【说说】时发生异常");
		}
	}
	
	/**
	 * 切换到相册列表页面
	 * @param QZONE_URL 目标QQ空间的首页地址
	 * @return 是否切换成功
	 */
	private static boolean switchToMoodPage(final String QZONE_URL) {
		UIUtils.log("正在打开QQ空间页面: ", QZONE_URL);
		Browser.open(QZONE_URL);
		
		UIUtils.log("正在切换到【说说列表】...");
		boolean isOk = false;
		WebElement a = Browser.findElement(By.id("QM_Profile_Mood_A"));
		if(a != null) {
			isOk = true;
			Browser.click(a);
		}
		return isOk;
	}
	
}
