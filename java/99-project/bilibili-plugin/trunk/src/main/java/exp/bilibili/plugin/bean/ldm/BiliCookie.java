package exp.bilibili.plugin.bean.ldm;

import exp.bilibili.plugin.envm.CookieType;
import exp.bilibili.plugin.envm.Danmu;
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
	
	/** 从cookies提取的csrf token */
	private String csrf;
	
	/** 该cookie对应的用户ID */
	private String uid;
	
	/** 该cookie对应的用户昵称 */
	private String nickName;
	
	/** 是否已绑定手机 */
	private boolean isBindTel;
	
	/** 是否已实名认证 */
	private boolean isRealName;
	
	/** 是否为房管 */
	private boolean isRoomAdmin;
	
	/** 是否为老爷/年费老爷 */
	private boolean isVip;
	
	/** 是否为提督/总督 */
	private boolean isGuard;
	
	/** 自动投喂 */
	private boolean autoFeed;
	
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
		this.csrf = (StrUtils.isEmpty(csrf) ? "" : csrf);
		this.uid = (StrUtils.isEmpty(uid) ? "" : uid);
		this.nickName = (StrUtils.isEmpty(nickName) ? "" : nickName);
		this.isBindTel = false;
		this.isRealName = false;
		this.isRoomAdmin = false;
		this.isVip = false;
		this.isGuard = false;
		this.autoFeed = (autoFeed ? true : false);
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
	
	public void setType(CookieType type) {
		this.type = type;
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
