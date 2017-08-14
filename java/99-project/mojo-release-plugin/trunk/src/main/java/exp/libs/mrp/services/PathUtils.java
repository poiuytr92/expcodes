package exp.libs.mrp.services;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * 路径处理工具.
 * 
 * 	处理原则： 本工具类的所有函数返回值,只要是路径,就一定【不能】以 分隔符 结尾.
 * 
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class PathUtils {

	/**
	 * 判断是否为全路径（即以根开始的路径）
	 * 
	 * @param path 路径
	 * @return true:全路径; false:非全路径
	 */
	public static boolean isFullPath(String path) {
		boolean isFull = false;
		if(path != null && !"".equals(path.trim())) {
			Pattern ptn = Pattern.compile("^(([A-Za-z]:[/|\\\\])|/).*$");
			Matcher mth = ptn.matcher(path);
			
			if(mth.find()) {
				isFull = true;
			}
		}
		return isFull;
	}
	
	/**
	 * 路径合并。
	 * 所有不满足合并条件的入参，均返回 prefixPath。
	 * 否则返回符合unix标准的合并路径。
	 * 
	 * @param prefixPath 路径前缀，全路径或相对路径
	 * @param suffixPath 路径后缀，要求是相对路径
	 * @return 路径前缀 + 路径分隔符 + 路径后缀 (路径不以路径分隔符结尾)
	 */
	public static String combPath(String prefixPath, String suffixPath) {
		String comPath = prefixPath;
		
		if(prefixPath != null && suffixPath != null) {
			prefixPath = prefixPath.trim().replace('\\', '/');
			suffixPath = suffixPath.trim().replace('\\', '/');
			
			if(false == isFullPath(suffixPath)) {
				if(false == prefixPath.endsWith("/")) {
					prefixPath += "/";
				}
				if(true == suffixPath.endsWith("/")) {
					suffixPath = suffixPath.substring(
							0, suffixPath.length() - 1);
				}
				comPath = prefixPath + suffixPath;
				comPath = comPath.replace("/./", "/");
			}
			comPath = comPath.replace('/', File.separatorChar).
					replace('\\', File.separatorChar);
		}
		return comPath;
	}
	
	/**
	 * 获取 项目的编译根路径，如：
	 * 	F:/workspace/j2se/utf8/catt-maven-plugin/target/classes
	 * 
	 * @return 项目的编译根路径
	 */
	public static String getRootCompilePath() {
		return new File(PathUtils.class.getResource("/").getPath()).
				getAbsolutePath();
	}
	
	/**
	 * 获取 类的编译路径，如：
	 * 	F:/workspace/j2se/utf8/catt-maven-plugin/target/classes/
	 * 		com/catt/plugin/utils
	 * 
	 * @param clazz 求路径的类
	 * @return 类的编译路径
	 */
	public static String getClassCompilePath(Class<?> clazz) {
		return new File(clazz.getResource("").getPath()).
				getAbsolutePath();
	}
	
	/**
	 * 获取 项目的根路径，如：
	 * 	F:/workspace/j2se/utf8/catt-maven-plugin/
	 * 
	 * @return 项目的根路径
	 */
	public static String getProjectRootPath() {
		return System.getProperty("user.dir");
		
		//或这样做：
		//return new File(".").getCanonicalPath();
	}
	
	/**
	 * 获取项目自身引用的所有jar包的路径。
	 * 只能获取运行main方法的项目所需要的jar包路径，而不能获取其他项目的jar包类路径。
	 * 
	 * 如果是外部jdk调用，则返回的是 -cp 的参数表。
	 * 
	 * @return 项目引用的所有包的路径
	 */
	public static String[] getAllClassPaths() {
		return System.getProperty("java.class.path").split(";");
	}
	
}
