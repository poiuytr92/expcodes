package exp.libs.warp.other.xls.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel实体类的抽象类，代表一个Excel文件
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 * 
 */
public abstract class ExcelBean {

	/**
	 * 空Excel对象保存时默认添加的Sheet页名称
	 */
	public static final String DEFAULT_EMPTY_SHEET_NAME = "Sheet1";

	/**
	 * 默认日期单元格的样式
	 */
	private CellStyle dateCellStyle;

	/**
	 * poi原始工作薄对象
	 */
	protected Workbook workbook;

	/**
	 * 根据Excel文件，创建Excel对象
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static ExcelBean create(String filePath)
			throws InvalidFormatException, IOException {
		return create(new File(filePath));
	}

	/**
	 * 根据Excel文件，创建Excel对象
	 * 
	 * @param file
	 *            文件对象
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static ExcelBean create(File file) throws InvalidFormatException,
			IOException {
		return create(new FileInputStream(file));
	}

	/**
	 * 根据Excel输入流，创建Excel对象
	 * 
	 * @param inputStream
	 *            文件输入流
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static ExcelBean create(InputStream inputStream)
			throws InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(inputStream);

		if (workbook instanceof HSSFWorkbook) {
			return new HSSFExcelBean((HSSFWorkbook) workbook);
		} else if (workbook instanceof XSSFWorkbook) {
			return new XSSFExcelBean((XSSFWorkbook) workbook);
		}

		throw new InvalidFormatException("非Excel格式文件！");
	}

	/**
	 * 根据名称获取Sheet对象
	 * 
	 * @param name
	 *            Sheet的名称
	 * @return
	 */
	public SheetBean getSheetBean(String name) {
		Sheet sheet = workbook.getSheet(name);
		return getSheetBean(sheet);
	}

	/**
	 * 根据索引获取Sheet对象
	 * 
	 * @param index
	 *            Sheet的索引
	 * @return
	 */
	public SheetBean getSheetBean(int index) {
		if (index < 0 || index >= workbook.getNumberOfSheets()) {
			return null;
		}

		Sheet sheet = workbook.getSheetAt(index);
		return getSheetBean(sheet);
	}

	/**
	 * 根据poi原始sheet获取Sheet对象
	 * 
	 * @param sheet
	 *            poi原始sheet
	 * @return
	 */
	public SheetBean getSheetBean(Sheet sheet) {
		if (sheet == null) {
			return null;
		}
		return new SheetBean(this, sheet);
	}

	/**
	 * 创建默认名称的Sheet对象
	 * 
	 * @return
	 */
	public SheetBean createSheetBean() {
		Sheet sheet = workbook.createSheet();
		return getSheetBean(sheet);
	}

	/**
	 * 创建指定名称的Sheet对象
	 * 
	 * @param name
	 *            Sheet的名称
	 * @return
	 */
	public SheetBean createSheetBean(String name) {
		Sheet sheet = workbook.createSheet(name);
		return getSheetBean(sheet);
	}

	/**
	 * 获取指定名称的Sheet对象，如果该名称的Sheet不存在，则创建
	 * 
	 * @param name
	 *            Sheet的名称
	 * @return
	 */
	public SheetBean getOrCreateSheetBean(String name) {
		Sheet sheet = workbook.getSheet(name);
		if (sheet == null) {
			sheet = workbook.createSheet(name);
		}
		return getSheetBean(sheet);
	}

	/**
	 * 创建分页sheet页，sheet页名称将加上“_页码”，该方法在数据为空的情况下也会创建一个sheet页
	 * 
	 * @param sheetName
	 *            sheet页名称
	 * @param data
	 *            数据内容
	 * @param containTitle
	 *            数据内容中是否包含列标题
	 * @param pageSize
	 *            每页数据行数（不包含列标题）
	 * @return 创建sheet页个数
	 */
	public int createPagingSheetWithData(String sheetName,
			List<List<Object>> data, boolean containTitle, int pageSize) {
		List<Object> titleContent = null;
		List<List<Object>> dataContent = data;
		if (containTitle && data != null && data.size() > 0) {
			titleContent = data.get(0);
			dataContent = data.subList(1, data.size());
		}

		if (dataContent == null) {
			dataContent = Collections.emptyList();
		}

		// 计算页数
		int page = (int) Math.ceil(dataContent.size() / (double) pageSize);
		if (page == 0) {
			page = 1;
		}

		for (int i = 0; i < page; i++) {
			SheetBean sheetBean = getOrCreateSheetBean(sheetName + "_"
					+ (i + 1));

			int endIndex = (i + 1) * pageSize < dataContent.size() ? ((i + 1) * pageSize)
					: dataContent.size();
			List<List<Object>> pageContent = dataContent.subList(i * pageSize,
					endIndex);

			if (containTitle) {
				List<List<Object>> tmp = new ArrayList<List<Object>>();
				tmp.add(titleContent);
				tmp.addAll(pageContent);
				pageContent = tmp;
			}

			sheetBean.setContent(pageContent);
		}

		return page;
	}

	/**
	 * 保存Excel对象到指定文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void write(String filePath) throws FileNotFoundException,
			IOException {
		write(new File(filePath));
	}

	/**
	 * 保存Excel对象到指定文件
	 * 
	 * @param file
	 *            文件对象
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void write(File file) throws FileNotFoundException, IOException {
		File parentFile = file.getParentFile();
		if (parentFile != null) {
			parentFile.mkdirs();// 创建父目录
		}

		FileOutputStream fos = new FileOutputStream(file);
		write(fos);
		fos.close();
	}

	/**
	 * 保存Excel对象到指定文件
	 * 
	 * @param outputStream
	 *            文件输出流
	 * @throws IOException
	 */
	public void write(OutputStream outputStream) throws IOException {
		if (workbook.getNumberOfSheets() == 0) {// 防止空Excel文件
			workbook.createSheet(DEFAULT_EMPTY_SHEET_NAME);
			workbook.write(outputStream);
			workbook.removeSheetAt(0);
		} else {
			workbook.write(outputStream);
		}
	}

	/**
	 * 获取每个Sheet页支持的行数
	 * 
	 * @return
	 */
	public abstract int getMaxRows();

	/**
	 * 获取每个Sheet页支持的列数
	 * 
	 * @return
	 */
	public abstract int getMaxColumns();

	/**
	 * 获得poi原始的工作薄对象
	 * 
	 * @return
	 */
	public Workbook getWorkbook() {
		return workbook;
	}

	/**
	 * 获取日期单元格的样式
	 * 
	 * @return
	 */
	public CellStyle getDateCellStyle() {
		if (dateCellStyle == null) {
			dateCellStyle = workbook.createCellStyle();
			DataFormat format = workbook.createDataFormat();
			dateCellStyle.setDataFormat(format.getFormat("yyyy-m-d"));
		}

		return dateCellStyle;
	}

}
