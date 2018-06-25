package exp.bilibili.protocol.bean.other;

import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.protocol.xhr.Other;
import exp.bilibili.protocol.xhr.WatchLive;
import exp.libs.utils.num.IDUtils;

/**
 * <PRE>
 * 手机直播的视频参数对象
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-02-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AppVideo {

	/** 秒单�? */
	private final static int SEC_UNIT = 1000;
	
	/** URL有效期：半小�? */
	private final static int EXPIRES = 1800;
	
	/** 所观看直播的真实房间号 */
	private String roomId;
	
	/** 所观看直播的主播信�? */
	private User up;
	
	/** 每次更新手机直播视频地址�?, 重新生成的GUID (FIXME: 生成规则未知) */
	private String guid;
	
	/** 当前手机直播视频地址URL(每次更新后有效期为半小时) */
	private String url;
	
	/** 每次更新手机直播视频地址�?, 开始观看视频的时间�? */
	private long createTime;
	
	/** 每次更新手机直播视频地址�?, 该视频可以观看的有效时间 */
	private long expiresTime;
	
	/** 上一次观看这个视�? 的时间点 */
	private long lastViewTime;
	
	/**
	 * 构造函�?
	 * @param roomId 直播间号
	 */
	public AppVideo() {
		this.roomId = "";
		this.up = User.NULL;
		this.guid = "";
		this.url = "";
		this.createTime = 0;
		this.expiresTime = 0;
	}
	
	/**
	 * 更新所观看的直播视频信�?
	 * @param cookie 观看者的cookie
	 * @param roomId 所观看的直播房间号
	 */
	public void update(BiliCookie cookie, int roomId) {
		this.roomId = String.valueOf(RoomMgr.getInstn().getRealRoomId(roomId));
		this.up = Other.queryUpInfo(roomId);
		this.guid = IDUtils.getUUID().replace("-", "");
		this.url = WatchLive.getAppVideoURL(cookie, roomId);
		this.createTime = System.currentTimeMillis() / SEC_UNIT;
		this.expiresTime = (createTime + EXPIRES) * SEC_UNIT;	// URL有效期半小时
		this.lastViewTime = createTime;
	}
	
	public boolean isVaild() {
		return (System.currentTimeMillis() <= expiresTime);
	}

	public String getRoomId() {
		return roomId;
	}
	
	public String getUpId() {
		return up.ID();
	}
	
	public int getUpLv() {
		return up.LV();
	}

	public String getGuid() {
		return guid;
	}

	public String getUrl() {
		return url;
	}

	public long getCreateTime() {
		return createTime;
	}

	public long getExpiresTime() {
		return expiresTime;
	}
	
	public int getDeltaSecond() {
		long now = System.currentTimeMillis() / 1000;
		long delta = now - lastViewTime;
		lastViewTime = now;
		return (int) delta;
	}
	
}
