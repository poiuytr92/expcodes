package exp.bilibili.plugin.core.front.login;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;

import exp.bilibili.plugin.core.front.__LoginCallback;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.envm.LoginType;
import exp.libs.warp.ui.SwingUtils;

public class LoginBtn {

	private final static String LOGOUT_TIPS = "注销";
	
	private String loginTips;
	
	private LoginType type;
	
	private JButton btn;
	
	private __LoginCallback callback;
	
	private HttpCookie cookie;
	
	private QRLoginUI qrLoginUI;
	
	private VCLoginUI vcLoginUI;
	
	public LoginBtn(LoginType type) {
		this(type, "", null);
	}
	
	public LoginBtn(LoginType type, String btnName) {
		this(type, btnName, null);
	}
	
	public LoginBtn(LoginType type, String btnName, __LoginCallback callback) {
		this.type = type;
		this.callback = callback;
		this.cookie = HttpCookie.NULL;
		
		this.loginTips = btnName;
		this.btn = new JButton(btnName);
		btn.setForeground(Color.BLACK);
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(LOGOUT_TIPS.equals(btn.getText())) {
					logout();
					
				} else {
					login();
				}
			}
		});
		
		initVCLoginUI();
	}
	
	private void initQRLoginUI() {
		this.qrLoginUI = new QRLoginUI(type);
		qrLoginUI.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				cookie = qrLoginUI.getCookie();
				if(cookie.isVaild()) {
					btn.setText(LOGOUT_TIPS);
					if(callback != null) {
						callback.afterLogin(cookie);
					}
				}
			}
		});
	}
	
	private void initVCLoginUI() {
		this.vcLoginUI = new VCLoginUI(type);
		vcLoginUI.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				cookie = vcLoginUI.getCookie();
				if(cookie.isVaild()) {
					btn.setText(LOGOUT_TIPS);
					if(callback != null) {
						callback.afterLogin(cookie);
					}
				}
			}
		});
	}
	
	private void login() {
		if(SwingUtils.confirm("请选择登陆方式 : ", "扫码登陆 (1天)", "帐密登陆 (30天)")) {
			initQRLoginUI();	// QR登陆时的检测线程不能重复启动，只能每次新建对象
			qrLoginUI._view();
			
		} else {
			vcLoginUI._view();
		}
	}
	
	private void logout() {
		if(SwingUtils.confirm("注销登陆 ?") && CookiesMgr.INSTN().del(cookie)) {
			btn.setText(loginTips);
			if(callback != null) {
				callback.afterLogout(cookie);
			}
		}
	}
	
	public HttpCookie getCookie() {
		return cookie;
	}
	
	public JButton getButton() {
		return btn;
	}
	
	public void doClick() {
		btn.doClick();
	}
	
	public void markLogined(HttpCookie cookie) {
		if(cookie.isVaild()) {
			this.cookie = cookie;
			btn.setText(LOGOUT_TIPS);
		}
	}
	
}
