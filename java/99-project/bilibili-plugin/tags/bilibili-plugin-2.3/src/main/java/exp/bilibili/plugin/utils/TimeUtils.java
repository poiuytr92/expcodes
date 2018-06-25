package exp.bilibili.plugin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final static Logger log = LoggerFactory.getLogger(TimeUtils.class);
	
	private final static String GMT_FORMAT = "EEE, dd-MMM-yyyy HH:mm:ss z";
	
	protected TimeUtils() {}
	
	public static String getCurTime() {
		String time = toStr(System.currentTimeMillis(), "HH:mm:ss");
		return StrUtils.concat("[", time, "] ");
	}
	
	/**
	 * 把cookie中的有效时间转换为日�?
	 * @param expires 有效时间,格式�?: Tue, 06-Feb-2018 11:54:42 GMT
	 * @return
	 */
	public static Date toDate(String expires) {
		Date date = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(GMT_FORMAT, Locale.ENGLISH); 
	        date = sdf.parse(expires);
	        
		} catch(Exception e) {
			log.error("转换时间失败: {}", expires, e);
		}
		return date;
	}
	
	/**
	 * 把日期转换为cookie中的有效时间
	 * @param date 日期
	 * @return 有效时间,格式�?: Tue, 06-Feb-2018 11:54:42 GMT
	 */
	public static String toExpires(Date date) {
		String sDate = "Thu, 01-Jan-1970 08:00:00 GMT+08:00";
		if(date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(GMT_FORMAT, Locale.ENGLISH);
			sDate = sdf.format(date);
		}
		return sDate;
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
