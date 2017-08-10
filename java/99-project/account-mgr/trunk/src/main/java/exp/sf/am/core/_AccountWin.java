package exp.sf.am.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.AbstractTable;
import exp.libs.warp.ui.cpt.win.MainWindow;
import exp.sf.am.bean.TUser;
import exp.sf.am.utils.CryptoUtils;

public class _AccountWin extends MainWindow {

	private static final long serialVersionUID = -3227397290475968153L;

	private final static String[] HEADER = {
		"序号", "相关应用", "相关网址", "登陆账号", "登陆密码", 
		"查询密码", "取款密码", "支付密码", "服务密码", "绑定邮箱", 
		"绑定手机", "绑定身份证号", "绑定身份证名", "密码提示问题1", 
		"密码提示答案1", "密码提示问题2", "密码提示答案2", 
		"密码提示问题3", "密码提示答案3", "备注"
	};
	
	public static void main(String[] args) {
		System.out.println(CryptoUtils.encode("384C09CB1A16D378"));
	}
	
	private AccountTable accTable;
	
	protected _AccountWin(TUser user) {
		super("账密管理", 0, 50, true, user);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.accTable = new AccountTable();
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(initTabPanel(), BorderLayout.CENTER);
	}
	
	private Component initTabPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			JTabbedPane tabPanel = new JTabbedPane(); {
				tabPanel.addTab("帐密列表", SwingUtils.addAutoScroll(accTable));
				tabPanel.addTab("查询帐密", null);
				tabPanel.addTab("添加帐密", null);
			}
			panel.add(tabPanel, BorderLayout.CENTER);
		}
		return SwingUtils.addScroll(panel);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

	
	
	private class AccountTable extends AbstractTable {

		public AccountTable() {
			super(HEADER, 100);
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
