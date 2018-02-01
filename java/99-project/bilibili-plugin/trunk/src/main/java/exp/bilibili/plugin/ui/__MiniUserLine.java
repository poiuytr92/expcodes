package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.envm.CookieType;
import exp.bilibili.plugin.ui.login.LoginBtn;
import exp.libs.warp.ui.SwingUtils;

/**
 * <PRE>
 * 小号账号管理窗口的单行组件
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class __MiniUserLine extends JPanel {

	private final static long serialVersionUID = -8472154443768267316L;
	
	private JTextField usernameTF;
	
	private LoginBtn loginBtn;
	
	private JRadioButton autoFeed;
	
	private BiliCookie miniCookie;
	
	public __MiniUserLine() {
		this(BiliCookie.NULL);
	}
	
	public __MiniUserLine(BiliCookie cookie) {
		super(new BorderLayout());
		
		this.miniCookie = cookie;
		init();
	}
	
	private void init() {
		this.autoFeed = new JRadioButton("自动投喂");
		autoFeed.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(miniCookie != BiliCookie.NULL) {
					miniCookie.setAutoFeed(autoFeed.isSelected());
				}
			}
		});
		
		this.usernameTF = new JTextField(miniCookie.NICKNAME());
		usernameTF.setEditable(false);
		
		this.loginBtn = new LoginBtn(CookieType.MINI, "登陆", new Callback());
		if(loginBtn.markLogined(miniCookie)) {
			autoFeed.doClick();
		}
		
		add(SwingUtils.getPairsPanel("小号昵称", usernameTF), BorderLayout.CENTER);
		add(SwingUtils.getEBorderPanel(loginBtn.getButton(), autoFeed), BorderLayout.EAST);
		SwingUtils.addBorder(this);
	}
	
	private class Callback implements __LoginCallback {

		@Override
		public void afterLogin(final BiliCookie cookie) {
			miniCookie = cookie;
			autoFeed.doClick();
			usernameTF.setText(miniCookie.NICKNAME());
		}

		@Override
		public void afterLogout(final BiliCookie cookie) {
			miniCookie.setAutoFeed(false);
			miniCookie = BiliCookie.NULL;
			autoFeed.setSelected(false);
			usernameTF.setText("");
		}
		
	}
	
}
