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

import windows.UserWindow_Add;
import windows.UserWindow_Check;
import Tool.DBTable;
import Tool.SqlToJava;
import Tool.TabelToExcel;
import Tool.TableComponents;
import Tool.TableOperate;
import Tool.VFlowLayout;

/*
 * 账户管理
 */
public class UserPanelManager {

	private JPanel mainPanel;
	
	private JPanel westPanel;
	
	private JPanel centerPanel;
	
	public Vector<Vector<Object>> tableDateModel;
	
	private TableComponents tableModel;
	
	public UserPanelManager(JPanel userPanel) {
		this.mainPanel = userPanel;
		mainPanel.setLayout(new BorderLayout());
		
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
		
		JButton checkBtn = new JButton("查询账户");
		addBtnToPanel(westPanel, checkBtn, 100, 40);
		checkBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new UserWindow_Check(UserPanelManager.this);
				
			}
			
		});
		
		JButton addBtn = new JButton("增加账户");
		addBtnToPanel(westPanel, addBtn, 100, 40);
		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new UserWindow_Add(UserPanelManager.this);
			}
			
		});
		
		JButton updateBtn = new JButton("修改账户");
		addBtnToPanel(westPanel, updateBtn, 100, 40);
		updateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				if (!isSelectOne()) {
					
					return;
				}
				
				int row = tableModel.getSelectedRow();
				new UserWindow_Add(UserPanelManager.this, row);

			}
			
		});
		
		JButton deletcBtn = new JButton("删除账户");
		addBtnToPanel(westPanel, deletcBtn, 100, 40);
		deletcBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//是：0   否：1  取消：2
				int flag = JOptionPane.showConfirmDialog(null, "是否删除所选账户？");
				
				if (flag == 0) {//选择了是
					
					int[] rows = tableModel.getSelectedRows();
					for (int row : rows) {
						
						String username = (String) tableDateModel.get(row).get(1);
						if (username != null)
							SqlToJava.delete(DBTable.username, "username", username);
					}
					
					checkAll();
				}
			}
			
		});
		
		JButton exportBtn = new JButton("导出账户");
		addBtnToPanel(westPanel, exportBtn, 100, 40);
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
		            
		            String[] colHead = {"序号", "用户名", "密码", "身份"};
	            	TabelToExcel.createExcel("账户表", colHead, tableDateModel, -1, path);
	  
		            JOptionPane.showMessageDialog(null, "导出成功！");
		        }
			}
			
		});
		
		
		JButton refreshBtn = new JButton("刷新");
		addBtnToPanel(westPanel, refreshBtn, 100, 40);
		refreshBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				checkAll();
			}
			
		});
		
		return;
	}

	private void initTable() {
		centerPanel.setLayout(new BorderLayout());
		
		String[] colName = {"序号", "用户名", "密码", "身份"};
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
	
	//查询全部账户记录
	public void checkAll() {
		
		String selectAttributes = "username, password, identity";
		Vector<Vector<Object>> data = SqlToJava.select(DBTable.username, selectAttributes);
		
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
	
	//通过用户名或身份查询帐号记录，flag=0查询用户名，flag=2查询身份
	public void checkWithCondition(String condition, int flag) {
		
		if (condition == null) {
			TableOperate.cleanTableDate(tableDateModel, 0);
		}
		
		String selectAttributes = "username, password, identity";
		Vector<Vector<Object>> data = SqlToJava.select(DBTable.username, selectAttributes);
		
		
		int row = 0;
		for (Vector<Object> rowData : data) {
			if (condition.equals(rowData.get(flag))) {//获取对应列的数据
				
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