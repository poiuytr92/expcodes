package exp.bilibili.plugin.core.front.login.win;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.envm.LoginType;
import exp.libs.warp.ui.SwingUtils;

public class UserLine extends JPanel {

	private final static long serialVersionUID = -8472154443768267316L;
	
	private JTextField usernameTF;
	
	private LoginBtn loginBtn;
	
	public UserLine() {
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
	
	private class Callback implements LoginCallback {

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
