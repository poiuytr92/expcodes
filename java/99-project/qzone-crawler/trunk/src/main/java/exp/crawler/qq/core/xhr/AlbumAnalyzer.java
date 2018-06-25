package exp.crawler.qq.core.xhr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.crawler.qq.Config;
import exp.crawler.qq.bean.Album;
import exp.crawler.qq.bean.Photo;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.core.interfaze.BaseAlbumAnalyzer;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.envm.XHRAtrbt;
import exp.crawler.qq.utils.PicUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.crawler.qq.utils.XHRUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 【空间相册】解析器
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AlbumAnalyzer extends BaseAlbumAnalyzer {

	/** 累计发起请求次数 */
	private int requestCnt;
	
	/**
	 * 构造函�?
	 * @param QQ 被爬取数据的目标QQ
	 */
	public AlbumAnalyzer(String QQ) {
		super(QQ);
	}
	
	/**
	 * 初始�?
	 */
	@Override
	protected void init() {
		this.requestCnt = 0;
	}
	
	/**
	 * 提取所有相册及其内的照片信�?
	 * @return 
	 */
	@Override
	protected List<Album> getAlbums() {
		List<Album> albums = _getAlbumLists();
		for(Album album : albums) {
			_open(album);
		}
		return albums;
	}
	
	/**
	 * 获取相册列表(仅相册信�?, 不含内部照片信息)
	 * @return
	 */
	@Override
	protected List<Album> _getAlbumLists() {
		UIUtils.log("正在提取QQ [", QQ, "] 的相册列�?...");
		
		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
		Map<String, String> request = _getAlbumRequest();
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
					UIUtils.log("相册 [", name, "] 被加�?, 无法读取");
				}
			}
		} catch(Exception e) {
			UIUtils.log(e, "提取QQ [", QQ, "] 的相册列表异�?");
		}
		
		UIUtils.log("提取QQ [", QQ, "] 的相册列表完�?: �? [", albums.size(), "] 个相�?");
		return albums;
	}
	
	/**
	 * 相册列表请求参数
	 * @return
	 */
	private Map<String, String> _getAlbumRequest() {
		Map<String, String> request = _getRequest();
		request.put(XHRAtrbt.handset, "4");
		request.put(XHRAtrbt.filter, "1");
		request.put(XHRAtrbt.needUserInfo, "1");
		request.put(XHRAtrbt.pageNumModeSort, "40");
		request.put(XHRAtrbt.pageNumModeClass, "15");
		return request;
	}
	
	/**
	 * 打开相册, 提取其中的所有照片信�?
	 * @param album 相册信息
	 * @return
	 */
	@Override
	protected void _open(Album album) {
		UIUtils.log("正在读取相册 [", album.NAME(), "] (�?", 
				album.PAGE_NUM(), "�?, 照片x", album.TOTAL_PIC_NUM(), ")");
		
		for(int page = 1; page <= album.PAGE_NUM(); page++) {
			UIUtils.log(" -> 正在提取�? [", page, "] 页的照片信息...");
			List<Photo> pagePhotos = _getPagePhotos(album, page);
			album.addPhotos(pagePhotos);
			
			UIUtils.log(" -> �? [", page, "] 页照片提取完�?, 当前进度: ", 
					album.PIC_NUM(), "/", album.TOTAL_PIC_NUM());
			ThreadUtils.tSleep(Config.SLEEP_TIME);
		}
	}
	
	/**
	 * 获取相册的分页照片信�?
	 * @param album 相册信息
	 * @param page 页数
	 * @return
	 */
	@Override
	protected List<Photo> _getPagePhotos(Album album, int page) {
		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
		Map<String, String> request = _getPhotoRequest(album.ID(), page);
		String response = HttpURLUtils.doGet(URL.PHOTO_LIST_URL, header, request);
		
		List<Photo> photos = new LinkedList<Photo>();
		try {
			JSONObject json = JSONObject.fromObject(XHRUtils.toJson(response));
			JSONObject data = JsonUtils.getObject(json, XHRAtrbt.data);
			JSONArray photoList = JsonUtils.getArray(data, XHRAtrbt.photoList);
			for(int i = 0; i < photoList.size(); i++) {
				JSONObject photo =  photoList.getJSONObject(i);
				String desc = JsonUtils.getStr(photo, XHRAtrbt.desc);
				String time = JsonUtils.getStr(photo, XHRAtrbt.uploadtime);
				String url = JsonUtils.getStr(photo, XHRAtrbt.url);
				url = PicUtils.convert(url);
				
				photos.add(new Photo(desc, time, url));
			}
		} catch(Exception e) {
			UIUtils.log(e, "提取相册 [", album.NAME(), "] �?", page, "页的照片信息异常");
		}
		return photos;
	}
	
	/**
	 * 分页照片的请求参�?
	 * @param albumId 相册ID
	 * @param page 页码
	 * @return
	 */
	private Map<String, String> _getPhotoRequest(String albumId, int page) {
		Map<String, String> request = _getRequest();
		request.put(XHRAtrbt.topicId, albumId);
		request.put(XHRAtrbt.pageStart, String.valueOf((page - 1) * Config.BATCH_LIMT));
		request.put(XHRAtrbt.pageNum, String.valueOf(Config.BATCH_LIMT));
		request.put(XHRAtrbt.mode, "0");
		request.put(XHRAtrbt.noTopic, "0");
		request.put(XHRAtrbt.skipCmtCount, "0");
		request.put(XHRAtrbt.singleurl, "1");
		request.put(XHRAtrbt.outstyle, "json");
		request.put(XHRAtrbt.json_esc, "1");
		request.put(XHRAtrbt.batchId, "");
		return request;
	}
	
	/**
	 * 相册/照片请求参数
	 * @return
	 */
	private Map<String, String> _getRequest() {
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.g_tk, Browser.GTK());
		request.put(XHRAtrbt.callback, StrUtils.concat("shine", requestCnt, "_Callback"));
		request.put(XHRAtrbt.callbackFun, StrUtils.concat("shine", requestCnt++));
		request.put(XHRAtrbt.underline, String.valueOf(System.currentTimeMillis()));
		request.put(XHRAtrbt.uin, Browser.UIN());
		request.put(XHRAtrbt.hostUin, QQ);
		request.put(XHRAtrbt.inCharset, Config.CHARSET);
		request.put(XHRAtrbt.outCharset, Config.CHARSET);
		request.put(XHRAtrbt.source, "qzone");
		request.put(XHRAtrbt.plat, "qzone");
		request.put(XHRAtrbt.format, "jsonp");
		request.put(XHRAtrbt.notice, "0");
		request.put(XHRAtrbt.appid, "4");
		request.put(XHRAtrbt.idcNum, "4");
//		request.put(XHRAtrbt.t, "869307580");	// 非固�?, 暂未知道是什么�?, 但非必填参数
		return request;
	}
	
}
