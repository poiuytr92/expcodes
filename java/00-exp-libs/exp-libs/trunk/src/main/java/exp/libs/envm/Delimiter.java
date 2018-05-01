package exp.libs.envm;

/**
 * <pre>
 * 枚举类：分隔符
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Delimiter {

	/** 当前操作平台所用的默认行分隔符 */
	public static String DEFAULT = System.getProperty("line.separator");
	
	/** 回车符 */
	public static String CR = "\r";
	
	/** 换行符 */
	public static String LF = "\n";
	
	/** 回车换行符 */
	public static String CRLF = CR.concat(LF);
	
	/** 无操作平台，分隔符为\0 */
	public static String NUL = "\0";
	
	/** WINDOWS操作平台，分隔符为\r\n */
	public static String WINDOWS = CRLF;

	/** DOS操作平台，分隔符为\r\n */
	public static String DOS = CRLF;

	/** LINUX操作平台，分隔符为\n */
	public static String LINUX = LF;

	/** UNIX操作平台，分隔符为\n */
	public static String UNIX = LF;

	/** MAC操作平台，分隔符为\r */
	public static String MAC = CR;

}
