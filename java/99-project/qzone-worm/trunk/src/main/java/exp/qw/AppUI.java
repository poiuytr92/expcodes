package exp.qw;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import exp.libs.utils.pub.NumUtils;
import exp.libs.utils.pub.StrUtils;
import exp.qw.bean.BrowserDriver;
import exp.qw.core.MoodWordAnalyzer;
import exp.qw.utils.UIUtils;

public class AppUI extends JFrame {
	
	/** 唯一性序列 */
	private static final long serialVersionUID = -8666543884710680704L;

	/** 屏宽 */
	private final int WIN_WIDTH = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	/** 屏高 */
	private final int WIN_HIGH = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	/** 界面宽 */
	private int width = 750;
	
	/** 界面高 */
	private int high = 600;
	
	/** 根面板 */
	private JPanel rootPanel;
	
	/** 基面板 */
	private JPanel basePanel;
	
	/** 用户名输入框 */
	private JTextField usernameTF;
	
	/** 目标QQ号输入框 */
	private JTextField targetQQTF;
	
	/** 要爬取目标QQ空间的 [说说] 页数 */
	private JTextField pageNumTF;
	
	/** 密码输入框 */
	private JPasswordField passwordPF;
	
	/** 爬取按钮 */
	private JButton wormBtn;
	
	/** 日志输出区 */
	private JTextArea logTA;
	
	/** 单例 */
	private static volatile AppUI instance;
	
	private static BrowserDriver browserDriver;
	
	private AppUI() {
		super("QQ空间图文爬取器");
		this.setSize(width, high);
		this.setLocation((WIN_WIDTH / 2 - width / 2), (WIN_HIGH / 2 - high / 2));
		this.rootPanel = new JPanel(new GridLayout(1, 1));
		this.basePanel = new JPanel(new BorderLayout());
		
		this.setContentPane(rootPanel);
		rootPanel.add(basePanel, 0);
		
		initComponentsLayout();
		setComponentsListener();
		
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(0 == JOptionPane.showConfirmDialog(basePanel, 
						"退出QQ空间图文爬取器 ?\r\n\r\n", "警告", 
						JOptionPane.YES_NO_OPTION)) {
					dispose();
					System.exit(0);
				}
			}
		});
	}
	
	public static void createInstn(BrowserDriver browserDriver) {
		AppUI.browserDriver = browserDriver;
		getInstn();
	}

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

	/**
	 * 获取输入配对组件
	 * @param desCmp 描述组件
	 * @param prmCmp 参数输入组件
	 * @return 输入配对组件
	 */
	private JPanel getInputPanel(JComponent desCmp, JComponent prmCmp) {
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(desCmp, BorderLayout.WEST);
		inputPanel.add(prmCmp, BorderLayout.CENTER);
		return inputPanel;
	}
	
	/**
	 * 初始化组件布局
	 */
	private void initComponentsLayout() {
		JPanel inPanel = new JPanel(new BorderLayout());
		{
			inPanel.add(createInputPanel(), BorderLayout.CENTER);
		}
		basePanel.add(inPanel, BorderLayout.NORTH);
		
		JPanel outPanel = new JPanel(new GridLayout(1, 1));
		{
			JPanel logPanel = new JPanel(new BorderLayout());
			logPanel.setBorder(new TitledBorder(" 日志 ")); {
				logPanel.add(createLogPanel(), BorderLayout.CENTER);
			}
			outPanel.add(logPanel, 0);
		}
		basePanel.add(outPanel, BorderLayout.CENTER);
	}

	private JPanel createInputPanel() {
		JPanel inputPanel = new JPanel(new GridLayout(5, 1));
		inputPanel.setBorder(new TitledBorder(" 控制 "));
		
		{
			this.wormBtn = new JButton("开始爬取QQ空间数据");
			inputPanel.add(wormBtn, 0);
		}
		
		{
			JLabel httpLabel = new JLabel("目标QQ号 : ");
			this.targetQQTF = new JTextField("要爬取空间数据的QQ(暂只爬取【说说】)");
			JPanel httpPanel = getInputPanel(httpLabel, targetQQTF);
			inputPanel.add(httpPanel, 1);
		}
		
		{
			JLabel pageNumLabel = new JLabel("说说页数 : ");
			this.pageNumTF = new JTextField("1000");
			JPanel pageNumPanel = getInputPanel(pageNumLabel, pageNumTF);
			inputPanel.add(pageNumPanel, 2);
		}
		
		{
			JLabel unLabel = new JLabel("你的QQ号 : ");
			this.usernameTF = new JTextField("请确保你有查看对方空间权限 (不负责破解)");
			JPanel unPanel = getInputPanel(unLabel, usernameTF);
			inputPanel.add(unPanel, 3);
		}
		
		{
			JLabel pwLabel = new JLabel("你QQ密码 : ");
			this.passwordPF = new JPasswordField("");
			passwordPF.setToolTipText("放心不盗你号");
			JPanel pwPanel = getInputPanel(pwLabel, passwordPF);
			inputPanel.add(pwPanel, 4);
		}
		return inputPanel;
	}
	
	private JScrollPane createLogPanel() {
		this.logTA = new JTextArea("");
		logTA.setEditable(false);
		logTA.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		logTA.setFont(new Font("宋体", Font.PLAIN, 15));
		
		JScrollPane scollPanel = new JScrollPane(logTA, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		return scollPanel;
	}
	
	private void setComponentsListener() {
		setBtnListener();
	}
	
	private void setBtnListener() {
		wormBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = usernameTF.getText().trim();
				String password = String.valueOf(passwordPF.getPassword());
				String targetQQ = targetQQTF.getText().trim();
				
				if(StrUtils.isNotEmpty(username) && 
						StrUtils.isNotEmpty(password) && 
						StrUtils.isNotEmpty(targetQQ)) {
					
					new Thread() {
						public void run() {
							int pageNum = NumUtils.toInt(pageNumTF.getText().trim());
							pageNum = (pageNum <= 0 ? 1000 : pageNum);
							
							UIUtils.log("程序已启动, 正在打开浏览器模拟操作...");
							
							MoodWordAnalyzer.catchOnlineInfo(AppUI.browserDriver, 
									username, password, targetQQ, pageNum);
							MoodWordAnalyzer.downloadDatas();
							MoodWordAnalyzer.copyTogether();
							
							UIUtils.log("所有抓取操作已完成.");
							wormBtn.setEnabled(true);
						};
					}.start();
					
					wormBtn.setEnabled(false);
				}
			}
		});
	}
	
	private void resetLogs() {
		logTA.setText("");
	}
	
	public void appendLog(String log) {
		logTA.append(log);
		logTA.append("\r\n");
		logTA.setCaretPosition(logTA.getText().length());	//自动滚动到最后
	}
	
	public void coverLog(String log) {
		resetLogs();
		logTA.append(log);
		logTA.append("\r\n");
		logTA.setCaretPosition(logTA.getText().length());	//自动滚动到最后
	}
	
}
