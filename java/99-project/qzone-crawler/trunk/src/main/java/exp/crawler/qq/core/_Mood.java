package exp.crawler.qq.core;

import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.webkit.WebBrowser;

public class _Mood {

	private final static Logger log = LoggerFactory.getLogger(MoodAnalyzer.class);
	
	private final static String INFO_DIR = "./data/page/";
	
	private final static String INFO_PATH_PREFIX = "page-info-";
	
	private final static String INFO_PATH_SUFFIX = ".txt";
	
	private final static String LINE_END = "\r\n";
	
	private final static String MSG_SPLIT = "##==========";
	
	private final static String IMAGE_DIR = "./data/img/";
	
	private final static String IMAGE_SUFFIX = ".jpg";
	
	private final static String IMAGE_ALL_DIR = "./data/img/all-pic/";
	
	/**
	 * 捕获在线数据, 存储图文信息到本地，作为离线数据备用
	 * @param loginPage
	 * @param username
	 * @param password
	 * @param targetQQ
	 * @param totalPageNum
	 */
	public static void catchOnlineInfo(WebBrowser bDriver, 
			String username, String password, 
			String targetQQ, int totalPageNum) {
		WebDriver driver = bDriver.getDriver();
		String gtk = Browser.GTK();
		
		if(StrUtils.isNotEmpty(gtk)) {
			UIUtils.log("登陆成功： 开始抓取 【说说】 数据...");
			FileUtils.delete(INFO_DIR);	// 删除旧数据
			
			for(int idx = 0; idx < totalPageNum; idx++) {
				UIUtils.log("正在抓取第 [", (idx + 1), "] 页的图文数据...");
				String pageUrl = getPageUrl(targetQQ, idx, gtk);
				driver.get(pageUrl);
				
				String pageData = driver.getPageSource();
				boolean isEnd = parse(idx, pageData);
				UIUtils.log("抓取第 [", (idx + 1), "] 页的图文数据完成.");
				
				if(isEnd) {
					break;
				}
			}
		} else {
			UIUtils.log("登陆失败： 无法获得 [g_tk] 码, 本次操作终止.");
		}
		
		bDriver.quit();
	}
	
	/**
	 * 获取【说说】的翻页地址
	 * @param qqNum
	 * @param pageNum
	 * @param token 似乎每次登陆都不同，但该次登陆之后就固定了
	 * @return
	 */
	protected static String getPageUrl(String qqNum, int pageNum, String token) {
		final int EACH_PAGE_LIMIT = 20;
		int startIdx = pageNum * EACH_PAGE_LIMIT;
		
		String url = StrUtils.concat("https://h5.qzone.qq.com/proxy/domain/", 
				"taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin=", qqNum,  
				"&inCharset=utf-8&outCharset=utf-8&hostUin=", qqNum,  
				"&notice=0&sort=0&pos=", startIdx, "&num=", EACH_PAGE_LIMIT,  
				"&cgi_host=http%3A%2F%2Ftaotao.qq.com%2Fcgi-bin", 
				"%2Femotion_cgi_msglist_v6&code_version=1&format=jsonp", 
				"&need_private_comment=1&g_tk=", token);
		return url;
	}
	
	/**
	 * 解析页面数据，存储到本地备用
	 * @param pageIdx
	 * @param pageData
	 */
	protected static boolean parse(int pageIdx, String pageData) {
		String filePath = getSaveFilePath(pageIdx);
		
		boolean isEnd = false;
		try {
			String json = getJson(pageData);
			JSONObject jsonObj = JSONObject.fromObject(json);
			JSONArray msglist = jsonObj.getJSONArray("msglist");
			for(int i = 0; i < msglist.size(); i++) {
				JSONObject msg = msglist.getJSONObject(i);
				save(msg, filePath);
			}
		} catch (Exception e) {
			
			// 已到了最末页
			if("JSONObject[\"msglist\"] is not a JSONArray.".equals(e.getMessage())) {
				isEnd = true;
				
			} else {
				log.error("抓取第 [{}] 页的图文数据失败.", pageIdx, e);
			}
		}
		return isEnd;
	}
	
	private static String getSaveFilePath(int pageIdx) {
		String filePath = StrUtils.concat(
				INFO_DIR, INFO_PATH_PREFIX, 
				StrUtils.leftPad(String.valueOf(pageIdx), '0', 3), 
				INFO_PATH_SUFFIX);
		return filePath;
	}
	
	/**
	 * 从html页面中截取真正有用的 json数据
	 * @param pageData
	 * @return
	 */
	private static String getJson(String pageData) {
		String regex = ">_Callback\\(([\\s\\S]+)\\);</pre></body></html>";
		String json = RegexUtils.findFirst(pageData, regex).replace("\\/", "/");
		return json;
	}
	
	private static void save(JSONObject msg, String filePath) {
		StringBuilder sb = new StringBuilder();
		
		String content = msg.getString("content").replaceAll("[\r|\n]", "").trim();
		sb.append(StrUtils.isEmpty(content) ? "null" : content).append(LINE_END);
		
		long createTime = msg.getLong("created_time") * 1000;
		sb.append(TimeUtils.toStr(createTime, "yyyyMMddHHmmss")).append(LINE_END);
		
		try {
			JSONArray pics = msg.getJSONArray("pic");
			for(int i = 0; i < pics.size(); i++) {
				JSONObject pic = pics.getJSONObject(i); 
				String url = pic.getString("url3");
				sb.append(url).append(LINE_END);
			}
		} catch (Exception e) {
			// Undo 不存在节点时会报错, 图片节点不一定存在，可忽略
		}
		
		sb.append(MSG_SPLIT).append(LINE_END);
		FileUtils.write(filePath, sb.toString(), Charset.UTF8, true);
	}
	
	/**
	 * 根据捕获的离线数据，下载图文信息并归整
	 */
	public static void downloadDatas() {
		int picNum = 0;
		UIUtils.log("准备下载图文信息, 正在删除历史数据...");
		FileUtils.delete(IMAGE_DIR);
		
		UIUtils.log("开始下载图文信息...");
		File pageInfoDir = new File(INFO_DIR);
		File[] pageInfos = pageInfoDir.listFiles();
		for(File pageInfo : pageInfos) {
			if(pageInfo.getName().startsWith(INFO_PATH_PREFIX) && 
					pageInfo.getName().endsWith(INFO_PATH_SUFFIX)) {
				picNum += down(pageInfo);
			}
		}
		UIUtils.log("下载所有图文信息完成, 共下载 [", picNum, "] 张图片.");
	}
	
	/**
	 * 解析并下载离线的页面信息文件
	 * @param pageInfo
	 */
	protected static int down(File pageInfo) {
		int picNum = 0;
		String fileName = pageInfo.getName().replace(INFO_PATH_SUFFIX, "");
		UIUtils.log("正在下载 [", fileName, "] 记录的图文信息...");
		
		String info = FileUtils.read(pageInfo, Charset.UTF8);
		String[] msgs = info.split(MSG_SPLIT);
		for(int idx = 0; idx < msgs.length; idx++) {
			String msg = msgs[idx].trim();
			if(StrUtils.isEmpty(msg)) {
				continue;
			}
			String msgPath = StrUtils.concat(fileName, "/", 
					StrUtils.leftPad(String.valueOf(idx), '0', 2));
			picNum += down(msgPath, msg);
		}
		UIUtils.log("下载 [", fileName, "] 记录的图文信息完成, 本页共 [", picNum, "] 张图片.");
		return picNum;
	}
	
	/**
	 * 下载单条【说说】信息
	 * @param msg
	 */
	private static int down(String filePath, String msg) {
		int picNum = 0;
		String saveDir = StrUtils.concat(IMAGE_DIR, filePath, "/");
		
		// 保存【说说】原文
		String msgPath = StrUtils.concat(saveDir, "msg.txt");
		FileUtils.write(msgPath, msg, Charset.UTF8, false);
		
		// 保存【说说】图片
		String[] lines = msg.split(LINE_END);
		String createTime = lines[1];
		
		UIUtils.log("正在下载说说 [", lines[0], "] 记录的图文信息...");
		if(lines.length > 2) {
			for(int i = 2; i < lines.length; i++) {
				String line = lines[i].trim();
				if(!line.startsWith("http")) {
					continue;
				}
				
				String picName = StrUtils.concat(createTime, "-", i - 2, IMAGE_SUFFIX);
				String savePath = StrUtils.concat(saveDir, picName);
				HttpURLUtils.downloadByGet(savePath, line, null, null);
				picNum++;
			}
		}
		UIUtils.log("下载图文信息完成, 本条【说说】共 [", picNum, "] 张图片.");
		return picNum;
	}
	
	/**
	 * 把所有下载的图片复制到同一个目录
	 */
	public static void copyTogether() {
		FileUtils.delete(IMAGE_ALL_DIR);
		FileUtils.createDir(IMAGE_ALL_DIR);
		
		UIUtils.log("正在复制 [", IMAGE_DIR, "] 下的所有图片到 [", IMAGE_ALL_DIR, "] ...");
		copy(new File(IMAGE_DIR));
		UIUtils.log("复制完成.");
	}
	
	private static void copy(File file) {
		if(file.isFile()) {
			if(file.getName().endsWith(IMAGE_SUFFIX)) {
				FileUtils.copyFile(file, 
						new File(StrUtils.concat(IMAGE_ALL_DIR, file.getName())));
			}
		} else {
			File[] files = file.listFiles();
			for(File f : files) {
				copy(f);
			}
		}
	}
}
