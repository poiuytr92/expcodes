/*****************************************
  Description: OTP功能实现包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __OTP_IMPL_H_
#define __OTP_IMPL_H_

	namespace OTP_IMPL {

		/*
		 * 生成动态令牌
		 * @param privateKey 私钥（仅英文字符）
		 * @param timeOffset 时间偏移量（ms）
		 * @return 动态令牌
		 */
		const char* getOtpToket(const char* privateKey, const long long timeOffset);

		/*
		 * 校验动态令牌
		 * @param otpToken 动态令牌
		 * @param privateKey 私钥（仅英文字符）
		 * @return true:校验成功; false:校验失败
		 */
		const bool isValid(const char* otpToken, const char* privateKey);

	}

#endif