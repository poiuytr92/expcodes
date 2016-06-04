package exp.xp.utils;

import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

/**
 * <pre>
 * 美瞳工具类
 * </pre> 
 * @version 1.0 by 2015-06-01
 * @since   jdk版本：1.6
 * @author  Exp - liaoquanbin
 */
public final class BeautyEye {

	private BeautyEye() {}
	
	public static void init() {
		// 选择苹果风格边框类型
		BeautyEyeLNFHelper.frameBorderStyle = 
				BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
		
		// 隐藏右上角无效按钮【设置】
	    UIManager.put("RootPane.setupButtonVisible", false);
		
	    // 初始化 BeautyEye 外观组件
		try {	
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
			UIUtils.warn("Failed to initialize the appearance component by BeautyEye.");
		}
	}
	
}
