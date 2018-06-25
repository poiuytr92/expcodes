package exp.bilibili.plugin.utils;

import java.util.Date;

import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 时间工具类
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
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
	
	/**
	 * 检查当前时间是否为晚上(18:00~24:00)
	 * @return
	 */
	public static boolean isNight() {
		int hour = TimeUtils.getCurHour(8);	// 中国8小时时差
		return (hour >= 18 && hour < 24);
	}
	
	/**
	 * 获取本期时间
	 * @return yyyyMM 格式, �?: 201801
	 */
	public static int getCurPeriod() {
		return NumUtils.toInt(TimeUtils.toStr(new Date(), "yyyyMM"), 0);
	}
	
	/**
	 * 获取上期时间
	 * @return yyyyMM 格式, �?: 201712
	 */
	public static int getLastPeriod() {
		return getLastPeriod(getCurPeriod());
	}
	
	/**
	 * 获取上期时间
	 * @param curPeriod 本期时间, yyyyMM 格式, �?: 201801
	 * @return yyyyMM 格式, �?: 201712
	 */
	public static int getLastPeriod(int curPeriod) {
		int period = curPeriod;
		if(period % 100 == 1) {
			period = period - 101 + 12;
		} else {
			period = period - 1;
		}
		return period;
	}
	
}
