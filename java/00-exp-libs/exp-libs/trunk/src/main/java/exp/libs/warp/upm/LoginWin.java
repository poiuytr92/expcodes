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

import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

class LoginWin extends MainWindow {

	public static void main(String[] args) {
		BeautyEyeUtils.init();
		new LoginWin();
	}
	
	/** serialVersionUID */
	private static final long serialVersionUID = -1752327112586227761L;

	protected final static int HIGH = 250;
	
	protected final static int WIDTH = 330;
	
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
			
			public void windowIconified(WindowEvent e) { 
				helpWin._hide();	// 窗口最小化时隐藏帮助面板
			}
			
			public void windowDeiconified(WindowEvent e) {
				helpWin._view();	// 窗口还原时显示帮助面板  FIXME 且按钮未按下, 覆写_view方法
			}
			
		});
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.helpWin = new HelpWin();
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(toUnPwPanel(), BorderLayout.CENTER);
		
		JButton btn = new JButton("︾");
		btn.setPreferredSize(new Dimension(WIDTH, 15));	// 设置按钮高度
		rootPanel.add(btn, BorderLayout.SOUTH);
		
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				helpWin._view(); // FIXME
			}
		});
	}

	private JPanel toUnPwPanel() {
		JPanel panel = new JPanel(new GridLayout(6, 1)); {
			panel.add(new JLabel(), 0);
			panel.add(SwingUtils.getPairsPanel("账号"), 1);
			panel.add(new JLabel(), 2);
			panel.add(SwingUtils.getPairsPanel("密码"), 3);
			panel.add(new JLabel(), 4);
			panel.add(toLogRegPanel(), 5);
		} SwingUtils.addBorder(panel);
		return panel;
	}
	
	private JPanel toLogRegPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 5)); {
			panel.add(new JLabel(), 0);
			panel.add(new JButton("登陆"), 1);
			panel.add(new JLabel(), 2);
			panel.add(new JButton("注册"), 3);
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
				helpWin.setLocation((int) point.getX(),(int) (point.getY() + WIDTH));
			}

		});
	}

}
