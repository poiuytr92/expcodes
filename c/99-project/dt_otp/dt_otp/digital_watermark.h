/*****************************************
  Description: （对32位MD5的）数字水印算法.
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __DIGITAL_WATERMARK_H_
#define __DIGITAL_WATERMARK_H_

	class DigitalWatermark {
		public:
			DigitalWatermark();
			~DigitalWatermark();

			const char* generate(const char* md5_32, const long long digital);	// 在32位MD5码中嵌入数字水印
			const char* extractMD5(const char* md5_64);							// 从已嵌入数字水印的MD5码中提取MD5码
			const long long extractDigital(const char* md5_64);					// 从已嵌入数字水印的MD5码中提取数字水印

		private:
			const char* toDwStr(const long long digital);	// 转换数字水印为字符串（返回长度必定为8，不足高位补0，越长高位截断）
			const char add(char xA, char xB);				// 16进制字符相加 xA + xB
			const char minus(char xA, char xB);				// 16进制字符相减 xA - xB
			int hex2Int(char x);							// 16进制字符转换为10进制数字
			char int2Hex(int n);							// 10进制数字转换为16进制字符

		private:
			const static char HEX_TABLE[16];			// 16进制编码表
			const static char CHECKCODE_TABLE[10][3];	// 数字0-9的校验码（随机校验码）

            const static int MD5_LEN = 32;      // MD5长度
            const static int DW_MD5_LEN = 64;   // 嵌有数字水印的MD5长度
            const static int DW_LEN = 8;        // 数字水印长度
	};


#endif