package exp.libs.warp.net.cookie;

import java.util.Date;

import exp.libs.envm.DateFormat;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;

/**
 * <PRE>
 * 单个cookie属性集
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _HttpCookie {

	/** cookie键名 */
	protected String name;
	
	/** cookie键�? */
	protected String value;
	
	/** cookie域名 */
	protected final static String DOMAIN = "Domain";
	
	/** cookie域�? */
	protected String domain;
	
	/** cookie路径�? */
	protected final static String PATH = "Path";
	
	/** cookie路径�? */
	protected String path;
	
	/** cookie有效期名 */
	protected final static String EXPIRE = "Expires";
	
	/** cookie有效期值（英文GMT格式, �?: Thu, 01-Jan-1970 08:00:00 GMT+08:00�? */
	protected Date expiry;
	
	/** cookie属性：若出现该关键字表示该cookie只会在HTTPS中进行会话验�? */
	protected final static String SECURE = "Secure";
	
	/** 是否出现了Secure关键�? */
	protected boolean isSecure;
	
	/** cookie属性：若出现该关键字表示该cookie无法被JS等脚本读�?, 可防止XSS攻击 */
	protected final static String HTTPONLY = "HttpOnly";
	
	/** 是否出现了HttpOnly关键�? */
	protected boolean isHttpOnly;
	
	/**
	 * 构造函�?
	 */
	protected _HttpCookie() {
		this.name = "";
		this.value = "";
		this.domain = "";
		this.path = "";
		this.expiry = new Date();
		this.isSecure = false;
		this.isHttpOnly = false;
	}
	
	/**
	 * 构造函�?
	 * @param headerCookie HTTP响应头中�? Set-Cookie, 格式如：
	 * 	JSESSIONID=4F12EEF0E5CC6E8B239906B29919D40E; Domain=www.baidu.com; Path=/; Expires=Mon, 29-Jan-2018 09:08:16 GMT+08:00; Secure; HttpOnly; 
	 */
	protected _HttpCookie(String headerCookie) {
		this();
		init(headerCookie);
	}
	
	/**
	 * 通过HTTP响应头中的Set-Cookie串初始化
	 * @param headerCookie 格式如：JSESSIONID=4F12EEF0E5CC6E8B239906B29919D40E; Domain=www.baidu.com; Path=/; Expires=Mon, 29-Jan-2018 09:08:16 GMT+08:00; Secure; HttpOnly;
	 */
	private void init(String headerCookie) {
		String[] vals = headerCookie.split(";");
		for(int i = 0; i < vals.length; i++) {
			int idx = vals[i].indexOf('=');
			if(idx > 0) {
				String key = vals[i].substring(0, idx).trim();
				String val = vals[i].substring(idx + 1).trim();
				
				if(i == 0) {
					this.name = key;
					this.value = val;
					
				} else {
					if(DOMAIN.equalsIgnoreCase(key)) {
						this.domain = val;
						
					} else if(PATH.equalsIgnoreCase(key)) {
						this.path = val;
						
					} else if(EXPIRE.equalsIgnoreCase(key)) {
						this.expiry = TimeUtils.toDate(
								val.replace('-', ' '), DateFormat.GMT);
					}
				}
				
			} else {
				String key = vals[i].trim();
				if(SECURE.equalsIgnoreCase(key)) {
					this.isSecure = true;
					
				} else if(HTTPONLY.equalsIgnoreCase(key)) {
					this.isHttpOnly = true;
				}
			}
		}
	}
	
	/**
	 * cookie是否有效
	 * @return
	 */
	protected boolean isVaild() {
		return StrUtils.isNotEmpty(name);
	}
	
	/**
	 * 生成该Cookie的名值对
	 * 	在与服务端校验cookie会话�?, 只需对name与value属性进行校�?, 其他属性无需校验, 保存在本地即�?
	 * @return name=value
	 */
	protected String toNV() {
		return (!isVaild() ? "" : StrUtils.concat(name, "=", value));
	}

	/**
	 * 生成该cookie在Header中的字符串形�?
	 * @return 形如：JSESSIONID=4F12EEF0E5CC6E8B239906B29919D40E; Domain=www.baidu.com; Path=/; Expires=Mon, 29-Jan-2018 09:08:16 GMT+08:00; Secure; HttpOnly; 
	 */
	protected String toHeaderCookie() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("=").append(value).append(" ; ");
		sb.append(DOMAIN).append("=").append(domain).append(" ; ");
		sb.append(PATH).append("=").append(path).append(" ; ");
		sb.append(EXPIRE).append("=").append(TimeUtils.toExpires(expiry)).append(" ; ");
		if(isSecure == true) { sb.append(SECURE).append(" ; "); }
		if(isHttpOnly == true) { sb.append(HTTPONLY).append(" ; "); }
		return sb.toString();
	}
	
	protected String getName() {
		return name;
	}

	protected String getValue() {
		return value;
	}

	protected String getDomain() {
		return domain;
	}

	protected String getPath() {
		return path;
	}

	protected Date getExpiry() {
		return expiry;
	}

	protected boolean isSecure() {
		return isSecure;
	}

	protected boolean isHttpOnly() {
		return isHttpOnly;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setValue(String value) {
		this.value = value;
	}

	protected void setDomain(String domain) {
		this.domain = domain;
	}

	protected void setPath(String path) {
		this.path = path;
	}

	protected void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	protected void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}

	protected void setHttpOnly(boolean isHttpOnly) {
		this.isHttpOnly = isHttpOnly;
	}
	
	@Override
	public String toString() {
		return toHeaderCookie();
	}
	
}
