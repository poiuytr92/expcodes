package FunctionModule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import windows.BrightLittleBill;
import Tool.DateUnit;
import Tool.SqlToJava;
import Tool.TabelToExcel;
import Tool.TableComponents;
import Tool.TableOperate;
import Tool.VFlowLayout;

/*
 * 明细账查询
 */
public class CheckPanelManager {

	private JPanel mainPanel;
	private JPanel westPanel;
	private JPanel centerPanel;
	private TableComponents tableModel;
	private JTextField dateField;
	
	public CheckPanelManager(JPanel checkPanel) {
		this.mainPanel = checkPanel;
		mainPanel.setLayout(new BorderLayout());
		
		
		// Add Panel
		westPanel = new JPanel();
		centerPanel = new JPanel();
		westPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		centerPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		mainPanel.add(westPanel, "West");
		mainPanel.add(centerPanel, "Center");
		
		// Add Button
		westPanel.setLayout(new VFlowLayout());

		westPanel.add( new JLabel(" ") );
		westPanel.add( new JLabel("待查明细帐月份", JLabel.CENTER) );
		dateField = new JTextField( DateUnit.getCurrentYM() );
		dateField.setHorizontalAlignment(JTextField.CENTER);
		westPanel.add( dateField );
		westPanel.add( new JLabel(" ") );

		clickableBtn(westPanel, new JButton("查询明细帐"), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					if( tableModel.getSelectedRowCount() == 1) {
						
						Integer selectedId = Integer.valueOf(
								tableModel.getValueAt(tableModel.getSelectedRow(), 0).toString());
						String selectedName = 
								tableModel.getValueAt(tableModel.getSelectedRow(), 1).toString();
						BrightLittleBill win = new BrightLittleBill(
								tableModel.getValueAt(tableModel.getSelectedRow(), 2).toString()
								+ " - " + selectedName + " 的明细帐");
						String[] head = new String[] {"日期", "编号", "摘要",
								"收入数量", "收入金额", "支出数量", "支出金额",  "结存数量", "结存金额"};
						mkTable( win.mainPanel, head,
								mingSaiData( selectedId, dateField.getText() ));
						win.setVisible(true);
					}
					else
						JOptionPane.showMessageDialog(null, "请选择一张货单");
			}});

		clickableBtn(westPanel, new JButton("导出明细帐"), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( tableModel.getSelectedRowCount() == 1) {
					JFileChooser fc = new JFileChooser();
					int flag = fc.showSaveDialog(mainPanel);
			        if (flag == JFileChooser.APPROVE_OPTION){
			            File file=fc.getSelectedFile();
			            String path=file.getPath();
			            
			            if (!path.endsWith(".xls")) {
			            	path += ".xls";
			            }
			            Integer selectedId = Integer.valueOf(
								tableModel.getValueAt(tableModel.getSelectedRow(), 0).toString());
						String selectedName = 
								tableModel.getValueAt(tableModel.getSelectedRow(), 1).toString();
						String headName = 
								tableModel.getValueAt(tableModel.getSelectedRow(), 2).toString()
								+ " - " + selectedName + " 的明细帐";
						String[] head = new String[] {"日期", "编号", "摘要",
								"收入数量", "收入金额", "支出数量", "支出金额",  "结存数量", "结存金额"};
		            	TabelToExcel.createExcel( headName, head,
		            			mingSaiData( selectedId, dateField.getText() ), -1, path);
		  
			            JOptionPane.showMessageDialog(null, "导出成功！");
			        }
		        }
				else
					JOptionPane.showMessageDialog(null, "请选择一张货单");
			}});

	}

	public void init() {
		String[] head = new String[] {"商品编号", "商品名称",
				"商品分类", "计量单位", "进货均价", "结存数量", "结存金额"};
		tableModel = mkTable( centerPanel, head, baseData() );
	}

	// 基础数据生成函数
	private Vector<Vector<Object>> baseData() {
		Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
		Vector<Vector<Object>> goodsData = 
				SqlToJava.executeQuerty("select id, name, type, measure_word from goods");
		Vector<Vector<Object>> baseData = 
				SqlToJava.executeQuerty("select cost, amount, cost*amount from warehouse");
		for( int i=0 ; i < goodsData.size() ; i++) {
			Vector<Object> a = new Vector<Object>( goodsData.get(i) );
			a.addAll( baseData.get(i) );
			a.set( a.size()-1, (new DecimalFormat("#.##")).format(
					Float.valueOf(a.get(a.size()-1).toString())));
			tableData.add( a );
		}
		
		for( int line=0; line < 20; line ++)
			tableData.add( new Vector<Object>() );
		
		return tableData;
	}

	// 明细帐数据生成函数
	private Vector<Vector<Object>> mingSaiData(Integer goodsId, String month) {
		Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
		Vector<Vector<Object>> billData = SqlToJava.executeQuerty(
				"select date, id, type, partner, amount, price from bill "
				+" where goods_id=" + goodsId +" and status=" +"'同意'"+ " order by date");
		Float sumMoney = new Float(0);
		Integer lastGoods = new Integer(0);
		Integer sumGoods = new Integer(0);
		// Until last month
		for( Vector<Object> bill : billData)
			if( DateUnit.cmpDate( month, bill.get(0).toString().substring(0, 7)) >0 ) {
				String bc = bill.get(2).toString().substring(0, 2);
				if( bc.equals("进货")) {
					lastGoods += Integer.valueOf( bill.get(4).toString() );
					sumMoney += Float.valueOf( bill.get(4).toString() ) 
							* Float.valueOf( bill.get(5).toString() );
					sumGoods += Integer.valueOf( bill.get(4).toString() );
				}
				else {
					lastGoods -= Integer.valueOf( bill.get(4).toString() );
				}
			}
		Vector<Object> t = new Vector<Object>();
		t.add(" ");t.add(" ");t.add("上月结存");t.add(" ");t.add(" ");t.add(" ");t.add(" ");t.add( lastGoods );
		if( sumGoods == 0 )
			t.add( 0 );
		else
			t.add( (new DecimalFormat("#.##")).format( 
					sumMoney *  (lastGoods / Float.valueOf( sumGoods ))) );
		tableData.add( t );
//		tableData.add( new Vector<Object>() );
		
		// this month
		for( Vector<Object> bill : billData)
			if( DateUnit.cmpDate( month, bill.get(0).toString().substring(0, 7)) ==0 ) {
				Vector<Vector<Object>> goodsPartenerData = SqlToJava.executeQuerty(
						"select partner from goods where id=" + goodsId);
				String goodsPartner = goodsPartenerData.get(0).get(0).toString();
				
				Vector<Object> a = new Vector<Object>();
				a.add( bill.get(0).toString().split(" ")[0].split("-")[2]  );
				a.add( bill.get(1) );
				String bc = bill.get(2).toString().substring(0, 2);
				if( bc.equals("进货")) {
					a.add( bc +"于 "+ goodsPartner);
					lastGoods += Integer.valueOf( bill.get(4).toString() );
					sumMoney += Float.valueOf( bill.get(4).toString() ) 
							* Float.valueOf( bill.get(5).toString() );
					sumGoods += Integer.valueOf( bill.get(4).toString() );
					a.add( bill.get(4).toString() );
					a.add( Float.valueOf( bill.get(4).toString() ) 
							* Float.valueOf( bill.get(5).toString() ) );
					a.add( " " );
					a.add( " " );
				}
				else {
					a.add( bc +"于 "+ bill.get(3).toString() );
					lastGoods -= Integer.valueOf( bill.get(4).toString() );
					a.add( " " );
					a.add( " " );
					a.add( bill.get(4).toString() );
					a.add( Float.valueOf( bill.get(4).toString() ) 
							* Float.valueOf( bill.get(5).toString() ) );
				}
				a.add( lastGoods );
				a.add( " " );
				tableData.add( a );
			}
		
		
		// end of table
//		tableData.add( new Vector<Object>() );
		t = new Vector<Object>();
		t.add(" ");t.add(" ");t.add("本月结存");t.add(" ");t.add(" ");t.add(" ");t.add(" ");t.add( lastGoods );
		if( sumGoods == 0 )
			t.add( 0 );
		else
			t.add( (new DecimalFormat("#.##")).format( 
					sumMoney *  (lastGoods / Float.valueOf( sumGoods ))) );
		tableData.add( t );
		
		for( int line=0; line < 30; line ++) {
			t = new Vector<Object>();
			t.add("");
			tableData.add( t );
		}
			

		return tableData;
	}

	// Tools
	private TableComponents mkTable(JPanel panel, String [] colName, Vector<Vector<Object>> data) {
		panel.removeAll();
		panel.setLayout(new BorderLayout());
		Vector<String> colHead = TableOperate.setColumnHead(colName);
		TableComponents newTableModel = new TableComponents(data, colHead);
		JScrollPane tablePanel = new JScrollPane(newTableModel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(tablePanel,  "Center");
		newTableModel.getColumnModel().getColumn(0).setMaxWidth(70);
		return newTableModel;
	}
	private JButton clickableBtn(JPanel panel, JButton btn, ActionListener a) {
		btn.setPreferredSize(new Dimension(100, 40));
		panel.add(btn);
		btn.addActionListener(a);
		return btn;
	}
}

/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */