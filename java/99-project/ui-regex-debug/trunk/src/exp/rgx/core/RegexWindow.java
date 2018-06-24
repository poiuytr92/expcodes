package exp.rgx.core;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/**
 * <PRE>
 * 主界面
 * </PRE>
 * <B>PROJECT：</B> ui-regex-debug
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-06-01
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class RegexWindow extends JFrame {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = -7679341006767970768L;

	/**
	 * 屏幕宽度
	 */
	private final int winWidth = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	/**
	 * 屏幕高度
	 */
	private final int winHigh = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	/**
	 * 界面初始宽度
	 */
	private int width = 400;
	
	/**
	 * 界面初始高度
	 */
	private int high = 375;
	
	/**
	 * 某次匹配后的正则对象
	 */
	private RegexEntity regexObj;
	
	/**
	 * 根面板
	 */
	private JPanel rootPanel;
	
	/**
	 * 原始内容的文本区
	 */
	private JTextArea contentTA;
	
	/**
	 * 正则表达式文本框
	 */
	private JTextField regexTF;
	
	/**
	 * 匹配结果的文本区
	 */
	private JTextArea matchTA;
	
	/**
	 * 匹配按钮
	 */
	private JButton reMthBtn;
	
	/**
	 * 显示总匹配项数的标签
	 */
	private JLabel totalMthLb;

	/**
	 * 总匹配项数的文字
	 */
	private final String totalMthMsg = "总匹配项数: ";
	
	/**
	 * 显示当前匹配项数的标签
	 */
	private JLabel curMthLb;
	
	/**
	 * 当前匹配项数的文字
	 */
	private final String curMthMsg = "当前匹配项: ";
	
	/**
	 * 显示上一个匹配项的按钮
	 */
	private JButton backBtn;
	
	/**
	 * 显示下一个匹配项的按钮
	 */
	private JButton nextBtn;
	
	/**
	 * 下拉框组件：显示当前的group号
	 */
	private JComboBox grpComboBox;
	
	/**
	 * 界面构造函数
	 * @param winName 界面窗口名称
	 */
	public RegexWindow(String winName) {
		super(winName);
		this.regexObj = new RegexEntity();
		this.setSize(width, high);
		this.setLocation((winWidth / 2 - width / 2), 
				(winHigh / 2 - high / 2));
		this.rootPanel = new JPanel(new GridLayout(2, 1));
		this.setContentPane(rootPanel);
		
		initComponents();
		setComponentsListener();
		
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/**
	 * 初始化界面组件
	 */
	private void initComponents() {
		//输入区布局面板：Border相对布局方式布置输入组件
		JPanel inputPanel = new JPanel(new BorderLayout());
		
		contentTA = new JTextArea();
		contentTA.setLineWrap(true);	//换行
		{
			JPanel contentPanel = new JPanel();
			contentPanel.setBorder(new TitledBorder("原始内容"));
			contentPanel.setLayout(new GridLayout(1,1));
			{
				JPanel panel = new JPanel(new BorderLayout());
				panel.setLayout(new BorderLayout());
				panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
				panel.add(new JScrollPane(contentTA), BorderLayout.CENTER);
				contentPanel.add(panel, 0);
			}
			inputPanel.add(contentPanel, BorderLayout.CENTER);
		}
		
		regexTF = new JTextField();
		reMthBtn = new JButton("开始匹配");
		{
			JPanel regexPanel = new JPanel();
			regexPanel.setBorder(new TitledBorder("正则表达式"));
			regexPanel.setLayout(new BorderLayout());
			{
				JPanel txtPanel = new JPanel(new BorderLayout());
				txtPanel.setLayout(new BorderLayout());
				txtPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
				txtPanel.add(regexTF, BorderLayout.CENTER);
				
				JPanel btnPanel = new JPanel(new GridLayout(1, 1));
				btnPanel.add(reMthBtn, 0);
				
				regexPanel.add(txtPanel, BorderLayout.CENTER);
				regexPanel.add(btnPanel, BorderLayout.EAST);
			}
			inputPanel.add(regexPanel, BorderLayout.SOUTH);
		}
		
		rootPanel.add(inputPanel, 0);
		
		//////////////////////////////////////////////////////////////
		//输出区布局面板：Border相对布局方式布置输出组件
		JPanel outputPanel = new JPanel(new BorderLayout());
		
		matchTA = new JTextArea();
		matchTA.setLineWrap(true);	//换行
		{
			JPanel matchPanel = new JPanel();
			matchPanel.setBorder(new TitledBorder("匹配结果"));
			matchPanel.setLayout(new GridLayout(1,1));
			{
				JPanel panel = new JPanel(new BorderLayout());
				panel.setLayout(new BorderLayout());
				panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
				panel.add(new JScrollPane(matchTA), BorderLayout.CENTER);
				matchPanel.add(panel, 0);
			}
			outputPanel.add(matchPanel, BorderLayout.CENTER);
		}
		
		totalMthLb = new JLabel(totalMthMsg + "0");
		curMthLb = new JLabel(curMthMsg + "0");
		backBtn = new JButton("上一次匹配");
		nextBtn = new JButton("下一次匹配");
		grpComboBox = new JComboBox(new String[]{"group 0"});
		{
			JPanel opPanel = new JPanel(new BorderLayout());
			opPanel.setBorder(new TitledBorder("操作面板"));
			opPanel.setLayout(new GridLayout(5, 1));
			{
				opPanel.add(totalMthLb, 0);
				opPanel.add(curMthLb, 1);
				opPanel.add(backBtn, 2);
				opPanel.add(nextBtn, 3);
				opPanel.add(grpComboBox, 4);
			}
			outputPanel.add(opPanel, BorderLayout.EAST);
		}
		
		rootPanel.add(outputPanel, 1);
	}

	/**
	 * 重置 显示当前的group号 的下拉框组件
	 * 每次重新匹配时都会触发
	 * @return 是否重置成功
	 */
	private boolean resetGroupCBItem() {
		boolean mthSucc = false;
		List<List<String>> mthList = regexObj.getMthList();
		
		if(mthList.size() > 0) {
			int grpNum = mthList.get(regexObj.getCurFindNum()).size();
			
			grpComboBox.removeAllItems();
			for(int i = 0; i < grpNum; i++) {
				grpComboBox.addItem("group " + i);
			}
			mthSucc = true;
		} else {
			mthSucc = false;
		}
		return mthSucc;
	}
	
	/**
	 * 设置各个组件的监听动作
	 */
	private void setComponentsListener() {
		reMthBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				reMthBtn.setText("重新匹配");
				
				regexObj.redoMatch(contentTA.getText(), regexTF.getText());
				totalMthLb.setText(totalMthMsg + regexObj.getTotalFindNum());
				curMthLb.setText(curMthMsg + (regexObj.getCurFindNum() + 1));
				
				if(true == resetGroupCBItem()) {
					matchTA.setText(regexObj.getGroup(0, 0));
				} else {
					matchTA.setText("[ERROR] 正则匹配失败.");
				}
			}
		});
		
		backBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				regexObj.setCurFindNum(regexObj.backFindIndex());
				curMthLb.setText(curMthMsg + (regexObj.getCurFindNum() + 1));
				
				String mthRst = regexObj.getGroup(regexObj.getCurFindNum(), 
						grpComboBox.getSelectedIndex());
				matchTA.setText(mthRst != null ? mthRst : 
					"[ERROR] 正则匹配失败.");
			}
		});
		
		nextBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				regexObj.setCurFindNum(regexObj.nextFindIndex());
				curMthLb.setText(curMthMsg + (regexObj.getCurFindNum() + 1));

				String mthRst = regexObj.getGroup(regexObj.getCurFindNum(), 
						grpComboBox.getSelectedIndex());
				matchTA.setText(mthRst != null ? mthRst : 
					"[ERROR] 正则匹配失败.");
			}
		});
		
		grpComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String mthRst = regexObj.getGroup(regexObj.getCurFindNum(), 
						grpComboBox.getSelectedIndex());
				matchTA.setText(mthRst != null ? mthRst : 
					"[ERROR] 正则匹配失败.");
			}
		});
	}
	
}
