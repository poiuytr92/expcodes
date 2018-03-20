package exp.crawler.qq.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.StrUtils;
import exp.crawler.qq.ui.AppUI;

/**
 * <PRE>
 * 界面工具类
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class UIUtils {

	private final static Logger log = LoggerFactory.getLogger(UIUtils.class);
	
	protected UIUtils() {}
	
	public static void log(Object... msgs) {
		log(StrUtils.concat(msgs));
	}
	
	public static void log(String msg) {
		log.info(msg);
		msg = StrUtils.concat(TimeUtils.getCurTime(), msg);
		AppUI.getInstn().toConsole(msg);
	}
	
}
