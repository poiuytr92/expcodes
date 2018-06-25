package exp.libs.envm;

/**
 * <PRE>
 * 枚举类：编码
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Charset {

	/** 系统默认编码 */
	public final static String DEFAULT = System.getProperty("sun.jnu.encoding");
	
	/** UNICODE编码 */
	public final static String UNICODE = "UNICODE";
	
	/** UTF-8编码 */
	public final static String UTF8 = "UTF-8";
	
	/** GBK编码（繁简�? */
	public final static String GBK = "GBK";
	
	/** GB2312编码（简�? */
	public final static String GB2312 = "GB2312";
	
	/** ASCII编码（ISO-8859-1�? */
	public final static String ASCII = "ISO-8859-1";
	
	/** ISO-8859-1编码  */
	public final static String ISO = "ISO-8859-1";
	
}
