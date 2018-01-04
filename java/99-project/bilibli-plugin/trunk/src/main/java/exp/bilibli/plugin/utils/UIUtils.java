package exp.bilibli.plugin.utils;

import java.awt.Toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.core.front.AppUI;
import exp.bilibli.plugin.core.front._NoticeUI;
import exp.bilibli.plugin.envm.ChatColor;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 界面工具类
 * </PRE>
 * <B>PROJECT：</B> exp-libs
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
	
	public static void chat(Object... msgs) {
		chat(StrUtils.concat(msgs));
	}
	
	public static void chat(String msg) {
		msg = StrUtils.concat(TimeUtils.getCurTime(), msg);
		AppUI.getInstn().toChat(msg);
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
	}
	
	public static void updateLotteryCnt() {
		AppUI.getInstn().updateLotteryCnt(1);
	}
	
	public static void updateLotteryCnt(int num) {
		AppUI.getInstn().updateLotteryCnt(num);
	}
	
	public static void printVersionInfo() {
		AppUI.getInstn().printVersionInfo();
	}
	
	public static String getCurRoomId() {
		return AppUI.getInstn().getRoomId();
	}
	
	public static ChatColor getCurChatColor() {
		return AppUI.getInstn().getCurChatColor();
	}
	
	public static boolean isBackLotteryMode() {
		return AppUI.getInstn().isBackLotteryMode();
	}
	
	public static void notityLive(String roomId) {
		new _NoticeUI(roomId)._show();		// 右下角通知提示
		Toolkit.getDefaultToolkit().beep();	// 蜂鸣音提示
	}
	
}
