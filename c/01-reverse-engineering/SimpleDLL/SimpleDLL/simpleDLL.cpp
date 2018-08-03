//------------------ SimpleDLL.cpp ----------------

//注意此处的宏定义需要写在#include "SimpleDLL.h"之前
//以完成在dll项目内部使用__declspec(dllexport)导出
//在dll项目外部使用时，用__declspec(dllimport)导入
#define DLL_IMPLEMENT

#include "SimpleDLL.h"
#include<Windows.h>
#include <intrin.h>
#include <stdlib.h>
#include <string.h>


namespace SIMPLE_UTILS {
	int DLL_API add(int x, int y)
	{
		return x+y;
	}
}

