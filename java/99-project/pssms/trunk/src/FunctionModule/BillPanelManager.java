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

import windows.BillWindow_Check;
import windows.ContainerWindow;
import Tool.DBTable;
import Tool.DateUnit;
import Tool.NumberFormat;
import Tool.SqlToJava;
import Tool.TabelToExcel;
import Tool.TableComponents;
import Tool.TableOperate;
import Tool.VFlowLayout;

/*
 * 单据
 */
public class BillPanelManager {

private JPanel mainPanel;
	
	private JPanel westPanel;
	
	private JPanel centerPanel;
	
	public Vector<Vector<Object>> tableDateModel;
	
	private TableComponents tableModel;
	
	private String workType;
	
	public BillPanelManager(JPanel billPanel, String workType) {
		this.mainPanel = billPanel;
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
		
		//共用
		JButton checkBtn = new JButton("查询");
		
		checkBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new BillWindow_Check(BillPanelManager.this);
				
			}
			
		});
		
		//属于审核功能
		JButton rejectBtn = new JButton("驳回");
		
		rejectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				audit("驳回");
			}
			
		});
		
		//属于审核功能
		JButton agreeBtn = new JButton("同意");
		agreeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				audit("同意");
			}
			
		});
		
		//属于进货,出货
		JButton submitBtn = new JButton("提交审核");
		submitBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!isSelectOne()) {
					return;
				}
				
				int row = tableModel.getSelectedRow();
				Vector<Object> rowData = tableDateModel.get(row);
				
				if (!( "编辑中".equals(rowData.get(10)) || 
						"驳回".equals(rowData.get(10)) )) {
					JOptionPane.showMessageDialog(null, "只能提交驳回和编辑中的货单");
					return;
				}
				
				if ("出货".equals(workType)) {
					if (rowData.get(8)==null || "".equals(rowData.get(8))) {
						
						JOptionPane.showMessageDialog(null, "请选择客户");
						return;
					}
				}
				
				SqlToJava.update(DBTable.bill, "id", rowData.get(0), "status, date", "待审核", DateUnit.getCurrentYMD());
				JOptionPane.showMessageDialog(null, "提交成功！");

				checkAll();
			}
			
		});
		
		//属于审核
		JButton lookBtn = new JButton("查看库存");
		lookBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel lookPanel = new JPanel();
				ContainerWindow window = new ContainerWindow("查看库存", lookPanel);
				new StorePanelManager(lookPanel, "查看", window);

			}
			
		});
		
		//属于进货,出货
		JButton addBillBtn = new JButton("新建"+workType+"单");
		addBillBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel selectPanel = new JPanel();
				ContainerWindow window = new ContainerWindow("选择商品", selectPanel);
				new StorePanelManager(selectPanel, workType, window, BillPanelManager.this, "增加");
			}
			
		});
		
		//属于进货,出货
		JButton updateBtn = new JButton("修改"+workType+"单");
		updateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!isSelectOne()) {
					return;
				}
				
				int row = tableModel.getSelectedRow();
				
				if (!( "编辑中".equals(tableDateModel.get(row).get(10)) || 
						"驳回".equals(tableDateModel.get(row).get(10)) )) {
					JOptionPane.showMessageDialog(null, "只能修改驳回和编辑中的货单");
					return;
				}
				JPanel selectPanel = new JPanel();
				ContainerWindow window = new ContainerWindow("选择商品", selectPanel);
				new StorePanelManager(selectPanel, workType, window, BillPanelManager.this, "修改", tableDateModel.get(row));
			}
			
		});
		
		//属于出货
		JButton selectCustomerBtn = new JButton("选择客户");
		selectCustomerBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!isSelectOne()) {
					return;
				}
				
				int row = tableModel.getSelectedRow();
				
				if (!( "编辑中".equals(tableDateModel.get(row).get(10)) || 
						"驳回".equals(tableDateModel.get(row).get(10)) )) {
					JOptionPane.showMessageDialog(null, "只能修改驳回和编辑中的货单");
					return;
				}
				
				JPanel selectPanel = new JPanel();
				ContainerWindow window = new ContainerWindow("选择客户", selectPanel);
				new PartnerPanelManager(selectPanel, "客户", "选择", tableDateModel.get(row), window);
			}
			
		});
		
		//属于进货,出货
		JButton deleteBtn = new JButton("删除"+workType+"单");
		deleteBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//是：0   否：1  取消：2
				int flag = JOptionPane.showConfirmDialog(null, "是否删除所选账单？");
				
				boolean canDelete = true;
				
				if (flag == 0) {//选择了是
					
					int[] rows = tableModel.getSelectedRows();
					for (int row : rows) {
						
						Integer id = (Integer) tableDateModel.get(row).get(0);
						if (id != null) {
							if (!( "编辑中".equals(tableDateModel.get(row).get(10)) || 
									"驳回".equals(tableDateModel.get(row).get(10)) )) {
								canDelete = false;
								continue;
							}
							SqlToJava.delete(DBTable.bill, "id", id);
						}
					}
					checkAll();
					if (!canDelete) {
						
						JOptionPane.showMessageDialog(null, "只能删除驳回和编辑中的货单");
					}
				}
			}
			
		});
		
		//共用
		JButton exportBtn = new JButton("导出");
		exportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				
				int flag = fc.showSaveDialog(mainPanel);//保存对话框
		        if (flag == JFileChooser.APPROVE_OPTION){
		        	
		            File file=fc.getSelectedFile();
		            String path=file.getPath();

		            if (!path.endsWith(".xls")) {
		            	path += ".xls";
		            }
		            
		            String[] colHead = {"货单号", "货单类型", "货品名称", "货品类别", "单位", "供应商", "单价", "数量", "客户", "日期", "货单状态"};
	            	TabelToExcel.createExcel(workType+"表单", colHead, tableDateModel, -1, path);
		            
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
		
		addBtnToPanel(westPanel, checkBtn, 100, 40);
		
		if ("审核".equals(workType)) {
			addBtnToPanel(westPanel, agreeBtn, 100, 40);
			addBtnToPanel(westPanel, rejectBtn, 100, 40);
			addBtnToPanel(westPanel, lookBtn, 100, 40);
		} else {//workType是出货或者入货
			
			addBtnToPanel(westPanel, addBillBtn, 100, 40);
			
			if ("出货".equals(workType)) {
				addBtnToPanel(westPanel, selectCustomerBtn, 100, 40);
			}
			
			addBtnToPanel(westPanel, updateBtn, 100, 40);
			addBtnToPanel(westPanel, deleteBtn, 100, 40);
			addBtnToPanel(westPanel, submitBtn, 100, 40);
			
		}
		
		addBtnToPanel(westPanel, exportBtn, 100, 40);
		addBtnToPanel(westPanel, refreshBtn, 100, 40);
		return;
	}

	private void initTable() {
		centerPanel.setLayout(new BorderLayout());
		
		String[] colName = {"货单号", "货单类型", "货品名称", "货品类别", "单位", "供应商", "单价", "数量", "客户", "日期", "货单状态"};
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
	
	private boolean isSelectOne() {
		if (tableModel.getSelectedRowCount() != 1) {
			
			JOptionPane.showMessageDialog(null, "请选择一张货单");
			return false;
		}
		
		int row = tableModel.getSelectedRow();
		if (tableDateModel.get(row).get(0) == null) {
			JOptionPane.showMessageDialog(null, "请选择一张货单");
			return false;
		}
		
		return true;
	}
	
	private void audit(String status) {

		if (!isSelectOne())
			return;
		
		//是：0   否：1  取消：2
		int flag = JOptionPane.showConfirmDialog(null, "是否"+status+"所选账单？");
		
		if (flag == 0) {//选择了是
			
			int row = tableModel.getSelectedRow();
			Vector<Object> rowData = tableDateModel.get(row);
			
			Double price = (Double)rowData.get(6);
			Integer amount = (Integer)rowData.get(7);
			
			if ("同意".equals(status)) {//同意后，更新仓库数据
				String attributes = "amount, cost, new_price";
				Vector<Vector<Object>> oldData = SqlToJava.select(DBTable.warehouse, attributes, "goods_id", rowData.get(11));
				
//				Double oldPrice = (Double)oldData.get(0).get(2);
				Double oldCost = (Double)oldData.get(0).get(1);
				Integer oldAmount = (Integer)oldData.get(0).get(0); 
				
				Integer newAmount=0;
				if ("进货单".equals(rowData.get(1))) {//进货
					newAmount = oldAmount+amount;
					Double newCost = (oldAmount*oldCost+amount*price)/(newAmount);
					
					SqlToJava.update(DBTable.warehouse, "goods_id", rowData.get(11), attributes, newAmount, NumberFormat.doubleFormat(newCost), NumberFormat.doubleFormat(price));
					
				} else if ("出货单".equals(rowData.get(1))){//销售
					newAmount = oldAmount-amount;
					if (newAmount < 0) {
						JOptionPane.showMessageDialog(null, "仓库货存不足，自动驳回！");
						status = "驳回";
						SqlToJava.update(DBTable.bill, "id", rowData.get(0), "status, date", status, DateUnit.getCurrentYMD());
						return;
					} else {
						
						SqlToJava.update(DBTable.warehouse, "goods_id", rowData.get(11), "amount", newAmount);
					}
				}
			}
			
			SqlToJava.update(DBTable.bill, "id", rowData.get(0), "status, date", status, DateUnit.getCurrentYMD());
			JOptionPane.showMessageDialog(null, "审核成功！");
			
			checkAll();
		}
	}
	
	//查询全部账户记录
	public void checkAll() {
		
		String workCondition="";
		String workConditionValue="";
		if ("审核".equals(workType)) {
			workCondition = "status";
			workConditionValue = "待审核";
		} else {
			workCondition = "type";
			workConditionValue = workType+"单";
		}
		
		Vector<Vector<Object>> data = SqlToJava.select(DBTable.bill, "*", workCondition, workConditionValue);
		
		if (tableDateModel.size() < (data.size()+5)) {
			TableOperate.addTableDateModelRow(tableDateModel, data.size()-tableDateModel.size()+10);
		}
		
		for (int i=0; i<data.size(); i++) {
			
			Vector<Vector<Object>> goodsData = SqlToJava.select(DBTable.goods, "name, type, measure_word, partner", "id", data.get(i).get(7));
//			data.get(i).remove(7);
			data.get(i).addAll(2, goodsData.get(0));
			TableOperate.updateTableDate(tableDateModel, data.get(i), i);
		}
		
		TableOperate.cleanTableDate(tableDateModel, data.size());
		tableModel.repaint();
	}
	
	//这里的condition其实就是表中的列的序号，查询第几列，值是多少
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
			
			String value1 = "";
			if (condition < 0)
				value1 += rowData.get(9);
			else
				value1 += rowData.get(condition);
			
			if (compare(value1, conditionValue, condition)) {//获取对应列的数据
//				rowData.insertElementAt(row+1, 0);//插入序号
				Vector<Object> updateData = new Vector<Object>(rowData);
				TableOperate.updateTableDate(tableDateModel, updateData, row);
				row++;
			}
		}
		
		TableOperate.cleanTableDate(tableDateModel, row);
		tableModel.repaint();
	}
	
	
	private boolean compare(String value1, String value2, int type) {
		
		if (type < 0) {
			Integer val = DateUnit.cmpDate(value1, value2);
			if (val == null) {
				return false;
				
			} else if (type == -1) {  //value1等于于value2
				if (val == 0)
					return true;
				
			} else if (type == -2) {   //value1迟于于value2
				if (val < 0)
					return true;
				
			} else if (type == -3) {   //value1迟于于value2
				if (val > 0)
					return true;
			}
		} else {
			
			if (value2.equals(value1)) {
				return true;
			}
		}
		return false;
	}

	private void addBtnToPanel(JPanel panel, JButton btn, int width, int height) {
		btn.setPreferredSize(new Dimension(width, height));
		panel.add(btn);
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