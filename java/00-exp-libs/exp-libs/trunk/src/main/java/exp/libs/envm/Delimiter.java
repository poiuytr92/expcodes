package exp.libs.envm;

/**
 * <pre>
 * 枚举类：分隔符
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Delimiter {

	/** 当前操作平台所用的默认行分隔符 */
	public final static String DEFAULT = System.getProperty("line.separator");
	
	/** 回车�? */
	public final static String CR = "\r";
	
	/** 换行�? */
	public final static String LF = "\n";
	
	/** 回车换行�? */
	public final static String CRLF = CR.concat(LF);
	
	/** 无操作平台，分隔符为\0 */
	public final static String NUL = "\0";
	
	/** WINDOWS操作平台，分隔符为\r\n */
	public final static String WINDOWS = CRLF;

	/** DOS操作平台，分隔符为\r\n */
	public final static String DOS = CRLF;

	/** LINUX操作平台，分隔符为\n */
	public final static String LINUX = LF;

	/** UNIX操作平台，分隔符为\n */
	public final static String UNIX = LF;

	/** MAC操作平台，分隔符为\r */
	public final static String MAC = CR;

}
