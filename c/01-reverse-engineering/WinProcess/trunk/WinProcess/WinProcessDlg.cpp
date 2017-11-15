
// WinProcessDlg.cpp : 实现文件
//

#include "stdafx.h"
#include "WinProcess.h"
#include "WinProcessDlg.h"
#include "afxdialogex.h"

#include "utils_str.h"
#include "DialogProcessDetail.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// 用于应用程序“关于”菜单项的 CAboutDlg 对话框

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

// 对话框数据
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

// 实现
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
END_MESSAGE_MAP()


// CWinProcessDlg 对话框




CWinProcessDlg::CWinProcessDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CWinProcessDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	spMgr = new SystemProcessMgr();
}

CWinProcessDlg::~CWinProcessDlg() {
	delete spMgr; spMgr = NULL;
}

void CWinProcessDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_PROCESS_LIST, m_process_table);
}

BEGIN_MESSAGE_MAP(CWinProcessDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDOK, &CWinProcessDlg::OnBnClickedReflash)
	ON_BN_CLICKED(IDC_DETAIL, &CWinProcessDlg::OnBnClickedDetail)
	ON_BN_CLICKED(IDCANCEL, &CWinProcessDlg::OnBnClickedExit)
	ON_NOTIFY(LVN_ITEMCHANGED, IDC_PROCESS_LIST, &CWinProcessDlg::OnLvnItemchangedList2)
END_MESSAGE_MAP()


// CWinProcessDlg 消息处理程序

BOOL CWinProcessDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// 将“关于...”菜单项添加到系统菜单中。

	// IDM_ABOUTBOX 必须在系统命令范围内。
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// 设置此对话框的图标。当应用程序主窗口不是对话框时，框架将自动
	//  执行此操作
	SetIcon(m_hIcon, TRUE);			// 设置大图标
	SetIcon(m_hIcon, FALSE);		// 设置小图标

	// TODO: 在此添加额外的初始化代码

	/*++++ 初始化进程表单 ++++*/
	DWORD style = m_process_table.GetExtendedStyle();
	style |= LVS_EX_FULLROWSELECT;	// 当选中某个单元格时同时选中整行
	style |= LVS_EX_GRIDLINES;		// 显示网格
	style |= LVS_ALIGNTOP;			// 垂直滚动条
	m_process_table.SetExtendedStyle(style);

	m_process_table.InsertColumn(0, _T("序号"), LVCFMT_CENTER, 50);
	m_process_table.InsertColumn(1, _T("进程号"), LVCFMT_CENTER, 75);
	m_process_table.InsertColumn(2, _T("平台"), LVCFMT_CENTER, 50);
	m_process_table.InsertColumn(3, _T("进程名称"), LVCFMT_CENTER, 100);
	m_process_table.SetColumnWidth(3, LVSCW_AUTOSIZE_USEHEADER);	// 自动调整最后一列列宽, 避免出现空列
	/*---- 初始化进程表单 ----*/

	// 刷新进程列表
	reflashProcessList(true);

	return TRUE;  // 除非将焦点设置到控件，否则返回 TRUE
}

void CWinProcessDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// 如果向对话框添加最小化按钮，则需要下面的代码
//  来绘制该图标。对于使用文档/视图模型的 MFC 应用程序，
//  这将由框架自动完成。

void CWinProcessDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // 用于绘制的设备上下文

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// 使图标在工作区矩形中居中
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// 绘制图标
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

//当用户拖动最小化窗口时系统调用此函数取得光标
//显示。
HCURSOR CWinProcessDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}


// 刷新进程列表按钮事件
void CWinProcessDlg::OnBnClickedReflash()
{
	bool sortByPID = (::MessageBox(this->m_hWnd, _T("Sort By PID ? ( Else By Name )"), _T("Tips"), MB_OKCANCEL) == IDOK);
	reflashProcessList(sortByPID);
}

// 刷新进程列表
void CWinProcessDlg::reflashProcessList(bool sortByPID) {
	// 清空进程列表
	m_process_table.DeleteAllItems();

	// 获取当前系统进程信息
	spMgr->reflashProcessList();

	// 更新进程列表
	int cnt = 0;
	if(sortByPID == true) {	// 按进程号升序排序
		DWORD* pids = spMgr->getAllPIDs();
		for(; *(pids + cnt) != INVAILD_PID; cnt++) {
			DWORD pid = *(pids + cnt);
			Process process = spMgr->getProcess(pid);
			addToList(cnt, process);
		}

	} else {	// 按进程名升序排序
		Process** processes = spMgr->getAllProcesses();
		for(; (**(processes + cnt)) != INVAILD_PROCESS; cnt++) {
			Process process = (**(processes + cnt));
			addToList(cnt, process);
		}
	}

	// 更新进程总数
	TCHAR* wCNT = STR_UTILS::toWChar(cnt);
	SetDlgItemText(IDC_PROCESS_NUM, wCNT);
	STR_UTILS::sFree(wCNT);
}

// FIXME : toWChar返回的数组所占用的资源未被释放
void CWinProcessDlg::addToList(int idx, Process process) {
	TCHAR* wIDX = STR_UTILS::toWChar(idx + 1);
	TCHAR* wPID = STR_UTILS::toWChar(process.pid);
	TCHAR* wName = STR_UTILS::toWChar(process.pName.c_str());

	m_process_table.InsertItem(idx, wIDX);
	m_process_table.SetItemText(idx, 1, wPID);
	m_process_table.SetItemText(idx, 2, (process.isX64 ? _T("x64") : _T("x86")));
	m_process_table.SetItemText(idx, 3, wName);	

	STR_UTILS::sFree(wIDX);
	STR_UTILS::sFree(wPID);
	STR_UTILS::sFree(wName);
}

void CWinProcessDlg::OnBnClickedDetail()
{
	int rowId = m_process_table.GetNextItem(-1, LVIS_SELECTED);
	if(rowId >= 0) {
		CString sPID = m_process_table.GetItemText(rowId, 1);
		DWORD pid = _wtol(sPID);

		ProcessModule* pm = spMgr->getProcessModuleInfo(pid);
		DialogProcessDetail* dpd = new DialogProcessDetail();
		dpd->updateToList(pm);
		dpd->DoModal();	// 显示界面
	}
}

void CWinProcessDlg::OnBnClickedExit()
{
	if(::MessageBox(this->m_hWnd, _T("Exit ?"), _T("Tips"), MB_OKCANCEL) == IDOK) {
		CDialogEx::OnCancel();
	}
}

void CWinProcessDlg::OnLvnItemchangedList2(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMLISTVIEW pNMLV = reinterpret_cast<LPNMLISTVIEW>(pNMHDR);
	// TODO: 在此添加控件通知处理程序代码
	*pResult = 0;
}
