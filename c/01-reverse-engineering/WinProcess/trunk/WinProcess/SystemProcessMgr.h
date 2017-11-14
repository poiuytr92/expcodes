#pragma once

#include "stdafx.h"
#include "utils_os.h"

#include <Windows.h>
#include <string>
#include <map>
using namespace std;

// 无效的进程号(系统进程号为0, DWORD为无符号整型，只能取最大值)
const static DWORD INVAILD_PID = 0xFFFFFFFF;

/************************************************************************/
/* 单个进程的信息对象                                                   */
/************************************************************************/ 
class Process {
	public:
		DWORD pid;
		string pName;
		bool isX64;

		Process(): pid(INVAILD_PID), pName(""), isX64(false) {}

		bool operator == (const Process& other) {
			return (this == &other || this->pid == other.pid);
		}

		bool operator != (const Process& other) {
			return !(operator == (other));
		}
};

// 默认的空进程对象
static Process INVAILD_PROCESS;



class ProcessModule : public Process {
	public:
		DWORD mSize;		// modBaseSize 模块大小（字节）
		DWORD mID;			// th32ModuleID, 此成员已经不再被使用，通常被设置为1
		DWORD usage;		// GlblcntUsage 或 ProccntUsage 全局模块的使用计数，即模块的总载入次数。通常这一项是没有意义的
		BYTE* baseAddr;		// modBaseAddr 模块基址（在所属进程范围内）
		HMODULE hModule;	// hModule 模块句柄地址（在所属进程范围内）
		string mName;		// szModule[MAX_MODULE_NAME32 + 1];	 NULL结尾的字符串，其中包含模块名。
		string mPath;		// szExePath[MAX_PATH];	 NULL结尾的字符串，其中包含的位置，或模块的路径。

		ProcessModule() : Process() {

		}
};

/************************************************************************/
/* 
备注编辑
modBaseAddr和hModule的成员只有在指定的th32ProcessID进程中才有效。

---->  me32.dwSize==1080
---->  me32.GlblcntUsage==2
---->  me32.hModule==0xfd930000
---->  me32.modBaseAddr==0xfd930000
---->  me32.ProccntUsage==2
---->  me32.szExePath==C:\Windows\system32\CRYPTBASE.dll
---->  me32.szModule==CRYPTBASE.dll
---->  me32.th32ModuleID==1
---->  me32.th32ProcessID==664
---->  CNT==16

---->  me32.dwSize==1080
---->  me32.GlblcntUsage==2
---->  me32.hModule==0xff760000
---->  me32.modBaseAddr==0xff760000
---->  me32.ProccntUsage==2
---->  me32.szExePath==C:\Windows\system32\ADVAPI32.dll
---->  me32.szModule==ADVAPI32.dll
---->  me32.th32ModuleID==1
---->  me32.th32ProcessID==664
---->  CNT==17

---->  me32.dwSize==1080
---->  me32.GlblcntUsage==8
---->  me32.hModule==0xff240000
---->  me32.modBaseAddr==0xff240000
---->  me32.ProccntUsage==8
---->  me32.szExePath==C:\Windows\SYSTEM32\sechost.dll
---->  me32.szModule==sechost.dll
---->  me32.th32ModuleID==1
---->  me32.th32ProcessID==664
---->  CNT==18*/
/************************************************************************/


/************************************************************************/
/* 系统进程集管理对象                                                   */
/************************************************************************/ 
class SystemProcessMgr
{
	public:
		SystemProcessMgr() {
			IS_X64_OS = OS_UTILS::isX64();
			processMap = new map<DWORD, Process>();
			pids = new DWORD[1];
			processes = new Process*[1];
		}

		~SystemProcessMgr() {
			clearProcesses();
			delete processMap;
			delete pids;
			delete processes;
		}

		bool reflashProcessList();
		const Process& getProcess(DWORD pid);

		DWORD* getAllPIDs();
		Process** getAllProcesses();	// 获取所有进程对象的地址数组
		ProcessModule* getProcessModuleInfo(DWORD pid);	// 获取进程模块信息

	protected:
		bool traverseProcesses();
		void clearProcesses();

		const Process& addProcess(DWORD pid, string pName);
		bool isX64Process(DWORD pid);
		static bool compare(Process* aProc, Process* bProc);	// 类内的sort比较函数必须是静态

	private:
		bool IS_X64_OS;
		map<DWORD, Process>* processMap;	// 当前进程表
		DWORD* pids;						// 当前进程对象号数组
		Process** processes;				// 当前进程对象的指针数组
};


//在psaipi.dll中的函数EnumProcesses用来枚举进程 
typedef BOOL (_stdcall *ENUMPROCESSES)(  //注意这里要指明调用约定为-stdcall
	DWORD* pProcessIds,  //指向进程ID数组链  
	DWORD cb,    //ID数组的大小，用字节计数
	DWORD* pBytesReturned);   //返回的字节

//在psapi.dll中的函数EnumProcessModules用来枚举进程模块
typedef BOOL (_stdcall *ENUMPROCESSMODULES)(
	HANDLE hProcess,   //进程句柄
	HMODULE* lphModule, //指向模块句柄数组链
	DWORD cb,    //模块句柄数组大小，字节计数
	LPDWORD lpcbNeeded);   //存储所有模块句柄所需的字节数

//在psapi.dll中的函数GetModuleFileNameEx获得进程模块名
typedef DWORD (_stdcall *GETMODULEFILENAMEEX)(
	HANDLE hProcess,   //进程句柄
	HMODULE hModule,   //进程模块句柄
	LPTSTR lpFilename,   //存放模块全路径名
	DWORD nSize    //lpFilename缓冲区大小，字符计算
	);