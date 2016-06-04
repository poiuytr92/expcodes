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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import windows.ContainerWindow;
import windows.PartnerWindow_Add;
import windows.PartnerWindow_Check;
import Tool.DBTable;
import Tool.DateUnit;
import Tool.SqlToJava;
import Tool.TabelToExcel;
import Tool.TableComponents;
import Tool.TableOperate;
import Tool.VFlowLayout;

/*
 * 合作伙伴管理
 */
public class PartnerPanelManager {

	private JPanel mainPanel;
	
	private JPanel westPanel;
	
	private JPanel centerPanel;
	
	public Vector<Vector<Object>> tableDateModel;
	
	private TableComponents tableModel;
	
	private String partnerType;
	
	private String workType;
	
	private Vector<Object> updateData;
	
	private ContainerWindow window;
	
	private JLabel supplierLabel;
	
	public PartnerPanelManager(JPanel userPanel, String partnerType, Object...objects) {
		
		if (objects != null && objects.length > 0) {
			this.workType = (String)objects[0];
			
			if ("客户".equals(partnerType)) {
				this.updateData = (Vector<Object>)objects[1];
			} else if ("供应商".equals(partnerType)){
				this.supplierLabel = (JLabel)objects[1];
			}
			
			this.window = (ContainerWindow)objects[2];
		}
		
		this.mainPanel = userPanel;
		mainPanel.setLayout(new BorderLayout());
		
		this.partnerType = partnerType;
		
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
		
		JButton checkBtn = new JButton("查询"+partnerType);
		
		checkBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new PartnerWindow_Check(PartnerPanelManager.this);
				
			}
			
		});
		
		JButton addBtn = new JButton("增加"+partnerType);
		
		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new PartnerWindow_Add(PartnerPanelManager.this, partnerType);
			}
			
		});
		
		JButton updateBtn = new JButton("修改"+partnerType);
		
		updateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				if (!isSelectOne()) {
					return;
				}
				
				int row = tableModel.getSelectedRow();
				new PartnerWindow_Add(PartnerPanelManager.this, partnerType, row);

			}
			
		});
		
		JButton deletcBtn = new JButton("删除"+partnerType);
		
		deletcBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//是：0   否：1  取消：2
				int flag = JOptionPane.showConfirmDialog(null, "是否删除所选"+partnerType+"？");
				
				if (flag == 0) {//选择了是
					
					int[] rows = tableModel.getSelectedRows();
					for (int row : rows) {
						
						String name = (String) tableDateModel.get(row).get(1);
						if (name != null) {
							
							SqlToJava.delete(DBTable.partner, "name, type", name, partnerType);
						}
					}
					
					checkAll();
				}
			}
			
		});
		
		JButton exportBtn = new JButton("导出"+partnerType);
		
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
		            
		            String[] colHead = {"序号", partnerType+"名称", "联系地址", "联系电话", "E-mail", "传真"};
	            	TabelToExcel.createExcel(partnerType+"表", colHead, tableDateModel, -1, path);
		            
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
				
				String name = (String)tableDateModel.get(row).get(1);
				
				if ("客户".equals(partnerType)) {
					
					updateData.set(8, name);
					SqlToJava.update(DBTable.bill, "id", updateData.get(0), "partner, date", name, DateUnit.getCurrentYMD());
				} else {
//					updateData.set(4, name);
//					SqlToJava.u pdate(DBTable.goods, "id", updateData.get(8), "partner", name);
					supplierLabel.setText(name);
				}
				
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
		if ("选择".equals(workType)) {
			addBtnToPanel(westPanel, okBtn, 100, 40);
			addBtnToPanel(westPanel, cancleBtn, 100, 40);
		} else {
			addBtnToPanel(westPanel, addBtn, 100, 40);
			addBtnToPanel(westPanel, updateBtn, 100, 40);
			addBtnToPanel(westPanel, deletcBtn, 100, 40);
			addBtnToPanel(westPanel, exportBtn, 100, 40);
		}
		
		addBtnToPanel(westPanel, refreshBtn, 100, 40);
		return;
	}

	private void initTable() {
		centerPanel.setLayout(new BorderLayout());
		
		String[] colName = {"序号", partnerType+"名称", "联系地址", "联系电话", "E-mail", "传真"};
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
		
		String selectAttributes = "name, address, tel, email, fax";
		Vector<Vector<Object>> data = SqlToJava.select(DBTable.partner, selectAttributes, "type", partnerType);
		
		for (int i=0; i<data.size(); i++) {
			data.get(i).insertElementAt(i+1, 0);//插入序号
		}
		
		if (tableDateModel.size() < (data.size()+5)) {
			TableOperate.addTableDateModelRow(tableDateModel, data.size()-tableDateModel.size()+10);
		}
		
		for (int i=0; i<data.size(); i++) {
			TableOperate.updateTableDate(tableDateModel, data.get(i), i);
		}
		
		TableOperate.cleanTableDate(tableDateModel, data.size());
		tableModel.repaint();
	}
	
	//通过名称查询
	public void checkWithCondition(String condition) {
		
		if (condition == null) {
			TableOperate.cleanTableDate(tableDateModel, 0);
		}
		
		String selectAttributes = "name, address, tel, email, fax";
		Vector<Vector<Object>> data = SqlToJava.select(DBTable.partner, selectAttributes, "name, type", condition, partnerType);
		
		
		int row = 0;
		for (Vector<Object> rowData : data) {
			if (condition.equals(rowData.get(0))) {//获取对应列的数据
				
				rowData.insertElementAt(row+1, 0);//插入序号
				TableOperate.updateTableDate(tableDateModel, rowData, row);
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