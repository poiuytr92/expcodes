package exp.bilibili.plugin.core.front;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import exp.bilibili.plugin.core.front.login.LoginBtn;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.envm.LoginType;
import exp.libs.warp.ui.SwingUtils;

class __MiniUserLine extends JPanel {

	private final static long serialVersionUID = -8472154443768267316L;
	
	private JTextField usernameTF;
	
	private LoginBtn loginBtn;
	
	protected __MiniUserLine() {
		super(new BorderLayout());
		
		initLayout();
	}
	
	private void initLayout() {
		SwingUtils.addBorder(this);
		
		this.usernameTF = new JTextField();
		usernameTF.setEditable(false);
		this.loginBtn = new LoginBtn(LoginType.MINI, "登陆", new Callback());
		
		add(SwingUtils.getPairsPanel("小号昵称", usernameTF), BorderLayout.CENTER);
		add(loginBtn.getButton(), BorderLayout.EAST);
	}
	
	private class Callback implements __LoginCallback {

		@Override
		public void afterLogin(HttpCookie cookie) {
			usernameTF.setText(cookie.NICKNAME());
		}

		@Override
		public void afterLogout(HttpCookie cookie) {
			usernameTF.setText("");
		}
		
	}
	
}
