package exp.crawler.qq.utils;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 时间工具类
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TimeUtils extends exp.libs.utils.time.TimeUtils {

	/** 私有化构造函�? */
	protected TimeUtils() {}
	
	/**
	 * 获取当前时间(用于打印界面日志)
	 * @return [HH:mm:ss]
	 */
	public static String getCurTime() {
		String time = toStr(System.currentTimeMillis(), "HH:mm:ss");
		return StrUtils.concat("[", time, "] ");
	}
	
}
