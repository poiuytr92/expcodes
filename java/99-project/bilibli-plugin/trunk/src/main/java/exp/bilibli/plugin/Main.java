package exp.bilibli.plugin;

import exp.bilibli.plugin.core.front.AppUI;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> xxxxxx
 * <B>SUPPORT：</B> xxxxxx
 * @version   xxxxxx
 * @author    xxxxxx
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		BeautyEyeUtils.init();
		
//		String code = SwingUtils.input("请输入注册码");
//		if(code.matches("[a-zA-Z]\\d[a-zA-Z]\\d")) {
			AppUI.getInstn();
			
//		} else {
//			SwingUtils.warn("未授权用户");
//		}
	}
	
	
}
