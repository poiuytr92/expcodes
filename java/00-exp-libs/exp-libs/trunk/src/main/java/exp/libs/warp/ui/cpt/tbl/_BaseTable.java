package exp.libs.warp.ui.cpt.tbl;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * <PRE>
 * 表单组件
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
abstract class _BaseTable extends _TableRenderer {

	/** serialVersionUID */
	private static final long serialVersionUID = -6301415063691311781L;
	
	/** 表单默认列数 */
	private final static int DEFAULT_COL = 5;
	
	/** 表单默认行数 */
	private final static int DEFAULT_ROW = 100;
	
	/** 表单最多呈现的行数（超出范围只会显示前N行） */
	private int maxViewRow;
	
	/**
	 * 
	 * @param headers 表头（影响列数）
	 * @param maxViewRow 表单最多呈现的行数
	 */
	protected _BaseTable(List<String> headers, int maxViewRow) {
		super(toVector(headers), createDataContainer(headers, maxViewRow));
		this.maxViewRow = (maxViewRow <= 0 ? DEFAULT_ROW : maxViewRow);
	}
	
	/**
	 * 构造表�?
	 * @param headers
	 * @return
	 */
	private static Vector<String> toVector(List<String> headers) {
		Vector<String> vector = new Vector<String>();
		if(headers != null && headers.size() > 0) {
			for(String header : headers) {
				vector.add(header == null ? "" : header.trim());
			}
		} else {
			for(int i = 0; i < DEFAULT_COL; i++) {
				vector.add("NULL");
			}
		}
		return vector;
	}

	/**
	 * 构造一个固定大小的初始表单容器
	 * @return
	 */
	private static Vector<Vector<String>> createDataContainer(
			List<String> headers, int maxViewRow) {
		maxViewRow = (maxViewRow <= 0 ? DEFAULT_ROW : maxViewRow);
		int col = DEFAULT_COL;
		if(headers != null && headers.size() > 0) {
			col = headers.size();
		}
		
		Vector<Vector<String>> dataContainer = new Vector<Vector<String>>(maxViewRow);
		for(int r = 0; r < maxViewRow; r++) {
			dataContainer.add(getEmptyRow(col));
		}
		return dataContainer;
	}
	
	/**
	 * 获取空行
	 * @param col
	 * @return
	 */
	private static Vector<String> getEmptyRow(int col) {
		String[] array = new String[col];
		Arrays.fill(array, "");
		Vector row = new Vector(Arrays.asList(array));
		return row;
	}
	
	/**
	 * 刷新表单数据
	 * @param newDatas 新数�?
	 */
	protected void _reflash(Vector<Vector<String>> datas) {
		if(datas != null) {
			dataContainer.clear();
			dataContainer.addAll(datas);
			
			// 容器未满，用空行填充
			int size = maxViewRow - datas.size();
			if(size > 0) {
				for(int i = 0; i < size; i++) {
					dataContainer.add(getEmptyRow(COL_SIZE()));
				}
			}
		}
		this.repaint();
	}
	
	protected void _add(Vector<String> rowData, int rowIdx) {
		if(rowData == null) {
			return;
		}
		
		rowIdx = (rowIdx <= 0 ? 0 : rowIdx);
		rowIdx = (rowIdx >= ROW_SIZE() ? ROW_SIZE() : rowIdx);
		dataContainer.add(rowIdx, rowData);
		this.repaint();
	}
	
	public Vector<String> _del(int rowIdx) {
		Vector<String> rowData = null;
		if(!inRange(rowIdx)) {
			return rowData;
		}
		
		rowData = dataContainer.remove(rowIdx);
		this.repaint();
		return rowData;
	}
	
	protected Vector<String> _getRowData(int rowIdx) {
		Vector<String> rowData = new Vector<String>();
		if(inRange(rowIdx)) {
			rowData = dataContainer.get(rowIdx);
		}
		return rowData;
	}
	
	private boolean inRange(int rowIdx) {
		return (rowIdx >= 0 && rowIdx < ROW_SIZE());
	}
	
}
