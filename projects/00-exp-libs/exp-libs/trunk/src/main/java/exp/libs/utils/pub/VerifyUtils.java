package exp.libs.utils.pub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exp.libs.envm.Regex;

/**
 * <PRE>
 * 校验工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class VerifyUtils {

	/** 私有化构造方法 */
	protected VerifyUtils() { }

	public static boolean isAscii(final char c) {
		return (c >= 0x00 && c <= 0x7F);
	}
	
	/**
	 * 判断字符是不是ascii控制字符.
	 * @param ch
	 * @return
	 */
	public static boolean isAsciiCtrl(final char ch) {
		return ch < 32 || ch == 127;
	}
	
	public static boolean isRealNumber(final String str) {
		return RegexUtils.matches(str, Regex.REAL.VAL);
	}
	
	public static boolean isPositiveReal(final String str) {
		return RegexUtils.matches(str, Regex.REAL_POSITIVE.VAL);
	}
	
	public static boolean isNegativeReal(final String str) {
		return RegexUtils.matches(str, Regex.REAL_NEGATIVE.VAL);
	}
	
	public static boolean isNotNegativeReal(final String str) {
		return RegexUtils.matches(str, Regex.REAL_NOT_NEGATIVE.VAL);
	}
	
	public static boolean isIntegerNumber(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER.VAL);
	}
	
	public static boolean isPositiveInteger(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER_POSITIVE.VAL);
	}
	
	public static boolean isNegativeInteger(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER_NEGATIVE.VAL);
	}
	
	public static boolean isNotNegativeInteger(final String str) {
		return RegexUtils.matches(str, Regex.INTEGER_NOT_NEGATIVE.VAL);
	}
	
	public static boolean isFloatNumber(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT.VAL);
	}
	
	public static boolean isPositiveFloat(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT_POSITIVE.VAL);
	}
	
	public static boolean isNegativeFloat(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT_NEGATIVE.VAL);
	}
	
	public static boolean isNotNegativeFloat(final String str) {
		return RegexUtils.matches(str, Regex.FLOAT_NOT_NEGATIVE.VAL);
	}
	
	public static boolean isDigits(final String str) {
		return RegexUtils.matches(str, Regex.DIGITS.VAL);
	}
	
	public static boolean isLetter(final String str) {
		return RegexUtils.matches(str, Regex.LETTER.VAL);
	}
	
	public static boolean isLetter(final char c) {
		boolean isMatch = false;
		if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			isMatch = true;
		}
		return isMatch;
	}
	
	public static boolean isUpperLetter(final String str) {
		return RegexUtils.matches(str, Regex.LETTER_UPPER.VAL);
	}
	
	public static boolean isUpperLetter(final char c) {
		return (c >= 'A' && c <= 'Z');
	}
	
	public static boolean isLowerLetter(final String str) {
		return RegexUtils.matches(str, Regex.LETTER_LOWER.VAL);
	}
	
	public static boolean isLowerLetter(final char c) {
		return (c >= 'a' && c <= 'z');
	}
	
	public static boolean isDigitsOrLetter(final String str) {
		return RegexUtils.matches(str, Regex.DIGITS_LETTER.VAL);
	}
	
	public static boolean isDigitsOrLetter(final char c) {
		boolean isMatch = false;
		if((c >= '0' && c <= '9') || 
				(c >= 'a' && c <= 'z') || 
				(c >= 'A' && c <= 'Z')) {
			isMatch = true;
		}
		return isMatch;
	}
	
	public static boolean isUsername(final String str) {
		return RegexUtils.matches(str, Regex.USERNAME.VAL);
	}
	
	public static boolean isPassword(final String str) {
		return RegexUtils.matches(str, Regex.PASSWORD.VAL);
	}
	
	public static boolean isFullwidth(final String str) {
		return RegexUtils.matches(str, Regex.FULL_WIDTH_CHAR.VAL);
	}
	
	public static boolean isEmail(final String str) {
		return RegexUtils.matches(str, Regex.EMAIL.VAL);
	}
	
	public static boolean isHttp(final String str) {
		return RegexUtils.matches(str, Regex.HTTP.VAL);
	}
	
	public static boolean isTelephone(final String str) {
		return RegexUtils.matches(str, Regex.TELEPHONE.VAL);
	}
	
	public static boolean isMobilePhone(final String str) {
		return RegexUtils.matches(str, Regex.MOBILEPHONE.VAL);
	}
	
	public static boolean isIdentity(final String str) {
		return RegexUtils.matches(str, Regex.ID_CARD.VAL);
	}
	
	public static boolean isYear(final String str) {
		return RegexUtils.matches(str, Regex.YEAR.VAL);
	}
	
	public static boolean isMonth(final String str) {
		return RegexUtils.matches(str, Regex.MONTH.VAL);
	}
	
	public static boolean isDay(final String str) {
		return RegexUtils.matches(str, Regex.DAY.VAL);
	}
	
	public static boolean isHour(final String str) {
		return RegexUtils.matches(str, Regex.HOUR.VAL);
	}
	
	public static boolean isMinute(final String str) {
		return RegexUtils.matches(str, Regex.MINUTE.VAL);
	}
	
	public static boolean isSecond(final String str) {
		return RegexUtils.matches(str, Regex.SECOND.VAL);
	}
	
	public static boolean isMillis(final String str) {
		return RegexUtils.matches(str, Regex.MILLIS.VAL);
	}
	
	public static boolean isDate(final String str) {
		return RegexUtils.matches(str, Regex.DATE.VAL);
	}
	
	public static boolean isTime(final String str) {
		return RegexUtils.matches(str, Regex.TIME.VAL);
	}
	
	public static boolean isDateTime(final String str) {
		return RegexUtils.matches(str, Regex.DATE_TIME.VAL);
	}
	
	public static boolean isMac(final String str) {
		return RegexUtils.matches(str, Regex.MAC.VAL);
	}
	
	public static boolean isIp(final String str) {
		return isIpv4(str);
	}
	
	public static boolean isIpv4(final String str) {
		return RegexUtils.matches(str, Regex.IPV4.VAL);
	}
	
	public static boolean isIpv6(final String str) {
		return RegexUtils.matches(str, Regex.IPV6.VAL);
	}
	
	public static boolean isPort(final String str) {
		return RegexUtils.matches(str, Regex.PORT.VAL);
	}
	
	public static boolean isPort(final int port) {
		return (port >= 0 && port <= 65535);
	}
	
	public static boolean isSocket(final String str) {
		return RegexUtils.matches(str, Regex.SOCKET.VAL);
	}
	
	@Deprecated
	public static boolean isChinese(final String str) {
		return RegexUtils.matches(str, Regex.CHINESE.VAL);
	}
	
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
	 * 判断字符串中是否存在乱码
	 * @param str 
	 * @return
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
