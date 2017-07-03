package exp.libs.utils.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * 标准化工具类
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class StandardUtils {

	/** 私有化构造函数. */
	private StandardUtils() {}
	
	/**
	 * 根据实际需要的容量，返回构造Map的标准容量。
	 * 返回值为 >= actualSize 的 2^n 
	 * 
	 * int最大值为 2^30 -1 = 2147483647
	 * 
	 * @param actualSize 实际容量
	 * @return 标准容量
	 */
	public static int getMapSize(int actualSize) {
		boolean isGet = false;
		int size = 2;
		
		for(int i = 1; i < 30; i++) {
			size = size<<1;
			if(size >= actualSize) {
				isGet = true;
				break;
			}
		}
		
		if(isGet == false) {
			size = actualSize;
		}
		return size;
	}
	
	/**
	 * dos脚本内容转换为符合unix标准内容。
	 * 实则上不是dos也能转换为unix。
	 * 
	 * @param dos dos脚本内容
	 * @return 符合unix标准内容
	 */
	public static String dos2unix(String dos) {
		String unix = "";
		if(dos != null) {
			unix = dos.replace("\r", "").replace('\\', '/');
		}
		return unix;
	}
	
	/**
	 * unix脚本内容转换为符合dos标准内容。
	 * 实则上不是unix也能转换为dos。
	 * 
	 * @param unix unix脚本内容
	 * @return 符合dos标准内容
	 */
	public static String unix2dos(String unix) {
		String dos = "";
		if(unix != null) {
			dos = unix.replace("\r", "").replace("\n", "\r\n").replace('/', '\\');
		}
		return dos;
	}
	
	public static String javaBatToSh(String javaBat) {
		String shell = dos2unix(javaBat);
		
		Pattern ptn = Pattern.compile("-cp\\s+(\\S+)");
		Matcher mth = ptn.matcher(shell);
		if(mth.find()) {
			String dosCp = mth.group(1);
			String unixCp = dosCp.replace("jar;", "jar:");
			shell = shell.replace(dosCp, unixCp);
		}
		return shell;
	}
	
	public static String javaShToBat(String javaShell) {
		String bat = unix2dos(javaShell);
		
		Pattern ptn = Pattern.compile("-cp\\s+(\\S+)");
		Matcher mth = ptn.matcher(bat);
		if(mth.find()) {
			String unixCp = mth.group(1);
			String dosCp = unixCp.replace("jar:", "jar;");
			bat = bat.replace(unixCp, dosCp);
		}
		return bat;
	}
	
}
