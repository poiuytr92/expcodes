package exp.bilibili.plugin;

import exp.bilibili.plugin.ui.AppUI;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;

/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BPMain {
	
	public static void main(String[] args) {
		if(OSUtils.getStartlock(2333)) {
			LogUtils.loadLogBackConfig();
			Config.getInstn();
			BeautyEyeUtils.init();
			AppUI.createInstn(args);
			
		} else {
			System.err.println("禁止重复启动程序");
		}
	}
	
}
