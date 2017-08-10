package exp.sf.am;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.FrameBorderStyle;

import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.sf.am.win.AccountMgr;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>项    目：</B> xxxxxxx
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2017
 * @version   1.0 2017-07-12
 * @author    ??：??@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		BeautyEyeUtils.init(FrameBorderStyle.translucencySmallShadow);
		AccountMgr.createInstn();
	}
	
}
