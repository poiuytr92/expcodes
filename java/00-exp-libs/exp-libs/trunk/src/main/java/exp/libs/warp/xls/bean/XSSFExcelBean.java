package exp.libs.warp.xls.bean;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel2007实体类，代表一个Excel2007文件
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 * 
 */
public class XSSFExcelBean extends ExcelBean {

	/**
	 * 创建一个2007版本的Excel对象
	 */
	public XSSFExcelBean() {
		workbook = new XSSFWorkbook();
	}

	/**
	 * 创建一个2007版本的Excel对象
	 * 
	 * @param workbook
	 *            指定poi工作薄对象
	 */
	public XSSFExcelBean(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	@Override
	public int getMaxRows() {
		return SpreadsheetVersion.EXCEL2007.getMaxRows();
	}

	@Override
	public int getMaxColumns() {
		return SpreadsheetVersion.EXCEL2007.getMaxColumns();
	}
	
	/**
	 * 创建指定名称的Sheet对象
	 * 
	 * @param name
	 *            Sheet的名称
	 * @return
	 */
	@Override
	public Sheet2007Bean createSheetBean(String name) {
		Sheet sheet = workbook.createSheet(name);
		return getSheetBean(sheet);
	}
	
	/**
	 * 根据poi原始sheet获取Sheet对象
	 * 
	 * @param sheet
	 *            poi原始sheet
	 * @return
	 */
	@Override
	public Sheet2007Bean getSheetBean(Sheet sheet) {
		if (sheet == null) {
			return null;
		}
		return new Sheet2007Bean(this, sheet);
	}

}
