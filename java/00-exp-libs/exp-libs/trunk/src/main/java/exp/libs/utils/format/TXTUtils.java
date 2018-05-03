package exp.libs.utils.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.FileType;
import exp.libs.utils.encode.CompressUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.num.UnitUtils;

/**
 * <PRE>
 * TXT转码工具.
 * ------------------------
 *  可把任意文件与txt文件进行互转:
 *   读取文件的字节流，并转换成16进制字符串存储到txt文件.
 *  
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-03
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TXTUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(TXTUtils.class);
	
	/** 私有化构造函数 */
	protected TXTUtils() {}
	
	/**
	 * 把任意文件/目录转码成TXT文件
	 * @param file 任意文件或目录对象(对于目录会先压缩成zip文件)
	 * @return TXT文件路径（若转码失败则返回空串）
	 */
	public static String toTXT(File file) {
		String txtPath = "";
		if(file != null) {
			txtPath = toTXT(file.getAbsolutePath());
		}
		return txtPath;
	}
	
	/**
	 * 把任意文件/目录转码成TXT文件
	 * @param filePath 文件或目录路径(对于目录会先压缩成zip文件)
	 * @return TXT文件路径（若转码失败则返回空串）
	 */
	public static String toTXT(String filePath) {
		String txtPath = "";
		if(FileUtils.isDirectory(filePath)) {
			String zipPath = CompressUtils.compress(filePath);
			txtPath = _toTXT(new File(zipPath));
			FileUtils.delete(zipPath);
			
		} else {
			txtPath = _toTXT(new File(filePath));
		}
		return txtPath;
	}
	
	/**
	 * 把任意文件转码成TXT文件
	 * @param file 任意文件对象
	 * @return TXT文件路径（若转码失败则返回空串）
	 */
	private static String _toTXT(File file) {
		String srcPath = file.getAbsolutePath();
		String txtPath = srcPath.concat(FileType.TXT.EXT);
		return (_toTXT(srcPath, txtPath) ? txtPath : "");
	}
	
	/**
	 * 把任意文件转码成TXT文件
	 * @param srcPath 任意文件路径
	 * @param txtPath TXT文件路径
	 * @return true:转码成功; false:转码失败
	 */
	private static boolean _toTXT(String srcPath, String txtPath) {
		boolean isOk = false;
		File txtFile = FileUtils.createFile(txtPath);
		try {
			FileInputStream fis = new FileInputStream(new File(srcPath));
			FileOutputStream fos = new FileOutputStream(txtFile);
			byte[] buff = new byte[UnitUtils._1_MB];
			int rc = 0;
			while ((rc = fis.read(buff, 0, UnitUtils._1_MB)) > 0) {
				String hex = BODHUtils.toHex(buff, 0, rc);
				fos.write(hex.getBytes(Charset.ISO));
			}
			fos.flush();
			fos.close();
			fis.close();
			isOk = true;
			
		} catch (Exception e) {
			log.error("把文件 [{}] 转码为TXT失败.", srcPath, e);
		}
		return isOk;
	}
	
	/**
	 * 把TXT转码文件恢复为原文件（若原文件是目录, 则恢复为zip压缩包）
	 * @param txtPath TXT文件路径
	 * @return 恢复后的文件路径（若恢复失败则返回空串）
	 */
	public static String toFile(String txtPath) {
		return toFile(new File(txtPath));
	}
	
	/**
	 * 把TXT转码文件恢复为原文件（若原文件是目录, 则恢复为zip压缩包）
	 * @param txtFile TXT文件对象
	 * @return 恢复后的文件路径（若恢复失败则返回空串）
	 */
	public static String toFile(File txtFile) {
		String snkPath = "";
		if(txtFile != null && txtFile.isFile() && 
				txtFile.getAbsolutePath().toLowerCase().endsWith(FileType.TXT.EXT)) {
			String txtPath = txtFile.getAbsolutePath();
			snkPath = txtPath.replace(FileType.TXT.EXT, "");
			snkPath = (_toFile(txtPath, snkPath) ? snkPath : "");
		}
		return snkPath;
	}
	
	/**
	 * 把TXT转码文件恢复为原文件（若原文件是目录, 则恢复为zip压缩包）
	 * @param txtPath TXT文件路径
	 * @param snkPath 原文件路径
	 * @return true:恢复成功; false:恢复失败
	 */
	private static boolean _toFile(String txtPath, String snkPath) {
		boolean isOk = false;
		File snkFile = FileUtils.createFile(snkPath);
		try {
			FileInputStream fis = new FileInputStream(new File(txtPath));
			FileOutputStream fos = new FileOutputStream(snkFile);
			byte[] buff = new byte[UnitUtils._1_MB];
			int rc = 0;
			while ((rc = fis.read(buff, 0, UnitUtils._1_MB)) > 0) {
				String hex = new String(buff, 0, rc, Charset.ISO);
				fos.write(BODHUtils.toBytes(hex));
			}
			fos.flush();
			fos.close();
			fis.close();
			isOk = true;
			
		} catch (Exception e) {
			log.error("恢复TXT转码文件 [{}] 失败.", txtPath, e);
		}
		return isOk;
	}
	
}
