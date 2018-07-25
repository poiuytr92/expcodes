/*****************************************
  Description: 字符串处理工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __STR_UTILS_H_
#define __STR_UTILS_H_

	namespace STR_UTILS {

		/*
		 * 计算字符串长度
		 * @param str 字符串
		 * @return 字符串长度
		 */
		const int sLen(const char* str);

		/*
		 * 反转字符串
		 * @param str 字符串
		 * @return 反转的字符串
		 */
		const char* reverse(const char* str);

		/*
		 * 获取子串
		 * @param str 字符串
		 * @param start 起始下标（包括），若小0自动修正为0
		 * @param end 终止下标（不包括），若大于串长自动修正为串长
		 * @return 子串
		 */
		const char* substr(const char* str, const int start, const int end);

		/*
		 * 字符串拼接
		 * @param str1 字符串1
		 * @param str2 字符串2
		 * @return str1 + str2
		 */
		const char* concat(const char* str1, const char* str2);

		/*
		 * 把字符串中所有英文字符转换为大写
		 * @param str 字符串
		 * @return 转换成大写的字符串
		 */
		const char* toUpperCase(const char* str);

		/*
		 * 把字符串中所有英文字符转换为小写
		 * @param str 字符串
		 * @return 转换成小写的字符串
		 */
		const char* toLowCase(const char* str);

		/*
		 * 比较字符串的值是否相同
		 * @param str1 字符串1
		 * @param str2 字符串2
		 * @return true:相同; false:不同
		 */
		const bool isEqual(const char* str1, const char* str2);

		/*
		 * 释放字符串指针指向的内存
		 * @param str 字符串指针
		 */
		void sFree(const char* str);

	}

#endif