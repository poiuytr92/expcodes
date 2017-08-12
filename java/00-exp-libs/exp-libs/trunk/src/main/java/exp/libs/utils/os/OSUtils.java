package exp.libs.utils.os;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * <PRE>
 * OS工具类
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class OSUtils {

	/** 用于判断操作平台类型的系统属性 */
	protected final static String OS_NAME = 
			System.getProperty("os.name").toLowerCase(); 
	
	/** 用于判断操作系统位宽的系统属性 */
	protected final static String OS_ARCH = 
			System.getProperty("os.arch").toLowerCase();

	/** 操作系统字符集编码 */
	protected static String OS_ENCODING = 
			System.getProperty("sun.jnu.encoding").toUpperCase();
	
	/** 私有化构造函数 */
	protected OSUtils() {}
	
	/**
	 * 判断当前操作系统是否为windows
	 * @return true:windows; false:linux/mac
	 */
	public static boolean isWin() {
		boolean isWin = true;
		
		if(OS_NAME.contains("win")) {
			isWin = true;
			
		} else if(OS_NAME.contains("mac")) {
			isWin = false;	//暂不可能mac平台上运行, 否则这段代码需修改
			
		} else {
			isWin = false;	//linux
		}
		return isWin;
	}
	
	public static boolean isUnix() {
		return !isWin();
	}
	
	/**
	 * 判断当前操作系统位宽是否为32位.
	 * （主要针对win, linux由于兼容32和64, 只能用64位）.
	 * 
	 * os 32位： x86
	 * os 64位：amd64
	 * linux 32位: i386
	 * linux 64位：amd64
	 * 
	 * @return true:64; false:32
	 */
	public static boolean isX64() {
		return OS_ARCH.contains("64");
	}
	
	public static boolean isX32() {
		return !isX64();
	}
	
	public static String getSysEncoding() {
		return OS_ENCODING;
	}
	
	/**
	 * 复制文本到剪切板
	 * @param txt 文本内容
	 */
	public static void copyToClipboard(final String txt) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(txt), null);
	}
	
	/**
	 * 从剪切板获得文字
	 * @return 文本内容
	 */
	public static String pasteFromClipboard() {
		String txt = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tf = sysClip.getContents(null);
		if (tf != null) {
			if (tf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					txt = (String) tf.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {}
			}
		}
		return txt;
	}

}
