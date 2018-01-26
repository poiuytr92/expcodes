package exp.bilibili.protocol.cookie;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.utils.TimeUtils;
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
	private List<HttpCookie> miniCookies;
	
	private static volatile CookiesMgr instance;
	
	private CookiesMgr() {
		this.mainCookie = HttpCookie.NULL;
		this.vestCookie = HttpCookie.NULL;
		this.tempCookie = HttpCookie.NULL;
		this.miniCookies = new LinkedList<HttpCookie>();
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
		if(cookie == null || cookie == HttpCookie.NULL || !cookie.isVaild()) {
			return isOk;
		}
		
		if(LoginType.MAIN == type) {
			this.mainCookie = cookie;
			isOk = save(cookie, COOKIE_MAIN_PATH);
			
		} else if(LoginType.VEST == type) {
			this.vestCookie = cookie;
			isOk = save(cookie, COOKIE_VEST_PATH);
			
		} else if(LoginType.TEMP == type) {
			this.tempCookie = cookie;
			isOk = true;
			
		} else {
			this.miniCookies.add(cookie);
			isOk = save(cookie, PathUtils.combine(COOKIE_DIR, StrUtils.concat(
					COOKIE_MINI_PREFIX, TimeUtils.getSysDate("yyyyMMddHHmmSS"), SUFFIX)));
		}
		return isOk;
	}
	
	private boolean save(HttpCookie cookie, String cookiePath) {
		String data = CryptoUtils.toDES(cookie.toString());
		return FileUtils.write(cookiePath, data, Charset.ISO, false);
	}
	
	public boolean load(LoginType type) {
		boolean isOk = true;
		if(LoginType.MAIN == type) {
			mainCookie = load(COOKIE_MAIN_PATH);
			isOk = (mainCookie != HttpCookie.NULL);
			
		} else if(LoginType.VEST == type) {
			vestCookie = load(COOKIE_VEST_PATH);
			isOk = (vestCookie != HttpCookie.NULL);

		} else if(LoginType.TEMP == type) {
			isOk = (tempCookie != HttpCookie.NULL);
			
		} else {
			File dir = new File(COOKIE_DIR);
			String[] fileNames = dir.list();
			for(String fileName : fileNames) {
				if(fileName.contains(COOKIE_MINI_PREFIX)) {
					HttpCookie miniCookie = load(PathUtils.combine(dir.getPath(), fileName));
					if(miniCookie != HttpCookie.NULL) {
						miniCookies.add(miniCookie);
						isOk &= true;
						
					} else {
						isOk &= false;
					}
				}
			}
		}
		return isOk;
	}
	
	private HttpCookie load(String cookiePath) {
		HttpCookie cookie = HttpCookie.NULL;
		if(FileUtils.exists(cookiePath)) {
			String data = CryptoUtils.deDES(FileUtils.read(cookiePath, Charset.ISO));
			if(StrUtils.isNotEmpty(data)) {
				cookie = new HttpCookie(data);
			}
		}
		return cookie;
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
	 *  若成功则把昵称也更新到cookie中
	 * @param cookie
	 * @return
	 */
	public static boolean checkLogined(HttpCookie cookie) {
		String nickName = MsgSender.queryUsername(cookie);
		cookie.setNickName(nickName);
		return !cookie.isExpire();
	}
	
}
