package exp.qw.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;
import exp.qw.Config;
import exp.qw.cache.Browser;
import exp.qw.core.AlbumAnalyzer;
import exp.qw.core.Landers;

public class AppUI extends MainWindow {

	private static final long serialVersionUID = -7825507638221203671L;

	private final static int WIDTH = 750;
	
	private final static int HEIGHT = 600;
	
	/** 界面文本框最大缓存行数 */
	private final static int MAX_LINE = 200;
	
	/** 换行符 */
	private final static String LINE_END = "\r\n";
	
	/** QQ号输入框 */
	private JTextField unTF;
	
	/** 密码输入框 */
	private JPasswordField pwTF;
	
	/** 目标QQ号输入框 */
	private JTextField targetQQTF;
	
	/** 【记住登陆信息】选项 */
	private JRadioButton rememberBtn;
	
	/** 登陆按钮 */
	private JButton loginBtn;
	
	/** 是否登陆成功 */
	private boolean isLogin;
	
	/** 【相册】爬取按钮 */
	private JButton albumBtn;
	
	/** 【说说】爬取按钮 */
	private JButton moodBtn;
	
	/** 日志输出区 */
	private JTextArea consoleTA;
	
	/** 单例 */
	private static volatile AppUI instance;
	
	private AppUI() {
		super("QQ空间爬虫 - By EXP", WIDTH, HEIGHT);
	}
	
	/**
	 * 创建实例
	 * @param args main入参
	 */
	public static void createInstn(String[] args) {
		getInstn();
	}
	
	/**
	 * 获取单例
	 * @return
	 */
	public static AppUI getInstn() {
		if(instance == null) {
			synchronized (AppUI.class) {
				if(instance == null) {
					instance = new AppUI();
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.unTF = new JTextField("");
		this.pwTF = new JPasswordField("");
		this.targetQQTF = new JTextField("");
		
		unTF.setToolTipText("请确保此QQ具有查看对方空间权限 (不负责权限破解)");
		targetQQTF.setToolTipText("此软件不盗号, 不放心勿用");
		
		
		this.loginBtn = new JButton("登陆 QQ 空间");
		this.albumBtn = new JButton("爬取【相册】图文");
		this.moodBtn = new JButton("爬取【说说】图文");
		
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, loginBtn);
		BeautyEyeUtils.setButtonStyle(NormalColor.green, albumBtn);
		BeautyEyeUtils.setButtonStyle(NormalColor.green, moodBtn);
		loginBtn.setForeground(Color.BLACK);
		albumBtn.setForeground(Color.BLACK);
		moodBtn.setForeground(Color.BLACK);
		
		
		this.isLogin = false;
		this.consoleTA = new JTextArea();
		consoleTA.setEditable(false);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getCtrlPanel(), BorderLayout.NORTH);
		rootPanel.add(getConsolePanel(), BorderLayout.CENTER);
	}
	
	private JPanel getCtrlPanel() {
		JPanel panel = SwingUtils.getVGridPanel(
				SwingUtils.getPairsPanel("QQ账号", unTF), 
				SwingUtils.getPairsPanel("QQ密码", pwTF), 
				SwingUtils.getPairsPanel("目标QQ", targetQQTF), 
				loginBtn, 
				SwingUtils.getHGridPanel(albumBtn, moodBtn)
		);
		SwingUtils.addBorder(panel, "control");
		return panel;
	}
	
	private JScrollPane getConsolePanel() {
		JScrollPane scollPanel = SwingUtils.addAutoScroll(consoleTA);
		SwingUtils.addBorder(scollPanel, "console");
		return scollPanel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		setQQTFListener(unTF);
		setQQTFListener(targetQQTF);
		setLoginBtnListener();
		setAlbumBtnListener();
		setMoodBtnListener();
	}
	
	public void setQQTFListener(final JTextField textField) {
		textField.addKeyListener(new KeyListener() {

		    @Override
		    public void keyTyped(KeyEvent e) {
		        String text = textField.getText();  // 当前输入框内容
		        char ch = e.getKeyChar();   // 准备附加到输入框的字符

		        // 限制不能输入非数字
		        if(!(ch >= '0' && ch <= '9')) {
		            e.consume();    // 销毁当前输入字符

		        // 限制不能是0开头
		        } else if("".equals(text) && ch == '0') {   
		            e.consume();
		        }
		    }

		    @Override
		    public void keyReleased(KeyEvent e) {
		        // TODO Auto-generated method stub
		    }

		    @Override
		    public void keyPressed(KeyEvent e) {
		        // TODO Auto-generated method stub
		    }
		});
	}
	
	public void setLoginBtnListener() {
		loginBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = unTF.getText();
				String password = String.valueOf(pwTF.getPassword());
				
				if(StrUtils.isNotEmpty(username, password)) {
					isLogin = Landers.toLogin(username, password, getQZoneURL());
					if(isLogin == true) {
						loginBtn.setEnabled(false);
						SwingUtils.warn("登陆成功");
						
					} else {
						Browser.quit();
						SwingUtils.warn("登陆失败: 账号或密码错误");
					}
				} else {
					SwingUtils.warn("账号或密码不能为空");
				}
			}
		});
	}
	
	public void setAlbumBtnListener() {
		
	}

	public void setMoodBtnListener() {
		moodBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				AlbumAnalyzer.downloadAlbums(getQZoneURL());
				
//				if(StrUtils.isNotEmpty(username) && 
//						StrUtils.isNotEmpty(password) && 
//						StrUtils.isNotEmpty(targetQQ)) {
					
//					new Thread() {
//						public void run() {
//							int pageNum = NumUtils.toInt(pageNumTF.getText().trim());
//							pageNum = (pageNum <= 0 ? 1000 : pageNum);
//							
//							UIUtils.log("程序已启动, 正在打开浏览器模拟操作...");
//							
//							MoodWordAnalyzer.catchOnlineInfo(_AppUI.browserDriver, 
//									username, password, targetQQ, pageNum);
//							MoodWordAnalyzer.downloadDatas();
//							MoodWordAnalyzer.copyTogether();
//							
//							UIUtils.log("所有抓取操作已完成.");
//							moodBtn.setEnabled(true);
//						};
//					}.start();
//					
//					moodBtn.setEnabled(false);
//				}
			}
		});
	}
	
	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeExit() {
		Browser.quit();
	}
	
	private String getQZoneURL() {
		String targetQQ = targetQQTF.getText().trim();
		return Config.QZONE_HOME_URL_PREFIX.concat(targetQQ);
	}
	
	public void toConsole(String msg) {
		if(StrUtils.count(consoleTA.getText(), '\n') >= MAX_LINE) {
			consoleTA.setText("");
		}
		
		consoleTA.append(msg.concat(LINE_END));
		SwingUtils.toEnd(consoleTA);
	}

}
