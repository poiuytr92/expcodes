/*****************************************
  Description: 字符串处理工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#include "stdafx.h"
#include "str_utils.h"

namespace STR_UTILS {

	/*
	 * 计算字符串长度
	 * @param str 字符串
	 * @return 字符串长度
	 */
	const int sLen(const char* str) {
		int len = 0;
		while(*(str + len) != '\0') {
			len++;
		}
		return len;
	}

	/*
	 * 反转字符串
	 * @param str 字符串
	 * @return 反转的字符串
	 */
	const char* reverse(const char* str) {
		int len = sLen(str);
		char* rStr = new char[len + 1];

		len--;
		for(int i = 0; i <= len; i++) {
			*(rStr + i) = *(str + len - i);
		}
		*(rStr + len + 1) = '\0';
		return rStr;
	}

	/*
	 * 获取子串
	 * @param str 字符串
	 * @param start 起始下标（包括），若小0自动修正为0
	 * @param end 终止下标（不包括），若大于串长自动修正为串长
	 * @return 子串
	 */
	const char* substr(const char* str, const int start, const int end) {
		int len = sLen(str);
		int sIdx = start < 0 ? 0 : start;
		int eIdx = end > len ? len : end;
		
		char* s;
		int i = 0;
		if(sIdx < eIdx) {
			s = new char[eIdx - sIdx + 1];
			for(int idx = sIdx; idx < eIdx; idx++) {
				*(s + i++) = *(str + idx);
			}

		} else if (sIdx > eIdx) {
			s = new char[sIdx - eIdx + 1];
			for(int idx = sIdx; idx > eIdx; idx--) {
				*(s + i++) = *(str + idx);
			}

		} else {
			s = new char[2];
			*(s + i++) = *(str + sIdx);
		}
		*(s + i) = '\0';
		return s;
	}

	/*
	 * 字符串拼接
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return str1 + str2
	 */
	const char* concat(const char* str1, const char* str2) {
		int len1 = sLen(str1);
		int len2 = sLen(str2);
		char* str = new char[len1 + len2 + 1];
		int idx = 0;

		for(int i = 0; i < len1; i++) {
			*(str + idx) = *(str1 + i);
			idx++;
		}

		for(int i = 0; i < len2; i++) {
			*(str + idx) = *(str2 + i);
			idx++;
		}

		*(str + idx) = '\0';
		return str;
	}
	
	/*
	 * 把字符串中所有英文字符转换为大写
	 * @param str 字符串
	 * @return 转换成大写的字符串
	 */
	const char* toUpperCase(const char* str) {
		int len = sLen(str);
		char* upper = new char[len + 1];
		for(int i = 0; i < len; i++) {
			char c = *(str + i);
			if(c >= 'a' && c <= 'z') {
				c -= 32;
			}
			*(upper + i) = c;
		}
		*(upper + len) = '\0';
		return upper;
	}

	/*
	 * 把字符串中所有英文字符转换为小写
	 * @param str 字符串
	 * @return 转换成小写的字符串
	 */
	const char* toLowCase(const char* str) {
		int len = sLen(str);
		char* low = new char[len + 1];
		for(int i = 0; i < len; i++) {
			char c = *(str + i);
			if(c >= 'A' && c <= 'Z') {
				c += 32;
			}
			*(low + i) = c;
		}
		*(low + len) = '\0';
		return low;
	}

	/*
	 * 比较字符串的值是否相同
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return true:相同; false:不同
	 */
	const bool isEqual(const char* str1, const char* str2) {
		bool isEqual = true;
		int len1 = sLen(str1);
		int len2 = sLen(str2);

		if(len1 != len2) {
			isEqual = false;

		} else {
			for(int i = 0; i < len1; i++) {
				if(*(str1 + i) != *(str2 + i) ) {
					isEqual = false;
					break;
				}
			}
		}
		return isEqual;
	}
		
	/*
	 * 释放字符串指针指向的内存
	 * @param str 字符串指针
	 */
	void sFree(const char* str) {
		delete[] str;
		str = NULL;
	}

}
