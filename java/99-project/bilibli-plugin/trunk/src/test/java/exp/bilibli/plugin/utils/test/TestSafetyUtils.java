package exp.bilibli.plugin.utils.test;

import exp.bilibli.plugin.utils.SafetyUtils;

/**
 * <PRE>
 * 生成授权码文件
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestSafetyUtils {

	public static void main(String[] args) {
		
		// 生成授权码文件
		int day = 60;
		System.out.println(SafetyUtils.certificateToFile(day));
	}
	
}
