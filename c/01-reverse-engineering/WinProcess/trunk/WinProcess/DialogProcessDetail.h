#pragma once

#include "SystemProcessMgr.h"

// DialogProcessDetail 对话框

class DialogProcessDetail : public CDialogEx
{
	DECLARE_DYNAMIC(DialogProcessDetail)

public:
	DialogProcessDetail(CWnd* pParent = NULL);   // 标准构造函数
	virtual ~DialogProcessDetail();

	void updateToList(ProcessModule* pm);

// 对话框数据
	enum { IDD = IDD_PROC_DETAIL };

protected:
	virtual BOOL OnInitDialog();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

	void addToList(int idx, string mName, string mPath);

	DECLARE_MESSAGE_MAP()
public:
	CListCtrl m_module_table;
};
