/*****************************************
  Description: OTP功能实现包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#include "stdafx.h"
#include "num_utils.h"
#include "str_utils.h"
#include "time_utils.h"
#include "crypto_utils.h"
#include "otp_impl.h"

#define ERROR_TOKEN "INNER_ERROR\0"

/*
 * 获取指定时间的本位偏移点
 * @param timeMillis 指定时间(ms)
 * @param timeOffset 时间偏移量(ms)
 * @return 指定时间的本位偏移点(ms)
 */
inline const long long getOffsetTime(const long long timeMillis, const long long timeOffset) {
	long long offset = (timeOffset <= 0LL ? 1LL : timeOffset);	//避免除0
	long long remainder = timeMillis % timeOffset;
	return timeMillis - remainder;
}


namespace OTP_IMPL {

	/*
	 * 生成动态令牌
	 * @param privateKey 私钥（仅英文字符）
	 * @param timeOffset 时间偏移量（ms）
	 * @return 动态令牌
	 */
	const char* getOtpToket(const char* privateKey, const long long timeOffset) {

		// 获取当前系统时间的[本位偏移点]
		const long long curSysTime = TIME_UTILS::getCurrentTimeMillis();
		const long long curSysTimeOffset = getOffsetTime(curSysTime, timeOffset);

		// 生成 本位偏移点 的MD5码
		const char* sCurSysTimeOffset = NUM_UTILS::toStr(curSysTimeOffset);
		const char* md5 = CRYPTO_UTILS::toMD5(sCurSysTimeOffset, privateKey);

		// 把时间偏移量作为数字水印嵌入MD5
		const char* dwMD5 = CRYPTO_UTILS::addWatermark(md5, timeOffset);

		// 生成令牌
		const char* otpToken = STR_UTILS::reverse(dwMD5);

		// 若令牌长度不为64，说明发生内部异常
		if(STR_UTILS::sLen(otpToken) != 64) {
			STR_UTILS::sFree(otpToken);
			otpToken = ERROR_TOKEN;
		}

		// 释放临时资源
		STR_UTILS::sFree(sCurSysTimeOffset);
		STR_UTILS::sFree(md5);
		STR_UTILS::sFree(dwMD5);
		return otpToken;
	}

	/*
	 * 校验动态令牌
	 * @param otpToken 动态令牌
	 * @param privateKey 私钥（仅英文字符）
	 * @return true:校验成功; false:校验失败
	 */
	const bool isValid(const char* otpToken, const char* privateKey) {
		bool isValid = false;

		// MD5（定长64）
		if(STR_UTILS::sLen(otpToken) == 64) {
			const char* otpTokenUpper = STR_UTILS::toUpperCase(otpToken);
			const char* dwMD5 = STR_UTILS::reverse(otpTokenUpper);		// 含数字水印的MD5
			long long digitalWater = CRYPTO_UTILS::getWatermark(dwMD5);	// 提取数字水印

			// 当水印通过自校验时, 说明水印未被修改
			if(digitalWater > 0) {
				const long long timeOffset = digitalWater;				// 数字水印就是生成令牌所用的 时间偏移量
				const char* otpTokenMd5 = CRYPTO_UTILS::getMD5(dwMD5);	// 提取真正的令牌MD5码

				// 若令牌长度不为32，说明发生内部异常
				if(STR_UTILS::sLen(otpTokenMd5) != 32) {
					// TODO: Print Error

				} else {

					// 计算本地时间偏移点
					const long long curSysTime = TIME_UTILS::getCurrentTimeMillis();
					const long long ctOffset = getOffsetTime(curSysTime, timeOffset);	// 本位偏移点
					const long long ptOffset = ctOffset + timeOffset;	// 正偏移点
					const long long ntOffset = ctOffset - timeOffset;	// 负偏移点
					const char* sCtOffset = NUM_UTILS::toStr(ctOffset);
					const char* sPtOffset = NUM_UTILS::toStr(ptOffset);
					const char* sNtOffset = NUM_UTILS::toStr(ntOffset);

					// 计算本地各个时间偏移点的 MD5
					const char* ctMD5 = CRYPTO_UTILS::toMD5(sCtOffset, privateKey);	//本位偏移点的MD5
					const char* ptMD5 = CRYPTO_UTILS::toMD5(sPtOffset, privateKey);	//正偏移点的MD5
					const char* ntMD5 = CRYPTO_UTILS::toMD5(sNtOffset, privateKey);	//负偏移点的MD5
			
					// 只要本地的某个时间偏移点与令牌MD5匹配， 则OTP口令有效
					if (STR_UTILS::isEqual(otpTokenMd5, ctMD5) || 
							STR_UTILS::isEqual(otpTokenMd5, ptMD5) || 
							STR_UTILS::isEqual(otpTokenMd5, ntMD5)) {
						isValid = true;
					}

					// 释放临时资源
					STR_UTILS::sFree(sCtOffset);
					STR_UTILS::sFree(sPtOffset);
					STR_UTILS::sFree(sNtOffset);
					STR_UTILS::sFree(ctMD5);
					STR_UTILS::sFree(ptMD5);
					STR_UTILS::sFree(ntMD5);
				}

				STR_UTILS::sFree(otpTokenMd5);
			}
			
			STR_UTILS::sFree(otpTokenUpper);
			STR_UTILS::sFree(dwMD5);
		}
		return isValid;
	}

}
