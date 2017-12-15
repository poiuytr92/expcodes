package exp.bilibli.plugin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import exp.bilibli.plugin.core.gift.RoomMgr;
import exp.bilibli.plugin.core.gift.WebSockClient;
import exp.bilibli.plugin.core.site.LoginMgr;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;


public class AppUI extends MainWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = 2097374309672044616L;

	private static final int WIDTH = 1000;
	
	private static final int HEIGHT = 600;
	
	private JButton linkBtn;
	
	private JButton lotteryBtn;
	
	private JButton loginBtn;
	
	private JTextField httpTF;
	
	private JTextField ridTF;
	
	private JTextArea chatTA;
	
	private JTextArea consoleTA;
	
	private JTextArea notifyTA;
	
	private JTextArea sttcTA;
	
	private int lotteryCnt;
	
	private JLabel lotteryLabel;
	
	private WebSockClient wsClient;
	
	private static volatile AppUI instance;
	
	private AppUI() {
		super("Bilibili插件姬 - By 亚絲娜", WIDTH, HEIGHT);
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
		this.httpTF = new JTextField("http://live.bilibili.com/");
		this.ridTF = new JTextField("438", 15);
		
		// FIXME 暂时不允许修改，似乎每个直播间的请求信息都不一样
		httpTF.setEditable(false);
		ridTF.setEditable(false);
		
		this.linkBtn = new JButton("连接直播间");
		this.lotteryBtn = new JButton("发起抽奖");
		this.loginBtn = new JButton("扫码登陆");
		
		this.chatTA = new JTextArea();
		this.consoleTA = new JTextArea(6, 10);
		this.notifyTA = new JTextArea(1, 40);
		this.sttcTA = new JTextArea(5, 40);
		
		chatTA.setEditable(false);
		consoleTA.setEditable(false);
		notifyTA.setEditable(false);
		sttcTA.setEditable(false);
		
		this.lotteryCnt = 0;
		this.lotteryLabel = new JLabel(" 00000 ");
		
		this.wsClient = new WebSockClient();
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
		
		JPanel livePanel = new JPanel(new BorderLayout()); {
			livePanel.add(SwingUtils.getPairsPanel("直播间地址", httpTF), BorderLayout.CENTER);
			livePanel.add(SwingUtils.getPairsPanel("房间号", ridTF), BorderLayout.EAST);
		}
		panel.add(livePanel, 1);
		return panel;
	}
	
	private JPanel _getUserPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, "直播间信息");
		panel.add(SwingUtils.addAutoScroll(chatTA), BorderLayout.CENTER);
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
		SwingUtils.addBorder(panel, " 系统公告 ");
		panel.add(SwingUtils.addAutoScroll(notifyTA), BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel _getStatisticsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, " 抽奖统计 ");
		panel.add(SwingUtils.addAutoScroll(sttcTA), BorderLayout.CENTER);
		
		JPanel sumPanel = new JPanel(new BorderLayout()); {
			sumPanel.add(SwingUtils.getPairsPanel("自动参与抽奖次数累计", lotteryLabel), 
					BorderLayout.EAST);
		}
		panel.add(sumPanel, BorderLayout.SOUTH);
		return panel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		linkBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int roomId = NumUtils.toInt(ridTF.getText().trim(), 0);
				roomId = RoomMgr.getInstn().getRealRoomId(roomId);
				if(roomId <= 0) {
					SwingUtils.warn("直播间地址无效");
					return;
				}
				
				if(!wsClient.isRun()) {
					wsClient.reset(roomId);
					wsClient._start();
					
				} else {
					wsClient.relink(roomId);
				}
				
				ThreadUtils.tSleep(500);
			}
		});
		
		
		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LoginMgr.getInstn().login();
			}
			
		});
	}
	
	@Override
	protected void beforeExit() {
		wsClient._stop();
		
		// FIXME: planjs浏览器进程不会自动退出
	}
	
	public void toChat(String msg) {
		chatTA.append(msg);
		chatTA.append("\r\n");
		SwingUtils.toEnd(chatTA);
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
	
	public void updateLotteryCnt() {
		lotteryCnt++;
		String cnt = StrUtils.leftPad(String.valueOf(lotteryCnt), '0', 5);
		lotteryLabel.setText(StrUtils.concat(" ", cnt, " "));
	}
	
}
