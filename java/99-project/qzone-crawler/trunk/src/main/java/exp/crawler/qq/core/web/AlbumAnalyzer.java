package exp.crawler.qq.core.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.crawler.qq.bean.Album;
import exp.crawler.qq.bean.Photo;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;


/**
 * <PRE>
 * 【相册】解析器
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
	
	/** 行为休眠间隔 */
	private final static long SLEEP_TIME = 50;
	
	/** 相册保存目录 */
	private final static String ALBUM_DIR = "./data/album/";
	
	/** 相册信息保存文件名 */
	private final static String ALBUM_NAME = "AlbumInfo-[相册信息].txt";
	
	/**
	 * 下载所有相册
	 * @param QQ 目标QQ
	 */
	public static void downloadAlbums(String QQ) {
		try {
			if(!switchToAlbumPage(QQ)) {
				UIUtils.log("切换到【相册列表】失败");
				
			} else {
				FileUtils.delete(ALBUM_DIR);
				download(getAlbumAndPhotos(QQ));
				UIUtils.log("任务完成, QQ[", QQ, "] 的【相册】数据已保存到: ", ALBUM_DIR);
			}
		} catch(Exception e) {
			UIUtils.log("下载 QQ[", QQ, "] 的空间【相册】时发生异常");
			log.error("下载 QQ[{}] 的空间【相册】时发生异常", QQ, e);
		}
	}
	
	/**
	 * 切换到相册列表页面
	 * @param QQ 目标QQ
	 * @return 是否切换成功
	 */
	private static boolean switchToAlbumPage(String QQ) {
		final String QZONE_HOMR_URL = URL.QZONE_HOMR_URL(QQ);
		UIUtils.log("正在打开目标QQ空间: ", QZONE_HOMR_URL);
		Browser.open(QZONE_HOMR_URL);
		
		UIUtils.log("正在切换到【相册列表】...");
		boolean isOk = false;
		WebElement a = Browser.findElement(By.id("QM_Profile_Photo_A"));
		if(a != null) {
			isOk = true;
			Browser.click(a);
		}
		return isOk;
	}
	
	/**
	 * 提取所有相册及其内的照片信息
	 * @param QQ
	 * @return 相册 -> 照片集
	 */
	private static Map<Album, List<Photo>> getAlbumAndPhotos(String QQ) {
		Map<Album, List<Photo>> albumAndPhotos = new LinkedHashMap<Album, List<Photo>>();
		List<Album> albums = _getAlbums(QQ);
		for(Album album : albums) {
			List<Photo> photos = _open(album);
			albumAndPhotos.put(album, photos);
		}
		return albumAndPhotos;
	}
	
	
	/**
	 * 提取所有相册信息
	 * @param QQ 目标QQ
	 * @return
	 */
	private static List<Album> _getAlbums(String QQ) {
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
				String url = URL.ALBUM_URL(QQ, aid);
				albums.add(new Album(aid, title, url, num));
			}
		}
		
		UIUtils.log("获取【相册列表】成功: 可读相册x", albums.size(), 
				", 加密或空相册x", (list.size() - albums.size()));
		return albums;
	}
	
	/**
	 * 打开相册, 提取其中的所有照片信息
	 * @param album 相册信息
	 * @return
	 */
	private static List<Photo> _open(Album album) {
		List<Photo> photos = new LinkedList<Photo>();
		UIUtils.log("正在读取相册 [", album.NAME(), "] (照片x", album.TOTAL_PIC_NUM(), "), 地址: ", album.URL());
		
		Browser.open(album.URL());
		Browser.switchToFrame(By.id("tphoto"));
		
		// 提取相册内所有照片信息
		for(int page = 1; ; page++) {
			
			UIUtils.log("正在提取第 [", page, "] 页的照片信息...");
			photos.addAll(_getCurPagePhotoURLs());
			UIUtils.log("第 [", page, "] 页照片提取完成, 当前进度: ", photos.size(), "/", album.TOTAL_PIC_NUM());
			
			if(_nextPage() == false) {
				break;
			}
		}
		return photos;
	}

	/**
	 * 获取当前页面的所有照片信息
	 * @return
	 */
	private static List<Photo> _getCurPagePhotoURLs() {
		List<Photo> photos = new LinkedList<Photo>();
		
		// 加载本页所有照片
		while(true) {
			WebElement more = Browser.findElement(By.className("j-pl-photolist-tip-more"));
			if(more == null) {
				break;
			}
			more.click();
			ThreadUtils.tSleep(SLEEP_TIME);
		}
		
		// 提取本页所有照片的信息
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
			
			// 保存照片信息(用于下载)
			photos.add(new Photo(desc, date, url));
		}
		return photos;
	}
	
	/**
	 * 切换到下一页
	 * @return true:已切换到下一页; false:已是最后一页
	 */
	private static boolean _nextPage() {
		boolean hasNext = true;
		for(int retry = 1; retry <= 10; retry++) {
			try {
				WebElement next = Browser.findElement(By.id("pager_next_1"));
				if(next != null) {
					next.click();
				}
			} catch(Exception e) {
				ThreadUtils.tSleep(SLEEP_TIME);
			}
		}
		return hasNext;
	}
	
	/**
	 * 下载所有相册及其内的照片
	 * @param albumAndPhotos 相册及照片集
	 */
	private static void download(Map<Album, List<Photo>> albumAndPhotos) {
		UIUtils.log("所有【相册】图文信息提取完成, 开始下载...");
		
		Iterator<Album> albums = albumAndPhotos.keySet().iterator();
		while(albums.hasNext()) {
			Album album = albums.next();
			StringBuilder infos = new StringBuilder(album.toString());
			
			List<Photo> photos = albumAndPhotos.get(album);
			FileUtils.createDir(ALBUM_DIR.concat(album.NAME()));
			
			UIUtils.log("正在下载相册 [", album.NAME(), "] 的照片...");
			int cnt = 0;
			for(Photo photo : photos) {
				boolean isOk = _download(album, photo);
				cnt += (isOk ? 1 : 0);
				infos.append(photo.toString(isOk));
				UIUtils.log("下载进度(", (isOk ? "成功" : "失败"), "): ", cnt, "/", photos.size());
			}
			UIUtils.log("相册 [", album.NAME(), "] 下载完成: ", cnt, "/", photos.size());
			
			// 保存下载信息
			String fileName = StrUtils.concat(ALBUM_DIR, album.NAME(), "/", ALBUM_NAME);
			FileUtils.write(fileName, infos.toString(), Charset.UTF8, false);
		}
	}
	
	/**
	 * 下载照片
	 * @param album 照片所属的相册信息
	 * @param photo 照片信息
	 * @return 是否下载成功
	 */
	private static boolean _download(Album album, Photo photo) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "image/webp,image/*,*/*;q=0.8");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.COOKIE, Browser.COOKIE().toNVCookie());
		header.put(HttpUtils.HEAD.KEY.HOST, RegexUtils.findFirst(photo.URL(), "http://([^/]*)/"));
		header.put(HttpUtils.HEAD.KEY.REFERER, album.URL());
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, HttpUtils.HEAD.VAL.USER_AGENT);

		boolean isOk = false;
		String savePath = StrUtils.concat(ALBUM_DIR, album.NAME(), "/", photo.getPicName());
		for(int retry = 0; !isOk && retry < 3; retry++) {
			isOk = HttpURLUtils.downloadByGet(savePath, photo.URL(), header, null);
			if(isOk == false) {
				FileUtils.delete(savePath);
			}
		}
		return isOk;
	}
	
}
