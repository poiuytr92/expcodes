package exp.libs.warp.other.xls.bean;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;

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
public class Sheet2007Bean extends SheetBean{

	public Sheet2007Bean(ExcelBean excelBean, Sheet sheet) {
		super(excelBean, sheet);
		
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
		Hyperlink link =  new XSSFHyperlink(XSSFHyperlink.LINK_URL);
		link.setAddress(urlOrPath);
		cell.setHyperlink(link);
	}

}
