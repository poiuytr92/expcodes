package windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Tool.DBTable;
import Tool.LinkToDB;
import Tool.SqlToJava;

/*
 * 登陆窗口
 */
public class LoginWindow extends JFrame {

	private static final long serialVersionUID = -4206567819227462488L;

	private int winWidth = 280;
	
	private int winHeight = 220;
	
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight);
	
	private JPanel mainPanel;
	
	private JPanel infoPanel;
	
	private boolean isOpenExtern = false;	//标记辅助窗口是否打开
	
	private HelpWindow helpWindow;			//辅助窗口
	
	private GridBagLayout gridBagLayout;	//网格包布局管理器
	
	private JTextField userNameField;
	
	private JPasswordField passwordField;
	
	private ButtonGroup radioBtnGroup;		//单选按钮组
	
	public LoginWindow() {
		super("进销存管理系统 - 登陆");
		initWindow();
	}
	
	public LoginWindow(String winTitle) {
		super(winTitle);
		initWindow();
	}
	
	private void initWindow() {
		this.setSize(winWidth, winHeight);
		this.setLocation(LocationX, LocationY);
		this.setResizable(false);
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.white);
		this.getContentPane().add(mainPanel);
		mainPanel.setLayout(new BorderLayout());
		
		infoPanel = new JPanel();
		infoPanel.setBackground(Color.white);
		gridBagLayout = new GridBagLayout();
		infoPanel.setLayout(gridBagLayout);
		
		mainPanel.add(infoPanel, "Center");
		helpWindow = new HelpWindow(winWidth, winHeight);
		
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
				LinkToDB.disConnection();		//关闭与数据库的连接
				helpWindow.setVisible(false);
				e.getWindow().setVisible(false);
				helpWindow.dispose();
				e.getWindow().dispose();
				System.exit(0);
			}
			
			//窗口最小化时隐藏帮助面板
			public void windowIconified(WindowEvent e) { 
				helpWindow.setVisible(false);
			}
			
			//窗口还原时显示帮助面板
			public void windowDeiconified(WindowEvent e) {
				helpWindow.setVisible(true);
			}
			
		});
		
		//黏着效果监听
		this.addComponentListener(new ComponentAdapter() {
			
			//当登陆窗口移动时，帮助面板跟随移动
			@Override
			public void componentMoved(ComponentEvent e) {
				Point point = getLocation();
				helpWindow.setLocation((int) point.getX(),(int) (point.getY()+winHeight));
			}

			//当展开帮助面板时，必显示在登陆窗口下方
			@Override
			public void componentShown(ComponentEvent e) {
				Point point = getLocation();
				helpWindow.setLocation((int) point.getX(),(int) (point.getY()+winHeight));
			}

		});
		
		initComponents();
		this.setVisible(true);
		return;
	}

	private void initComponents() {
		
		JLabel userNameLabel = new JLabel("账 户：");
		userNameField = new JTextField(29);
		
		JLabel passwordLabel = new JLabel("密 码：");
		passwordField = new JPasswordField(29);
		passwordField.setEchoChar('*');	//设置回显字符
		passwordField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					loginEvent();
				}
			}
		});
		
		JRadioButton managerRBtn = new JRadioButton("经理");
		managerRBtn.setBackground(Color.white);
		JRadioButton buyRBtn = new JRadioButton("采购员");
		buyRBtn.setBackground(Color.white);
		JRadioButton wareRBtn = new JRadioButton("仓管员");
		wareRBtn.setBackground(Color.white);
		JRadioButton saleRBtn = new JRadioButton("销售员");
		saleRBtn.setBackground(Color.white);
		
		radioBtnGroup = new ButtonGroup();
		radioBtnGroup.add(managerRBtn);
		radioBtnGroup.add(buyRBtn);
		radioBtnGroup.add(wareRBtn);
		radioBtnGroup.add(saleRBtn);
		
		JButton loginBtn = new JButton("登陆");
		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				loginEvent();
			}
			
		});
		
		JButton cancelBtn = new JButton("取消");
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancelBtnEvent();
			}
			
		});
		
		addGridBagComponent(new JLabel(" "), 0, 0, 1, 1);	//空行
		addGridBagComponent(userNameLabel, 1, 0, 1, 1);		//"账户"标签
		addGridBagComponent(userNameField, 1, 1, 10, 1);	//"账户"文本框
		addGridBagComponent(new JLabel(" "), 2, 0, 1, 1);	//空行
		addGridBagComponent(passwordLabel, 3, 0, 1, 1);		//"密码"标签
		addGridBagComponent(passwordField, 3, 1, 10, 1);	//"密码"输入框
		addGridBagComponent(new JLabel(" "), 4, 0, 1, 1);	//空行
		addGridBagComponent(managerRBtn, 5, 0, 1, 1);		//"经理"单选按钮
		addGridBagComponent(buyRBtn, 5, 2, 1, 1);			//"采购员"单选按钮
		addGridBagComponent(wareRBtn, 5, 4, 1, 1);			//"仓管员"单选按钮
		addGridBagComponent(saleRBtn, 5, 6, 1, 1);			//"销售员"单选按钮
		addGridBagComponent(new JLabel(" "), 6, 0, 1, 1);	//空行
		addGridBagComponent(loginBtn, 7, 0, 3, 1);			//"登陆"按钮
		addGridBagComponent(cancelBtn, 7, 4, 3, 1);			//"取消"按钮
		addGridBagComponent(new JLabel(" "), 8, 0, 1, 1);	//空行
		
		//展开按钮，查看帮助信息
		final JButton externBtn = new JButton("︾");
		externBtn.setPreferredSize(new Dimension(winWidth, 15));
		externBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isOpenExtern == false) {
					isOpenExtern = true;
					externBtn.setText("︽");
					helpWindow.setVisible(true);
				}
				else {
					isOpenExtern = false;
					externBtn.setText("︾");
					helpWindow.setVisible(false);
				}
			}
			
		});
		mainPanel.add(externBtn, "South");
		return;
	}

	//利用网格包gridBagLayout约束组件位置及其占用的行数和列数
	//并把组件component添加到infoPanel
	private void addGridBagComponent(Component component, int row, int col, int nRow, int nCol) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();	//网格包容器
		gridBagConstraints.gridx = col;
		gridBagConstraints.gridy = row;
		gridBagConstraints.gridwidth = nRow;
		gridBagConstraints.gridheight = nCol;
		gridBagLayout.setConstraints(component, gridBagConstraints);		//设置布局约束
		infoPanel.add(component);
		return;
	}

	//登陆事件
	protected void loginEvent() {
		String userName = userNameField.getText();
		String password = String.valueOf(passwordField.getPassword());
		String role = "";
		
		int roleIndex = 0;
		Enumeration<AbstractButton> e = radioBtnGroup.getElements();
		while(e.hasMoreElements()) {
			if(((JRadioButton)e.nextElement()).isSelected() == false) {
				roleIndex++;
				continue;
			}
			break;
		}
		switch(roleIndex) {
			case 0:
				role = "经理";
				break;
			case 1:
				role = "采购员";
				break;
			case 2:
				role = "仓管员";
				break;
			case 3:
				role = "销售员";
				break;
			case 4:
				JOptionPane.showMessageDialog(null, "请选择登陆身份！");
				return;
		}
		
		//根据角色身份从数据库筛选，查找帐密键值对的结果集
		Vector<Vector<Object>> resultList = SqlToJava.select(DBTable.username, "*", "username", userName);
		
		if (resultList.size() == 0) {
			JOptionPane.showMessageDialog(null, "用户名不存在！");
			return;
		}
		
		//检查输入的帐密对是否与结果集中的某条记录一致
		for(List<Object> objectList : resultList) {
			if(!objectList.get(2).equals(password)) {
				JOptionPane.showMessageDialog(null, "密码错误！");
				break;
			}
			
			if(!objectList.get(3).equals(role)) {
				JOptionPane.showMessageDialog(null, "请选择适当的身份！");
				break;
			}

			helpWindow.setVisible(false);	//隐藏帮助面板
			this.setVisible(false);			//隐藏登录窗口
			helpWindow.dispose();			//注销帮助面板组件
			this.dispose();					//注销登陆窗口组件
			new MainWindow("进销存管理系统", role, userName);	//打开主窗口
			JOptionPane.showMessageDialog(null, "登陆成功！欢迎使用本系统！");
			return;
			
		}
		
		return;
	}

	//取消登陆事件
	protected void cancelBtnEvent() {
		LinkToDB.disConnection();	//关闭与数据库的连接
		helpWindow.setVisible(false);
		this.setVisible(false);
		helpWindow.dispose();
		this.dispose();
		System.exit(0);
	}
}



/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */