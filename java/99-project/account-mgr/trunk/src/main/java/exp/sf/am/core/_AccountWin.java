package exp.sf.am.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
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
					// TODO
					
				// 添加帐密
				} else if(idx == 2) {
					// TODO
				}
			}
		});
	}
	
	private void updateAccountTable() {
		accDatas.clear();
		accDatas.addAll(DBMgr.queryAccounts(user));
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
	
	private class AccountTable extends AbstractTable {

		public AccountTable() {
			super(HEADER, 100);
			
			/**
			 * AUTO_RESIZE_ALL_COLUMNS : 自动取相同列宽，内容比表头长则内容省略, 若水平方向空间不足，则表头呈现省略号， 不会出现水平滚动条
			 * AUTO_RESIZE_OFF: 列宽根据实际表头长度而定，内容比表头长则内容省略， 若水平方向空间不足，依然完全呈现表头文字， 会出现水平滚条
			 */
			setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		}

		/** serialVersionUID */
		private static final long serialVersionUID = -2194275100301409161L;

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
