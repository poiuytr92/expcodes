/*****************************************
  Description: 加密/解密工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#define DLL_IMPLEMENT

#include "stdafx.h"
#include "utils_str.h"
#include "_md5.h"
#include "_digital_watermark.h"
#include "utils_crypto.h"

namespace CRYPTO_UTILS {

	/*
	 * 生成MD5码（32位）
	 * @param text 明文
	 * @param key 密钥
	 * @return MD5码（32位）
	 */
	DLL_API const char* toMD5(const char* text, const char* key) {
		const char* eText = STR_UTILS::concat(text, key);
		const int eLen = STR_UTILS::sLen(eText);
		const char* md5 = MD5(eText, eLen).toCharArray();
		const char* md5Upper = STR_UTILS::toUpperCase(md5);

		STR_UTILS::sFree(eText);
		STR_UTILS::sFree(md5);
		return md5Upper;
	}

	/*
	 * 向MD5（32位）嵌入数字水印
	 * @param md5 MD5码（32位）
	 * @param digitalWatermark 数字水印（长度1~8）
	 * @return 嵌有数字水印的MD5（64）位
	 */
	DLL_API const char* addWatermark(const char* md5, const long long digitalWatermark) {
		const char* dwMD5 = DigitalWatermark().generate(md5, digitalWatermark);
		const char* dwMD5Upper = STR_UTILS::toUpperCase(dwMD5);

        STR_UTILS::sFree(dwMD5);
		return dwMD5Upper;
	}

	/*
	 * 从嵌有数字水印的MD5（64位）中提取MD5码（32位）
	 * @param dwMD5 嵌有数字水印的MD5（64位）
	 * @return MD5码（32位）
	 */
	DLL_API const char* getMD5(const char* dwMD5) {
		return DigitalWatermark().extractMD5(dwMD5);
	}

	/*
	 * 从嵌有数字水印的MD5（64位）中提取数字水印
	 * @param dwMD5 嵌有数字水印的MD5（64位）
	 * @return 数字水印
	 */
	DLL_API const long long getWatermark(const char* dwMD5) {
		return DigitalWatermark().extractDigital(dwMD5);
	}

}
