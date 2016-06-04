package FunctionModule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import windows.ContainerWindow;
import windows.StoreWindow_Add;
import windows.StoreWindow_Check;
import Tool.DBTable;
import Tool.DateUnit;
import Tool.NumberFormat;
import Tool.SqlToJava;
import Tool.TabelToExcel;
import Tool.TableComponents;
import Tool.TableOperate;
import Tool.VFlowLayout;

/*
 * 账户管理
 */
public class StorePanelManager {

	private JPanel mainPanel;
	
	private JPanel westPanel;
	
	private JPanel centerPanel;
	
	public Vector<Vector<Object>> tableDateModel;
	
	private TableComponents tableModel;
	
	private String workType;
	
	private Vector<Object> updateData;
	
	private ContainerWindow window;
	
	private String type;
	
	private BillPanelManager billPanelManager;
	
	public StorePanelManager(JPanel userPanel, String workType, Object...objects) {
		
		if (objects != null && objects.length > 0) {
			
			this.window = (ContainerWindow)objects[0];
			if (!"查看".equals(workType)) {
				billPanelManager = (BillPanelManager)objects[1];
				this.type = (String)objects[2];                //记录是增加还是修改
				
				if ("修改".equals(type)) {
					this.updateData = (Vector<Object>)objects[3];  //存储要更新的数据
				}
				
			}
		}
		
		this.mainPanel = userPanel;
		mainPanel.setLayout(new BorderLayout());
		
		this.workType = workType;
		
		westPanel = new JPanel();
		westPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		centerPanel = new JPanel();
		centerPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		mainPanel.add(westPanel, "West");
		mainPanel.add(centerPanel, "Center");
		
		initButton();
		initTable();
		
		checkAll();
	}
	

	private void initButton() {
		westPanel.setLayout(new VFlowLayout());
		
		JButton checkBtn = new JButton("查询");
		
		checkBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new StoreWindow_Check(StorePanelManager.this);
				
			}
			
		});
		
		JButton addBtn = new JButton("增加类别");
		
		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new StoreWindow_Add(StorePanelManager.this);
			}
			
		});
		
		JButton updateBtn = new JButton("修改类别");
		
		updateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				if (!isSelectOne()) {
					
					return;
				}
				
				int row = tableModel.getSelectedRow();
				new StoreWindow_Add(StorePanelManager.this, row);

			}
			
		});
		
		JButton deletcBtn = new JButton("删除类别");
		
		deletcBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//是：0   否：1  取消：2
				int flag = JOptionPane.showConfirmDialog(null, "是否删除所选商品类别？");
				
				if (flag == 0) {//选择了是
					
					int[] rows = tableModel.getSelectedRows();
					for (int row : rows) {
						
						Integer amount = (Integer) tableDateModel.get(row).get(5);
						if (amount!=null && amount != 0) {
							JOptionPane.showMessageDialog(null, "商品: "+tableDateModel.get(row).get(1)+" 还有货存，不能删除");
							return;
						}
						Integer id = (Integer) tableDateModel.get(row).get(8);
						if (id != null) {
							System.out.println(id);
							SqlToJava.delete(DBTable.warehouse, "goods_id", id);
							SqlToJava.delete(DBTable.goods, "id", id);
						}
					}
					
					checkAll();
				}
			}
			
		});
		
		JButton exportBtn = new JButton("导出库存");
		
		exportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				JFileChooser fc = new JFileChooser();
				
				int flag = fc.showSaveDialog(mainPanel);//保存对话框
		        if (flag == JFileChooser.APPROVE_OPTION){
		        	
		            File file=fc.getSelectedFile();
		            String path=file.getPath();
		            if (!path.endsWith(".xls")) {//确保后缀名为.txt
		            	path += ".xls";
		            }
		            
		            String[] colHead = {"序号", "货品名称", "货品类别", "单位", "供应商", "库存数量", "成本", "最新进货价"};
	            	TabelToExcel.createExcel("库存表", colHead, tableDateModel, -1, path);
		            
		            JOptionPane.showMessageDialog(null, "导出成功！");
		        }
			}
			
		});
		
		
		JButton refreshBtn = new JButton("刷新");
		
		refreshBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				checkAll();
			}
			
		});
		
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				

				if (!isSelectOne()) {
					return;
				}
				
				int row = tableModel.getSelectedRow();
				
				Double price = 0.0;
				Integer amount = 0;
				try {
					
					String input = JOptionPane.showInputDialog("请输入货品价格");
					price = Double.parseDouble(input);
					
					input = JOptionPane.showInputDialog("请输入货品数量");
					amount = Integer.parseInt(input);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "输入格式不对!");
					return;
				}
				
				if (price <= 0.0) {
					JOptionPane.showMessageDialog(null, "价格需要大于0！");
					return;
				}
				
				if (amount <= 0.0) {
					JOptionPane.showMessageDialog(null, "数量需要大于0！");
					return;
				}
				
				if ("出货".equals(workType) && amount > (Integer)tableDateModel.get(row).get(5)) {
					JOptionPane.showMessageDialog(null, "仓库库存不足!");
					return;
				}
				
				Integer goods_id = (Integer)tableDateModel.get(row).get(8);
				
				if ("增加".equals(type)) {
					String attributeValue = "type, price, amount, date, status, goods_id";
//					{"货单号", "货单类型", "货品名称", "货品分类", "单位", "供应商", "进货单价", "数量", "客户", "日期", "货单状态"};
					
					SqlToJava.insert(DBTable.bill, attributeValue, workType+"单", NumberFormat.doubleFormat(price), amount, DateUnit.getCurrentYMD(), "编辑中", goods_id);
				} else {
					
					SqlToJava.update(DBTable.bill, "id", updateData.get(0), "price, goods_id, amount, date", NumberFormat.doubleFormat(price), goods_id, amount, DateUnit.getCurrentYMD());
				}
				billPanelManager.checkAll();
				window.closeWindow();
			}
			
		});
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				window.closeWindow();
			}
			
		});
		
		
		addBtnToPanel(westPanel, checkBtn, 100, 40);
		if ("仓管".equals(workType)) {
			addBtnToPanel(westPanel, addBtn, 100, 40);
			addBtnToPanel(westPanel, updateBtn, 100, 40);
			addBtnToPanel(westPanel, deletcBtn, 100, 40);
			addBtnToPanel(westPanel, exportBtn, 100, 40);
		} else if ("查看".equals(workType)){
			addBtnToPanel(westPanel, cancleBtn, 100, 40);
		} else {//进货，出货
			
			addBtnToPanel(westPanel, okBtn, 100, 40);
			addBtnToPanel(westPanel, cancleBtn, 100, 40);
		}
		
		addBtnToPanel(westPanel, refreshBtn, 100, 40);
		return;
	}

	private void initTable() {
		centerPanel.setLayout(new BorderLayout());
		
		String[] colName = {"序号", "货品名称", "货品类别", "单位", "供应商", "库存数量", "成本", "最新进货价"};
		Vector<String> colHead = TableOperate.setColumnHead(colName);
		tableDateModel = TableOperate.initTableDateModel(50);
		
		tableModel = new TableComponents(tableDateModel, colHead);
		JScrollPane tablePanel = new JScrollPane(tableModel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerPanel.add(tablePanel,  "Center");
		tableModel.getColumnModel().getColumn(0).setMaxWidth(70);
		tableModel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

		});
	}
	
	//查询记录
	public void checkAll() {
		
		Vector<Vector<Object>> data = SqlToJava.select(DBTable.warehouse);
		
		if (tableDateModel.size() < (data.size()+5)) {
			TableOperate.addTableDateModelRow(tableDateModel, data.size()-tableDateModel.size()+10);
		}
		
		for (int i=0; i<data.size(); i++) {
			
			
			data.get(i).set(0, i+1);
			Vector<Vector<Object>> goodsData = SqlToJava.select(DBTable.goods, "name, type, measure_word, partner", "id", data.get(i).get(4));
			data.get(i).addAll(1, goodsData.get(0));
			
			TableOperate.updateTableDate(tableDateModel, data.get(i), i);
		}
		
		TableOperate.cleanTableDate(tableDateModel, data.size());
		tableModel.repaint();
	}
	
	//condition:列序号
	//conditionValue:该列的值
	public void checkWithCondition(Integer condition, String conditionValue) {
		
		if (conditionValue == null) {
			TableOperate.cleanTableDate(tableDateModel, 0);
		}
		
		checkAll();
		
		int row = 0;
		int size = tableDateModel.size();
		for (int i=0; i<size; i++) {
			Vector<Object> rowData = tableDateModel.get(i);
			
			if (rowData.get(1) == null)
				break;
			
			if (conditionValue.equals(rowData.get(condition))) {//获取对应列的数据
				
				Vector<Object> updateData = new Vector<Object>(rowData);
				TableOperate.updateTableDate(tableDateModel, updateData, row);
				row++;
			}
		}
		
		TableOperate.cleanTableDate(tableDateModel, row);
		tableModel.repaint();
	}

	private void addBtnToPanel(JPanel panel, JButton btn, int width, int height) {
		btn.setPreferredSize(new Dimension(width, height));
		panel.add(btn);
		return;
	}

	private boolean isSelectOne() {
		if (tableModel.getSelectedRowCount() != 1) {
			
			JOptionPane.showMessageDialog(null, "请选择一项");
			return false;
		}
		
		int row = tableModel.getSelectedRow();
		if (tableDateModel.get(row).get(0) == null) {
			JOptionPane.showMessageDialog(null, "请选择一项");
			return false;
		}
		
		return true;
	}
}

/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */