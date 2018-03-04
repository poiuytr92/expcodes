package exp.libs.warp.net.http.cookie;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 单个会话的cookie集
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class HttpCookie {
	
	/** 换行符 */
	protected final static String LFCR = "\r\n";
	
	/** cookie属性集 */
	protected List<_HttpCookie> cookies;
	
	/** 多个cookie的NV组合而成的NV串 */
	protected String nvCookies;
	
	/** cookies是否发生变化 */
	protected boolean isChanged;
	
	/**
	 * 构造函数
	 */
	public HttpCookie() {
		this.cookies = new LinkedList<_HttpCookie>();
		this.nvCookies = "";
		this.isChanged = false;
	}
	
	/**
	 * 构造函数
	 * @param headerCookies 多个HTTP响应头中的 Set-Cookie（换行分隔）, 格式如：
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
	 * cookies是否有效
	 * @return true:有效; false:无效
	 */
	public boolean isVaild() {
		return (cookies.size() > 0);
	}
	
	/**
	 * 添加一个Set-Cookie串
	 * @param headerCookie HTTP响应头中的 Set-Cookie, 格式如：
	 * 	JSESSIONID=4F12EEF0E5CC6E8B239906B29919D40E; Domain=www.baidu.com; Path=/; Expires=Mon, 29-Jan-2018 09:08:16 GMT+08:00; Secure; HttpOnly; 
	 */
	public void add(String headerCookie) {
		add(new _HttpCookie(headerCookie));
	}
	
	/**
	 * 添加一个cookie对象
	 * @param cookie
	 */
	public void add(_HttpCookie cookie) {
		if(cookie != null && cookie.isVaild()) {
			isChanged = true;
			cookies.add(cookie);
			
			takeCookieNVE(cookie.getName(), cookie.getValue(), cookie.getExpiry());
		}
	}
	
	/**
	 * 在添加新的cookie时会触发此方法, 用于提取某些特殊的名值对作为常量, 例如CSRF
	 * @param name cookie键名
	 * @param value cookie键值
	 * @param expires cookie有效期
	 */
	protected void takeCookieNVE(String name, String value, Date expires) {
		// Undo
	}
	
	/**
	 * 生成所有cookie的名值对列表(分号分隔)
	 * @return cookie的名值对列表(分号分隔)
	 */
	public String toNVCookie() {
		if(isChanged == true) {
			
			StringBuilder kvs = new StringBuilder();
			for(_HttpCookie cookie : cookies) {
				kvs.append(cookie.toNV()).append("; ");
			}
			nvCookies = kvs.toString();
		}
		return nvCookies;
	}
	
	/**
	 * 生成所有cookie在Header中的字符串形式(换行符分隔)
	 * @return 形如：
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
	
	@Override
	public String toString() {
		return toHeaderCookie();
	}
	
}
