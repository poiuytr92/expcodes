package exp.token.utils;

/**
 * <PRE>
 * OS工具类
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class OsUtils {

	/** 用于判断操作平台类型的系统属性 */
	private final static String OS_NAME = 
			System.getProperty("os.name").toLowerCase(); 
	
	/** 用于判断操作系统位宽的系统属性 */
	private final static String OS_ARCH = 
			System.getProperty("os.arch").toLowerCase();
	
	/**
	 * 私有化构造函数，避免误用.
	 */
	private OsUtils() {}
	
	/**
	 * 判断当前操作系统是否为windows
	 * @return true:windows; false:linux/mac
	 */
	public static boolean isWin() {
		boolean isWin = true;
		
		if(OS_NAME.contains("win")) {
			isWin = true;
			
		} else if(OS_NAME.contains("mac")) {
			isWin = false;	//暂不可能mac平台上运行, 否则这段代码需修改
			
		} else {
			isWin = false;	//linux
		}
		return isWin;
	}
	
	/**
	 * 判断当前操作系统位宽是否为32位.
	 * （主要针对win, linux由于兼容32和64, 只能用64位）.
	 * 
	 * os 32位： x86
	 * os 64位：amd64
	 * linux 32位: i386
	 * linux 64位：amd64
	 * 
	 * @return true:64; false:32
	 */
	public static boolean isX64() {
		return OS_ARCH.contains("64");
	}
	
}
