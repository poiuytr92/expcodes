package exp.crawler.qq.bean;

import java.util.Date;

import exp.crawler.qq.utils.EncryptUtils;
import exp.libs.warp.net.cookie.WebKitCookie;

/**
 * <PRE>
 * QQ-Cookie
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class QQCookie extends WebKitCookie {

	/** NULL-cookie对象 */
	public final static QQCookie NULL = new QQCookie();
	
	/** 用于登陆QQ的SIG属性键 */
	private final static String SIG_KEY = "pt_login_sig";
	
	/** 登陆验证码的校验码的属性键 */
	private final static String VCODE_KEY = "verifysession";
	
	/** 当前所登陆QQ号的cookie属性键 */
	private final static String UIN_KEY = "uin";
	
	/** 用于生成GTK的cookie属性键 */
	private final static String PSKEY_KEY = "p_skey";
	
	/** 用于登陆QQ的SIG�? */
	private String sig;
	
	/** 登陆验证码的校验�? */
	private String verifysession;
	
	/** 当前登陆账号(即登陆的QQ�?) */
	private String uin;
	
	/** 每次登陆QQ空间都会通过p_skey生成一个固定的GTK, 用于其他页面操作 */
	private String gtk;
	
	/** 每次登陆QQ空间都会生成一个固定的qzonetoken, 用于其他页面操作 */
	private String qzoneToken;
	
	/** QQ昵称 */
	private String nickName;
	
	@Override
	protected void init() {
		this.sig = "";
		this.verifysession = "";
		this.uin = "";
		this.gtk = "";
		this.qzoneToken = "";
		this.nickName = "";
	}
	
	/**
	 * 在添加新的cookie时会触发此方�?, 用于提取某些特殊的名值对作为常量, 例如CSRF
	 * @param name cookie键名
	 * @param value cookie键�?
	 * @param expires cookie有效�?
	 * return true:保留该cookie; false;丢弃该cookie
	 */
	protected boolean takeCookieNVE(String name, String value, Date expires) {
		boolean isKeep = true;
		
		if(SIG_KEY.equalsIgnoreCase(name)) {
			this.sig = value;
			
		} else if(VCODE_KEY.equalsIgnoreCase(name)) {
			this.verifysession = value;
			
		} else if(UIN_KEY.equalsIgnoreCase(name)) {
			this.uin = value;
			uin = uin.replaceFirst("^[o|O]", "");
			uin = uin.replaceFirst("^0*", "");
			nickName = uin;
			
		} else if(PSKEY_KEY.equalsIgnoreCase(name)) {
			this.gtk = EncryptUtils.toGTK(value);
		}
		return isKeep;
	}
	
	public String SIG() {
		return sig;
	}
	
	public String VERIFYSESSION() {
		return verifysession;
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
	
	public String NICKNAME() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
}
