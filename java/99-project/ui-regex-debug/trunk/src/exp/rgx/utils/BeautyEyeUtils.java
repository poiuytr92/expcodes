package exp.rgx.utils;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

/**
 * <PRE>
 * swing美瞳组件工具
 * </PRE>
 * <B>PROJECT : </B> ui-regex-debug
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2015-06-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class BeautyEyeUtils {

	/**
	 * 私有化构造函�?
	 */
	private BeautyEyeUtils() {}
	
	/**
	 * 初始�? BeautyEye
	 */
	public static void init() {
		// 选择苹果风格边框类型
		BeautyEyeLNFHelper.frameBorderStyle = 
				BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
		
		// 隐藏右上角无效按钮【设置�?
	    UIManager.put("RootPane.setupButtonVisible", false);
		
	    // 初始�? BeautyEye 外观组件
		try {	
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
			warn("Failed to initialize the appearance component by BeautyEye.");
		}
	}
	
	/**
	 * 警告弹窗
	 * @param msg 警告消息
	 */
	public static void warn(String msg) {
		JOptionPane.showMessageDialog(
			    null, msg, "Warn", JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * 异常弹窗
	 * @param msg 异常消息
	 * @param e 异常
	 */
	public static void error(String msg, Throwable e) {
		JOptionPane.showMessageDialog(
			    null, msg, "Error", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
	
}
