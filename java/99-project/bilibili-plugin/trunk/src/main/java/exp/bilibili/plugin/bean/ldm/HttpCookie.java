package exp.bilibili.plugin.bean.ldm;

import java.util.LinkedList;
import java.util.List;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 单个账号的cookie集
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class HttpCookie {
	
	private final static String LFCR = "\r\n";
	
	private List<_HttpCookie> cookies;
	
	/** 多个cookies组合而成的NV串 */
	private String nvCookies;
	
	/** cookies是否发生变化 */
	private boolean isChanged;
	
	public HttpCookie() {
		this.cookies = new LinkedList<_HttpCookie>();
		this.nvCookies = "";
		isChanged = false;
	}
	
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
	
	public void add(String headerCookie) {
		add(new _HttpCookie(headerCookie));
	}
	
	public void add(_HttpCookie cookie) {
		if(cookie != null && cookie.isVaild()) {
			isChanged = true;
			cookies.add(cookie);
			
			taskNV(cookie.getName(), cookie.getValue());
		}
	}
	
	/**
	 * 在添加新的cookie时会触发此方法, 用于提取某些特殊的名值对作为常量, 例如CSRF
	 * @param name
	 * @param value
	 */
	protected void taskNV(String name, String value) {
		// Undo
	}
	
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
