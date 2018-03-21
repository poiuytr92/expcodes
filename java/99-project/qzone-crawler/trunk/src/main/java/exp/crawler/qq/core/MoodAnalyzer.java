package exp.crawler.qq.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.crawler.qq.bean.Mood;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

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
	
	/** 说说每页图文信息的保存路径前缀 */
	private final static String PAGE_DIR_PREFIX = MOOD_DIR.concat("content/page-");
	
	/** 说说每页所有照片的保存目录 */
	private final static String PHOTO_DIR = MOOD_DIR.concat("photos/");
	
	/**
	 * 下载所有说说
	 * @param QZONE_URL 目标QQ空间的首页地址
	 */
	public static void downloadMoods(final String QZONE_URL) {
		try {
			if(!switchToMoodPage(QZONE_URL)) {
				UIUtils.log("切换到【说说列表】失败");
				
			} else {
				FileUtils.delete(MOOD_DIR);
				download(getAllMoods());
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
	
	/**
	 * 提取每一页的说说及其照片信息
	 * @return 相册 -> 照片集
	 */
	private static List<Mood> getAllMoods() {
		
		// 切换到本页【说说列表】的嵌套页
		Browser.switchToFrame(By.id("app_canvas_frame"));
		
		// 获取本页说说列表
		List<Mood> moods = new LinkedList<Mood>();
		for(int page = 1; ; page++) {
			
			UIUtils.log("正在提取第 [", page, "] 页的说说信息...");
//			moods.addAll(_getCurPageMoods(page));
			UIUtils.log("第 [", page, "] 页说说提取完成, 累计数量: ", moods.size());
			
			if(_nextPage() == false) {
				break;
			}
		}
		return moods;
	}
	
	/**
	 * 获取当前页面的所有照片信息
	 * @return
	 */
	private static List<Mood> _getCurPageMoods(int page) {
		List<Mood> moods = new LinkedList<Mood>();
		
		WebElement ul = Browser.findElement(By.id("msgList"));
		List<WebElement> list = ul.findElements(By.xpath("li"));
		for(WebElement li : list) {
			
			WebElement desc = li.findElement(By.xpath("div[3]/div[2]/pre"));
			String content = desc.getText().trim();
			
			List<String> urls = new LinkedList<String>();
			List<WebElement> as = li.findElements(By.xpath("div[3]/div[3]/div[1]/div/a"));
			for(WebElement a : as) {
				String url = a.getAttribute("href");
				url = url.replace("psbe?", "psb?");	// 去除权限加密（部分相册虽然没密码，但不是对所有人可见的）
				url = url.replace("/c/", "/b/");	// 缩略图变成大图
				urls.add(url);
			}
			
			Mood mood = new Mood(page, content, urls);
			moods.add(mood);
		}
		return moods;
	}
	
	/**
	 * 切换到下一页
	 * @return true:已切换到下一页; false:已是最后一页
	 */
	private static boolean _nextPage() {
		boolean hasNext = true;
		for(int retry = 1; retry <= 10; retry++) {
			try {
				WebElement pagenav = Browser.findElement(By.className("mod_pagenav_main"));
				WebElement next = pagenav.findElement(By.xpath("a[last()]"));
				if(next != null && "下一页".equals(next.getAttribute("title"))) {
					next.click();
					break;
				}
			} catch(Exception e) {
				ThreadUtils.tSleep(SLEEP_TIME);
			}
		}
		return hasNext;
	}
	
	/**
	 * 下载每一页的说说及其照片信息
	 * @param pageAndPhotos 说说及照片集
	 */
	private static void download(List<Mood> moods) {
		for(Mood mood : moods) {
			_download(mood);
		}
	}
	
	/**
	 * 下载照片
	 * @param album 照片所属的相册信息
	 * @param photo 照片信息
	 * @return 是否下载成功
	 */
	private static boolean _download(Mood mood) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "image/webp,image/*,*/*;q=0.8");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.COOKIE, Browser.COOKIE().toNVCookie());
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, HttpUtils.HEAD.VAL.USER_AGENT);

		int idx = 0, cnt = 0;
		for(String picURL : mood.PIC_URLS()) {
			String picName = getFileName(mood.CONTENT(), idx++);
			cnt += (_download(mood, header, picName, picURL) ? 1 : 0);
		}
		return (cnt == mood.PIC_URLS().size());
	}
	
	private static String getFileName(String desc, int idx) {
		String name = StrUtils.concat("[", IDUtils.getTimeID(), "]-[", idx, "] ", desc);
		name = FileUtils.delForbidCharInFileName(name, "");
		name = StrUtils.showSummary(name);
		name = name.concat(".png");
		return name;
	}
	
	private static boolean _download(Mood mood, 
			Map<String, String> header, String picName, String picURL) {
		header.put(HttpUtils.HEAD.KEY.HOST, RegexUtils.findFirst(picURL, "http://([^/]*)/"));
		
		boolean isOk = false;
		String savePath = StrUtils.concat(PAGE_DIR_PREFIX, mood.PAGE(), "/", picName);
		for(int retry = 0; !isOk && retry < 3; retry++) {
			isOk = HttpURLUtils.downloadByGet(savePath, picURL, header, null);
			if(isOk == false) {
				FileUtils.delete(savePath);
				
			} else {
				
				// 复制照片到合集目录
				FileUtils.copyFile(savePath, PHOTO_DIR.concat(picName));
			}
		}
		return isOk;
	}
	
}
