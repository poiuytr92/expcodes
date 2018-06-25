package exp.libs.warp.ver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.NormTable;
import exp.libs.warp.ui.cpt.win.MainWindow;

/**
 * <PRE>
 * 程序版本管理界面
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _VerMgrUI extends MainWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -3365462601777108786L;
	
	private final static String[] HEADER = {
		"版本�?", "责任�?", "定版时间", "升级内容概要"
	};
	
	private final static String DEFAULT_TITLE = "版本管理";
	
	/** [当前版本]的Tab面板索引 */
	private final static int CUR_VER_TAB_IDX = 2;
	
	/** Tab面板 */
	private JTabbedPane tabbedPanel;
	
	/** 项目版本信息 */
	private _PrjVerInfo prjVerInfo;
	
	/** 历史版本表单 */
	private _HisVerTable hisVerTable;
	
	/** 用于编辑新增版本的临时对�? */
	private _VerInfo tmpVerInfo;
	
	/** 保存项目信息的按�? */
	private JButton savePrjInfoBtn;
	
	/** 查找历史版本的按�? */
	private JButton findHisVerBtn;
	
	/** 修改当前版本信息的按�? */
	private JButton modifyCurVerBtn;
	
	/** 新增新版本信息的按钮 */
	private JButton createVerBtn;
	
	/** 界面单例 */
	private static volatile _VerMgrUI instance;
	
	/**
	 * 私有化构造函�?
	 */
	private _VerMgrUI() {
		super(DEFAULT_TITLE, 700, 430);
	}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	protected static _VerMgrUI getInstn() {
		if(instance == null) {
			synchronized (_VerMgrUI.class) {
				if(instance == null) {
					instance = new _VerMgrUI();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 覆写窗口的退出模�?
	 * 	（不自动显示窗体�? 且增�? System.exit, 因为单纯的隐藏窗体无法结束数据库进程�?
	 */
	@Override
	protected void initCloseWindowMode() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(SwingUtils.confirm("退�? ?")) {
					_hide();
					System.exit(0);
				}
			}
		});
	}
	
	@Override
	protected void initComponents(Object... args) {
		if(_VerDBMgr.getInstn().initVerDB()) {
			this.prjVerInfo = loadPrjVerInfo();
		} else {
			this.prjVerInfo = new _PrjVerInfo(null);
		}
		
		this.hisVerTable = new _HisVerTable();
		this.tmpVerInfo = new _VerInfo();
		
		updateTitle();
		reflashHisVerTable();
		
		this.savePrjInfoBtn = new JButton("�?  �?");
		this.findHisVerBtn = new JButton("�?  �?");
		this.modifyCurVerBtn = new JButton("�?  �?");
		this.createVerBtn = new JButton("�?  �?");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, 
				savePrjInfoBtn, findHisVerBtn, modifyCurVerBtn, createVerBtn);
		savePrjInfoBtn.setForeground(Color.BLACK);
		findHisVerBtn.setForeground(Color.BLACK);
		modifyCurVerBtn.setForeground(Color.BLACK);
		createVerBtn.setForeground(Color.BLACK);
	}
	
	private _PrjVerInfo loadPrjVerInfo() {
		List<_VerInfo> verInfos = toVerInfos(_VerDBMgr.getInstn().getHisVerInfos());
		_PrjVerInfo prjVerInfo = new _PrjVerInfo(verInfos);
		
		Map<String, String> prjInfo = _VerDBMgr.getInstn().getPrjVerInfo();
		prjVerInfo.setPrjName(prjInfo.get("S_PROJECT_NAME"));
		prjVerInfo.setPrjDesc(prjInfo.get("S_PROJECT_DESC"));
		prjVerInfo.setTeamName(prjInfo.get("S_TEAM_NAME"));
		prjVerInfo.setPrjCharset(prjInfo.get("S_PROJECT_CHARSET"));
		prjVerInfo.setDiskSize(prjInfo.get("S_DISK_SIZE"));
		prjVerInfo.setCacheSize(prjInfo.get("S_CACHE_SIZE"));
		prjVerInfo.setAPIs(prjInfo.get("S_APIS"));
		return prjVerInfo;
	}
	
	private List<_VerInfo> toVerInfos(List<Map<String, String>> verDatas) {
		List<_VerInfo> verInfos = new LinkedList<_VerInfo>();
		for(Map<String, String> verData : verDatas) {
			_VerInfo verInfo = new _VerInfo();
			verInfo.setAuthor(verData.get("S_AUTHOR"));
			verInfo.setVersion(verData.get("S_VERSION"));
			verInfo.setDatetime(verData.get("S_DATETIME"));
			verInfo.setUpgradeContent(verData.get("S_UPGRADE_CONTENT"));
			verInfo.setUpgradeStep(verData.get("S_UPGRADE_STEP"));
			
			verInfo.setValToUI();	// 把读取到的值设置到界面容器�?
			verInfos.add(verInfo);
		}
		return verInfos;
	}

	private void updateTitle() {
		String title = DEFAULT_TITLE;
		String prjName = prjVerInfo.getPrjName();
		if(StrUtils.isNotEmpty(prjName)) {
			title = StrUtils.concat(title, " [", prjName, "]");
		}
		setTitle(title);
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

	private Component initHistoryPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			JScrollPane tblPanel = SwingUtils.addAutoScroll(hisVerTable);
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
		panel.add(modifyCurVerBtn, BorderLayout.SOUTH);
		return panel;
	}

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
				if(savePrjInfo()) {
					SwingUtils.info("保存项目信息成功");
				} else {
					SwingUtils.warn("保存项目信息失败");
				}
			}
		});
		
		findHisVerBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String keyword = SwingUtils.input("请输入查找关键字: ");
				reflashHisVerTable(keyword);
			}
		});
		
		modifyCurVerBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(modifyCurVerInfo()) {
					SwingUtils.info("更新当前版本信息成功");
				} else {
					SwingUtils.warn("更新当前版本信息失败");
				}
			}
		});
		
		createVerBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_VerInfo newVerInfo = new _VerInfo();
				newVerInfo.setValFromUI(tmpVerInfo);
				
				String errDesc = checkVerInfo(newVerInfo);
				if(StrUtils.isEmpty(errDesc)) {
					if(addVerInfo(newVerInfo)) {
						tmpVerInfo.clear();		// 清空 [新增版本信息] 面板
						reflashHisVerTable();	// 刷新 [历史版本信息] 列表
						tabbedPanel.setSelectedIndex(CUR_VER_TAB_IDX);	// 切到选中 [当前版本信息]
						SwingUtils.info("新增版本成功");
						
					} else {
						SwingUtils.warn("保存新版本信息失�?");
					}
				} else {
					SwingUtils.warn("新增版本失败: ".concat(errDesc));
				}
			}
		});
	}
	
	@Override
	protected void AfterView() {}

	@Override
	protected void beforeHide() {}
	
	@Override
	protected void beforeExit() {}
	
	/**
	 * 保存项目信息
	 * @return
	 */
	private boolean savePrjInfo() {
		prjVerInfo.setValFromUI();
		return _VerDBMgr.getInstn().savePrjInfo(prjVerInfo);
	}
	
	/**
	 * 检查版本信�?
	 * @param verInfo
	 * @return 非空则通过
	 */
	private String checkVerInfo(_VerInfo verInfo) {
		return prjVerInfo.checkVersion(verInfo);
	}
	
	/**
	 * 新增版本信息
	 * @param verInfo
	 * @return
	 */
	private boolean addVerInfo(_VerInfo verInfo) {
		boolean isOk = _VerDBMgr.getInstn().addVerInfo(verInfo);
		if(isOk == true) {
			prjVerInfo.addVerInfo(verInfo);
		}
		return isOk;
	}
	
	private boolean modifyCurVerInfo() {
		_VerInfo curVerInfo = prjVerInfo.getCurVer();
		curVerInfo.getDatetimeTF().setText(TimeUtils.getSysDate());
		curVerInfo.setValFromUI(null);
		
		boolean isOk = _VerDBMgr.getInstn().modifyCurVerInfo(curVerInfo);
		if(isOk == true) {
			prjVerInfo.modifyCurVerInfo();
		}
		return isOk;
	}
	
	/**
	 * 删除版本信息
	 * @param verInfo
	 * @return
	 */
	private boolean delVerInfo(_VerInfo verInfo) {
		boolean isOk = _VerDBMgr.getInstn().delVerInfo(verInfo);
		if(isOk == true) {
			prjVerInfo.delVerInfo(verInfo);
		}
		return isOk;
	}
	
	private _VerInfo getVerInfo(int row) {
		return prjVerInfo.getVerInfo(row);
	}
	
	private void reflashHisVerTable() {
		reflashHisVerTable(null);
	}
	
	private void reflashHisVerTable(String keyword) {
		hisVerTable.reflash(prjVerInfo.toHisVerTable(keyword));
	}
	
	protected String getCurVerInfo() {
		return _VerDBMgr.getInstn().toCurVerInfo(
				prjVerInfo.getPrjName(), 
				prjVerInfo.getPrjDesc(), 
				prjVerInfo.getCurVer().getVersion(), 
				prjVerInfo.getCurVer().getDatetime(), 
				prjVerInfo.getCurVer().getAuthor());
	}
	
	
	/**
	 * <PRE>
	 * 历史版本表单组件
	 * </PRE>
	 * 
	 * @author Administrator
	 * @date 2017�?7�?6�?
	 */
	private class _HisVerTable extends NormTable {
		
		private static final long serialVersionUID = -3111568334645181825L;
		
		private _HisVerTable() {
			super(HEADER, 100);
		}
		
		@Override
		protected void initRightBtnPopMenu(JPopupMenu popMenu) {
			JMenuItem detail = new JMenuItem("查看详情");
			JMenuItem delete = new JMenuItem("删除版本");
			JMenuItem reflash = new JMenuItem("刷新列表");
			popMenu.add(detail);
			popMenu.add(delete);
			popMenu.add(reflash);
			
			detail.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					_VerInfo verInfo = getVerInfo(getCurRow());
					if(verInfo != null) {
						verInfo._view();
					}
				}
			});
			
			delete.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					_VerInfo verInfo = getVerInfo(getCurRow());
					if(verInfo == null) {
						return;
					}
					
					String desc = StrUtils.concat("删除版本 [", verInfo.getVersion(), "]");
					if(SwingUtils.confirm(StrUtils.concat("确认", desc, " ?"))) {
						if(delVerInfo(verInfo)) {
							reflashHisVerTable();	// 刷新表单
							SwingUtils.warn(StrUtils.concat("删除", desc, " 成功"));
							
						} else {
							SwingUtils.warn(StrUtils.concat("删除", desc, " 失败"));
						}
					}
				}
			});
			
			reflash.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					reflashHisVerTable();	// 刷新表单
				}
			});
		}
		
	}

}
