package exp.bilibili.plugin.core.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import exp.bilibili.plugin.cache.OnlineUserMgr;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.thread.LoopThread;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 直播间在线用户抽奖器
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _LotteryUI extends PopChildWindow {

	private final static long serialVersionUID = -4322589966897649896L;

	private final static int WIDTH = 800;
	
	private final static int HEIGHT = 400;
	
	private final static int ROW = 10;
	
	private final static int COL = 4;
	
	private List<String> users;
	
	private JPanel userPanel;
	
	private JButton luckyBtn;
	
	private JTextField luckyTF;
	
	private JTextArea luckyTA;
	
	private boolean isLottery;
	
	private NameViewer viewer;
	
	protected _LotteryUI() {
		super("直播间活跃用户抽�?", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.users = OnlineUserMgr.getInstn().getAllUsers();
		this.userPanel = new JPanel(new GridLayout(ROW, COL));
		SwingUtils.addBorder(userPanel, "在线活跃用户列表  (仅随机显�?" +  (ROW * COL) + "�?)");
		refreshUserPanel();
		
		this.luckyBtn = new JButton("抽奖人数:" + users.size());
		luckyBtn.setForeground(Color.RED);
		
		this.luckyTF = new JTextField();
		luckyTF.setForeground(Color.RED);
		luckyTF.setEditable(false);
		
		this.luckyTA = new JTextArea(1, 20);
		luckyTA.setEditable(false);
		
		this.isLottery = false;
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(userPanel, BorderLayout.CENTER);
		rootPanel.add(getLotteryPanel(), BorderLayout.SOUTH);
		rootPanel.add(getLuckyPanel(), BorderLayout.EAST);
	}
	
	private void refreshUserPanel() {
		userPanel.removeAll();
		
		int size = ROW * COL;
		size = (size > users.size() ? users.size() : size);
		for(int i = 0; i < size; i++) {
			userPanel.add(new JLabel(users.get(i)), i);
		}
	}
	
	private JPanel getLotteryPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			panel.add(SwingUtils.getHGridPanel(
						new JLabel(" "),
						luckyBtn,
						new JLabel("[小幸运]: ", JLabel.RIGHT),
						luckyTF,
						new JLabel(" ")), 
				BorderLayout.CENTER
			);
		}
		SwingUtils.addBorder(panel, "下一个小幸运是你吗？");
		return panel;
	}
	
	private JPanel getLuckyPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			panel.add(SwingUtils.addAutoScroll(luckyTA), BorderLayout.CENTER);
		}
		SwingUtils.addBorder(panel, "小幸运们");
		return panel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		luckyBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// 触发开始抽奖事�?
				if(isLottery == false) {
					if(users.size() > 1) {
						isLottery = true;
						startLotteryEvent();
					} else {
						SwingUtils.warn("人数不足, 无法抽奖");
					}
					
				// 触发停止抽奖事件
				} else {
					stopLotteryEvent();
					isLottery = false;
				}
				
				ThreadUtils.tSleep(500); // 避免连续点击
			}
		});
	}
	
	private void startLotteryEvent() {
		luckyBtn.setText("祈祷吧！");
		
		viewer = new NameViewer();
		viewer._start();
	}

	private void stopLotteryEvent() {
		if(viewer == null) {
			return;
		}
		viewer._stop();
		viewer._join();
		String username = luckyTF.getText();
		users.remove(username);
		
		luckyBtn.setText("抽奖人数:" + users.size());
		toLuckys(username);
		SwingUtils.info("恭喜 [" + username + "] 中奖!!!");
	}
	
	private void toLuckys(String username) {
		luckyTA.append(username);
		luckyTA.append("\r\n");
		SwingUtils.toEnd(luckyTA);
	}
	
	protected void refreshUsers() {
		if(isLottery == true) {
			return;	// 若在抽奖�?, 则不更新用户�?
		}
		
		users.clear();
		users = OnlineUserMgr.getInstn().getAllUsers();
		refreshUserPanel();
		
		luckyBtn.setText("抽奖人数:" + users.size());
		luckyTF.setText("");
	}
	
	protected void clear() {
		users.clear();
		if(viewer != null) {
			viewer._stop();
			OnlineUserMgr.getInstn().clear();
		}
	}
	
	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private class NameViewer extends LoopThread {

		protected NameViewer() {
			super("名字随机闪现�?");
		}

		@Override
		protected void _before() {}

		@Override
		protected void _loopRun() {
			int idx = RandomUtils.randomInt(users.size());
			String username = users.get(idx);
			luckyTF.setText(username);
		}

		@Override
		protected void _after() {}
		
	}
	
}
