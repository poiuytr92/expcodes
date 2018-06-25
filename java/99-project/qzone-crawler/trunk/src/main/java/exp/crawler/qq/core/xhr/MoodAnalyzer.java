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
import exp.crawler.qq.core.interfaze.BaseMoodAnalyzer;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.envm.XHRAtrbt;
import exp.crawler.qq.utils.PicUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.crawler.qq.utils.XHRUtils;
import exp.libs.envm.HttpHead;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.http.HttpURLUtils;

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
	
	/**
	 * 构造函�?
	 * @param QQ 被爬取数据的目标QQ
	 */
	public MoodAnalyzer(String QQ) {
		super(QQ);
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
		UIUtils.log("正在提取QQ [", QQ, "] 的说说动�?...");
		
		final int PAGE_NUM = _getPageNum();
		for(int page = 1; page <= PAGE_NUM; page++) {
			UIUtils.log(" -> 正在提取�? [", page, "/", PAGE_NUM, "] 页的说说信息...");
			List<Mood> pageMoods = _getPageMoods(page);
			moods.addAll(pageMoods);
			
			UIUtils.log(" -> �? [", page, "/", PAGE_NUM, 
					"] 页说说提取完�?, 累计说说数量: ", moods.size());
			ThreadUtils.tSleep(Config.SLEEP_TIME);
		}
		return moods;
	}
	
	/**
	 * 获取说说总页�?
	 * @return
	 */
	@Override
	protected int _getPageNum() {
		UIUtils.log("正在提取QQ [", QQ, "] 的说说页�?...");
		String response = _getPageMoodJson(1);
		int total = 0;
		try {
			JSONObject json = JSONObject.fromObject(response);
			total = JsonUtils.getInt(json, XHRAtrbt.total, 0);	// 总说说数�?
		} catch(Exception e) {
			UIUtils.log(e, "提取QQ [", QQ, "] 的说说页数失�?");
		}
		return PicUtils.getPageNum(total, Config.BATCH_LIMT);
	}
	
	/**
	 * 获取分页的说说内�?
	 * @param page 页码
	 * @return 
	 */
	@Override
	protected List<Mood> _getPageMoods(int page) {
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
			UIUtils.log(e, "提取�? [", page, "] 页的说说信息异常");
		}
		return moods;
	}
	
	/**
	 * 获取分页的说说的Json
	 * @param page 页码
	 * @return json
	 */
	private String _getPageMoodJson(int page) {
		Map<String, String> header = _getMoodHeader(Browser.COOKIE());
		Map<String, String> request = _getMoodRequest(Browser.GTK(), Browser.QZONE_TOKEN(), page);
		String response = HttpURLUtils.doGet(URL.MOOD_URL, header, request);
		return XHRUtils.toJson(response);
	}
	
	/**
	 * 分页说说请求�?
	 * @param cookie
	 * @return
	 */
	private static Map<String, String> _getMoodHeader(QQCookie cookie) {
		Map<String, String> header = XHRUtils.getHeader(cookie);
		header.put(HttpHead.KEY.REFERER, URL.MOOD_REFERER);
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
	
}
