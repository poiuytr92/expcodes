package Tool;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/*
 * 把table生成为Excel文件
 */

public class TabelToExcel {
	
	/*
	 * tableName: 表名（与Excel文件名无关）
	 * colHead: 表头
	 * tableData: 要导出的excel内容
	 * printLen: 打印表的长度。-1：打印有效长度; -2：打印全表; >=0:打印指定长度
	 * path: 导出位置（绝对路径），包含文件名（及后缀）
	 */
	public static boolean createExcel(String tableName, String[] colHead, Vector<Vector<Object>> tableData, int printLen, String path) {
		
		if(printLen < -2) {
			return false;
		}
		
		if(printLen==-2 || printLen>tableData.size()) {
			printLen = tableData.size();
		}
		
		try {
			//创建Excel文件   
			WritableWorkbook xlsBook = Workbook.createWorkbook(new  File(path));
			
			//生成名为tableName的工作表，参数0表示这是第一页
	        WritableSheet sheet = xlsBook.createSheet(tableName,  0);
	        
			//设置单元格的格式
			WritableCellFormat tableNameFormat = getTableCellNomalFormat(Colour.ICE_BLUE);		//表名单元格格式
			WritableCellFormat tableHeadFormat = getTableCellNomalFormat(Colour.LIGHT_GREEN);	//表头单元格格式
			WritableCellFormat cellFormat = getTableCellNomalFormat(Colour.WHITE);				//单元格格式
			
			//添加表名
	        sheet.mergeCells(0, 0, colHead.length-1, 0);					//合并[0,0]到[0,colHead.length-1]单元格
	        sheet.addCell(new Label(0, 0, tableName, tableNameFormat));		//设置表名
	        
	        //添加表头
	        for(int col=0; col<colHead.length; col++) {
	        	sheet.addCell(new Label(col, 1, colHead[col], tableHeadFormat));
	        }
	        
	        //添加数据
	        boolean flag = false;
	        if(printLen == -1) {
	        	flag = true;
	        	printLen = tableData.size();
	        }
	        for(int r=0; r<printLen; r++) {
	        	Vector<Object> rowData = tableData.get(r);
	        	
	        	if(flag == true) {
		        	if (rowData.get(0)==null || "".equals(rowData.get(0))) {
		        		break;
		        	}
	        	}
	        	
	        	for(int c=0; c<colHead.length; c++) {
	        		Object cellData = rowData.get(c);
	        		if(cellData == null) {
	        			cellData = "";
	        		}
	        		sheet.addCell(new Label(c, r+2, cellData.toString(), cellFormat));
	        	}
	        }

	        //写入数据    
	        xlsBook.write();
	        
	        //关闭文件
	        xlsBook.close();
	        
	        return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//获取常规的单元格格式
	private static WritableCellFormat getTableCellNomalFormat(Colour color) {
		try {
			WritableFont font = new WritableFont(WritableFont.TIMES, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);					//水平居中
			format.setVerticalAlignment(VerticalAlignment.CENTRE);	//垂直居中
			format.setBorder(Border.ALL, BorderLineStyle.THIN);		//边框样式
			format.setBackground(color);
			return format;
		} catch (WriteException e) {
			e.printStackTrace();
		}					
		return null;
	}

}


/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */