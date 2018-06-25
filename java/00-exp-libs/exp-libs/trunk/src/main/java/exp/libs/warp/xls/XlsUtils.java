package exp.libs.warp.xls;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Excel处理工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-04-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XlsUtils {

	/** 私有化构造函�? */
	protected XlsUtils() {}
	
	/**
	 * 生成单元格格�?.
	 * 	此方法可配合 {@link Sheet.setStyle()} 使用
	 * 
	 * @param excel Excel工作簿对�?
	 * @param fontName 字体名称, �?: 宋体
	 * @param size 字体大小, �?: (short) 9
	 * @return 单元格格�?
	 */
	public static CellStyle getCellStyle(Excel excel, 
			String fontName, short fontSize) {
		return getCellStyle(excel, fontName, fontSize, 
				HSSFColor.AUTOMATIC.index, true);
	}
	
	/**
	 * 生成单元格格�?.
	 * 	此方法可配合 {@link Sheet.setStyle()} 使用
	 * 
	 * @param excel Excel工作簿对�?
	 * @param bgColor 单元格背景色, 如：HSSFColor.LIGHT_GREEN.index
	 * @return 单元格格�?
	 */
	public static CellStyle getCellStyle(Excel excel, short bgColor) {
		return getCellStyle(excel, "", (short) 0, bgColor, true);
	}
	
	/**
	 * 生成单元格格�?.
	 * 	此方法可配合 {@link Sheet.setStyle()} 使用
	 * 
	 * @param excel Excel工作簿对�?
	 * @param alignCenter 是否完全居中(水平+垂直方向居中)
	 * @return 单元格格�?
	 */
	public static CellStyle getCellStyle(Excel excel, boolean alignCenter) {
		return getCellStyle(excel, "", (short) 0, 
				HSSFColor.AUTOMATIC.index, true);
	}
	
	/**
	 * 生成单元格格�?.
	 * 	此方法可配合 {@link Sheet.setStyle()} 使用
	 * 
	 * @param excel Excel工作簿对�?
	 * @param fontName 字体名称, �?: 宋体
	 * @param size 字体大小, �?: (short) 9
	 * @param bgColor 单元格背景色, 如：HSSFColor.LIGHT_GREEN.index
	 * @param alignCenter 是否完全居中(水平+垂直方向居中)
	 * @return 单元格格�?
	 */
	public static CellStyle getCellStyle(Excel excel, 
			String fontName, short fontSize, short bgColor, boolean alignCenter) {
		CellStyle style = excel.createCellStyle();
		
		// 设置字体
		if(StrUtils.isNotEmpty(fontName) && fontSize > 0) {
			Font font = excel.createFont();
			font.setFontName(fontName);
			font.setFontHeightInPoints(fontSize);
			style.setFont(font);
		}
		
		// 设置背景�?
		if(HSSFColor.AUTOMATIC.index != bgColor) {
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 使用纯色填充单元�?
			style.setFillForegroundColor(bgColor);	// 设置单元格填充色
			
		} else {
			style.setFillPattern(HSSFCellStyle.NO_FILL);	// 不填充颜�?
		}
		
		// 设置居中
		if(alignCenter == true) {
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);	// 水平居中
			style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);	// 垂直居中
		}
		return style;
	}
}
