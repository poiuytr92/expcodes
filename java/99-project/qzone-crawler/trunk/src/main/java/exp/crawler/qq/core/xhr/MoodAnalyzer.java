package exp.crawler.qq.core.xhr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.crawler.qq.Config;
import exp.crawler.qq.bean.Mood;
import exp.crawler.qq.bean.QQCookie;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.envm.XHRAtrbt;
import exp.crawler.qq.utils.PicUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.crawler.qq.utils.XHRUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 【空间说说】解析器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MoodAnalyzer {
	
	/** 说说分页信息保存文件名 */
	private final static String MOOD_NAME = "MoodInfo-[说说信息].txt";
	
	/** 被爬取数据的目标QQ */
	private final String QQ;
	
	/** 说说保存目录 */
	private final String MOOD_DIR;
	
	/** 说说每页图文信息的保存路径前缀 */
	private final String PAGE_DIR_PREFIX;
	
	/** 说说所有照片的保存目录 */
	private final String PHOTO_DIR;
	
	/**
	 * 构造函数
	 * @param QQ 被爬取数据的目标QQ
	 */
	public MoodAnalyzer(String QQ) {
		this.QQ = StrUtils.isTrimEmpty(QQ) ? "0" : QQ;
		this.MOOD_DIR = StrUtils.concat(Config.DATA_DIR, this.QQ, "/mood/");
		this.PAGE_DIR_PREFIX = MOOD_DIR.concat("content/page-");
		this.PHOTO_DIR = MOOD_DIR.concat("photos/");
	}
	
	/**
	 * 执行空间说说解析, 并下载所有说说及相关照片
	 */
	public void execute() {
		try {
			
			// 清除上次下载的数据
			FileUtils.delete(MOOD_DIR);
			FileUtils.createDir(MOOD_DIR);
			
			// 下载说说及照片
			download(getMoods());
			UIUtils.log("任务完成: QQ [", QQ, "] 的空间说说已保存到 [", MOOD_DIR, "]");
			
		} catch(Exception e) {
			UIUtils.log(e, "任务失败: 下载 QQ [", QQ, "] 的空间说说时发生异常");
		}
	}
	
	/**
	 * 提取所有说说及相关的照片信息
	 * @return
	 */
	private List<Mood> getMoods() {
		List<Mood> moods = new LinkedList<Mood>();
		UIUtils.log("正在提取QQ [", QQ, "] 的说说列表...");
		
		final int PAGE_NUM = _getPageNum();
		for(int page = 1; page <= PAGE_NUM; page++) {
			UIUtils.log(" -> 正在提取第 [", page, "] 页的说说信息...");
			List<Mood> pageMoods = _getPageMoods(page);
			moods.addAll(pageMoods);
			
			UIUtils.log(" -> 第 [", page, "/", PAGE_NUM, 
					"] 页说说提取完成, 累计说说数量: ", moods.size());
			ThreadUtils.tSleep(Config.SLEEP_TIME);
		}
		return moods;
	}
	
	/**
	 * 获取说说总页数
	 * @return
	 */
	private int _getPageNum() {
		String response = _getPageMoodJson(1);
		int total = 0;
		try {
			JSONObject json = JSONObject.fromObject(response);
			total = JsonUtils.getInt(json, XHRAtrbt.total, 0);	// 总说说数量
		} catch(Exception e) {}
		return PicUtils.getPageNum(total, Config.BATCH_LIMT);
	}
	
	/**
	 * 获取分页的说说内容
	 * @param page 页码
	 * @return 
	 */
	private List<Mood> _getPageMoods(int page) {
		List<Mood> moods = new LinkedList<Mood>();
		String response = _getPageMoodJson(page);
		
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
	 * 获取分页的说说的Json
	 * @param QQ 目标QQ
	 * @param page 页数
	 * @return json
	 */
	private String _getPageMoodJson(int page) {
		Map<String, String> header = _getMoodHeader(Browser.COOKIE());
		Map<String, String> request = _getMoodRequest(Browser.GTK(), Browser.QZONE_TOKEN(), page);
		String response = HttpURLUtils.doGet(URL.MOOD_URL, header, request);
		return XHRUtils.toJson(response);
	}
	
	/**
	 * 分页说说请求头
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> _getMoodHeader(QQCookie cookie) {
		Map<String, String> header = XHRUtils.getHeader(cookie);
		header.put(HttpUtils.HEAD.KEY.REFERER, URL.MOOD_REFERER);
		return header;
	}
	
	/**
	 * 分页说说请求参数
	 * @param gtk
	 * @param qzoneToken
	 * @param page
	 * @return
	 */
	private Map<String, String> _getMoodRequest(
			String gtk, String qzoneToken, int page) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(XHRAtrbt.g_tk, gtk);
		request.put(XHRAtrbt.qzonetoken, qzoneToken);
		request.put(XHRAtrbt.uin, QQ);
		request.put(XHRAtrbt.hostUin, QQ);
		request.put(XHRAtrbt.pos, String.valueOf((page - 1) * Config.BATCH_LIMT));
		request.put(XHRAtrbt.num, String.valueOf(Config.BATCH_LIMT));
		request.put(XHRAtrbt.cgi_host, URL.MOOD_DOMAIN);
		request.put(XHRAtrbt.inCharset, Config.CHARSET);
		request.put(XHRAtrbt.outCharset, Config.CHARSET);
		request.put(XHRAtrbt.notice, "0");
		request.put(XHRAtrbt.sort, "0");
		request.put(XHRAtrbt.code_version, "1");
		request.put(XHRAtrbt.format, "jsonp");
		request.put(XHRAtrbt.need_private_comment, "1");
		return request;
	}
	
	/**
	 * 下载所有说说及相关的照片
	 * @param moods 说说集（含照片信息）
	 */
	private void download(List<Mood> moods) {
		UIUtils.log("提取QQ [", QQ, "] 的说说及照片完成, 开始下载...");
		
		int idx = 1;
		for(Mood mood : moods) {
			UIUtils.log("正在下载第 [", idx++, "/", moods.size(), "] 条说说: ", mood.CONTENT());
			int cnt = _download(mood);
			boolean isOk = (cnt == mood.PIC_NUM());
			UIUtils.log(" -> 说说照片下载完成, 成功率: ", cnt, "/", mood.PIC_NUM());
			
			// 保存下载信息
			String savePath = StrUtils.concat(PAGE_DIR_PREFIX, mood.PAGE(), "/", MOOD_NAME);
			FileUtils.write(savePath, mood.toString(isOk), Config.CHARSET, true);
		}
	}
	
	/**
	 * 下载单条说说及相关的照片
	 * @param mood 说说信息
	 * @return 成功下载的照片数
	 */
	private int _download(Mood mood) {
		Map<String, String> header = _getMoodHeader(Browser.COOKIE());

		int idx = 0, cnt = 0;
		for(String picURL : mood.getPicURLs()) {
			String picName = PicUtils.getPicName(String.valueOf(idx++), mood.CONTENT());
			boolean isOk = _download(header, mood.PAGE(), picName, picURL);
			cnt += (isOk ? 1 : 0);
			
			UIUtils.log(" -> 下载照片进度(", (isOk ? "成功" : "失败"), "): ", cnt, "/", mood.PIC_NUM());
			ThreadUtils.tSleep(Config.SLEEP_TIME);
		}
		return cnt;
	}
	
	/**
	 * 下载单张图片到说说的分页目录，并复制到图片合集目录
	 * @param header
	 * @param pageIdx 页码索引
	 * @param picName
	 * @param picURL
	 * @return
	 */
	private boolean _download(Map<String, String> header, 
			String pageIdx, String picName, String picURL) {
		header.put(HttpUtils.HEAD.KEY.HOST, XHRUtils.toHost(picURL));
		
		boolean isOk = false;
		String savePath = StrUtils.concat(PAGE_DIR_PREFIX, pageIdx, "/", picName);
		for(int retry = 0; !isOk && retry < Config.RETRY; retry++) {
			isOk = HttpURLUtils.downloadByGet(savePath, picURL, header, null, 
					Config.TIMEOUT, Config.TIMEOUT, Config.CHARSET);
			
			if(isOk == false) {
				FileUtils.delete(savePath);
			} else {
				FileUtils.copyFile(savePath, PHOTO_DIR.concat(picName));
			}
		}
		return isOk;
	}
	
}
