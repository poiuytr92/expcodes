package exp.crawler.qq.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.core.AlbumAnalyzer;
import exp.crawler.qq.core.Landers;
import exp.crawler.qq.core.MoodAnalyzer;
import exp.crawler.qq.envm.URL;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.ThreadPool;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

public class AppUI extends MainWindow {

	private static final long serialVersionUID = -7825507638221203671L;

	private final static int WIDTH = 750;
	
	private final static int HEIGHT = 600;
	
	/** 界面文本框最大缓存行数 */
	private final static int MAX_LINE = 200;
	
	/** 换行符 */
	private final static String LINE_END = "\r\n";
	
	/** 登陆信息保存路径 */
	private final static String LOGIN_INFO_PATH = "./conf/info.dat";
	
	/** 目标QQ号输入框 */
	private JTextField targetQQTF;
	
	/** QQ号输入框 */
	private JTextField unTF;
	
	/** 密码输入框 */
	private JPasswordField pwTF;
	
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
	
	/** 线程池 */
	private ThreadPool tp;
	
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
		this.targetQQTF = new JTextField("");
		this.unTF = new JTextField("");
		this.pwTF = new JPasswordField("");
		this.rememberBtn = new JRadioButton("记住我");
		
		targetQQTF.setToolTipText("需要爬取数据的目标QQ号");
		unTF.setToolTipText("请确保此QQ具有查看对方空间权限 (不负责权限破解)");
		pwTF.setToolTipText("此软件不盗号, 不放心勿用");
		recoveryLoginInfo();
		
		this.loginBtn = new JButton("登陆 QQ 空间");
		this.albumBtn = new JButton("爬取【相册】图文数据");
		this.moodBtn = new JButton("爬取【说说】图文数据");
		
		albumBtn.setEnabled(false);
		moodBtn.setEnabled(false);
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, loginBtn);
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, albumBtn);
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, moodBtn);
		loginBtn.setForeground(Color.BLACK);
		albumBtn.setForeground(Color.BLACK);
		moodBtn.setForeground(Color.BLACK);
		
		this.consoleTA = new JTextArea();
		consoleTA.setEditable(false);
		
		this.isLogin = false;
		this.tp = new ThreadPool(10);
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
				SwingUtils.getEBorderPanel(loginBtn, rememberBtn), 
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
		setNumTextFieldListener(unTF);
		setNumTextFieldListener(targetQQTF);
		setLoginBtnListener();
		setAlbumBtnListener();
		setMoodBtnListener();
	}
	
	private void setNumTextFieldListener(final JTextField textField) {
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
	
	private void setLoginBtnListener() {
		loginBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final String username = unTF.getText().trim();
				final String password = String.valueOf(pwTF.getPassword());
				
				if(StrUtils.isNotEmpty(username, password)) {
					loginBtn.setEnabled(false);
					tp.execute(new Thread() {
						
						@Override
						public void run() {
							isLogin = Landers.toLogin(username, password, getQZoneURL());
							if(isLogin == true) {
								albumBtn.setEnabled(true);
								moodBtn.setEnabled(true);
								unTF.setEditable(false);
								pwTF.setEditable(false);
								
								if(rememberBtn.isSelected()) {
									backupLoginInfo();
								}
							} else {
								Browser.quit();
								loginBtn.setEnabled(true);
							}
						}
					});
				} else {
					SwingUtils.warn("账号或密码不能为空");
				}
			}
		});
	}
	
	private void setAlbumBtnListener() {
		albumBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(moodBtn.isEnabled() == false) {
					SwingUtils.warn("请先等待【说说】下载完成...");
					
				} else {
					albumBtn.setEnabled(false);
					tp.execute(new Thread() {
						
						@Override
						public void run() {
							AlbumAnalyzer.downloadAlbums(getQZoneURL());
							albumBtn.setEnabled(true);
						}
					});
				}
			}
		});
	}

	private void setMoodBtnListener() {
		moodBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(albumBtn.isEnabled() == false) {
					SwingUtils.warn("请先等待【相册】下载完成...");
					
				} else {
					moodBtn.setEnabled(false);
					tp.execute(new Thread() {
						
						@Override
						public void run() {
							MoodAnalyzer.downloadMoods(getQZoneURL());
							moodBtn.setEnabled(true);
						}
					});
				}
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
		return URL.QZONE_DOMAIN.concat(targetQQ);
	}
	
	public void toConsole(String msg) {
		if(StrUtils.count(consoleTA.getText(), '\n') >= MAX_LINE) {
			consoleTA.setText("");
		}
		
		consoleTA.append(msg.concat(LINE_END));
		SwingUtils.toEnd(consoleTA);
	}
	
	/**
	 * 备份登陆信息
	 */
	private void backupLoginInfo() {
		String username = unTF.getText().trim();
		String password = String.valueOf(pwTF.getPassword());
		String targetQQ = targetQQTF.getText().trim();
		
		String loginInfo = StrUtils.concat(
				CryptoUtils.toDES(username), LINE_END, 
				CryptoUtils.toDES(password), LINE_END, 
				CryptoUtils.toDES(targetQQ)
		);
		FileUtils.write(LOGIN_INFO_PATH, loginInfo, Charset.ISO, false);
	}
	
	/**
	 * 还原登陆信息
	 */
	private void recoveryLoginInfo() {
		List<String> lines = FileUtils.readLines(LOGIN_INFO_PATH, Charset.ISO);
		if(lines.size() == 3) {
			unTF.setText(CryptoUtils.deDES(lines.get(0).trim()));
			pwTF.setText(CryptoUtils.deDES(lines.get(1).trim()));
			targetQQTF.setText(CryptoUtils.deDES(lines.get(2).trim()));
		}
	}

}
