package windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import FunctionModule.UserPanelManager;

/*
 * 账户 - 查询窗口
 */
public class UserWindow_Check extends JFrame {

    private static final long serialVersionUID = -5845185369191601530L;

	private int winWidth = 350;
	
	private int winHeight = 200;
	
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight/2);
	
	private JPanel mainPanel;
	
	private GridBagLayout gridBagLayout;	//网格包布局管理器
	
	private JTextField tfUsername;
	
	private JComboBox cbIdentity;
	
	private UserPanelManager userPanelManager;
	
	public UserWindow_Check(UserPanelManager um) {
		super("帐号查询");
		this.setSize(winWidth, winHeight);
		this.setLocation(LocationX, LocationY);
		this.setResizable(false);
		
		this.userPanelManager = um;
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.white);
		this.getContentPane().add(mainPanel);
		
		gridBagLayout = new GridBagLayout();
		mainPanel.setLayout(gridBagLayout);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
		
		initComponents();
		
		this.setVisible(true);
	}
	
	private void closeWindow() {
		setVisible(false);
		dispose();
	}
	
	private void initComponents() {
		
		JLabel lbCheckWays = new JLabel("选择查询方式：");
		
		tfUsername = new JTextField(25);
		tfUsername.setEditable(true);
		
		cbIdentity = new JComboBox();
		cbIdentity.addItem("经理");
		cbIdentity.addItem("采购员");
		cbIdentity.addItem("仓管员");
		cbIdentity.addItem("销售员");
		cbIdentity.setPreferredSize(new Dimension(155, 22));
		cbIdentity.setEnabled(false);
		
		JRadioButton checkUsernameRBtn = new JRadioButton("按用户名查询", true);
		checkUsernameRBtn.setBackground(Color.white);
		checkUsernameRBtn.setPreferredSize(new Dimension(100, 22));
		checkUsernameRBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tfUsername.setEditable(true);
				cbIdentity.setEnabled(false);
			}
			
		});
		
		JRadioButton checkIdentityRBtn = new JRadioButton("按身份查询");
		checkIdentityRBtn.setBackground(Color.white);
		checkIdentityRBtn.setPreferredSize(new Dimension(100, 22));
		checkIdentityRBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tfUsername.setEditable(false);
				cbIdentity.setEnabled(true);
			}
			
		});
		
		JRadioButton checkAllRBtn = new JRadioButton("查询全部");
		checkAllRBtn.setBackground(Color.white);
		checkAllRBtn.setPreferredSize(new Dimension(100, 22));
		checkAllRBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tfUsername.setEditable(false);
				cbIdentity.setEnabled(false);
			}
			
		});
		
		final ButtonGroup radioBtnGroup = new ButtonGroup();
		radioBtnGroup.add(checkUsernameRBtn);
		radioBtnGroup.add(checkAllRBtn);
		radioBtnGroup.add(checkIdentityRBtn);
		
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
//				OkEvent();
				int index = 0;
				Enumeration<AbstractButton> e = radioBtnGroup.getElements();
				while(e.hasMoreElements()) {
					if(((JRadioButton)e.nextElement()).isSelected() == false) {
						index++;
						continue;
					}
					break;
				}
				
				switch(index) {
				case 0:
					userPanelManager.checkWithCondition(tfUsername.getText(), 0);
					break;
				case 1:
					userPanelManager.checkAll();
					break;
				case 2:
					userPanelManager.checkWithCondition((String)cbIdentity.getSelectedItem(), 2);
					break;
				default:
					JOptionPane.showMessageDialog(null, "请选择查询方式！");
					return;
				}
				closeWindow();
			}
			
		});
		
		JButton cancelBtn = new JButton("取消");
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
//				cancelBtnEvent();
				closeWindow();
			}
			
		});
		
		addGridBagComponent(lbCheckWays, 0, 0, 1, 1);	
		
		addGridBagComponent(new JLabel(" "), 1, 0, 1, 1);	
		
		addGridBagComponent(checkUsernameRBtn, 2, 0, 1, 1);		
		addGridBagComponent(tfUsername, 2, 1, 10, 1);	
		
//		addGridBagComponent(new JLabel(" "), 3, 0, 1, 1);
		
		addGridBagComponent(checkIdentityRBtn, 4, -1, 1, 1);		
		addGridBagComponent(cbIdentity, 4, 1, 10, 1);
		
//		addGridBagComponent(new JLabel(" "), 5, 0, 1, 1);
		
		addGridBagComponent(checkAllRBtn, 6, 0, 1, 1);		
		
		addGridBagComponent(new JLabel(" "), 6, 1, 3, 1);
		
		addGridBagComponent(new JLabel(" "), 7, 0, 1, 1);
		
		addGridBagComponent(okBtn, 8, 0, 3, 1);		
		addGridBagComponent(new JLabel(" "), 8, 5, 3, 1);
		addGridBagComponent(cancelBtn, 8, 8, 3, 1);		
		return;
	}

	//利用网格包gridBagLayout约束组件位置及其占用的行数和列数
	//并把组件component添加到mainPanel
	private void addGridBagComponent(Component component, int row, int col, int nRow, int nCol) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();	//网格包容器
		gridBagConstraints.gridx = col;
		gridBagConstraints.gridy = row;
		gridBagConstraints.gridwidth = nRow;
		gridBagConstraints.gridheight = nCol;
		gridBagConstraints.ipadx = 2;
		gridBagLayout.setConstraints(component, gridBagConstraints);		//设置布局约束
		mainPanel.add(component);
		return;
	}
}


/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */
