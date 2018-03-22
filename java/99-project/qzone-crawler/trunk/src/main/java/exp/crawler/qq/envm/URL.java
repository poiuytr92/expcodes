package exp.crawler.qq.envm;

import exp.libs.utils.other.StrUtils;

public class URL {

	/** QQ空间登陆页面 */
	public final static String QZONE_LOGIN_URL = "http://qzone.qq.com/";
	
	/** QQ空间域名地址(前缀) */
	private final static String QZONE_DOMAIN = "https://user.qzone.qq.com/";
	
	/**
	 * QQ空间地址
	 * @param QQ
	 * @return
	 */
	public final static String QZONE_HOMR_URL(final String QQ) {
		return QZONE_DOMAIN.concat(QQ);
	}
	
	/**
	 * 相册地址
	 * @param QQ
	 * @param AID 相册ID
	 * @return
	 */
	public final static String ALBUM_URL(final String QQ, final String AID) {
		return StrUtils.concat(QZONE_HOMR_URL(QQ), "/photo/", AID);
	}
	
	/** 获取说说分页内容URL */
	public final static String MOOD_URL = 
			"https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6";
	
	/** 说说引用地址 */
	public final static String MOOD_REFERER = 
			"https://qzs.qq.com/qzone/app/mood_v6/html/index.html";
	
	/** 说说域名地址 */
	public final static String MOOD_DOMAIN = 
			"http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6";
			
}
