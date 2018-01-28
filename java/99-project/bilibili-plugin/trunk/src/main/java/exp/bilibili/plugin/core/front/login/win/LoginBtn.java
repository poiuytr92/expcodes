package exp.bilibili.plugin.core.front.login.win;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;

import exp.bilibili.plugin.core.front.login.QRLoginUI;
import exp.bilibili.plugin.core.front.login.VCLoginUI;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.envm.LoginType;
import exp.libs.warp.ui.SwingUtils;

public class LoginBtn {

	private final static String LOGOUT_TIPS = "注销";
	
	private String loginTips;
	
	private LoginType type;
	
	private JButton btn;
	
	private LoginCallback callback;
	
	private HttpCookie cookie;
	
	private QRLoginUI qrLoginUI;
	
	private VCLoginUI vcLoginUI;
	
	public LoginBtn(LoginType type) {
		this(type, "", null);
	}
	
	public LoginBtn(LoginType type, String btnName) {
		this(type, btnName, null);
	}
	
	public LoginBtn(LoginType type, String btnName, LoginCallback callback) {
		this.type = type;
		
		this.loginTips = btnName;
		this.btn = new JButton(btnName);
		btn.setForeground(Color.BLACK);
		
		this.callback = callback;
		this.cookie = HttpCookie.NULL;
		this.qrLoginUI = new QRLoginUI(type);
		this.vcLoginUI = new VCLoginUI(type);
		
		initListener();
	}
	
	private void initListener() {
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
	
	private boolean login() {
		if(SwingUtils.confirm("请选择登陆方式 : ", "扫码登陆 (1天)", "帐密登陆 (30天)")) {
			_loginByQrcode();
			
		} else {
			_loginByVccode();
		}
		
		if(cookie.isVaild() == true) {
			CookiesMgr.INSTN().add(cookie, type);
		}
		return cookie.isVaild();
	}
	
	private boolean logout() {
		boolean isOk = CookiesMgr.INSTN().del(cookie);
		if(isOk == true) {
			btn.setText(loginTips);
			if(callback != null) {
				callback.afterLogout(cookie);
			}
		}
		return isOk;
	}
	
	private void _loginByQrcode() {
		qrLoginUI = new QRLoginUI(type);
		qrLoginUI._view();
	}
	
	private void _loginByVccode() {
		vcLoginUI._view();
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
	
}
