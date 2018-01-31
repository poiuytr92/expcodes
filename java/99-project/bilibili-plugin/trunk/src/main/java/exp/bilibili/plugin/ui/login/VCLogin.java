package exp.bilibili.plugin.ui.login;

import exp.bilibili.plugin.bean.ldm.HttpCookie;
import exp.bilibili.protocol.MsgSender;

/**
 * <PRE>
 * 帐密登陆.
 *  可用于登陆主号、小号、马甲号
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class VCLogin {

	protected VCLogin() {}
	
	/**
	 * 下载登陆用的验证码图片
	 * @param imgPath 图片保存路径
	 * @return 与该验证码配套的cookies
	 */
	protected static String downloadVccode(String imgPath) {
		return MsgSender.downloadVccode(imgPath);
	}
	
	/**
	 * 使用帐密+验证码的方式登录
	 * @param username 账号
	 * @param password 密码
	 * @param vccode 验证码
	 * @param vcCookies 与验证码配套的cookies
	 * @return
	 */
	protected static HttpCookie toLogin(String username, String password, 
			String vccode, String vcCookies) {
		HttpCookie cookie = MsgSender.toLogin(username, password, vccode, vcCookies);
		if(cookie != HttpCookie.NULL) {
			MsgSender.queryUserInfo(cookie);
		}
		return cookie;
	}
	
}
