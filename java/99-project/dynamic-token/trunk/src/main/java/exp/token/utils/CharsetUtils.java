package exp.token.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * 字符集工具类
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CharsetUtils {

	/**
	 * 私有化构造函数，避免误用.
	 */
	private CharsetUtils() {}
	
	/**
	 * 校验字符串是否包含中文
	 * @param s 字符串
	 * @return true:包含中文; false:不包含中文
	 */
	public static boolean isASCII(final String s) {
		boolean isASCII = false;
		if(s != null) {
			Pattern ptn = Pattern.compile("[\u4e00-\u9fa5]");	//unicode中文标识
			Matcher mth = ptn.matcher(s);
			if(mth.find()) {
				isASCII = false;
			} else {
				isASCII = true;
			}
		}
		return isASCII;
	}
	
	/**
	 * 从字符串中获取属于"单词"的字符:
	 * 只保留 a-zA-Z0-9_ 的单词字符, 其余字符均用 ? 替代.
	 * @param s 原字符串
	 * @return 只含"单词"字符的字符串
	 */
	public static String getWordChars(final String s) {
		StringBuilder en = new StringBuilder();
		if(s != null) {
			char[] chs = s.toCharArray();
			for(char ch : chs) {
				if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || 
						(ch >= '0' && ch <= '9') || ch == '_') {
					en.append(ch);
				} else {
					en.append("?");
				}
			}
		}
		return en.toString();
	}
	
}
