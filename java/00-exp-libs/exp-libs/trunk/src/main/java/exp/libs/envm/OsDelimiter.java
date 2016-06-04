package exp.libs.envm;

/**
 * <pre>
 * 枚举类：操作系统的行分隔符
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class OsDelimiter {

	/** 当前操作平台所用的默认行分隔符 */
	public static String DEFAULT = System.getProperty("line.separator");
	
	/** WINDOWS操作平台，分隔符为\r\n */
	public static String WINDOWS = "\r\n";

	/** DOS操作平台，分隔符为\r\n */
	public static String DOS = "\r\n";

	/** LINUX操作平台，分隔符为\n */
	public static String LINUX = "\n";

	/** UNIX操作平台，分隔符为\n */
	public static String UNIX = "\n";

	/** MAC操作平台，分隔符为\r */
	public static String MAC = "\r";

	/** 无操作平台，分隔符为\0 */
	public static String NUL = "\0";
	
}
