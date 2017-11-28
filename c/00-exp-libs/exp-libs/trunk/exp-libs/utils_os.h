/*****************************************
  Description: 系统工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#pragma once;

#ifndef __UTILS_OS_H_
#define __UTILS_OS_H_

	#include "stdafx.h"

	namespace OS_UTILS {

		/*
		 * 判断当前系统是否为64位
		 * @return true:64位; false:32位
		 */
		DLL_API bool isX64();

		/*
		 * 复制文本数据到剪贴板
		 * @param pData 文本数据
		 * @return true:复制成功; false:复制失败
		 */
		DLL_API bool copyToClipboard(const char* pData);

		/*
		 * 复制文本数据到剪贴板
		 * @param pData 文本数据
		 * @param len 文本数据长度
		 * @return true:复制成功; false:复制失败
		 */
		DLL_API bool copyToClipboard(const char* pData, const int len);

		/*
		 * 从剪贴板获取文本数据
		 * @return 文本数据
		 */
		DLL_API const char* pasteFromClipboard();

	}

#endif
