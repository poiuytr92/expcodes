package exp.qw.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.num.IDUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.net.webkit.WebUtils;
import exp.qw.bean.Album;
import exp.qw.bean.Photo;
import exp.qw.cache.Browser;


/**
 * <PRE>
 * 相册解析器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AlbumAnalyzer {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(AlbumAnalyzer.class);
	
	/**
	 * 下载相册
	 * @param qq QQ号
	 */
	public static void downloadAlbums(final String QZONE_URL) {
		List<Album> albums = switchToAlbumList(QZONE_URL);
		for(Album album : albums) {
			open(album);
		}
	}
	
	/**
	 * 切换到相册列表页面
	 * @param QZONE_URL QQ空间主页
	 * @return 相册列表
	 */
	private static List<Album> switchToAlbumList(final String QZONE_URL) {
		
		log.info("正在打开QQ空间主页: {}", QZONE_URL);
		Browser.open(QZONE_URL);
		
		log.info("正在获取【相册列表】入口...");
		boolean isOk = false;
		WebElement ul = Browser.findElement(By.className("stats-list"));
		List<WebElement> list = ul.findElements(By.tagName("li"));
		for(WebElement li : list) {
			WebElement a = li.findElement(By.tagName("a"));
			String id = a.getAttribute("id");
			
			// 【相册】入口
			if("QM_Profile_Photo_A".equals(id)) {
				log.info("正在切换到【相册列表】...");
				Browser.click(a);
				isOk = true;
				break;
			}
		}
		
		List<Album> albums = new LinkedList<Album>();
		if(isOk == true) {
			
			// 切换到【相册列表】的嵌套页
			WebUtils.switchToFrame(Browser.DRIVER(), By.id("tphoto"));
			
			// 获取相册列表
			ul = Browser.findElement(By.className("js-album-list-ul"));
			list = ul.findElements(By.xpath("li"));
			for(WebElement li : list) {
				WebElement div = li.findElement(By.className("js-album-item"));
				WebElement desc = div.findElement(By.className("js-album-desc-a"));
				WebElement picNum = div.findElement(By.className("pic-num"));
				
				String aid = div.getAttribute("data-id");
				String title = desc.getAttribute("title");
				int num = NumUtils.toInt(picNum.getText().trim(), 0);
				
				if(num <= 0 || StrUtils.isNotEmpty(div.getAttribute("data-question"))) {
					// Undo: 相册内无照片，或相册被加密
					
				} else {
					String url = StrUtils.concat(QZONE_URL, "/photo/", aid);
					albums.add(new Album(aid, title, url, num));
				}
			}
			
			log.info("获取【相册列表】成功: 可读相册x{}, 加密或无效相册x{}", 
					albums.size(), (list.size() - albums.size()));
			
		} else {
			log.warn("获取【相册列表】失败");
		}
		return albums;
	}
	
	
	
	
	/**
	 * 打开第idx个相册
	 * @param idx 相册索引
	 * @return 该相册所有图片的下载路径
	 */
	private static List<Photo> open(Album album) {
		Browser.open(album.URL());
		log.info("相册 [{}] 已打开, 共 [{}] 张照片: {}", album.NAME(), album.NUM(), album.URL());
		
		List<Photo> photos = new LinkedList<Photo>();
		WebUtils.switchToFrame(Browser.DRIVER(), By.id("tphoto"));
		
		int cnt = 0;
		WebElement ul = Browser.findElement(By.className("j-pl-photolist-ul"));
		List<WebElement> list = ul.findElements(By.xpath("li"));
		for(WebElement li : list) {
			WebElement desc = li.findElement(By.xpath("div/div[1]/div/div"));
			String name = desc.getText().trim();
			log.info("name: {}", name);
			
			WebElement span = li.findElement(By.xpath("div/div[2]/div/span"));
			String date = span.getText().trim();
			log.info("date: {}", date);
			
			WebElement img = li.findElement(By.xpath("div/div[1]/a/img"));
			String smallUrl = img.getAttribute("src");
			if(smallUrl == null) {
				smallUrl = img.getAttribute("data-src");
			}
			String bigUrl = smallUrl.replace("/m/", "/b/");
			log.info("url: {}", bigUrl);
			
			
			
			downloadPhoto(album, bigUrl);
//			Browser.open(bigUrl);
//			ThreadUtils.tSleep(2000);
//			Browser.screenshot("./log/photo.png");
			break;
//			photos.add(new Photo(name, date, bigUrl));
		}
		return photos;
	}
	
	private static void downloadPhoto(Album album, String url) {
		Map<String, String> header = new HashMap<String, String>();
		header.put("accept", "*/*");
		header.put("accept-encoding", "gzip, deflate, sdch, br");
		header.put("accept-language", "zh-CN,zh;q=0.8,en;q=0.6");
		header.put("cookie", Browser.COOKIE().toNVCookie());
		header.put("referer", album.URL());
		header.put("user-agent", HttpUtils.HEAD.VAL.USER_AGENT);
		
		boolean isOk = HttpURLUtils.downloadByGet("./log/" + IDUtils.getMillisID() + ".png", url, header, null);
		System.out.println(isOk + " : " + url);
	}
	
}
