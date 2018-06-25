package exp.libs.warp.io.flow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.Endline;

/**
 * <PRE>
 * 字符串流读取器.
 * 	该读取器只会对指定字符串逐字符读入一次，无法重新读取.
 * 
 * 使用示例:
 * 	StringFlowReader sfr = new StringFlowReader(String, Charset.UTF8);
 *  while(sfr.hasNextLine()) {
 *  	String line = sfr.readLine('\n');	// 行分隔符可自由替换
 *  	// ... do for line
 *  }
 *  sfr.close();
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class StringFlowReader {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(StringFlowReader.class);
	
	/** 所读入字符串的默认编码 */
	public final static String DEFAULT_CHARSET = Charset.ISO;
	
	/**
	 * 所读入字符串的默认[行终止符].
	 * (默认为换行符, 但存在无换行符的流式文件需要另外指�?)
	 */
	public final static char DEFAULT_LINE_END = Endline.CR;
	
	/** 所读入的字符串编码 */
	private String charset;
	
	/** 流读取器 */
	private InputStreamReader strReader;
	
	/** 标记是否存在可读的下一�? */
	private boolean hasNextLine;
	
	/**
	 * 构造函�?
	 * @param str 待读入字符串
	 */
	public StringFlowReader(String str) {
		init(str, DEFAULT_CHARSET);
	}
	
	/**
	 * 构造函�?
	 * @param str 待读入字符串
	 * @param charset 待读入字符串的编�?
	 */
	public StringFlowReader(String str, String charset) {
		init(str, charset);
	}
	
	/**
	 * 初始�?
	 * @param str 待读入字符串
	 * @param charset 待读入字符串的编�?
	 */
	public void init(String str, String charset) {
		this.charset = testEncode(charset) ? charset : DEFAULT_CHARSET;
		this.hasNextLine = false;
		
		if(str != null) {
			try {
				this.strReader = new InputStreamReader(
						new ByteArrayInputStream(str.getBytes(this.charset)), 
						this.charset);
				this.hasNextLine = true;
			} catch (Exception e) {
				log.error("读取字符�? [{}] 失败.", str, e);
				close();
			}
		}
		
		if(!hasNextLine) {
			log.error("构造字符串 [{}] 的流式读取器失败.", str);
		}
	}
	
	/**
	 * 测试编码是否合法.
	 * @param charset 被测试编�?
	 * @return true:编码合法; false:编码非法
	 */
	private boolean testEncode(String charset) {
		boolean isVaild = true;
		try {
			"test".getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			isVaild = false;
		}
		return isVaild;
	}
	
	/**
	 * 当前文件流是否存在下一行（以实际的[行终止符]标记[行]�?
	 * @return true:存在; false:不存�?
	 */
	public boolean hasNextLine() {
		return hasNextLine;
	}
	
	/**
	 * 读取当前行（使用[换行符]作为[行终止符]�?.
	 * 	此方法需配合 hasNextLine 方法使用（类似迭代器的使用方式）.
	 * @return 当前行数�?
	 */
	public String readLine() {
		return readLine(DEFAULT_LINE_END);
	}
	
	/**
	 * 读取当前行（使用[自定义符号]作为[行终止符]�?.
	 * 	此方法需配合 hasNextLine 方法使用（类似迭代器的使用方式）.
	 * @param lineEnd 自定义行终止�?
	 * @return 当前行数�?
	 */
	public String readLine(char lineEnd) {
		if(!hasNextLine) {
			return "";
		}
		
		StringBuilder line = new StringBuilder();
		try {
			while(true) {
				int n = strReader.read();
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
			log.error("读取字符串流过程中发生异�?.", e);
			close();
		}
		return line.toString();
	}
	
	/**
	 * 关闭文件流读取器
	 */
	public void close() {
		if(strReader != null) {
			try {
				strReader.close();
			} catch (IOException e) {
				System.err.println("关闭字符串流式读取器失败.");
			}
		}
		strReader = null;
		hasNextLine = false;
	}
	
}
