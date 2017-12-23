package exp.bilibli.plugin.utils;

import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;

public class VerCodeUtils {

	protected VerCodeUtils() {}
	
	public static int calculateImage(String verCodeImgPath) {
		return calculate(OCRUtils.jpgToTxt(verCodeImgPath));
	}
	
	public static int calculate(String expression) {
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
	
}
