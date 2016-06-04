package exp.libs.utils.log;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import exp.libs.utils.pub.StrUtils;

public class LogUtils {

	public static void loadLogBackConfig() {
		loadLogBackConfig(null);
	}
		
	public static void loadLogBackConfig(String logbackConfPath) {
		if(StrUtils.isEmpty(logbackConfPath)) {
			logbackConfPath = "./conf/logback.xml";
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
