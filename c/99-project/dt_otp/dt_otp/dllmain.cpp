/*****************************************
  Description: 定义 DLL 应用程序的入口点
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

// _WIN32/WIN32 可以用来判断是否 Windows 系统（对于跨平台程序）
// _WIN64 用来判断编译环境是 x86 还是 x64
#ifdef _WIN32

	#include "stdafx.h"
	#include <windows.h>

	BOOL APIENTRY DllMain(HMODULE hModule, DWORD  ul_reason_for_call,  LPVOID lpReserved)
	{
		switch (ul_reason_for_call)
		{
			case DLL_PROCESS_ATTACH:
			case DLL_THREAD_ATTACH:
			case DLL_THREAD_DETACH:
			case DLL_PROCESS_DETACH:
				break;
		}
		return TRUE;
	}

#endif

