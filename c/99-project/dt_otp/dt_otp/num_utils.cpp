/*****************************************
  Description: 数字处理工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#include "stdafx.h"
#include "str_utils.h"
#include "num_utils.h"

/*
 * 把定长数字字符串转换成long long数值
 * （若字符串包含非数字字符则跳过该字符）
 * @param str 数字字符串
 * @param len 字符串长度
 * @return long long 数值
 */
inline long long toLL(const char* str, const int len) {
	long long n = 0LL;
	for(int i = 0; i < len; i++) {
		char c = *(str + i);
		if(c < '0' || c > '9') {
			continue;
		}
		n *= 10;
		n += (c - '0');
	}
	return n;
}

namespace NUM_UTILS {

	/*
	 * 把数字字符串转换成long long数值
	 * @param str 数字字符串
	 * @return long long 数值
	 */
	const long long toLongLong(const char* str) {
		long long n = 0LL;
		int len = STR_UTILS::sLen(str);

		// 位数溢出(long long 型的最大值9223372036854775807， 算上符号位最多20位)
		if(len > 20) {
			// Undo

		// 若第1位不是符号位则位数溢出
		} else if (len > 19) {
			if(*(str) == '-' || *(str) == '+') {
				n = toLL(str + 1, 19);
			}

		// 位数不溢出（但数值可能溢出， 此处不做数值溢出处理)
		} else {	
			if(*(str) == '-' || *(str) == '+') {
				n = toLL(str + 1, len - 1);
			} else {
				n = toLL(str, len);
			}
		}

		n = (*(str) == '-' ? -n : n);
		return n;
	}

	/*
	 * 把long long数值转换成数字字符串
	 * @param _long long long 数值
	 * @return 数字字符串
	 */
	const char* toStr(const long long _long) {
		char* s = new char[21];	//long long 的最大值 9223372036854775807 共19位，算上符号位和结尾符，共需19+2位空间
		int idx = 0;
		long long n = _long;

		do {
			int r = (n % 10);
			r = (r < 0 ? -r : r);
			*(s + idx++) = r + '0';
			n /= 10;
		} while(n != 0LL);

		if(_long < 0) {
			*(s + idx++) = '-';
		}
		*(s + idx) = '\0';

		const char* sLong = STR_UTILS::reverse(s);
		STR_UTILS::sFree(s);
		return sLong;
	}

}

