package Tool;

import java.text.DecimalFormat;

/*
 * 数字格式
 */

public class NumberFormat {

	public static String doubleFormat(double number) {
		try {
			return (new DecimalFormat("0.00").format(number));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String moneyFormat(double number) {
		try {
			return "￥" + doubleFormat(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}




/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */