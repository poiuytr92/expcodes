package exp.crawler.qq;

import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.crawler.qq.ui.AppUI;

/**
 * <PRE>
 * QQ空间爬虫:
 * 	可爬取相册和说说图文信息
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-22
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
