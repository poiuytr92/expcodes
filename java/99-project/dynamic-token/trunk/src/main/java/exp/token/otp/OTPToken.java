package exp.token.otp;


/**
 * <PRE>
 * OTP令牌：
 * 	生成器/校验器
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class OTPToken {

	/**
	 * 私有化构造函数，避免误用.
	 */
	private OTPToken() {}
	
	/**
	 * 获取动态令牌(使用 默认私钥 和 默认时间偏移量).
	 * 	若初始化失败, 始终返回 "0000".
	 * 
	 * @return 动态令牌
	 */
	public static String getToken() {
		return getToken(_OTP_JAPI.getInstn().getDefaultPrivateKey());
	}
	
	/**
	 * 获取动态令牌(使用  默认时间偏移量).
	 * 	若初始化失败, 始终返回 "0000".
	 * 
	 * @param timeOffset 时间偏移量(ms) [取值范围: 1s-1h]
	 * @return 动态令牌
	 */
	public static String getToken(final String privateKey) {
		return getToken(privateKey, _OTP_JAPI.getInstn().getDefaultTimeOffset());
	}
	
	/**
	 * 获取动态令牌.
	 * 	若初始化失败, 始终返回 "0000".
	 * 
	 * @param privateKey 私钥
	 * @param timeOffset 时间偏移量(ms) [取值范围: 1s-1h]
	 * @return 动态令牌
	 */
	public static String getToken(final long timeOffset) {
		return _OTP_JAPI.getInstn().getOtpToken(
				_OTP_JAPI.getInstn().getDefaultPrivateKey(), timeOffset);
	}
	
	/**
	 * 获取动态令牌.
	 * 	若初始化失败, 始终返回 "0000".
	 * 
	 * @param privateKey 私钥
	 * @param timeOffset 时间偏移量(ms) [取值范围: 1s-1h]
	 * @return 动态令牌
	 */
	public static String getToken(final String privateKey, final long timeOffset) {
		return _OTP_JAPI.getInstn().getOtpToken(privateKey, timeOffset);
	}
	
	/**
	 * 校验动态令牌是否有效(使用  默认私钥).
	 * 	若初始化失败, 始终返回 "false".
	 * 
	 * @param otpToken 动态令牌
	 * @return true:有效; false:无效
	 */
	public static boolean isValid(final String otpToken) {
		return isValid(otpToken, _OTP_JAPI.getInstn().getDefaultPrivateKey());
	}
	
	/**
	 * 校验动态令牌是否有效.
	 * 	若初始化失败, 始终返回 "false".
	 * 
	 * @param otpToken 动态令牌
	 * @param privateKey 私钥
	 * @return true:有效; false:无效
	 */
	public static boolean isValid(final String otpToken, final String privateKey) {
		return _OTP_JAPI.getInstn().isValid(otpToken, privateKey);
	}
	
}
