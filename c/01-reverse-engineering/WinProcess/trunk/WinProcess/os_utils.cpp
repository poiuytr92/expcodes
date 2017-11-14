
#include "stdafx.h"
#include "os_utils.h"


/************************************************************************/
/* 判断系统是否为64位                                                   */
/************************************************************************/
bool isX64OS() {
	bool isX64 = false;
	typedef VOID (WINAPI *LPFN_GetNativeSystemInfo) (__out LPSYSTEM_INFO lpSystemInfo);
	LPFN_GetNativeSystemInfo fnGetNativeSystemInfo = 
		(LPFN_GetNativeSystemInfo) GetProcAddress(GetModuleHandleW(L"kernel32"), "GetNativeSystemInfo");
	if(fnGetNativeSystemInfo) {
		SYSTEM_INFO sysInfo= { 0 };
		fnGetNativeSystemInfo(&sysInfo);
		if(sysInfo.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_IA64 || 
			sysInfo.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_AMD64) {
				isX64 = true;
		}
	}
	return isX64;
}
