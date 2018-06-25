package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.au.api.AppVerInfo;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.bean.ldm.HotLiveRange;
import exp.bilibili.plugin.cache.ActivityMgr;
import exp.bilibili.plugin.cache.ChatMgr;
import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.cache.MsgKwMgr;
import exp.bilibili.plugin.cache.OnlineUserMgr;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.cache.StormScanner;
import exp.bilibili.plugin.cache.WebBot;
import exp.bilibili.plugin.envm.CookieType;
import exp.bilibili.plugin.envm.Danmu;
import exp.bilibili.plugin.envm.Identity;
import exp.bilibili.plugin.monitor.SafetyMonitor;
import exp.bilibili.plugin.ui.login.LoginBtn;
import exp.bilibili.plugin.utils.SafetyUtils;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.XHRSender;
import exp.bilibili.protocol.ws.BiliWebSocketMgr;
import exp.libs.envm.Colors;
import exp.libs.envm.Delimiter;
import exp.libs.envm.FileType;
import exp.libs.utils.encode.CompressUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.ThreadPool;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

/**
 * <PRE>
 * 主应用程序窗口
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AppUI extends MainWindow {

	/** serialVersionUID */
	private final static long serialVersionUID = 2097374309672044616L;

	private final static int WIDTH = 1200;
	
	private final static int HEIGHT = 700;
	
	/** 避免连续点击按钮的锁定时�? */
	private final static long LOCK_TIME = 50;
	
	/** 界面文本框最大缓存行�? */
	private final static int MAX_LINE = 200;
	
	/** 换行�? */
	private final static char LF = '\n';
	
	/** 主分割面�? */
	private JSplitPane splitPanel;
			
	private String loginUser;
	
	private boolean isLogined;
	
	private JButton loginBtn;
	
	private JButton percentBtn;
	
	private JButton logoutBtn;
	
	private JButton addUserBtn;
	
	private JButton exportBtn;
	
	private JButton importBtn;
	
	private JButton loveBtn;
	
	private JButton linkBtn;
	
	private JButton lotteryBtn;
	
	private JButton activeListBtn;
	
	private JButton sendBtn;
	
	private JButton colorBtn;
	
	private JButton musicBtn;
	
	private JButton eMusicBtn;
	
	private JButton noticeBtn;
	
	private JButton eNoticeBtn;
	
	private JButton callBtn;
	
	private JButton eCallBtn;
	
	private JButton thxBtn;
	
	private JButton eThxBtn;
	
	private JButton nightBtn;
	
	private JButton eNightBtn;
	
	private JButton stormBtn;
	
	private JButton eStormBtn;
	
	private JButton guardBtn;
	
	private JTextField httpTF;
	
	private JTextField liveRoomTF;
	
	private JTextArea chatTA;
	
	private JTextField chatTF;
	
	private JTextArea consoleTA;
	
	private JTextArea notifyTA;
	
	private JTextArea sttcTA;
	
	private int lotteryCnt;
	
	private JLabel lotteryLabel;
	
	private BiliWebSocketMgr wsMgr;
	
	private _ProbabilityUI probabilityUI;
	
	private _MiniUserMgrUI miniLoginMgrUI;
	
	private _LotteryUI lotteryUI;
	
	private _ColorUI colorUI;
	
	private _StormModeUI stormUI;
	
	private Colors curChatColor;
	
	private boolean joinLottery;
	
	/** 线程�? */
	private ThreadPool tp;
	
	private static volatile AppUI instance;
	
	private AppUI() {
		super("哔哩哔哩插件�? - By 亚絲�?", WIDTH, HEIGHT);
	}
	
	/**
	 * 创建实例
	 * @param args main入参
	 */
	public static void createInstn(String[] args) {
		if(checkIdentity(args)) {
			
			// 非试用用户才 导出自动升级入口
			if(!Identity.less(Identity.USER)) {
				AppVerInfo.export(Config.APP_NAME);
			}
			
			// 启动程序实例
			getInstn();
			
		} else {
			System.exit(0);
		}
	}
	
	/**
	 * <pre>
	 * 身份校验:
	 *  管理�?:拥有全部功能, 不受授权码和证书影响
	 *  主播:拥有部分功能, 受授权码和授权时间影�?
	 *  普通用�?:拥有部分功能, 受授权码和授权时间影�?
	 *  游客:拥有部分功能, 受授权码和授权时间影�?
	 * </pre>
	 * @param args main入参
	 */
	public static boolean checkIdentity(String[] args) {
		boolean isOk = true;
		if(StrUtils.isEmpty(args) || args.length != 1) {
			isOk = false;
			
		} else {
			
			// 管理�?: 无条件开启所有功�?
			if(Identity.ADMIN.CMD().equals(args[0])) {
				Identity.set(Identity.ADMIN);
				
			// 用户
			} else {
				String code = SwingUtils.input("请输入注册码");
				String errMsg = SafetyUtils.checkAC(code);
				if(StrUtils.isNotEmpty(errMsg)) {
					SwingUtils.warn(errMsg);
					isOk = false;
				}
				
				// 主播用户
				if(Identity.UPLIVE.CMD().equals(args[0])) {
					Identity.set(Identity.UPLIVE);
					
				// 普通用�?
				} else if(Identity.USER.CMD().equals(args[0])) {
					Identity.set(Identity.USER);
					
				// 试用用户(游客)
				} else {
					Identity.set(Identity.GUEST);
				}
			}
		}
		return isOk;
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
		this.tp = new ThreadPool(10);
		
		this.chatTF = new JTextField();
		this.httpTF = new JTextField("http://live.bilibili.com/");
		this.liveRoomTF = new JTextField(String.valueOf(Config.getInstn().SIGN_ROOM_ID()), 15);
		chatTF.setToolTipText("内容长度限制: ".concat(String.valueOf(Danmu.LEN_LIMIT)));
		httpTF.setEditable(false);
		
		this.loginUser = "";
		this.isLogined = false;
		this.percentBtn = newButton("%");
		this.loginBtn = newButton("扫码/帐密登陆");
		this.logoutBtn = newButton("销");
		this.addUserBtn = newButton("�?");
		this.exportBtn = newButton("�?");
		this.importBtn = newButton("�?");
		this.loveBtn = newButton("�?");
		this.linkBtn = newButton("偷窥直播�? (无需登陆)");
		this.lotteryBtn = newButton("抽奖�? (发起直播间抽�?)");
		this.activeListBtn = newButton("�?");
		this.sendBtn = newButton("发言");
		this.colorBtn = newButton("�?");
		this.musicBtn = newButton("随缘点歌�?");
		this.eMusicBtn = newButton(">");
		this.callBtn = newButton("小call�?");
		this.eCallBtn = newButton(">");
		this.noticeBtn = newButton("公告�?");
		this.eNoticeBtn = newButton(">");
		this.thxBtn = newButton("答谢�?");
		this.eThxBtn = newButton(">");
		this.nightBtn = newButton("晚安�?");
		this.eNightBtn = newButton(">");
		this.stormBtn = newButton("节奏风暴");
		this.eStormBtn = newButton(">");
		this.guardBtn = newButton("补领总督奖励");
		
		loveBtn.setToolTipText("设为默认");
		loveBtn.setForeground(Color.MAGENTA);
		activeListBtn.setForeground(Color.BLUE);
		colorBtn.setForeground(Colors.BLUE.COLOR());
		
		this.chatTA = new JTextArea();
		this.consoleTA = new JTextArea(8, 10);
		this.notifyTA = new JTextArea(1, 40);
		this.sttcTA = new JTextArea(10, 40);
		chatTA.setEditable(false);
		consoleTA.setEditable(false);
		notifyTA.setEditable(false);
		sttcTA.setEditable(false);
		
		this.joinLottery = false;
		this.lotteryCnt = 0;
		this.lotteryLabel = new JLabel(" 00000 ");
		lotteryLabel.setForeground(Color.RED);
		
		this.wsMgr = new BiliWebSocketMgr();
		this.probabilityUI = new _ProbabilityUI();
		this.miniLoginMgrUI = new _MiniUserMgrUI();
		this.lotteryUI = new _LotteryUI();
		this.colorUI = new _ColorUI();
		this.stormUI = new _StormModeUI();
		this.curChatColor = Colors.RANDOM();
		
		printVersionInfo();
	}
	
	private JButton newButton(String name) {
		JButton btn = new JButton(name);
		btn.setForeground(Color.BLACK);	// 处理win7按钮文本弱化问题
		return btn; 
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		this.splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPanel.setLeftComponent(getLeftPanel());
		splitPanel.setRightComponent(getRightPanel());
		rootPanel.add(splitPanel, BorderLayout.CENTER);
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
		panel.add(SwingUtils.getHGridPanel(linkBtn, 
				SwingUtils.getEBorderPanel(lotteryBtn, activeListBtn)), 0);
		
		JPanel livePanel = new JPanel(new BorderLayout()); {
			livePanel.add(SwingUtils.getPairsPanel("直播间地址", httpTF), BorderLayout.CENTER);
			livePanel.add(SwingUtils.getEBorderPanel(
					SwingUtils.getPairsPanel("房间�?", liveRoomTF), loveBtn), 
					BorderLayout.EAST);
		}
		panel.add(livePanel, 1);
		return panel;
	}
	
	private JPanel _getLivePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel, "直播间信�?");
		panel.add(SwingUtils.addAutoScroll(chatTA), BorderLayout.CENTER);
		panel.add(_getSendPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel _getSendPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(chatTF, BorderLayout.CENTER);
		panel.add(SwingUtils.getEBorderPanel(
				SwingUtils.getEBorderPanel(sendBtn, colorBtn), 
				SwingUtils.getEBorderPanel(musicBtn, eMusicBtn)), 
				BorderLayout.EAST);
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
				SwingUtils.getEBorderPanel(callBtn, eCallBtn), 
				SwingUtils.getEBorderPanel(noticeBtn, eNoticeBtn), 
				SwingUtils.getEBorderPanel(thxBtn, eThxBtn), 
				SwingUtils.getEBorderPanel(nightBtn, eNightBtn), 
				SwingUtils.getEBorderPanel(stormBtn, eStormBtn), 
				guardBtn
		);
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
		panel.add(SwingUtils.getWBorderPanel(
				loginBtn, percentBtn), BorderLayout.CENTER);
		panel.add(SwingUtils.getHGridPanel(
				addUserBtn, exportBtn, importBtn, logoutBtn), BorderLayout.EAST);
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
		setPercentBtnListener();
		setLoginBtnListener();
		setLogoutBtnListener();
		setAddUserBtnListener();
		setExportBtnListener();
		setImportBtnListener();
		setLoveBtnListener();
		setLinkBtnListener();
		setLotteryBtnListener();
		setActiveListBtnListener();
		setChatTFListener();
		setSendBtnListener();
		setColorBtnListener();
		setMusicBtnListener();
		setNoticeBtnListener();
		setCallBtnListener();
		setThxBtnListener();
		setNightBtnListener();
		setStormBtnListener();
		setGuardBtnListener();
	}
	
	private void setPercentBtnListener() {
		percentBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Identity.less(Identity.UPLIVE)) {
					SwingUtils.warn("您未被授权更�? [抽奖参数] 哦~");
					return;
				}
				
				probabilityUI._view();
			}
		});
	}
	
	private void setLoginBtnListener() {
		loginBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isLogined() == false) {
					_loginMain();
					
				} else {
					switchLottery();	// 登陆成功�?, 变更为全局抽奖总开�?
				}
			}
		});
	}
	
	/**
	 * 登陆主号
	 */
	private void _loginMain() {
		
		// 自动登陆
		if(CookiesMgr.MAIN() != BiliCookie.NULL || 
				CookiesMgr.getInstn().load(CookieType.MAIN)) {
			markLogin(CookiesMgr.MAIN().NICKNAME());
			_loginMinis();
		
		// 手工登陆
		} else {
			LoginBtn btn = new LoginBtn(CookieType.MAIN, "", new __LoginCallback() {
				
				@Override
				public void afterLogin(final BiliCookie cookie) {
					markLogin(cookie.NICKNAME());
					_loginMinis();
				}
				
				@Override
				public void afterLogout(final BiliCookie cookie) {
					// Undo
				}
			});
			btn.doClick();
		}
	}
	
	/**
	 * 异步登陆所有小�?
	 */
	private void _loginMinis() {
		tp.execute(new Thread() {
			
			@Override
			public void run() {
				miniLoginMgrUI.init();
			};
		});
	}
	
	private void setLogoutBtnListener() {
		logoutBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(SwingUtils.confirm("注销 [主号] �? [马甲号] 并退出程�?, 继续�? ?")) {
					if(CookiesMgr.clearMainAndVestCookies()) {
						UIUtils.notityExit("注销成功, 重启后请重新登陆");
						
					} else {
						SwingUtils.info("注销失败");
					}
				}
			}
		});
	}
	
	private void setAddUserBtnListener() {
		addUserBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("请先登录主号");
					return;
				}
				
				miniLoginMgrUI._view();
			}
		});
	}
	
	private void setExportBtnListener() {
		exportBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(SwingUtils.confirm("备份登陆账号的Cookies? (用于升级迁移)")) {
					String snkPath = StrUtils.concat(PathUtils.getDesktopPath(), 
							File.separator, 
							FileUtils.getName(Config.getInstn().COOKIE_DIR()), 
							FileType.ZIP.EXT);
					if(CompressUtils.toZip(Config.getInstn().COOKIE_DIR(), snkPath)) {
						SwingUtils.info("Cookies已备份到桌面: ".concat(snkPath));
					}
				}
			}
		});
	}
	
	private void setImportBtnListener() {
		importBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(SwingUtils.confirm("导入Cookies? (会覆盖当前登陆账�?)")) {
					JFileChooser fc = new JFileChooser();
					
					if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)) {
						File file = fc.getSelectedFile();
						
						boolean isOk = true;
						if(FileType.ZIP == FileUtils.getFileType(file)) {
							isOk = CompressUtils.unZip(file.getAbsolutePath(), ".");
							isOk &= !FileUtils.isEmpty(Config.getInstn().COOKIE_DIR());
						} else {
							isOk = false;
						}
						
						if(isOk == true) {
							SwingUtils.info("Cookies已导�?".concat(
									isLogined() ? "(重启后生�?)" : ""));
						} else {
							SwingUtils.warn("无效的Cookies文件");
						}
					}
				}
			}
		});
	}
	
	private void setLoveBtnListener() {
		loveBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int roomId = getLiveRoomId();
				int realRoomId = RoomMgr.getInstn().getRealRoomId(roomId);
				if(realRoomId <= 0) {
					SwingUtils.warn("直播间房号无�?/未收�?");
					return;
				}
				
				if(Config.getInstn().setSignRoomId(roomId)) {
					linkBtn.doClick();
					SwingUtils.info("默认房间号变更为: ".concat(String.valueOf(roomId)));
				}
			}
		});
	}

	private void setLinkBtnListener() {
		linkBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int roomId = RoomMgr.getInstn().getRealRoomId(getLiveRoomId());
				if(roomId <= 0) {
					SwingUtils.warn("直播间房号无�?/未收�?");
					return;
				}
				
				wsMgr.relinkLive(roomId);	// 连接到版聊直播间
				_switchRoom();	// 切换房间后的操作
				lockBtn();
			}
		});
	}
	
	/**
	 * 切换房间后的操作
	 */
	private void _switchRoom() {
		chatTA.setText("");		// 清空版聊�?
		OnlineUserMgr.getInstn().clear(); // 清空上一直播间的在线用户列表
		OnlineUserMgr.getInstn().updateManagers(); // 更新当前直播间的房管列表(含主�?)
		
		// 更新主号在新房间的权�?(主要是房管、弹幕长�?)
		if(isLogined() == true) {
			XHRSender.queryUserAuthorityInfo(CookiesMgr.MAIN());
			
			// 暂不开放动态切换软件权�?
//			if(Identity.less(Identity.ADMIN)) {
//				if(CookiesMgr.MAIN().isRoomAdmin()) {
//					Identity.set(Identity.UPLIVE);
//				} else {
//					Identity.set(Identity.USER);
//				}
//			}
		}
	}
	
	private void setLotteryBtnListener() {
		lotteryBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Identity.less(Identity.UPLIVE)) {
					SwingUtils.warn("您未被授权发�? [直播间抽奖] 哦~");
					return;
				}
				
				lotteryUI._view();
				lockBtn();
			}
		});
	}
	
	private void setActiveListBtnListener() {
		activeListBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Identity.less(Identity.ADMIN)) {
					SwingUtils.warn("您未被授权管�? [活跃值排行榜] 哦~");
					return;
				}
				
				ActivityMgr.getInstn().init();
				new _ActiveListUI()._view();
			}
		});
	}
	
	private void setChatTFListener() {
		chatTF.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(chatTF.getText().length() > CookiesMgr.MAIN().DANMU_LEN()) {
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
					sendBtn.doClick();// 监听到回车键则触发发送按�?
				}
			}
		});
	}
	
	private void setSendBtnListener() {
		sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能发言哦~");
					return;
				}
				
				final String msg = chatTF.getText();
				if(StrUtils.isNotEmpty(msg)) {
					tp.execute(new Thread() {
						
						@Override
						public void run() {
							XHRSender.sendDanmu(msg, curChatColor);
						}
					});
				}
				chatTF.setText("");
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
	
	private void setMusicBtnListener() {
		musicBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能召�? [随缘点歌姬] 哦~");
					return;
				}
				
				tp.execute(new Thread() {
					
					@Override
					public void run() {
						String music = MsgKwMgr.getMusic();
						if(StrUtils.isNotEmpty(music)) {
							XHRSender.sendDanmu("#点歌 ".concat(music), curChatColor);
						}
					}
				});
				lockBtn();
			}
		});
		
		eMusicBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new _EditorUI("歌单", Config.getInstn().MUSIC_PATH())._view();
				lockBtn();
			}
			
		});
	}
	
	
	private void setNoticeBtnListener() {
		noticeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能召�? [公告姬] 哦~");
					return;
					
				} else if(Identity.less(Identity.UPLIVE)) {
					SwingUtils.warn("为了守护直播间秩�?, 非主播用户无法召�? [公告姬] 哦~");
					return;
					
				} else if(Identity.less(Identity.ADMIN) && 
						Config.getInstn().isTabuAutoChat(getLiveRoomId())) {
					SwingUtils.warn("您未被授权在此直播间使用 [公告姬] 哦~");
					return;
				}
				
				ChatMgr.getInstn()._start();
				ChatMgr.getInstn().setAutoNotice();
				if(ChatMgr.getInstn().isAutoNotice()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, noticeBtn);
					UIUtils.log("[公告姬] 被召唤成功O(∩_�?)O");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, noticeBtn);
					UIUtils.log("[公告姬] 被封印啦/(ㄒo�?)/");
				}
				lockBtn();
			}
		});
		
		eNoticeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(Identity.less(Identity.UPLIVE)) {
					return;
				}
				
				new _EditorUI("公告", Config.getInstn().NOTICE_PATH())._view();
				lockBtn();
			}
			
		});
	}
	
	private void setCallBtnListener() {
		callBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能使�? [小call姬] 哦~");
					return;
				}
				
				tp.execute(new Thread() {
					
					@Override
					public void run() {
						List<String> calls = new ArrayList<String>(MsgKwMgr.getCalls());
						if(ListUtils.isEmpty(calls)) {
							return;
						}
						
						for(BiliCookie cookie : CookiesMgr.ALL()) {
							if(!cookie.isBindTel() || RandomUtils.genBoolean()) {
								continue;
							}
							
							String msg = RandomUtils.genElement(calls);
							calls.remove(msg);
							
							XHRSender.sendDanmu(cookie, msg);
							ThreadUtils.tSleep(RandomUtils.genInt(1000, 10000));
						}
					}
				});
				lockBtn();
			}
		});
		
		eCallBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new _EditorUI("打call语录", Config.getInstn().CALL_PATH())._view();
				lockBtn();
			}
			
		});
	}
	
	private void setThxBtnListener() {
		thxBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能召�? [答谢姬] 哦~");
					return;
					
				} else if(Identity.less(Identity.UPLIVE)) {
					SwingUtils.warn("为了守护直播间秩�?, 非主播用户无法召�? [答谢姬] 哦~");
					return;
					
				} else if(Identity.less(Identity.ADMIN) && 
						Config.getInstn().isTabuAutoChat(getLiveRoomId())) {
					SwingUtils.warn("您未被授权在此直播间使用 [答谢姬] 哦~");
					return;
				}
				
				ChatMgr.getInstn()._start();
				ChatMgr.getInstn().setAutoThankYou();
				if(ChatMgr.getInstn().isAutoThankYou()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, thxBtn);
					UIUtils.log("[答谢姬] 被召唤成功O(∩_�?)O");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, thxBtn);
					UIUtils.log("[答谢姬] 被封印啦/(ㄒo�?)/");
				}
				lockBtn();
			}
		});
		
		eThxBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new _EditorUI("骚气形容�?", Config.getInstn().ADV_PATH())._view();
				lockBtn();
			}
			
		});
	}
	
	private void setNightBtnListener() {
		nightBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("您是个有身份的人~ 先登录才能召�? [晚安姬] 哦~");
					return;
					
				} else if(Identity.less(Identity.UPLIVE)) {
					SwingUtils.warn("为了守护直播间秩�?, 非主播用户无法召�? [晚安姬] 哦~");
					return;
					
				} else if(Identity.less(Identity.ADMIN) && 
						Config.getInstn().isTabuAutoChat(getLiveRoomId())) {
					SwingUtils.warn("您未被授权在此直播间使用 [晚安姬] 哦~");
					return;
				}
				
				ChatMgr.getInstn()._start();
				ChatMgr.getInstn().setAutoGoodNight();
				if(ChatMgr.getInstn().isAutoGoodNight()) {
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, nightBtn);
					UIUtils.log("[晚安姬] 被召唤成功O(∩_�?)O");
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, nightBtn);
					UIUtils.log("[晚安姬] 被封印啦/(ㄒo�?)/");
				}
				lockBtn();
			}
		});
		
		eNightBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new _EditorUI("晚安关键�?", Config.getInstn().NIGHT_PATH())._view();
				lockBtn();
			}
			
		});
	}
	
	private void setStormBtnListener() {
		stormBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("登陆后才能使用此功能");
					return;
				}
				
				// 扫描器线程未启动，则触发登录马甲流程
				if(!StormScanner.getInstn().isRun()) {
					_loginStormVest();
					
				// 扫描器线程已启动，则纯粹切换扫描状�?
				} else {
					StormScanner.getInstn().setScan();
					if(StormScanner.getInstn().isScan()) {
						BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, stormBtn);
						UIUtils.log("[全平台节奏风暴扫描] 已启�?");
						
					} else {
						BeautyEyeUtils.setButtonStyle(NormalColor.normal, stormBtn);
						UIUtils.log("[全平台节奏风暴扫描] 已停�?");
					}
				}
				lockBtn();
			}
		});
		
		eStormBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stormUI._view();
			}
		});
	}
	
	/**
	 * 登录节奏风暴马甲�?(用于扫描全平台节奏风�?)
	 */
	private void _loginStormVest() {
		CookiesMgr.getInstn().load(CookieType.VEST);
		BiliCookie vestCookie = CookiesMgr.VEST();
		
		// 若现有马甲号不是主号，则使用现有马甲�?
		if(BiliCookie.NULL != vestCookie && !CookiesMgr.MAIN().equals(vestCookie)) {
			_startStormScanner();
			
		// 若不存在马甲�? �? 现有马甲号是主号�? 则询�?
		} else if(SwingUtils.confirm("存在风险, 是否使用 [马甲号] 扫描 ? (收益归主号所�?)")) {
			LoginBtn btn = new LoginBtn(CookieType.VEST, "", new __LoginCallback() {
				
				@Override
				public void afterLogin(final BiliCookie cookie) {
					_startStormScanner();
				}
				
				@Override
				public void afterLogout(final BiliCookie cookie) {
					// Undo
				}
				
			});
			btn.doClick();
			
		// 使用主号作为马甲�?
		} else {
			CookiesMgr.getInstn().add(CookiesMgr.MAIN(), CookieType.VEST);
			_startStormScanner();
		}
	}
	
	/**
	 * 开启节奏风暴扫�?
	 */
	private void _startStormScanner() {
		StormScanner.getInstn()._start();
		lockBtn();
		stormBtn.doClick();
	}
	
	private void setGuardBtnListener() {
		guardBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLogined()) {
					SwingUtils.warn("先登录才�? [补领总督亲密奖励] 哦~");
					return;
					
				} else if(Identity.less(Identity.UPLIVE)) {
					SwingUtils.warn("非主播用户无�? [补领总督亲密奖励] 哦~");
					return;
				}
				
				guardBtn.setEnabled(false);
				UIUtils.log("正在扫描人气直播间的总督列表...");
				new Thread() {
					
					@Override
					public void run() {
						int cnt = XHRSender.getGuardGift();
						if(cnt <= 0) {
							UIUtils.log("暂时检索不到未领取的总督奖励.");
						}
						
						guardBtn.setEnabled(true);
					};
				}.start();
			}
		});
	}
	
	@Override
	protected void AfterView() {
		
		// 设置主分割面板的左右比例(只能在窗体可见时此方法才有效)
		splitPanel.setDividerLocation(0.65);
	}

	@Override
	protected void beforeHide() {}
	
	@Override
	protected void beforeExit() {
		wsMgr._stop();
		lotteryUI.clear();
		
		StormScanner.getInstn()._stop();
		ChatMgr.getInstn()._stop();
		WebBot.getInstn()._stop();
		MsgKwMgr.getInstn().clear();
		SafetyMonitor.getInstn()._stop();
		ActivityMgr.getInstn().save();
	}
	
	
	public void toChat(String msg) {
		if(StrUtils.count(chatTA.getText(), LF) >= MAX_LINE) {
			chatTA.setText("");
		}
		
		chatTA.append(msg.concat(Delimiter.CRLF));
		SwingUtils.toEnd(chatTA);
	}
	
	public void toConsole(String msg) {
		if(StrUtils.count(consoleTA.getText(), LF) >= MAX_LINE) {
			consoleTA.setText("");
		}
		
		consoleTA.append(msg.concat(Delimiter.CRLF));
		SwingUtils.toEnd(consoleTA);
	}
	
	public void toNotify(String msg) {
		if(StrUtils.count(notifyTA.getText(), LF) >= MAX_LINE) {
			notifyTA.setText("");
		}
		
		notifyTA.append(msg.concat(Delimiter.CRLF));
		SwingUtils.toEnd(notifyTA);
	}
	
	public void toStatistics(String msg) {
		if(isLogined() == true) {
			if(StrUtils.count(sttcTA.getText(), LF) >= MAX_LINE) {
				sttcTA.setText("");
			}
			
			sttcTA.append(msg.concat(Delimiter.CRLF));
			SwingUtils.toEnd(sttcTA);
		}
	}
	
	/**
	 * 更新抽奖成功次数
	 */
	public void updateLotteryCnt(int num) {
		if(isLogined() && num > 0) {
			lotteryCnt += num;
			String cnt = StrUtils.leftPad(String.valueOf(lotteryCnt), '0', 5);
			lotteryLabel.setText(StrUtils.concat(" ", cnt, " "));
		}
	}
	
	/**
	 * 标记已登陆成�?
	 */
	public void markLogin(String username) {
		loginUser = username;
		isLogined = true;
		loginBtn.setText("自动抽奖");
		switchLottery();
		
		linkBtn.doClick();	// 登陆后自动连接到当前直播�?
		WebBot.getInstn()._start();	// 启动仿真机器�?
		
		updateTitle("0000-00-00");
		UIUtils.log("欢迎肥来: ".concat(loginUser));
		
		// 开始监控软件授�?
		SafetyMonitor.getInstn()._start();
	}
	
	/**
	 * 切换：是否开启全局抽奖
	 */
	private void switchLottery() {
		joinLottery = !joinLottery;
		if(joinLottery == true) {
			BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, loginBtn);
			UIUtils.log("已激活全平台自动抽奖（小电视、摩天大楼、高能抽奖等�?");
			wsMgr._start();	// 启动分区监听
			
		} else {
			BeautyEyeUtils.setButtonStyle(NormalColor.normal, loginBtn);
			UIUtils.log("已关闭全平台自动抽奖");
		}
	}
	
	/**
	 * 全局开关：是否参加抽奖
	 * @return
	 */
	public boolean isJoinLottery() {
		return joinLottery;
	}
	
	/**
	 * 更新软件标题(用户�?+授权时间)
	 */
	public void updateTitle(String certificateTime) {
		String title = StrUtils.concat(getTitle().replaceFirst("    \\[.*", ""), 
				"    [登陆用户 : ", loginUser, 
				"] [授权到期: ", certificateTime, "]");
		setTitle(title);
	}
	
	/**
	 * 检查是否已登录
	 * @return
	 */
	public boolean isLogined() {
		return isLogined;
	}	
	
	/**
	 * 获取当前监听的直播间地址
	 * @return
	 */
	public String getLiveUrl() {
		return StrUtils.concat(httpTF.getText(), liveRoomTF.getText());
	}
	
	/**
	 * 获取当前监听的直播房间号
	 * @return
	 */
	public int getLiveRoomId() {
		return NumUtils.toInt(liveRoomTF.getText().trim());
	}
	
	/**
	 * 根据节奏风暴策略获取人气直播间的扫描范围
	 * @return
	 */
	public HotLiveRange getHotLiveRange() {
		return stormUI.getHotLiveRange();
	}
	
	/**
	 * 获取参与抽奖的概�?
	 * @return 参与抽奖的概�?
	 */
	public int getLotteryProbability() {
		return probabilityUI.PROBABILITY();
	}
	
	/**
	 * 获取参与抽奖的反应时�?
	 * @return 参与抽奖的反应时�?
	 */
	public long getReactionTime() {
		return probabilityUI.REACTION_TIME();
	}
	
	/**
	 * 是否自动投喂主播
	 * @return
	 */
	public boolean isAutoFeed() {
		return miniLoginMgrUI.isAutoFeed();
	}
	
	/**
	 * 更新弹幕颜色
	 * @param color
	 */
	protected void updateChatColor(Colors color) {
		curChatColor = color;
		colorBtn.setForeground(color.COLOR());
		colorUI._hide();
		UIUtils.log("当前弹幕颜色 [", curChatColor.ZH(), "]");
	}
	
	public Colors getCurChatColor() {
		return curChatColor;
	}
	
	/**
	 * 瞬时锁定按钮，避免连续点�?
	 */
	private void lockBtn() {
		ThreadUtils.tSleep(LOCK_TIME);
	}
	
	/**
	 * 打印授权版本信息
	 */
	public void printVersionInfo() {
		toConsole("**********************************************************");
		toConsole(StrUtils.concat(" [亚絲娜] 享有本软件的完全著作�? (当前版本: v", Config.APP_VER, ")"));
		toConsole(" 未经许可严禁擅自用于商业用�?, 违者保留追究其法律责任的权�?");
		toConsole("**********************************************************");
	}
	
}
