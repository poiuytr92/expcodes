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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import FunctionModule.UserPanelManager;
import Tool.DBTable;
import Tool.SqlToJava;

/*
 * 账户 - 增改窗口
 */
public class UserWindow_Add extends JFrame {

	private static final long serialVersionUID = 7871150747793481499L;

	private int winWidth = 250;
	
	private int winHeight = 300;
	
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight/2);
	
	private JPanel mainPanel;
	
	private GridBagLayout gridBagLayout;

	private JTextField userNameField;

	private JPasswordField passwordField;

	private JComboBox cbIdentity;

	private JPasswordField passwordCField;
	
	private UserPanelManager userPanelManager;
	
	private int row;
	
	private boolean isUpdate=false;
	
	private String oldUsername;
	
	public UserWindow_Add(UserPanelManager um) {
		super("增加账户");
		userPanelManager = um;
		isUpdate = false;
		initWindow();
	}
	
	public UserWindow_Add(UserPanelManager um, int row) {

		super("修改账户");
		userPanelManager = um;
		isUpdate = true;
		this.row = row;
		initWindow();
		
	}
	
	private void initWindow() {
		this.setSize(winWidth, winHeight);
		this.setLocation(LocationX, LocationY);
		this.setResizable(false);
		
		
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
		
		JLabel userNameLabel = new JLabel("账 户：");
		userNameField = new JTextField(22);
		
		JLabel passwordLabel = new JLabel("密 码：");
		passwordField = new JPasswordField(22);
		
		JLabel passwordCLabel = new JLabel("确 认：");
		passwordCField = new JPasswordField(22);
		
		JLabel identityLabel = new JLabel("身 份：");
		
		cbIdentity = new JComboBox();
		cbIdentity.addItem("经理");
		cbIdentity.addItem("采购员");
		cbIdentity.addItem("仓管员");
		cbIdentity.addItem("销售员");
		cbIdentity.setPreferredSize(new Dimension(140, 22));
		
		if (isUpdate) {
			Vector<Object> rowData = userPanelManager.tableDateModel.get(row);
			oldUsername = (String)rowData.get(1);
			userNameField.setText(oldUsername);
			cbIdentity.setSelectedItem(rowData.get(3));
		}
		
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String userName = userNameField.getText();
				
				if (userName==null || "".equals(userName)) {
					JOptionPane.showMessageDialog(null, "用户名不能为空!");
					return;
				}
				
				if (!userName.equals(oldUsername)) {
					//检查账户是否重复
					Vector<Vector<Object>> data = SqlToJava.select(DBTable.username, "*", "username", userName);
					if (data.size() != 0) {
						JOptionPane.showMessageDialog(null, "用户名已存在!");
						return;
					}
				}
				
				String password = String.valueOf(passwordField.getPassword());
				String passwordC = String.valueOf(passwordCField.getPassword());
				
				if (password==null || "".equals(password)) {
					JOptionPane.showMessageDialog(null, "密码不能为空!");
					return;
				} else if (!password.equals(passwordC)) {
					passwordField.setText("");
					passwordCField.setText("");
					JOptionPane.showMessageDialog(null, "两次输入的密码不相同!");
					return;
				}
				
				String role = (String) cbIdentity.getSelectedItem();
				
				if (isUpdate) {
					SqlToJava.update(DBTable.username, "username", oldUsername, "username, password, identity", userName, password, role);
					JOptionPane.showMessageDialog(null, "修改账户成功!");
				} else {
					
					
					SqlToJava.insert(DBTable.username, "username, password, identity", userName, password, role);
					
					JOptionPane.showMessageDialog(null, "添加账户成功!");
				}
				
				userPanelManager.checkAll();
				
				
				closeWindow();
			}
			
		});
		
		JButton cancelBtn = new JButton("取消");
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeWindow();
			}
			
		});
		
		addGridBagComponent(userNameLabel, 0, 0, 1, 1);		//"账户"标签
		addGridBagComponent(userNameField, 0, 1, 10, 1);	//"账户"文本框
		
		addGridBagComponent(new JLabel(" "), 1, 0, 1, 1);	//空行
		
		addGridBagComponent(passwordLabel, 2, 0, 1, 1);		//"密码"标签
		addGridBagComponent(passwordField, 2, 1, 10, 1);	//"密码"输入框
		
		addGridBagComponent(new JLabel(" "), 3, 0, 1, 1);	//空行
		
		addGridBagComponent(passwordCLabel, 4, 0, 1, 1);		//"确认密码"标签
		addGridBagComponent(passwordCField, 4, 1, 10, 1);	//"确认密码"输入框
		
		addGridBagComponent(new JLabel(" "), 5, 0, 1, 1);	//空行
		
		addGridBagComponent(identityLabel, 6, 0, 1, 1);		
		addGridBagComponent(cbIdentity, 6, 1, 10, 1);			
		
		addGridBagComponent(new JLabel(" "), 7, 0, 1, 1);	//空行
		addGridBagComponent(new JLabel(" "), 8, 0, 1, 1);	//空行
		addGridBagComponent(new JLabel(" "), 9, 0, 1, 1);	//空行
		
		addGridBagComponent(okBtn, 10, 0, 3, 1);			//"确定"按钮
		JLabel lb = new JLabel(" ");
		lb.setPreferredSize(new Dimension(80, 22));
		addGridBagComponent(lb, 10, 1, 3, 1);
		addGridBagComponent(cancelBtn, 10, 8, 3, 1);			//"取消"按钮
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