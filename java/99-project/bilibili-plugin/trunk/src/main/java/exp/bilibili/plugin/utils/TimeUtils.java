package exp.bilibili.plugin.utils;

import java.util.Date;

import exp.libs.envm.DateFormat;
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
	
	/**
	 * 获取当前时间
	 * @return [HH:mm:ss]
	 */
	public static String getCurTime() {
		String time = toStr(System.currentTimeMillis(), DateFormat.HMS);
		return StrUtils.concat("[", time, "] ");
	}
	
	/**
	 * 检查当前时间是否为晚上(18:00~24:00)
	 * @return
	 */
	public static boolean isNight() {
		int hour = TimeUtils.getCurHour(PEKING_HOUR_OFFSET);
		return (hour >= 18 && hour < 24);
	}
	
	/**
	 * 检查当前时间是否为凌晨(1:00~6:00)
	 * @return
	 */
	public static boolean isDawn() {
		int hour = TimeUtils.getCurHour(PEKING_HOUR_OFFSET);
		return (hour >= 1 && hour < 7);
	}
	
	/**
	 * 检查当前时间是否为0点附�?(23:00~1:00)
	 * @return
	 */
	public static boolean inZeroPointRange() {
		final long ZERO = TimeUtils.getZeroPointMillis(PEKING_HOUR_OFFSET) + DAY_UNIT;	// 当前24�?
		final long RANGE_BGN = ZERO - HOUR_UNIT;	// 当天23�?
		final long RANGE_END = ZERO + HOUR_UNIT;	// 明天1�?
		long now = System.currentTimeMillis();
		return (now >= RANGE_BGN && now <= RANGE_END);
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
