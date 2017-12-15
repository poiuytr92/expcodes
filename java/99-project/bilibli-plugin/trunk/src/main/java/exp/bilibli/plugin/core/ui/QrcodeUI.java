package exp.bilibli.plugin.core.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
		this.reflashBtn = new JButton("刷新图案");
		this.finishBtn = new JButton("扫描登陆后请点我");
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
				updateQrcode();
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
