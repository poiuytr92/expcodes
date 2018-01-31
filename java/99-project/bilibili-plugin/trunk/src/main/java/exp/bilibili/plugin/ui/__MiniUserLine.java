package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import exp.bilibili.plugin.bean.ldm.HttpCookie;
import exp.bilibili.plugin.envm.LoginType;
import exp.bilibili.plugin.ui.login.LoginBtn;
import exp.libs.warp.ui.SwingUtils;

public class __MiniUserLine extends JPanel {

	private final static long serialVersionUID = -8472154443768267316L;
	
	private JTextField usernameTF;
	
	private LoginBtn loginBtn;
	
	private JRadioButton autoFeed;
	
	public __MiniUserLine() {
		this(HttpCookie.NULL);
	}
	
	public __MiniUserLine(HttpCookie cookie) {
		super(new BorderLayout());
		
		init(cookie);
	}
	
	private void init(HttpCookie cookie) {
		this.usernameTF = new JTextField(cookie.NICKNAME());
		usernameTF.setEditable(false);
		
		this.loginBtn = new LoginBtn(LoginType.MINI, "登陆", new Callback());
		loginBtn.markLogined(cookie);
		this.autoFeed = new JRadioButton("自动投喂");
		
		add(SwingUtils.getPairsPanel("小号昵称", usernameTF), BorderLayout.CENTER);
		add(SwingUtils.getEBorderPanel(loginBtn.getButton(), autoFeed), BorderLayout.EAST);
		SwingUtils.addBorder(this);
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
