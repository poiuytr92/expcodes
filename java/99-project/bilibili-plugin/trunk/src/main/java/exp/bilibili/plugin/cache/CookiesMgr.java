package exp.bilibili.plugin.cache;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.HttpCookie;
import exp.bilibili.plugin.envm.Level;
import exp.bilibili.plugin.envm.LoginType;
import exp.bilibili.protocol.XHRSender;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

public class CookiesMgr {

	/** cookie保存目录 */
	private final static String COOKIE_DIR = Config.getInstn().COOKIE_DIR();
	
	/**  文件名后缀 */
	private final static String SUFFIX = ".dat";
	
	/** 主号cookie文件路径 */
	private final static String COOKIE_MAIN_PATH = PathUtils.combine(COOKIE_DIR, 
			StrUtils.concat("cookie-main", SUFFIX));
	
	/** 马甲号cookie文件路径 */
	private final static String COOKIE_VEST_PATH = PathUtils.combine(COOKIE_DIR, 
			StrUtils.concat("cookie-vest", SUFFIX));
	
	/** 小号cookie文件名前缀 */
	private final static String COOKIE_MINI_PREFIX = "cookie-mini-";
	
	/** 上限保存的小号Cookie个数 */
	public final static int MAX_NUM = Config.LEVEL >= Level.ADMIN ? 99 : (
			Config.LEVEL >= Level.UPLIVE ? 8 : 3);
	
	/** 主号cookie */
	private HttpCookie mainCookie;
	
	/** 马甲号cookie */
	private HttpCookie vestCookie;
	
	/** 小号cookie集 */
	private Set<HttpCookie> miniCookies;
	
	/** 所有cookie保存路径 */
	private Map<HttpCookie, String> cookiePaths;
	
	/** 最近一次添加过cookie的时间点 */
	private long lastAddCookieTime;
	
	private static volatile CookiesMgr instance;
	
	private CookiesMgr() {
		this.mainCookie = HttpCookie.NULL;
		this.vestCookie = HttpCookie.NULL;
		this.miniCookies = new HashSet<HttpCookie>();
		this.cookiePaths = new HashMap<HttpCookie, String>();
		this.lastAddCookieTime = System.currentTimeMillis();
	}
	
	public static CookiesMgr INSTN() {
		if(instance == null) {
			synchronized (CookiesMgr.class) {
				if(instance == null) {
					instance = new CookiesMgr();
				}
			}
		}
		return instance;
	}
	
	public boolean add(HttpCookie cookie, LoginType type) {
		boolean isOk = false;
		if(cookie == null || cookie == HttpCookie.NULL) {
			return isOk;
		}
		
		cookie.setType(type);
		if(LoginType.MAIN == type) {
			this.mainCookie = cookie;
			isOk = save(cookie, COOKIE_MAIN_PATH);
			
		} else if(LoginType.VEST == type) {
			this.vestCookie = cookie;
			isOk = save(cookie, COOKIE_VEST_PATH);
			
		} else {
			if(miniCookies.size() < MAX_NUM) {
				String cookiePath = cookiePaths.get(cookie);
				if(cookiePath == null) {
					cookiePath = PathUtils.combine(COOKIE_DIR, StrUtils.concat(
							COOKIE_MINI_PREFIX, cookie.UID(), SUFFIX));
				}
				
				this.miniCookies.add(cookie);
				isOk = save(cookie, cookiePath);
			}
		}
		return isOk;
	}
	
	private boolean save(HttpCookie cookie, String cookiePath) {
		cookiePaths.put(cookie, cookiePath);
		String data = CryptoUtils.toDES(cookie.toString());
		boolean isOk = FileUtils.write(cookiePath, data, Charset.ISO, false);
		if(isOk == true) {
			lastAddCookieTime = System.currentTimeMillis();
		}
		return isOk;
	}
	
	public boolean load(LoginType type) {
		boolean isOk = false;
		if(LoginType.MAIN == type) {
			mainCookie = load(COOKIE_MAIN_PATH, type);
			isOk = (mainCookie != HttpCookie.NULL);
			
		} else if(LoginType.VEST == type) {
			vestCookie = load(COOKIE_VEST_PATH, type);
			isOk = (vestCookie != HttpCookie.NULL);
			
		} else {
			File dir = new File(COOKIE_DIR);
			String[] fileNames = dir.list();
			for(String fileName : fileNames) {
				if(fileName.contains(COOKIE_MINI_PREFIX) && miniCookies.size() < MAX_NUM) {
					String cookiePath = PathUtils.combine(dir.getPath(), fileName);
					HttpCookie miniCookie = load(cookiePath, type);
					if(HttpCookie.NULL != null) {
						miniCookies.add(miniCookie);
						isOk = true;
					}
				}
			}
		}
		return isOk;
	}
	
	private HttpCookie load(String cookiePath, LoginType type) {
		HttpCookie cookie = HttpCookie.NULL;
		if(FileUtils.exists(cookiePath)) {
			String data = CryptoUtils.deDES(FileUtils.read(cookiePath, Charset.ISO));
			if(StrUtils.isNotEmpty(data)) {
				cookie = new HttpCookie(data);
				cookie.setType(type);
				
				if(checkLogined(cookie) && !cookiePaths.containsKey(cookie)) {
					cookiePaths.put(cookie, cookiePath);
					lastAddCookieTime = System.currentTimeMillis();
				} else {
					cookie = HttpCookie.NULL;
					FileUtils.delete(cookiePath);
				}
			}
		}
		return cookie;
	}
	
	public boolean del(HttpCookie cookie) {
		boolean isOk = false;
		if(cookie == null || cookie == HttpCookie.NULL) {
			return isOk;
		}
		
		if(LoginType.MAIN == cookie.TYPE()) {
			this.mainCookie = HttpCookie.NULL;
			
		} else if(LoginType.VEST == cookie.TYPE()) {
			this.vestCookie = HttpCookie.NULL;
			
		} else {
			this.miniCookies.remove(cookie);
		}
		
		String cookiePath = cookiePaths.remove(cookie);
		return FileUtils.delete(cookiePath);
	}

	public HttpCookie MAIN() {
		return mainCookie;
	}

	public HttpCookie VEST() {
		return vestCookie;
	}
	
	public Iterator<HttpCookie> MINIs() {
		List<HttpCookie> cookies = new LinkedList<HttpCookie>();
		Iterator<HttpCookie> minis = miniCookies.iterator();
		for(int i = 0; i < MAX_NUM; i++) {
			if(minis.hasNext()) {
				cookies.add(minis.next());
			}
		}
		return cookies.iterator();
	}
	
	public Iterator<HttpCookie> ALL() {
		List<HttpCookie> cookies = new LinkedList<HttpCookie>();
		if(HttpCookie.NULL != mainCookie) { cookies.add(mainCookie); }
		if(HttpCookie.NULL != vestCookie) { cookies.add(vestCookie); }
		
		Iterator<HttpCookie> minis = miniCookies.iterator();
		for(int i = 0; i < MAX_NUM; i++) {
			if(minis.hasNext()) {
				cookies.add(minis.next());
			}
		}
		return cookies.iterator();
	}
	
	public long getLastAddCookieTime() {
		return lastAddCookieTime;
	}
	
	public int size() {
		int size = 0;
		size += (mainCookie != HttpCookie.NULL ? 1 : 0);
		size += (vestCookie != HttpCookie.NULL ? 1 : 0);
		size += miniSize();
		return size;
	}
	
	public int miniSize() {
		return miniCookies.size();
	}
	
	public static boolean clearMainAndVestCookies() {
		boolean isOk = FileUtils.delete(COOKIE_MAIN_PATH);
		isOk &= FileUtils.delete(COOKIE_VEST_PATH);
		return isOk;
	}
	
	public static boolean clearAllCookies() {
		boolean isOk = FileUtils.delete(COOKIE_DIR);
		isOk &= (FileUtils.createDir(COOKIE_DIR) != null);
		return isOk;
	}
	
	/**
	 * 检查cookie是否可以登陆成功
	 *  若成功则把账号ID和昵称也更新到cookie中
	 * @param cookie
	 * @return
	 */
	public static boolean checkLogined(HttpCookie cookie) {
		return (HttpCookie.NULL != cookie && XHRSender.queryUserInfo(cookie));
	}
	
}
