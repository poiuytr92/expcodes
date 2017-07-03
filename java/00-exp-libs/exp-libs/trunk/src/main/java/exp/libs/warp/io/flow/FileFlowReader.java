package exp.libs.warp.io.flow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.Endline;

/**
 * <PRE>
 * 文件流读取器.
 * 	该读取器只会对指定文件逐字符读入一次，无法重新读取.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FileFlowReader {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(FileFlowReader.class);
	
	/** 所读入文件的默认编码 */
	public final static String DEFAULT_ENCODE = Charset.ISO;
	
	/**
	 * 所读入文件的默认[行终止符].
	 * (默认为换行符, 但存在无换行符的流式文件需要另外指定)
	 */
	public final static char DEFAULT_LINE_END = Endline.CR;
	
	/** 所读入的文件对象 */
	private File file;
	
	/** 所读入的文件编码 */
	private String fileEncode;
	
	/** 文件流读取器 */
	private InputStreamReader fileReader;
	
	/** 标记是否存在可读的下一行 */
	private boolean hasNextLine;
	
	/**
	 * 构造函数
	 * @param file 待读入文件
	 * @param fileEncode 待读入文件的编码
	 */
	public FileFlowReader(File file, String fileEncode) {
		init(file, fileEncode);
	}
	
	/**
	 * 构造函数
	 * @param filePath 待读入文件的路径
	 * @param fileEncode 待读入文件的编码
	 */
	public FileFlowReader(String filePath, String fileEncode) {
		init(new File(filePath), fileEncode);
	}
	
	/**
	 * 初始化
	 * @param file 待读入文件
	 * @param fileEncode 待读入文件的编码
	 */
	private void init(File file, String fileEncode) {
		this.file = file;
		this.fileEncode = testEncode(fileEncode) ? fileEncode : DEFAULT_ENCODE;
		this.hasNextLine = false;
		
		if(file != null && file.isFile()) {
			try {
				this.fileReader = new InputStreamReader(
						new FileInputStream(this.file), this.fileEncode);
				this.hasNextLine = true;
			} catch (Exception e) {
				log.error("读取文件 [{}] 失败.", file.getAbsoluteFile(), e);
				close();
			}
		}
		
		if(!hasNextLine) {
			log.error("构造文件 [{}] 的流式读取器失败.", 
					(file == null ? "null" : file.getPath()));
		}
	}
	
	/**
	 * 测试编码是否合法.
	 * @param encode 被测试编码
	 * @return true:编码合法; false:编码非法
	 */
	private boolean testEncode(String encode) {
		boolean isVaild = true;
		try {
			"test".getBytes(encode);
		} catch (UnsupportedEncodingException e) {
			isVaild = false;
		}
		return isVaild;
	}
	
	/**
	 * 当前文件流是否存在下一行（以实际的[行终止符]标记[行]）
	 * @return true:存在; false:不存在
	 */
	public boolean hasNextLine() {
		return hasNextLine;
	}
	
	/**
	 * 读取当前行（使用[换行符]作为[行终止符]）.
	 * 	此方法需配合 hasNextLine 方法使用（类似迭代器的使用方式）.
	 * @return 当前行数据
	 */
	public String readLine() {
		return readLine(DEFAULT_LINE_END);
	}
	
	/**
	 * 读取当前行（使用[自定义符号]作为[行终止符]）.
	 * 	此方法需配合 hasNextLine 方法使用（类似迭代器的使用方式）.
	 * @param lineEnd 自定义行终止符
	 * @return 当前行数据
	 */
	public String readLine(char lineEnd) {
		if(!hasNextLine) {
			return "";
		}
		
		StringBuilder line = new StringBuilder();
		try {
			while(true) {
				int n = fileReader.read();
				if(n == -1) {
					hasNextLine = false;	//已到文件末尾
					break;
				}
				char c = (char) n;
				line.append(c);
				
				if(c == lineEnd) {	// 已到行尾
					break;
				}
			}
		} catch (IOException e) {
			log.error("流式读取文件 [{}] 过程中发生异常.", file.getPath(), e);
			close();
		}
		return line.toString();
	}
	
	/**
	 * 关闭文件流读取器
	 */
	public void close() {
		if(fileReader != null) {
			try {
				fileReader.close();
			} catch (IOException e) {
				System.err.println("关闭流式文件 [" + 
						(file == null ? "null" : file.getPath()) + "] 失败.");
			}
		}
		file = null;
		fileReader = null;
		hasNextLine = false;
	}
	
}
