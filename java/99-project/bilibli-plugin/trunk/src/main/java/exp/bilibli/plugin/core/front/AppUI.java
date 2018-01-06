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

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.bilibli.plugin.cache.ChatMgr;
import exp.bilibli.plugin.cache.MsgKwMgr;
import exp.bilibli.plugin.cache.OnlineUserMgr;
import exp.bilibli.plugin.cache.RoomMgr;
import exp.bilibli.plugin.core.back.MsgSender;
import exp.bilibli.plugin.core.back.WebSockClient;
import exp.bilibli.plugin.envm.ChatColor;
import exp.bilibli.plugin.utils.SafetyUtils;
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

	/** 界面文本框最大缓存行数 */
	private final static int MAX_LINE = 200;
	
	private final static String LINE_END = "\r\n";
	
	private final static int WIDTH = 1024;
	
	private final static int HEIGHT = 600;
	
	private final static int CHAT_LIMIT = 20;
	
	/** 是否为管理员使用的版本 */
	private static boolean IS_ADMIN;
	
	private JButton defaultBtn;
	
	private JButton linkBtn;
	
	private JButton lotteryBtn;
	
	private JButton loginBtn;
	
	private JButton modeBtn;
	
	private JButton sendBtn;
	
	private JButton colorBtn;
	
	private JButton thxBtn;
	
	private JButton noticeBtn;
	
	private JButton eNoticeBtn;
	
	private JButton nightBtn;
	
	private JButton callBtn;
	
	private JButton eCallBtn;
	
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
	
	private _LotteryUI lotteryUI;
	
	private _QrcodeUI qrcodeUI;
	
	private _ModeUI modeUI;
	
	private _ColorUI colorUI;
	
	private ChatColor curChatColor;
	
	private boolean isRunning;
	
	private static volatile AppUI instance;
	
	private AppUI() {
		super("哔哩哔哩插件姬 - By 亚絲娜", WIDTH, HEIGHT);
	}
	
	/**
	 * 创建实例
	 * @param args main入参
	 */
	public static void createInstn(String[] args) {
		checkIdentity(args);
		getInstn();
	}
	
	/**
	 * 身份校验
	 * @param args main入参
	 */
	public static void checkIdentity(String[] args) {
		IS_ADMIN = false;
		
		// 管理员: 无条件开启所有功能
		if(args == null || args.length <= 0) {
			IS_ADMIN = true;
			
		// 用户
		} else {
			String code = SwingUtils.input("请输入注册码");
			String errMsg = SafetyUtils.checkAC(code);
			if(StrUtils.isNotEmpty(errMsg)) {
				SwingUtils.warn(errMsg);
				System.exit(0);
				
			} else {
				
				// 主播用户在校验通过后开启所有功能
				IS_ADMIN = (args.length > 1 ? true : false);
			}
		}
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
		this.chatTF = new JTextField();
		this.httpTF = new JTextField("http://live.bilibili.com/");
		this.ridTF = new JTextField(Config.getInstn().SIGN_ROOM_ID(), 15);
		chatTF.setToolTipText("内容长度限制: 20");
		httpTF.setEditable(false);
		
		this.defaultBtn = new JButton("★");
		this.linkBtn = new JButton("偷窥直播间 (无需登陆)");
		this.lotteryBtn = new JButton("抽奖姬 (发起直播间抽奖)");
		this.loginBtn = new JButton("扫码登陆 (可自动参与全平台抽奖)");
		this.modeBtn = new JButton("抽奖模式");
		this.sendBtn = new JButton("发言");
		this.colorBtn = new JButton("●");
		this.thxBtn = new JButton("答谢姬");
		this.noticeBtn = new JButton("公告姬");
		this.eNoticeBtn = new JButton(">");
		this.nightBtn = new JButton("晚安姬");
		this.callBtn = new JButton("小call姬");
		this.eCallBtn = new JButton(">");
		defaultBtn.setToolTipText("设为默认");
		defaultBtn.setForeground(Color.MAGENTA);
		linkBtn.setForeground(Color.BLACK);
		lotteryBtn.setForeground(Color.BLACK);
		loginBtn.setForeground(Color.BLACK);
		modeBtn.setForeground(Color.BLACK);
		sendBtn.setForeground(Color.BLACK);
		colorBtn.setForeground(ChatColor.BLUE.COLOR());
		thxBtn.setForeground(Color.BLACK);
		noticeBtn.setForeground(Color.BLACK);
		eNoticeBtn.setForeground(Color.BLACK);
		nightBtn.setForeground(Color.BLACK);
		callBtn.setForeground(Color.BLACK);
		eCallBtn.setForeground(Color.BLACK);
		
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
		this.lotteryUI = new _LotteryUI();
		this.qrcodeUI = new _QrcodeUI();
		this.modeUI = new _ModeUI();
		this.colorUI = new _ColorUI();
		this.curChatColor = ChatColor.WHITE;
		
		this.isRunning = false;
		printVersionInfo();
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
			livePanel.add(SwingUtils.getEBorderPanel(
					SwingUtils.getPairsPanel("房间号", ridTF), defaultBtn), 
					BorderLayout.EAST);
		}
		panel.add(livePanel, 1);
		return panel;
	}
	
	private JPanel _getLivePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, "直播间信息");
		panel.add(SwingUtils.addAutoScroll(chatTA), BorderLayout.CENTER);
		panel.add(_getSendPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel _getSendPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(chatTF, BorderLayout.CENTER);
		panel.add(SwingUtils.getEBorderPanel(sendBtn, colorBtn), BorderLayout.EAST);
		return panel;
	}
	
	private JPanel _getConsolePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, "运行日志");
		panel.add(SwingUtils.addAutoScroll(consoleTA), BorderLayout.CENTER);
		panel.add(_getCtrlPanel(), BorderLayout.EAST);
		return panel;
	}
	
	private JPanel _getCtrlPanel() {
		return SwingUtils.getVGridPanel(
				thxBtn, 
				SwingUtils.getEBorderPanel(noticeBtn, eNoticeBtn), 
				SwingUtils.getEBorderPanel(callBtn, eCallBtn), 
				nightBtn);
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
		panel.add(modeBtn, BorderLayout.EAST);
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
		setDefaultBtnListener();
		setLinkBtnListener();
		setLotteryBtnListener();
		setLoginBtnListener();
		setModeBtnListener();
		setSendBtnListener();
		setColorBtnListener();
		setThxBtnListener();
		setNoticeBtnListener();
		setNightBtnListener();
		setCallBtnListener();
		setChatTFListener();
		setEditBtnListener();
	}
	
	private void setDefaultBtnListener() {
		defaultBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(IS_ADMIN == false) {
					SwingUtils.warn("非主播用户没有这个技能哦::>_<::");
					return;
				}
				
				String roomId = ridTF.getText();
				if(Config.getInstn().setSignRoomId(roomId)) {
					SwingUtils.info("默认房间号变更为: ".concat(roomId));
				}
			}
		});
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
				
				chatTA.setText("");		// 清空版聊区
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
	
	private void setModeBtnListener() {
		modeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				modeUI._view();
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
				String roomId = getRoomId();
				if(StrUtils.isNotEmpty(msg, roomId)) {
					MsgSender.sendChat(msg, curChatColor, roomId);
					chatTF.setText("");
				}
			}
		});
	}
	
	private void setColorBtnListener() {
		colorBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorUI._view();
			}
		});
	}
	
	private void setThxBtnListener() {
		thxBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogin()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能召唤 [答谢姬] 哦~");
					return;
					
				} else if(IS_ADMIN == false) {
					SwingUtils.warn("为了守护直播间秩序, 非主播用户无法召唤 [答谢姬] 哦~");
					return;
				}
				ChatMgr.getInstn()._start();
				
				ChatMgr.getInstn().setAutoThankYou();
				if(ChatMgr.getInstn().isAutoThankYou()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, thxBtn);
					UIUtils.log("[答谢姬] 被召唤成功O(∩_∩)O");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, thxBtn);
					UIUtils.log("[答谢姬] 被封印啦/(ㄒoㄒ)/");
				}
				ThreadUtils.tSleep(100);
			}
		});
	}
	
	private void setNoticeBtnListener() {
		noticeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogin()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能召唤 [公告姬] 哦~");
					return;
					
				} else if(IS_ADMIN == false) {
					SwingUtils.warn("为了守护直播间秩序, 非主播用户无法召唤 [公告姬] 哦~");
					return;
				}
				ChatMgr.getInstn()._start();
				
				ChatMgr.getInstn().setAutoNotice();
				if(ChatMgr.getInstn().isAutoNotice()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, noticeBtn);
					UIUtils.log("[公告姬] 被召唤成功O(∩_∩)O");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, noticeBtn);
					UIUtils.log("[公告姬] 被封印啦/(ㄒoㄒ)/");
				}
				ThreadUtils.tSleep(100);
			}
		});
	}
	
	private void setCallBtnListener() {
		callBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogin()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能召唤 [小call姬] 哦~");
					return;
					
				} else if(IS_ADMIN == false) {
					SwingUtils.warn("为了守护直播间秩序, 非主播用户无法召唤 [小call姬] 哦~");
					return;
				}
				ChatMgr.getInstn()._start();
				
				ChatMgr.getInstn().setAutoCall();
				if(ChatMgr.getInstn().isAutoCall()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, callBtn);
					UIUtils.log("[小call姬] 被召唤成功O(∩_∩)O");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, callBtn);
					UIUtils.log("[小call姬] 被封印啦/(ㄒoㄒ)/");
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
					SwingUtils.warn("您是个有身份的人~ 先登录才能召唤 [晚安姬] 哦~");
					return;
					
				} else if(IS_ADMIN == false) {
					SwingUtils.warn("为了守护直播间秩序, 非主播用户无法召唤 [晚安姬] 哦~");
					return;
				}
				
				ChatMgr.getInstn().setAutoGoodNight();
				if(ChatMgr.getInstn().isAutoGoodNight()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, nightBtn);
					UIUtils.log("[晚安姬] 被召唤成功O(∩_∩)O");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, nightBtn);
					UIUtils.log("[晚安姬] 被封印啦/(ㄒoㄒ)/");
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
				// Undo
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					sendBtn.doClick();// 监听到回车键则触发发送按钮
				}
			}
		});
	}
	
	private void setEditBtnListener() {
		eNoticeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(IS_ADMIN == false) {
					return;
				}
				
				new _EditorUI("公告姬", Config.getInstn().NOTICE_PATH())._view();
				ThreadUtils.tSleep(100);
			}
			
		});
		
		eCallBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(IS_ADMIN == false) {
					return;
				}
				
				new _EditorUI("打call姬", Config.getInstn().CALL_PATH())._view();
				ThreadUtils.tSleep(100);
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
		
		ChatMgr.getInstn()._stop();
		LoginMgr.getInstn()._stop();	
		WebBot.getInstn()._stop();
		MsgKwMgr.getInstn().clear();
		
		Browser.quit();
	}
	
	
	public void toChat(String msg) {
		if(StrUtils.count(chatTA.getText(), '\n') >= MAX_LINE) {
			chatTA.setText("");
		}
		
		chatTA.append(msg);
		chatTA.append(LINE_END);
		SwingUtils.toEnd(chatTA);
	}
	
	public void toConsole(String msg) {
		if(StrUtils.count(consoleTA.getText(), '\n') >= MAX_LINE) {
			consoleTA.setText("");
		}
		
		consoleTA.append(msg);
		consoleTA.append(LINE_END);
		SwingUtils.toEnd(consoleTA);
	}
	
	public void toNotify(String msg) {
		if(StrUtils.count(notifyTA.getText(), '\n') >= MAX_LINE) {
			notifyTA.setText("");
		}
		
		notifyTA.append(msg);
		notifyTA.append(LINE_END);
		SwingUtils.toEnd(notifyTA);
	}
	
	public void toStatistics(String msg) {
		if(isLogin() == true) {
			if(StrUtils.count(sttcTA.getText(), '\n') >= MAX_LINE) {
				sttcTA.setText("");
			}
			
			sttcTA.append(msg);
			sttcTA.append(LINE_END);
			SwingUtils.toEnd(sttcTA);
		}
	}
	
	/**
	 * 更新抽奖成功次数
	 */
	public void updateLotteryCnt(int num) {
		if(!loginBtn.isEnabled() && num > 0) {	// 登陆按钮被禁用, 说明登陆成功
			lotteryCnt += num;
			String cnt = StrUtils.leftPad(String.valueOf(lotteryCnt), '0', 5);
			lotteryLabel.setText(StrUtils.concat(" ", cnt, " "));
		}
	}
	
	/**
	 * 更新二维码图片
	 */
	public void updateQrcode() {
		qrcodeUI.updateImg();
	}
	
	/**
	 * 更新二维码有效时间
	 * @param time
	 */
	public void updateQrcodeTime(int time) {
		qrcodeUI.updateTime(time);
	}
	
	/**
	 * 标记已登陆成功
	 */
	protected void markLogin(String username) {
		loginBtn.setEnabled(false);	// 标识已登陆
		qrcodeUI._hide();
		linkBtn.doClick();	// 登陆后自动连接到当前直播间
		WebBot.getInstn()._start();
		
		UIUtils.log("登陆成功:".concat(username));
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
	
	/**
	 * 是否为暗中抽奖模式
	 * @return true:使用后台协议抽奖; false:模拟前端行为抽奖
	 */
	public boolean isBackLotteryMode() {
		return modeUI.isBackMode();
	}
	
	/**
	 * 获取当前监听的直播间地址
	 * @return
	 */
	protected String getLiveUrl() {
		return StrUtils.concat(httpTF.getText(), ridTF.getText());
	}
	
	/**
	 * 获取当前监听的直播房间号
	 * @return
	 */
	public String getRoomId() {
		return ridTF.getText();
	}
	
	/**
	 * 更新弹幕颜色
	 * @param color
	 */
	protected void updateChatColor(ChatColor color) {
		curChatColor = color;
		colorBtn.setForeground(color.COLOR());
		colorUI._hide();
		UIUtils.log("当前弹幕颜色 [", curChatColor.ZH(), "]");
	}
	
	public ChatColor getCurChatColor() {
		return curChatColor;
	}
	
	/**
	 * 打印授权版本信息
	 */
	public void printVersionInfo() {
		toConsole("**********************************************************");
		toConsole(" [亚絲娜] 享有本软件的完全著作权");
		toConsole(" 未经许可严禁擅自用于商业用途, 违者保留追究其法律责任的权利");
		toConsole("**********************************************************");
	}
	
}
