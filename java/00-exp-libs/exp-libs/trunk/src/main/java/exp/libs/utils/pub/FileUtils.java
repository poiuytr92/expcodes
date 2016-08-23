package exp.libs.utils.pub;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.StorageUnit;

/**
 * <PRE>
 * 文件工具包
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
	
	protected FileUtils() {}
	
	public static boolean exists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
	
	public static boolean isFile(String filePath) {
		File file = new File(filePath);
		return file.isFile();
	}
	
	public static boolean isDirectory(String filePath) {
		File file = new File(filePath);
		return file.isDirectory();
	}
	
	public static String getName(String filePath) {
		File file = new File(filePath);
		return file.getName();
	}

	public static void copyFile(String srcPath, String destPath) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		copyFile(srcFile, destFile);
	}
	
	public static void copyFile(File srcFile, File destFile) {
		try {
			org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
			
		} catch (Exception e) {
			log.error("复制文件失败: 从 [{}] 到 [{}].", 
					(srcFile == null ? "null" : srcFile.getPath()), 
					(destFile == null ? "null" : destFile.getPath()), e);
		}
	}

	public static void copyDirectory(String srcPath, String destPath) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		copyDirectory(srcFile, destFile);
	}
	
	public static void copyDirectory(File srcDir, File destDir)  {
		try {
			org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir);
			
		} catch (Exception e) {
			log.error("复制文件夹失败: 从 [{}] 到 [{}].", 
					(srcDir == null ? "null" : srcDir.getPath()), 
					(destDir == null ? "null" : destDir.getPath()), e);
		}
	}
	
	public static void moveFile(String srcPath, String destPath) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		moveFile(srcFile, destFile);
	}
	
	public static void moveFile(File srcFile, File destFile) {
		try {
			org.apache.commons.io.FileUtils.moveFile(srcFile, destFile);
			
		} catch (Exception e) {
			log.error("移动文件失败: 从 [{}] 到 [{}].", 
					(srcFile == null ? "null" : srcFile.getPath()), 
					(destFile == null ? "null" : destFile.getPath()), e);
		}
	}
	
	public static void moveDirectory(String srcPath, String destPath) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		moveDirectory(srcFile, destFile);
	}
	
	public static void moveDirectory(File srcDir, File destDir)  {
		try {
			org.apache.commons.io.FileUtils.moveDirectory(srcDir, destDir);
			
		} catch (Exception e) {
			log.error("移动文件夹失败: 从 [{}] 到 [{}].", 
					(srcDir == null ? "null" : srcDir.getPath()), 
					(destDir == null ? "null" : destDir.getPath()), e);
		}
	}
	
	public static File createFile(String filePath) {
		return create(filePath, true);
	}
	
	public static File createDir(String dirPath) {
		return create(dirPath, false);
	}

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
	
	public static boolean delete(String path) {
		return delete(new File(path));
	}
	
	public static boolean delete(File file) {
		boolean isOk = true;
		if(file.exists()) {
			if(file.isFile()) {
				isOk &= file.delete();
				
			} else if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File f : files) {
					isOk &= delete(f);
				}
				isOk &= file.delete();
			}
		}
		return isOk;
	}
	
	public static String read(String filePath) {
		if(filePath == null) {
			return "";
		}
        return read(new File(filePath));
    }
	
    public static String read(File file) {
    	String s = "";
        try {
			s = org.apache.commons.io.FileUtils.readFileToString(file);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return s;
    }
    
    public static String read(String filePath, String encoding) {
    	if(filePath == null) {
			return "";
		}
        return read(new File(filePath), encoding);
    }
    
    public static String read(File file, String encoding) {
    	String s = "";
        try {
			s = org.apache.commons.io.FileUtils.readFileToString(file, encoding);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return s;
    }
    
    public static List<String> readLines(String filePath) {
        return readLines(new File(filePath));
	}
    
    public static List<String> readLines(File file) {
    	List<String> lines = new LinkedList<String>();
        try {
        	lines = org.apache.commons.io.FileUtils.readLines(file);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return lines;
	}
    
    public static List<String> readLines(String filePath, String encoding) {
    	return readLines(new File(filePath), encoding);
	}
    
    public static List<String> readLines(File file, String encoding) {
    	List<String> lines = new LinkedList<String>();
        try {
        	lines = org.apache.commons.io.FileUtils.readLines(file, encoding);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return lines;
	}
    
    public static List<String> readLines(String filePath, Charset encoding) {
    	return readLines(new File(filePath), encoding);
	}
    
    public static List<String> readLines(File file, Charset encoding) {
    	List<String> lines = new LinkedList<String>();
        try {
        	lines = org.apache.commons.io.FileUtils.readLines(file, encoding);
		} catch (Exception e) {
			log.error("读取文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return lines;
	}

	public static boolean write(String filePath, String data) {
    	return write(new File(filePath), data);
	}
	
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
	
	public static boolean write(String filePath, String data, boolean append) {
		return write(new File(filePath), data, append);
	}
	
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
	
	public static boolean write(String filePath, String data, String encoding) {
		return write(new File(filePath), data, encoding);
	}
	
	public static boolean write(File file, String data, String encoding) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, encoding);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	public static boolean write(String filePath, String data, Charset encoding) {
		return write(new File(filePath), data, encoding);
	}
	
	public static boolean write(File file, String data, Charset encoding) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, encoding);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	public static boolean write(String filePath, String data, String encoding, boolean append) {
		return write(new File(filePath), data, encoding, append);
	}
	
	public static boolean write(File file, String data, String encoding, boolean append) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, encoding, append);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	public static boolean write(String filePath, String data, Charset encoding, boolean append) {
		return write(new File(filePath), data, encoding, append);
	}
	
	public static boolean write(File file, String data, Charset encoding, boolean append) {
    	boolean isOk = false;
        try {
        	org.apache.commons.io.FileUtils.write(file, data, encoding, append);
        	isOk = true;
        	
		} catch (Exception e) {
			log.error("写文件失败: ", (file == null ? "null" : file.getPath()), e);
		}
        return isOk;
	}
	
	public static double getByteSize(File file) {
		return (file == null ? 0D : file.length());
	}
	
	public static double getKBSize(File file) {
		return getByteSize(file) / 1024.0;
	}
	
	public static double getMBSize(File file) {
		return getKBSize(file) / 1024.0;
	}
	
	public static double getGBSize(File file) {
		return getMBSize(file) / 1024.0;
	}
	
	public static double getTBSize(File file) {
		return getGBSize(file) / 1024.0;
	}
	
	public static String getSize(File file) {
		double size = getByteSize(file);
		String unit = StorageUnit.BYTE.VAL;
		
		if(size >= 1024 && StorageUnit.BYTE.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.KB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.KB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.MB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.MB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.GB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.GB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.TB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.TB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.PB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.PB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.EB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.EB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.ZB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.ZB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.YB.VAL;
		}
		
		if(size >= 1024 && StorageUnit.YB.VAL.equals(unit)) { 
			size = size / 1024.0;
			unit = StorageUnit.BB.VAL;
		}
		return new DecimalFormat("0.00 " + unit).format(size);
	}
	
	/**
	 * 递归遍历目录下的每一层，计算整个目录的大小
	 * @param dirPath 目录/文件路径
	 * @return 整个目录/文件大小，单位bytes
	 */
	public static long getDirSize(final String dirPath) {
		File dir = new File(dirPath);
		long size = 0;

		if (dir.exists()) {
			if (dir.isDirectory()) {
				for (File file : dir.listFiles()) {
					size += getDirSize(StrUtils.concat(dirPath, 
							File.separator, file.getName()));
				}
			} else {
				size = dir.length();
			}
		}
		return size;
	}
	
	/**
	 * 获取文件头信息
	 * @param file 文件
	 * @return 文件头信息
	 */
	public static String getHeadMsg(File file) {
		String head = "";
		String filePath = (file == null ? null : file.getAbsolutePath());
		if(filePath != null) {
			head = getHeadMsg(filePath);
		}
		return head;
	}
	
	/**
	 * 获取文件头信息
	 * @param filePath 文件路径
	 * @return 文件头信息
	 */
	public static String getHeadMsg(String filePath) {
		String head = "";
		try {
			FileInputStream is = new FileInputStream(filePath);
			byte[] bytes = new byte[4];
			
			is.read(bytes, 0, bytes.length);
			is.close();
			
			head = BODHUtils.toHex(bytes);
			
		} catch (Exception e) {
			log.error("获取文件 [{}] 的文件头信息失败.", filePath, e);
		}
		return head;
	}
	
	/**
	 * 获取文件扩展名，包括[.]符号
	 * @param file 文件
	 * @return 文件扩展名，包括[.]符号
	 */
	public static String getExtension(File file) {
		String extension = "";
		String fileName = (file == null ? null : file.getName());
		if(fileName != null) {
			extension = getExtension(fileName);
		}
		return extension;
	}
	
	/**
	 * 获取文件扩展名，包括[.]符号
	 * @param fileName 文件名
	 * @return 文件扩展名，包括[.]符号
	 */
	public static String getExtension(String fileName) {
		String extension = "";
		int pos = fileName.lastIndexOf(".");
		if (pos != -1) {
			extension = fileName.substring(pos).toLowerCase();
		}
		return extension;
	}
	
	/**
	 * 复制jar包内中的文件
	 * @param packPath 包内文件的包路径,如: /foo/bar/test.txt
	 * @param destPath
	 */
	public static boolean copyFileInJar(String packPath, String destPath) {
		boolean isOk = false;
		final int BUFFER_SIZE = 4096;
		InputStream is = FileUtils.class.getResourceAsStream(packPath);
		createFile(destPath);
		
		try {
			FileOutputStream fos = new FileOutputStream(destPath);
			byte[] buff = new byte[BUFFER_SIZE];
			int rc = 0;
			while ((rc = is.read(buff, 0, BUFFER_SIZE)) > 0) {
				fos.write(buff, 0, rc);
			}
			fos.flush();
			fos.close();
			isOk = true;
			
		} catch (Exception e) {
			log.error("复制文件失败: 从 [{}] 到 [{}].", packPath, destPath, e);
		}
		
		try {
			is.close();
		} catch (Exception e) {
			log.error("读取Jar内文件异常: 关闭IO流失败.", e);
		}
		return isOk;
	}
	
	/**
	 * 读取jar包中的文件
	 * @param packPath 包内文件的包路径,如: /foo/bar/test.txt
	 * @param encode 文件编码
	 * @return 文件内容
	 * @throws Exception
	 */
	public static String readFileInJar(String packPath, String encoding) {
		final int BUFFER_SIZE = 4096;
		InputStream is = FileUtils.class.getResourceAsStream(packPath);
		String str = "";
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buff = new byte[BUFFER_SIZE];
			int rc = 0;
			while ((rc = is.read(buff, 0, BUFFER_SIZE)) > 0) {
				bos.write(buff, 0, rc);
			}
			byte[] arrByte = bos.toByteArray();
			str = new String(arrByte, encoding);
			bos.close();
			
		} catch (Exception e) {
			log.error("读取Jar内文件失败: ", packPath, e);
		}
		
		try {
			is.close();
		} catch (Exception e) {
			log.error("读取Jar内文件异常: 关闭IO流失败.", e);
		}
		return str;
	}
	
	public static List<String> listFilesInJar(String jarFilePath) {
		return listFilesInJar(jarFilePath, null);
	}

	public static List<String> listFilesInJar(String jarFilePath, String extension) {
		return listFilesInJar(new File(jarFilePath), extension);
	}
	
	public static List<String> listFilesInJar(File jarFile, String extension) {
		List<String> list = new LinkedList<String>();
		boolean isFilter = StrUtils.isNotEmpty(extension);
		try {
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> envm = jar.entries();
			while (envm.hasMoreElements()) {
				JarEntry e = envm.nextElement();
				String path = e.getName();
				
				if(isFilter && path.toLowerCase().endsWith(extension.toLowerCase())) {
					list.add(path);
				}
			}
		} catch (Exception e) {
			log.error("读取Jar内文件列表失败: ", 
					(jarFile == null ? "null" : jarFile.getPath()), e);
		}
		return list;
	}
	
}
