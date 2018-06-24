package exp.crawler.qq;

import exp.au.api.AppVerInfo;
import exp.crawler.qq.ui.AppUI;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;

/**
 * <PRE>
 * QQ空间爬虫:
 * 	可爬取相册和说说图文信息
 * </PRE>
 * <B>PROJECT：</B> qzone-crawler
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-22
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class Main {

	/**
	 * 程序入口
	 * @param args
	 */
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		BeautyEyeUtils.init();
		Config.getInstn();
		AppVerInfo.export(Config.APP_NAME);
		
		AppUI.createInstn(args);
	}
	
}
