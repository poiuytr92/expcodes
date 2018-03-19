package exp.libs.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

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
		return delete(new File(path));
	}
	
	/**
	 * 删除文件/目录(包括子文件/子目录)
	 * @param path 文件/目录路径
	 * @param filterRegex 过滤正则（匹配过滤的文件/目录保留）
	 * @return true:全部删除成功; false:全部删除失败
	 */
	public static boolean delete(String path, String filterRegex) {
		return delete(new File(path), filterRegex);
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
		if(file.exists()) {
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
		return FileType.toFileType(getHexHeader(filePath));
	}
	
	/**
	 * 获取文件类型
	 * @param file 文件
	 * @return 文件类型
	 */
	public static FileType getFileType(File file) {
		return FileType.toFileType(getHexHeader(file));
	}
	
	/**
	 * 获取文件头信息
	 * @param filePath 文件路径
	 * @return 文件头信息
	 */
	public static String getHexHeader(String filePath) {
		String head = "";
		if(filePath != null) {
			head = getHexHeader(new File(filePath));
		}
		return head;
	}
	
	/**
	 * 获取文件头信息
	 * @param file 文件
	 * @return 文件头信息
	 */
	public static String getHexHeader(File file) {
		String head = "";
		try {
			FileInputStream is = new FileInputStream(file);
			byte[] bytes = new byte[4];
			
			is.read(bytes, 0, bytes.length);
			is.close();
			
			head = BODHUtils.toHex(bytes);
			
		} catch (Exception e) {
			log.error("获取文件 [{}] 的文件头信息失败.", 
					(file == null ? "null" : file.getAbsolutePath()), e);
		}
		return head;
	}
	
	/**
	 * 获取文件扩展名(包括[.]符号)
	 * @param file 文件
	 * @return 文件扩展名(包括[.]符号)
	 */
	public static String getExtension(File file) {
		return getExtension(file == null ? "" : file.getName());
	}
	
	/**
	 * 获取文件扩展名(包括[.]符号)
	 * @param fileName 文件名
	 * @return 文件扩展名(包括[.]符号)
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
