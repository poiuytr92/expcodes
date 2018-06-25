package exp.rgx;

import exp.rgx.core.RegexWindow;
import exp.rgx.utils.BeautyEyeUtils;


/**
 * <PRE>
 * 正则测试工具程序入口
 * </PRE>
 * <B>PROJECT : </B> ui-regex-debug
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2015-06-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {

	public static void main(String[] args) {
		BeautyEyeUtils.init();// 美化外观用，可要可不�?
		
		new RegexWindow("正则测试工具");
	}
}
