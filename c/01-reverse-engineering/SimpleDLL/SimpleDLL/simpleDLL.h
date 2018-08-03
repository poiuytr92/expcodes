//------------------ SimpleDLL.h ----------------

#pragma once;

//该宏完成在dll项目内部使用__declspec(dllexport)导出
//在dll项目外部使用时，用__declspec(dllimport)导入


#ifndef __SIMPLE_H_
#define __SIMPLE_H_

	#include "stdafx.h"

	namespace SIMPLE_UTILS {
		DLL_API int add(int x, int y); //简单方法
	}
	

#endif