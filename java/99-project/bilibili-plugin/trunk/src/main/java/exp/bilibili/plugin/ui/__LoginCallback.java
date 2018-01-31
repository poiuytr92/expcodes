package exp.bilibili.plugin.ui;

import exp.bilibili.plugin.bean.ldm.HttpCookie;

/**
 * <PRE>
 * 登陆成功后的回调接口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface __LoginCallback {

	public void afterLogin(final HttpCookie cookie);
	
	public void afterLogout(final HttpCookie cookie);
	
}
