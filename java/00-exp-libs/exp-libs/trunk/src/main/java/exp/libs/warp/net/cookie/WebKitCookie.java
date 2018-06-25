package exp.libs.warp.net.cookie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.Cookie;

/**
 * <PRE>
 * selenium的cookie集
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebKitCookie extends HttpCookie {

	/**
	 * 构造函�?
	 */
	public WebKitCookie() {
		super();
	}
	
	/**
	 * 构造函�?
	 * @param headerCookies 多个HTTP响应头中�? Set-Cookie（换行分隔）, 格式如：
	 * 	sid=iji8r99z ; Domain=www.baidu.com ; Path=/ ; Expires=Thu, 31-Jan-2019 21:18:46 GMT+08:00 ; 
	 * 	JSESSIONID=87E6F83AD8F5EC3C1BF1B08736E8D28A ; Domain= ; Path=/ ; Expires=Wed, 31-Jan-2018 21:18:43 GMT+08:00 ; HttpOnly ; 
	 * 	DedeUserID__ckMd5=14ad42f429c3e8b7 ; Domain=www.baidu.com ; Path=/ ; Expires=Fri, 02-Mar-2018 21:18:46 GMT+08:00 ; 
	 */
	public WebKitCookie(String headerCookies) {
		super(headerCookies);
	}
	
	/**
	 * 构造函�?
	 * @param cookies selenium的cookie�?
	 */
	public WebKitCookie(Collection<Cookie> cookies) {
		super();
		add(cookies);
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 添加多个selenium的cookie
	 * @param cookie
	 */
	public void add(Collection<Cookie> cookies) {
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				add(cookie);
			}
		}
	}
	
	/**
	 * 添加一个selenium的cookie
	 * @param cookie
	 */
	public void add(Cookie cookie) {
		add(new _WebKitCookie(cookie));
	}
	
	/**
	 * 添加一个webkit的cookie对象
	 * @param cookie
	 */
	public void add(_WebKitCookie cookie) {
		if(cookie != null && cookie.isVaild()) {
			cookies.add(cookie);
			takeCookieNVE(cookie.getName(), cookie.getValue(), cookie.getExpiry());
		}
	}
	
	/**
	 * 生成selenium的cookie�?
	 * @return selenium的cookie�?
	 */
	public Set<Cookie> toSeleniumCookies() {
		Set<Cookie> seleniumCookies = new HashSet<Cookie>();
		for(_HttpCookie cookie : cookies) {
			seleniumCookies.add(((_WebKitCookie) cookie).toSeleniumCookie());
		}
		return seleniumCookies;
	}

}
