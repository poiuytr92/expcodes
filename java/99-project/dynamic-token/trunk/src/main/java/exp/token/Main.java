package exp.token;

import exp.token.otp.OTPToken;

/**
 * <PRE>
 * OTP令牌（One-time Password）.
 * 	其作用类似于QQ、新浪的登陆安全盾: 
 *  在手机提供一串口令, 然后在一定时间范围内, 在电脑输入这串口令进行登陆验证.
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	/**
	 * 此方法仅用于测试, 其他项目使用时直接调用 {@link exp.token.otp.OTPToken} 接口即可
	 * @param args
	 */
	public static void main(String[] args) {
		final String PRIVATE_KEY = "http://exp-blog.com";	// 口令私钥
		final long TIME_OFFSET = 60000;	// 校验令牌时允许的时间偏差值（正负60s）
		
		// 获取令牌
		String otpToken = OTPToken.getToken(PRIVATE_KEY, TIME_OFFSET);
		System.out.println("Token: " + otpToken);
		
		/* 经过若干时间后....  */
		
		// 校验令牌
		boolean isValid = OTPToken.isValid(otpToken, PRIVATE_KEY);
		System.out.println("IsValid: " + isValid);
	}

}
