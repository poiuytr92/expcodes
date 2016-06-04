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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import FunctionModule.StorePanelManager;

/*
 * 库存 - 查询窗口
 */
public class StoreWindow_Check extends JFrame {

    private static final long serialVersionUID = -5845185369191601530L;

	private int winWidth = 350;
	
	private int winHeight = 200;
	
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight/2);
	
	private JPanel mainPanel;
	
	private GridBagLayout gridBagLayout;	//网格包布局管理器
	
	private JTextField tfConditionValue;
	
	private StorePanelManager storePanelManager;
	
	private JComboBox cbCondition;
	
	public StoreWindow_Check(StorePanelManager pm) {
		super("仓库查询");
		this.setSize(winWidth, winHeight);
		this.setLocation(LocationX, LocationY);
		this.setResizable(false);
		
		this.storePanelManager = pm;
		
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
		
		
		
		cbCondition = new JComboBox();
		cbCondition.addItem("货品名称");
		cbCondition.addItem("货品类别");
		cbCondition.addItem("供应商");
		cbCondition.addItem("查询全部");
		cbCondition.setPreferredSize(new Dimension(95, 22));
		
		tfConditionValue = new JTextField(25);
		
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = cbCondition.getSelectedIndex();
				
				switch(index) {
				case 0:
					storePanelManager.checkWithCondition(1, tfConditionValue.getText());
					break;
				case 1:
					storePanelManager.checkWithCondition(2, tfConditionValue.getText());
					break;
				case 2:
					storePanelManager.checkWithCondition(4, tfConditionValue.getText());
					break;
				case 3:
					storePanelManager.checkAll();
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
				closeWindow();
			}
			
		});
		
		addGridBagComponent(lbCheckWays, 0, 0, 1, 1);	
		
		addGridBagComponent(new JLabel(" "), 1, 0, 1, 1);	
		
		addGridBagComponent(cbCondition, 2, 0, 1, 1);		
		addGridBagComponent(tfConditionValue, 2, 1, 10, 1);	
		
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
