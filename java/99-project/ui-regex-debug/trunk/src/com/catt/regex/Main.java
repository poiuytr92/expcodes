package com.catt.regex;

import com.catt.regex.core.RegexWindow;
import com.catt.regex.utils.BeautyEyeUtils;



/**
 * <pre>
 * 正则测试工具主函数
 * </pre>	
 * @version   1.0 by 2013-12-26
 * @since     jdk版本：1.6
 * @author 廖权斌 ：liaoquanbin@gdcattsoft.com	<PRE>
 * 	<B>任务编号</B>： 
 *	<B>项目</B>：研发-集约化产品开发平台	 
 *	<B>公司</B>：广东凯通软件开发技术有限公司 综合网管接口组 (c) 2013 </PRE>
 */
public class Main {

	public static void main(String[] args) {
		BeautyEyeUtils.init();// 美化外观用，可要可不要
		
		new RegexWindow("正则测试工具");
	}
}
