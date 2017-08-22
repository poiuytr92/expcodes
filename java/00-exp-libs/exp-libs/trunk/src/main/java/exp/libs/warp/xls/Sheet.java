package exp.libs.warp.xls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;

public class Sheet {

	private final CellStyle DEFAULT_DATETIME_STYLE;
	
	public final static Sheet NULL = new Sheet(null, false, null);
	
	/** sheet页 */
	private org.apache.poi.ss.usermodel.Sheet sheet;
	
	private boolean is2007;
	
	protected Sheet(org.apache.poi.ss.usermodel.Sheet sheet, 
			boolean is2007, CellStyle dataTimeStyle) {
		this.sheet = sheet;
		this.is2007 = is2007;
		this.DEFAULT_DATETIME_STYLE = dataTimeStyle;
	}

	public int MAX_ROW() {
		return (is2007 ? SpreadsheetVersion.EXCEL2007.getMaxRows() : 
				SpreadsheetVersion.EXCEL97.getMaxRows());
	}
	
	public int MAX_COL() {
		return (is2007 ? SpreadsheetVersion.EXCEL2007.getMaxColumns() : 
				SpreadsheetVersion.EXCEL97.getMaxColumns());
	}
	
	private boolean _inRange(int row, int col) {
		return (row >= 0 && row < MAX_ROW() && col >= 0 && col < MAX_COL());
	}
	
	private Row _createRow(int rowIdx) {
		Row row = sheet.getRow(rowIdx);
		if (row == null) {
			row = sheet.createRow(rowIdx);
		}
		return row;
	}
	
	private Cell _createCell(int rowIdx, int colIdx) {
		Cell cell = null;
		Row row = _createRow(rowIdx);
		if(row != null) {
			cell = row.getCell(colIdx);
			if (cell == null) {
				cell = row.createCell(colIdx);
			}
		}
		return cell;
	}
	
	/**
	 * 
	 * @param rowIdx 行索引(从0开始)
	 * @param colIdx 列索引(从0开始)
	 * @return
	 */
	private Cell _getCell(int rowIdx, int colIdx) {
		Cell cell = null;
		Row row = sheet.getRow(rowIdx);
		if (row != null) {
			cell = row.getCell(colIdx);
		}
		return cell;
	}
	
	public Object getVal(int row, int col) {
		if(isNull() || !_inRange(row, col)) {
			return null;
		}
		return _getVal(row, col);
	}
	
	private Object _getVal(int row, int col) {
		Cell cell = _getCell(row, col);
		if (cell == null) {
			return null;
		}

		// 若单元格的值类型是表达式，则修正值类型为表达式结果的值类型
		int valType = cell.getCellType();
		if (valType == Cell.CELL_TYPE_FORMULA) {
			valType = cell.getCachedFormulaResultType();
		}
		return _getVal(cell, valType);
	}

	private Object _getVal(Cell cell, int valType) {
		Object val = null;
		switch (valType) {
			case Cell.CELL_TYPE_STRING: {
				val = cell.getRichStringCellValue().getString();
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: {
				if (DateUtil.isCellDateFormatted(cell)) {
					val = cell.getDateCellValue();
					
				} else {
					double _double = cell.getNumericCellValue();
					int _int = (int) _double;
					long _long = (long) _double;
					val = (_int == _double ? _int : 
						(_long == _double ? _long : _double));
				}
				break;
			}
			case Cell.CELL_TYPE_BOOLEAN: {
				val = cell.getBooleanCellValue();
				break;
			}
			case Cell.CELL_TYPE_ERROR: {
				val = "#N/A";
				break;
			}
			case Cell.CELL_TYPE_BLANK: {
				val = null;
				break;
			}
			default: {
				val = null;
				break;
			}
		}
		return val;
	}
	
	public boolean setVal(int row, int col, Object val) {
		if(isNull() || !_inRange(row, col)) {
			return false;
		}
		return _setVal(row, col, val);
	}
	
	private boolean _setVal(int row, int col, Object val) {
		Cell cell = _createCell(row, col);
		if(cell == null) {
			return false;
		}
		
		if(val == null) {
			cell.setCellType(Cell.CELL_TYPE_BLANK);
			
		} else if (val instanceof Integer) {
			cell.setCellValue(((Integer) val).doubleValue());
			
		} else if (val instanceof Double) {
			cell.setCellValue(((Double) val).doubleValue());
			
		} else if (val instanceof Date) {
			cell.setCellValue((Date) val);
			cell.setCellStyle(DEFAULT_DATETIME_STYLE);
			
		} else if (val instanceof Boolean) {
			cell.setCellValue((Boolean) val);
			
		} else {
			cell.setCellValue(val.toString());
		}
		return true;
	}
	
	public boolean setHyperlink(int row, int col, String val, String url) {
		if(isNull() || !_inRange(row, col)) {
			return false;
		}
		
		Cell cell = _createCell(row, col);
		if(cell == null) {
			return false;
		}
		
		cell.setCellValue(val);
		Hyperlink hyperlink = (is2007 ? 
				new XSSFHyperlink(XSSFHyperlink.LINK_URL) : 
				new HSSFHyperlink(HSSFHyperlink.LINK_URL));
		hyperlink.setAddress(url);
		cell.setHyperlink(hyperlink);
		return true;
	}
	
	public void setStyle(int row, CellStyle style) {
		if(!isNull() && _inRange(row, 0)) {
			_createRow(row).setRowStyle(style);
		}
	}
	
	public void setStyle(int row, int col, CellStyle style) {
		if(!isNull() && _inRange(row, col)) {
			_createCell(row, col).setCellStyle(style);
		}
	}
	
	public List<List<Object>> getAllDatas(int bgnRow, int endRow) {
		List<List<Object>> tableDatas = new ArrayList<List<Object>>(2);
		if(isNull() == false) {
			tableDatas = getRowDatas(0, sheet.getLastRowNum());
		}
		return tableDatas;
	}
	
	public List<List<Object>> getRangeDatas(
			int bgnRow, int bgnCol, int endRow, int endCol) {
		List<List<Object>> tableDatas = new ArrayList<List<Object>>(2);
		if(isNull() || bgnRow > endRow || bgnCol > endCol || 
				!_inRange(bgnRow, bgnCol) || !_inRange(endRow, endCol)) {
			return tableDatas;
		}
		
		tableDatas = new ArrayList<List<Object>>(endRow - bgnRow + 1);
		for(int row = bgnRow; row <= endRow; row++) {
			List<Object> rowDatas = new ArrayList<Object>(endCol - bgnCol + 1);
			for(int col = bgnCol; col <= endCol; col++) {
				rowDatas.add(_getVal(row, col));
			}
			tableDatas.add(rowDatas);
		}
		return tableDatas;
	}
	
	public List<List<Object>> getRowDatas(int bgnRow, int endRow) {
		List<List<Object>> tableDatas = new ArrayList<List<Object>>(2);
		if(isNull() || bgnRow > endRow || 
				!_inRange(bgnRow, 0) || !_inRange(endRow, 0)) {
			return tableDatas;
		}
		
		tableDatas = new ArrayList<List<Object>>(endRow - bgnRow + 1);
		for(int row = bgnRow; row <= endRow; row++) {
			tableDatas.add(_getRowDatas(row));
		}
		return tableDatas;
	}
	
	public List<Object> getRowDatas(int row) {
		List<Object> rowDatas = new ArrayList<Object>(2);
		if(isNull() || !_inRange(row, 0)) {
			return rowDatas;
		}
		return _getRowDatas(row);
	}
	
	private List<Object> _getRowDatas(int row) {
		List<Object> rowDatas = new ArrayList<Object>(2);
		Row _row = _createRow(row);
		if(_row != null) {
			int size = _row.getLastCellNum();
			rowDatas = new ArrayList<Object>(size);
			for(int col = 0; col < size; col++) {
				rowDatas.add(_getVal(row, col));
			}
		}
		return rowDatas;
	}
	
	public boolean setDatas(List<List<Object>> datas) {
		return setDatas(datas, 0, 0);
	}
	
	public boolean setDatas(List<List<Object>> datas, int offsetRow, int offsetCol) {
		if(isNull() || datas == null || 
				!_inRange(datas.size() + offsetRow - 1, 0)) {
			return false;
		}
		
		boolean isOk = true;
		for(int size = datas.size(), row = 0; row < size; row++) {
			List<Object> rowDatas = datas.get(row);
			isOk &= setRowDatas(rowDatas, row + offsetRow, offsetCol);
		}
		return isOk;
	}
	
	public boolean setRowDatas(List<Object> rowDatas, int row) {
		return setRowDatas(rowDatas, 0);
	}
	
	public boolean setRowDatas(List<Object> rowDatas, int row, int offsetCol) {
		if(isNull() || rowDatas == null || 
				!_inRange(row, rowDatas.size() + offsetCol - 1)) {
			return false;
		}
		
		boolean isOk = true;
		for(int size = rowDatas.size(), col = 0; col < size; col++) {
			Object val = rowDatas.get(col);
			isOk &= _setVal(row, col + offsetCol, val);
		}
		return isOk;
	}
	
	public boolean isNull() {
		return (sheet == null);
	}

	/**
	 * 清空sheet页内容
	 */
	public void clear() {
		if(isNull()) {
			return;
		}
		
		for(int i = sheet.getLastRowNum(); i >= 0; i--) {
			Row row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
	}
	
}
