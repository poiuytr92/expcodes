package exp.bilibili.plugin;

import exp.bilibili.plugin.utils.SafetyUtils;

/**
 * <PRE>
 * 生成授权信息
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class GenerateSafetyInfo {

	public static void main(String[] args) {
		updatePrivateTime();
	}
	
	/**
	 * 更新对私授权时间
	 */
	public static void updatePrivateTime() {
		int day = 35;	// 授权时间(从当前开始往后推N天)
		String code = SafetyUtils.certificateToFile(day);	// 授权码
		System.out.println(code);
	}
	
}
