package org.util;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

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
	
	/*
	  * 判断字符串是否为整数，速度最快
	  * @param str 传入的字符串 
	  * @return 是整数返回true,否则返回false 
	*/
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	  
	/* 
	  * 判断字符串是否为浮点数，包括double和float 
	  * @param str 传入的字符串 
	  * @return 是浮点数返回true,否则返回false 
	*/
	public static boolean isDouble(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}
	
//	//判断字符串是否为数字
//	//方法一：用java自带的函数
//	public static boolean isNumeric(String str){
//		for (int i = str.length();--i>=0;){
//			if (!Character.isDigit(str.charAt(i))){
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	//判断字符串是否为数字
//	//方法二
//	public static boolean isNumeric(String str){
//		Pattern pattern = Pattern.compile("[0-9]*");
//		return pattern.matcher(str).matches();
//	}
//	
//	//判断字符串是否为数字
//	//方法三
//	public final static boolean isNumeric(String str) {
//		if (str != null && !"".equals(str.trim())) {
//			return str.matches("^[0-9]*$");
//		}
//		return false;
//	}
//	
//	//判断字符串是否为数字
//	//方法四
//	public static boolean isNumeric(String str) {
//		for(int i=str.length();--i>=0;) {
//			int chr=str.charAt(i);
//			if(chr<48 || chr>57) {
//				 return false;
//			}
//		}
//		return true;
//	} 

}
