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


/**
 * <PRE>
 * 【空间说说】解析器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MoodAnalyzer extends BaseMoodAnalyzer {

	/** 目标QQ空间首页 */
	private final String QZONE_HOMR_URL;
	
	/**
	 * 构造函数
	 * @param QQ 被爬取数据的目标QQ
	 */
	public MoodAnalyzer(String QQ) {
		super(QQ);
		this.QZONE_HOMR_URL = URL.QZONE_HOMR_URL(this.QQ);
	}
	
	/**
	 * 初始化
	 */
	@Override
	protected void init() {
		// Undo
	}

	/**
	 * 提取所有说说及相关的照片信息
	 * @return
	 */
	@Override
	protected List<Mood> getMoods() {
		List<Mood> moods = new LinkedList<Mood>();
		if(switchToMoodPage() == true) {
			UIUtils.log("正在提取QQ [", QQ, "] 的说说动态...");
			
			// 切换到【说说动态】的嵌套页
			Browser.switchToFrame(By.id("app_canvas_frame"));
			
			final int PAGE_NUM = _getPageNum();
			for(int page = 1; page <= PAGE_NUM; page++) {
				UIUtils.log(" -> 正在提取第 [", page, "] 页的说说信息...");
				List<Mood> pageMoods = _getPageMoods(page);
				moods.addAll(pageMoods);
				
				UIUtils.log(" -> 第 [", page, "/", PAGE_NUM, 
						"] 页说说提取完成, 累计说说数量: ", moods.size());
				ThreadUtils.tSleep(Config.SLEEP_TIME);
				
				if(_nextPage() == false) {
					break;
				}
			}
		}
		return moods;
	}
	
	/**
	 * 切换到说说动态页面
	 * @return 是否切换成功
	 */
	private boolean switchToMoodPage() {
		UIUtils.log("正在打开QQ [", QQ, "] 的空间首页...");
		Browser.open(QZONE_HOMR_URL);
		
		UIUtils.log("正在切换到QQ [", QQ, "] 的说说动态...");
		boolean isOk = false;
		WebElement a = Browser.findElement(By.id("QM_Profile_Mood_A"));
		if(a != null) {
			isOk = true;
			a.click();
			
		} else {
			UIUtils.log("切换到QQ [", QQ, "] 的说说动态失败");
		}
		return isOk;
	}
	
	/**
	 * 获取说说总页数
	 * @return
	 */
	@Override
	protected int _getPageNum() {
		int pageNum = 0;
		try {
			WebElement last = Browser.findElement(
					By.xpath("p[@class='mod_pagenav_main']/a[last()-1]/span"));
			if(last != null) {
				pageNum = NumUtils.toInt(last.getText().trim(), 0);
			}
		} catch(Exception e) {}
		return pageNum;
	}
	
	/**
	 * 获取分页的说说内容
	 * @param page 页码
	 * @return 
	 */
	@Override
	protected List<Mood> _getPageMoods(int page) {
		List<Mood> moods = new LinkedList<Mood>();
		try {
			
			// 提取本页所有说说的信息
			WebElement ul = Browser.findElement(By.id("msgList"));
			List<WebElement> list = ul.findElements(By.xpath("li"));
			for(WebElement li : list) {
				WebElement pre = li.findElement(By.className("content"));
				WebElement time = li.findElement(By.className("goDetail"));
				
				String content = pre.getText().trim();
				long millis = TimeUtils.toMillis(time.getAttribute("title"), "yyyy年MM月dd日 HH:mm");
				
				Mood mood = new Mood(page, content, millis);
				WebElement div = li.findElement(By.className("img-attachments-inner"));
				List<WebElement> as = div.findElements(By.xpath("a"));
				for(WebElement a : as) {
					String url = a.getAttribute("href");
					url = PicUtils.convert(url);
					mood.addPicURL(url);
				}
				moods.add(mood);
			}
		} catch(Exception e) {
			UIUtils.log(e, "提取第 [", page, "] 页的说说信息异常");
		}
		return moods;
	}
	
	/**
	 * 切换到下一页
	 * @return true:已切换到下一页; false:已是最后一页
	 */
	private boolean _nextPage() {
		boolean hasNext = false;
		for(int retry = 1; retry <= Config.RETRY; retry++) {
			try {
				WebElement next = Browser.findElement(
						By.xpath("p[@class='mod_pagenav_main']/a[last()]"));
				if(next != null) {
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
