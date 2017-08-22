package exp.libs.warp.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.FileType;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.other.StrUtils;

public class Excel {

	private Logger log = LoggerFactory.getLogger(Excel.class);
	
	private final CellStyle DEFAULT_DATETIME_STYLE;
	
	private boolean init;
	
	private File xlsFile;
	
	/** Excel工作簿 */
	private Workbook workbook;
	
	private boolean is2007;
	
	private Map<org.apache.poi.ss.usermodel.Sheet, Sheet> sheets;
	
	public Excel(String xlsFilePath) {
		this.init = init(xlsFilePath);
		this.DEFAULT_DATETIME_STYLE = (init ? initDateCellStyle() : null);
	}
	
	public Excel(File xlsFile) {
		this.init = init(xlsFile);
		this.DEFAULT_DATETIME_STYLE = (init ? initDateCellStyle() : null);
	}
	
	private boolean init(String xlsFilePath) {
		boolean isOk = false;
		if(xlsFilePath == null) {
			return isOk;
		}
		
		if(xlsFilePath.toLowerCase().endsWith(FileType.XLS.EXT) || 
				xlsFilePath.toLowerCase().endsWith(FileType.XLSX.EXT)) {
			File xlsFile = FileUtils.createFile(xlsFilePath);
			isOk = init(xlsFile);
		}
		return isOk;
	}
	
	private boolean init(File xlsFile) {
		boolean isOk = false;;
		if(xlsFile == null || !FileUtils.exists(xlsFile)) {
			log.error("加载xls文件失败: 文件路径无效");
			return isOk;
		}
		
		String headMsg = FileUtils.getHeadMsg(xlsFile);
		if(!FileType.XLS.HEAD_MSG.equals(headMsg) && 
				!FileType.XLSX.HEAD_MSG.equals(headMsg)) {
			log.error("加载xls文件失败: 不支持的文件类型({})", xlsFile.getAbsoluteFile());
			return isOk;
		}
		
		this.sheets = new HashMap<org.apache.poi.ss.usermodel.Sheet, Sheet>();
		this.xlsFile = xlsFile;
		FileInputStream fis = null; 
		try {
			fis = new FileInputStream(xlsFile);
			this.workbook = WorkbookFactory.create(fis);
			this.is2007 = (workbook instanceof XSSFWorkbook);
			isOk = true;
			
		} catch(Exception e) {
			log.error("加载xls文件失败: [{}]", xlsFile.getAbsoluteFile(), e);
			
		} finally {
			IOUtils.close(fis);
		}
		return isOk;
	}
	
	private CellStyle initDateCellStyle() {
		CellStyle dateCellStyle = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		dateCellStyle.setDataFormat(format.getFormat("yyyy-mm-dd hh:mm:ss"));
		return dateCellStyle;
	}
	
	public int MAX_ROW() {
		return (!init ? 0 : (is2007 ? 
				SpreadsheetVersion.EXCEL2007.getMaxRows() : 
				SpreadsheetVersion.EXCEL97.getMaxRows()));
	}
	
	public int MAX_COL() {
		return (!init ? 0 : (is2007 ? 
				SpreadsheetVersion.EXCEL2007.getMaxColumns() : 
				SpreadsheetVersion.EXCEL97.getMaxColumns()));
	}
	
	public int SHEET_SIZE() {
		return (init ? workbook.getNumberOfSheets() : 0);
	}
	
	private boolean _inRange(int index) {
		return (index >= 0 && index < workbook.getNumberOfSheets());
	}
	
	private String _getDefaultSheetName() {
		return StrUtils.concat("Sheet", (workbook.getNumberOfSheets() + 1));
	}
	
	private Sheet _getSheet(org.apache.poi.ss.usermodel.Sheet poiSheet) {
		if(poiSheet == null) {
			return Sheet.NULL;
		}
		
		Sheet sheet = sheets.get(poiSheet);
		if(sheet == null) {
			sheet = new Sheet(poiSheet, is2007, DEFAULT_DATETIME_STYLE);
			sheets.put(poiSheet, sheet);
		}
		return sheet;
	}
	
	public Sheet getSheet(String name) {
		Sheet sheet = Sheet.NULL;
		if(init == true) {
			org.apache.poi.ss.usermodel.Sheet poiSheet = workbook.getSheet(name);
			sheet = (poiSheet == null ? createSheet(name) : _getSheet(poiSheet));
		}
		return sheet;
	}

	/**
	 * 
	 * @param index sheet页索引(从0开始)
	 * @return
	 */
	public Sheet getSheet(int index) {
		Sheet sheet = Sheet.NULL;
		if (init && _inRange(index)) {
			sheet = _getSheet(workbook.getSheetAt(index));
		}
		return sheet;
	}
	
	public Sheet createSheet() {
		return createSheet(_getDefaultSheetName());
	}
	
	public Sheet createSheet(String name) {
		Sheet sheet = Sheet.NULL;
		try {
			org.apache.poi.ss.usermodel.Sheet poiSheet = StrUtils.isEmpty(name) ? 
					workbook.createSheet() : workbook.createSheet(name);
			sheet = _getSheet(poiSheet);
			
		} catch(Exception e) {
			log.error("创建sheet页 [{}] 失败", name, e);
		}
		return sheet;
	}
	
	public int creatPagingSheets(List<String> header, List<List<Object>> datas, 
			String sheetNamePrefix, int pageLimit) {
		int page = -1;
		if(init == false) {
			return page;
		}
		
		// TODO
		
		return page;
	}
	
	public boolean delFirstSheet() {
		return delSheet(0);
	}
	
	/**
	 * 
	 * @param index sheet页索引(从0开始)
	 * @return
	 */
	public boolean delSheet(int index) {
		boolean isOk = false;
		if(init && _inRange(index)) {
			Sheet sheet = sheets.remove(workbook.getSheetAt(index));
			if(sheet != null) { sheet.clear(); }
			workbook.removeSheetAt(index);
			isOk = true;
		}
		return isOk;
	}
	
	public boolean save() {
		return saveAs(xlsFile);
	}
	
	public boolean saveAs(String filePath) {
		boolean isOk = false;
		if(init == true && filePath != null) {
			isOk = saveAs(new File(filePath));
		}
		return isOk;
	}
	
	public boolean saveAs(File file) {
		boolean isOk = false;
		if(init == false || file == null) {
			return isOk;
		}
		
		if(!file.exists()) {
			String savePath = file.getAbsolutePath();
			file = FileUtils.createFile(savePath);
			if(file == null) {
				log.error("保存xls到文件失败: 无法创建文件({})", savePath);
				return isOk;
			}
		}

		// 若无sheet页则创建一个临时的sheet页（Excel保存时不允许无sheet页）
		if(SHEET_SIZE() <= 0) {
			createSheet(_getDefaultSheetName());
		}
		
		// 把内容覆写到文件
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			workbook.write(fos);
			
		} catch (Exception e) {
			log.error("保存xls到文件失败: [{}]", file.getAbsoluteFile(), e);
			
		} finally {
			IOUtils.close(fos);
		}
		
		delFirstSheet();	// 删除临时sheet页
		return isOk;
	}
	
	/**
	 * 清空Excel工作簿
	 */
	public void clear() {
		if(init == false) {
			return;
		}
		
		for(int size = SHEET_SIZE(), idx = size - 1; idx >= 0; idx--) {
			delSheet(idx);
		}
	}
	
}
