package exp.sf.am.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.AbstractTable;
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
		tabPanel.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int idx = ((JTabbedPane) e.getSource()).getSelectedIndex();
				
				// 搜索帐密
				if(idx == 1) {
					SwingUtils.input("请输入搜索关键字:");
					// TODO
					
				// 添加帐密
				} else if(idx == 2) {
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
	
	// TODO 可以再封装一层， 带右键浮动菜单的表单, 右键表单自定义
	private class AccountTable extends AbstractTable {

		/** serialVersionUID */
		private static final long serialVersionUID = -2194275100301409161L;
		
		private int curRow;
		
		private JPopupMenu popMenu;
		
		public AccountTable() {
			super(HEADER, 100);
			this.curRow = -1;
			initPopMenu();
		}

		private void initPopMenu() {
			this.popMenu = new JPopupMenu();
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
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() != MouseEvent.BUTTON3) {	
				return;	// 只处理鼠标右键事件
			}
			
			// 识别当前操作行（选中行优先，若无选中则为鼠标当前所在行）
			curRow = getCurSelectRow();
			curRow = (curRow < 0 ? getCurMouseRow() : curRow);
			if(curRow < 0) {
				return;
			}
			
			// 呈现浮动菜单
			popMenu.show(e.getComponent(), e.getX(), e.getY());
		}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
}
