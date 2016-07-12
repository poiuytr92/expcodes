package exp.libs.utils.pub;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.DateFormat;
import exp.libs.envm.TimeUnit;

/**
 * <PRE>
 * 时间工具类.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TimeUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(TimeUtils.class);
	
	private final static String DEFAULT_TIME = "0000-00-00 00:00:00.000";
	
	public final static String FORMAT_YMDHMS = DateFormat.YMDHMS;
	
	public final static String FORMAT_YMDHMSS = DateFormat.YMDHMSS;
	
	/** 私有化构造函数 */
	protected TimeUtils() {}
	
	/**
	 * 把[Date时间]转换为[UTC时间]
	 * @param date Date时间
	 * @return UTC时间(转换失败则返回0)
	 */
	public static long toUTC(Date date) {
		long millis = 0L;
		if(date == null) {
			return millis;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YMDHMS);
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
	 * 把[millis时间]转换为[yyyy-MM-dd HH:mm:ss格式字符串]
	 * @param millis millis时间
	 * @return yyyy-MM-dd HH:mm:ss格式字符串
	 */
	public static String toStr(long millis) {
		return toStr((millis > 0 ? new Date(millis) : null), FORMAT_YMDHMS);
	}
	
	/**
	 * 把[millis时间]转换为指定格式字符串
	 * @param millis millis时间
	 * @param format 日期格式字符串
	 * @return 指定格式字符串
	 */
	public static String toStr(long millis, String format) {
		return toStr((millis > 0 ? new Date(millis) : null), format);
	}
	
	/**
	 * 把[Date时间]转换为[yyyy-MM-dd HH:mm:ss格式字符串]
	 * @param date Date时间
	 * @return yyyy-MM-dd HH:mm:ss格式字符串
	 */
	public static String toStr(Date date) {
		return toStr(date, FORMAT_YMDHMS);
	}
	
	/**
	 * 把[Date时间]转换为指定格式字符串
	 * @param date Date时间
	 * @param format 日期格式字符串
	 * @return 指定格式字符串
	 */
	public static String toStr(Date date, String format) {
		String ymdhms = DEFAULT_TIME;
		if(date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			ymdhms = sdf.format(date);
		}
		return ymdhms;
	}
	
	/**
	 * 获取[yyyy-MM-dd HH:mm:ss.SSS格式]的当前系统时间
	 * @return 当前系统时间
	 */
	public static String getSysDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.FORMAT_YMDHMSS);
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 获取指定格式的当前系统时间
	 * @param format 指定日期格式
	 * @return 当前系统时间
	 */
	public static String getSysDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 把[yyyy-MM-dd HH:mm:ss格式字符串]转换为[Date时间]
	 * @param ymdhms yyyy-MM-dd HH:mm:ss格式字符串
	 * @return Date时间 (转换失败则返回起始时间 1970-1-1 08:00:00)
	 */
	public static Date toDate(String ymdhms) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YMDHMS);
		Date date = null;
		try {
			date = sdf.parse(ymdhms);
		} catch (Exception e) {
			date = new Date(0);
			log.error("转换 [{}] 为日期类型失败.", ymdhms, e);
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
	 * @param ymdhms yyyy-MM-dd HH:mm:ss格式字符串
	 * @return 毫秒时间
	 */
	public static long toMillis(String ymdhms) {
		Date date = toDate(ymdhms);
		return (date == null ? 0 : date.getTime());
	}
	
	/**
	 * 判断是否 [time<=endTime]
	 * @param time 被判定时间点
	 * @param endTime 参照时间点
	 * @return 若 [time<=endTime] 返回true; 反之返回false
	 */
	public static boolean isBefore(long time, long endTime) {
		return time <= endTime;
	}
	
	/**
	 * 判断是否 [time>startTime]
	 * @param time 被判定时间点
	 * @param startTime 参照时间点
	 * @return 若 [time>startTime] 返回true; 反之返回false
	 */
	public static boolean isAfter(long time, long startTime) {
		return startTime <= time;
	}
	
	/**
	 * 判断是否 [startTime<=time<=endTime]
	 * @param time 被判定时间点
	 * @param startTime 参照时间起点
	 * @param endTime 参照时间终点
	 * @return 若 [startTime<=time<=endTime] 返回true; 反之返回false
	 */
	public static boolean isBetween(long time, long startTime, long endTime) {
		return (startTime <= time) & (time <= endTime);
	}
	
	/**
	 * 获取指定时间前n个小时的时间
	 * @param time 指定时间
	 * @param hour 小时数
	 * @return 指定时间前n个小时的时间
	 */
	public static long getBeforeHour(long time, int hour) {
		return time - 3600000 * hour;
	}
	
	/**
	 * 获取指定时间后n个小时的时间
	 * @param time 指定时间
	 * @param hour 小时数
	 * @return 指定时间后n个小时的时间
	 */
	public static long getAfterHour(long time, int hour) {
		return time + 3600000 * hour;
	}
	
	/**
	 * 获取上一个正点时间
	 * @return 上一个正点时间
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
	 * 获取下一个正点时间
	 * @return 下一个正点时间
	 */
	public static long getNextOnTime() {
		return getCurOnTime() + 3600000;
	}
	
	/**
	 * 获取30天前的日期
	 * @return yyyy-MM-dd HH:mm:ss型时间
	 */
	public static String getLastMonthDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YMDHMS);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -30);
		return sdf.format(new Date(cal.getTime().getTime()));
	}
	
	public static double getSecond(long millis) {
		return millis / 1000.0;
	}
	
	public static double getMinute(long millis) {
		return millis / 60000.0;
	}
	
	public static double getHour(long millis) {
		return millis / 3600000.0;
	}
	
	public static double getDay(long millis) {
		return millis / 86400000.0;
	}
	
	public static String getUnitTime(long millis) {
		double time = millis;
		String unit = TimeUnit.MS.VAL;
		
		if(time >= 1000 && TimeUnit.MS.VAL.equals(unit)) { 
			time = time / 1000.0;
			unit = TimeUnit.SECOND.VAL;
		}
		
		if(time >= 60 && TimeUnit.SECOND.VAL.equals(unit)) { 
			time = time / 60.0;
			unit = TimeUnit.MINUTE.VAL;
		}
		
		if(time >= 60 && TimeUnit.MINUTE.VAL.equals(unit)) { 
			time = time / 60.0;
			unit = TimeUnit.HOUR.VAL;
		}
		
		if(time >= 24 && TimeUnit.HOUR.VAL.equals(unit)) { 
			time = time / 24.0;
			unit = TimeUnit.DAY.VAL;
		}
		return new DecimalFormat("0.00 " + unit).format(time);
	}
	
	/**
	 * 根据计数器获取毫秒时间: 计数值越大，毫秒值越大.
	 * 毫秒时间 = 2^(cnt-1) * 1000
	 * 
	 * @param cnt 计数值(1~31)
	 * @return 毫秒时间(ms).
	 */
	public static long getMillisTime(final int cnt) {
		return getMillisTime(cnt, 0, Long.MAX_VALUE);
	}
	
	/**
	 * 根据计数器获取毫秒时间: 计数值越大，毫秒值越大.
	 * 毫秒时间 = 2^(cnt-1) * 1000
	 * 
	 * @param cnt 计数值(1~31)
	 * @param maxMillisTime 最大毫秒值(ms)
	 * @return 毫秒时间(ms).
	 */
	public static long getMillisTime(final int cnt, final long maxMillisTime) {
		return getMillisTime(cnt, 0, maxMillisTime);
	}
	
	/**
	 * 根据计数器获取毫秒时间: 计数值越大，毫秒值越大.
	 * 毫秒时间 = 2^(cnt-1) * 1000
	 * 
	 * @param cnt 计数值(1~31)
	 * @param minMillisTime 最小毫秒值(ms)
	 * @param maxMillisTime 最大毫秒值(ms)
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
	
}
