package exp.crawler.qq.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.crawler.qq.bean.Album;
import exp.crawler.qq.bean.Photo;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.envm.XHRAtrbt;
import exp.crawler.qq.utils.UIUtils;
import exp.crawler.qq.utils.XHRUtils;
import exp.libs.envm.Charset;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
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
	
	/** 每次最多请求30张照片 */
	private final static int EACH_PAGE_LIMIT = 30;
	
	/** 行为休眠间隔 */
	private final static long SLEEP_TIME = 50;
	
	/** 相册保存目录 */
	private final static String ALBUM_DIR = "./data/album/";
	
	/** 相册信息保存文件名 */
	private final static String ALBUM_NAME = "AlbumInfo-[相册信息].txt";
	
	/** 发起请求次数 */
	private static int REQUEST_COUNT = 0;
	
	/**
	 * 下载所有相册
	 * @param QQ 目标QQ
	 */
	public static void downloadAlbums(String username, String QQ) {
		try {
			REQUEST_COUNT = 0;
			FileUtils.delete(ALBUM_DIR);
			download(getAlbumAndPhotos(username, QQ));
			UIUtils.log("任务完成, QQ[", QQ, "] 的【相册】数据已保存到: ", ALBUM_DIR);
			
		} catch(Exception e) {
			UIUtils.log("下载 QQ[", QQ, "] 的空间【相册】时发生异常");
			log.error("下载 QQ[{}] 的空间【相册】时发生异常", QQ, e);
		}
	}
	
	/**
	 * 提取所有相册及其内的照片信息
	 * @param QQ
	 * @return 相册 -> 照片集
	 */
	private static Map<Album, List<Photo>> getAlbumAndPhotos(String username, String QQ) {
		Map<Album, List<Photo>> albumAndPhotos = new LinkedHashMap<Album, List<Photo>>();
		List<Album> albums = _getAlbums(username, QQ);
		for(Album album : albums) {
			List<Photo> photos = _open(album, username, QQ);
			albumAndPhotos.put(album, photos);
		}
		return albumAndPhotos;
	}
	
	
	/**
	 * 提取所有相册信息
	 * @param QQ 目标QQ
	 * @return
	 */
	private static List<Album> _getAlbums(String username, String QQ) {
		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.g_tk, Browser.GTK());
		request.put(XHRAtrbt.callback, StrUtils.concat("shine", REQUEST_COUNT, "_Callback"));
		request.put(XHRAtrbt.callbackFun, StrUtils.concat("shine", REQUEST_COUNT++));
		request.put(XHRAtrbt.underline, String.valueOf(System.currentTimeMillis()));
		request.put(XHRAtrbt.uin, username);
		request.put(XHRAtrbt.hostUin, QQ);
		request.put(XHRAtrbt.inCharset, "utf-8");
		request.put(XHRAtrbt.outCharset, "utf-8");
		request.put(XHRAtrbt.source, "qzone");
		request.put(XHRAtrbt.plat, "qzone");
		request.put(XHRAtrbt.format, "jsonp");
		request.put(XHRAtrbt.notice, "0");
		request.put(XHRAtrbt.appid, "4");
		request.put(XHRAtrbt.idcNum, "4");
		request.put(XHRAtrbt.handset, "4");
		request.put(XHRAtrbt.filter, "1");
		request.put(XHRAtrbt.needUserInfo, "1");
		request.put(XHRAtrbt.pageNumModeSort, "40");
		request.put(XHRAtrbt.pageNumModeClass, "15");
//		request.put(XHRAtrbt.t, "869307580");	// 不知道是什么值
		
		String response = HttpURLUtils.doGet(URL.ALBUM_LIST_URL, header, request);
		
		List<Album> albums = new LinkedList<Album>();
		try {
			JSONObject json = JSONObject.fromObject(XHRUtils.toJson(response));
			JSONObject data = JsonUtils.getObject(json, XHRAtrbt.data);
			JSONArray albumList = JsonUtils.getArray(data, XHRAtrbt.albumListModeSort);
			for(int i = 0; i < albumList.size(); i++) {
				JSONObject album = albumList.getJSONObject(i);
				String name = JsonUtils.getStr(album, XHRAtrbt.name);
				String question = JsonUtils.getStr(album, XHRAtrbt.question);
				
				if(StrUtils.isEmpty(question)) {
					int total = JsonUtils.getInt(album, XHRAtrbt.total, 0);
					String id = JsonUtils.getStr(album, XHRAtrbt.id);
					String url = URL.ALBUM_URL(QQ, id);
					
					albums.add(new Album(id, name, url, total));
					UIUtils.log("获得相册 [", name, "] (照片x", total, "), 地址: ", url);
					
				} else {
					UIUtils.log("相册 [", name, "] 被加密, 无法读取");
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	/**
	 * 打开相册, 提取其中的所有照片信息
	 * @param album 相册信息
	 * @return
	 */
	private static List<Photo> _open(Album album, String username, String QQ) {
		final int PAGE_NUM = _getTotalPageNum(album);
		UIUtils.log("正在读取相册 [", album.NAME(), "] (照片x", album.NUM(), "), 总页数: ", PAGE_NUM);
		
		List<Photo> photos = new LinkedList<Photo>();
		for(int page = 1; page <= PAGE_NUM; page++) {
			
			UIUtils.log("正在提取第 [", page, "] 页的照片信息...");
			photos.addAll(getPagePhotos(username, QQ, album.ID(), page));
			UIUtils.log("第 [", page, "] 页照片提取完成, 当前进度: ", photos.size(), "/", album.NUM());
			
			ThreadUtils.tSleep(SLEEP_TIME);
		}
		return photos;
	}
	
	private static int _getTotalPageNum(Album album) {
		int pageNum = album.NUM() / EACH_PAGE_LIMIT;
		if(album.NUM() % EACH_PAGE_LIMIT != 0) {
			pageNum += 1;	// 向上取整
		}
		return pageNum;
	}

	/**
	 * 获取当前页面的所有照片信息
	 * @return
	 */
	private static List<Photo> getPagePhotos(String username, String QQ, String albumId, int page) {
		List<Photo> photos = new LinkedList<Photo>();
		
		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.g_tk, Browser.GTK());
		request.put(XHRAtrbt.callback, StrUtils.concat("shine", REQUEST_COUNT, "_Callback"));
		request.put(XHRAtrbt.callbackFun, StrUtils.concat("shine", REQUEST_COUNT++));
		request.put(XHRAtrbt.underline, String.valueOf(System.currentTimeMillis()));
		request.put(XHRAtrbt.uin, username);
		request.put(XHRAtrbt.hostUin, QQ);
		request.put(XHRAtrbt.inCharset, "utf-8");
		request.put(XHRAtrbt.outCharset, "utf-8");
		request.put(XHRAtrbt.source, "qzone");
		request.put(XHRAtrbt.plat, "qzone");
		request.put(XHRAtrbt.format, "jsonp");
		request.put(XHRAtrbt.notice, "0");
		request.put(XHRAtrbt.appid, "4");
		request.put(XHRAtrbt.idcNum, "4");
		request.put(XHRAtrbt.topicId, albumId);
		request.put(XHRAtrbt.mode, "0");
		request.put(XHRAtrbt.noTopic, "0");
		request.put(XHRAtrbt.skipCmtCount, "0");
		request.put(XHRAtrbt.singleurl, "1");
		request.put(XHRAtrbt.outstyle, "json");
		request.put(XHRAtrbt.json_esc, "1");
		request.put(XHRAtrbt.batchId, "");
		request.put(XHRAtrbt.pageStart, String.valueOf((page - 1) * EACH_PAGE_LIMIT));
		request.put(XHRAtrbt.pageNum, String.valueOf(EACH_PAGE_LIMIT));
//		request.put(XHRAtrbt.t, "869307580");	// 不知道是什么值
		
		String response = HttpURLUtils.doGet(URL.PHOTO_LIST_URL, header, request);
		try {
			JSONObject json = JSONObject.fromObject(XHRUtils.toJson(response));
			JSONObject data = JsonUtils.getObject(json, XHRAtrbt.data);
			JSONArray photoList = JsonUtils.getArray(data, XHRAtrbt.photoList);
			for(int i = 0; i < photoList.size(); i++) {
				JSONObject photo =  photoList.getJSONObject(i);
				String desc = JsonUtils.getStr(photo, XHRAtrbt.desc);
				String time = JsonUtils.getStr(photo, XHRAtrbt.uploadtime);
				String url = JsonUtils.getStr(photo, XHRAtrbt.url);
				url = url.replace("psbe?", "psb?");	// 去除权限加密（部分相册虽然没密码，但不是对所有人可见的）
				url = url.replace("/m/", "/b/");	// 缩略图变成大图
				
				photos.add(new Photo(desc, time, url));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return photos;
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
		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
		header.put(HttpUtils.HEAD.KEY.HOST, XHRUtils.toHost(photo.URL()));
		header.put(HttpUtils.HEAD.KEY.REFERER, album.URL());

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
