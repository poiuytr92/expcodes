package exp.bilibili.plugin.bean.ldm;

import exp.bilibili.plugin.envm.CookieType;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.cookie.HttpCookie;

/**
 * <PRE>
 * B站账号的cookie集
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BiliCookie extends HttpCookie {

	/** NULL-cookie对象 */
	public final static BiliCookie NULL = new BiliCookie();
	
	/** B站CSRF标识 */
	private final static String CSRF_KEY = "bili_jct";
	
	/** B站用户ID标识 */
	private final static String UID_KEY = "DedeUserID";
	
	/** 登陆类型 */
	private CookieType type;
	
	/** 自动投喂 */
	private boolean autoFeed;
	
	/** 该cookie对应的用户ID */
	private String uid;
	
	/** 该cookie对应的用户昵称 */
	private String nickName;
	
	/** 是否已绑定手机 */
	private boolean bindTel;
	
	/** 从cookies提取的csrf token */
	private String csrf;
	
	public BiliCookie() {
		super();
		init();
	}
	
	public BiliCookie(String headerCookies) {
		super(headerCookies);
		init();
	}
	
	private void init() {
		this.type = (type == null ? CookieType.UNKNOW : type);
		this.autoFeed = (autoFeed ? true : false);
		this.uid = (StrUtils.isEmpty(uid) ? "" : uid);
		this.nickName = (StrUtils.isEmpty(nickName) ? "" : nickName);
		this.bindTel = false;
		this.csrf = (StrUtils.isEmpty(csrf) ? "" : csrf);
	}
	
	@Override
	protected void takeNV(String name, String value) {
		if(CSRF_KEY.equalsIgnoreCase(name)) {
			csrf = value;
			
		} else if(UID_KEY.equalsIgnoreCase(name)) {
			uid = value;
		}
	}
	
	/**
	 * cookies是否有效
	 * @return true:有效; false:无效
	 */
	public boolean isVaild() {
		return (super.isVaild() && StrUtils.isNotEmpty(uid, nickName));
	}
	
	public CookieType TYPE() {
		return type;
	}
	
	public String CSRF() {
		return csrf;
	}
	
	public String UID() {
		return uid;
	}
	
	public String NICKNAME() {
		return nickName;
	}
	
	public boolean BIND_TEL() {
		return bindTel;
	}
	
	public void setType(CookieType type) {
		this.type = type;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void setBindTel(boolean bindTel) {
		this.bindTel = bindTel;
	}

	public boolean isAutoFeed() {
		return autoFeed;
	}

	public void setAutoFeed(boolean autoFeed) {
		this.autoFeed = autoFeed;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof BiliCookie)) {
			return false;
		}
		
		BiliCookie other = (BiliCookie) obj;
		return this.uid.equals(other.uid);
	}
	
	@Override
	public int hashCode() {
		return uid.hashCode();
	}
	
}
