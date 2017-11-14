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


BEGIN_MESSAGE_MAP(DialogProcessDetail, CDialogEx)
END_MESSAGE_MAP()


// DialogProcessDetail 消息处理程序
