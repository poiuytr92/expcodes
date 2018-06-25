package exp.sf.am;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.FrameBorderStyle;

import exp.libs.warp.ui.BeautyEyeUtils;
import exp.sf.am.core.AppMgr;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>项    目：</B> xxxxxxx
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2017
 * @version   1.0 2017-07-12
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	public static void main(String[] args) {
		BeautyEyeUtils.init(FrameBorderStyle.translucencySmallShadow);
		AppMgr.createInstn();
	}
	
}
