package exp.libs.utils.verify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exp.libs.envm.Regex;

/**
 * <PRE>
 * 校验工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class VerifyUtils {

	/** 私有化构造方�? */
	protected VerifyUtils() {}

	/**
	 * 测试字符是否为[ASCII字符].
	 * @param c 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isASCII(final char c) {
		return (c >= 0x00 && c <= 0x7F);
	}
	
	/**
	 * 测试字符是否为[ASCII控制字符].
	 * @param ch 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isASCIICtrl(final char c) {
		return c < 32 || c == 127;
	}
	
	/**
	 * 测试字符串是否为[实数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isRealNumber(final String str) {
		return RegexUtils.matches(str, Regex.REAL.VAL);
	}
	
	/**
	 * 测试字符串是否为[正实数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isPositiveReal(final String str) {
		return RegexUtils.matches(str, Regex.REAL_POSITIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[负实数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isNegativeReal(final String str) {
		return RegexUtils.matches(str, Regex.REAL_NEGATIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[非负实数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isNotNegativeReal(final String str) {
		return RegexUtils.matches(str, Regex.REAL_NOT_NEGATIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[整数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isIntegerNumber(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER.VAL);
	}
	
	/**
	 * 测试字符串是否为[正整数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isPositiveInteger(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER_POSITIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[负整数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isNegativeInteger(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER_NEGATIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[非负整数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isNotNegativeInteger(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER_NOT_NEGATIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[浮点数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isFloatNumber(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT.VAL);
	}
	
	/**
	 * 测试字符串是否为[正浮点数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isPositiveFloat(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT_POSITIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[负浮点数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isNegativeFloat(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT_NEGATIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[非负浮点数].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isNotNegativeFloat(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT_NOT_NEGATIVE.VAL);
	}
	
	/**
	 * 测试字符串是否为[数字].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isDigits(final String str) {
		return RegexUtils.matches(str, Regex.DIGITS.VAL);
	}
	
	/**
	 * 测试字符是否为[数字].
	 * @param c 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isDigits(final char c) {
		boolean isMatch = false;
		if(c >= '0' && c <= '9') {
			isMatch = true;
		}
		return isMatch;
	}
	
	/**
	 * 测试字符串是否为[字母].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isLetter(final String str) {
		return RegexUtils.matches(str, Regex.LETTER.VAL);
	}
	
	/**
	 * 测试字符是否为[字母].
	 * @param c 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isLetter(final char c) {
		boolean isMatch = false;
		if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			isMatch = true;
		}
		return isMatch;
	}
	
	/**
	 * 测试字符串是否为[大写字母].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isUpperLetter(final String str) {
		return RegexUtils.matches(str, Regex.LETTER_UPPER.VAL);
	}
	
	/**
	 * 测试字符是否为[大写字母].
	 * @param c 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isUpperLetter(final char c) {
		return (c >= 'A' && c <= 'Z');
	}
	
	/**
	 * 测试字符串是否为[小写字母].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isLowerLetter(final String str) {
		return RegexUtils.matches(str, Regex.LETTER_LOWER.VAL);
	}
	
	/**
	 * 测试字符是否为[小写字母].
	 * @param c 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isLowerLetter(final char c) {
		return (c >= 'a' && c <= 'z');
	}
	
	/**
	 * 测试字符串是否为[数字或字母].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isDigitsOrLetter(final String str) {
		return RegexUtils.matches(str, Regex.DIGITS_LETTER.VAL);
	}
	
	/**
	 * 测试字符是否为[数字或字母].
	 * @param c 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isDigitsOrLetter(final char c) {
		boolean isMatch = false;
		if((c >= '0' && c <= '9') || 
				(c >= 'a' && c <= 'z') || 
				(c >= 'A' && c <= 'Z')) {
			isMatch = true;
		}
		return isMatch;
	}
	
	/**
	 * 测试字符串是否满足[账号]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isUsername(final String str) {
		return RegexUtils.matches(str, Regex.USERNAME.VAL);
	}
	
	/**
	 * 测试字符串是否满足[密码]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isPassword(final String str) {
		return RegexUtils.matches(str, Regex.PASSWORD.VAL);
	}
	
	/**
	 * 测试字符串是否为[全角字符].
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isFullwidth(final String str) {
		return RegexUtils.matches(str, Regex.FULL_WIDTH_CHAR.VAL);
	}
	
	/**
	 * 测试字符串是否满足[邮箱]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isEmail(final String str) {
		return RegexUtils.matches(str, Regex.EMAIL.VAL);
	}
	
	/**
	 * 测试字符串是否满足[HTTP地址]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isHttp(final String str) {
		return RegexUtils.matches(str, Regex.HTTP.VAL);
	}
	
	/**
	 * 测试字符串是否满足[固话号码]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isTelephone(final String str) {
		return RegexUtils.matches(str, Regex.TELEPHONE.VAL);
	}
	
	/**
	 * 测试字符串是否满足[手机号码]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isMobilePhone(final String str) {
		return RegexUtils.matches(str, Regex.MOBILEPHONE.VAL);
	}
	
	/**
	 * 测试字符串是否满足[身份证号]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isIdentity(final String str) {
		return RegexUtils.matches(str, Regex.ID_CARD.VAL);
	}
	
	/**
	 * 测试字符串是否满足[日期-年份]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isYear(final String str) {
		return RegexUtils.matches(str, Regex.YEAR.VAL);
	}
	
	/**
	 * 测试字符串是否满足[日期-月份]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isMonth(final String str) {
		return RegexUtils.matches(str, Regex.MONTH.VAL);
	}
	
	/**
	 * 测试字符串是否满足[日期-天]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isDay(final String str) {
		return RegexUtils.matches(str, Regex.DAY.VAL);
	}
	
	/**
	 * 测试字符串是否满足[时间-小时]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isHour(final String str) {
		return RegexUtils.matches(str, Regex.HOUR.VAL);
	}
	
	/**
	 * 测试字符串是否满足[时间-分钟]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isMinute(final String str) {
		return RegexUtils.matches(str, Regex.MINUTE.VAL);
	}
	
	/**
	 * 测试字符串是否满足[时间-秒]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isSecond(final String str) {
		return RegexUtils.matches(str, Regex.SECOND.VAL);
	}
	
	/**
	 * 测试字符串是否满足[时间-毫秒]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isMillis(final String str) {
		return RegexUtils.matches(str, Regex.MILLIS.VAL);
	}
	
	/**
	 * 测试字符串是否满足[日期（年-�?-日））]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isDate(final String str) {
		return RegexUtils.matches(str, Regex.DATE.VAL);
	}
	
	/**
	 * 测试字符串是否满足[时间（时:�?:�?.毫秒）]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isTime(final String str) {
		return RegexUtils.matches(str, Regex.TIME.VAL);
	}
	
	/**
	 * 测试字符串是否满足[日期时间]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isDateTime(final String str) {
		return RegexUtils.matches(str, Regex.DATE_TIME.VAL);
	}
	
	/**
	 * 测试字符串是否满足[MAC地址]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isMac(final String str) {
		return RegexUtils.matches(str, Regex.MAC.VAL);
	}
	
	/**
	 * 测试字符串是否满足[IPv4地址]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isIP(final String str) {
		return isIPv4(str);
	}
	
	/**
	 * 测试字符串是否满足[IPv4地址]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isIPv4(final String str) {
		return RegexUtils.matches(str, Regex.IPV4.VAL);
	}
	
	/**
	 * 测试字符串是否满足[IPv6地址]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isIPv6(final String str) {
		return RegexUtils.matches(str, Regex.IPV6.VAL);
	}
	
	/**
	 * 测试字符串是否满足[端口]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isPort(final String str) {
		return RegexUtils.matches(str, Regex.PORT.VAL);
	}
	
	/**
	 * 测试数字是否满足[端口]的通用定义要求.
	 * @param port 被测试数�?
	 * @return true:�?; false:�?
	 */
	public static boolean isPort(final int port) {
		return (port >= 0 && port <= 65535);
	}
	
	/**
	 * 测试字符串是否满足[socket]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isSocket(final String str) {
		return RegexUtils.matches(str, Regex.SOCKET.VAL);
	}
	
	/**
	 * 测试字符串是否满足[MD5]的通用定义要求.
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isMD5(final String str) {
		return RegexUtils.matches(str, Regex.MD5.VAL);
	}
	
	/**
	 * 测试字符串是否为[中文汉字]
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean isChinese(final String str) {
		return RegexUtils.matches(str, Regex.CHINESE.VAL);
	}
	
	/**
	 * 测试字符是否为[中文汉字]
	 * @param c 被测试字�?
	 * @return true:�?; false:�?
	 */
	public static boolean isChinese(final char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || 
        		ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || 
        		ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || 
        		ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || 
        		ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || 
        		ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || 
        		ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
	}
	
	/**
	 * 判断字符串中是否含有[中文汉字]
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean existChinese(final String str) {
		boolean existChinese = false;
		if(str != null) {
			char[] cs = str.toCharArray();
			for(char c : cs) {
				existChinese = isChinese(c);
				if(existChinese) {
					break;
				}
			}
		}
		return existChinese;
	}
	
	/**
	 * 判断字符串中是否含有[乱码]
	 * @param str 被测试字符串
	 * @return true:�?; false:�?
	 */
	public static boolean existMessyChinese(final String str) {
		Pattern ptn = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher mth = ptn.matcher(str);
		String temp = mth.replaceAll("").replaceAll("\\p{P}", "");
		
		boolean isExist = false;
		char[] ch = temp.trim().toCharArray();
		for(char c : ch) {
			if(!isDigitsOrLetter(c) && !VerifyUtils.isChinese(c) && 
					(c & 128) == 128) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}
	
}
