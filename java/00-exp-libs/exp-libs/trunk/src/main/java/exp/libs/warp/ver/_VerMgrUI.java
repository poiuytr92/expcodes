package exp.libs.warp.ver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import exp.libs.utils.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

class _VerMgrUI extends MainWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -3365462601777108786L;
	
	private final static int CUR_VER_TAB_IDX = 2;
	
	private JTabbedPane tabbedPanel;
	
	/** 项目版本信息 */
	private _PrjVerInfo prjVerInfo;
	
	/** 保存项目信息的按钮 */
	private JButton savePrjInfoBtn;
	
	/** 查找历史版本的按钮 */
	private JButton findHisVerBtn;
	
	/** 复制当前版本信息的按钮 */
	private JButton copyCurVerBtn;
	
	/** 新增新版本信息的按钮 */
	private JButton createVerBtn;
	
	/** 用于记录新增版本的临时版本信息 */
	private _VerInfo tmpVerInfo;
	
	private Vector<Vector<String>> hisVerInfos;
	
	private _HisVerTable hisVerTable;
	
	/** 界面单例 */
	private static volatile _VerMgrUI instance;
	
	/**
	 * 私有化构造函数
	 */
	private _VerMgrUI(String title, _PrjVerInfo prjVerInfo) {
		super(title, 600, 400, false, prjVerInfo);
	}
	
	/**
	 * 创建程序UI实例
	 * @param prjName 项目名称
	 * @param verInfos 版本信息
	 * @return
	 */
	public static _VerMgrUI getInstn(_PrjVerInfo prjVerInfo) {
		if(instance == null) {
			synchronized (_VerMgrUI.class) {
				if(instance == null) {
					String title = getTitle(prjVerInfo);
					instance = new _VerMgrUI(title, prjVerInfo);
				}
			}
		}
		return instance;
	}
	
	private static String getTitle(_PrjVerInfo prjVerInfo) {
		String title = "版本管理";
		if(prjVerInfo != null) {
			String prjName = prjVerInfo.getPrjName();
			if(StrUtils.isNotEmpty(prjName)) {
				title = StrUtils.concat(title, " [", prjName, "]");
			}
		}
		return title;
	}
	
	@Override
	protected void initComponents(Object... args) {
		if(args != null && args.length > 0 && args[0] instanceof _PrjVerInfo) {
			this.prjVerInfo = (_PrjVerInfo) args[0];
		}
		
		this.savePrjInfoBtn = new JButton("保存");
		this.findHisVerBtn = new JButton("查找");
		this.copyCurVerBtn = new JButton("复制当前版本信息");
		this.createVerBtn = new JButton("保存");
		
		initVer();
	}
	
	private void initVer() {
		this.tmpVerInfo = new _VerInfo();
		this.hisVerTable = initTable();
	}

	private _HisVerTable initTable() {
		Vector<String> header = initHeader();
		this.hisVerInfos = new Vector<Vector<String>>();
		reflashHisVerInfos();
		return new _HisVerTable(header, hisVerInfos);
	}
	
	private Vector<String> initHeader() {
		Vector<String> header = new Vector<String>();
		header.add("版本号");
		header.add("责任人");
		header.add("定版时间");
		header.add("升级内容概要");
		return header;
	}
	
	private void reflashHisVerInfos() {
		final int MAX_HIS_VER_NUM = 50;	// 最多显示的历史版本数目
		this.hisVerInfos.clear();
		List<_VerInfo> historyVers = prjVerInfo.getHistoryVers();
		for(int i = historyVers.size() - 1; i >= 0; i--) {
			_VerInfo verInfo = historyVers.get(i);
			Vector<String> row = new Vector<String>();
			row.add(verInfo.getVersion());
			row.add(verInfo.getAuthor());
			row.add(verInfo.getDatetime());
			row.add(StrUtils.showSummary(verInfo.getUpgradeContent().trim()));
			hisVerInfos.add(row);
		}
		
		// 填充空白行
		for(int size = MAX_HIS_VER_NUM - historyVers.size(), 
				i = 0; i < size; i++) {
			hisVerInfos.add(new Vector<String>());
		}
	}
	
	private void reflashHisVerTable() {
		reflashHisVerInfos();
		hisVerTable.reflash();
	}
	
	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(initTabPanel(), BorderLayout.CENTER);
	}

	private JPanel initTabPanel() {
		JPanel tabPanel = new JPanel(new BorderLayout()); {
			this.tabbedPanel = new JTabbedPane(JTabbedPane.TOP); {
				tabbedPanel.add(initPrjInfoPanel(), "项目信息");
				tabbedPanel.add(initHistoryPanel(), "历史版本信息");
				tabbedPanel.add(initCurrentPanel(), "当前版本信息");
				tabbedPanel.add(initNewVerPanel(), "新增版本信息");
			}
			tabbedPanel.setSelectedIndex(CUR_VER_TAB_IDX);	// 默认选中 [当前版本信息]
			tabPanel.add(tabbedPanel, BorderLayout.CENTER);
		}
		return tabPanel;
	}
	
	private Component initPrjInfoPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			panel.add(prjVerInfo.toPanel(), BorderLayout.CENTER);
		}
		panel.add(savePrjInfoBtn, BorderLayout.SOUTH);
		return panel;
	}

	// 右键可查看详情，允许删除, 删除需确认
	private Component initHistoryPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			JPanel tblPanel = new JPanel(new BorderLayout()); {
				tblPanel.add(SwingUtils.addAutoScroll(hisVerTable), BorderLayout.CENTER);
			}
			panel.add(tblPanel, BorderLayout.CENTER);
		}
		panel.add(findHisVerBtn, BorderLayout.SOUTH);
		return panel;
	}
	
	private Component initCurrentPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			_VerInfo curVerInfo = prjVerInfo.getCurVer();
			panel.add(curVerInfo.toPanel(false), BorderLayout.CENTER);
		}
		panel.add(copyCurVerBtn, BorderLayout.SOUTH);
		return panel;
	}

	// 禁止添加重复的版本号 和 小于当前版本的版本号
	private Component initNewVerPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			panel.add(tmpVerInfo.toPanel(true), BorderLayout.CENTER);
		}
		panel.add(createVerBtn, BorderLayout.SOUTH);
		return panel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		savePrjInfoBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				prjVerInfo.savePrjInfo();
			}
		});
		
		findHisVerBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		copyCurVerBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		createVerBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_VerInfo newVerInfo = new _VerInfo();
				newVerInfo.setValFromUI(tmpVerInfo);
				
				if(prjVerInfo.addVerInfo(newVerInfo)) {
					tmpVerInfo.clear();	// 清空 [新增版本信息] 面板
					reflashHisVerTable();	// 刷新 [历史版本信息] 列表
					tabbedPanel.setSelectedIndex(CUR_VER_TAB_IDX);	// 切到选中 [当前版本信息]
					
				} else {
					SwingUtils.warn("新建版本失败");
				}
			}
		});
	}
	
}
