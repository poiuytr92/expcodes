package exp.bilibili.plugin;

import exp.bilibili.plugin.core.front.AppUI;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		Config.getInstn();
		
		BeautyEyeUtils.init();
		AppUI.createInstn(args);
	}
	
}
