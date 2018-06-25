package exp.libs.warp.net.cookie;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 单个会话的cookie集
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class HttpCookie {
	
	/** 换行�? */
	protected final static String LFCR = "\r\n";
	
	/** cookie属性集 */
	protected List<_HttpCookie> cookies;
	
	/** 多个cookie的NV组合而成的NV�? */
	protected String nvCookies;
	
	/**
	 * 构造函�?
	 */
	public HttpCookie() {
		this.cookies = new LinkedList<_HttpCookie>();
		this.nvCookies = "";
		init();
	}
	
	/**
	 * 构造函�?
	 * @param headerCookies 多个HTTP响应头中�? Set-Cookie（换行分隔）, 格式如：
	 * 	sid=iji8r99z ; Domain=www.baidu.com ; Path=/ ; Expires=Thu, 31-Jan-2019 21:18:46 GMT+08:00 ; 
	 * 	JSESSIONID=87E6F83AD8F5EC3C1BF1B08736E8D28A ; Domain= ; Path=/ ; Expires=Wed, 31-Jan-2018 21:18:43 GMT+08:00 ; HttpOnly ; 
	 * 	DedeUserID__ckMd5=14ad42f429c3e8b7 ; Domain=www.baidu.com ; Path=/ ; Expires=Fri, 02-Mar-2018 21:18:46 GMT+08:00 ; 
	 */
	public HttpCookie(String headerCookies) {
		this();
		
		if(StrUtils.isNotEmpty(headerCookies)) {
			String[] lines = headerCookies.split(LFCR);
			for(int i = 0; i < lines.length; i++) {
				add(lines[i]);
			}
		}
	}
	
	/**
	 * 初始�?.
	 *  此方法在{@link takeCookieNVE()}之前执行
	 */
	protected abstract void init();
	
	/**
	 * cookies是否有效
	 * @return true:有效; false:无效
	 */
	public boolean isVaild() {
		return (cookies.size() > 0);
	}
	
	/**
	 * 添加一个Set-Cookie�?
	 * @param headerCookie HTTP响应头中�? Set-Cookie, 格式如：
	 * 	JSESSIONID=4F12EEF0E5CC6E8B239906B29919D40E; Domain=www.baidu.com; Path=/; Expires=Mon, 29-Jan-2018 09:08:16 GMT+08:00; Secure; HttpOnly; 
	 */
	public boolean add(String headerCookie) {
		return add(new _HttpCookie(headerCookie));
	}
	
	/**
	 * 添加一个cookie对象
	 * @param cookie
	 */
	protected boolean add(_HttpCookie cookie) {
		boolean isOk = false;
		if(cookie != null && cookie.isVaild()) {
			isOk = takeCookieNVE(
					cookie.getName(), cookie.getValue(), cookie.getExpiry());
			if(isOk == true) {
				cookies.add(cookie);
			}
		}
		return isOk;
	}
	
	/**
	 * 在添加新的cookie时会触发此方�?, 用于提取某些特殊的名值对作为常量
	 * @param name cookie键名
	 * @param value cookie键�?
	 * @param expires cookie有效�?
	 * return true:保留该cookie; false;丢弃该cookie
	 */
	protected boolean takeCookieNVE(String name, String value, Date expires) {
		// Undo 仅在子类实现
		return true;
	}
	
	/**
	 * 生成所有cookie的名值对列表(分号分隔)
	 * @return cookie的名值对列表(分号分隔)
	 */
	public String toNVCookie() {
		StringBuilder kvs = new StringBuilder();
		for(_HttpCookie cookie : cookies) {
			kvs.append(cookie.toNV()).append("; ");
		}
		nvCookies = kvs.toString();
		return nvCookies;
	}
	
	/**
	 * 生成所有cookie在Header中的字符串形�?(换行符分�?)
	 * @return 形如�?
	 * 	sid=iji8r99z ; Domain=www.baidu.com ; Path=/ ; Expires=Thu, 31-Jan-2019 21:18:46 GMT+08:00 ; 
	 * 	JSESSIONID=87E6F83AD8F5EC3C1BF1B08736E8D28A ; Domain= ; Path=/ ; Expires=Wed, 31-Jan-2018 21:18:43 GMT+08:00 ; HttpOnly ; 
	 * 	DedeUserID__ckMd5=14ad42f429c3e8b7 ; Domain=www.baidu.com ; Path=/ ; Expires=Fri, 02-Mar-2018 21:18:46 GMT+08:00 ; 
	 */
	public String toHeaderCookie() {
		StringBuilder sb = new StringBuilder();
		for(_HttpCookie cookie : cookies) {
			sb.append(cookie.toString()).append(LFCR);
		}
		return sb.toString();
	}
	
	/**
	 * 获取cookie数量
	 * @return
	 */
	public int size() {
		return cookies.size();
	}
	
	/**
	 * 清除cookies
	 */
	public void clear() {
		cookies.clear();
	}
	
	@Override
	public String toString() {
		return toHeaderCookie();
	}
	
}
