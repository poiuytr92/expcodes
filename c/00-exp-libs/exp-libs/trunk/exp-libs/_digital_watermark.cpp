/*****************************************
  Description: （对32位MD5的）数字水印算法.
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#define DLL_IMPLEMENT

#include "stdafx.h"
#include "utils_str.h"
#include "_digital_watermark.h"

/* 10进制 - 16进制 转换编码表 */
const char DigitalWatermark::HEX_TABLE[16] = {
	'0', '1', '2', '3',
	'4', '5', '6', '7',
	'8', '9', 'A', 'B',
	'C', 'D', 'E', 'F'
};

/* 
 * 数字水印校验码表.
 * 此表的校验码为随机生成，只需保证生成水印和析取水印的校验码表相同即可。
 */
const char DigitalWatermark::CHECKCODE_TABLE[10][3] = {
	{'8', 'D', 'C'},	// 数字 0 校验码
	{'A', 'A', '9'},	// 数字 1 校验码
	{'6', '7', 'D'},	// 数字 2 校验码
	{'4', '3', '3'},	// 数字 3 校验码
	{'5', '4', 'B'},	// 数字 4 校验码
	{'7', '5', 'E'},	// 数字 5 校验码
	{'F', '2', '3'},	// 数字 6 校验码
	{'E', '3', '6'},	// 数字 7 校验码
	{'C', '0', '1'},	// 数字 8 校验码
	{'8', 'E', '2'},	// 数字 9 校验码
};

/* 构造函数 */
DigitalWatermark::DigitalWatermark() {
	// None
}

/* 析构函数 */
DigitalWatermark::~DigitalWatermark() {
	// None
}

/*
 * 向MD5（32位）嵌入数字水印
 * @param md5_32 MD5码（32位）
 * @param digital 数字水印（长度1~8）
 * @return 嵌有数字水印的MD5（64）位
 */
const char* DigitalWatermark::generate(const char* md5_32, const long long digital) {
	char* dwMD5 = new char[1];	// 含数字水印的MD5码
	*dwMD5 = '\0';

	if(STR_UTILS::sLen(md5_32) == MD5_LEN) {
		const char* sDigital = toDwStr(digital);	// 数字水印的字符串，长度必定为8
        STR_UTILS::sFree(dwMD5);
		dwMD5 = new char[DW_MD5_LEN + 1];
		int idx = 0;

		for(int i = 0; i < DW_LEN; i++) {
			const char* pMD5 = md5_32 + (i * 4);	// 每4位为一段
			const char n = *(sDigital + i);			// 取对应的水印位
			const char* cCode = *(CHECKCODE_TABLE + (n - '0'));	// 取水印位数字对应的校验码

			// 嵌入原码位
			*(dwMD5 + idx++) = *(pMD5 + 0);
			*(dwMD5 + idx++) = *(pMD5 + 1);
			*(dwMD5 + idx++) = *(pMD5 + 2);
			*(dwMD5 + idx++) = *(pMD5 + 3);

			// 嵌入水印位
			*(dwMD5 + idx++) = add(*(pMD5 + 0), n);

			// 嵌入校验位
			*(dwMD5 + idx++) = add(*(pMD5 + 1), *(cCode + 0));
			*(dwMD5 + idx++) = add(*(pMD5 + 2), *(cCode + 1));
			*(dwMD5 + idx++) = add(*(pMD5 + 3), *(cCode + 2));
		}

		*(dwMD5 + idx++) = '\0';
        STR_UTILS::sFree(sDigital);
	}
	return dwMD5;
}

/*
 * 从嵌有数字水印的MD5（64位）中提取MD5码（32位）
 * @param md5_64 嵌有数字水印的MD5（64位）
 * @return MD5码（32位）
 */
const char* DigitalWatermark::extractMD5(const char* md5_64) {
	char* md5 = new char[1];	// 真正的MD5码
	*md5 = '\0';

	if(STR_UTILS::sLen(md5_64) == DW_MD5_LEN) {
        STR_UTILS::sFree(md5);
		md5 = new char[MD5_LEN + 1];
		int idx = 0;
		for(int i = 0; i < DW_LEN; i++) {
			const char* pMD5 = md5_64 + (i * 8);	// 每8位为一段

			// 只取高4位
			*(md5 + idx++) = *(pMD5 + 0);
			*(md5 + idx++) = *(pMD5 + 1);
			*(md5 + idx++) = *(pMD5 + 2);
			*(md5 + idx++) = *(pMD5 + 3);
		}
		*(md5 + idx) = '\0';
	}
	return md5;
}

/*
 * 从嵌有数字水印的MD5（64位）中提取数字水印
 * @param md5_64 嵌有数字水印的MD5（64位）
 * @return 数字水印
 */
const long long DigitalWatermark::extractDigital(const char* md5_64) {
	long long digital = 0LL;	// 数字水印
	
	if(STR_UTILS::sLen(md5_64) == DW_MD5_LEN) {
		for(int i = 0; i < DW_LEN; i++) {
			const char* pMD5 = md5_64 + (i * 8);	// 每8位为一段
			
			// 计算水印位
			int n = hex2Int(minus(*(pMD5 + 4), *(pMD5 + 0)));

			// 校验水印位
			bool isOk = false;
			if(n >=0 && n <= 9) {
                
				const char* cCode = *(CHECKCODE_TABLE + n);	// 取水印位数字对应的校验码
                isOk = true;
                isOk &= (*(cCode + 0) == minus(*(pMD5 + 5), *(pMD5 + 1)));
                isOk &= (*(cCode + 1) == minus(*(pMD5 + 6), *(pMD5 + 2)));
                isOk &= (*(cCode + 2) == minus(*(pMD5 + 7), *(pMD5 + 3)));
			}

            // 还原数字水印
			if(isOk == true) {
				digital = digital * 10 + n;	

            // 还原数字水印出错
			} else {
				digital = 0LL;
				break;
			}
		}
	}
	return digital;
}

/*
 * 转换数字水印为 8位字符串（不足高位补0，越长高位截断）
 * @param digital 数字水印
 * @return 8位数字水印字符串
 */
const char* DigitalWatermark::toDwStr(const long long digital) {
	char* s = new char[DW_LEN + 1];
	int idx = 0;
	long long n = digital;

	do {
		int r = (n % 10);
		r = (r < 0 ? -r : r);
		*(s + idx++) = r + '0';
		n /= 10;
	} while(n != 0LL && idx < DW_LEN);	// 长度越限截断

	while(idx < DW_LEN) {
		*(s + idx++) = '0';	// 长度不足高位补0
	}
	*(s + idx) = '\0';

	const char* dwStr = STR_UTILS::reverse(s);
	STR_UTILS::sFree(s);
	return dwStr;
}

/*
 * 16进制字符相加
 * @param xA 被加数（0-F）
 * @param xB 加数（0-F）
 * @return 16进制字符（越界取模）
 */
const char DigitalWatermark::add(char xA, char xB) {
	int nA = hex2Int(xA);
	int nB = hex2Int(xB);
	return int2Hex(nA + nB);
}

/*
 * 16进制字符相减
 * @param xA 被减数（0-F）
 * @param xB 减数（0-F）
 * @return 16进制字符（越界取模）
 */
const char DigitalWatermark::minus(char xA, char xB) {
	int nA = hex2Int(xA);
	int nB = hex2Int(xB);
	return int2Hex(nA - nB);
}

/*
 * 16进制字符转换为10进制数字
 * @param x 16进制数（0-F）
 * @return 10进制数字
 */
int DigitalWatermark::hex2Int(char x) { 
	int n = 0;

	if(x >= '0' && x <= '9') {
		n = x - '0';

	} else if(x >= 'a' && x <= 'f') {
		n = x - 'a' + 10;

	} else if(x >= 'A' && x <= 'F') {
		n = x - 'A' + 10;
	}
	return n;
}

/*
 * 10进制数字转换为16进制字符
 * @param n 10进制数字
 * @return 16进制数（越界取模）
 */
char DigitalWatermark::int2Hex(int n) {
	int idx = n;

	if(n < 0) {
		idx = n % 16 + 16;

	} else if(n >= 16) {
		 idx = n % 16;
	}
	return HEX_TABLE[idx];
}