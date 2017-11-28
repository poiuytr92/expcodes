#pragma once

#include "stdafx.h"
#include "utils_os.h"

#include <Windows.h>
#include <string>
#include <map>
#include <list>
using namespace std;


/************************************************************************/
/* 单个模块对象（从 MODULEENTRY32 映射字段）                            */
/************************************************************************/ 
class Module {
public:
	DWORD mid;			// th32ModuleID, 此成员已经不再被使用，通常被设置为1
	DWORD mSize;		// modBaseSize 单个模块大小（字节）
	DWORD usage;		// GlblcntUsage 或 ProccntUsage 全局模块的使用计数，即模块的总载入次数。通常这一项是没有意义的
	BYTE* pBaseAddr;	// modBaseAddr 模块基址的指针（在所属进程范围内），真实类型是unsigned char*
	HMODULE hModule;	// hModule 模块句柄地址的指针（在所属进程范围内），真实类型是int， 16进制地址值等于pBaseAddr
	string name;		// szModule[MAX_MODULE_NAME32 + 1];	 NULL结尾的字符串，其中包含模块名。
	string path;		// szExePath[MAX_PATH];	 NULL结尾的字符串，其中包含的位置，或模块的路径。

	Module() : mSize(0), mid(0), usage(0), pBaseAddr(0), hModule(0) {
		// Undo
	}

	~Module() {
		// Undo
	}
};


// 无效的进程号(系统进程号为0, DWORD为无符号整型，只能取最大值)
const static DWORD INVAILD_PID = 0xFFFFFFFF;


/************************************************************************/
/* 单个进程的信息对象                                                   */
/************************************************************************/ 
class BaseProcess {
	public:
		DWORD pid;
		string pName;
		bool isX64;

		BaseProcess(): pid(INVAILD_PID), pName(""), isX64(false) {}

		bool operator == (const BaseProcess& other) {
			return (this == &other || this->pid == other.pid);
		}

		bool operator != (const BaseProcess& other) {
			return !(operator == (other));
		}
};

// 默认的空进程对象
static BaseProcess INVAILD_PROCESS;


/************************************************************************/
/* 单个进程内的所有模块对象                                             */
/************************************************************************/ 
class Process : public BaseProcess {	// 从 MODULEENTRY32 映射字段
	public:
		Process() : BaseProcess() {
			this->mCnt = 0;
			this->modules = new list<Module*>();
		}

		~Process() {
			clear();
			delete modules; modules = NULL;
		}

		int mCnt;					// 模块个数
		list<Module*>* modules;		// 模块列表
		void add(Module* modules);

	private:
		void clear();
};

// 默认的空进程模块对象
static Process INVAILD_PROCESS_MODULE;


/************************************************************************/
/* 系统进程集管理对象                                                   */
/************************************************************************/ 
class SystemProcessMgr
{
	public:
		SystemProcessMgr() {
			this->IS_X64_OS = OS_UTILS::isX64();
			this->processMap = new map<DWORD, BaseProcess>();
			this->pids = new DWORD[1];
			this->processes = new BaseProcess*[1];
			this->process = new Process();
		}

		~SystemProcessMgr() {
			clearProcesses();
			delete processMap; processMap = NULL;
			delete[] pids; pids = NULL;
			delete[] processes; processes = NULL;
			delete process; process = NULL;
		}

		bool reflashProcessList();
		const BaseProcess& getBaseProcessInfo(DWORD pid);	// 获取简单的进程信息

		DWORD* getAllPIDs();
		BaseProcess** getAllProcesses();	// 获取所有进程对象的地址数组
		Process* getProcess(DWORD pid);		// 获取进程对象指针(包含模块信息)

	protected:
		bool traverseProcesses();
		void clearProcesses();

		const BaseProcess& addProcess(DWORD pid, string pName);
		bool isX64Process(DWORD pid);
		static bool compare(BaseProcess* aProc, BaseProcess* bProc);	// 类内的sort比较函数必须是静态

	private:
		bool IS_X64_OS;
		map<DWORD, BaseProcess>* processMap;	// 当前进程表
		DWORD* pids;							// 当前进程对象的ID数组
		BaseProcess** processes;				// 当前进程对象的指针数组
		Process* process;						// 当前选中的进程指针
};






////在psaipi.dll中的函数EnumProcesses用来枚举进程 
//typedef BOOL (_stdcall *ENUMPROCESSES)(  //注意这里要指明调用约定为-stdcall
//	DWORD* pProcessIds,  //指向进程ID数组链  
//	DWORD cb,    //ID数组的大小，用字节计数
//	DWORD* pBytesReturned);   //返回的字节
//
////在psapi.dll中的函数EnumProcessModules用来枚举进程模块
//typedef BOOL (_stdcall *ENUMPROCESSMODULES)(
//	HANDLE hProcess,   //进程句柄
//	HMODULE* lphModule, //指向模块句柄数组链
//	DWORD cb,    //模块句柄数组大小，字节计数
//	LPDWORD lpcbNeeded);   //存储所有模块句柄所需的字节数
//
////在psapi.dll中的函数GetModuleFileNameEx获得进程模块名
//typedef DWORD (_stdcall *GETMODULEFILENAMEEX)(
//	HANDLE hProcess,   //进程句柄
//	HMODULE hModule,   //进程模块句柄
//	LPTSTR lpFilename,   //存放模块全路径名
//	DWORD nSize    //lpFilename缓冲区大小，字符计算
//	);