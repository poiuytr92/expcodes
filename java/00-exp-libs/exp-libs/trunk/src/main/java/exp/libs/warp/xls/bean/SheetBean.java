package exp.libs.warp.xls.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Sheet页实体类，代表Excel文件中的一个Sheet页
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 * 
 */
public class SheetBean {

	/**
	 * poi原始的Sheet对象
	 */
	protected Sheet sheet;
	/**
	 * 所属的Excel对象
	 */
	private ExcelBean excelBean;

	/**
	 * 创建一个Sheet对象
	 * 
	 * @param excelBean
	 *            所属Excel对象
	 * @param sheet
	 *            poi原始的Sheet对象
	 */
	public SheetBean(ExcelBean excelBean, Sheet sheet) {
		this.excelBean = excelBean;
		this.sheet = sheet;
	}

	/**
	 * 获取内容为List数组
	 * 
	 * @return
	 */
	public List<List<Object>> getContent() {
		if (sheet.getPhysicalNumberOfRows() == 0) {
			return new ArrayList<List<Object>>();
		}
		return getContent(0, sheet.getLastRowNum());
	}

	/**
	 * 获取指定范围内的行内容为List数组
	 * 
	 * @param startRow
	 *            起始行号
	 * @param endRow
	 *            结束行号（包含）
	 * @return
	 */
	public List<List<Object>> getContent(int startRow, int endRow) {
		List<List<Object>> content = new ArrayList<List<Object>>();

		for (int i = startRow; i <= endRow; i++) {
			List<Object> row = getRowContent(i);
			content.add(row);
		}

		return content;
	}

	/**
	 * 获取行内容为List
	 * 
	 * @param rowNum
	 *            行号
	 * @return
	 */
	private List<Object> getRowContent(int rowNum) {
		List<Object> content = new ArrayList<Object>();

		Row row = sheet.getRow(rowNum);
		if (row != null) {
			for (int i = 0; i < row.getLastCellNum(); i++) {
				content.add(getCellValue(rowNum, i));
			}
		}

		return content;
	}

	/**
	 * 获取指定范围内容为List数组
	 * 
	 * @param startRow
	 *            起始行号
	 * @param startColumn
	 *            起始列号
	 * @param endRow
	 *            结束行号（包含）
	 * @param endColumn
	 *            结束列号（包含）
	 * @return
	 */
	public List<List<Object>> getContent(int startRow, int startColumn,
			int endRow, int endColumn) {
		List<List<Object>> content = new ArrayList<List<Object>>();

		for (int i = startRow; i <= endRow; i++) {
			List<Object> row = new ArrayList<Object>();
			for (int j = startColumn; j <= endColumn; j++) {
				row.add(getCellValue(i, j));
			}
			content.add(row);
		}

		return content;
	}

	/**
	 * 根据List数组设置内容
	 * 
	 * @param content
	 */
	public void setContent(List<List<Object>> content) {
		clear();

		if (content != null) {
			int contentSize = content.size();
			for (int i = 0; i < contentSize; i++) {
				List<Object> rowContent = content.get(i);
				setRowContent(i, rowContent);
			}
		}
	}

	/**
	 * 根据List设置行内容
	 * 
	 * @param rowNum
	 *            行号
	 * @param rowContent
	 *            行内容
	 */
	public void setRowContent(int rowNum, List<Object> rowContent) {
		int rowContentSize = rowContent.size();
		if (rowContent != null && rowContentSize > 0) {
			for (int i = 0; i < rowContentSize; i++) {
				setCellValue(rowNum, i, rowContent.get(i));
			}
		}
	}

	/**
	 * 清空sheet页内容
	 */
	private void clear() {
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
	}

	/**
	 * 获取指定单元格的值
	 * 
	 * @param rowNum
	 *            行号
	 * @param colNum
	 *            列号
	 * @return
	 */
	public Object getCellValue(int rowNum, int colNum) {
		Cell cell = getCell(rowNum, colNum);
		if (cell == null) {
			return null;
		}

		int valueType = cell.getCellType();
		if (valueType == Cell.CELL_TYPE_FORMULA) {
			valueType = cell.getCachedFormulaResultType();
		}
		return getCellValueByValueType(cell, valueType);
	}

	/**
	 * 根据单元格值类型获取值
	 * 
	 * @param cell
	 *            单元格
	 * @param valueType
	 *            单元格值类型
	 * @return
	 */
	private Object getCellValueByValueType(Cell cell, int valueType) {
		switch (valueType) {
		case Cell.CELL_TYPE_STRING:
			return cell.getRichStringCellValue().getString();
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				double dValue = cell.getNumericCellValue();
				int iValue = (int) dValue;
				if (iValue == dValue) {
					return iValue;
				}
				return dValue;
			}
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_ERROR:
			return "#N/A";
		case Cell.CELL_TYPE_BLANK:
		default:
			return null;
		}
	}

	/**
	 * 设置指定单元格的值
	 * 
	 * @param rowNum
	 *            行号
	 * @param colNum
	 *            列号
	 * @param value
	 *            值
	 */
	public void setCellValue(int rowNum, int colNum, Object value) {
		Cell cell = getOrCreateCell(rowNum, colNum);
		if (value == null) {
			cell.setCellType(Cell.CELL_TYPE_BLANK);
		} else if (value instanceof Integer) {
			cell.setCellValue(((Integer) value).doubleValue());
		} else if (value instanceof Double) {
			cell.setCellValue(((Double) value).doubleValue());
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
			cell.setCellStyle(getExcelBean().getDateCellStyle());// 设置日期样式
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue(value.toString());
		}
	}
	
	
	/**
	 * 设置超链接单元格，默认value：Link
	 *
	 * @param rowNum
	 * @param colNum
	 * @param urlOrPath
	 * @param hlink_style
	 */
	public void setCellLinkValue(int rowNum, int colNum, String urlOrPath, 
			CellStyle hlink_style) {
		setCellLinkValue(rowNum, colNum, "Link", urlOrPath, hlink_style);
	}
	
	/**
	 * 设置超链接单元格
	 *
	 * @param rowNum
	 * @param colNum
	 * @param value
	 * @param urlOrPath
	 * @param hlink_style
	 */
	public void setCellLinkValue(int rowNum, int colNum, String value, 
			String urlOrPath, CellStyle hlink_style) {
		Cell cell = getOrCreateCell(rowNum, colNum);
		cell.setCellValue(value);
		if (hlink_style != null) {
			cell.setCellStyle(hlink_style);
		}
		Hyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
		link.setAddress(urlOrPath);
		cell.setHyperlink(link);
	}

	/**
	 * 设置指定单元格的样式
	 * 
	 * @param rowNum
	 *            行号
	 * @param colNum
	 *            列号
	 * @param style
	 *            样式
	 */
	public void setCellStyle(int rowNum, int colNum, CellStyle style) {
		Cell cell = getOrCreateCell(rowNum, colNum);
		cell.setCellStyle(style);
	}

	/**
	 * 获取单元格对象
	 * 
	 * @param rowNum
	 *            行号
	 * @param colNum
	 *            列号
	 * @return
	 */
	private Cell getCell(int rowNum, int colNum) {
		Row row = sheet.getRow(rowNum);
		if (row == null) {
			return null;
		}

		return row.getCell(colNum);
	}

	/**
	 * 获取或创建单元格对象
	 * 
	 * @param rowNum
	 *            行号
	 * @param colNum
	 *            列号
	 * @return
	 */
	protected Cell getOrCreateCell(int rowNum, int colNum) {
		Row row = sheet.getRow(rowNum);
		if (row == null) {
			row = sheet.createRow(rowNum);
		}

		Cell cell = row.getCell(colNum);
		if (cell == null) {
			cell = row.createCell(colNum);
		}

		return cell;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public ExcelBean getExcelBean() {
		return excelBean;
	}

}
