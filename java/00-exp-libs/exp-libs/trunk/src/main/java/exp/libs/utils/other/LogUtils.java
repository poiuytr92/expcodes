package exp.libs.utils.other;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

/**
 * <PRE>
 * 日志工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-18
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LogUtils {

	private final static String LOGBACK_PATH = "./conf/logback.xml";
	
	/** 私有化构造函�? */
	protected LogUtils() {}
	
	/**
	 * 加载logback日志配置文件(默认路径�?./conf/logback.xml)
	 */
	public static void loadLogBackConfig() {
		loadLogBackConfig(LOGBACK_PATH);
	}
	
	/**
	 * 加载logback日志配置文件
	 * @param logbackConfPath 日志配置文件路径
	 */
	public static void loadLogBackConfig(String logbackConfPath) {
		if(StrUtils.isEmpty(logbackConfPath)) {
			logbackConfPath = LOGBACK_PATH;
		}
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		
		try {
			configurator.doConfigure(logbackConfPath);
		} catch (Exception e) {
			System.err.println(
					"Fail to load logBack configure file: " + logbackConfPath);
		}
	}
	
}
