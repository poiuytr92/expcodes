package exp.bilibli.plugin;

import exp.bilibli.plugin.core.front.AppUI;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> bilibli-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		BeautyEyeUtils.init();
		AppUI.createInstn(args);
	}
	
}
