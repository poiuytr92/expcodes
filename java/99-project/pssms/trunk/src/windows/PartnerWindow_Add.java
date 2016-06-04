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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import FunctionModule.PartnerPanelManager;
import Tool.DBTable;
import Tool.SqlToJava;

/*
 * 供应商/客户 - 增改窗口
 */
public class PartnerWindow_Add extends JFrame {

	private static final long serialVersionUID = 7871150747793481499L;

	private int winWidth = 250;
	
	private int winHeight = 300;
	
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight/2);
	
	private JPanel mainPanel;
	
	private GridBagLayout gridBagLayout;

	private PartnerPanelManager partnerPanelManager;
	
	private int row;
	
	private boolean isUpdate=false;
	
	private String oldName;
	
	private String oldAddress;
	
	private String oldTel;
	
	private String oldEmail;
	
	private String oldFax;
	
	private String partnerType;
	
	public PartnerWindow_Add(PartnerPanelManager pm, String partnerType) {
		super("增加"+partnerType);
		partnerPanelManager = pm;
		this.partnerType = partnerType;
		isUpdate = false;
		
		initWindow();
	}
	
	public PartnerWindow_Add(PartnerPanelManager pm, String partnerType, int row) {

		super("修改"+partnerType);
		partnerPanelManager = pm;
		this.partnerType = partnerType;
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
		
		JLabel nameLabel = new JLabel(" 名 称：");
		final JTextField nameField = new JTextField(22);
		
		JLabel addressLabel = new JLabel(" 地 址：");
		final JTextField addressField = new JTextField(22);
		
		JLabel telLabel = new JLabel(" 电 话：");
		final JTextField telField = new JTextField(22);
		
		JLabel emailLabel = new JLabel("E-mail：");
		final JTextField emailField = new JTextField(22);
		
		JLabel faxLabel = new JLabel(" 传 真：");
		final JTextField faxField = new JTextField(22);
		
		if (isUpdate) {
			Vector<Object> rowData = partnerPanelManager.tableDateModel.get(row);
			oldName = (String) rowData.get(1);
			oldAddress = (String) rowData.get(2);
			oldTel = (String) rowData.get(3);
			oldEmail = (String) rowData.get(4);
			oldFax = (String) rowData.get(5);
			
			nameField.setText(oldName);
			addressField.setText(oldAddress);
			telField.setText(oldTel);
			emailField.setText(oldEmail);
			faxField.setText(oldFax);
		}
		
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String attributes = "name, address, tel, email, fax, type";
				
				String name = nameField.getText();
				String address = addressField.getText();
				String tel = telField.getText();
				String email = emailField.getText();
				String fax = faxField.getText();
				
				if (name==null || "".equals(name)) {
					JOptionPane.showMessageDialog(null, "名称不能为空!");
					return;
				}
				
				if (!name.equals(oldName)) {
					//检查是否重复
					Vector<Vector<Object>> data = SqlToJava.select(DBTable.partner, "name", "name, type", name, partnerType);
					if (data.size() != 0) {
						JOptionPane.showMessageDialog(null, partnerType+"已存在!");
						return;
					}
				}
				
				
				if (isUpdate) {
					SqlToJava.update(DBTable.partner, "name", oldName, attributes, name, address, tel, email, fax, partnerType);
					JOptionPane.showMessageDialog(null, "修改成功!");
				} else {
					
					
					SqlToJava.insert(DBTable.partner, attributes, name, address, tel, email, fax, partnerType);
					
					JOptionPane.showMessageDialog(null, "添加成功!");
				}
				
				partnerPanelManager.checkAll();
				
				
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
		
		addGridBagComponent(nameLabel, 0, 0, 1, 1);			//"账户"标签
		addGridBagComponent(nameField, 0, 1, 10, 1);		//"账户"文本框
		addGridBagComponent(new JLabel(" "), 1, 0, 1, 1);	//空行
		
		addGridBagComponent(addressLabel, 2, 0, 1, 1);		//"地址"标签
		addGridBagComponent(addressField, 2, 1, 10, 1);		//"地址"文本框
		addGridBagComponent(new JLabel(" "), 3, 0, 1, 1);	//空行
		
		addGridBagComponent(telLabel, 4, 0, 1, 1);			//"电话"标签
		addGridBagComponent(telField, 4, 1, 10, 1);			//"电话"文本框
		addGridBagComponent(new JLabel(" "), 5, 0, 1, 1);	//空行
		
		addGridBagComponent(emailLabel, 6, 0, 1, 1);		//"邮箱"标签
		addGridBagComponent(emailField, 6, 1, 10, 1);		//"邮箱"文本框
		addGridBagComponent(new JLabel(" "), 7, 0, 1, 1);	//空行
		
		addGridBagComponent(faxLabel, 8, 0, 1, 1);			//"传真"标签
		addGridBagComponent(faxField, 8, 1, 10, 1);			//"传真"文本框
		addGridBagComponent(new JLabel(" "), 9, 0, 1, 1);	//空行
		
		addGridBagComponent(okBtn, 10, 0, 3, 1);			//"确定"按钮
		JLabel lb = new JLabel(" ");
		lb.setPreferredSize(new Dimension(80, 22));
		addGridBagComponent(lb, 10, 1, 3, 1);
		addGridBagComponent(cancelBtn, 10, 8, 3, 1);		//"取消"按钮
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
