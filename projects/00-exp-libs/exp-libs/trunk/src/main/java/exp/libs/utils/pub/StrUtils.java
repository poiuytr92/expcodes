package exp.libs.utils.pub;

import java.util.List;

/**
 * <PRE>
 * 字符串处理工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class StrUtils {

	protected StrUtils() {}
	
	/**
	 * 判断字符串是否为空
	 * @param s 字符串
	 * @return true:s为null或""; false:反之
	 */
	public static boolean isEmpty(String s) {
		boolean isEmpty = false;
		if(s == null || "".equals(s)) {
			isEmpty = true;
		}
		return isEmpty;
	}
	
	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}
	
	/**
	 * 判断字符串是否全为空白字符
	 * @param s 待判断字符串
	 * @return 是否全空白字符串
	 */
	public static boolean isBlank(String s) {
		if (s == null) {
			return false;
		}
		return isEmpty(s.trim());
	}
	
	public static String toNotNull(String str) {
		return (str == null ? "" : str);
	}
	
	/**
	 * 字符串倒转
	 * @param str 原字符串
	 * @return 倒转的字符串
	 */
	public static String reverse(String str) {
		if (str == null) {
			return "";
		}
		return new StringBuilder(str).reverse().toString();
	}
	
	public static String multiChar(char c, int num) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < num; i++) {
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * 连接多个对象为一个字符串
	 * @param objlist 多个对象
	 * @return 依次连接所有对象的字符串
	 */
	public static String concat(Object... objlist) {
		StringBuilder sb = new StringBuilder();
		if(objlist != null) {
			for(Object o : objlist) {
				if(o == null) {
					continue;
				}
				sb.append(o.toString());
			}
		}
		return sb.toString();
	}
	
	/**
	 * 连接多个字符串为一个字符串
	 * @param strlist 多个字符串
	 * @return 依次连接所有字符串的字符串
	 */
	public static String concat(String... strlist) {
		StringBuilder sb = new StringBuilder();
		if(strlist != null) {
			for(String str : strlist) {
				if(str == null) {
					continue;
				}
				sb.append(str);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 使用指定分隔符连接字符串
	 * @param list 字符串列表
	 * @param separator 分隔符
	 * @return 依次连接所有字符串的字符串
	 */
	public static String concat(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		if(list != null) {
			separator = (separator == null ? "" : separator);
			for(String str : list) {
				if(str == null) {
					continue;
				}
				sb.append(str).append(separator);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 转换字符串的第一个字母大写
	 * 
	 * @param str 要转换字符串
	 * @return 转换后的结果 ，如： string==>String
	 */
	public static String upperAtFirst(String str) {
		String first = str.substring(0, 1);
		String suffix = str.substring(1, str.length());
		return StrUtils.concat(first.toUpperCase(), suffix);
	}
	
	/**
	 * 转换字符串的第一个字母为小写
	 * 
	 * @param str 要转换字符串
	 * @return 转换后的结果 ，如： String==>string
	 */
	public static String lowerAtFirst(String str) {
		String first = str.substring(0, 1);
		String suffix = str.substring(1, str.length());
		return StrUtils.concat(first.toLowerCase(), suffix);
	}
	
	/**
	 * 令字符串中的所有空字符可视
	 * @param str 字符串
	 * @return 所含空字符变成可视字符的字符串
	 */
	public static String view(final String str) {
		StringBuilder sb = new StringBuilder();
		char[] cs = str.toCharArray();
		for(char c : cs) {
			sb.append(view(c));
		}
		return sb.toString();
	}
	
	/**
	 * 令空字符变成可视字符串
	 * @param emptyChar 空字符
	 * @return 可视字符
	 */
	public static String view(final char emptyChar) {
		String str = null;
		switch(emptyChar) {
			case '\\' : { str = "\\\\"; break; } 
			case '\t' : { str = "\\t"; break; } 
			case '\r' : { str = "\\r"; break; } 
			case '\n' : { str = "\\n"; break; } 
			case '\0' : { str = "\\0"; break; } 
			case '\b' : { str = "\\b"; break; } 
			default : { str = String.valueOf(emptyChar); break; }
		}
		return str;
	}
	
	/**
	 * 在字符串s[左边]用字符c补长, 目标长度是size.
	 * @param s 待填充的字符串
	 * @param c 用于填充的字符
	 * @param size 填充完成后字符串的总长度
	 * @return 左边填充了字符c且长度为size的字符串
	 */
	public static String leftPad(final String s, final char c, final int size) {
		String str = (s == null ? "" : s);
		int fillCnt = size - str.length();
		if(fillCnt > 0) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < fillCnt; i++) {
				sb.append(c);
			}
			sb.append(str);
			str = sb.toString();
		}
		return str;
	}
	
	/**
	 * 在字符串s[右边]用字符c补长, 目标长度是size.
	 * @param s 待填充的字符串
	 * @param c 用于填充的字符
	 * @param size 填充完成后字符串的总长度
	 * @return 右边填充了字符c且长度为size的字符串
	 */
	public static String rightPad(final String s, final char c, final int size) {
		String str = (s == null ? "" : s);
		int fillCnt = size - str.length();
		if(fillCnt > 0) {
			StringBuilder sb = new StringBuilder(str);
			for(int i = 0; i < fillCnt; i++) {
				sb.append(c);
			}
			str = sb.toString();
		}
		return str;
	}
	
	/**
	 * 移除字符串中的头尾空字符
	 * @param str 原字符串
	 * @return 移除空字符后的字符串
	 */
	public static String trim(String str) {
		return (str == null ? "" : str.trim());
	}
	
	/**
	 * 移除字符串中的所有空字符
	 * @param str 原字符串
	 * @return 移除空字符后的字符串
	 */
	public static String trimAll(final String str) {
		return (str == null ? "" : str.replaceAll("\\s", ""));
	}
	
	/**
	 * 在字符串特定距离填充空格
	 * @param str 原字符串
	 * @param interval 填充间隔
	 * @return 填充空格后的字符串
	 */
	public static String addSpace(final String str, final int interval) {
		if(interval <= 0) {
			return (str == null ? "" : str);
		}
		
		StringBuilder sb = new StringBuilder();
		if(str != null) {
			int cnt = 0;
			char[] chs = str.toCharArray();
			for(char c : chs) {
				if(++cnt > interval) {	// cnt = interval + 1, 避免str末尾还补空格
					cnt = 1;			// 从第2轮开始计数起点为1，因为上一轮会残留1个字符
					sb.append(' ');
				}
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static String[] split(String s, String bgnDelmiter, String endDelmiter) {
		s = (s == null ? "" : s);
		String[] ss = null;
		
		if(isNotEmpty(bgnDelmiter) && isNotEmpty(endDelmiter)) {
			String regex = concat(ESCUtils.toRegexESC(bgnDelmiter), 
					"([\\s\\S]*?)", ESCUtils.toRegexESC(endDelmiter));
			List<String> subs = RegexUtils.findFirsts(s, regex);
			ss = (String[]) subs.toArray();
			
		} else if(isNotEmpty(bgnDelmiter)) {
			ss = s.split(bgnDelmiter);
			
		} else if(isNotEmpty(endDelmiter)) {
			ss = s.split(endDelmiter);
			
		} else {
			ss = new String[] { s };
		}
		return ss;
	}
	
	/**
	 * 按子串类型切割字符串
	 * @param s
	 * @param delmiter
	 * @param clazz
	 * @return
	 */
	public static Object[] split(String s, String delmiter, Class<?>[] clazz) {
		s = (s == null ? "" : s);
		delmiter = (delmiter == null ? "" : delmiter);
		if(clazz == null) {
			return s.split(delmiter);
		}
		
		String[] ss = s.split(delmiter, clazz.length);
		Object[] os = new Object[ss.length];
		for (int i = 0; i < os.length; i++) {
			os[i] = ObjUtils.toObj(ss[i], clazz[i]);
		}
		return os;
	}
	
	/**
	 * 把字符串切割成不同长度的子串
	 * @param str 原字符串
	 * @param lens 每个子串的长度
	 * @return 切割后的子串
	 */
	public static String[] split(final String str, final int[] lens) {
		if(lens == null) {
			return (str == null ? new String[] {""} : new String[] {str});
		}
		
		String s = (str == null ? "" : str);
		String[] sAry = new String[lens.length];
		int sLen = s.length();
		
		for(int i = 0; i < lens.length; i++) {
			int len = lens[i];
			
			if(sLen >= len) {
				sAry[i] = s.substring(0, len);
				s = s.substring(len);
				sLen -= len;
				
			} else {
				sAry[i] = s.substring(0, sLen);
				s = "";
				sLen = 0;
			}
		}
		return sAry;
	}
	
	/**
	 * 从字符串s中截取bgn（不包括）和end（不包括）之间的子串
	 * @param s
	 * @param bgn
	 * @param end
	 * @return
	 */
	public static String substr(String s, String bgn, String end) {
		String sub = "";
		if(isNotEmpty(s)) {
			bgn = (bgn == null ? "" : bgn);
			end = (end == null ? "" : end);
			
			int bgnIdx = s.indexOf(bgn);
			int bgnLen = bgn.length();
			if(bgnIdx < 0) {
				sub = "";
			} else {
				sub = s.substring(bgnIdx + bgnLen);
			}
			
			if(isNotEmpty(sub)) {
				int endIdx = sub.indexOf(end);
				if(endIdx < 0) {
					sub = "";
				} else if(endIdx == 0) {
					// UNDO
				} else {
					sub = sub.substring(0, endIdx);
				}
			}
		}
		return sub;
	}
	
	/**
	 * 在字符串s中截取第amount次出现的mark（不包括）之前的子串
	 * @param s
	 * @param mark
	 * @param amount
	 * @return
	 */
	public static String substr(String s, String mark, int amount) {
		String sub = (s == null ? "" : s);
		if(isNotEmpty(s) && isNotEmpty(mark) && amount > 0) {
			int len = mark.length();
			int sumIdx = 0;
			int subIdx = -1;
			int cnt = 0;
			do {
				subIdx = sub.indexOf(mark);
				if(subIdx < 0) {
					break;
				}
				
				sumIdx += (subIdx + len);
				sub = sub.substring(subIdx + len);
			} while(++cnt < amount);
			sub = (subIdx >= 0 ? s.substring(0, sumIdx - 1) : s);
		}
		return sub;
	}
	
	/**
	 * 截取字符串摘要
	 * @param str
	 * @return
	 */
	public static String showSummary(String str) {
		String summary = "";
		if(str != null) {
			if(str.length() > 128) {
				summary = concat(str.substring(0, 128), "...");
				
			} else {
				summary = str;
			}
		}
		return summary;
	}
	
	/**
	 * 计算字符串中指定字符出现的个数
	 * @param s
	 * @param c
	 * @return
	 */
	public static int count(final String s, final char c) {
		int cnt = 0;
		if(s != null) {
			char[] chArray = s.toCharArray();
			for(char ch : chArray) {
				if(ch == c) {
					cnt++;
				}
			}
		}
		return cnt;
	}
	
	/**
	 * 获取字符串中的中文个数
	 * @param s
	 * @return
	 */
	public static int chineseCnt(final String s) {
		int cnt = 0;
		if(s != null) {
			char[] cs = s.toCharArray();
			for(char c : cs) {
				cnt += (VerifyUtils.isChinese(c) ? 1 : 0);
			}
		}
		return cnt;
	}
	
	/**
	 * 以 [1中文长度=2英文长度] 换算字符串长度
	 * @param s
	 * @return
	 */
	public static int chineseLen(final String s) {
		int len = 0;
		if(s != null) {
			len = s.length() + chineseCnt(s);
		}
		return len;
	}
	
}
