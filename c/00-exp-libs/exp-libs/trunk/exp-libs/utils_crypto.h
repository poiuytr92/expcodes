/*****************************************
  Description: 加密/解密工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#pragma once;

#ifndef __UTILS_CRYPTO_H_
#define __UTILS_CRYPTO_H_

	#include "stdafx.h"

	namespace CRYPTO_UTILS {

		/*
		 * 生成MD5码（32位）
		 * @param text 明文
		 * @param key 密钥
		 * @return MD5码（32位）
		 */
		DLL_API const char* toMD5(const char* text, const char* key);

		/*
		 * 向MD5（32位）嵌入数字水印
		 * @param md5 MD5码（32位）
		 * @param digitalWatermark 数字水印（长度1~8）
		 * @return 嵌有数字水印的MD5（64）位
		 */
		DLL_API const char* addWatermark(const char* md5, const long long digitalWatermark);

		/*
		 * 从嵌有数字水印的MD5（64位）中提取MD5码（32位）
		 * @param dwMD5 嵌有数字水印的MD5（64位）
		 * @return MD5码（32位）
		 */
		DLL_API const char* getMD5(const char* dwMD5);

		/*
		 * 从嵌有数字水印的MD5（64位）中提取数字水印
		 * @param dwMD5 嵌有数字水印的MD5（64位）
		 * @return 数字水印
		 */
		DLL_API const long long getWatermark(const char* dwMD5);

	}

#endif