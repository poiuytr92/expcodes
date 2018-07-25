/*****************************************
  Description: 加密/解密工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __CRYPTO_UTILS_H_
#define __CRYPTO_UTILS_H_

	namespace CRYPTO_UTILS {

		/*
		 * 生成MD5码（32位）
		 * @param text 明文
		 * @param key 密钥
		 * @return MD5码（32位）
		 */
		const char* toMD5(const char* text, const char* key);

		/*
		 * 向MD5（32位）嵌入数字水印
		 * @param md5 MD5码（32位）
		 * @param digitalWatermark 数字水印（长度1~8）
		 * @return 嵌有数字水印的MD5（64）位
		 */
		const char* addWatermark(const char* md5, const long long digitalWatermark);

		/*
		 * 从嵌有数字水印的MD5（64位）中提取MD5码（32位）
		 * @param dwMD5 嵌有数字水印的MD5（64位）
		 * @return MD5码（32位）
		 */
		const char* getMD5(const char* dwMD5);

		/*
		 * 从嵌有数字水印的MD5（64位）中提取数字水印
		 * @param dwMD5 嵌有数字水印的MD5（64位）
		 * @return 数字水印
		 */
		const long long getWatermark(const char* dwMD5);

	}

#endif