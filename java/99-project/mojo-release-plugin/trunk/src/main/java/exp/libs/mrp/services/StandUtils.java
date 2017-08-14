package exp.libs.mrp.services;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.catt.util.regex.ValidateUtils;

/**
 * <PRE>
 * 标准化工具类
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-3-14
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class StandUtils {

	/**
	 * classPath正则式
	 */
	private final static String CP_REGEX = "-cp\\s+(\\S+)";
	
	/**
	 * dos脚本内容转换为符合unix标准内容。
	 * 实则上不是dos也能转换为unix。
	 * 
	 * @param content dos脚本内容
	 * @return 符合unix标准内容
	 */
	public static String dos2unix(String content) {
		content = content.replace("\r", "").replace('\\', '/');
		String dosCp = "";
		
		Pattern ptn = Pattern.compile(CP_REGEX);
		Matcher mth = ptn.matcher(content);
		if(mth.find()) {
			dosCp = mth.group(1);
			String unixCp = dosCp.replace("jar;", "jar:");
			content = content.replace(dosCp, unixCp);
		}
		return content;
	}
	
	/**
	 * unix脚本内容转换为符合dos标准内容。
	 * 实则上不是unix也能转换为dos。
	 * 
	 * @param content unix脚本内容
	 * @return 符合dos标准内容
	 */
	public static String unix2dos(String content) {
		content = content.replace("\r", "").	//避免误入dos2dos导致一堆\r
				replace("\n", "\r\n").replace('/', '\\');
		String unixCp = "";
		
		Pattern ptn = Pattern.compile(CP_REGEX);
		Matcher mth = ptn.matcher(content);
		if(mth.find()) {
			unixCp = mth.group(1);
			String dosCp = unixCp.replace("jar:", "jar;");	//避免路径含D:时错误替换
			content = content.replace(unixCp, dosCp);
		}
		return content;
	}
	
	/**
	 * 把路径转换为运行平台的标准路径。
	 * @param paths 路径集
	 * @return 标准路径集
	 */
	public static List<String> toStandPaths(List<String> paths) {
		return (ValidateUtils.isWin() ? 
				toWinPaths(paths) : toLinuxPaths(paths));
	}
	
	/**
	 * 把路径转换为运行平台的标准路径。
	 * @param path 路径
	 * @return 标准路径
	 */
	public static String toStandPath(String path) {
		return (ValidateUtils.isWin() ? toWinPath(path) : toLinuxPath(path));
	}
	
	/**
	 * 把linux路径转换为win路径
	 * @param linuxPaths linux路径
	 * @return win路径
	 */
	public static List<String> toWinPaths(List<String> linuxPaths) {
		List<String> winPaths = new LinkedList<String>();
		for(String linuxPath : linuxPaths) {
			winPaths.add(toWinPath(linuxPath));
		}
		return winPaths;
	}
	
	/**
	 * 把linux路径转换为win路径
	 * @param linuxPath linux路径
	 * @return win路径
	 */
	public static String toWinPath(String linuxPath) {
		String winPath = linuxPath.replace('\\', '/');
		winPath = winPath.replaceFirst("(?i)/home/cattsoft", "D:");
		winPath = winPath.replace('/', '\\');
		return winPath;
	}
	
	/**
	 * 把win路径转换为linux路径
	 * @param winPaths win路径
	 * @return linux路径
	 */
	public static List<String> toLinuxPaths(List<String> winPaths) {
		List<String> linuxPaths = new LinkedList<String>();
		for(String winPath : winPaths) {
			linuxPaths.add(toLinuxPath(winPath));
		}
		return linuxPaths;
	}
	
	/**
	 * 把win路径转换为linux路径
	 * @param winPath win路径
	 * @return linux路径
	 */
	public static String toLinuxPath(String winPath) {
		String linuxPath = winPath.replace('/', '\\');
		linuxPath = linuxPath.replaceFirst("(?i)D:", "/home/cattsoft");
		linuxPath = linuxPath.replace('\\', '/');
		return linuxPath;
	}
	
}
