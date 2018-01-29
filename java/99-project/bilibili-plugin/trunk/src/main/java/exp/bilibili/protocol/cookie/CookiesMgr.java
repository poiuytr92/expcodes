package exp.bilibili.protocol.cookie;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.protocol.envm.LoginType;
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
	
	/** 主号cookie */
	private HttpCookie mainCookie;
	
	/** 马甲号cookie */
	private HttpCookie vestCookie;
	
	/** 临时cookie */
	private HttpCookie tempCookie;
	
	/** 小号cookie集 */
	private Set<HttpCookie> miniCookies;
	
	/** 所有cookie保存路径 */
	private Map<HttpCookie, String> cookiePaths;
	
	private static volatile CookiesMgr instance;
	
	private CookiesMgr() {
		this.mainCookie = HttpCookie.NULL;
		this.vestCookie = HttpCookie.NULL;
		this.tempCookie = HttpCookie.NULL;
		this.miniCookies = new HashSet<HttpCookie>();
		this.cookiePaths = new HashMap<HttpCookie, String>();
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
			
		} else if(LoginType.TEMP == type) {
			this.tempCookie = cookie;
			isOk = true;	// 临时cookie无需写入外存
			
		} else {
			String cookiePath = cookiePaths.get(cookie);
			if(cookiePath == null) {
				cookiePath = PathUtils.combine(COOKIE_DIR, StrUtils.concat(
						COOKIE_MINI_PREFIX, cookie.UID(), SUFFIX));
			}
			
			this.miniCookies.add(cookie);
			isOk = save(cookie, cookiePath);
		}
		return isOk;
	}
	
	private boolean save(HttpCookie cookie, String cookiePath) {
		cookiePaths.put(cookie, cookiePath);
		String data = CryptoUtils.toDES(cookie.toString());
		return FileUtils.write(cookiePath, data, Charset.ISO, false);
	}
	
	public boolean load(LoginType type) {
		boolean isOk = true;
		if(LoginType.MAIN == type) {
			mainCookie = load(COOKIE_MAIN_PATH, type);
			isOk = checkLogined(mainCookie);
			if(isOk == false) {
				del(mainCookie);
			}
			
		} else if(LoginType.VEST == type) {
			vestCookie = load(COOKIE_VEST_PATH, type);
			isOk = checkLogined(vestCookie);
			if(isOk == false) {
				del(vestCookie);
			}

		} else if(LoginType.TEMP == type) {
			isOk = (tempCookie != HttpCookie.NULL);
			
		} else {
			File dir = new File(COOKIE_DIR);
			String[] fileNames = dir.list();
			for(String fileName : fileNames) {
				if(fileName.contains(COOKIE_MINI_PREFIX)) {
					String cookiePath = PathUtils.combine(dir.getPath(), fileName);
					
					HttpCookie miniCookie = load(cookiePath, type);
					if(miniCookie != HttpCookie.NULL) {
						if(checkLogined(miniCookie)) {
							miniCookies.add(miniCookie);
						} else {
							del(miniCookie);
						}
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
				cookiePaths.put(cookie, cookiePath);
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
			
		} else if(LoginType.TEMP == cookie.TYPE()) {
			this.tempCookie = HttpCookie.NULL;
			
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
	
	public HttpCookie TEMP() {
		return tempCookie;
	}

	public Iterator<HttpCookie> MINIs() {
		return miniCookies.iterator();
	}
	
	public Iterator<HttpCookie> ALL() {
		List<HttpCookie> cookies = new LinkedList<HttpCookie>();
		cookies.add(mainCookie);
		cookies.addAll(miniCookies);
		return cookies.iterator();
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
		return (HttpCookie.NULL != cookie && MsgSender.queryUserInfo(cookie));
	}
	
}
