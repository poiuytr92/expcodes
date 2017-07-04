package exp.libs.warp.ui.cpt.tbl;

import java.util.Vector;

@SuppressWarnings("rawtypes")
public class Table<T> extends _Table<T> {

	/** serialVersionUID */
	private static final long serialVersionUID = -6301415063691311781L;
	
	private final static Vector DEFAULT_HEADER = new Vector();
	
	private final static Vector DEFAULT_DATAS = new Vector();
	
	private Vector<Vector<T>> datas;
	
	@SuppressWarnings("unchecked")
	public Table(Vector<T> header, Vector<Vector<T>> datas) {
		super(header == null ? DEFAULT_HEADER : header, 
				datas == null ? DEFAULT_DATAS : datas);
		this.datas = datas;
	}
	
	/**
	 * 刷新表单数据
	 */
	public void reflash() {
		reflash(null);
	}
	
	/**
	 * 刷新表单数据
	 * @param newDatas 新数据
	 */
	public void reflash(Vector<Vector<T>> newDatas) {
		if(datas == null) {
			return;
		}
		
		if(newDatas != null) {
			datas.clear();
			datas.addAll(newDatas);
		}
		this.repaint();
	}
	
	public void add(Vector<T> rowData, int rowIdx) {
		if(datas == null || rowData == null) {
			return;
		}
		
		rowIdx = (rowIdx <= 0 ? 0 : rowIdx);
		rowIdx = (rowIdx >= ROW_SIZE() ? ROW_SIZE() : rowIdx);
		datas.add(rowIdx, rowData);
		this.repaint();
	}
	
	public void addTop(Vector<T> rowData) {
		add(rowData, 0);
	}
	
	public void addBtm(Vector<T> rowData) {
		add(rowData, Integer.MAX_VALUE);
	}
	
	public void del(int rowIdx) {
		if(datas == null || !inRange(rowIdx)) {
			return;
		}
		
		datas.remove(rowIdx);
		this.repaint();
	}
	
	public Vector<T> getRowData(int rowIdx) {
		Vector<T> rowData = new Vector<T>();
		if(datas != null && inRange(rowIdx)) {
			rowData = datas.get(rowIdx);
		}
		return rowData;
	}
	
	public Vector<T> getSelectedRowData() {
		return getRowData(getCurSelectRow());
	}
	
	private boolean inRange(int rowIdx) {
		return (rowIdx >= 0 && rowIdx < ROW_SIZE());
	}
	
}
