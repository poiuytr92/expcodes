
// WinProcessDlg.h : 头文件
//

#pragma once

#include "SystemProcessMgr.h"
#include "ProcessModuleDlg.h"

// CWinProcessDlg 对话框
class CWinProcessDlg : public CDialogEx
{
// 构造
public:
	CWinProcessDlg(CWnd* pParent = NULL);	// 标准构造函数
	~CWinProcessDlg();

// 对话框数据
	enum { IDD = IDD_WinProcess_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV 支持


// 实现
protected:
	HICON m_hIcon;

	// 生成的消息映射函数
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()

	void reflashProcessList(bool sortByPID);
	void addToList(int idx, Process process);

public:
	afx_msg void OnBnClickedReflash();
	afx_msg void OnBnClickedDetail();
	afx_msg void OnBnClickedExit();
	afx_msg void OnLvnItemchangedList2(NMHDR *pNMHDR, LRESULT *pResult);
	CListCtrl m_process_table;

	SystemProcessMgr* spMgr;
	ProcessModuleDlg* dpd;
};
