package exp.bilibli.plugin.core.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

class QrcodeUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = 3032128610929327304L;

	private final static String TIPS_PATH = LoginMgr.IMG_DIR.concat("/tips.png");
	
	private JButton reflashBtn;
	
	private JButton finishBtn;
	
	private JLabel imgLabel;
	
	protected QrcodeUI() {
		super("登陆二维码", 300, 350);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.reflashBtn = new JButton("刷新二维码");
		this.finishBtn = new JButton("扫码登陆后请点我");
		this.imgLabel = new JLabel(new ImageIcon(TIPS_PATH));
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(imgLabel, BorderLayout.CENTER);
		
		JPanel btnPanel = SwingUtils.getHFlowPanel(new JLabel(" "), 
				reflashBtn, new JLabel(" "), finishBtn, new JLabel(" "));
		rootPanel.add(btnPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		reflashBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(LoginMgr.getInstn().isLoading()) {
					SwingUtils.warn("正在下载二维码, 请稍后再刷新...");
					
				} else {
					
					SwingUtils.warn("正在刷新二维码, 请稍后...");
					if(!LoginMgr.getInstn().loginByQrcode()) {
						SwingUtils.warn("刷新二维码失败   ");
						
					} else {
						SwingUtils.info("二维码已刷新   ");
					}
				}
			}
		});
		
		
		finishBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UIUtils.log("正在保存登陆信息...");
				
				if(LoginMgr.getInstn().saveCookies()) {
					AppUI.getInstn().disableLogin();
					_hide();
					
					UIUtils.log("保存登陆信息成功");
					SwingUtils.info("已保存登陆信息, 下次使用无需登陆   ");
					
				} else {
					UIUtils.log("本次登陆失败, 请刷新二维码重试");
					SwingUtils.warn("本次登陆失败, 请刷新二维码重试   ");
				}
			}
		});
	}

	protected void updateQrcode() {
		File dir = new File(LoginMgr.IMG_DIR);
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.getName().contains(LoginMgr.IMG_NAME)) {
				ImageIcon img = new ImageIcon(file.getPath());
				imgLabel.setIcon(img);
			}
		}
	}
	
	
}
