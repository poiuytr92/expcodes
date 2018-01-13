package exp.bilibili.plugin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 时间工具类
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
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
	 * 把cookie中的有效时间转换为日期
	 * @param expires 有效时间,格式如: Tue, 06-Feb-2018 11:54:42 GMT
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
	 * 检查当前时间是否为晚上(18:00~24:00)
	 * @return
	 */
	public static boolean isNight() {
		int hour = TimeUtils.getCurHour(8);	// 中国8小时时差
		return (hour >= 18 && hour < 24);
	}
	
}
