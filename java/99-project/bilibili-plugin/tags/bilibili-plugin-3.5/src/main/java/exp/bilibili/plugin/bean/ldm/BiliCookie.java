package exp.bilibili.plugin.bean.ldm;

import java.util.Date;

import exp.bilibili.plugin.envm.CookieType;
import exp.bilibili.plugin.envm.Danmu;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.cookie.HttpCookie;

/**
 * <PRE>
 * B站账号的cookie集
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
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
	
	/** 从cookies提取的有效期 */
	private Date expires;
	
	/** 从cookies提取的csrf token */
	private String csrf;
	
	/** 从cookies提取的用户ID */
	private String uid;
	
	/** 该cookie对应的用户昵�? */
	private String nickName;
	
	/** 是否已绑定手�? */
	private boolean isBindTel;
	
	/** 是否已实名认�? */
	private boolean isRealName;
	
	/** 是否为房�? */
	private boolean isRoomAdmin;
	
	/** 是否为老爷/年费老爷 */
	private boolean isVip;
	
	/** 是否为提�?/总督 */
	private boolean isGuard;
	
	/** 自动投喂 */
	private boolean autoFeed;
	
	/** 投喂房间�? */
	private int feedRoomId;
	
	public BiliCookie() {
		super();
		init();
	}
	
	public BiliCookie(String headerCookies) {
		super(headerCookies);
		init();
	}
	
	private void init() {
		this.type = CookieType.UNKNOW;
		this.nickName = "";
		this.isBindTel = false;
		this.isRealName = false;
		this.isRoomAdmin = false;
		this.isVip = false;
		this.isGuard = false;
		this.autoFeed = false;
		this.feedRoomId = 0;
		
		// 以下值可能先�? {@link takeCookieNVE} 中被初始�?
		this.expires = (expires == null ? new Date() : expires);
		this.csrf = (StrUtils.isEmpty(csrf) ? "" : csrf);
		this.uid = (StrUtils.isEmpty(uid) ? "" : uid);
	}
	
	@Override
	protected void takeCookieNVE(String name, String value, Date expires) {
		if(CSRF_KEY.equalsIgnoreCase(name)) {
			this.csrf = value;
			
		} else if(UID_KEY.equalsIgnoreCase(name)) {
			this.uid = value;
			this.expires = expires;
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
	
	public void setType(CookieType type) {
		this.type = type;
	}
	
	public Date EXPIRES() {
		return expires;
	}
	
	public String CSRF() {
		return csrf;
	}
	
	public String UID() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String NICKNAME() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public boolean isBindTel() {
		return isBindTel;
	}
	
	public void setBindTel(boolean isBindTel) {
		this.isBindTel = isBindTel;
	}

	public boolean isRealName() {
		return isRealName;
	}

	public void setRealName(boolean isRealName) {
		this.isRealName = isRealName;
	}

	public boolean isRoomAdmin() {
		return isRoomAdmin;
	}

	public void setRoomAdmin(boolean isRoomAdmin) {
		this.isRoomAdmin = isRoomAdmin;
	}
	
	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}
	
	public boolean isGuard() {
		return isGuard;
	}

	public void setGuard(boolean isGuard) {
		this.isGuard = isGuard;
	}
	
	public int DANMU_LEN() {
		return (isGuard ? Danmu.LEN_GUARD : (isVip ? Danmu.LEN_VIP : Danmu.LEN));
	}

	public boolean isAutoFeed() {
		return autoFeed;
	}

	public void setAutoFeed(boolean autoFeed) {
		this.autoFeed = autoFeed;
	}
	
	public int getFeedRoomId() {
		return feedRoomId;
	}

	public void setFeedRoomId(int feedRoomId) {
		this.feedRoomId = feedRoomId;
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
