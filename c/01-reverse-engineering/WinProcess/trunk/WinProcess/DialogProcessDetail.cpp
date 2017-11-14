// DialogProcessDetail.cpp : 实现文件
//

#include "stdafx.h"
#include "WinProcess.h"
#include "DialogProcessDetail.h"
#include "afxdialogex.h"
#include "SystemProcessMgr.h"


// DialogProcessDetail 对话框

IMPLEMENT_DYNAMIC(DialogProcessDetail, CDialogEx)


DialogProcessDetail::DialogProcessDetail(CWnd* pParent /*=NULL*/)
	: CDialogEx(DialogProcessDetail::IDD, pParent)
{
}

DialogProcessDetail::~DialogProcessDetail()
{
}

void DialogProcessDetail::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

void DialogProcessDetail::updateToList(ProcessModule* pm) {

	TRACE(_T("\n ---->  me32.dwSize==%d"), pm->mSize);
	TRACE(_T("\n ---->  me32.GlblcntUsage==%d"), pm->usage);
	TRACE(_T("\n ---->  me32.hModule==0x%x"), pm->hModule);
	TRACE(_T("\n ---->  me32.modBaseAddr==0x%x"), pm->baseAddr);
	//TRACE(_T("\n ---->  me32.szExePath==%s"), me.szExePath);
	//TRACE(_T("\n ---->  me32.szModule==%s"), me.szModule);
	TRACE(_T("\n ---->  me32.th32ModuleID==%d"), pm->mID);
	TRACE(_T("\n ---->  me32.th32ProcessID==%d"), pm->pid);
	TRACE(_T("\n ---->  CNT==%d\n"), pm->mCnt);
}


BEGIN_MESSAGE_MAP(DialogProcessDetail, CDialogEx)
END_MESSAGE_MAP()


// DialogProcessDetail 消息处理程序