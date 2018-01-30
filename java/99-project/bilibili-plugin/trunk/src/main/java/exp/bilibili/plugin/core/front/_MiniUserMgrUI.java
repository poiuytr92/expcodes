package exp.bilibili.plugin.core.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.bilibili.plugin.Config;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.envm.LoginType;
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
	
	private JTextField feedRoomTF;
	
	private JButton feedBtn;
	
	private ADPanel<__MiniUserLine> adPanel;
	
	private boolean init;
	
	public _MiniUserMgrUI() {
		super("哔哩哔哩-小号管理列表", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.userLabel = new JLabel("0/".concat(String.valueOf(MAX_USER)));
		userLabel.setForeground(Color.RED);
		
		this.feedRoomTF = new JTextField(
				String.valueOf(Config.getInstn().SIGN_ROOM_ID()));
		this.feedBtn = new JButton("自动投喂");
		feedBtn.setForeground(Color.BLACK);
		BeautyEyeUtils.setButtonStyle(NormalColor.green, feedBtn);
		
		this.adPanel = new ADPanel<__MiniUserLine>(__MiniUserLine.class, MAX_USER);
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
		panel.add(SwingUtils.getWEBorderPanel(
				new JLabel("    [房间号]: "), feedRoomTF, feedBtn), BorderLayout.CENTER);
		return panel;
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void AfterView() {
		if(init == false) {
			init = true;
			int idx = 0;
			CookiesMgr.INSTN().load(LoginType.MINI);
			Iterator<HttpCookie> minis = CookiesMgr.INSTN().MINIs();
			while(minis.hasNext()) {
				HttpCookie cookie = minis.next();
				
				__MiniUserLine line = new __MiniUserLine(cookie);
				adPanel.set(line, idx++);
			}
		}
		updateUserCnt();
	}
	
	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
	protected void updateUserCnt() {
		String text = StrUtils.concat(adPanel.size(), "/", MAX_USER);
		userLabel.setText(text);
	}
	
	protected int getFeedRoomId() {
		return NumUtils.toInt(feedRoomTF.getText().trim());
	}

}
