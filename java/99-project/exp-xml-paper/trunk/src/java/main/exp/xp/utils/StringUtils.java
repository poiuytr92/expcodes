package exp.xp.utils;

/**
 * <PRE>
 * 字符串工具类
 * </PRE>
 * <B>PROJECT：</B> exp-xml-paper
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-06-01
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class StringUtils {

	/**
	 * 连接字符串
	 * @param strlist 多个字符串
	 * @return 依次连接所有字符串的字符串
	 */
	public static String concat(String... strlist) {
		StringBuilder sb = new StringBuilder();
		for(String str : strlist) {
			sb.append(str);
		}
		return sb.toString();
	}
	
}
