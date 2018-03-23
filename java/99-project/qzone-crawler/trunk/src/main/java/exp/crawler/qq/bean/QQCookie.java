package exp.crawler.qq.bean;

import java.util.Date;

import exp.libs.utils.other.JSUtils;
import exp.libs.warp.net.cookie.WebKitCookie;

public class QQCookie extends WebKitCookie {

	/** NULL-cookie对象 */
	public final static QQCookie NULL = new QQCookie();
	
	private final static String GTK_JS_PATH = "./conf/gtk.js";
	
	private final static String GTK_METHOD = "getACSRFToken";
	
	private final static String UIN_KEY = "uin";
	
	private final static String PSKEY_KEY = "p_skey";
	
	/** 当前登陆账号(即登陆的QQ号) */
	private String uin;
	
	/** 每次登陆QQ空间都会生成一个固定的GTK, 用于其他页面操作 */
	private String gtk;
	
	/** 每次登陆QQ空间都会生成一个固定的qzonetoken, 用于其他页面操作 */
	private String qzoneToken;
	
	@Override
	protected void init() {
		this.uin = "";
		this.gtk = "";
		this.qzoneToken = "";
	}
	
	/**
	 * 在添加新的cookie时会触发此方法, 用于提取某些特殊的名值对作为常量, 例如CSRF
	 * @param name cookie键名
	 * @param value cookie键值
	 * @param expires cookie有效期
	 * return true:保留该cookie; false;丢弃该cookie
	 */
	protected boolean takeCookieNVE(String name, String value, Date expires) {
		boolean isKeep = true;
		
		if(UIN_KEY.equalsIgnoreCase(name)) {
			this.uin = value;
			uin = uin.replaceFirst("^[o|O]", "");
			uin = uin.replaceFirst("^0*", "");
			
		} else if(PSKEY_KEY.equalsIgnoreCase(name)) {
			this.gtk = toGTK(value);
		}
		return isKeep;
	}
	
	/**
	 * 通过 skey 计算GTK码.
	 * 
	 * 先用 外置的JS算法 计算 GTK， 当使用 JS计算失败 时，才使用内置算法计算。
	 * 外置JS算法主要是为了在QQ更新了GTK算法情况下，可以对应灵活修改。
	 * 
	 * QQ计算GTK的JS函数获取方法：
	 * 	在登陆页面点击【登陆后】，按F12打开开发者工具，
	 * 	通过ctrl+shift+f全局搜索 【g_tk】，可以找到这个js函数
	 * 
	 * @param skey
	 * @return
	 */
	public String toGTK(String skey) {
		String gtk = "";
		try {
			Double dNum = (Double) JSUtils.executeJS(GTK_JS_PATH, GTK_METHOD, skey);
			gtk = String.valueOf((int) dNum.doubleValue());
			
		} catch (Throwable e) {
			gtk = _toGTK(skey);
		}
		return gtk;
	}
	
	/**
	 * 内置GTK算法
	 * @param skey
	 * @return
	 */
	private String _toGTK(String skey) {
		String gtk = "";
		int hash = 5381;
		for (int i = 0; i < skey.length(); ++i) {
			hash += (hash << 5) + (int) skey.charAt(i);
		}
		gtk = String.valueOf(hash & 0x7fffffff);
		return gtk;
	}
	
	public String UIN() {
		return uin;
	}
	
	public String GTK() {
		return gtk;
	}
	
	public String QZONE_TOKEN() {
		return qzoneToken;
	}
	
	public void setQzoneToken(String qzoneToken) {
		this.qzoneToken = qzoneToken;
	}
	
}
