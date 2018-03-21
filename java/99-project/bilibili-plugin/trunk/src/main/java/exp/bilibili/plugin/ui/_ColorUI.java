package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import exp.bilibili.plugin.envm.ChatColor;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 弹幕颜色选择窗口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _ColorUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -5691969159309932864L;

	private final static int WIDTH = 480;
	
	private final static int HEIGHT = 120;
	
	private JButton whiteBtn;
	
	private JButton redBtn;
	
	private JButton blueBtn;
	
	private JButton purpleBtn;
	
	private JButton cyanBtn;
	
	private JButton greenBtn;
	
	private JButton yellowBtn;
	
	private JButton orangeBtn;
	
	private JButton pinkBtn;
	
	private JButton peachPinkBtn;
	
	private JButton goldBtn;
	
	protected _ColorUI() {
		super("弹幕颜色", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.whiteBtn = new JButton("●");
		whiteBtn.setForeground(ChatColor.WHITE.COLOR());
		whiteBtn.setToolTipText(ChatColor.WHITE.ZH());
		
		this.redBtn = new JButton("●");
		redBtn.setForeground(ChatColor.RED.COLOR());
		redBtn.setToolTipText(ChatColor.RED.ZH());
		
		this.blueBtn = new JButton("●");
		blueBtn.setForeground(ChatColor.BLUE.COLOR());
		blueBtn.setToolTipText(ChatColor.BLUE.ZH());
		
		this.purpleBtn = new JButton("●");
		purpleBtn.setForeground(ChatColor.PURPLE.COLOR());
		purpleBtn.setToolTipText(ChatColor.PURPLE.ZH());
		
		this.cyanBtn = new JButton("●");
		cyanBtn.setForeground(ChatColor.CYAN.COLOR());
		cyanBtn.setToolTipText(ChatColor.CYAN.ZH());
		
		this.greenBtn = new JButton("●");
		greenBtn.setForeground(ChatColor.GREEN.COLOR());
		greenBtn.setToolTipText(ChatColor.GREEN.ZH());
		
		this.yellowBtn = new JButton("●");
		yellowBtn.setForeground(ChatColor.YELLOW.COLOR());
		yellowBtn.setToolTipText(ChatColor.YELLOW.ZH());
		
		this.orangeBtn = new JButton("●");
		orangeBtn.setForeground(ChatColor.ORANGE.COLOR());
		orangeBtn.setToolTipText(ChatColor.ORANGE.ZH());
		
		this.pinkBtn = new JButton("●");
		pinkBtn.setForeground(ChatColor.PINK.COLOR());
		pinkBtn.setToolTipText(ChatColor.PINK.ZH());
		
		this.peachPinkBtn = new JButton("●");
		peachPinkBtn.setForeground(ChatColor.PEACH_PINK.COLOR());
		peachPinkBtn.setToolTipText(ChatColor.PEACH_PINK.ZH());
		
		this.goldBtn = new JButton("●");
		goldBtn.setForeground(ChatColor.GOLD.COLOR());
		goldBtn.setToolTipText(ChatColor.GOLD.ZH());
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.getHGridPanel(
				whiteBtn, redBtn, blueBtn, purpleBtn, cyanBtn, greenBtn, 
				yellowBtn, orangeBtn, pinkBtn, peachPinkBtn, goldBtn
		), BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		whiteBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.WHITE);
			}
		});
		
		redBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.RED);
			}
		});

		blueBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.BLUE);
			}
		});

		purpleBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.PURPLE);
			}
		});

		cyanBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.CYAN);
			}
		});

		greenBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.GREEN);
			}
		});

		yellowBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.YELLOW);
			}
		});

		orangeBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.ORANGE);
			}
		});

		pinkBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.PINK);
			}
		});
		
		peachPinkBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.PEACH_PINK);
			}
		});

		goldBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUI.getInstn().updateChatColor(ChatColor.GOLD);
			}
		});
	}
	
	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}

}
