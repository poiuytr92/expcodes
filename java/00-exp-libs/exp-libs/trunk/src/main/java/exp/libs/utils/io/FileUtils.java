package exp.libs.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.FileType;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.os.CmdUtils;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.io.flow.FileFlowReader;

/**
 * <PRE>
 * 文件工具
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	/**
	 * 文件头长度表.
	 *  文件后缀 -> 文件头长度
	 */
	private final static Map<String, Integer> HEAD_LENS = 
			new HashMap<String, Integer>();
	
	/** 当前已知的文件类型中, 文件头最长的长度 */
	private static int MAX_HEAD_LEN = -1;

	/**
	 * 文件类型表.
	 *   文件头 -> 文件后缀 -> 文件类型
	 */
	private final static Map<String, Map<String, FileType>> FILE_TYPES = 
			new HashMap<String, Map<String,FileType>>();
	
	/**
	 * 初始化文件类型查询表单
	 */
	static {
		initFileHeadLens();
		initFileTypes();
	}
	
	/**
	 * 初始化文件头长度表
	 */
	private static void initFileHeadLens() {
		HEAD_LENS.put(FileType.UNKNOW.EXT, FileType.UNKNOW.HEAD_LEN);
		HEAD_LENS.put(FileType.TXT.EXT, FileType.TXT.HEAD_LEN);
		HEAD_LENS.put(FileType.BAT.EXT, FileType.BAT.HEAD_LEN);
		HEAD_LENS.put(FileType.BIN.EXT, FileType.BIN.HEAD_LEN);
		HEAD_LENS.put(FileType.INI.EXT, FileType.INI.HEAD_LEN);
		HEAD_LENS.put(FileType.TMP.EXT, FileType.TMP.HEAD_LEN);
		HEAD_LENS.put(FileType.MP3.EXT, FileType.MP3.HEAD_LEN);
		HEAD_LENS.put(FileType.WAVE.EXT, FileType.WAVE.HEAD_LEN);
		HEAD_LENS.put(FileType.MIDI.EXT, FileType.MIDI.HEAD_LEN);
		HEAD_LENS.put(FileType.JPG.EXT, FileType.JPG.HEAD_LEN);
		HEAD_LENS.put(FileType.PNG.EXT, FileType.PNG.HEAD_LEN);
		HEAD_LENS.put(FileType.BMP.EXT, FileType.BMP.HEAD_LEN);
		HEAD_LENS.put(FileType.GIF.EXT, FileType.GIF.HEAD_LEN);
		HEAD_LENS.put(FileType.TIFF.EXT, FileType.TIFF.HEAD_LEN);
		HEAD_LENS.put(FileType.CAD.EXT, FileType.CAD.HEAD_LEN);
		HEAD_LENS.put(FileType.PSD.EXT, FileType.PSD.HEAD_LEN);
		HEAD_LENS.put(FileType.RTF.EXT, FileType.RTF.HEAD_LEN);
		HEAD_LENS.put(FileType.XML.EXT, FileType.XML.HEAD_LEN);
		HEAD_LENS.put(FileType.HTML.EXT, FileType.HTML.HEAD_LEN);
		HEAD_LENS.put(FileType.EMAIL.EXT, FileType.EMAIL.HEAD_LEN);
		HEAD_LENS.put(FileType.OUTLOOK.EXT, FileType.OUTLOOK.HEAD_LEN);
		HEAD_LENS.put(FileType.OE.EXT, FileType.OE.HEAD_LEN);
		HEAD_LENS.put(FileType.ACCESS.EXT, FileType.ACCESS.HEAD_LEN);
		HEAD_LENS.put(FileType.DOC.EXT, FileType.DOC.HEAD_LEN);
		HEAD_LENS.put(FileType.XLS.EXT, FileType.XLS.HEAD_LEN);
		HEAD_LENS.put(FileType.PPT.EXT, FileType.PPT.HEAD_LEN);
		HEAD_LENS.put(FileType.DOCX.EXT, FileType.DOCX.HEAD_LEN);
		HEAD_LENS.put(FileType.XLSX.EXT, FileType.XLSX.HEAD_LEN);
		HEAD_LENS.put(FileType.PPTX.EXT, FileType.PPTX.HEAD_LEN);
		HEAD_LENS.put(FileType.ZIP.EXT, FileType.ZIP.HEAD_LEN);
		HEAD_LENS.put(FileType.RAR.EXT, FileType.RAR.HEAD_LEN);
		HEAD_LENS.put(FileType.TAR.EXT, FileType.TAR.HEAD_LEN);
		HEAD_LENS.put(FileType.GZ.EXT, FileType.GZ.HEAD_LEN);
		HEAD_LENS.put(FileType.BZ2.EXT, FileType.BZ2.HEAD_LEN);
		HEAD_LENS.put(FileType.WPD.EXT, FileType.WPD.HEAD_LEN);
		HEAD_LENS.put(FileType.EPS.EXT, FileType.EPS.HEAD_LEN);
		HEAD_LENS.put(FileType.PS.EXT, FileType.PS.HEAD_LEN);
		HEAD_LENS.put(FileType.PDF.EXT, FileType.PDF.HEAD_LEN);
		HEAD_LENS.put(FileType.QDF.EXT, FileType.QDF.HEAD_LEN);
		HEAD_LENS.put(FileType.PWL.EXT, FileType.PWL.HEAD_LEN);
		HEAD_LENS.put(FileType.AVI.EXT, FileType.AVI.HEAD_LEN);
		HEAD_LENS.put(FileType.RAM.EXT, FileType.RAM.HEAD_LEN);
		HEAD_LENS.put(FileType.RM.EXT, FileType.RM.HEAD_LEN);
		HEAD_LENS.put(FileType.MPEG_VIDEO.EXT, FileType.MPEG_VIDEO.HEAD_LEN);
		HEAD_LENS.put(FileType.MPEG.EXT, FileType.MPEG.HEAD_LEN);
		HEAD_LENS.put(FileType.MOV.EXT, FileType.MOV.HEAD_LEN);
		HEAD_LENS.put(FileType.ASF.EXT, FileType.ASF.HEAD_LEN);
		HEAD_LENS.put(FileType.DLL.EXT, FileType.DLL.HEAD_LEN);
		HEAD_LENS.put(FileType.EXE.EXT, FileType.EXE.HEAD_LEN);
		
		Iterator<Integer> lens = HEAD_LENS.values().iterator();
		while(lens.hasNext()) {
			Integer len = lens.next().intValue();
			MAX_HEAD_LEN = (MAX_HEAD_LEN < len ? len : MAX_HEAD_LEN);
		}
	}
	
	/**
	 * 初始化文件类型表
	 */
	private static void initFileTypes() {
		FILE_TYPES.put("", toMap(FileType.UNKNOW, FileType.TXT, FileType.BAT, 
				FileType.BIN, FileType.INI, FileType.TMP, FileType.MP3));
		FILE_TYPES.put("57415645", toMap(FileType.WAVE));
		FILE_TYPES.put("4D546864", toMap(FileType.MIDI));
		FILE_TYPES.put("FFD8FF", toMap(FileType.JPG));
		FILE_TYPES.put("89504E47", toMap(FileType.PNG));
		FILE_TYPES.put("424D", toMap(FileType.BMP));
		FILE_TYPES.put("47494638", toMap(FileType.GIF));
		FILE_TYPES.put("49492A00", toMap(FileType.TIFF));
		FILE_TYPES.put("41433130", toMap(FileType.CAD));
		FILE_TYPES.put("38425053", toMap(FileType.PSD));
		FILE_TYPES.put("7B5C727466", toMap(FileType.RTF));
		FILE_TYPES.put("3C3F786D6C", toMap(FileType.XML));
		FILE_TYPES.put("68746D6C3E", toMap(FileType.HTML));
		FILE_TYPES.put("44656C69766572792D646174653A", toMap(FileType.EMAIL));
		FILE_TYPES.put("2142444E", toMap(FileType.OUTLOOK));
		FILE_TYPES.put("CFAD12FEC5FD746F", toMap(FileType.OE));
		FILE_TYPES.put("5374616E64617264204A", toMap(FileType.ACCESS));
		FILE_TYPES.put("D0CF11E0", toMap(FileType.DOC, FileType.XLS, FileType.PPT));
		FILE_TYPES.put("504B0304", toMap(FileType.DOCX, FileType.XLSX, FileType.PPTX, FileType.ZIP));
		FILE_TYPES.put("52617221", toMap(FileType.RAR));
		FILE_TYPES.put("1F9D", toMap(FileType.TAR));
		FILE_TYPES.put("1F8B", toMap(FileType.GZ));
		FILE_TYPES.put("425A68", toMap(FileType.BZ2));
		FILE_TYPES.put("FF575043", toMap(FileType.WPD));
		FILE_TYPES.put("252150532D41646F6265", toMap(FileType.PS, FileType.EPS));
		FILE_TYPES.put("255044462D312E", toMap(FileType.PDF));
		FILE_TYPES.put("AC9EBD8F", toMap(FileType.QDF));
		FILE_TYPES.put("E3828596", toMap(FileType.PWL));
		FILE_TYPES.put("41564920", toMap(FileType.AVI));
		FILE_TYPES.put("2E7261FD", toMap(FileType.RAM));
		FILE_TYPES.put("2E524D46", toMap(FileType.RM));
		FILE_TYPES.put("000001B3", toMap(FileType.MPEG_VIDEO));
		FILE_TYPES.put("000001BA", toMap(FileType.MPEG));
		FILE_TYPES.put("6D6F6F76", toMap(FileType.MOV));
		FILE_TYPES.put("3026B2758E66CF11", toMap(FileType.ASF));
		FILE_TYPES.put("4D5A90", toMap(FileType.DLL, FileType.EXE));
	}
	
	/**
	 * 把若干个文件类型构造成Hash表单
	 * @param fileTypes
	 * @return Map: 文件后缀 -> 文件类型
	 */
	private static Map<String, FileType> toMap(FileType... fileTypes) {
		Map<String, FileType> map = new HashMap<String, FileType>();
		if(fileTypes != null) {
			for(FileType fileType : fileTypes) {
				map.put(fileType.EXT, fileType);
			}
		}
		return map;
	}
	
	/** 私有化构造函数 */
	protected FileUtils() {}
	
	/**
	 * 检查文件是否存在
	 * @param filePath 文件路径
	 * @return true:存在; false:不存在
	 */
	public static boolean exists(String filePath) {
		return (filePath != null && exists(new File(filePath)));
		
	}
	
	/**
	 * 检查文件是否存在
	 * @param file 文件
	 * @return true:存在; false:不存在
	 */
	public static boolean exists(File file) {
		return (file != null && file.exists());
	}
	
	/**
	 * 检查目录是否为空
	 * @param dirPath 目录路径
	 * @return true:空或不存在; false:存在且非空
	 */
	public static boolean isEmpty(String dirPath) {
		return (dirPath == null || isEmpty(new File(dirPath)));
	}
	
	/**
	 * 检查目录是否为空
	 * @param dir 目录
	 * @return true:空或不存在; false:存在且非空
	 */
	public static boolean isEmpty(File dir) {
		return (!exists(dir) || dir.listFiles().length <= 0);
	}
	
	/**
	 * 测试文件类型是否为[文件]
	 * @param filePath 文件路径
	 * @return true:是; false:否
	 */
	public static boolean isFile(String filePath) {
		File file = new File(filePath);
		return file.isFile();
	}
	
	/**
	 * 测试文件类型是否为[文件夹]
	 * @param filePath 文件路径
	 * @return true:是; false:否
	 */
	public static boolean isDirectory(String filePath) {
		File file = new File(filePath);
		return file.isDirectory();
	}
	
	/**
	 * 获取文件名
	 * @param filePath 文件路径
	 * @return 文件名（包括后缀）
	 */
	public static String getName(String filePath) {
		File file = new File(filePath);
		return file.getName();
	}

	/**
	 * 复制文件
	 * @param srcPath 源位置
	 * @param snkPath 目标位置
	 */
	public static void copyFile(String srcPath, String snkPath) {
		File srcFile = new File(srcPath);
		File snkFile = new File(snkPath);
		copyFile(srcFile, snkFile);
	}
	
	/**
	 * 复制文件
	 * @param srcFile 源文件
	 * @param snkFile 目标文件
	 */
	public static void copyFile(File srcFile, File snkFile) {
		try {
			org.apache.commons.io.FileUtils.copyFile(srcFile, snkFile);
			
		} catch (Exception e) {
			log.error("复制文件失败: 从 [{}] 到 [{}].", 
					(srcFile == null ? "null" : srcFile.getPath()), 
					(snkFile == null ? "null" : snkFile.getPath()), e);
		}
	}

	/**
	 * 复制文件夹
	 * @param srcPath 原位置
	 * @param snkPath 目标位置
	 */
	public static void copyDirectory(String srcPath, String snkPath) {
		File srcFile = new File(srcPath);
		File snkFile = new File(snkPath);
		copyDirectory(srcFile, snkFile);
	}
	
	/**
	 * 复制文件夹
	 * @param srcDir 源目录
	 * @param snkDir 目标目录
	 */
	public static void copyDirectory(File srcDir, File snkDir)  {
		try {
			org.apache.commons.io.FileUtils.copyDirectory(srcDir, snkDir);
			
		} catch (Exception e) {
			log.error("复制文件夹失败: 从 [{}] 到 [{}].", 
					(srcDir == null ? "null" : srcDir.getPath()), 
					(snkDir == null ? "null" : snkDir.getPath()), e);
		}
	}
	
	/**
	 * 移动文件
	 * @param srcPath 源位置
	 * @param snkPath 目标位置
	 */
	public static void moveFile(String srcPath, String snkPath) {
		File srcFile = new File(srcPath);
		File snkFile = new File(snkPath);
		moveFile(srcFile, snkFile);
	}
	
	/**
	 * 移动文件
	 * @param srcFile 源文件
	 * @param snkFile 目标文件
	 */
	public static void moveFile(File srcFile, File snkFile) {
		try {
			org.apache.commons.io.FileUtils.moveFile(srcFile, snkFile);
			
		} catch (Exception e) {
			log.error("移动文件失败: 从 [{}] 到 [{}].", 
					(srcFile == null ? "null" : srcFile.getPath()), 
					(snkFile == null ? "null" : snkFile.getPath()), e);
		}
	}
	
	/**
	 * 移动文件夹
	 * @param srcPath 源位置
	 * @param snkPath 目标位置
	 */
	public static void moveDirectory(String srcPath, String snkPath) {
		File srcFile = new File(srcPath);
		File snkFile = new File(snkPath);
		moveDirectory(srcFile, snkFile);
	}
	
	/**
	 * 移动文件夹 
	 * @param srcDir 源目录
	 * @param snkDir 目标目录
	 */
	public static void moveDirectory(File srcDir, File snkDir)  {
		try {
			org.apache.commons.io.FileUtils.moveDirectory(srcDir, snkDir);
			
		} catch (Exception e) {
			log.error("移动文件夹失败: 从 [{}] 到 [{}].", 
					(srcDir == null ? "null" : srcDir.getPath()), 
					(snkDir == null ? "null" : snkDir.getPath()), e);
		}
	}
	
	/**
	 * 创建文件（若为linux系统, 创建的文件会自动授权可读写）
	 * @param filePath 文件路径
	 * @return true:创建成功; false:创建失败
	 */
	public static File createFile(String filePath) {
		return create(filePath, true);
	}
	
	/**
	 * 创建目录（若为linux系统, 创建的目录会自动授权可读写）
	 * @param dirPath 目录路径
	 * @return true:创建成功; false:创建失败
	 */
	public static File createDir(String dirPath) {
		return create(dirPath, false);
	}

	/**
	 * 创建文件/目录
	 * @param path 文件/目录位置
	 * @param isFile true:创建文件; false:创建目录
	 * @return true:创建成功; false:创建失败
	 */
	public static File create(String path, boolean isFile) {
		File file = new File(path);
		file.setWritable(true, false); // 处理linux的权限问题

		boolean isCreated = true;
		try {
			if (file.exists() == false) {
				if (false == file.getParentFile().exists()) {
					isCreated = file.getParentFile().mkdirs();
				}
				isCreated = (isFile ? file.createNewFile() : file.mkdir());
			}
		} catch (Exception e) {
			isCreated = false;
			log.error("创建文件{} [{}] 失败", (isFile ? "" : "夹"), path, e);
		}
		
		file = (isCreated ? file : null);
		return file;
	}
	
	/**
	 * 删除文件/目录(包括子文件/子目录)
	 * @param path 文件/目录路径
	 * @return true:全部删除成功; false:全部删除失败
	 */
	public static boolean delete(String path) {
		return delete(path, "");
	}
	
	/**
	 * 删除文件/目录(包括子文件/子目录)
	 * @param path 文件/目录路径
	 * @param filterRegex 过滤正则（匹配过滤的文件/目录保留）
	 * @return true:全部删除成功; false:全部删除失败
	 */
	public static boolean delete(String path, String filterRegex) {
		boolean isOk = true;
		if(StrUtils.isNotEmpty(path)) {
			isOk = delete(new File(path), filterRegex);
		}
		return isOk;
	}
	
	/**
	 * 删除文件/目录(包括子文件/子目录)
	 * @param file 文件/目录
	 * @return true:全部删除成功; false:全部删除失败
	 */
	public static boolean delete(File file) {
		return delete(file, "");
	}
	
	/**
	 * 删除文件/目录(包括子文件/子目录)
	 * @param file 文件/目录
	 * @param filterRegex 过滤正则（匹配过滤的文件/目录保留）
	 * @return true:全部删除成功; false:全部删除失败
	 */
	public static boolean delete(File file, String filterRegex) {
		boolean isOk = true;
		if(file != null && file.exists()) {
			if(file.isFile()) {
				if(!RegexUtils.matches(file.getName(), filterRegex)) {
					isOk &= file.delete();
				}
				
			} else if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File f : files) {
					isOk &= delete(f, filterRegex);
				}
				
				if(!RegexUtils.matches(file.getName(), filterRegex)) {
					isOk &= file.delete();
				}
			}
		}
		return isOk;
	}
	
	/**
	 * <PRE>
	 * 使用系统默认编码读取文件内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
	 * @param filePath 文件路径
	 * @return 文件内容
	 */
	public static String read(String filePath) {
		if(filePath == null) {
			return "";
		}
        return read(new File(filePath));
    }
	
	/**
	 * <PRE>
	 * 使用系统默认编码读取文件内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
	 * @param file 文件
	 * @return 文件内容
	 */
    public static String read(File file) {
    	String s = "";
        try {
			s = org.apache.commons.io.FileUtils.readFileToString(file);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return s;
    }
    
    /**
     * <PRE>
	 * 读取文件内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param filePath 文件路径
     * @param charset 文件编码
     * @return 文件内容
     */
    public static String read(String filePath, String charset) {
    	if(filePath == null) {
			return "";
		}
        return read(new File(filePath), charset);
    }
    
    /**
     * <PRE>
	 * 读取文件内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param file 文件
     * @param charset 文件编码
     * @return 文件内容
     */
    public static String read(File file, String charset) {
    	String s = "";
        try {
			s = org.apache.commons.io.FileUtils.readFileToString(file, charset);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return s;
    }
    
    /**
     * <PRE>
	 * 使用系统默认编码分行读取文件所有内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static List<String> readLines(String filePath) {
        return readLines(new File(filePath));
	}
    
    /**
     * <PRE>
	 * 使用系统默认编码分行读取文件所有内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param file 文件
     * @return 文件内容
     */
    public static List<String> readLines(File file) {
    	List<String> lines = new LinkedList<String>();
        try {
        	lines = org.apache.commons.io.FileUtils.readLines(file);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return lines;
	}
    
    /**
     * <PRE>
	 * 分行读取文件所有内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param filePath 文件路径
     * @param charset 文件编码
     * @return 文件内容
     */
    public static List<String> readLines(String filePath, String charset) {
    	return readLines(new File(filePath), charset);
	}
    
    /**
     * <PRE>
	 * 分行读取文件所有内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param file 文件
     * @param charset 文件编码
     * @return 文件内容
     */
    public static List<String> readLines(File file, String charset) {
    	List<String> lines = new LinkedList<String>();
        try {
        	lines = org.apache.commons.io.FileUtils.readLines(file, charset);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return lines;
	}
    
    /**
     * <PRE>
	 * 分行读取文件所有内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param filePath 文件路径
     * @param charset 文件编码
     * @return 文件内容
     */
    public static List<String> readLines(String filePath, Charset charset) {
    	return readLines(new File(filePath), charset);
	}
    
    /**
     * <PRE>
	 * 分行读取文件所有内容.
	 * 	(此方法会一次性读取文件内所有内容, 不适用于大文件读取)
	 * </PRE>
     * @param file 文件
     * @param charset 文件编码
     * @return 文件内容
     */
    public static List<String> readLines(File file, Charset charset) {
    	List<String> lines = new LinkedList<String>();
        try {
        	lines = org.apache.commons.io.FileUtils.readLines(file, charset);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return lines;
	}

    /**
     * <PRE>
	 * 流式读取文件内容.
	 * 	(此方法会流式分段读取文件内容，适用于读取任意文件)
	 * 
	 * 示例:
	 * 	FileFlowReader ffr = readFlow(FILE_PATH, Charset.UTF8);
	 *  while(ffr.hasNextLine()) {
	 *  	String line = ffr.readLine('\n');
	 *  	// ... do for line
	 *  }
	 *  ffr.close();
	 * </PRE>
     * @param filePath 文件路径
     * @param charset 文件编码
     * @return 文件流式读取器
     */
    public static FileFlowReader readFlow(String filePath, String charset) {
    	return new FileFlowReader(filePath, charset);
    }
    
    /**
     * <PRE>
	 * 流式读取文件内容.
	 * 	(此方法会流式分段读取文件内容，适用于读取任意文件)
	 * 
	 * 示例:
	 * 	FileFlowReader ffr = readFlow(FILE_PATH, Charset.UTF8);
	 *  while(ffr.hasNextLine()) {
	 *  	String line = ffr.readLine('\n');
	 *  	// ... do for line
	 *  }
	 *  ffr.close();
	 * </PRE>
     * @param file 文件
     * @param charset 文件编码
     * @return 文件流式读取器
     */
    public static FileFlowReader readFlow(File file, String charset) {
    	return new FileFlowReader(file, charset);
    }
    
    /**
     * <PRE>
	 * 把数据覆写到指定文件.
	 * </PRE>
     * @param filePath 文件路径
     * @param data 文件数据(使用系统默认编码)
     * @return true：写入成功; false:写入失败
     */
	public static boolean write(String filePath, String data) {
    	return write(new File(filePath), data);
	}
	
	/**
     * <PRE>
	 * 把数据覆写到指定文件.
	 * </PRE>
     * @param file 文件
     * @param data 文件数据(使用系统默认编码)
     * @return true：写入成功; false:写入失败
     */
	public static boolean write(File file, String data) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	/**
	 * <PRE>
	 * 把数据写到指定文件.
	 * </PRE>
	 * @param filePath 文件路径
	 * @param data 文件数据(使用系统默认编码)
	 * @param append true:附加到末尾; false:覆写
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(String filePath, String data, boolean append) {
		return write(new File(filePath), data, append);
	}
	
	/**
	 * <PRE>
	 * 把数据写到指定文件.
	 * </PRE>
	 * @param file 文件
	 * @param data 文件数据(使用系统默认编码)
	 * @param append true:附加到末尾; false:覆写
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(File file, String data, boolean append) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, append);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	/**
	 * <PRE>
	 * 把数据覆写到指定文件.
	 * </PRE>
	 * @param filePath 文件路径
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(String filePath, String data, String charset) {
		return write(new File(filePath), data, charset);
	}
	
	/**
	 * <PRE>
	 * 把数据覆写到指定文件.
	 * </PRE>
	 * @param file 文件
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(File file, String data, String charset) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, charset);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	/**
	 * <PRE>
	 * 把数据覆写到指定文件.
	 * </PRE>
	 * @param filePath 文件路径
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(String filePath, String data, Charset charset) {
		return write(new File(filePath), data, charset);
	}
	
	/**
	 * <PRE>
	 * 把数据覆写到指定文件.
	 * </PRE>
	 * @param file 文件
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(File file, String data, Charset charset) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, charset);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	/**
	 * <PRE>
	 * 把数据写到指定文件.
	 * </PRE>
	 * @param filePath 文件路径
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @param append true:附加到末尾; false:覆写
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(String filePath, String data, String charset, boolean append) {
		return write(new File(filePath), data, charset, append);
	}
	
	/**
	 * <PRE>
	 * 把数据写到指定文件.
	 * </PRE>
	 * @param file 文件
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @param append true:附加到末尾; false:覆写
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(File file, String data, String charset, boolean append) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, charset, append);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	/**
	 * <PRE>
	 * 把数据写到指定文件.
	 * </PRE>
	 * @param filePath 文件路径
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @param append true:附加到末尾; false:覆写
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(String filePath, String data, Charset charset, boolean append) {
		return write(new File(filePath), data, charset, append);
	}
	
	/**
	 * <PRE>
	 * 把数据写到指定文件.
	 * </PRE>
	 * @param file 文件
	 * @param data 文件数据
	 * @param charset 数据编码
	 * @param append true:附加到末尾; false:覆写
	 * @return true：写入成功; false:写入失败
	 */
	public static boolean write(File file, String data, Charset charset, boolean append) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, charset, append);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	/**
	 * 计算文件/目录大小
	 * @param filePath 文件/目录路径
	 * @return 文件/目录大小，单位bytes
	 */
	public static long getSize(String filePath) {
		long size = 0;
		if(StrUtils.isNotTrimEmpty(filePath)) {
			size = getSize(new File(filePath));
		}
		return size;
	}
	
	/**
	 * 计算文件/目录大小
	 * @param file 文件/目录
	 * @return 文件/目录大小，单位bytes
	 */
	public static long getSize(File file) {
		long size = 0;
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				for (File sFile : file.listFiles()) {
					size += getSize(sFile);
				}
			} else {
				size = file.length();
			}
		}
		return size;
	}
	
	/**
	 * 获取文件类型
	 * @param filePath 文件路径
	 * @return 文件类型
	 */
	public static FileType getFileType(String filePath) {
		FileType fileType = FileType.UNKNOW;
		if(StrUtils.isNotTrimEmpty(filePath)) {
			fileType = getFileType(new File(filePath));
		}
		return fileType;
	}
	
	/**
	 * 获取文件类型
	 * @param file 文件
	 * @return 文件类型
	 */
	public static FileType getFileType(File file) {
		FileType fileType = FileType.UNKNOW;
		
		// 基于文件后缀ext与文件头header是正确配对的前提下, 验证猜测文件类型
		// (先用文件后缀获取理论文件头，再用实际文件头匹配理论文件头)
		String ext = getExtension(file);
		if(StrUtils.isNotTrimEmpty(ext)) {
			Integer headLen = HEAD_LENS.get(ext);
			if(headLen != null) {
				String header = _getHeader(file, headLen.intValue());
				fileType = _toFileType(header, ext);
			}
		}
		
		// 基于文件后缀ext与文件头header是不匹配的前提下, 通过文件头猜测文件类型
		if(fileType == FileType.UNKNOW) {
			String fileHeader = _getHeader(file, MAX_HEAD_LEN);
			Iterator<String> headers = FILE_TYPES.keySet().iterator();
			while(headers.hasNext()) {
				String header = headers.next();
				if(StrUtils.equals("", fileHeader, header) || 
						(!"".equals(header) && fileHeader.startsWith(header))) {
					Map<String, FileType> types = FILE_TYPES.get(header);
					if(types.size() == 1) {
						fileType = types.values().iterator().next();
						
					} else {
						log.error("判定文件 [{}] 的文件类型失败: 其文件后缀被篡改 (它可能是 {} 中的一个)", 
								file.getName(), types.values().toString());
					}
					break;
				}
			}
		}
		return fileType;
	}
	
	/**
	 * 根据文件头和文件后缀转换成文件类型对象
	 * @param header 16进制文件头
	 * @param ext 含.的文件后缀
	 * @return 文件类型对象
	 */
	private static FileType _toFileType(String header, String ext) {
		FileType fileType = null;
		if(StrUtils.isNotTrimEmpty(header, ext)) {
			header = header.trim().toUpperCase();
			ext = ext.trim().toLowerCase();
			
			Map<String, FileType> types = FILE_TYPES.get(header);
			if(types != null) {
				fileType = types.get(ext);
			}
		}
		return (fileType == null ? FileType.UNKNOW : fileType);
	}
	
	/**
	 * <pre>
	 * 获取文件头信息.
	 * </pre>
	 * @param file 文件
	 * @param headLen 文件头信息长度
	 * @return 文件头信息
	 */
	private static String _getHeader(File file, int headLen) {
		String header = "";
		if(headLen <= 0) {
			return header;
		}
		
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			byte[] bytes = new byte[headLen];
			is.read(bytes, 0, bytes.length);
			header = BODHUtils.toHex(bytes);
			
		} catch (Exception e) {
			log.error("获取文件 [{}] 的文件头信息失败.", 
					(file == null ? "null" : file.getAbsolutePath()), e);
			
		} finally {
			IOUtils.close(is);
		}
		return header;
	}
	
	/**
	 * 获取文件扩展名(包括[.]符号)
	 * @param file 文件
	 * @return 文件扩展名(全小写, 包括[.]符号)
	 */
	public static String getExtension(File file) {
		return getExtension(file == null ? "" : file.getName());
	}
	
	/**
	 * 获取文件扩展名(包括[.]符号)
	 * @param fileName 文件名
	 * @return 文件扩展名(全小写, 包括[.]符号)
	 */
	public static String getExtension(String fileName) {
		String extension = "";
		if(fileName == null) {
			return extension;
		}
		
		int pos = fileName.lastIndexOf(".");
		if (pos != -1) {
			extension = fileName.substring(pos).toLowerCase();
		}
		return extension;
	}
	
	/**
	 * 列举目录下的文件清单
	 * @param dirPath 目录位置
	 * @param extension 文件后缀
	 * @return 后缀匹配的文件清单
	 */
	public static List<File> listFiles(String dirPath, String extension) {
		return listFiles((dirPath == null ? null : new File(dirPath)), extension);
	}
	
	/**
	 * 列举目录下的文件清单
	 * @param dirPath 目录位置
	 * @param extension 文件后缀
	 * @return 后缀匹配的文件清单
	 */
	public static List<File> listFiles(File dir, String extension) {
		List<File> list = new LinkedList<File>();
		if(dir != null) {
			if(dir.exists()) {
				if(dir.isFile()) {
					if(_match(dir, extension)) {
						list.add(dir);
					}
					
				} else {
					File[] files = dir.listFiles();
					for(File file : files) {
						if(_match(file, extension)) {
							list.add(file);
						}
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 检查文件与后缀是否匹配
	 * @param file 文件
	 * @param extension 后缀
	 * @return true:匹配; false:不匹配
	 */
	private static boolean _match(File file, String extension) {
		return (StrUtils.isEmpty(extension) || 
				file.getName().toLowerCase().endsWith(extension.toLowerCase()));
	}
	
	/**
	 * <PRE>
	 * 隐藏文件/文件夹
	 * 	此方法仅适用于win系统. 
	 * 	linux系统直接在文件名前加.即可实现隐藏
	 * </PRE>
	 * @param filePath 文件路径
	 * @return true:隐藏成功; false:隐藏失败
	 */
	public static boolean hide(String filePath) {
		boolean isOk = false;
		if(StrUtils.isNotEmpty(filePath)) {
			isOk = hide(new File(filePath));
		}
		return isOk;
	}
	
	/**
	 * <PRE>
	 * 隐藏文件/文件夹
	 * 	此方法仅适用于win系统. 
	 * 	linux系统直接在文件名前加.即可实现隐藏
	 * </PRE>
	 * @param filePath
	 * @return
	 */
	public static boolean hide(File file) {
		boolean isOk = false;
		if(file != null && file.exists()) {
			if(OSUtils.isWin()) {
				String cmd = StrUtils.concat("attrib +H \"", 
						file.getAbsolutePath(), "\"");
				isOk = StrUtils.isTrimEmpty(CmdUtils.execute(cmd));
				
			} else if(OSUtils.isUnix()) {
				isOk = file.getName().startsWith(".");
			}
		}
		return isOk;
	}
	
	/**
	 * 移除不允许出现在文件名中的特殊字符
	 * @param fileName 文件名
	 * @param symbol 用于替代被移除的特殊字符
	 * @return 移除特殊字符后的文件名
	 */
	public static String delForbidCharInFileName(String fileName, String symbol) {
		String name = (fileName == null ? "" : fileName);
		return name.replaceAll("[/\\\\:\\*\"<>\\|\\?\r\n\t\0]", symbol);
	}
	
}
