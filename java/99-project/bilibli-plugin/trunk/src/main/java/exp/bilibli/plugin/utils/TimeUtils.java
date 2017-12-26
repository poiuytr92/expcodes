package exp.bilibli.plugin.utils;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 时间工具类
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TimeUtils extends exp.libs.utils.time.TimeUtils {

	protected TimeUtils() {}
	
	public static String getCurTime() {
		String time = toStr(System.currentTimeMillis(), "HH:mm:ss");
		return StrUtils.concat("[", time, "] ");
	}
	
}
