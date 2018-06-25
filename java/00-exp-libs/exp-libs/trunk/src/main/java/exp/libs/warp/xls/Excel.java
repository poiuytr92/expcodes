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
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.FileType;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Excel工作簿（支持2003和2007之后的版本）.
 * 
 * 使用示例:
 * 	Excel excel = new Excel("C:\\Users\\Administrator\\Desktop\\2003.xls");
 * 	excel.clear();	// 仅清空内存，不调用save/saveAs方法不会清空文件
 * 
 * 	List<String> header = Arrays.asList(new String[] { "序号", "姓名", "Remark" });
 * 	List<List<Object>> datas = new ArrayList<List<Object>>();
 * 	datas.add(Arrays.asList(new Object[] { "1", "张三", 98.999D }));
 * 	datas.add(Arrays.asList(new Object[] { "2", "李四", 1234567890123L }));
 * 	datas.add(Arrays.asList(new Object[] { "3", "王五", new Date() }));
 * 	datas.add(Arrays.asList(new Object[] { "4", "肾六", "hello excel" }));
 * 	datas.add(Arrays.asList(new Object[] { "5", "田七", null }));
 * 	int pageNum = excel.createPagingSheets(header, datas, "TEST-", 2);
 * 	boolean isOk = excel.saveAs("C:\\Users\\Administrator\\Desktop\\2007.xlsx");
 * 
 * 	Sheet sheet = excel.getSheet(0);
 * 	List<Object> rowDatas = sheet.getRowDatas(1);
 * 
 * 	sheet.delRow(1);	// 删除行
 * 	sheet.addRowDatas(Arrays.asList(new Object[] { "在末尾添加行" }));
 * 	isOk = excel.save();
 * 
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-22
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Excel {

	/** 日志�? */
	private Logger log = LoggerFactory.getLogger(Excel.class);
	
	/** 日期时间单元格的默认格式风格 */
	private final CellStyle DEFAULT_DATETIME_STYLE;
	
	/** 是否初始化成�? */
	private boolean init;
	
	/** 最后一次保存的xls文件对象 */
	private File xlsFile;
	
	/** Excel工作簿（存储xls缓存数据�? */
	private Workbook workbook;
	
	/** 是否�?2007版本的xlsx（反之为2003版本的xls�? */
	private boolean is2007;
	
	/** sheet页集�? */
	private Map<org.apache.poi.ss.usermodel.Sheet, Sheet> sheets;
	
	/**
	 * 构造函�?
	 * @param xlsFilePath Excel文件路径
	 */
	public Excel(String xlsFilePath) {
		this.init = init(xlsFilePath);
		this.DEFAULT_DATETIME_STYLE = (init ? initDateCellStyle() : null);
	}
	
	/**
	 * 构造函�?
	 * @param xlsFile Excel文件对象
	 */
	public Excel(File xlsFile) {
		this.init = init(xlsFile);
		this.DEFAULT_DATETIME_STYLE = (init ? initDateCellStyle() : null);
	}
	
	/**
	 * 初始�?
	 * @param xlsFilePath Excel文件路径
	 * @return true: 初始化成�?; false:初始化失�?
	 */
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

	/**
	 * 初始�?
	 * @param xlsFile Excel文件对象
	 * @return true: 初始化成�?; false:初始化失�?
	 */
	private boolean init(File xlsFile) {
		boolean isOk = false;;
		if(xlsFile == null || !FileUtils.exists(xlsFile)) {
			log.error("加载xls文件失败: 文件路径无效");
			return isOk;
		}
		
		FileType fileType = FileUtils.getFileType(xlsFile);
		if(FileType.XLS != fileType && FileType.XLSX != fileType) {
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
	
	/**
	 * 初始化日期时间单元格的默认格式风�?
	 * @return true: 初始化成�?; false:初始化失�?
	 */
	private CellStyle initDateCellStyle() {
		CellStyle dateCellStyle = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		dateCellStyle.setDataFormat(format.getFormat("yyyy-mm-dd hh:mm:ss"));
		return dateCellStyle;
	}
	
	/**
	 * <pre>
	 * 获取新的单元格风�?.
	 * ----------------------------
	 *   注：默认情况�?, Excel所有单元格是共享同一个CellStyle对象�?, 
	 *      因此若从单元格上getCellStyle后再修改风格, 会导致整个Excel风格同时改变.
	 *      所以当只需要改变某个单元格风格�?, 需要通过此方法新建一个CellStyle对象
	 * </pre>
	 * @return 单元格风�?
	 */
	public CellStyle createCellStyle() {
		return (init ? workbook.createCellStyle() : null);
	}
	
	/**
	 * 获取新的字体格式
	 * @return 字体格式
	 */
	public Font createFont() {
		return (init ? workbook.createFont() : null);
	}
	
	/**
	 * 获取Excel工作�?
	 * @return Excel工作�?
	 */
	public Workbook WORKBOOK() {
		return (init ? workbook : null);
	}
	
	/**
	 * Excel单个Sheet页支持的最大行�?
	 * @return 若初始化失败则返�?0
	 */
	public int MAX_ROW() {
		return (!init ? 0 : (is2007 ? 
				SpreadsheetVersion.EXCEL2007.getMaxRows() : 
				SpreadsheetVersion.EXCEL97.getMaxRows()));
	}
	
	/**
	 * Excel单个Sheet页支持的最大列�?
	 * @return 若初始化失败则返�?0
	 */
	public int MAX_COL() {
		return (!init ? 0 : (is2007 ? 
				SpreadsheetVersion.EXCEL2007.getMaxColumns() : 
				SpreadsheetVersion.EXCEL97.getMaxColumns()));
	}
	
	/**
	 * 当前sheet页数�?
	 * @return 若初始化失败则返�?0
	 */
	public int SHEET_SIZE() {
		return (init ? workbook.getNumberOfSheets() : 0);
	}
	
	/**
	 * 检查sheet索引是否在有效范围内
	 * @param index sheet索引
	 * @return true:�?;  false:�?
	 */
	private boolean _inRange(int index) {
		return (index >= 0 && index < workbook.getNumberOfSheets());
	}
	
	/**
	 * 生成默认的Sheet页名�?
	 * @return SheetN
	 */
	private String _getDefaultSheetName() {
		return StrUtils.concat("Sheet", (workbook.getNumberOfSheets() + 1));
	}
	
	/**
	 * 获取Sheet页对象（若不存在则创建）
	 * @param poiSheet POI的Sheet页对�?
	 * @return 封装的Sheet页对�?
	 */
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
	
	/**
	 * 获取Sheet页对象（若不存在则创建）
	 * @param name sheet名称
	 * @return Sheet页对象（若初始化失败则返回Sheet.NULL对象�?
	 */
	public Sheet getSheet(String name) {
		Sheet sheet = Sheet.NULL;
		if(init == true) {
			org.apache.poi.ss.usermodel.Sheet poiSheet = workbook.getSheet(name);
			sheet = (poiSheet == null ? createSheet(name) : _getSheet(poiSheet));
		}
		return sheet;
	}

	/**
	 * 获取Sheet页对�?
	 * @param index sheet页索�?(�?0开�?)
	 * @return Sheet页对象（若初始化失败或索引无效则返回Sheet.NULL对象�?
	 */
	public Sheet getSheet(int index) {
		Sheet sheet = Sheet.NULL;
		if (init && _inRange(index)) {
			sheet = _getSheet(workbook.getSheetAt(index));
		}
		return sheet;
	}
	
	/**
	 * 创建Sheet页（Sheet页名称为默认名称"SheetN"�?
	 * @return Sheet页对象（若初始化失败则返回Sheet.NULL对象�?
	 */
	public Sheet createSheet() {
		return createSheet(_getDefaultSheetName());
	}
	
	/**
	 * 创建Sheet�?
	 * @param name Sheet页名�?
	 * @return Sheet页对象（若初始化失败则返回Sheet.NULL对象�?
	 */
	public Sheet createSheet(String name) {
		Sheet sheet = Sheet.NULL;
		try {
			org.apache.poi.ss.usermodel.Sheet poiSheet = StrUtils.isEmpty(name) ? 
					workbook.createSheet() : workbook.createSheet(name);
			sheet = _getSheet(poiSheet);
			
		} catch(Exception e) {
			log.error("创建sheet�? [{}] 失败", name, e);
		}
		return sheet;
	}
	
	/**
	 * <PRE>
	 * 根据填充的数据，创建多个Sheet分页.
	 * 分页名称�?"sheetNamePrefix-pageIdx", 每页行数不超过pageRowLimit.
	 * </PRE>
	 * @param header 表头（若非空则会出现在每个Sheet页的首行�?
	 * @param datas 填充数据
	 * @param sheetNamePrefix Sheet页名称前缀
	 * @param pageRowLimit 每页的最大行�?
	 * @return 分页�?(给定的填充数据为空、或填充失败、或初始化失败则返回0)
	 */
	public int createPagingSheets(List<String> header, List<List<Object>> datas, 
			String sheetNamePrefix, int pageRowLimit) {
		int page = 0;
		if(init == false || ListUtils.isEmpty(datas)) {
			return page;
		}
		
		if(StrUtils.isEmpty(sheetNamePrefix)) { sheetNamePrefix = "PAGING-SHEET-"; }
		if(pageRowLimit > MAX_ROW()) { pageRowLimit = MAX_ROW(); }
		final int ROW_OFFSET = (ListUtils.isEmpty(header) ? 0 : 1);
		
		int pageRowCnt = 0;
		Sheet sheet = null;
		for(int size = datas.size(), row = 0; row < size; row++) {
			if(pageRowCnt == 0) {	// 新建sheet�?
				String sheetName = StrUtils.concat(sheetNamePrefix, ++page);
				sheet = createSheet(sheetName);
				if(ROW_OFFSET > 0) {	// 设置表头
					sheet.setHeader(header);
				}
			}
			
			List<Object> rowDatas = datas.get(row);
			sheet.setRowDatas(rowDatas, pageRowCnt + ROW_OFFSET);
			if(++pageRowCnt >= pageRowLimit) {
				pageRowCnt = 0;
			}
		}
		return page;
	}
	
	/**
	 * 删除首个Sheet�?
	 * @return true:成功; false失败
	 */
	public boolean delFirstSheet() {
		return delSheet(0);
	}
	
	/**
	 * 删除Sheet�?
	 * @param index sheet页索�?(�?0开�?)
	 * @return true:成功; false失败
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
	
	/**
	 * 保存缓存内容到Excel文件
	 * @return true:成功; false失败
	 */
	public boolean save() {
		return saveAs(xlsFile);
	}
	
	/**
	 * 另存缓存内容到Excel文件(若保存成功则操作的文件对象自动变更为另存的文�?)
	 * @param filePath 另存为的文件路径
	 * @return true:成功; false失败
	 */
	public boolean saveAs(String filePath) {
		boolean isOk = false;
		if(init == true && filePath != null) {
			isOk = saveAs(new File(filePath));
		}
		return isOk;
	}
	
	/**
	 * 另存缓存内容到Excel文件(若保存成功则操作的文件对象自动变更为另存的文�?)
	 * @param file 另存为的文件对象
	 * @return true:成功; false失败
	 */
	public boolean saveAs(File file) {
		boolean isOk = false;
		if(init == false || file == null) {
			return isOk;
		}
		
		if(!file.exists()) {
			String savePath = file.getAbsolutePath();
			file = FileUtils.createFile(savePath);
			if(file == null) {
				log.error("保存xls到文件失�?: 无法创建文件({})", savePath);
				return isOk;
			}
		}

		// 若无sheet页则创建一个临时的sheet页（Excel保存时不允许无sheet页）
		boolean mark = false;
		if(SHEET_SIZE() <= 0) {
			createSheet(_getDefaultSheetName());
			mark = true;
		}
		
		// 把内容覆写到文件
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			workbook.write(fos);
			this.xlsFile = file;
			isOk = true;
			
		} catch (Exception e) {
			log.error("保存xls到文件失�?: [{}]", file.getAbsoluteFile(), e);
			
		} finally {
			IOUtils.close(fos);
		}
		
		// 删除临时sheet�?
		if(mark == true) {
			delFirstSheet();
		}
		return isOk;
	}
	
	/**
	 * <PRE>
	 * 清空Excel工作簿内�?.
	 * 	（仅清空内存数据，不会影响Excel文件�?
	 * </PRE>
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
