package exp.libs.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import exp.libs.utils.StrUtils;

/**
 * <PRE>
 * 大文件读取类
 * </PRE>
 */
//FIXME
@Deprecated
public class LargeFileReader {

	/** 系统默认换行符，默认“\r\n”  */
	public static final String DELIMITER = 
			System.getProperty("line.separator", "\r\n");

	/** 文件偏移量  */
	private long pointer = 0L;
	
	/** 读取的行数  */
	private long lineNumber = 0;
	
	/** 系统默认文件编码  */
	private String encoding = null;
	
	/** 文件读取类  */
	private RandomAccessFile br = null;
	
	/** 当前文件，一个类只能处理一个文件  */
	private File curFile = null;
	
	/** 系统默认换行符  */
	private String delimiter = null;

	/**
	 * 构造方法
	 */
	@Deprecated
	public LargeFileReader() {
	}
	
	/**
	 * 构造方法
	 * @param file	文件对象
	 * @param encoding	读取文件编码
	 * @param delimiter 换行符
	 */
	public LargeFileReader(File file, String encoding, String delimiter) {
		this.curFile = file;
		if (StrUtils.isNotEmpty(encoding)) {
			this.encoding = encoding;
		} else {
			this.encoding = System.getProperty("file.encoding");
		}
		if (StrUtils.isNotEmpty(delimiter)) {
			this.delimiter = delimiter;
		} else {
			this.delimiter = System.getProperty("line.separator", "\r\n");
		}
	}
	
	/**
	 * 构造方法
	 * @param filePath	文件路径
	 * @param encoding	读取文件编码
	 * @param delimiter 换行符
	 */
	public LargeFileReader(String filePath, String encoding, String delimiter) {
		this(new File(filePath), encoding, delimiter);
	}

	/**
	 * 根据文件偏移量，读指定行数的字符串
	 * 读取失败返回空null
	 *
	 * @param skip 跳过偏移量
	 * @param maxReadLine 最大读取行数
	 * @return 字符串
	 * @throws IOException
	 */
	public String read(long skip, int maxReadLine) throws IOException {
		return read(curFile, skip, maxReadLine, encoding, delimiter);
	}
	
	/**
	 * 获取对应行数的偏移量
	 * 
	 * @param lineNum 行数
	 * @return	偏移量
	 * @throws IOException 
	 */
	public long getPointer(long lineNum) throws IOException {
		if (lineNum < 0) {
			throw new IOException("入参不能为小于0");
		}
		int readCount = 0;
		try {
			if (br == null) {
				br = new RandomAccessFile(curFile, "r");
			}
			br.seek(0);
			while (br.readLine() != null) {
				readCount++;
				if (readCount == lineNum) {
					return br.getFilePointer();
				}
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
		}
		return br.getFilePointer();
	}
	/**
	 * 根据文件偏移量，读指定行数的字符串
	 *
	 * @param path 文件路径
	 * @param skip 跳过偏移量
	 * @param maxReadLine 最大读取行数
	 * @param encoding 文件编码，不设置会乱码
	 * @return 字符串
	 * @throws IOException
	 */
	@Deprecated
	public String read(String path, long skip, int maxReadLine, String encoding)
			throws IOException {
		return read(new File(path), skip, maxReadLine, encoding, DELIMITER);
	}
	
	/**
	 * 根据文件偏移量，读指定行数的字符串
	 * 读取失败返回空null
	 *
	 * @param file 文件
	 * @param skip 跳过偏移量
	 * @param maxReadLine 最大读取行数
	 * @param encoding 文件编码，不设置会乱码
	 * @param delimiter 行分隔符
	 * @return 字符串
	 * @throws IOException
	 */
	@Deprecated
	public String read(File file, long skip, int maxReadLine, String encoding,
			String delimiter) throws IOException {
		String readStr = "";
		StringBuffer buf = null;
		try {
			if (br == null) {
				br = new RandomAccessFile(file, "r");
			}
			if (skip < 0 || skip > br.length()) {
				System.err.println("跳过字节数无效/大于文件长度");
				return readStr;
			}
			br.seek(skip);
			String line = "";
			buf = new StringBuffer();
			int readCount = 0;
			while ((line = br.readLine()) != null) {
				readCount++;
				buf.append(line).append(delimiter);
				if (readCount == maxReadLine) {
					readStr = new String(buf.toString().getBytes("ISO-8859-1"),
							encoding);
					this.pointer = br.getFilePointer();
					this.lineNumber = readCount;
					// System.out.println("读到：" + pointer);
					return readStr;
				}
			}
			if (buf.length() > 0) {
				readStr = new String(buf.toString().getBytes("ISO-8859-1"),
						encoding);
				this.pointer = br.getFilePointer();
				this.lineNumber = readCount;
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
//			br.close();
			buf = null;
		}
		return readStr;
	}
	
	/**
	 * 关闭读取流
	 */
	public void close() {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	/**
//	 * 简单测试
//	 *
//	 * @param args null
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException {
//		String path = "file" + File.separator + "outFile.txt";
//		LargeFileReader lfr = new LargeFileReader();
//		File file = new File(path);
//		System.out.println("文件长度：" + file.length());
//		String read = lfr.read(file, 1585887877, 1000, "gbk", DELIMITER);
//		System.out.println("文件偏移量 ：" + lfr.getPointer());
//		System.out.println("读取的行数 ：" + lfr.getLineNumber());
//		System.out.println("读取的内容 ：\n" + read);
//	}

	/**
	 * pointer
	 * 
	 * @return the pointer
	 */
	public long getPointer() {
		return pointer;
	}

	/**
	 * pointer
	 * 
	 * @param pointer
	 *            the pointer to set
	 */
	public void setPointer(long pointer) {
		this.pointer = pointer;
	}

	/**
	 * lineNumber
	 * @return the lineNumber
	 */
	public long getLineNumber() {
		return lineNumber;
	}

	/**
	 * lineNumber
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	/**
	 * 获取当前处理文件长度
	 *
	 * @return
	 */
	public long getFileLength() {
		return this.curFile.length();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}
}
