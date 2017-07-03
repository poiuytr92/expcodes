package exp.libs.warp.xls.bean;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;

/**
 * Excel2003实体类，代表一个Excel2003文件
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 * 
 */
public class HSSFExcelBean extends ExcelBean {

	/**
	 * 创建一个2003版本的Excel对象
	 */
	public HSSFExcelBean() {
		workbook = new HSSFWorkbook();
	}

	/**
	 * 创建一个2003版本的Excel对象
	 * 
	 * @param workbook
	 *            指定poi工作薄对象
	 */
	public HSSFExcelBean(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	@Override
	public int getMaxRows() {
		return SpreadsheetVersion.EXCEL97.getMaxRows();
	}

	@Override
	public int getMaxColumns() {
		return SpreadsheetVersion.EXCEL97.getMaxColumns();
	}

}
