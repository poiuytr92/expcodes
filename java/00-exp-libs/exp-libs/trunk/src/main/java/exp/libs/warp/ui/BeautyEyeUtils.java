package exp.libs.warp.ui;

import javax.swing.JButton;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.FrameBorderStyle;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

/**
 * <PRE>
 * swing美瞳组件工具.
 * 	设置swing组件风格样式
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class BeautyEyeUtils {

	/**
	 * 私有化构造函数
	 */
	private BeautyEyeUtils() {}
	
	/**
	 * 初始化 BeautyEye 
	 * 	(默认的苹果系统风格边框类型)
	 */
	public static void init() {
		// 选择苹果风格边框类型 （弱立体感半透明）
		init(FrameBorderStyle.translucencyAppleLike);
	}
	
	/**
	 * 初始化 BeautyEye 
	 * @param frameBorderStyle 组件风格样式
	 * 		translucencyAppleLike: 苹果系统风格边框类型 - 强立体感半透明（默认）
	 * 		translucencySmallShadow: 苹果系统风格边框类型 - 弱立体感半透明
	 * 		generalNoTranslucencyShadow: 苹果系统风格边框类型 - 普通不透明
	 * 		osLookAndFeelDecorated: 本地系统默认风格边框
	 */
	public static void init(FrameBorderStyle frameBorderStyle) {
		
		// 选择边框风格类型 
		BeautyEyeLNFHelper.frameBorderStyle = (frameBorderStyle == null ? 
				FrameBorderStyle.translucencyAppleLike : frameBorderStyle);
		
		// 隐藏右上角无效按钮【设置】
	    UIManager.put("RootPane.setupButtonVisible", false);
		
	    // 初始化 BeautyEye 外观组件
		try {	
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
			SwingUtils.warn("初始化 BeautyEye 外观组件失败.");
		}
	}
	
	/**
	 * 设置按钮风格样式
	 * @param button 按钮对象
	 * @param style 风格样式
	 * 		normal: 普通样式（默认）
	 * 		green: 绿色风格
	 * 		lightBlue: 浅蓝风格
	 * 		blue: 蓝色风格
	 * 		red: 红色风格
	 */
	public static void setButtonStyle(JButton button, NormalColor style) {
		if(button == null) {
			return;
		}
		
		style = (style == null ? NormalColor.normal : style);
		button.setUI(new BEButtonUI().setNormalColor(style));
	}
	
}
