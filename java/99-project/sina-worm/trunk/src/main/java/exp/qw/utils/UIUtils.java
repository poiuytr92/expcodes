package exp.qw.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.qw.AppUI;

public class UIUtils {

	private final static Logger log = LoggerFactory.getLogger(UIUtils.class);
	
	public static void BeautySwing() {
		BeautyEyeUtils.init();
	}
	
	public static void log(String msg) {
		log.info(msg);
		msg = StrUtils.concat(TimeUtils.getCurTimePrefix(), msg);
		AppUI.getInstn().appendLog(msg);
	}
	
	public static void log(Object... msg) {
		if(msg == null) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		if(msg != null) {
			for(Object o : msg) {
				if(o == null) {
					continue;
				}
				sb.append(o.toString());
			}
		}
		log(sb.toString());
	}
	
}
