package exp.crawler.qq.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.StrUtils;
import exp.crawler.qq.ui.AppUI;

/**
 * <PRE>
 * 界面工具类
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class UIUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(UIUtils.class);
	
	/** 私有化构造函�? */
	protected UIUtils() {}
	
	/**
	 * 打印异常日志到界面控制台
	 * @param e
	 * @param msgs
	 */
	public static void log(Throwable e, Object... msgs) {
		log(StrUtils.concat(msgs), e);
	}
	
	/**
	 * 打印异常日志到界面控制台
	 * @param e
	 * @param msg
	 */
	public static void log(Throwable e, String msg) {
		log(msg, e);
	}
	
	/**
	 * 打印日志到界面控制台
	 * @param msgs
	 */
	public static void log(Object... msgs) {
		log(StrUtils.concat(msgs), null);
	}
	
	/**
	 * 打印日志到界面控制台
	 * @param msgs
	 */
	public static void log(String msg) {
		log(msg, null);
	}
	
	/**
	 * 打印日志到界面控制台
	 * @param msg
	 * @param e
	 */
	private static void log(String msg, Throwable e) {
		if(e != null) {
			log.error("[ERROR] {}", msg, e);
			
		} else {
			log.info(msg);
		}
		
		msg = StrUtils.concat(TimeUtils.getCurTime(), msg);
		AppUI.getInstn().toConsole(msg);
	}
	
}
