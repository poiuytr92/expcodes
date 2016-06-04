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
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import FunctionModule.PartnerPanelManager;
import FunctionModule.StorePanelManager;
import Tool.DBTable;
import Tool.SqlToJava;

/*
 * 库存 - 增改窗口
 */
public class StoreWindow_Add extends JFrame {

	private static final long serialVersionUID = 7871150747793481499L;

	private int winWidth = 250;
	
	private int winHeight = 300;
	
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight/2);
	
	private JPanel mainPanel;
	
	private GridBagLayout gridBagLayout;

	private JTextField tfName;

	private JTextField tfType;

	private JButton btnSelectSupplier;

	private JTextField tfMeasureWord;
	
	private StorePanelManager storePanelManager;
	
	private int row;
	
	private boolean isUpdate=false;
	
	private int id;
	
	private JLabel supplierLabel;
	
	public StoreWindow_Add(StorePanelManager pm) {
		super("增加商品类别");
		storePanelManager = pm;
		isUpdate = false;
		initWindow();
	}
	
	public StoreWindow_Add(StorePanelManager pm, int row) {

		super("修改商品类别");
		storePanelManager = pm;
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
		
		JLabel lbName = new JLabel("名  称：");
		tfName = new JTextField(22);
		
		JLabel lbType = new JLabel("类  型：");
		tfType = new JTextField(22);
		
		JLabel lbMeasureWord = new JLabel("单  位：");
		tfMeasureWord = new JTextField(22);
		
		JLabel lbsupplier = new JLabel("供应商：");
		supplierLabel = new JLabel("请点击按钮选择供应商", SwingConstants.LEFT);
		supplierLabel.setBackground(Color.BLACK);
//		supplierLabel.setFont(new Font(null, Font.ITALIC, 12));
		
		btnSelectSupplier = new JButton("选择供应商");
		btnSelectSupplier.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel selectPanel = new JPanel();
				ContainerWindow window = new ContainerWindow("选择供应商", selectPanel);
				new PartnerPanelManager(selectPanel, "供应商", "选择", supplierLabel, window);
			}
			
		});
//		btnSelectSupplier.setPreferredSize(new Dimension(140, 22));
		
		if (isUpdate) {
			Vector<Object> rowData = storePanelManager.tableDateModel.get(row);
			id = (Integer)rowData.get(8);
			tfName.setText((String) rowData.get(1));
			tfType.setText((String) rowData.get(2));
			tfMeasureWord.setText((String) rowData.get(3));
			supplierLabel.setText((String) rowData.get(4));
		}
		
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String name = tfName.getText();
				String type = tfType.getText();
				String measureWord = tfMeasureWord.getText();
				String supplier = supplierLabel.getText();
				
				if (name==null || "".equals(name)) {
					JOptionPane.showMessageDialog(null, "名称不能为空!");
					return;
				}
				
				if (type==null || "".equals(type)) {
					JOptionPane.showMessageDialog(null, "类型不能为空!");
					return;
				}
				
				if (measureWord==null || "".equals(measureWord)) {
					JOptionPane.showMessageDialog(null, "单位不能为空!");
					return;
				}
				
				if (supplier==null || "".equals(supplier)) {
					JOptionPane.showMessageDialog(null, "供应商不能为空!");
					return;
				}
				
				
				String conditionAttribute = "name, type, measure_word, partner";
				
				Vector<Vector<Object>> data = SqlToJava.select(DBTable.goods, "id", conditionAttribute, name, type, measureWord, supplier);
				if (data.size() > 0) {
					JOptionPane.showMessageDialog(null, "商品已存在!");
					return;
				}
				
				if (isUpdate) {
					SqlToJava.update(DBTable.goods, "id", id, conditionAttribute, name, type, measureWord, supplier);
					JOptionPane.showMessageDialog(null, "修改商品成功!");
				} else {
					
					
					
					SqlToJava.insert(DBTable.goods, conditionAttribute, name, type, measureWord, supplier);
					
					data = SqlToJava.select(DBTable.goods, "id", conditionAttribute, name, type, measureWord, supplier);
					
					conditionAttribute = "amount, cost, new_price, goods_id";
					SqlToJava.insert(DBTable.warehouse, conditionAttribute, 0, 0, 0, data.get(0).get(0));
					
					JOptionPane.showMessageDialog(null, "添加商品成功!");
				}
				
				storePanelManager.checkAll();
				
				
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
		
		addGridBagComponent(lbName, 0, 0, 1, 1);
		addGridBagComponent(tfName, 0, 1, 10, 1);
		
		addGridBagComponent(new JLabel(" "), 1, 0, 1, 1);
		
		addGridBagComponent(lbType, 2, 0, 1, 1);
		addGridBagComponent(tfType, 2, 1, 10, 1);
		
		addGridBagComponent(new JLabel(" "), 3, 0, 1, 1);
		
		addGridBagComponent(lbMeasureWord, 4, 0, 1, 1);		
		addGridBagComponent(tfMeasureWord, 4, 1, 10, 1);
		
		addGridBagComponent(new JLabel(" "), 5, 0, 1, 1);	//空行
		
		addGridBagComponent(lbsupplier, 6, 0, 1, 1);
		addGridBagComponent(supplierLabel, 6, 1, 10, 1);

		addGridBagComponent(btnSelectSupplier, 8, 3, 1, 1);			
		
		addGridBagComponent(new JLabel(" "), 7, 0, 1, 1);	//空行
//		addGridBagComponent(new JLabel(" "), 8, 0, 1, 1);	//空行
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