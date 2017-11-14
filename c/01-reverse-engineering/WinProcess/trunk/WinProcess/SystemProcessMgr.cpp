
#include "stdafx.h"
#include "os_utils.h"
#include "str_utils.h"
#include "SystemProcessMgr.h"

#include <algorithm>
#include <string>
#include <map>
#include <Windows.h>
#include <TlHelp32.h>
#include <iostream>
using namespace std;


/************************************************************************/
/* 刷新当前系统进程列表                                                 */
/* @return 是否刷新成功                                                 */
/************************************************************************/ 
bool SystemProcessMgr::reflashProcessList() {
	clearProcesses();
	bool isOk = traverseProcesses();

	if(isOk == true) {
		TRACE(_T("Reflash Processes Success\r\n"));
	} else {
		TRACE(_T("Reflash Processes Fail\r\n"));
	}
	return isOk;
}


/************************************************************************/
/* 清空当前系统进程列表快照                                             */
/************************************************************************/ 
void SystemProcessMgr::clearProcesses() {
	map<DWORD, Process>::iterator its = processMap->begin();
	while(its != processMap->end()) {
		map<DWORD, Process>::iterator obj = its;
		its++;
		processMap->erase(obj);
	}
	processMap->clear();
}


/************************************************************************/
/* 遍历当前系统所有进程                                                 */
/* @return 是否遍历成功                                                 */
/************************************************************************/ 
bool SystemProcessMgr::traverseProcesses() {
	bool isOk = true;

	// 获取当前系统进程列表快照
	HANDLE hProcessSNapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	if(hProcessSNapshot == INVALID_HANDLE_VALUE) {
		TRACE(_T("Create Process Snapshot Error\r\n"));
		isOk = false;

	} else {
		PROCESSENTRY32 pe32;	// 进程结构体
		pe32.dwSize = sizeof(PROCESSENTRY32);	// 必须：指定之后要提取的进程结构体大小
		const int LEN = sizeof(pe32.szExeFile) / sizeof(pe32.szExeFile[0]);	// 存储进程名称的数组长度

		BOOL hasNext = Process32First(hProcessSNapshot, &pe32);	// 取出快照列表中的第一个进程
		while (hasNext) {
			char* tmp = toChar(pe32.szExeFile);
			string pName = string(tmp);
			DWORD pid = pe32.th32ProcessID;
			delete tmp;
			
			addProcess(pid, pName);	// 把进程信息添加到缓存
			hasNext = Process32NextW(hProcessSNapshot, &pe32);	// 取出快照列表中的下一个进程
		}
	}
	CloseHandle(hProcessSNapshot);	// 释放快照列表资源
	return isOk;
}


/************************************************************************/
/* 添加一个进程                                                         */
/************************************************************************/
const Process& SystemProcessMgr::addProcess(DWORD pid, string pName) {
	Process process;
	process.pid = pid;
	process.pName = pName;
	process.isX64 = isX64Process(pid);

	processMap->insert(pair<DWORD, Process>(pid, process));
	return getProcess(pid);	// 注意局部变量process的生命周期已结束，不能返回之
}


/************************************************************************/
/* 获取一个进程                                                         */
/************************************************************************/
const Process& SystemProcessMgr::getProcess(DWORD pid) {
	map<DWORD, Process>::iterator its = processMap->find(pid);
	return ( its == processMap->end() ? INVAILD_PROCESS : its->second );
}


/************************************************************************/
/* 获取所有进程ID                                                       */
/* @return 指针数组ID（数组最后一个ID为-1）                             */
/************************************************************************/
DWORD* SystemProcessMgr::getAllPIDs() {
	delete pids;
	const int LEN = processMap->size();
	pids = new DWORD[LEN + 1];

	int idx = 0;
	map<DWORD, Process>::iterator its = processMap->begin();
	while(its != processMap->end()) {
		*(pids + idx++) = its->first;
		its++;
	}

	sort(pids, pids + LEN);			// 按进程号升序排序
	*(pids + idx) = INVAILD_PID;	// 添加末尾标识
	return pids;
}

/************************************************************************/
/* 获取所有进程对象引用                                                 */
/* @return 进程对象的指针数组ID（数组最后一个对象为INVAILD_PROCESS）    */
/************************************************************************/
Process** SystemProcessMgr::getAllProcesses() {
	delete processes;
	const int LEN = processMap->size();
	processes = new Process*[LEN + 1];

	int idx = 0;
	map<DWORD, Process>::iterator its = processMap->begin();
	while(its != processMap->end()) {
		*(processes + idx++) = &(its->second);
		its++;
	}
	
	sort(processes, processes + LEN, compare);	// 按进程名升序排序
	*(processes + idx) = &INVAILD_PROCESS;		// 添加末尾标识
	return processes;
}

bool SystemProcessMgr::compare(Process* aProc, Process* bProc) {
	int rst = stricmp((*aProc).pName.c_str(), (*bProc).pName.c_str());

	// 注：由于sort()版本BUG问题，当rst==0时必须返回false，否则会报错
	return (rst < 0 ? true : false);	// 升序
	//return (rst > 0 ? true : false);	// 倒序
}

/************************************************************************/
/* 判断进程是否为64位                                                   */
/*  IsWow64Process 实际上不能用于判断进程的位数                         */
/*   其实际含义是某个进程是不是在[运行在wow64虚拟环境]下                */
/*    各种可能的情况如下：                                              */
/*    64-bit process on 64-bit Windows : FALSE                          */
/*    32-bit process on 64-bit Windows : TRUE                           */
/*    32-bit process on 32-bit Windows : FALSE                          */
/*   可以发现只有当32位程序运行在64位平台时才会返回TRUE,                */
/*   这也是为什么判定结果与windows自带的任务管理器存在出入              */
/************************************************************************/
bool SystemProcessMgr::isX64Process(DWORD pid) {
	bool isX64Process = false;
	if(this->IS_X64_OS == true) {
		HANDLE hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, pid);
		if(hProcess) {
			typedef BOOL (WINAPI *LPFN_ISWOW64PROCESS) (HANDLE, PBOOL);
			LPFN_ISWOW64PROCESS fnIsWow64Process = 
				(LPFN_ISWOW64PROCESS) GetProcAddress(GetModuleHandleW(L"kernel32"), "IsWow64Process");
			if(fnIsWow64Process != NULL) {
				BOOL isX64 = FALSE;
				fnIsWow64Process(hProcess, &isX64);
				isX64Process = isX64 ? true : false;
			}
		}
		CloseHandle(hProcess);
	}
	return isX64Process;
}


ProcessModule* SystemProcessMgr::getProcessModuleInfo(DWORD pid) {
	HANDLE hProcess = CreateToolhelp32Snapshot(TH32CS_SNAPMODULE, pid);
	if(hProcess == INVALID_HANDLE_VALUE) {
		TRACE(_T("Create Process-Module Snapshot Error\r\n"));

	} else {
		MODULEENTRY32 me;
		me.dwSize = sizeof(MODULEENTRY32);

		int cnt = 0;
		BOOL hasNext = Module32First(hProcess, &me);
		while (hasNext) {
			cnt++;
			
			TRACE(_T("\n ---->  me32.dwSize==%d"), me.dwSize);
			TRACE(_T("\n ---->  me32.GlblcntUsage==%d"), me.GlblcntUsage);
			TRACE(_T("\n ---->  me32.hModule==0x%x"), me.hModule);
			TRACE(_T("\n ---->  me32.modBaseAddr==0x%x"), me.modBaseAddr);
			TRACE(_T("\n ---->  me32.ProccntUsage==%d"), me.ProccntUsage);
			TRACE(_T("\n ---->  me32.szExePath==%s"), me.szExePath);
			TRACE(_T("\n ---->  me32.szModule==%s"), me.szModule);
			TRACE(_T("\n ---->  me32.th32ModuleID==%d"), me.th32ModuleID);
			TRACE(_T("\n ---->  me32.th32ProcessID==%d"), me.th32ProcessID);
			TRACE(_T("\n ---->  CNT==%d\n"),cnt);
			hasNext = Module32Next(hProcess, &me);
		}
	}
	CloseHandle(hProcess);
	return NULL;	// FIXME
}

//void SystemProcessMgr::getProcessInfo2(DWORD pid) {
//	HANDLE hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, pid);
//
//	CloseHandle(hProcess);
//}
//
//void SystemProcessMgr::getProcessInfo3(DWORD pid) {
//	HMODULE hPsDll = LoadLibrary(_T("PSAPI.DLL"));	// FreeLibrary
//	ENUMPROCESSES pEnumProcesses = (ENUMPROCESSES) GetProcAddress(hPsDll, "EnumProcesses");
//	ENUMPROCESSMODULES pEnumProcessModules = (ENUMPROCESSMODULES) GetProcAddress(hPsDll, "EnumProcessModules");
//	GETMODULEFILENAMEEX pGetModuleFileNameEx = (GETMODULEFILENAMEEX) GetProcAddress(hPsDll, "GetModuleFileNameExA");
//
//	DWORD pids[1] = { pid };
//	DWORD rst[1];
//	pEnumProcesses(pids, sizeof(pid), rst);
//	DWORD processcount = rst[0] / sizeof(DWORD);
//
//	for (int i = 0; i < processcount; i++) {
//		//打开进程
//		HANDLE hProcess = OpenProcess(PROCESS_QUERY_INFORMATION|PROCESS_VM_READ, false, pids[i]);
//		if (hProcess) {
//			pEnumProcessModules(hProcess, &hModule, sizeof(hModule), &needed);
//			pGetModuleFileNameEx(hProcess, hModule, path, sizeof(path));
//			GetShortPathName(path,path,256);
//			itoa(pids[i], temp, 10);
//			printf("%s --- %s\n",path,temp);
//
//		} else {
//			printf("Failed!!!\n");
//		}
//	}
//}
