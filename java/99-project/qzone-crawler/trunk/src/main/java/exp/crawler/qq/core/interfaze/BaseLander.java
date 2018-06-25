package exp.crawler.qq.core.interfaze;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * QQ空间登陆器: 基类
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-03-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class BaseLander {

	/** 所登陆的QQ */
	protected String QQ;
	
	/** 所登陆的QQ密码 */
	protected String password;
	
	/**
	 * 构造函�?
	 * @param QQ 所登陆的QQ
	 * @param password 所登陆的QQ密码
	 */
	protected BaseLander(String QQ, String password) {
		this.QQ = StrUtils.isTrimEmpty(QQ) ? "0" : QQ;
		this.password = StrUtils.isTrimEmpty(password) ? "" : password;
		init();
	}
	
	/**
	 * 初始�?
	 */
	protected abstract void init();
	
	/**
	 * 执行登陆操作
	 * @return true:登陆成功; false:登陆失败
	 */
	public abstract boolean execute();
	
	/**
	 * 提取本次登陆的GTK与QzoneToken
	 * @param url
	 */
	protected abstract boolean takeGTKAndToken(String url);
	
}
