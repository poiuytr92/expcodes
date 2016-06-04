package Tool;

import java.util.Vector;

/*
 * 表格操作
 */

public class TableOperate {

	//设置表头
	public static Vector<String> setColumnHead(String[] colName) {
		Vector<String> colHead = new Vector<String>();
		for(int i=0; i<colName.length; i++) {
			colHead.add(i, colName[i]);
		}
		return colHead;
	}
	
	//初始化表格的数据模型
	//modelSize为表格行数
	public static Vector<Vector<Object>> initTableDateModel(int modelSize) {
		Vector<Vector<Object>> tableDateModel = new Vector<Vector<Object>>();
		for(int pr=0; pr<modelSize; pr++) {
			tableDateModel.add(new Vector<Object>());
		}
		return tableDateModel;
	}
	
	//为表格的数据模型添加行数
	public static void addTableDateModelRow(Vector<Vector<Object>> tableDateModel, int rowNum) {
		
		if (tableDateModel.get(0).size() == 0) {
			
			for(int pr=0; pr<rowNum; pr++) {
				tableDateModel.add(new Vector<Object>());
			}
			
		} else {
			int rowDataSize = tableDateModel.get(0).size();
			
			for(int pr=0; pr<rowNum; pr++) {
				Vector<Object> rowData = new Vector<Object>();
				for (int i=0; i<rowDataSize; i++) {
					rowData.add(null);
				}
				tableDateModel.add(rowData);
			}
		}
		
		return;
	}
	
	//获取当前表格的总行数
	public static int getTableSize(Vector<Vector<Object>> tableDateModel) {
		return tableDateModel.size();
	}
	
	//获取当前表格的总行数
	public static int getTableSize(TableComponents tableModel) {
		return tableModel.TableLength;
	}
	
	//从startRow行开始清空表格数据
	public static boolean cleanTableDate(Vector<Vector<Object>> tableDateModel, int startRow) {
		
		int tableSize = tableDateModel.size();
		if(startRow >= tableSize) {
			return false;
		}
		
		
		for(int r=startRow; r<tableSize; r++) {
			Vector<Object> row = tableDateModel.get(r);
			
			for (int i=0; i<row.size(); i++) {
				row.set(i, null);
			}
		}
		return true;
	}
	
	//修改表格第row行的数据
	public static boolean updateTableDate(Vector<Vector<Object>> tableDateModel, Vector<Object> newDate, int row) {
		
		int tableSize = tableDateModel.size();
		if(row >= tableSize) {
			return false;
		}
		
		tableDateModel.set(row, newDate);
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
