package exp.bilibili.plugin.ui.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.envm.CookieType;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 帐密登陆窗口.
 *  可用于登陆主号、小号、马甲号
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class VCLoginUI extends PopChildWindow {
	
	/** serialVersionUID */
	private static final long serialVersionUID = -1752327112586227761L;

	private final static String IMG_DIR = Config.getInstn().IMG_DIR();
	
	private final static String TIPS_PATH = IMG_DIR.concat("/vcTips.jpg");
	
	private final static String VCIMG_PATH = IMG_DIR.concat("/vccode.jpg");
	
	private final static int IMG_WIDTH = 130;
	
	private final static int IMG_HEIGH = 35;
	
	private final static int WIDTH = 400;
	
	private final static int HEIGH = 300;
	
	private JTextField usernameTXT;
	
	private JPasswordField passwordTXT;
	
	private JButton viewBtn;
	
	private JTextField vccodeTXT;
	
	private JLabel imgLabel;
	
	private JButton reflashBtn;
	
	private JButton loginBtn;
	
	/** 与验证码配套的登陆用cookie */
	private String vcCookies;
	
	private CookieType type;
	
	private BiliCookie cookie;
	
	public VCLoginUI(CookieType type) {
		super("哔哩哔哩-帐密登陆", WIDTH, HEIGH, false, type);
	}
	
	@Override
	protected void initComponents(Object... args) {
		if(args != null && args.length > 0) {
			this.type = (CookieType) args[0];
		} else {
			this.type = CookieType.UNKNOW;
		}
		
		this.usernameTXT = new JTextField();
		this.passwordTXT = new JPasswordField();
		SwingUtils.hide(passwordTXT);
		this.vccodeTXT = new JTextField();
		
		this.viewBtn = new JButton(
				SwingUtils.loadImage("/exp/bilibili/plugin/ui/login/eye.png"));
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, viewBtn);
		
		this.reflashBtn = new JButton(
				SwingUtils.loadImage("/exp/bilibili/plugin/ui/login/reflash.png"));
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, reflashBtn);
		
		this.imgLabel = new JLabel();
		SwingUtils.setImage(imgLabel, TIPS_PATH, IMG_WIDTH, IMG_HEIGH);
		
		this.loginBtn = new JButton("登陆 哔哩哔哩");
		BeautyEyeUtils.setButtonStyle(NormalColor.green, loginBtn);
		loginBtn.setForeground(Color.BLACK);
		
		this.vcCookies = "";
		this.cookie = BiliCookie.NULL;
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(toAccountPanel(), BorderLayout.CENTER);
		rootPanel.add(loginBtn, BorderLayout.SOUTH);
	}

	/**
	 * 获取帐密面板
	 * @return
	 */
	private JPanel toAccountPanel() {
		JPanel panel = new JPanel(new GridLayout(7, 1)); {
			panel.add(new JLabel(), 0);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [账号] :  "), usernameTXT, 
					new JLabel("   ")), 1);
			panel.add(new JLabel(), 2);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [密码] :  "), passwordTXT, 
					SwingUtils.getEBorderPanel(viewBtn, new JLabel("   "))), 3);
			panel.add(new JLabel(), 4);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [验证] :  "), 
					SwingUtils.getEBorderPanel(vccodeTXT, imgLabel), 
					SwingUtils.getEBorderPanel(reflashBtn, new JLabel("   "))), 5);
			panel.add(new JLabel(), 6);
		} SwingUtils.addBorder(panel);
		return panel;
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		
		// 设置密码可视按钮监听
		viewBtn.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				SwingUtils.hide(passwordTXT);	// 鼠标释放时隐藏明�?
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				SwingUtils.view(passwordTXT);	// 鼠标按下时显示明�?
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
			
		});
				
		// 设置二维码刷新按钮监�?
		reflashBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateVccodeImg();
				ThreadUtils.tSleep(200);
			}
		});
				
		// 设置登陆按钮监听
		loginBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loginBtn.setEnabled(false);
				
				String username = usernameTXT.getText();
				String password = new String(passwordTXT.getPassword());
				String vccode = vccodeTXT.getText();
				
				if(StrUtils.isEmpty(username)) {
					SwingUtils.warn("账号不能为空");
					
				} else if(StrUtils.isEmpty(password)) {
					SwingUtils.warn("密码不能为空");
					
				} else if(StrUtils.isEmpty(vccode)) {
					SwingUtils.warn("验证码不能为�?");
					
				} else {
					toLogin(username, password, vccode);
				}
				
				loginBtn.setEnabled(true);
			}
		});
	}
	
	/**
	 * 更新验证码图�?, 同时获取配套的验证码cookie
	 */
	private void updateVccodeImg() {
		this.vcCookies = VCLogin.downloadVccode(VCIMG_PATH);
		SwingUtils.setImage(imgLabel, VCIMG_PATH, IMG_WIDTH, IMG_HEIGH);
	}
	
	/**
	 * 登陆
	 * @param username 账号
	 * @param password 密码
	 * @param vccode 验证�?
	 */
	private void toLogin(String username, String password, String vccode) {
		cookie = VCLogin.toLogin(username, password, vccode, vcCookies);
		if(!cookie.isVaild()) {
			SwingUtils.warn("登陆失败: 账号/密码/验证码错�?");
			reflashBtn.doClick();
			
		} else {
			CookiesMgr.getInstn().add(cookie, type);
			SwingUtils.info("登陆成功: ".concat(cookie.NICKNAME()));
			_hide();
		}
	}
	
	@Override
	protected void AfterView() {
		cookie = BiliCookie.NULL;
		vccodeTXT.setText("");
		reflashBtn.doClick();
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
	protected BiliCookie getCookie() {
		return cookie;
	}
	
}
