#pragma once

#include "SystemProcessMgr.h"

// ProcessModuleDlg 对话框

class ProcessModuleDlg : public CDialogEx
{
	DECLARE_DYNAMIC(ProcessModuleDlg)

public:
	ProcessModuleDlg(CWnd* pParent = NULL);   // 标准构造函数
	virtual ~ProcessModuleDlg();

	void updateToList(Process* pm);

// 对话框数据
	enum { IDD = IDD_PROC_DETAIL };

protected:
	virtual BOOL OnInitDialog();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

	void addToList(int idx, Module* module);

	DECLARE_MESSAGE_MAP()
public:
	CListCtrl m_module_table;
	afx_msg void OnBnClickedOk();

};
