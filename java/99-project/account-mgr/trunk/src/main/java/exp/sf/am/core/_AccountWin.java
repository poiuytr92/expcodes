package exp.sf.am.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.NormTable;
import exp.libs.warp.ui.cpt.win.MainWindow;
import exp.sf.am.bean.TAccount;
import exp.sf.am.bean.TUser;

public class _AccountWin extends MainWindow {

	private static final long serialVersionUID = -3227397290475968153L;

	private final static String[] HEADER = {
		"序号", "相关应用", "相关网址", "登陆账号", "登陆密码", "绑定邮箱", "绑定手机", "提示"
	};
	
	private JTabbedPane tabPanel;
	
	private TUser user;
	
	private AccountTable accTable;
	
	private List<TAccount> accDatas;
	
	protected _AccountWin(TUser user) {
		super("账密管理", 1024, 600, false, user);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.user = (TUser) args[0];
		this.accTable = new AccountTable();
		this.accDatas = new LinkedList<TAccount>();
		updateAccountTable();
	}
	
	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(initTabPanel(), BorderLayout.CENTER);
	}
	
	private Component initTabPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			this.tabPanel = new JTabbedPane(); {
				tabPanel.addTab("帐密列表", SwingUtils.addAutoScroll(accTable));
				tabPanel.addTab("搜索帐密", null);
				tabPanel.addTab("添加帐密", null);
			}
			panel.add(tabPanel, BorderLayout.CENTER);
		}
		return SwingUtils.addScroll(panel);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		tabPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Rectangle searchBtn = tabPanel.getBoundsAt(1);
				Rectangle addBtn = tabPanel.getBoundsAt(2);
				
				// 搜索帐密
				if(searchBtn.contains(e.getX(), e.getY())) {
					String keyword = SwingUtils.input("请输入搜索关键字:");
					updateAccountTable(keyword);
					
				// 添加帐密
				} else if(addBtn.contains(e.getX(), e.getY())) {
					// TODO
				}
			}
		});
	}
	
	private void updateAccountTable() {
		updateAccountTable(null);
	}
	
	private void updateAccountTable(String keyword) {
		accDatas.clear();
		accDatas.addAll(DBMgr.queryAccounts(user, keyword));
		accTable.reflash(_toTableDatas(accDatas));
	}

	private List<List<String>> _toTableDatas(List<TAccount> accounts){
		List<List<String>> datas = new LinkedList<List<String>>();
		for(TAccount account : accounts) {
			List<String> row = new LinkedList<String>();
			row.add(String.valueOf(account.getId()));
			row.add(account.getAppName());
			row.add(account.getUrl());
			row.add(account.getLoginUsername());
			row.add(account.getLoginPassword());
			row.add(account.getEmail());
			row.add(account.getPhone());
			row.add("右键详情查看更多");
			datas.add(row);
		}
		return datas;
	}
	
	
	
///////////////////////////////////////////////////////////////////////////////
	
	private class AccountTable extends NormTable {

		/** serialVersionUID */
		private static final long serialVersionUID = -2194275100301409161L;
		
		public AccountTable() {
			super(HEADER, 100);
		}

		@Override
		protected void initRightBtnPopMenu(JPopupMenu popMenu) {
			JMenuItem detail = new JMenuItem("查看详情");
			JMenuItem reflash = new JMenuItem("刷新列表");
			JMenuItem copy = new JMenuItem("复制到剪贴板");
			JMenuItem delete = new JMenuItem("删除");
			popMenu.add(detail);
			popMenu.add(reflash);
			popMenu.add(copy);
			popMenu.add(delete);
			
			detail.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					//TODO
				}
			});
			
			reflash.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					updateAccountTable();
				}
			});
			
			copy.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					//TODO
				}
			});

			delete.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					//TODO
				}
			});
		}
		
	}
}
