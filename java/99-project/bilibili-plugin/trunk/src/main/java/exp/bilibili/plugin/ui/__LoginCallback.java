package exp.bilibili.plugin.ui;

import exp.bilibili.plugin.bean.ldm.BiliCookie;

/**
 * <PRE>
 * 登陆成功后的回调接口
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface __LoginCallback {

	/**
	 * 登录成功后触�?
	 * @param cookie 登录成功后的cookie
	 */
	public void afterLogin(final BiliCookie cookie);
	
	/**
	 * 注销成功后触�?
	 * @param cookie 注销前的cookie
	 */
	public void afterLogout(final BiliCookie cookie);
	
}
