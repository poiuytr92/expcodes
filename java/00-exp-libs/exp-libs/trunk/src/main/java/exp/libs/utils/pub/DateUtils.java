package exp.libs.utils.pub;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 日期工具类.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(DateUtils.class);
	
	/** 私有化构造函数 */
	protected DateUtils() {}

	/**
	 * 是否为同一天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		return org.apache.commons.lang3.time.DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 是否为同一天
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}
		return org.apache.commons.lang3.time.DateUtils.isSameDay(cal1, cal1);
	}

	/**
	 * 是否为同一时间
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameInstant(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		return org.apache.commons.lang3.time.DateUtils.isSameInstant(date1, date2);
	}

	/**
	 * 是否为同一时间
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}
		return org.apache.commons.lang3.time.DateUtils.isSameInstant(cal1, cal2);
	}

	/**
	 * 是否为同一本地时间
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}
		return org.apache.commons.lang3.time.DateUtils.isSameLocalTime(cal1, cal2);
	}

	/**
	 * 尝试使用多种时间格式将字符串转换成为日期对象, 返回最先成功匹配时间格式的对象
	 * @param str
	 * @param parsePatterns
	 * @return 若解析失败则返回 new Date(0)
	 */
	public static Date parseDate(String str, String... parsePatterns) {
		Date date = new Date(0);
		try {
			date = org.apache.commons.lang3.time.
					DateUtils.parseDate(str, parsePatterns);
		} catch (ParseException e) {
			log.error("解析字符串[{}] 为时间格式失败.", e);
		}
		return date;
	}
	
	/**
	 * 尝试使用多种时间格式将字符串转换成为日期对象, 返回最先成功匹配时间格式的对象.
	 * 此方法严格按照实际日期标准转换, 如2月30日是非法日期, 则作为解析失败处理.
	 * @param str
	 * @param parsePatterns
	 * @return 若解析失败则返回 new Date(0)
	 */
	public static Date parseDateStrictly(String str, String... parsePatterns) {
		Date date = new Date(0);
		try {
			date = org.apache.commons.lang3.time.
					DateUtils.parseDateStrictly(str, parsePatterns);
		} catch (ParseException e) {
			log.error("解析字符串[{}] 为时间格式失败.", e);
		}
		return date;
	}

	/**
	 * 增减日期年份
	 * @param date
	 * @param amount
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
	 * @param date
	 * @param amount
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
	 * 以周为单位增减日期时间
	 * @param date
	 * @param amount
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
	 * @param date
	 * @param amount
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
	 * @param date
	 * @param amount
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addHours(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addHours(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期小时数失败.", e);
		}
		return newDate;
	}

	/**
	 * 以分钟为单位增减日期时间
	 * @param date
	 * @param amount
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addMinutes(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addMinutes(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期分钟数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 以秒为单位增减日期时间
	 * @param date
	 * @param amount
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
	 * @param date
	 * @param amount
	 * @return 若失败则返回变化前的时间
	 */
	public static Date addMilliseconds(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.addMilliseconds(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("增减日期毫秒数失败.", e);
		}
		return newDate;
	}

	/**
	 * 设置日期中的年份
	 * @param date
	 * @param amount
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setYears(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setYears(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期年份失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的月份
	 * @param date
	 * @param amount
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setMonths(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setMonths(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期月份失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的天数
	 * @param date
	 * @param amount
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setDays(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setDays(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期天数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的小时数
	 * @param date
	 * @param amount
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setHours(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setHours(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期小时数失败.", e);
		}
		return newDate;
	}

	/**
	 * 设置日期中的分钟数
	 * @param date
	 * @param amount
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setMinutes(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setMinutes(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期分钟数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的秒数
	 * @param date
	 * @param amount
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setSeconds(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setSeconds(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期秒数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 设置日期中的毫秒数
	 * @param date
	 * @param amount
	 * @return 若失败则返回设置前的时间
	 */
	public static Date setMilliseconds(Date date, int amount) {
		Date newDate = new Date(0);
		try {
			newDate = org.apache.commons.lang3.time.
					DateUtils.setMilliseconds(date, amount);
		} catch (Exception e) {
			newDate = date;
			log.error("设置日期秒毫秒数失败.", e);
		}
		return newDate;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[天]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInDays(Calendar cal, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInDays(cal, fragment);
		} catch (Exception e) {
			log.error("截取天数时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[月份]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInDays(Date date, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInDays(date, fragment);
		} catch (Exception e) {
			log.error("截取天数时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[小时]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInHours(Calendar cal, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInHours(cal, fragment);
		} catch (Exception e) {
			log.error("截取小时时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[小时]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInHours(Date date, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInHours(date, fragment);
		} catch (Exception e) {
			log.error("截取小时时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[分钟]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInMinutes(Calendar cal, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInMinutes(cal, fragment);
		} catch (Exception e) {
			log.error("截取分钟时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[分钟]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInMinutes(Date date, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInMinutes(date, fragment);
		} catch (Exception e) {
			log.error("截取分钟时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[秒]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInSeconds(Calendar cal, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInSeconds(cal, fragment);
		} catch (Exception e) {
			log.error("截取毫秒时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[秒]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInSeconds(Date date, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInSeconds(date, fragment);
		} catch (Exception e) {
			log.error("截取毫秒时间失败.", e);
		}
		return millis;
	}

	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[毫秒]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInMilliseconds(Calendar cal, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInMilliseconds(cal, fragment);
		} catch (Exception e) {
			log.error("截取毫秒时间失败.", e);
		}
		return millis;
	}
	
	/**
	 * 截取指定单位（秒，分钟，小时，月，年）以下的时间，大于该单位的被舍弃.
	 * 返回的数字以[毫秒]为单位.
	 * @param date
	 * @param fragment
	 * @return 截取失败返回0
	 */
	public static long getFragmentInMilliseconds(Date date, int fragment) {
		long millis = 0L;
		try {
			millis = org.apache.commons.lang3.time.
					DateUtils.getFragmentInMilliseconds(date, fragment);
		} catch (Exception e) {
			log.error("截取毫秒时间失败.", e);
		}
		return millis;
	}

}
