package exp.crawler.qq.core.web;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.crawler.qq.Config;
import exp.crawler.qq.bean.Mood;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.core.interfaze.BaseMoodAnalyzer;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.utils.PicUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.warp.net.webkit.WebUtils;


/**
 * <PRE>
 * 【空间说说】解析器
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MoodAnalyzer extends BaseMoodAnalyzer {

	/** 目标QQ空间首页 */
	private final String QZONE_HOMR_URL;
	
	/**
	 * 构造函�?
	 * @param QQ 被爬取数据的目标QQ
	 */
	public MoodAnalyzer(String QQ) {
		super(QQ);
		this.QZONE_HOMR_URL = URL.QZONE_HOMR_URL(this.QQ);
	}
	
	/**
	 * 初始�?
	 */
	@Override
	protected void init() {
		// Undo
	}

	/**
	 * 提取所有说说及相关的照片信�?
	 * @return
	 */
	@Override
	protected List<Mood> getMoods() {
		List<Mood> moods = new LinkedList<Mood>();
		if(switchToMoodPage() == true) {
			UIUtils.log("正在提取QQ [", QQ, "] 的说说动�?...");
			
			// 切换到【说说动态】的嵌套�?
			Browser.switchToFrame(By.id("app_canvas_frame"));
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			
			final int PAGE_NUM = _getPageNum();
			for(int page = 1; page <= PAGE_NUM; page++) {
				UIUtils.log(" -> 正在提取�? [", page, "/", PAGE_NUM, "] 页的说说信息...");
				List<Mood> pageMoods = _getPageMoods(page);
				moods.addAll(pageMoods);
				
				UIUtils.log(" -> �? [", page, "/", PAGE_NUM, 
						"] 页说说提取完�?, 累计说说数量: ", moods.size());
				ThreadUtils.tSleep(Config.SLEEP_TIME);
				
				if(_nextPage() == false) {
					break;
				}
			}
		}
		return moods;
	}
	
	/**
	 * 切换到说说动态页�?
	 * @return 是否切换成功
	 */
	private boolean switchToMoodPage() {
		UIUtils.log("正在打开QQ [", QQ, "] 的空间首�?...");
		Browser.open(QZONE_HOMR_URL);
		
		UIUtils.log("正在切换到QQ [", QQ, "] 的说说页�?...");
		boolean isOk = false;
		WebElement a = Browser.findElement(By.id("QM_Profile_Mood_A"));
		if(a != null) {
			isOk = true;
			WebUtils.click(Browser.DRIVER(), a);	// 选中
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			WebUtils.click(Browser.DRIVER(), a);	// 点击
			
		} else {
			UIUtils.log("切换到QQ [", QQ, "] 的说说页面失�?");
		}
		return isOk;
	}
	
	/**
	 * 获取说说总页�?
	 * @return
	 */
	@Override
	protected int _getPageNum() {
		UIUtils.log("正在提取QQ [", QQ, "] 的说说页�?...");
		int pageNum = 0;
		try {
			WebElement last = Browser.findElement(
					By.xpath("//p[@class='mod_pagenav_main']/a[last()-1]/span"));
			if(last != null) {
				pageNum = NumUtils.toInt(last.getText().trim(), 0);
			}
		} catch(Exception e) {
			UIUtils.log(e, "提取QQ [", QQ, "] 的说说页数失�?");
		}
		return pageNum;
	}
	
	/**
	 * 获取分页的说说内�?
	 * @param page 页码
	 * @return 
	 */
	@Override
	protected List<Mood> _getPageMoods(int page) {
		List<Mood> moods = new LinkedList<Mood>();
		try {
			WebElement ul = Browser.findElement(By.id("msgList"));
			List<WebElement> list = ul.findElements(By.xpath("li"));
			for(WebElement li : list) {
				WebElement pre = li.findElement(By.className("content"));
				String content = pre.getText().trim();
				
				WebElement time = li.findElement(By.className("goDetail"));
				long millis = TimeUtils.toMillis(time.getAttribute("title"), "yyyy年MM月dd�? HH:mm");
				
				Mood mood = new Mood(page, content, millis);
				try {
					WebElement div = li.findElement(By.className("img-attachments-inner"));
					List<WebElement> as = div.findElements(By.xpath("a"));
					for(WebElement a : as) {
						String url = a.getAttribute("href");
						url = PicUtils.convert(url);
						mood.addPicURL(url);
					}
				} catch(Exception e) {
					// Undo 说说不一定有照片
				}
				
				moods.add(mood);
			}
		} catch(Exception e) {
			UIUtils.log(e, "提取�? [", page, "] 页的说说信息异常");
		}
		return moods;
	}
	
	/**
	 * 切换到下一�?
	 * @return true:已切换到下一�?; false:已是最后一�?
	 */
	private boolean _nextPage() {
		boolean hasNext = false;
		for(int retry = 1; !hasNext && retry <= Config.RETRY; retry++) {
			try {
				WebElement next = Browser.findElement(
						By.xpath("//p[@class='mod_pagenav_main']/a[last()]"));
				if(next != null && "下一�?".equals(next.getAttribute("title"))) {
					next.click();
					hasNext = true;
				}
			} catch(Exception e) {
				ThreadUtils.tSleep(Config.SLEEP_TIME);
			}
		}
		return hasNext;
	}
	
}
