package exp.libs.warp.ver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
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

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.format.ESCUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.JarUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.warp.db.sql.DBUtils;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.NormTable;
import exp.libs.warp.ui.cpt.win.MainWindow;

/**
 * <PRE>
 * 程序版本管理界面
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _VerMgrUI extends MainWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -3365462601777108786L;
	
	private final static String[] HEADER = {
		"版本号", "责任人", "定版时间", "升级内容概要"
	};
	
	private final static String DEFAULT_TITLE = "版本管理";
	
	/** 版本信息库的脚本 */
	private final static String VER_DB_SCRIPT = "/exp/libs/warp/ver/VERSION-INFO-DB.sql";
	
	/** 版本库库名 */
	private final static String DB_NAME = ".verinfo";
	
	/** 资源目录 */
	private final static String RES_DIR = "./src/main/resources";
	
	/**
	 * 存储版本信息的文件数据库位置.
	 * 	[src/main/resources] 为Maven项目默认的资源目录位置（即使非Maven项目也可用此位置）
	 */
	private final static String VER_DB = RES_DIR.concat("/").concat(DB_NAME);
	
	/** 临时版本库位置（仅用于查看版本信息） */
	private final static String TMP_VER_DB = "./conf/".concat(DB_NAME);
	
	/** [当前版本]的Tab面板索引 */
	private final static int CUR_VER_TAB_IDX = 2;
	
	/** Tab面板 */
	private JTabbedPane tabbedPanel;
	
	/** 版本信息文件的数据源 */
	private DataSourceBean ds;
	
	/** 项目版本信息 */
	private _PrjVerInfo prjVerInfo;
	
	/** 历史版本表单 */
	private _HisVerTable hisVerTable;
	
	/** 用于编辑新增版本的临时对象 */
	private _VerInfo tmpVerInfo;
	
	/** 保存项目信息的按钮 */
	private JButton savePrjInfoBtn;
	
	/** 查找历史版本的按钮 */
	private JButton findHisVerBtn;
	
	/** 修改当前版本信息的按钮 */
	private JButton modifyCurVerBtn;
	
	/** 新增新版本信息的按钮 */
	private JButton createVerBtn;
	
	/** 界面单例 */
	private static volatile _VerMgrUI instance;
	
	/**
	 * 私有化构造函数
	 */
	private _VerMgrUI() {
		super(DEFAULT_TITLE, 600, 400);
	}
	
	/**
	 * 创建程序UI实例
	 * @param prjName 项目名称
	 * @param verInfos 版本信息
	 * @return
	 */
	public static _VerMgrUI getInstn() {
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
	 * 覆写窗口的退出模式
	 * 	（不自动显示窗体， 且增加 System.exit, 因为单纯的隐藏窗体无法结束数据库进程）
	 */
	@Override
	protected void initCloseWindowMode() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(SwingUtils.confirm("退出 ?")) {
					_hide();
					System.exit(0);
				}
			}
		});
	}
	
	@Override
	protected void initComponents(Object... args) {
		initDS();
		if(initVerDB()) {
			this.prjVerInfo = loadPrjVerInfo();
		} else {
			this.prjVerInfo = new _PrjVerInfo(null);
		}
		
		this.hisVerTable = new _HisVerTable();
		this.tmpVerInfo = new _VerInfo();
		
		updateTitle();
		reflashHisVerTable();
		
		this.savePrjInfoBtn = new JButton("保存");
		this.findHisVerBtn = new JButton("查找");
		this.modifyCurVerBtn = new JButton("修改");
		this.createVerBtn = new JButton("保存");
	}
	
	private void initDS() {
		this.ds = new DataSourceBean();
		ds.setDriver(DBType.SQLITE.DRIVER);
		ds.setCharset(Charset.UTF8);
		ds.setName(VER_DB);
		
		// 对于非开发环境, Sqlite无法直接读取jar包内的版本库, 需要先将其拷贝到硬盘
		if(!SqliteUtils.testConn(ds)) {
			if(!FileUtils.exists(TMP_VER_DB)) {
				JarUtils.copyFileInJar(VER_DB.replace(RES_DIR, ""), TMP_VER_DB);
				FileUtils.hide(TMP_VER_DB);
			}
			ds.setName(TMP_VER_DB);
		}
	}
	
	private boolean initVerDB() {
		boolean isOk = true;
		Connection conn = SqliteUtils.getConn(ds);
		String script = JarUtils.readFileInJar(VER_DB_SCRIPT, Charset.UTF8);
		try {
			String[] sqls = script.split(";");
			for(String sql : sqls) {
				if(StrUtils.isNotTrimEmpty(sql)) {
					isOk &= DBUtils.execute(conn, sql);
				}
			}
		} catch(Exception e) {
			isOk = false;
			SwingUtils.error("初始化项目版本信息库失败", e);
		}
		
		if(isOk == false) {
			SwingUtils.warn("执行项目版本信息库的初始化脚本失败");
		}
		SqliteUtils.releaseDisk(conn);
		SqliteUtils.close(conn);
		return isOk;
	}
	
	private _PrjVerInfo loadPrjVerInfo() {
		Connection conn = SqliteUtils.getConn(ds);
		String sql = StrUtils.concat("SELECT S_PROJECT_NAME, S_PROJECT_DESC, ", 
				"S_TEAM_NAME, S_PROJECT_CHARSET, S_DISK_SIZE, S_CACHE_SIZE, ",
				"S_APIS FROM T_PROJECT_INFO ORDER BY I_ID DESC LIMIT 1");
		Map<String, String> prjInfo = SqliteUtils.queryFirstRowStr(conn, sql);
		
		sql = StrUtils.concat("SELECT S_AUTHOR, S_VERSION, S_DATETIME, ", 
				"S_UPGRADE_CONTENT, S_UPGRADE_STEP FROM T_HISTORY_VERSIONS ", 
				"ORDER BY I_ID ASC");
		List<_VerInfo> verInfos = toVerInfos(SqliteUtils.queryKVSs(conn, sql));
		SqliteUtils.close(conn);
		
		_PrjVerInfo prjVerInfo = new _PrjVerInfo(verInfos);
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
			
			verInfo.setValToUI();	// 把读取到的值设置到界面容器中
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
						SwingUtils.warn("保存新版本信息失败");
					}
				} else {
					SwingUtils.warn("新增版本失败: ".concat(errDesc));
				}
			}
		});
	}
	
	/**
	 * 保存项目信息
	 * @return
	 */
	private boolean savePrjInfo() {
		prjVerInfo.setValFromUI();
		
		Connection conn = SqliteUtils.getConn(ds);
		String sql = "DELETE FROM T_PROJECT_INFO";
		SqliteUtils.execute(conn, sql);
		
		sql = StrUtils.concat("INSERT INTO T_PROJECT_INFO(S_PROJECT_NAME, ", 
				"S_PROJECT_DESC, S_TEAM_NAME, S_PROJECT_CHARSET, S_DISK_SIZE, ", 
				"S_CACHE_SIZE, S_APIS) VALUES(?, ?, ?, ?, ?, ?, ?)");
		boolean isOk = SqliteUtils.execute(conn, sql, new Object[] {
				prjVerInfo.getPrjName(), prjVerInfo.getPrjDesc(), 
				prjVerInfo.getTeamName(), prjVerInfo.getPrjCharset(), 
				prjVerInfo.getDiskSize(), prjVerInfo.getCacheSize(),
				prjVerInfo.getAPIs()
		});
		SqliteUtils.close(conn);
		return isOk;
	}
	
	/**
	 * 检查版本信息
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
		Connection conn = SqliteUtils.getConn(ds);
		String sql = StrUtils.concat("INSERT INTO T_HISTORY_VERSIONS(", 
				"S_AUTHOR, S_VERSION, S_DATETIME, S_UPGRADE_CONTENT, ", 
				"S_UPGRADE_STEP) VALUES(?, ?, ?, ?, ?)");
		boolean isOk = SqliteUtils.execute(conn, sql, new Object[] {
				verInfo.getAuthor(), verInfo.getVersion(), 
				verInfo.getDatetime(), verInfo.getUpgradeContent(), 
				verInfo.getUpgradeStep()
		});
		SqliteUtils.close(conn);
		
		if(isOk == true) {
			prjVerInfo.addVerInfo(verInfo);
		}
		return isOk;
	}
	
	private boolean modifyCurVerInfo() {
		_VerInfo curVer = prjVerInfo.getCurVer();
		curVer.getDatetimeTF().setText(TimeUtils.getSysDate());
		curVer.setValFromUI(null);
		
		Connection conn = SqliteUtils.getConn(ds);
		String sql = StrUtils.concat("UPDATE T_HISTORY_VERSIONS ", 
				"SET S_DATETIME = '", curVer.getDatetime(), "', ", 
				"S_UPGRADE_CONTENT = '", curVer.getUpgradeContent(), "', ", 
				"S_UPGRADE_STEP = '", curVer.getUpgradeStep(), "' ", 
				"WHERE S_VERSION = '", curVer.getVersion(), "'");
		boolean isOk = SqliteUtils.execute(conn, sql);
		SqliteUtils.close(conn);
		
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
		Connection conn = SqliteUtils.getConn(ds);
		String sql = StrUtils.concat("DELETE FROM T_HISTORY_VERSIONS ", 
				"WHERE S_VERSION = '", verInfo.getVersion(), "'");
		boolean isOk = SqliteUtils.execute(conn, sql);
		SqliteUtils.close(conn);
		
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
	
	protected String toCurVerInfo() {
		List<List<String>> curVerInfo = new LinkedList<List<String>>();
		curVerInfo.add(Arrays.asList(new String[] { "项目名称", prjVerInfo.getPrjName() }));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "项目描述", prjVerInfo.getPrjDesc()}));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "版本号", prjVerInfo.getCurVer().getVersion() }));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "定版时间", prjVerInfo.getCurVer().getDatetime() }));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "最后责任人", prjVerInfo.getCurVer().getAuthor() }));
		return ESCUtils.toTXT(curVerInfo, false);
	}
	
	
	/**
	 * <PRE>
	 * 历史版本表单组件
	 * </PRE>
	 * 
	 * @author Administrator
	 * @date 2017年7月6日
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
