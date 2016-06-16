package exp.libs.utils.code;


import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import exp.libs.envm.Charset;
import exp.libs.utils.pub.CharsetUtils;
import exp.libs.utils.pub.FileUtils;
import exp.libs.utils.pub.RegexUtils;
import exp.libs.utils.pub.StrUtils;
import exp.libs.warp.other.flow.FileFlowReader;

/**
 * <PRE>
 * Java工具类.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-13
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class JavaUtils {

	/** 私有化构造函数 */
	protected JavaUtils() {}
	
	public static void modifyPackagePath(String srcDirPath) {
		modifyPackagePath(srcDirPath, Charset.UTF8);
	}

	/**
	 * 修正Java源文件的package路径.
	 * 
	 * 	用于解决Eclipse迁移包代码时，不能自动修改package路径的问题。
	 * 	当要迁移整个包代码时，先在系统文件夹直接移动，再使用此方法调整所有源码文件的package路径。
	 * 	<B>使用要求：在迁移包代码之前，代码无任何语法错误。</B>
	 * 
	 * @param srcDirPath 源码根目录的绝对路径，如 D:foo/bar/project/src/main/java
	 * @param encoding 源码文件的内容编码
	 */
	public static void modifyPackagePath(String srcDirPath, String encoding) {
		if(StrUtils.isEmpty(srcDirPath) || CharsetUtils.isInvalid(encoding)) {
			return;
		}
		
		File srcDir = new File(srcDirPath);
		if(!srcDir.exists()) {
			return;
		}
		
		Map<String, String> packagePaths = new HashMap<String, String>();
		searchPackagePath(null, srcDir, encoding, packagePaths);
		modifyPackagePath(srcDir, encoding, packagePaths);
	}
	
	private static void searchPackagePath(String dir, File file, 
			String encoding, Map<String, String> packagePaths) {
		if(file.isDirectory()) {
			dir = (dir == null ? "" : StrUtils.concat(dir, file.getName(), "."));
			File[] files = file.listFiles();
			for(File f : files) {
				searchPackagePath(dir, f, encoding, packagePaths);
			}
			
		} else {
			if(file.getName().endsWith(".java") && StrUtils.isNotEmpty(dir)) {
				String newPackagePath = dir.replaceFirst("\\.$", "");
				String oldPackagePath = "";
				
				FileFlowReader ffr = new FileFlowReader(file, encoding);
				while(ffr.hasNextLine()) {
					String line = ffr.readLine(';');
					line = line.trim();
					if(line.startsWith("package")) {
						oldPackagePath = RegexUtils.findFirst(line, "package\\s([^;]+);");
						break;
					}
				}
				ffr.close();
				
				if(StrUtils.isNotEmpty(oldPackagePath) && 
						!oldPackagePath.equals(newPackagePath)) {
					packagePaths.put(oldPackagePath, newPackagePath);
				}
			}
		}
	}
	
	private static void modifyPackagePath(File file, 
			String encoding, Map<String, String> packagePaths) {
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(File f : files) {
				modifyPackagePath(f, encoding, packagePaths);
			}
			
		} else {
			if(file.getName().endsWith(".java")) {
				String content = FileUtils.read(file, encoding);
				Iterator<String> keyIts = packagePaths.keySet().iterator();
				while(keyIts.hasNext()) {
					String oldPackagePath = keyIts.next();
					String newPackagePath = packagePaths.get(oldPackagePath);
					content = content.replace(oldPackagePath, newPackagePath);
					if("".equals(newPackagePath)) {
						content = content.replaceAll("import\\s+\\w+;", "");	// 根目录下的类不能import
					}
				}
				FileUtils.write(file, content, encoding, false);
			}
		}
	}
	
}
