package exp.libs.utils.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 日期工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(DateUtils.class);
	
	/** 私有化构造函�? */
	protected DateUtils() {}

	/**
	 * 增减日期年份
	 * @param date 日期
	 * @param amount 增加数量（单位：年）
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addYears(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addYears(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期年份失败.", e);
		}
		return newDate;
	}

	/**
	 * 增减日期月份，年份自动按12进位
	 * @param date 日期
	 * @param amount 增减数（单位：月�?
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addMonths(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addMonths(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期月份失败.", e);
		}
		return newDate;
	}

	/**
	 * 以周为单位增减日期时�?
	 * @param date 日期
	 * @param amount 增减数（单位：周�?
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addWeeks(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addWeeks(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期周数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 以天数为单位增减日期时间
	 * @param date 日期
	 * @param amount 增减数（单位：天�?
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addDays(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addDays(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期天数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 以小时为单位增减日期时间
	 * @param date 日期
	 * @param amount 增减数（单位：小时）
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addHours(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addHours(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期小时数失�?.", e);
		}
		return newDate;
	}

	/**
	 * 以分钟为单位增减日期时间
	 * @param date 日期
	 * @param amount 增减数（单位：分钟）
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addMinutes(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addMinutes(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期分钟数失�?.", e);
		}
		return newDate;
	}
	
	/**
	 * 以秒为单位增减日期时�?
	 * @param date 日期
	 * @param amount 增减数（单位：秒�?
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addSeconds(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addSeconds(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期秒数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 以毫秒为单位增减日期时间
	 * @param date 日期
	 * @param amount 增减数（单位：毫秒）
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addMilliseconds(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addMilliseconds(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期毫秒数失�?.", e);
		}
		return newDate;
	}

	/**
	 * 设置日期中的年份
	 * @param date 日期
	 * @param year 年份�?
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setYears(Date date, int year) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setYears(date, year);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期年份失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的月份
	 * @param date 日期
	 * @param month 月份�?
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setMonths(Date date, int month) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setMonths(date, month);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期月份失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的天数
	 * @param date 日期
	 * @param day 天数�?
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setDays(Date date, int day) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setDays(date, day);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期天数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的小时�?
	 * @param date 日期
	 * @param hour 小时�?
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setHours(Date date, int hour) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setHours(date, hour);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期小时数失�?.", e);
		}
		return newDate;
	}

	/**
	 * 设置日期中的分钟�?
	 * @param date 日期
	 * @param minute 分钟�?
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setMinutes(Date date, int minute) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setMinutes(date, minute);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期分钟数失�?.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的秒数
	 * @param date 日期
	 * @param second 秒�?
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setSeconds(Date date, int second) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setSeconds(date, second);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期秒数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的毫秒�?
	 * @param date 日期
	 * @param mills 毫秒�?
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setMilliseconds(Date date, int mills) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setMilliseconds(date, mills);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期秒毫秒数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 尝试使用多种时间格式将字符串转换成为日期对象, 返回最先成功匹配时间格式的对象
	 * @param str 字符�?
	 * @param formats 日期格式�?
	 * @return 若解析失败则返回 new Date(0)
	 */
	public static Date parseDate(String str, String... formats) {
		Date date = new Date(0);
		try {
			date = org.apache.commons.lang3.time.
					DateUtils.parseDate(str, formats);
		} catch (ParseException e) {
			log.error("解析字符串[{}] 为时间格式失�?.", e);
		}
		return date;
	}
	
	/**
	 * <PRE>
	 * 尝试使用多种时间格式将字符串转换成为日期对象, 返回最先成功匹配时间格式的对象.
	 * (此方法严格按照实际日期标准转�?, �?2�?30日是非法日期, 则作为解析失败处�?)
	 * </PRE>
	 * @param str 字符�?
	 * @param formats 日期格式�?
	 * @return 若解析失败则返回 new Date(0)
	 */
	public static Date parseDateStrictly(String str, String... formats) {
		Date date = new Date(0);
		try {
			date = org.apache.commons.lang3.time.
					DateUtils.parseDateStrictly(str, formats);
		} catch (ParseException e) {
			log.error("解析字符串[{}] 为时间格式失�?.", e);
		}
		return date;
	}
	
	/**
	 * 校验两个日期是否为同一�?
	 * @param date1  日期1
	 * @param date2  日期2
	 * @return true:同一�?; false:不是同一�?
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		boolean isSameDay = false;
		if (date1 != null && date2 != null) {
			isSameDay = org.apache.commons.lang3.time.
					DateUtils.isSameDay(date1, date2);
		}
		return isSameDay;
	}

	/**
	 * 校验两个日期是否为同一�?
	 * @param cal1  日期1
	 * @param cal2  日期2
	 * @return true:同一�?; false:不是同一�?
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		boolean isSameDay = false;
		if (cal1 != null && cal2 != null) {
			isSameDay = org.apache.commons.lang3.time.
					DateUtils.isSameDay(cal1, cal1);
		}
		return isSameDay;
	}

	/**
	 * 校验两个日期是否为同一时间（精确到毫秒�?
	 * @param date1  日期1
	 * @param date2  日期2
	 * @return true:同一时间; false:不是同一时间
	 */
	public static boolean isSameInstant(Date date1, Date date2) {
		boolean isSameInstant = false;
		if (date1 != null && date2 != null) {
			isSameInstant = org.apache.commons.lang3.time.
					DateUtils.isSameInstant(date1, date2);
		}
		return isSameInstant;
	}

	/**
	 * 校验两个日期是否为同一时间（精确到毫秒�?
	 * @param cal1  日期1
	 * @param cal2  日期2
	 * @return true:同一时间; false:不是同一时间
	 */
	public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
		boolean isSameInstant = false;
		if (cal1 != null && cal2 != null) {
			isSameInstant = org.apache.commons.lang3.time.
					DateUtils.isSameInstant(cal1, cal2);
		}
		return isSameInstant;
	}

	/**
	 * 校验两个日期是否为同一本地时间
	 * @param cal1  日期1
	 * @param cal2  日期2
	 * @return true:同一本地时间; false:不是同一本地时间
	 */
	public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
		boolean isSameLocalTime = false;
		if (cal1 != null && cal2 != null) {
			isSameLocalTime = org.apache.commons.lang3.time.
					DateUtils.isSameLocalTime(cal1, cal2);
		}
		return isSameLocalTime;
	}

}
