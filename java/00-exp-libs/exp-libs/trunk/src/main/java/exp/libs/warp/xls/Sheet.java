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

/**
 * <PRE>
 * Sheet页，需配合Excel工作簿使用.
 * 	(对Sheet页的所有操作均只影响内存数据，对外存文件无影响)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-22
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Sheet {

	/** 默认的Sheet操作对象 */
	public final static Sheet NULL = new Sheet(null, false, null);
	
	/** 日期时间单元格的默认格式风格 */
	private final CellStyle DEFAULT_DATETIME_STYLE;
	
	/** POI的sheet页对�? */
	private org.apache.poi.ss.usermodel.Sheet sheet;
	
	/** 是否�?2007版本的xlsx（反之为2003版本的xls�? */
	private boolean is2007;
	
	/**
	 * 构造函�?
	 * @param sheet POI的sheet页对�?
	 * @param is2007 是否�?2007版本的xlsx（反之为2003版本的xls�?
	 * @param dataTimeStyle 日期时间单元格的默认格式风格
	 */
	protected Sheet(org.apache.poi.ss.usermodel.Sheet sheet, 
			boolean is2007, CellStyle dataTimeStyle) {
		this.sheet = sheet;
		this.is2007 = is2007;
		this.DEFAULT_DATETIME_STYLE = dataTimeStyle;
	}

	/**
	 * 获取sheet页名�?
	 * @return sheet页名�?
	 */
	public String NAME() {
		return (isNull() ? "NULL" : sheet.getSheetName());
	}
	
	/**
	 * 单个Sheet页支持的最大行�?
	 * @return 若初始化失败则返�?0
	 */
	public int MAX_ROW() {
		return (isNull() ? 0 : (is2007 ? 
				SpreadsheetVersion.EXCEL2007.getMaxRows() : 
				SpreadsheetVersion.EXCEL97.getMaxRows()));
	}
	
	/**
	 * 单个Sheet页支持的最大列�?
	 * @return 若初始化失败则返�?0
	 */
	public int MAX_COL() {
		return (isNull() ? 0 : (is2007 ? 
				SpreadsheetVersion.EXCEL2007.getMaxColumns() : 
				SpreadsheetVersion.EXCEL97.getMaxColumns()));
	}
	
	/**
	 * 检查sheet的行列索引是否在有效范围�?
	 * @param row 行索�?
	 * @param col 列索�?
	 * @return true:�?;  false:�?
	 */
	private boolean _inRange(int row, int col) {
		return (row >= 0 && row < MAX_ROW() && col >= 0 && col < MAX_COL());
	}
	
	/**
	 * 创建�?
	 * @param row 行索引（�?0开始）
	 * @return 创建失败返回null
	 */
	private Row _createRow(int row) {
		Row _row = _getRow(row);
		if (_row == null) {
			_row = sheet.createRow(row);
		}
		return _row;
	}
	
	/**
	 * 获取�?
	 * @param row 行索引（�?0开始）
	 * @return 获取失败返回null
	 */
	private Row _getRow(int row) {
		return sheet.getRow(row);
	}
	
	/**
	 * 创建单元�?
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @return 创建失败返回null
	 */
	private Cell _createCell(int row, int col) {
		Cell cell = null;
		Row _row = _createRow(row);
		if(_row != null) {
			cell = _row.getCell(col);
			if (cell == null) {
				cell = _row.createCell(col);
			}
		}
		return cell;
	}
	
	/**
	 * 获取单元�?
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @return 获取失败返回null
	 */
	private Cell _getCell(int row, int col) {
		Cell cell = null;
		Row _row = sheet.getRow(row);
		if (_row != null) {
			cell = _row.getCell(col);
		}
		return cell;
	}
	
	/**
	 * 获取单元格�?
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @return 无值或异常返回null
	 */
	public Object getVal(int row, int col) {
		if(isNull() || !_inRange(row, col)) {
			return null;
		}
		return _getVal(row, col);
	}
	
	/**
	 * 获取单元格�?
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @return 无值或异常返回null
	 */
	private Object _getVal(int row, int col) {
		Cell cell = _getCell(row, col);
		if (cell == null) {
			return null;
		}

		// 若单元格的值类型是表达式，则修正值类型为表达式结果的值类�?
		int valType = cell.getCellType();
		if (valType == Cell.CELL_TYPE_FORMULA) {
			valType = cell.getCachedFormulaResultType();
		}
		return _getVal(cell, valType);
	}

	/**
	 * 获取单元格�?
	 * @param cell 单元格对�?
	 * @param valType 值类�?
	 * @return 无值或异常返回null
	 */
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
	
	/**
	 * 设置单元格�?
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @param val 单元格�?
	 * @return true:设置成功; false:设置失败
	 */
	public boolean setVal(int row, int col, Object val) {
		if(isNull() || !_inRange(row, col)) {
			return false;
		}
		return _setVal(row, col, val);
	}
	
	/**
	 * 设置单元格�?
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @param val 单元格�?
	 * @return true:设置成功; false:设置失败
	 */
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
	
	/**
	 * 设置单元格超链接
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @param val 单元格�?
	 * @param url 超链接地址
	 * @return true:设置成功; false:设置失败
	 */
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
	
	/**
	 * 获取Sheet页所有数�?
	 * @return List<List<Object>> (不会返回null)
	 */
	public List<List<Object>> getAllDatas() {
		List<List<Object>> tableDatas = new ArrayList<List<Object>>(2);
		if(isNull() == false) {
			tableDatas = getRowDatas(0, getLastRowNum());
		}
		return tableDatas;
	}
	
	/**
	 * 获取指定范围内的数据
	 * @param bgnRow 起始行索引（�?0开始）
	 * @param bgnCol 起始列索引（�?0开始）
	 * @param endRow 结束行索引（�?0开始）
	 * @param endCol 结束列索引（�?0开始）
	 * @return List<List<Object>> (不会返回null)
	 */
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
	
	/**
	 * 获取指定行范围的数据
	 * @param bgnRow 起始行索引（�?0开始）
	 * @param endRow 结束行索引（�?0开始）
	 * @return List<List<Object>> (不会返回null)
	 */
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
	
	/**
	 * 获取指定行的数据
	 * @param row 行索引（�?0开始）
	 * @return List<Object> (不会返回null)
	 */
	public List<Object> getRowDatas(int row) {
		List<Object> rowDatas = new ArrayList<Object>(2);
		if(isNull() || !_inRange(row, 0)) {
			return rowDatas;
		}
		return _getRowDatas(row);
	}
	
	/**
	 * 获取指定行的数据
	 * @param row 行索引（�?0开始）
	 * @return List<Object> (不会返回null)
	 */
	private List<Object> _getRowDatas(int row) {
		List<Object> rowDatas = new ArrayList<Object>(2);
		Row _row = _getRow(row);
		if(_row != null) {
			int size = _row.getLastCellNum();
			rowDatas = new ArrayList<Object>(size);
			for(int col = 0; col < size; col++) {
				rowDatas.add(_getVal(row, col));
			}
		}
		return rowDatas;
	}
	
	/**
	 * <PRE>
	 * 覆写Sheet页某个行列范围内的数�?.
	 * 	覆写范围与覆写数据的行列数相�?.
	 * </PRE>
	 * @param datas 覆写数据
	 * @return true:覆写成功; false:覆写失败
	 */
	public boolean setDatas(List<List<Object>> datas) {
		return setDatas(datas, 0, 0);
	}
	
	/**
	 * <PRE>
	 * 覆写Sheet页某个行列范围内的数�?.
	 * 	覆写范围与覆写数据的行列数相�?.
	 * </PRE>
	 * @param datas 覆写数据
	 * @param offsetRow 覆写操作的偏移行索引（即起始行，�?0开始）
	 * @param offsetCol 覆写操作的偏移列索引（即起始列，�?0开始）
	 * @return true:覆写成功; false:覆写失败
	 */
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
	
	/**
	 * <PRE>
	 * 覆写指定行的数据.
	 * 	覆写列范围与覆写数据的长度相�?.
	 * </PRE>
	 * @param rowDatas 行数�?
	 * @param row 行索引（�?0开始）
	 * @return true:覆写成功; false:覆写失败
	 */
	public boolean setRowDatas(List<Object> rowDatas, int row) {
		return setRowDatas(rowDatas, row, 0);
	}
	
	/**
	 * <PRE>
	 * 覆写指定行的数据.
	 * 	覆写列范围与覆写数据的长度相�?.
	 * </PRE>
	 * @param rowDatas 行数�?
	 * @param row 行索引（�?0开始）
	 * @param offsetCol 覆写操作的偏移列索引（即起始列，�?0开始）
	 * @return true:覆写成功; false:覆写失败
	 */
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
	
	/**
	 * 在Sheet页末尾添加行
	 * @param rowDatas 行数�?
	 * @return true:添加成功; false:添加失败
	 */
	public boolean addRowDatas(List<Object> rowDatas) {
		return addRowDatas(rowDatas, 0);
	}
	
	/**
	 * 在Sheet页末尾添加行
	 * @param rowDatas 行数�?
	 * @param offsetCol 添加操作的偏移列索引（即起始列，�?0开始）
	 * @return true:添加成功; false:添加失败
	 */
	public boolean addRowDatas(List<Object> rowDatas, int offsetCol) {
		return setRowDatas(rowDatas, getLastRowNum() + 1, offsetCol);
	}
	
	/**
	 * <PRE>
	 * 设置表头(默认在Sheet页的第一�?).
	 * </PRE>
	 * @param header 表头列表
	 * @return true:设置成功; false:设置失败
	 */
	public boolean setHeader(List<String> header) {
		if(isNull() || header == null) {
			return false;
		}
		
		boolean isOk = true;
		for(int size = header.size(), col = 0; col < size; col++) {
			String val = header.get(col);
			isOk &= _setVal(0, col, val);
		}
		return isOk;
	}
	
	/**
	 * 设置Sheet页所有单元格格式
	 * @param style 单元格格�?
	 */
	public void setStyle(CellStyle style) {
		if(!isNull()) {
			for(int row = 0; row < getLastRowNum(); row++) {
				Row _row = _getRow(row);
				if(_row != null) {
					_row.setRowStyle(style);
				}
			}
		}
	}
	
	/**
	 * 设置指定行的所有单元格格式
	 * @param row 行索引（�?0开始）
	 * @param style 单元格格�?
	 */
	public void setStyle(int row, CellStyle style) {
		if(!isNull() && _inRange(row, 0)) {
			Row _row = _createRow(row);
			if(_row != null) {
				_row.setRowStyle(style);
			}
		}
	}
	
	/**
	 * 设置单元格格�?
	 * @param row 行索引（�?0开始）
	 * @param col 列索引（�?0开始）
	 * @param style 单元格格�?
	 */
	public void setStyle(int row, int col, CellStyle style) {
		if(!isNull() && _inRange(row, col)) {
			_createCell(row, col).setCellStyle(style);
		}
	}
	
	/**
	 * 清空行数据（但不删除行）
	 * @param row 行索引（�?0开始）
	 * @return true:清空成功; false:清空失败
	 */
	public boolean clrRow(int row) {
		if(isNull() == true) {
			return false;
		}
		
		_clearRow(row);
		return true;
	}
	
	/**
	 * 删除�?
	 * @param row 行索引（�?0开始）
	 * @return true:清空成功; false:清空失败
	 */
	public boolean delRow(int row) {
		if(isNull() == true) {
			return false;
		}
		
		_clearRow(row);	// 删除行数�?
		_shiftRow(row);	// 删除行的下面所有行上移一�?
		return true;
	}
	
	/**
	 * 清空行数据（但不删除行）
	 * @param row 行索引（�?0开始）
	 * @return true:清空成功; false:清空失败
	 */
	private void _clearRow(int row) {
		Row _row = _getRow(row);
		if (_row != null) {
			sheet.removeRow(_row);
		}
	}
	
	/**
	 * 把删除行以下的所有行都上移一�?
	 * @param delRow 删除行的行索引（�?0开始）
	 * @return true:上移成功; false:上移失败
	 */
	private void _shiftRow(int delRow) {
		int bgnRow = delRow + 1;		// 移动的开始行�?, 此处为删除行的下一�?
		int endRow = getLastRowNum();	// 移动的结束的行号, 此处为最后一�?
		int shiftCnt = (endRow - bgnRow + 1) * -1;	// 移动的行�?(正数向下移动, 负数向上移动)
		sheet.shiftRows(bgnRow, endRow, shiftCnt);
	}
	
	/**
	 * 返回当前Sheet页最后一行的行索�?
	 * @return 最后一行的行索�?
	 */
	public int getLastRowNum() {
		return (isNull() ? 0 : sheet.getLastRowNum());
	}
	
	/**
	 * 清空sheet页内�?
	 */
	public void clear() {
		if(isNull()) {
			return;
		}
		
		for(int i = getLastRowNum(); i >= 0; i--) {
			_clearRow(i);
		}
	}
	
	/**
	 * 测试当前Sheet页是否为空对象（无效对象�?
	 * @return true:无效对象; false:有效对象
	 */
	public boolean isNull() {
		return (sheet == null);
	}
	
}
