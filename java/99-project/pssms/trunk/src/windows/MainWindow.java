package windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import FunctionModule.AuditPanelManager;
import FunctionModule.BuyPanelManager;
import FunctionModule.CheckPanelManager;
import FunctionModule.CustomerPanelManager;
import FunctionModule.SalePanelManager;
import FunctionModule.SupplierPanelManager;
import FunctionModule.UserPanelManager;
import FunctionModule.WarehousePanelManager;
import Tool.LinkToDB;

/*
 * 主窗口
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = -1739603142116585725L;

	private int winWidth = 800;
	
	private int winHeight = 600;
	
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight/2);
	
	private JPanel mainPanel;
	
	private String role = "经理";	//默认登陆系统的角色
	
	private String username = "jingli";
	
	private JTabbedPane functionCards;
	
	private CheckPanelManager checkPanelManager;
	
	public MainWindow() {
		super("进销存管理系统");
		initWindow();
	}
	
	public MainWindow(String winTitle, String role, String username) {
		super(winTitle);
		this.role = role;
		this.username = username;
		initWindow();
	}
	
	private void initWindow() {
		this.setSize(winWidth, winHeight);
		this.setLocation(LocationX, LocationY);
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.white);
		this.getContentPane().add(mainPanel);
		mainPanel.setLayout(new BorderLayout());
		
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
				e.getWindow().setVisible(false);
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		
		initComponents();
		this.setVisible(true);
		return;
	}

	private void initComponents() {
		
		functionCards = new JTabbedPane();
		
		new UserPanelManager( tabPanelAdder(
				functionCards, new JPanel(), new JLabel("账户管理"), 60, 40 ));
		new SupplierPanelManager( tabPanelAdder(
				functionCards, new JPanel(), new JLabel("供应商管理"), 70, 40 ));
		new CustomerPanelManager( tabPanelAdder( 
				functionCards, new JPanel(), new JLabel("客户管理"), 60, 40 ));
		new BuyPanelManager( tabPanelAdder(
				functionCards, new JPanel(), new JLabel("采购管理"), 60, 40 ));
		new SalePanelManager( tabPanelAdder(
				functionCards, new JPanel(), new JLabel("销售管理"), 60, 40 ));
		new WarehousePanelManager( tabPanelAdder(
				functionCards, new JPanel(), new JLabel("库存管理"), 60, 40 ));
		new AuditPanelManager( tabPanelAdder(
				functionCards, new JPanel(), new JLabel("单据审核"), 60, 40 ));
		checkPanelManager = new CheckPanelManager( tabPanelAdder(
				functionCards, new JPanel(), new JLabel("明细账查询"), 70, 40 ));
		
		// north panel
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		JButton switchBtn = new JButton("切换用户");
		switchBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//是：0   否：1  取消：2
				int flag = JOptionPane.showConfirmDialog(null, "确定要切换登陆用户吗？");
				if(flag == 0) {
					setVisible(false);	//隐藏主界面
					dispose();			//注销主界面组件
					new LoginWindow("进销存管理系统 - 登陆");	//打开登陆窗口
				}
			}
			
		});
		//根据角色权限为其配置功能选项卡
		Map<String, List<Integer>> roleToJob = new HashMap<String, List<Integer>>();
		roleToJob.put("经理", Arrays.asList(0, 1, 2, 6, 7));
		roleToJob.put("采购员", Arrays.asList(3));
		roleToJob.put("仓管员", Arrays.asList(5, 7));
		roleToJob.put("销售员", Arrays.asList(4));
		
		for(int i : new int[] {0, 1, 2, 3, 4, 5, 6, 7}) {
			if( ! roleToJob.get(role).contains(i) ) {
				functionCards.setEnabledAt(i, false);
			}
		}
		functionCards.setSelectedIndex( roleToJob.get(role).get(0) );
		functionCards.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if( functionCards.getSelectedIndex() == 7 )
					checkPanelManager.init();
			}
		});
		
		northPanel.add(new JLabel("登陆用户: "+username + "    身份: "+role, SwingConstants.CENTER), "Center");
		northPanel.add(switchBtn, "East");
		mainPanel.add(northPanel, "North");
		mainPanel.add(functionCards, "Center");
		return;
	}
	
	// Add panel to TabPanel and return the new panel
	private JPanel tabPanelAdder(JTabbedPane tabPanel, JPanel panel, JLabel tab, int width, int hight) {
		tabPanel.add(panel);
		tab.setHorizontalAlignment(SwingConstants.CENTER);
		tab.setPreferredSize(new Dimension(width, hight));
		tabPanel.setTabComponentAt(tabPanel.indexOfComponent(panel), tab);
		return panel;
	}
}


/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */
