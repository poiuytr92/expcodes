package exp.crawler.qq.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.crawler.qq.bean.Mood;
import exp.crawler.qq.bean.QQCookie;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.envm.XHRAtrbt;
import exp.crawler.qq.utils.PicUtils;
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
 * 【说说】解析器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MoodAnalyzer {
	
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(MoodAnalyzer.class);
	
	/**
	 * 每页最多20条说说
	 * （但可能少于20条, 即每页的实际个数不定, 造成这种情况可能因为被删除的说说依然占位）
	 */
	private final static int EACH_PAGE_LIMIT = 20;
	
	/** 行为休眠间隔 */
	private final static long SLEEP_TIME = 50;
	
	/** 超时时间(说说有些图片可能已经失效， 超时时间可以短些) */
	private final static int TIMEOUT = 10000;
	
	/** 说说保存目录 */
	private final static String MOOD_DIR = "./data/mood/";
	
	/** 说说每页图文信息的保存路径前缀 */
	private final static String PAGE_DIR_PREFIX = MOOD_DIR.concat("content/page-");
	
	/** 说说每页所有照片的保存目录 */
	private final static String PHOTO_DIR = MOOD_DIR.concat("photos/");
	
	/** 说说分页信息保存文件名 */
	private final static String MOOD_NAME = "MoodInfo-[说说信息].txt";
	
	/**
	 * 下载所有说说
	 * @param QQ 目标QQ
	 */
	public static void downloadMoods(String QQ) {
		try {
			FileUtils.delete(MOOD_DIR);
			download(getAllMoods(QQ));
			UIUtils.log("任务完成, QQ[", QQ, "] 的【说说】数据已保存到: ", MOOD_DIR);
			
		} catch(Exception e) {
			UIUtils.log("下载 QQ[", QQ, "] 的空间【说说】时发生异常");
			log.error("下载 QQ[{}] 的空间【说说】时发生异常", QQ, e);
		}
	}
	
	/**
	 * 获取所有说说的图文信息
	 * @param QQ 目标QQ
	 * @return
	 */
	private static List<Mood> getAllMoods(String QQ) {
		List<Mood> moods = new LinkedList<Mood>();
		final int PAGE_NUM = _getTotalPageNum(QQ);
		UIUtils.log("目标QQ[", QQ, "] 的说说总页数: ", PAGE_NUM);
		
		for(int page = 1; page <= PAGE_NUM; page++) {
			UIUtils.log("正在提取第 [", page, "] 页的说说数据...");
			moods.addAll(getPageMoods(QQ, page));
			UIUtils.log("第 [", page, "/", PAGE_NUM, "] 页说说提取完成, 累计: ", moods.size());
			
			ThreadUtils.tSleep(SLEEP_TIME);
		}
		return moods;
	}
	
	/**
	 * 获取目标QQ空间的说说总页数
	 * @param QQ
	 * @return
	 */
	private static int _getTotalPageNum(String QQ) {
		String response = _getPageMoods(QQ, 1);
		int total = 0;
		try {
			JSONObject json = JSONObject.fromObject(response);
			total = JsonUtils.getInt(json, XHRAtrbt.total, 0);	// 总说说数量
		} catch(Exception e) {}
		
		int pageNum = total / EACH_PAGE_LIMIT;
		if(total % EACH_PAGE_LIMIT != 0) {
			pageNum += 1;	// 向上取整
		}
		return pageNum;
	}
	
	/**
	 * 获取分页的说说内容
	 * @param QQ 目标QQ
	 * @param page 页数
	 * @return 
	 */
	private static List<Mood> getPageMoods(String QQ, int page) {
		List<Mood> moods = new LinkedList<Mood>();
		String response = _getPageMoods(QQ, page);
		
		try {
			JSONObject json = JSONObject.fromObject(response);
			JSONArray msglist = JsonUtils.getArray(json, XHRAtrbt.msglist);
			for(int i = 0; i < msglist.size(); i++) {
				JSONObject msg = msglist.getJSONObject(i);
				String content = JsonUtils.getStr(msg, XHRAtrbt.content);
				long createTime = JsonUtils.getLong(msg, XHRAtrbt.created_time, 0) * 1000;
				
				Mood mood = new Mood(page, content, createTime);
				JSONArray pics = JsonUtils.getArray(msg, XHRAtrbt.pic);
				for(int j = 0; j < pics.size(); j++) {
					JSONObject pic = pics.getJSONObject(j);
					String url = JsonUtils.getStr(pic, XHRAtrbt.url3);
					mood.addPicURL(url);
				}
				
				moods.add(mood);
			}
		} catch(Exception e) {
			log.error("提取 QQ[{}] 第 [{}] 页的空间【说说】数据异常", QQ, page, e);
		}
		return moods;
	}
	
	/**
	 * 获取分页的说说内容
	 * @param QQ 目标QQ
	 * @param page 页数
	 * @return json
	 */
	private static String _getPageMoods(String QQ, int page) {
		Map<String, String> header = _toMoodHeader(Browser.COOKIE());
		Map<String, String> request = _toMoodeRequest(QQ, page, Browser.GTK(), Browser.QZONE_TOKEN());
		String response = HttpURLUtils.doGet(URL.MOOD_URL, header, request);
		return XHRUtils.toJson(response);
	}
	
	/**
	 * 说说分页请求头
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> _toMoodHeader(QQCookie cookie) {
		Map<String, String> header = XHRUtils.getHeader(cookie);
		header.put(HttpUtils.HEAD.KEY.REFERER, URL.MOOD_REFERER);
		return header;
	}
	
	/**
	 * 说说分页请求参数
	 * @param QQ
	 * @param page
	 * @param gtk
	 * @param qzoneToken
	 * @return
	 */
	private static Map<String, String> _toMoodeRequest(String QQ, int page, 
			String gtk, String qzoneToken) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.uin, QQ);
		request.put(XHRAtrbt.hostUin, QQ);
		request.put(XHRAtrbt.inCharset, "utf-8");
		request.put(XHRAtrbt.outCharset, "utf-8");
		request.put(XHRAtrbt.notice, "0");
		request.put(XHRAtrbt.sort, "0");
		request.put(XHRAtrbt.pos, String.valueOf((page - 1) * EACH_PAGE_LIMIT));
		request.put(XHRAtrbt.num, String.valueOf(EACH_PAGE_LIMIT));
		request.put(XHRAtrbt.cgi_host, URL.MOOD_DOMAIN);
		request.put(XHRAtrbt.code_version, "1");
		request.put(XHRAtrbt.format, "jsonp");
		request.put(XHRAtrbt.need_private_comment, "1");
		request.put(XHRAtrbt.g_tk, gtk);
		request.put(XHRAtrbt.qzonetoken, qzoneToken);
		return request;
	}
	
	/**
	 * 下载每一页的说说及其照片信息
	 * @param pageAndPhotos 说说及照片集
	 */
	private static void download(List<Mood> moods) {
		UIUtils.log("所有【说说】图文信息提取完成, 开始下载...");
		
		int idx = 1;
		for(Mood mood : moods) {
			
			UIUtils.log("正在下载说说(图片x", mood.PIC_SIZE(), "): ", mood.CONTENT());
			int picCnt = _download(mood);
			boolean isOk = (picCnt == mood.PIC_SIZE());
			UIUtils.log(" 下载图片", (isOk ? "成功" : "失败"), ": ", picCnt, "/", mood.PIC_SIZE());
			UIUtils.log(" 当前总进度: ", idx++, "/", moods.size());
			
			// 保存下载信息
			String fileName = StrUtils.concat(PAGE_DIR_PREFIX, mood.PAGE(), "/", MOOD_NAME);
			FileUtils.write(fileName, mood.toString(isOk), Charset.UTF8, true);
		}
	}
	
	/**
	 * 下载说说图文
	 * @param mood 说说信息
	 * @return 成功下载的图片数
	 */
	private static int _download(Mood mood) {
		Map<String, String> header = _toMoodHeader(Browser.COOKIE());

		int idx = 0, cnt = 0;
		for(String picURL : mood.getPicURLs()) {
			String picName = PicUtils.getPicName(String.valueOf(idx++), mood.CONTENT());
			cnt += (_download(header, mood.PAGE(), picName, picURL) ? 1 : 0);
			ThreadUtils.tSleep(SLEEP_TIME);
		}
		return cnt;
	}
	
	/**
	 * 下载单张图片到说说的分页目录，并复制到图片合集目录
	 * @param header
	 * @param pageNum
	 * @param picName
	 * @param picURL
	 * @return
	 */
	private static boolean _download(Map<String, String> header, 
			String pageNum, String picName, String picURL) {
		header.put(HttpUtils.HEAD.KEY.HOST, XHRUtils.toHost(picURL));
		
		boolean isOk = false;
		String savePath = StrUtils.concat(PAGE_DIR_PREFIX, pageNum, "/", picName);
		for(int retry = 0; !isOk && retry < 3; retry++) {
			isOk = HttpURLUtils.downloadByGet(savePath, picURL, header, null, 
					TIMEOUT, TIMEOUT, Charset.UTF8);
			if(isOk == false) {
				FileUtils.delete(savePath);
				
			} else {
				FileUtils.copyFile(savePath, PHOTO_DIR.concat(picName));
			}
		}
		return isOk;
	}
	
}
