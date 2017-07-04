package exp.libs.warp.ver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import exp.libs.utils.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.Table;
import exp.libs.warp.ui.cpt.win.MainWindow;

class _VerMgrUI extends MainWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -3365462601777108786L;
	
	/** 界面单例 */
	private static volatile _VerMgrUI instance;
	
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
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(initTabPanel(), BorderLayout.CENTER);
	}

	private JPanel initTabPanel() {
		JPanel tabPanel = new JPanel(new BorderLayout()); {
			JTabbedPane tabbedPanel = new JTabbedPane(JTabbedPane.TOP); {
				tabbedPanel.add(initPrjInfoPanel(), "项目信息");
				tabbedPanel.add(initHistoryPanel(), "历史版本信息");
				tabbedPanel.add(initCurrentPanel(), "当前版本信息");
				tabbedPanel.add(initNewVerPanel(), "新增版本信息");
			}
			tabbedPanel.setSelectedIndex(2);	// 默认选中 [当前版本信息]
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
				tblPanel.add(SwingUtils.addAutoScroll(getTable()), BorderLayout.CENTER);
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
			_VerInfo newVerInfo = new _VerInfo();
			panel.add(newVerInfo.toPanel(true), BorderLayout.CENTER);
		}
		panel.add(createVerBtn, BorderLayout.SOUTH);
		return panel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		savePrjInfoBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				prjVerInfo.save();
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
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	private Table<String> getTable() {
		Vector<String> header = getHeader();
		Vector<Vector<String>> datas = getDatas();
		Table<String> table = new Table<String>(header, datas);
		return table;
	}
	
	private Vector<String> getHeader() {
		Vector<String> header = new Vector<String>();
		header.add("版本号");
		header.add("责任人");
		header.add("定版时间");
		header.add("升级内容概要");
		return header;
	}
	
	private Vector<Vector<String>> getDatas() {
		Vector<Vector<String>> datas = new Vector<Vector<String>>();
		
		Vector<String> row = new Vector<String>();
		row.add("0.0.0");
		row.add("aaaa");
		row.add("2017-07-05");
		row.add("ddddddddddddddd");
		datas.add(row);
		
		Vector<String> row2 = new Vector<String>();
		row2.add("0.0.1");
		row2.add("bbbb");
		row2.add("2017-07-04");
		row2.add("ggggggggggggggggggggggggggg");
		datas.add(row2);
		return datas;
	}

}
