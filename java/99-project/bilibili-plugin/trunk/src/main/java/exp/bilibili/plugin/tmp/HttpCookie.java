package exp.bilibili.plugin.tmp;

import java.util.Date;

import org.openqa.selenium.Cookie;

import exp.bilibili.plugin.utils.TimeUtils;
import exp.libs.utils.other.StrUtils;

public class HttpCookie {

	private String name;
	
	private String value;
	
	private String domain;
	
	private String path;
	
	private Date expiry;
	
	private boolean isSecure;
	
	private boolean isHttpOnly;
	
	public HttpCookie() {
		this.name = "";
		this.value = "";
		this.domain = "";
		this.path = "";
		this.expiry = new Date();
		this.isSecure = false;
		this.isHttpOnly = false;
	}
	
	/**
	 * 
	 * @param responseHeadCookie HTTP响应头中的 Set-Cookie
	 */
	public HttpCookie(String responseHeadCookie) {
		this();
		init(responseHeadCookie);
	}
	
	private void init(String responseHeadCookie) {
		String[] vals = responseHeadCookie.split(";");
		for(int i = 0; i < vals.length; i++) {
			String[] kv = vals[i].split("=");
			if(kv.length == 2) {
				String key = kv[0].trim();
				String val = kv[1].trim();
				
				if(i == 0) {
					this.name = key;
					this.value = val;
					
				} else {
					if("Domain".equalsIgnoreCase(key)) {
						this.domain = val;
						
					} else if("Path".equalsIgnoreCase(key)) {
						this.path = val;
						
					} else if("Expires".equalsIgnoreCase(key)) {
						this.expiry = TimeUtils.toDate(val);
					}
				}
				
			} else if(kv.length == 1){
				String key = kv[0].trim();
				if("Secure".equalsIgnoreCase(key)) {
					this.isSecure = true;
					
				} else if("HttpOnly".equalsIgnoreCase(key)) {
					this.isHttpOnly = true;
				}
			}
		}
	}
	
	public boolean isVaild() {
		return StrUtils.isNotEmpty(name);
	}
	
	public String toKV() {
		return (!isVaild() ? "" : StrUtils.concat(name, "=", value));
	}

	public Cookie toSeleniumCookie() {
		return new Cookie(name, value, domain, path, expiry, isSecure, isHttpOnly);
	}
	
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getDomain() {
		return domain;
	}

	public String getPath() {
		return path;
	}

	public Date getExpiry() {
		return expiry;
	}

	public boolean isSecure() {
		return isSecure;
	}

	public boolean isHttpOnly() {
		return isHttpOnly;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}

	public void setHttpOnly(boolean isHttpOnly) {
		this.isHttpOnly = isHttpOnly;
	}
	
}
