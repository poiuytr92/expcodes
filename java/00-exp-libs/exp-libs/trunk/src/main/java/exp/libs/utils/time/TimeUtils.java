package exp.libs.utils.time;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.DateFormat;
import exp.libs.utils.num.NumUtils;

/**
 * <PRE>
 * 时间工具类.
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TimeUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(TimeUtils.class);
	
	/** 默认时间�? */
	private final static String DEFAULT_TIME = "0000-00-00 00:00:00.000";
	
	/** 默认GMT时间�? */
	private final static String DEFAULT_GMT = "Thu, 01 Jan 1970 08:00:00 GMT+08:00";
	
	/** 日期格式�? yyyy-MM-dd HH:mm:ss */
	private final static String FORMAT_YMDHMS = DateFormat.YMDHMS;
	
	/** 日期格式�? yyyy-MM-dd HH:mm:ss.SSS */
	private final static String FORMAT_YMDHMSS = DateFormat.YMDHMSS;
	
	/** GMT日期格式(多用于cookie的有效时�?)�? EEE, dd MMM yyyy HH:mm:ss z */
	private final static String FORMAT_GMT = DateFormat.GMT;
	
	/** 北京时差�?8小时 */
	public final static int PEKING_HOUR_OFFSET = 8;
	
	/** "�?"换算为millis单位 */
	public final static long DAY_UNIT = 86400000L;

	/** "小时"换算为millis单位 */
	public final static long HOUR_UNIT = 3600000L;
	
	/** "分钟"换算为millis单位 */
	public final static long MIN_UNIT = 60000L;
	
	/** 私有化构造函�? */
	protected TimeUtils() {}
	
	/**
	 * 生成SimpleDateFormat对象
	 * @param format 时间格式
	 * @return SimpleDateFormat对象
	 */
	private final static SimpleDateFormat createSDF(String format) {
		
		// Locale.ENGLISH用于设定所生成的格式字符串中的符号为英文标�?
		return new SimpleDateFormat(format, Locale.ENGLISH);
	}
	
	/**
	 * 把[Date时间]转换为[UTC时间]
	 * @param date Date时间
	 * @return UTC时间(转换失败则返�?0)
	 */
	public static long toUTC(Date date) {
		long millis = 0L;
		if(date == null) {
			return millis;
		}
		
		SimpleDateFormat sdf = createSDF(FORMAT_YMDHMS);
		String ymdhms = sdf.format(date);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			millis = sdf.parse(ymdhms).getTime();
		} catch (Exception e) {
			log.error("转换UTC时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 把[Date时间]转换为[cookie有效时间]
	 * @param date Date时间
	 * @return cookie有效时间(转换失败则返回默认�? Thu, 01 Jan 1970 08:00:00 GMT+08:00)
	 */
	public static String toExpires(Date date) {
		String expires = DEFAULT_GMT;
		if(date != null) {
			SimpleDateFormat sdf = createSDF(FORMAT_GMT);
			expires = sdf.format(date);
		}
		return expires;
	}
	
	/**
	 * 把[millis时间]转换为[yyyy-MM-dd HH:mm:ss格式字符串]
	 * @param millis millis时间
	 * @return yyyy-MM-dd HH:mm:ss格式字符�?
	 */
	public static String toStr(long millis) {
		return toStr(millis, FORMAT_YMDHMS);
	}
	
	/**
	 * 把[millis时间]转换为指定格式字符串
	 * @param millis millis时间
	 * @param format 日期格式字符�?
	 * @return 指定格式字符�?
	 */
	public static String toStr(long millis, String format) {
		return toStr((millis >= 0 ? new Date(millis) : null), format);
	}
	
	/**
	 * 把[Date时间]转换为[yyyy-MM-dd HH:mm:ss格式字符串]
	 * @param date Date时间
	 * @return yyyy-MM-dd HH:mm:ss格式字符�?
	 */
	public static String toStr(Date date) {
		return toStr(date, FORMAT_YMDHMS);
	}
	
	/**
	 * 把[Date时间]转换为指定格式字符串
	 * @param date Date时间
	 * @param format 日期格式字符�?
	 * @return 指定格式字符�?
	 */
	public static String toStr(Date date, String format) {
		String sDate = DEFAULT_TIME;
		if(date != null) {
			SimpleDateFormat sdf = createSDF(format);
			sDate = sdf.format(date);
		}
		return sDate;
	}
	
	/**
	 * 获取[yyyy-MM-dd HH:mm:ss.SSS格式]的当前系统时�?
	 * @return 当前系统时间
	 */
	public static String getSysDate() {
		SimpleDateFormat sdf = createSDF(TimeUtils.FORMAT_YMDHMSS);
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 获取指定格式的当前系统时�?
	 * @param format 指定日期格式
	 * @return 当前系统时间
	 */
	public static String getSysDate(String format) {
		SimpleDateFormat sdf = createSDF(format);
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 把[yyyy-MM-dd HH:mm:ss格式字符串]转换为[Date时间]
	 * @param ymdhms yyyy-MM-dd HH:mm:ss格式字符�?
	 * @return Date时间 (转换失败则返回起始时�? 1970-1-1 08:00:00)
	 */
	public static Date toDate(String ymdhms) {
		return toDate(ymdhms, FORMAT_YMDHMS);
	}
	
	/**
	 * 把[format格式字符串]转换为[Date时间]
	 * @param sDate 时间字符�?
	 * @param format 时间字符串格�?
	 * @return Date时间 (转换失败则返回起始时�? 1970-1-1 08:00:00)
	 */
	public static Date toDate(String sDate, String format) {
		SimpleDateFormat sdf = createSDF(format);
		Date date = null;
		try {
			date = sdf.parse(sDate);
		} catch (Exception e) {
			date = new Date(0);
			log.error("转换 [{}] 为日期类型失�?.", sDate, e);
		}
		return date;
	}
	
	/**
	 * 把[Timestamp时间]转换为[Date时间]
	 * @param timestamp Timestamp时间
	 * @return Date时间
	 */
	public static Date toDate(Timestamp timestamp) {
		return (timestamp == null ? new Date(0) : new Date(timestamp.getTime()));
	}
	
	/**
	 * 把[Date时间]转换为[Timestamp时间]
	 * @param date Date时间
	 * @return Timestamp时间
	 */
	public static Timestamp toTimestamp(Date date) {
		return (date == null ? new Timestamp(0) : new Timestamp(date.getTime()));
	}
	
	/**
	 * 把[yyyy-MM-dd HH:mm:ss格式字符串]转换为[毫秒时间]
	 * @param ymdhms yyyy-MM-dd HH:mm:ss格式字符�?
	 * @return 毫秒时间
	 */
	public static long toMillis(Date date) {
		return (date == null ? 0 : date.getTime());
	}
	
	/**
	 * 把[yyyy-MM-dd HH:mm:ss格式字符串]转换为[毫秒时间]
	 * @param ymdhms yyyy-MM-dd HH:mm:ss格式字符�?
	 * @return 毫秒时间
	 */
	public static long toMillis(String ymdhms) {
		return toMillis(toDate(ymdhms));
	}
	
	/**
	 * 把[format格式字符串]转换为[毫秒时间]
	 * @param sDate 时间字符�?
	 * @param format 时间字符串格�?
	 * @return 毫秒时间
	 */
	public static long toMillis(String sData, String format) {
		return toMillis(toDate(sData, format));
	}
	
	/**
	 * 判断是否 [time<=endTime]
	 * @param time 被判定时间点
	 * @param endTime 参照时间�?
	 * @return �? [time<=endTime] 返回true; 反之返回false
	 */
	public static boolean isBefore(long time, long endTime) {
		return time <= endTime;
	}
	
	/**
	 * 判断是否 [time>bgnTime]
	 * @param time 被判定时间点
	 * @param bgnTime 参照时间�?
	 * @return �? [time>bgnTime] 返回true; 反之返回false
	 */
	public static boolean isAfter(long time, long bgnTime) {
		return bgnTime <= time;
	}
	
	/**
	 * 判断是否 [bgnTime<=time<=endTime]
	 * @param time 被判定时间点
	 * @param bgnTime 参照时间起点
	 * @param endTime 参照时间终点
	 * @return �? [bgnTime<=time<=endTime] 返回true; 反之返回false
	 */
	public static boolean isBetween(long time, long bgnTime, long endTime) {
		return (bgnTime <= time) & (time <= endTime);
	}
	
	/**
	 * 获取指定时间前n个小时的时间
	 * @param time 指定时间
	 * @param hour 小时�?
	 * @return 指定时间前n个小时的时间
	 */
	public static long getBeforeHour(long time, int hour) {
		return time - 3600000 * hour;
	}
	
	/**
	 * 获取指定时间后n个小时的时间
	 * @param time 指定时间
	 * @param hour 小时�?
	 * @return 指定时间后n个小时的时间
	 */
	public static long getAfterHour(long time, int hour) {
		return time + 3600000 * hour;
	}
	
	/**
	 * 获取上一个正点时�?
	 * @return 上一个正点时�?
	 */
	public static long getLastOnTime() {
		return getCurOnTime() - 3600000;
	}
	
	/**
	 * 获取当前正点时间
	 * @return 当前正点时间
	 */
	public static long getCurOnTime() {
		long now = System.currentTimeMillis();
		return now - (now % 3600000);
	}
	
	/**
	 * 获取下一个正点时�?
	 * @return 下一个正点时�?
	 */
	public static long getNextOnTime() {
		return getCurOnTime() + 3600000;
	}
	
	/**
	 * 以当前时间为参考，获取 ±Day 的日�?
	 * @param beforeOrAfterDay 正负天数
	 * @return yyyy-MM-dd HH:mm:ss型时�?
	 */
	public static String getDate(int beforeOrAfterDay) {
		SimpleDateFormat sdf = createSDF(FORMAT_YMDHMS);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, beforeOrAfterDay);
		return sdf.format(new Date(cal.getTime().getTime()));
	}
	
	/**
	 * 根据计数器获取毫秒时�?: 计数值越大，毫秒值越�?.
	 * 毫秒时间 = 2^(cnt-1) * 1000
	 * 
	 * @param cnt 计数�?(1~31)
	 * @return 毫秒时间(ms).
	 */
	public static long getMillisTime(final int cnt) {
		return getMillisTime(cnt, 0, Long.MAX_VALUE);
	}
	
	/**
	 * 根据计数器获取毫秒时�?: 计数值越大，毫秒值越�?.
	 * 毫秒时间 = 2^(cnt-1) * 1000
	 * 
	 * @param cnt 计数�?(1~31)
	 * @param maxMillisTime 最大毫秒�?(ms)
	 * @return 毫秒时间(ms).
	 */
	public static long getMillisTime(final int cnt, final long maxMillisTime) {
		return getMillisTime(cnt, 0, maxMillisTime);
	}
	
	/**
	 * 根据计数器获取毫秒时�?: 计数值越大，毫秒值越�?.
	 * 毫秒时间 = 2^(cnt-1) * 1000
	 * 
	 * @param cnt 计数�?(1~31)
	 * @param minMillisTime 最小毫秒�?(ms)
	 * @param maxMillisTime 最大毫秒�?(ms)
	 * @return 毫秒时间(ms).
	 */
	public static long getMillisTime(int cnt, 
			final long minMillisTime, final long maxMillisTime) {
		long millisTime = 0;
		if(cnt > 0) {
			cnt = (cnt > 32 ? 32 : cnt);
			millisTime = (1L << (cnt - 1));
			millisTime *= 1000;
			millisTime = NumUtils.min(millisTime, maxMillisTime);
		}
		millisTime = NumUtils.max(millisTime, minMillisTime);
		return millisTime;
	}
	
	/**
	 * 获取当前的小时值（默认为北京时�?8小时时差�?
	 * @return 当前小时
	 */
	public static int getCurHour() {
		return getCurHour(PEKING_HOUR_OFFSET);
	}
	
	/**
	 * 获取当前的小时�?
	 * @param offset 时差�?
	 * @return 当前小时
	 */
	public static int getCurHour(int offset) {
		long hour = ((System.currentTimeMillis() % DAY_UNIT) / HOUR_UNIT);
		hour = (hour + offset + 24) % 24;	// 时差
		return (int) hour;
	}
	
	/**
	 * 获取当前的分钟数
	 * @return 当前分钟�?
	 */
	public static int getCurMinute() {
		return (int) (System.currentTimeMillis() % DAY_UNIT % HOUR_UNIT / MIN_UNIT);
	}
	
	/**
	 * 获取当天零点毫秒时间（默认为北京时间8小时时差�?
	 * @return 零点毫秒时间
	 */
	public static long getZeroPointMillis() {
		return getZeroPointMillis(PEKING_HOUR_OFFSET);
	}
	
	/**
	 * 获取当天零点毫秒时间
	 * @param offset 时差�?
	 * @return 零点毫秒时间
	 */
	public static long getZeroPointMillis(int offset) {
		int curHour = TimeUtils.getCurHour(offset);
		long zero = System.currentTimeMillis() + (curHour < offset ? DAY_UNIT : 0);
		zero = zero / DAY_UNIT * DAY_UNIT;
		zero = zero - HOUR_UNIT * offset;
		return zero;
	}
	
}
