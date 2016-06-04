package exp.xp.utils;

/**
 * <pre>
 * 字符串工具类
 * </pre> 
 * @version 1.0 by 2015-06-01
 * @since   jdk版本：1.6
 * @author  Exp - liaoquanbin
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
