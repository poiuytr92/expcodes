package exp.bilibili.plugin.cache;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.HttpCookie;
import exp.bilibili.plugin.envm.Level;
import exp.bilibili.plugin.envm.CookieType;
import exp.bilibili.protocol.XHRSender;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 账号cookie管理器
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
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
	
	/** 小号cookie保存路径 */
	private Map<HttpCookie, String> miniPaths;
	
	/** 最近一次添加过cookie的时间点 */
	private long lastAddCookieTime;
	
	/** 单例 */
	private static volatile CookiesMgr instance;
	
	/**
	 * 构造函数
	 */
	private CookiesMgr() {
		this.mainCookie = HttpCookie.NULL;
		this.vestCookie = HttpCookie.NULL;
		this.miniCookies = new HashSet<HttpCookie>();
		this.miniPaths = new HashMap<HttpCookie, String>();
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
	
	public boolean add(HttpCookie cookie, CookieType type) {
		boolean isOk = false;
		if(cookie == null || cookie == HttpCookie.NULL) {
			return isOk;
		}
		
		cookie.setType(type);
		if(CookieType.MAIN == type) {
			this.mainCookie = cookie;
			isOk = save(cookie, COOKIE_MAIN_PATH);
			
		} else if(CookieType.VEST == type) {
			this.vestCookie = cookie;
			isOk = save(cookie, COOKIE_VEST_PATH);
			
		} else {
			if(miniCookies.size() < MAX_NUM) {
				String cookiePath = miniPaths.get(cookie);
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
		if(cookie.TYPE() == CookieType.MINI) {
			miniPaths.put(cookie, cookiePath);
		}
		
		String data = CryptoUtils.toDES(cookie.toString());
		boolean isOk = FileUtils.write(cookiePath, data, Charset.ISO, false);
		if(isOk == true) {
			lastAddCookieTime = System.currentTimeMillis();
		}
		return isOk;
	}
	
	public boolean load(CookieType type) {
		boolean isOk = false;
		if(CookieType.MAIN == type) {
			mainCookie = load(COOKIE_MAIN_PATH, type);
			isOk = (mainCookie != HttpCookie.NULL);
			
		} else if(CookieType.VEST == type) {
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
	
	private HttpCookie load(String cookiePath, CookieType type) {
		HttpCookie cookie = HttpCookie.NULL;
		if(FileUtils.exists(cookiePath)) {
			String data = CryptoUtils.deDES(FileUtils.read(cookiePath, Charset.ISO));
			if(StrUtils.isNotEmpty(data)) {
				cookie = new HttpCookie(data);
				cookie.setType(type);
				
				if(checkLogined(cookie) == true) {
					if(cookie.TYPE() == CookieType.MINI && !miniPaths.containsKey(cookie)) {
						miniPaths.put(cookie, cookiePath);
					}
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
		
		String cookiePath = "";
		if(CookieType.MAIN == cookie.TYPE()) {
			this.mainCookie = HttpCookie.NULL;
			cookiePath = COOKIE_MAIN_PATH;
			
		} else if(CookieType.VEST == cookie.TYPE()) {
			this.vestCookie = HttpCookie.NULL;
			cookiePath = COOKIE_VEST_PATH;
			
		} else {
			this.miniCookies.remove(cookie);
			cookiePath = miniPaths.remove(cookie);
		}
		
		return FileUtils.delete(cookiePath);
	}

	public HttpCookie MAIN() {
		return mainCookie;
	}

	public HttpCookie VEST() {
		return vestCookie;
	}
	
	public Set<HttpCookie> MINIs() {
		Set<HttpCookie> cookies = new LinkedHashSet<HttpCookie>();
		Iterator<HttpCookie> minis = miniCookies.iterator();
		for(int i = 0; i < MAX_NUM; i++) {
			if(minis.hasNext()) {
				cookies.add(minis.next());
			}
		}
		return cookies;
	}
	
	public Set<HttpCookie> ALL() {
		Set<HttpCookie> cookies = new LinkedHashSet<HttpCookie>();
		if(HttpCookie.NULL != mainCookie) { cookies.add(mainCookie); }
		if(HttpCookie.NULL != vestCookie) { cookies.add(vestCookie); }
		cookies.addAll(MINIs());
		return cookies;
	}
	
	/**
	 * 持有cookie数
	 * @return
	 */
	public int size() {
		int size = 0;
		size += (mainCookie != HttpCookie.NULL ? 1 : 0);
		size += (vestCookie != HttpCookie.NULL ? 1 : 0);
		size += miniSize();
		return size;
	}
	
	/**
	 * 持有小号的cookie数
	 * @return
	 */
	public int miniSize() {
		return miniCookies.size();
	}
	
	/**
	 * 获取最近一次添加cookie的时间
	 * @return
	 */
	public long getLastAddCookieTime() {
		return lastAddCookieTime;
	}
	
	/**
	 * 清除主号和马甲号的cookies
	 * @return
	 */
	public static boolean clearMainAndVestCookies() {
		boolean isOk = FileUtils.delete(COOKIE_MAIN_PATH);
		isOk &= FileUtils.delete(COOKIE_VEST_PATH);
		return isOk;
	}
	
	/**
	 * 清除所有cookies
	 * @return
	 */
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
