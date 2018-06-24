package exp.libs.mrp;

/**
 * <PRE>
 * 控制台日志类.
 * </PRE>
 * <B>PROJECT：</B> mojo-release-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-15
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class Log {

	public static void debug(String msg) {
		System.out.println("[MRP] [DEBUG] ".concat(msg));
	}
	
	public static void info(String msg) {
		System.out.println("[MRP] [INFO] ".concat(msg));
	}
	
	public static void warn(String msg) {
		System.out.println("[MRP] [WARN] ".concat(msg));
	}

	public static void error(String msg) {
		System.err.println("[MRP] [ERROR] ".concat(msg));
	}
	
	public static void error(String msg, Throwable e) {
		error(msg);
		e.printStackTrace();
	}
	
}
