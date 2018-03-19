package exp.qw.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.qw.bean.Album;
import exp.qw.bean.Photo;
import exp.qw.cache.Browser;
import exp.qw.utils.UIUtils;


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

	/** 相册保存目录 */
	private final static String ALBUM_DIR = "./data/album/";
	
	/**
	 * 下载相册
	 * @param qq QQ号
	 */
	public static void downloadAlbums(final String QZONE_URL) {
		if(!switchToAlbumPage(QZONE_URL)) {
			UIUtils.log("获取【相册列表】失败");
			
		} else {
			
			// 提取所有相册的照片信息
			Map<Album, List<Photo>> allPhotos = new HashMap<Album, List<Photo>>();
			List<Album> albums = getAlbums(QZONE_URL);
			for(Album album : albums) {
				List<Photo> photos = open(album);
				allPhotos.put(album, photos);
			}
			
			// 下载照片
			Iterator<Album> albumIts = allPhotos.keySet().iterator();
			while(albumIts.hasNext()) {
				Album album = albumIts.next();
				List<Photo> photos = allPhotos.get(album);
				FileUtils.createDir(ALBUM_DIR.concat(album.NAME()));
				
				UIUtils.log("正在下载相册 [", album.NAME(), "] 的照片...");
				int cnt = 0;
				for(Photo photo : photos) {
					cnt += (downloadPhoto(album, photo) ? 1 : 0);
					UIUtils.log("下载进度: ", cnt, "/", photos.size());
				}
				UIUtils.log("下载完成, 共下载照片: ", cnt, "/", photos.size());
			}
		}
		UIUtils.log("所有相册下载完毕, 图文数据已保存到: ", ALBUM_DIR);
	}
	
	/**
	 * 切换到相册列表页面
	 * @param QZONE_URL QQ空间主页
	 * @return 是否切换成功
	 */
	private static boolean switchToAlbumPage(final String QZONE_URL) {
		UIUtils.log("正在打开QQ空间页面: ", QZONE_URL);
		Browser.open(QZONE_URL);
		
		UIUtils.log("正在获取【相册列表】入口...");
		boolean isOk = false;
		WebElement ul = Browser.findElement(By.className("stats-list"));
		List<WebElement> list = ul.findElements(By.tagName("li"));
		for(WebElement li : list) {
			WebElement a = li.findElement(By.tagName("a"));
			String id = a.getAttribute("id");
			
			// 【相册】入口
			if("QM_Profile_Photo_A".equals(id)) {
				UIUtils.log("正在切换到【相册列表】...");
				Browser.click(a);
				isOk = true;
				break;
			}
		}
		return isOk;
	}
	
	private static List<Album> getAlbums(final String QZONE_URL) {
		List<Album> albums = new LinkedList<Album>();
			
		// 切换到【相册列表】的嵌套页
		Browser.switchToFrame(By.id("tphoto"));
		
		// 获取相册列表
		WebElement ul = Browser.findElement(By.className("js-album-list-ul"));
		List<WebElement> list = ul.findElements(By.xpath("li"));
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
		
		UIUtils.log("获取【相册列表】成功: 可读相册x", albums.size(), 
				", 加密或空相册x", (list.size() - albums.size()));
		return albums;
	}
	
	/**
	 * 打开相册
	 * @return 该相册所有图片的下载路径
	 */
	private static List<Photo> open(Album album) {
		List<Photo> photos = new LinkedList<Photo>();
		UIUtils.log("正在读取相册 [", album.NAME(), "] (照片x", album.NUM(), "), 地址: ", album.URL());
		
		Browser.open(album.URL());
		Browser.switchToFrame(By.id("tphoto"));
		
		// 提取相册内所有照片信息
		for(int page = 1; ; page++) {
			
			UIUtils.log("正在提取第 [", page, "] 页的照片信息...");
			photos.addAll(getCurPagePhotoURLs());
			UIUtils.log("第 [", page, "] 页提取完成, 当前进度: ", photos.size(), "/", album.NUM());
			
			// 下一页
			WebElement next = Browser.findElement(By.id("pager_next_1"));
			if(next == null) {
				break;
			}
			next.click();
		}
		return photos;
	}

	/**
	 * 获取当前页面的所有照片地址
	 * 
	 *  FIXME: 腾讯只预加载了前30张
	 * @return
	 */
	private static List<Photo> getCurPagePhotoURLs() {
		List<Photo> photos = new LinkedList<Photo>();
		
		WebElement ul = Browser.findElement(By.className("j-pl-photolist-ul"));
		List<WebElement> list = ul.findElements(By.xpath("li"));
		for(WebElement li : list) {
			
			// 取照片描述
			WebElement title = li.findElement(By.xpath("div/div[1]/div/div"));
			String desc = title.getAttribute("title");
			
			// 取照片上传日期
			WebElement span = li.findElement(By.xpath("div/div[2]/div/span"));
			String date = span.getAttribute("title");
			
			// 取照片原图地址
			WebElement img = li.findElement(By.xpath("div/div[1]/a/img"));
			String url = img.getAttribute("src");
			if(url == null) {
				url = img.getAttribute("data-src");
			}
			url = url.replace("psbe?", "psb?");	// 去除权限加密（部分相册虽然没密码，但不是对所有人可见的）
			url = url.replace("/m/", "/b/");	// 缩略图变成大图
			url = url.replace("&rf=photolist", "&rf=viewer_4&t=5");	// 无影响：呈现方式(photolist为缩略图列表, viewer_4为幻灯片)
			
			// 保存照片信息(用于下载)
			photos.add(new Photo(desc, date, url));
		}
		return photos;
	}
	
	/**
	 * 下载照片
	 * @param album
	 * @param photo
	 * @return
	 */
	private static boolean downloadPhoto(Album album, Photo photo) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "image/webp,image/*,*/*;q=0.8");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.COOKIE, Browser.COOKIE().toNVCookie());
		header.put(HttpUtils.HEAD.KEY.HOST, RegexUtils.findFirst(photo.URL(), "http://([^/]*)/"));
		header.put(HttpUtils.HEAD.KEY.REFERER, album.URL());
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, HttpUtils.HEAD.VAL.USER_AGENT);
		
		String savePath = StrUtils.concat(ALBUM_DIR, album.NAME(), photo.getFileName(), ".png");
		return HttpURLUtils.downloadByGet(savePath, photo.URL(), header, null);
	}
	
}
