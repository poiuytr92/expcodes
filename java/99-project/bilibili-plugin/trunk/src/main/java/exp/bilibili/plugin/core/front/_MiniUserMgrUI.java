package exp.bilibili.plugin.core.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exp.bilibili.plugin.Config;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.bilibili.protocol.envm.LoginType;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.pnl.ADPanel;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

public class _MiniUserMgrUI extends PopChildWindow {

	private static final long serialVersionUID = 4379374798564622516L;

	private final static int WIDTH = 500;
	
	private final static int HEIGHT = 600;
	
	private JTextField feedRoomTF;
	
	private JButton feedBtn;
	
	private ADPanel<__MiniUserLine> adPanel;
	
	private boolean init;
	
	public _MiniUserMgrUI() {
		super("哔哩哔哩-小号管理列表", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.feedRoomTF = new JTextField(
				String.valueOf(Config.getInstn().SIGN_ROOM_ID()));
		this.feedBtn = new JButton("修改房间号");
		feedBtn.setForeground(Color.BLACK);
		
		this.adPanel = new ADPanel<__MiniUserLine>(__MiniUserLine.class);
		this.init = false;
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(adPanel.getJScrollPanel(), BorderLayout.CENTER);
		rootPanel.add(SwingUtils.addBorder(SwingUtils.getWEBorderPanel
				(new JLabel(" [自动投喂房间号]: "), feedRoomTF, feedBtn)), BorderLayout.SOUTH);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void AfterView() {
		if(init == true) {
			return;
		}
		
		CookiesMgr.INSTN().load(LoginType.MINI);
		Iterator<HttpCookie> minis = CookiesMgr.INSTN().MINIs();
		while(minis.hasNext()) {
			HttpCookie cookie = minis.next();
			
			__MiniUserLine line = new __MiniUserLine(cookie);
			adPanel.addLine(line);
		}
		adPanel.delLine(0);
		init = true;
	}
	
	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}

}
