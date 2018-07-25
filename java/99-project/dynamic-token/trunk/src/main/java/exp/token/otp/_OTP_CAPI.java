package exp.token.otp;

/**
 * <PRE>
 * OTP API(c++)
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _OTP_CAPI {

	/**
	 * 获取默认私钥.
	 * @return 默认私钥(保密)
	 */
	protected static native String getDefaultPrivateKey();
	
	/**
	 * 获取默认时间偏移量.
	 * @return 默认时间偏移量(60000ms)
	 */
	protected static native long getDefaultTimeOffset();
	
	/**
	 * 获取动态令牌.
	 * @param privateKey 私钥
	 * @param timeOffset 时间偏移量(ms)
	 * @return 动态令牌
	 */
	protected static native String getOtpToken(
			final String privateKey, final long timeOffset);
	
	/**
	 * 校验动态令牌是否有效.
	 * @param otpToken 动态令牌
	 * @param privateKey 私钥
	 * @return true:有效; false:无效
	 */
	protected static native boolean isValid(
			final String otpToken, final String privateKey);
	
}
