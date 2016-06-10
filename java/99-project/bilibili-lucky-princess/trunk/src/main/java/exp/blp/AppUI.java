package exp.blp;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import exp.blp.bean.TableComponent;
import exp.blp.bean.UserData;
import exp.blp.core.PageDataAnalyzer;
import exp.libs.utils.os.ThreadUtils;

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
	
	/** 启动按钮 */
	private JButton startBtn;
	
	/** http输入框 */
	private JTextField httpTF;
	
	/** 在线用户表单组件 */
	private TableComponent userTable;
	
	/** 在线用户数据 */
	private Vector<Vector<String>> userDatas;
	
	/** 日志输出区 */
	private JTextArea logTA;
	
	private PageDataAnalyzer pageAnalyzer;
	
	/** 单例 */
	private static volatile AppUI instance;
	
	private AppUI() {
		super("BiliBili抽奖姬");
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
						"退出BiliBili抽奖姬 ?\r\n\r\n", "警告", 
						JOptionPane.YES_NO_OPTION)) {
					if(pageAnalyzer != null) {
						pageAnalyzer._stop();
					}
					dispose();
					System.exit(0);
				}
			}
		});
	}
	
	public static void createInstn() {
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
		
		JPanel outPanel = new JPanel(new GridLayout(2, 1));
		{
			JPanel userPanel = new JPanel(new BorderLayout());
			userPanel.setBorder(new TitledBorder(" 在线用户 ")); {
				userPanel.add(createUserPanel(), BorderLayout.CENTER);
			}
			
			JPanel logPanel = new JPanel(new BorderLayout());
			logPanel.setBorder(new TitledBorder(" 日志 ")); {
				logPanel.add(createLogPanel(), BorderLayout.CENTER);
			}
			
			outPanel.add(userPanel, 0);
			outPanel.add(logPanel, 1);
		}
		basePanel.add(outPanel, BorderLayout.CENTER);
	}

	private JPanel createInputPanel() {
		JPanel inputPanel = new JPanel(new GridLayout(2, 1));
		inputPanel.setBorder(new TitledBorder(" 控制 "));
		
		{
			this.startBtn = new JButton("连接直播间");
			inputPanel.add(startBtn, 0);
		}
		
		{
			JLabel httpLabel = new JLabel("直播间地址 : ");
			this.httpTF = new JTextField("http://live.bilibili.com/51108");
			JPanel httpPanel = getInputPanel(httpLabel, httpTF);
			inputPanel.add(httpPanel, 1);
		}
		return inputPanel;
	}
	
	private JScrollPane createUserPanel() {
		this.userDatas = getUserDatas();
		Vector<String> tableHeader = getUserTableHeader();
		this.userTable = new TableComponent(tableHeader, userDatas);
		JScrollPane scollPanel = new JScrollPane(userTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return scollPanel;
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
	
	private Vector<String> getUserTableHeader() {
		Vector<String> header = new Vector<String>();
		header.add("用户名");
		header.add("互动次数");
		header.add("主动申请抽奖");
		return header;
	}
	
	private Vector<Vector<String>> getUserDatas() {
		Vector<Vector<String>> datas = new Vector<Vector<String>>();
		int size = 0;
		
		if(pageAnalyzer != null) {
			Map<String, UserData> userdatas = pageAnalyzer.getAnalyzer().getUserdatas();
			size = userdatas.size();
			
			Iterator<UserData> userdataIts = userdatas.values().iterator();
			while(userdataIts.hasNext()) {
				UserData userdata = userdataIts.next();
				Vector<String> row = new Vector<String>();
				row.add(userdata.getUsername());
				row.add(String.valueOf(userdata.getAction()));
				row.add(userdata.isJoin() ? "是" : "否");
				datas.add(row);
			}
		}
		
		// 若不足50行, 自动填充到50行
		for(int i = size; i <= 50; i++) {
			Vector<String> row = new Vector<String>();
			row.add("");
			row.add("");
			row.add("");
			datas.add(row);
		}
		return datas;
	}
	
	public void reflashUserDatas() {
		userDatas.clear();
		userDatas.addAll(getUserDatas());
		userTable.repaint();
	}
	
	private void setComponentsListener() {
		setBtnListener();
	}
	
	private void setBtnListener() {
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pageAnalyzer == null || pageAnalyzer.isStop()) {
					String url = httpTF.getText().trim();
					pageAnalyzer = new PageDataAnalyzer(url);
					pageAnalyzer._start();
					
					ThreadUtils.tSleep(1000);	// 避免连击
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
