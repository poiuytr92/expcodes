package exp.libs.warp.upm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.FrameBorderStyle;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

class LoginWin extends MainWindow {

	public static void main(String[] args) {
		BeautyEyeUtils.init(FrameBorderStyle.translucencySmallShadow);
		new LoginWin();
	}
	
	/** serialVersionUID */
	private static final long serialVersionUID = -1752327112586227761L;

	protected final static int HIGH = 210;
	
	protected final static int WIDTH = 330;
	
	private final static String FOLD = "︽", OPEN = "︾";
	
	private JButton helpBtn;
	
	private HelpWin helpWin;
	
	protected LoginWin() {
		super("登陆", WIDTH, HIGH);
	}
	
	@Override
	protected void initCloseWindowMode() {
		_view();	// 默认显示窗口
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(SwingUtils.confirm("Exit ?")) {
					_hide();
					System.exit(0);
				}
			}
			
			// 窗口最小化时隐藏帮助面板
			public void windowIconified(WindowEvent e) { 
				helpWin._hide();
			}
			
			// 窗口还原时, 若帮助面板此前已展开则重新显示
			public void windowDeiconified(WindowEvent e) {
				if(FOLD.equals(helpBtn.getText())) {
					helpWin._view();
				}
			}
			
		});
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.helpBtn = new JButton(OPEN);
		this.helpWin = new HelpWin();
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(toUnPwPanel(), BorderLayout.CENTER);
		
		helpBtn.setPreferredSize(new Dimension(WIDTH, 15));	// 设置按钮高度
		rootPanel.add(helpBtn, BorderLayout.SOUTH);
		
		helpBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(OPEN.equals(helpBtn.getText())) {
					helpWin._view();
					helpBtn.setText(FOLD);
					
				} else {
					helpWin._hide();
					helpBtn.setText(OPEN);
				}
			}
		});
	}

	private JPanel toUnPwPanel() {
		JPanel panel = new JPanel(new GridLayout(6, 1)); {
			panel.add(new JLabel(), 0);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [账号] :  "), new JTextField(), 
					new JLabel("   ")), 1);
			panel.add(new JLabel(), 2);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [密码] :  "), new JPasswordField(), 
					new JLabel("   ")), 3);
			panel.add(new JLabel(), 4);
			panel.add(toLogRegPanel(), 5);
		} SwingUtils.addBorder(panel);
		return panel;
	}
	
	private JPanel toLogRegPanel() {
		JButton loginBtn = new JButton("登陆");
		JButton registBtn = new JButton("注册");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, loginBtn, registBtn);
		
		JPanel panel = new JPanel(new GridLayout(1, 5)); {
			panel.add(new JLabel(), 0);
			panel.add(loginBtn, 1);
			panel.add(new JLabel(), 2);
			panel.add(registBtn, 3);
			panel.add(new JLabel(), 4);
		}
		return panel;
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		
		// 黏着效果
		this.addComponentListener(new ComponentAdapter() {
			
			//当登陆窗口移动时，帮助面板跟随移动
			@Override
			public void componentMoved(ComponentEvent e) {
				Point point = getLocation();
				helpWin.setLocation((int) point.getX(),(int) (point.getY() + HIGH));
			}

			//当展开帮助面板时，必显示在登陆窗口下方
			@Override
			public void componentShown(ComponentEvent e) {
				Point point = getLocation();
				helpWin.setLocation((int) point.getX(),(int) (point.getY() + HIGH));
			}

		});
	}

}
