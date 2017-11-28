/*****************************************
  Description: 系统工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#define DLL_IMPLEMENT

#include "stdafx.h"
#include "utils_os.h"
#include <Windows.h>

namespace OS_UTILS {

	DLL_API bool isX64() {
		bool x64 = false;
		typedef VOID (WINAPI *LPFN_GetNativeSystemInfo) (__out LPSYSTEM_INFO lpSystemInfo);
		LPFN_GetNativeSystemInfo fnGetNativeSystemInfo = 
			(LPFN_GetNativeSystemInfo) GetProcAddress(GetModuleHandleW(L"kernel32"), "GetNativeSystemInfo");
		if(fnGetNativeSystemInfo) {
			SYSTEM_INFO sysInfo= { 0 };
			fnGetNativeSystemInfo(&sysInfo);
			if(sysInfo.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_IA64 || 
				sysInfo.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_AMD64) {
					x64 = true;
			}
		}
		return x64;
	}

	DLL_API bool copyToClipboard(const char* pData) {
		const int len = strlen(pData);
		return copyToClipboard(pData, len);
	}

	DLL_API bool copyToClipboard(const char* pData, const int len) {
		BOOL isOk = FALSE;
		if(::OpenClipboard(NULL)) {
			::EmptyClipboard();

			HGLOBAL clipbuffer = ::GlobalAlloc(GMEM_DDESHARE, len + 1); {
				char* buffer = (char*)::GlobalLock(clipbuffer);
				strcpy(buffer, pData);
				::GlobalUnlock(clipbuffer);
			}
			::SetClipboardData(CF_TEXT, clipbuffer);
			
			isOk = TRUE;
		}
		::CloseClipboard();
		return isOk;
	}

	DLL_API const char* pasteFromClipboard() {
		char* pData = new char[1];
		pData[0] = '\0';

		if(::OpenClipboard(NULL)) {
			HGLOBAL hMemory = GetClipboardData(CF_TEXT);
			if(hMemory != NULL) {
				delete[] pData;
				pData = (char*)::GlobalLock(hMemory); 
				if(pData == NULL) {
					pData = new char[1];
					pData[0] = '\0';
				}
				::GlobalUnlock(hMemory);
			}
		}
		::CloseClipboard();
		return pData;
	}

}