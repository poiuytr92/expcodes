package exp.bilibili.plugin.utils;

import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 校验码识别工具
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class VercodeUtils {
	
	/** 私有化构造函�? */
	protected VercodeUtils() {}
	
	/**
	 * 计算小学数学验证码图片中的表达式
	 * @param imgPath 小学数学验证码图片路�?, 目前仅有 a+b �? a-b 两种形式的验证码
	 * @return 表达式计算结�?
	 */
	public static int calculateExpressionImage(String imgPath) {
		String expression = OCRUtils.imgToTxt(imgPath);	// 图像识别
		expression = revise(expression);	// 修正表达�?
		return calculate(expression);
	}
	
	/**
	 * 目前小学数学验证码图片的运算式只�? a+b �? a-b 两种形式, 由于字体问题，某些数字会被固定识别错�?, 
	 *  此方法用于修正常见的识别错误的数�?/符号, 提高识别�?
	 * @param txt
	 * @return
	 */
	private static String revise(String expression) {
		String revise = expression;
		
		revise = revise.replace("[1", "0");
		revise = revise.replace("[|", "0");
		
		revise = revise.replace("'I", "7");
		
		revise = revise.replace("l�?", "4");
		revise = revise.replace("l»", "4");
		revise = revise.replace("b", "4");
		revise = revise.replace("h", "4");
		
		revise = revise.replace("i", "1");
		revise = revise.replace("I", "1");
		revise = revise.replace("]", "1");
		revise = revise.replace("|", "1");
		
		revise = revise.replace("E", "6");
		
		revise = revise.replace("B", "8");
		
		revise = revise.replace("H", "9");
		revise = revise.replace("Q", "9");
		
		revise = revise.replace("·", "-");
		return revise;
	}
	
	/**
	 * 计算表达�?
	 * @param expression 表达�?, 目前仅有 a+b �? a-b 两种形式
	 * @return
	 */
	private static int calculate(String expression) {
		int rst = 0;
		if(StrUtils.isNotEmpty(expression)) {
			String[] nums = expression.split("\\+|\\-");
			if(nums.length == 2) {
				int a = NumUtils.toInt(nums[0], 0);
				int b = NumUtils.toInt(nums[1], 0);
				rst = (expression.contains("+") ? (a + b) : (a - b));
			}
		}
		return rst;
	}
	
	/**
	 * 节奏风暴校验码辨�?
	 * @param imgPath 节奏风暴校验码图片路�?, �?4~5个变形字�?
	 * @return 文字形式字符
	 */
	public static String recognizeStormImage(String imgPath) {
		return StrUtils.isTrimEmpty(imgPath) ? "" : 
			TensorFlowUtils.imgToTxt(imgPath);
	}
	
}
