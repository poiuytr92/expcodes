package exp.bilibli.plugin.utils;

import exp.bilibli.plugin.core.ui.AppUI;
import exp.libs.utils.other.StrUtils;

public class UIUtils {

	protected UIUtils() {}
	
	public static void chat(Object... msgs) {
		chat(StrUtils.concat(msgs));
	}
	
	public static void chat(String msg) {
		msg = StrUtils.concat(TimeUtils.getCurTime(), msg);
		AppUI.getInstn().toChat(msg);
	}
	
	public static void log(Object... msgs) {
		log(StrUtils.concat(msgs));
	}
	
	public static void log(String msg) {
		msg = StrUtils.concat(TimeUtils.getCurTime(), msg);
		AppUI.getInstn().toConsole(msg);
	}
	
	public static void notify(Object... msgs) {
		notify(StrUtils.concat(msgs));
	}
	
	public static void notify(String msg) {
		msg = StrUtils.concat(TimeUtils.getCurTime(), msg);
		AppUI.getInstn().toNotify(msg);
	}
	
	public static void statistics(Object... msgs) {
		statistics(StrUtils.concat(msgs));
	}
	
	public static void statistics(String msg) {
		msg = StrUtils.concat(TimeUtils.getCurTime(), msg);
		AppUI.getInstn().toStatistics(msg);
		AppUI.getInstn().updateLotteryCnt();
	}
	
}
