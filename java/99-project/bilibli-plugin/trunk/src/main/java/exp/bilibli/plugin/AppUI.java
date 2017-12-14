package exp.bilibli.plugin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import exp.bilibli.plugin.core.gift.WebSockMonitor;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.NormTable;
import exp.libs.warp.ui.cpt.win.MainWindow;


public class AppUI extends MainWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = 2097374309672044616L;

	private final static String[] HEADER = {
		"ID", "称号", "勋章", "等级", "昵称", "行为"
	};
	
	private JButton linkBtn;
	
	private JButton lotteryBtn;
	
	private JButton loginBtn;
	
	private JTextField httpTF;
	
	private _UserTable userTable;
	
	private JTextArea consoleTA;
	
	private JTextArea notifyTA;
	
	private JTextArea sttcTA;
	
	private int lotteryCnt;
	
	private static volatile AppUI instance;
	
	private AppUI() {
		super("Bilibili插件姬 - By 亚絲娜", 1000, 600);
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
	
	@Override
	protected void initComponents(Object... args) {
		this.linkBtn = new JButton("连接直播间");
		this.lotteryBtn = new JButton("发起抽奖");
		this.loginBtn = new JButton("扫码登陆");
		
		this.consoleTA = new JTextArea(6, 10);
		this.notifyTA = new JTextArea(1, 40);
		this.sttcTA = new JTextArea(5, 40);
		
		consoleTA.setEditable(false);
		notifyTA.setEditable(false);
		sttcTA.setEditable(false);
		
		this.lotteryCnt = 0;
		this.httpTF = new JTextField("http://live.bilibili.com/438");
		this.userTable = new _UserTable();
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getLeftPanel(), BorderLayout.CENTER);
		rootPanel.add(getRightPanel(), BorderLayout.EAST);
	}
	
	
	private JPanel getLeftPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(_getLinkPanel(), BorderLayout.NORTH);
		panel.add(_getUserPanel(), BorderLayout.CENTER);
		panel.add(_getConsolePanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel _getLinkPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1));
		SwingUtils.addBorder(panel);
		panel.add(SwingUtils.getHGridPanel(linkBtn, lotteryBtn), 0);
		panel.add(SwingUtils.getPairsPanel("直播间地址", httpTF), 1);
		return panel;
	}
	
	private JPanel _getUserPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, "在线用户");
		panel.add(SwingUtils.addAutoScroll(userTable), BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel _getConsolePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, "运行日志");
		panel.add(SwingUtils.addAutoScroll(consoleTA), BorderLayout.CENTER);
		return panel;
	}
	
	
	
	private JPanel getRightPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(_getLoginPanel(), BorderLayout.NORTH);
		panel.add(_getNotifyPanel(), BorderLayout.CENTER);
		panel.add(_getStatisticsPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel _getLoginPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel);
		panel.add(loginBtn, BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel _getNotifyPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, " 礼物公告 ");
		panel.add(SwingUtils.addAutoScroll(notifyTA), BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel _getStatisticsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, " 抽奖统计 ");
		panel.add(SwingUtils.addAutoScroll(sttcTA), BorderLayout.CENTER);
		panel.add(SwingUtils.getPairsPanel("自动参与抽奖次数", 
				new JLabel(" " + lotteryCnt + " ")), BorderLayout.SOUTH);
		return panel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		linkBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				WebSockMonitor.getInstn()._start();
			}
		});
	}
	
	@Override
	protected void beforeExit() {
		WebSockMonitor.getInstn()._stop();
	}
	
	
	public void addLotteryCnt() {
		lotteryCnt++;
	}
	
	public void toConsole(String msg) {
		consoleTA.append(msg);
		consoleTA.append("\r\n");
		SwingUtils.toEnd(consoleTA);
	}
	
	public void toNotify(String msg) {
		notifyTA.append(msg);
		notifyTA.append("\r\n");
		SwingUtils.toEnd(notifyTA);
	}
	
	public void toStatistics(String msg) {
		sttcTA.append(msg);
		sttcTA.append("\r\n");
		SwingUtils.toEnd(sttcTA);
	}
	
	
	
	
	private class _UserTable extends NormTable {

		/** serialVersionUID */
		private static final long serialVersionUID = -3457133356274208383L;

		private _UserTable() {
			super(HEADER, 100);
		}
		
		@Override
		protected void initRightBtnPopMenu(JPopupMenu popMenu) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
}
