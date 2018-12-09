/*****************************************
  Description: 字符串处理工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#pragma once;

#ifndef __UTILS_STR_H_
#define __UTILS_STR_H_

	#include "stdafx.h"

	namespace STR_UTILS {

		/*
		 * 计算字符串长度
		 * @param str 字符串
		 * @return 字符串长度
		 */
		DLL_API const int sLen(const char* str);

		/*
		 * 反转字符串
		 * @param str 字符串
		 * @return 反转的字符串
		 */
		DLL_API const char* reverse(const char* str);

		/*
		 * 获取子串
		 * @param str 字符串
		 * @param start 起始下标（包括），若小0自动修正为0
		 * @param end 终止下标（不包括），若大于串长自动修正为串长
		 * @return 子串
		 */
		DLL_API const char* substr(const char* str, const int start, const int end);

		/*
		 * 字符串拼接
		 * @param str1 字符串1
		 * @param str2 字符串2
		 * @return str1 + str2
		 */
		DLL_API const char* concat(const char* str1, const char* str2);

		/*
		 * 把字符串中所有英文字符转换为大写
		 * @param str 字符串
		 * @return 转换成大写的字符串
		 */
		DLL_API const char* toUpperCase(const char* str);

		/*
		 * 把字符串中所有英文字符转换为小写
		 * @param str 字符串
		 * @return 转换成小写的字符串
		 */
		DLL_API const char* toLowCase(const char* str);

		/*
		 * 比较字符串的值是否相同
		 * @param str1 字符串1
		 * @param str2 字符串2
		 * @return true:相同; false:不同
		 */
		DLL_API const bool isEqual(const char* str1, const char* str2);

		/*
		 * 释放字符串指针指向的内存
		 * @param str 字符串指针
		 */
		DLL_API void sFree(const char* str);

		/*
		 * 释放字符串指针指向的内存
		 * @param str 等宽字符串指针
		 */
		DLL_API void sFree(const wchar_t* str);

		/*
		 * 把[整数]转换成[等宽字符数组(unicode)]
		 * @param num 整数
		 * @return 等宽字符数组(unicode)
		 */
		DLL_API wchar_t* toWChar(const int num);

		/*
		 * 把[不等宽字符数组]转换成[等宽字符数组(unicode)]
		 * @param _char 不等宽字符数组
		 * @return 等宽字符数组(unicode)
		 */
		DLL_API wchar_t* toWChar(const char* _char);

		/*
		 * 把[等宽字符数组(unicode)]转换成[不等宽字符数组]
		 * @param _wchar 等宽字符数组(unicode)
		 * @return 不等宽字符数组
		 */
		DLL_API char* toChar(const wchar_t* _wchar);

	}

#endif