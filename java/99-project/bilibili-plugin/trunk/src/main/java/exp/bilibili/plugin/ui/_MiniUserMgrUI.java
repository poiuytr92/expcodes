package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.HttpCookie;
import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.envm.LoginType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.pnl.ADPanel;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

public class _MiniUserMgrUI extends PopChildWindow {
	
	private static final long serialVersionUID = 4379374798564622516L;

	private final static int WIDTH = 500;
	
	private final static int HEIGHT = 600;
	
	/** 上限挂机人数 */
	private final static int MAX_USER = CookiesMgr.MAX_NUM;
	
	private JLabel userLabel;
	
	private int roomId;
	
	private JTextField roomTF;
	
	private JButton roomBtn;
	
	private JButton feedBtn;
	
	private ADPanel<__MiniUserLine> adPanel;
	
	private boolean autoFeed;
	
	private boolean init;
	
	public _MiniUserMgrUI() {
		super("哔哩哔哩-小号管理列表", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.userLabel = new JLabel("0/".concat(String.valueOf(MAX_USER)));
		userLabel.setForeground(Color.RED);
		
		this.roomId = Config.getInstn().SIGN_ROOM_ID();
		this.roomTF = new JTextField(String.valueOf(roomId));
		
		this.roomBtn = new JButton("修改房号");
		BeautyEyeUtils.setButtonStyle(NormalColor.green, roomBtn);
		roomBtn.setForeground(Color.BLACK);
		
		this.feedBtn = new JButton("自动投喂");
		feedBtn.setForeground(Color.BLACK);
		
		this.adPanel = new ADPanel<__MiniUserLine>(__MiniUserLine.class, MAX_USER);
		this.autoFeed = false;
		this.init = false;
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(adPanel.getJScrollPanel(), BorderLayout.CENTER);
		rootPanel.add(getSouthPanel(), BorderLayout.SOUTH);
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel);
		panel.add(SwingUtils.getPairsPanel("挂机数", userLabel), BorderLayout.WEST);
		panel.add(SwingUtils.getWEBorderPanel(new JLabel("    [房间号]: "), roomTF, 
				SwingUtils.getHGridPanel(roomBtn, feedBtn)), BorderLayout.CENTER);
		return panel;
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		roomBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int id = NumUtils.toInt(roomTF.getText().trim(), 0);
				if(id > 0) {
					roomId = id;
					SwingUtils.warn("修改成功");
					if(autoFeed == true) {
						UIUtils.log("[自动投喂] 房间号变更为: ", roomId);
					}
					
				} else {
					SwingUtils.warn("无效的房间号");
				}
			}
		});
		
		
		feedBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				autoFeed = !autoFeed;
				if(autoFeed == true) {
					BeautyEyeUtils.setButtonStyle(NormalColor.blue, feedBtn);
					UIUtils.log("[自动投喂] 已启动, 投喂房间号: ", roomId);
					
				} else {
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, feedBtn);
					UIUtils.log("[自动投喂] 已关闭");
				}
			}
		});
	}

	protected void init() {
		if(init == false) {
			init = true;
			int idx = 0;
			CookiesMgr.INSTN().load(LoginType.MINI);
			Set<HttpCookie> cookies = CookiesMgr.INSTN().MINIs();
			for(HttpCookie cookie : cookies) {
				__MiniUserLine line = new __MiniUserLine(cookie);
				adPanel.set(line, idx++);
			}
		}
		updateUserCount();
	}
	
	@Override
	protected void AfterView() {
		updateUserCount();
	}
	
	private void updateUserCount() {
		String text = StrUtils.concat(CookiesMgr.INSTN().miniSize(), "/", MAX_USER);
		userLabel.setText(text);
	}
	
	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
	protected int getFeedRoomId() {
		return roomId;
	}

	protected boolean isAutoFeed() {
		return autoFeed;
	}
	
}
