// DialogProcessDetail.cpp : 实现文件
//

#include "stdafx.h"
#include "WinProcess.h"
#include "DialogProcessDetail.h"
#include "afxdialogex.h"
#include "SystemProcessMgr.h"
#include "utils_str.h"


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
	DDX_Control(pDX, IDC_MODULE_LIST, m_module_table);
}

BOOL DialogProcessDetail::OnInitDialog() {
	CDialogEx::OnInitDialog();

	// TODO: 在此添加额外的初始化代码
	/*++++ 初始化进程模块表单 ++++*/
	DWORD style = m_module_table.GetExtendedStyle();
	style |= LVS_EX_FULLROWSELECT;	// 当选中某个单元格时同时选中整行
	style |= LVS_EX_GRIDLINES;		// 显示网格
	style |= LVS_ALIGNTOP;			// 垂直滚动条
	m_module_table.SetExtendedStyle(style);

	m_module_table.InsertColumn(0, _T("序号"), LVCFMT_CENTER, 50);
	m_module_table.InsertColumn(1, _T("模块名称"), LVCFMT_CENTER, 100);
	m_module_table.SetColumnWidth(2, LVSCW_AUTOSIZE_USEHEADER);	// 自动调整最后一列列宽, 避免出现空列
	/*---- 初始化进程模块表单 ----*/

	return TRUE;
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


	m_module_table.DeleteAllItems();
	list<string>::iterator nameIts = pm->mNames->begin();
	list<string>::iterator pathIts = pm->mPaths->begin();
	for(int i = 0; i < pm->mCnt; i++, nameIts++, pathIts++) {
		string name = *nameIts;
		string path = *pathIts;
		addToList(i, name, path);
	}

}

// FIXME : toWChar返回的数组所占用的资源未被释放
void DialogProcessDetail::addToList(int idx, string mName, string mPath) {
	TCHAR* wIDX = STR_UTILS::toWChar(idx + 1);
	TCHAR* wName = STR_UTILS::toWChar(mName.c_str());
	TCHAR* wPath = STR_UTILS::toWChar(mPath.c_str());

	m_module_table.InsertItem(idx, wIDX);
	m_module_table.SetItemText(idx, 1, wName);
	m_module_table.SetItemText(idx, 2, wPath);

	STR_UTILS::sFree(wIDX);
	STR_UTILS::sFree(wName);
	STR_UTILS::sFree(wPath);
}

BEGIN_MESSAGE_MAP(DialogProcessDetail, CDialogEx)
END_MESSAGE_MAP()


// DialogProcessDetail 消息处理程序