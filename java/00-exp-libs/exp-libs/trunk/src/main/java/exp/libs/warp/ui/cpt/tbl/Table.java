package exp.libs.warp.ui.cpt.tbl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public abstract class Table extends _Table {

	/** serialVersionUID */
	private static final long serialVersionUID = 191838444436258900L;

	public Table(List<String> headers) {
		super(headers);
	}
	
	public Table(String[] headers) {
		super(headers != null ? Arrays.asList(headers) : null);
	}
	
	/**
	 * 刷新表单数据
	 */
	public void reflash() {
		super._reflash(null);
	}
	
	/**
	 * 刷新表单数据
	 * @param newDatas 新数据
	 */
	public void reflash(List<List<String>> datas) {
		if(datas == null) {
			super._reflash(null);
			
		} else {
			Vector<Vector<String>> vDatas = new Vector<Vector<String>>();
			for(List<String> rowData : datas) {
				vDatas.add(new Vector<String>(rowData));
			}
			super._reflash(vDatas);
		}
	}
	
	public void add(List<String> rowData, int rowIdx) {
		if(rowData == null) {
			return;
		}
		super._add(new Vector<String>(rowData), rowIdx);
	}
	
	public void addTop(List<String> rowData) {
		add(rowData, 0);
	}
	
	public void addBtm(List<String> rowData) {
		add(rowData, Integer.MAX_VALUE);
	}
	
	public void add(List<String> rowData) {
		addTop(rowData);
	}
	
	public List<String> del(int rowIdx) {
		List<String> rowData = new LinkedList<String>();
		Vector<String> delData = super._del(rowIdx);
		if(delData != null) {
			rowData.addAll(delData);
		}
		return rowData;
	}
	
	public List<String> getRowData(int rowIdx) {
		List<String> rowData = new LinkedList<String>();
		Vector<String> data = super._getRowData(rowIdx);
		if(data != null) {
			rowData.addAll(data);
		}
		return rowData;
	}
	
	public List<String> getSelectedRowData() {
		return getRowData(getCurSelectRow());
	}
	
}
