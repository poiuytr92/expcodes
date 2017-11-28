// ProcessModuleDlg.cpp : 实现文件
//

#include "stdafx.h"
#include "WinProcess.h"
#include "ProcessModuleDlg.h"
#include "afxdialogex.h"
#include "SystemProcessMgr.h"
#include "utils_str.h"
#include "utils_os.h"


// ProcessModuleDlg 对话框

IMPLEMENT_DYNAMIC(ProcessModuleDlg, CDialogEx)


ProcessModuleDlg::ProcessModuleDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(ProcessModuleDlg::IDD, pParent)
{
}

ProcessModuleDlg::~ProcessModuleDlg()
{
}

void ProcessModuleDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_MODULE_LIST, m_module_table);
}

BOOL ProcessModuleDlg::OnInitDialog() {
	CDialogEx::OnInitDialog();

	// TODO: 在此添加额外的初始化代码

	/*++++ 初始化进程模块表单 ++++*/
	DWORD style = m_module_table.GetExtendedStyle();
	style |= LVS_EX_FULLROWSELECT;	// 当选中某个单元格时同时选中整行
	style |= LVS_EX_GRIDLINES;		// 显示网格
	style |= LVS_ALIGNTOP;			// 垂直滚动条
	m_module_table.SetExtendedStyle(style);

	m_module_table.InsertColumn(0, _T("序号"), LVCFMT_CENTER, 50);
	m_module_table.InsertColumn(1, _T("模块大小(byte)"), LVCFMT_CENTER, 120);
	m_module_table.InsertColumn(2, _T("全局引用数"), LVCFMT_CENTER, 100);
	m_module_table.InsertColumn(3, _T("基址指针(指向地址可能无效)"), LVCFMT_CENTER, 220);
	m_module_table.InsertColumn(4, _T("模块名称"), LVCFMT_CENTER, 100);
	m_module_table.InsertColumn(5, _T("模块路径"), LVCFMT_CENTER, 100);
	m_module_table.SetColumnWidth(5, LVSCW_AUTOSIZE_USEHEADER);	// 自动调整最后一列列宽, 避免出现空列
	/*---- 初始化进程模块表单 ----*/

	return TRUE;
}

void ProcessModuleDlg::updateToList(Process* process) {
	TCHAR* wTmp = STR_UTILS::toWChar(process->pid);
	SetDlgItemText(IDC_STATIC_PID, wTmp);
	STR_UTILS::sFree(wTmp);

	wTmp = STR_UTILS::toWChar(process->mCnt);
	SetDlgItemText(IDC_STATIC_MCNT, wTmp);
	STR_UTILS::sFree(wTmp);

	m_module_table.DeleteAllItems();
	list<Module*>::iterator moduleIts = process->modules->begin();
	for(int i = 0; i < process->mCnt; i++, moduleIts++) {
		Module* module = *moduleIts;
		addToList(i, module);
	}

}

void ProcessModuleDlg::addToList(int idx, Module* module) {
	TCHAR* wIDX = STR_UTILS::toWChar(idx + 1);
	TCHAR* wSize = STR_UTILS::toWChar(module->mSize);
	TCHAR* wUsage = STR_UTILS::toWChar(module->usage);
	TCHAR* wName = STR_UTILS::toWChar(module->name.c_str());
	TCHAR* wPath = STR_UTILS::toWChar(module->path.c_str());

	m_module_table.InsertItem(idx, wIDX);
	m_module_table.SetItemText(idx, 1, wSize);
	m_module_table.SetItemText(idx, 2, wUsage);
	m_module_table.SetItemText(idx, 4, wName);
	m_module_table.SetItemText(idx, 5, wPath);

	STR_UTILS::sFree(wIDX);
	STR_UTILS::sFree(wSize);
	STR_UTILS::sFree(wUsage);
	STR_UTILS::sFree(wName);
	STR_UTILS::sFree(wPath);



	// FIXME: 此处打印的【基址指针】有两个问题
	// ① 实际基址指针长度为双字节（16位），但swprintf_s函数强行把高8位全置零
	// ② 该基址指针所指向的基址未必是有效地址（似乎由于win访问权限的问题,部分x64模块地址被屏蔽）
	const int ADDR_LEN = 32;
	TCHAR wAddr[ADDR_LEN] = {0};
	swprintf_s(wAddr, ADDR_LEN, _T("0x%016x"), module->pBaseAddr);
	m_module_table.SetItemText(idx, 3, wAddr);
}

BEGIN_MESSAGE_MAP(ProcessModuleDlg, CDialogEx)
	ON_BN_CLICKED(IDOK, &ProcessModuleDlg::OnBnClickedOk)
END_MESSAGE_MAP()


// ProcessModuleDlg 消息处理程序

void ProcessModuleDlg::OnBnClickedOk() {
	bool isOk = false;
	int rowId = m_module_table.GetNextItem(-1, LVIS_SELECTED);
	if(rowId >= 0) {
		CString mSize = m_module_table.GetItemText(rowId, 1);
		CString usage = m_module_table.GetItemText(rowId, 2);
		CString mAddr = m_module_table.GetItemText(rowId, 3);
		CString mName = m_module_table.GetItemText(rowId, 4);
		CString mPath = m_module_table.GetItemText(rowId, 5);
		
		// 拼接模块信息
		CString mInfo;
		mInfo.Format(_T("[模块名称] %s\r\n[模块路径] %s\r\n[模块基址] %s\r\n[模块大小] %s byte\r\n[全局引用] %s\r\n"), 
			mName, mPath, mAddr, mSize, usage);

		// 转换信息格式并复制到剪贴板
		char* pMInfo = STR_UTILS::toChar(mInfo.GetBuffer(mInfo.GetLength()));
		isOk = OS_UTILS::copyToClipboard(pMInfo);

		STR_UTILS::sFree(pMInfo);
		mInfo.ReleaseBuffer();
	}
	
	if(isOk == true) {
		::MessageBox(this->m_hWnd, _T("copy success"), _T("Tips"), MB_OK);
	} else {
		::MessageBox(this->m_hWnd, _T("copy fail"), _T("Tips"), MB_OK);
		//AfxMessageBox(_T("copy fail"));	// 虽然更简单，但是默认父句柄是基于主窗口的，不合理
	}
}
