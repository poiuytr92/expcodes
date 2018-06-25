package exp.libs.jvm;

import java.lang.instrument.Instrumentation;

/**
 * <PRE>
 * JVM实例代理类.
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class JVMAgent {
	
	/** JVM实例代理 */
	private static Instrumentation instrumentation;

	/**
	 * 代理程序入口，在main方法执行前执�?.
	 * @param agentArgs 代理程序参数（类比于main参数�?
	 * @param instP JVM实例代理（由JVM自动提供�?
	 */
	public static void premain(String agentArgs, Instrumentation inst) {
		instrumentation = inst;
	}
	
	/**
	 * 获取JVM代理对象
	 * @return JVM代理对象
	 */
	public static Instrumentation getInstn() {
		return instrumentation;
	}
	
}