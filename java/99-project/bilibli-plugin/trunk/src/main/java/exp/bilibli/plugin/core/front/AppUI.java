package exp.bilibli.plugin.core.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.ChatMgr;
import exp.bilibli.plugin.cache.OnlineUserMgr;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.core.back.WebSockClient;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;


/**
 * <PRE>
 * 主应用程序窗口
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AppUI extends MainWindow {

	/** serialVersionUID */
	private final static long serialVersionUID = 2097374309672044616L;

	private final static int WIDTH = 1000;
	
	private final static int HEIGHT = 600;
	
	private final static int CHAT_LIMIT = 20;
	
	private JButton linkBtn;
	
	private JButton lotteryBtn;
	
	private JButton loginBtn;
	
	private JButton sendBtn;
	
	private JButton thxBtn;
	
	private JButton nightBtn;
	
	private JTextField httpTF;
	
	private JTextField ridTF;
	
	private JTextArea chatTA;
	
	private JTextField chatTF;
	
	private JTextArea consoleTA;
	
	private JTextArea notifyTA;
	
	private JTextArea sttcTA;
	
	private int lotteryCnt;
	
	private JLabel lotteryLabel;
	
	private WebSockClient wsClient;
	
	private LotteryUI lotteryUI;
	
	private QrcodeUI qrcodeUI;
	
	private boolean isRunning;
	
	private static volatile AppUI instance;
	
	private AppUI() {
		super("Bilibili插件姬 - By 亚絲娜", WIDTH, HEIGHT);
	}
	
	public static void createInstn(String[] args) {
		if(args != null && args.length > 0) {
			String code = SwingUtils.input("请输入注册码");
			if(!code.matches("[a-zA-Z]\\d[a-zA-Z]\\d")) {
				SwingUtils.warn("未授权用户");
				System.exit(0);
				return;
			}
		}
		
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
	
	@Override
	protected void initComponents(Object... args) {
		this.chatTF = new JTextField();
		this.httpTF = new JTextField("http://live.bilibili.com/");
		this.ridTF = new JTextField("438", 15);
		chatTF.setToolTipText("内容长度限制: 20");
		httpTF.setEditable(false);
		
		this.linkBtn = new JButton("偷窥直播间 (无需登陆)");
		this.lotteryBtn = new JButton("抽奖姬 (发起直播间抽奖)");
		this.loginBtn = new JButton("扫码登陆 (可自动参与全平台抽奖)");
		this.sendBtn = new JButton("发言");
		this.thxBtn = new JButton("答谢姬");
		this.nightBtn = new JButton("晚安姬");
		linkBtn.setForeground(Color.BLACK);
		lotteryBtn.setForeground(Color.BLACK);
		loginBtn.setForeground(Color.BLACK);
		sendBtn.setForeground(Color.BLACK);
		thxBtn.setForeground(Color.BLACK);
		nightBtn.setForeground(Color.BLACK);
		
		this.chatTA = new JTextArea();
		this.consoleTA = new JTextArea(8, 10);
		this.notifyTA = new JTextArea(1, 40);
		this.sttcTA = new JTextArea(7, 40);
		chatTA.setEditable(false);
		consoleTA.setEditable(false);
		notifyTA.setEditable(false);
		sttcTA.setEditable(false);
		
		this.lotteryCnt = 0;
		this.lotteryLabel = new JLabel(" 00000 ");
		lotteryLabel.setForeground(Color.RED);
		
		this.wsClient = new WebSockClient();
		this.lotteryUI = new LotteryUI();
		this.qrcodeUI = new QrcodeUI();
		
		this.isRunning = false;
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getLeftPanel(), BorderLayout.CENTER);
		rootPanel.add(getRightPanel(), BorderLayout.EAST);
	}
	
	
	private JPanel getLeftPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(_getLinkPanel(), BorderLayout.NORTH);
		panel.add(_getLivePanel(), BorderLayout.CENTER);
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
	
	private JPanel _getLivePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, "直播间信息");
		panel.add(SwingUtils.addAutoScroll(chatTA), BorderLayout.CENTER);
		panel.add(_getChatPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel _getChatPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(chatTF, BorderLayout.CENTER);
		panel.add(SwingUtils.getHGridPanel(sendBtn, thxBtn, nightBtn), BorderLayout.EAST);
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
		setLinkBtnListener();
		setLotteryBtnListener();
		setLoginBtnListener();
		setSendBtnListener();
		setThxBtnListener();
		setNightBtnListener();
		setChatTFListener();
	}

	private void setLinkBtnListener() {
		linkBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				isRunning = true;
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
				
				OnlineUserMgr.getInstn().clear(); // 重连直播间时清空在线用户列表
				ThreadUtils.tSleep(200);	// 避免连续点击
			}
		});
	}
	
	private void setLotteryBtnListener() {
		lotteryBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				isRunning = true;
				
				lotteryUI.refreshUsers();
				lotteryUI._view();
				ThreadUtils.tSleep(500);	// 避免连续点击
			}
		});
	}
	
	private void setLoginBtnListener() {
		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isRunning = true;
				qrcodeUI._view();
				
				if(LoginMgr.getInstn().isRun() == false) {
					LoginMgr.getInstn()._start();
					
					UIUtils.log("正在连接登陆服务器, 请稍后...");
					UIUtils.log("正在下载登陆二维码, 请打开 [哔哩哔哩手机客户端] 扫码登陆...");
				}
			}
		});
	}
	
	private void setSendBtnListener() {
		sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogin()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能发言哦~");
					return;
				}
				
				String msg = chatTF.getText();
				if(StrUtils.isNotEmpty(msg)) {
					ChatMgr.getInstn().addMsg(msg);
					chatTF.setText("");
				}
			}
		});
	}
	
	private void setThxBtnListener() {
		thxBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogin()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能答谢哦~");
					return;
				}
				
				ChatMgr.getInstn().setAutoThankYou();
				if(ChatMgr.getInstn().isAutoThankYou()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, thxBtn);
					UIUtils.log("[自动答谢姬] 已开启");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, thxBtn);
					UIUtils.log("[自动答谢姬] 已关闭");
				}
				ThreadUtils.tSleep(100);
			}
		});
	}
	
	private void setNightBtnListener() {
		nightBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogin()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能晚安哦~");
					return;
				}
				
				ChatMgr.getInstn().setAutoGoodNight();
				if(ChatMgr.getInstn().isAutoGoodNight()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, nightBtn);
					UIUtils.log("[自动晚安姬] 已开启");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, nightBtn);
					UIUtils.log("[自动晚安姬] 已关闭");
				}
				ThreadUtils.tSleep(100);
			}
		});
	}
	
	private void setChatTFListener() {
		chatTF.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(chatTF.getText().length() > CHAT_LIMIT) {
					e.consume(); // 销毁新输入的字符，限制长度
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					sendBtn.doClick();// 监听到回车键则触发发送按钮
				}
			}
		});
	}
	
	@Override
	protected void beforeExit() {
		if(isRunning == false) {
			return;	// 避免没有启动过浏览器直接关闭时, 也会把浏览器先打开再关闭
		}
		
		wsClient._stop();
		lotteryUI.clear();
		
		LoginMgr.getInstn()._stop();	
		WebBot.getInstn()._stop();
		Browser.quit();
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
		if(!loginBtn.isEnabled()) {	// 登陆按钮被禁用, 说明登陆成功
			sttcTA.append(msg);
			sttcTA.append("\r\n");
			SwingUtils.toEnd(sttcTA);
		}
	}
	
	public void updateLotteryCnt() {
		if(!loginBtn.isEnabled()) {	// 登陆按钮被禁用, 说明登陆成功
			lotteryCnt++;
			String cnt = StrUtils.leftPad(String.valueOf(lotteryCnt), '0', 5);
			lotteryLabel.setText(StrUtils.concat(" ", cnt, " "));
		}
	}
	
	public void updateQrcode() {
		qrcodeUI.updateImg();
	}
	
	public void updateQrcodeTime(int time) {
		qrcodeUI.updateTime(time);
	}
	
	/**
	 * 标记已登陆成功
	 */
	protected void markLogin() {
		loginBtn.setEnabled(false);
		qrcodeUI._hide();
		WebBot.getInstn()._start();
		
		UIUtils.log("登陆成功 (仅首次登陆需要扫码)");
		UIUtils.log("已激活全平台自动抽奖机能（包括小电视、高能抽奖等）");
		SwingUtils.info("登陆成功 (自动抽奖已激活)");
	}
	
	/**
	 * 确认是否已登录
	 * @return
	 */
	private boolean isLogin() {
		return !loginBtn.isEnabled();
	}
	
	protected String getLiveUrl() {
		return StrUtils.concat(httpTF.getText(), ridTF.getText());
	}
	
}
