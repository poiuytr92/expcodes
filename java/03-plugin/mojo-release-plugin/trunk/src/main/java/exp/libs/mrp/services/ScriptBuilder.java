package exp.libs.mrp.services;

import java.util.List;

import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.mrp.Config;
import exp.libs.mrp.Log;
import exp.libs.mrp.cache.JarMgr;
import exp.libs.mrp.envm.Placeholders;
import exp.libs.mrp.envm.ScriptNames;
import exp.libs.mrp.envm.TplNames;
import exp.libs.utils.format.StandardUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.tpl.Template;

/**
 * <PRE>
 * 脚本构造器
 * </PRE>
 * <B>PROJECT : </B> mojo-release-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-05-15
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ScriptBuilder {

	protected ScriptBuilder() {}
	
	private static boolean createScript(final String name, final String content) {
		String filePath = PathUtils.combine(
				Config.getInstn().getReleaseDir(), name);
		String charset = Config.getInstn().getCharset();
		boolean isOk = FileUtils.write(filePath, content, charset, false);
		if(isOk == true) {
			Log.info("创建脚本成功: ".concat(name));
		} else {
			Log.warn("创建脚本失败: ".concat(name));
		}
		return isOk;
	}
	
	public static boolean exec() {
		boolean isOk = true;
		isOk &= buildAppPath();
		isOk &= buildThreadName();
		isOk &= buildUnixPid();
		isOk &= buildUnixStart();
		isOk &= buildUnixStop();
		isOk &= buildUnixVersion();
		isOk &= buildDosStart();
		isOk &= buildDosVersion();
		return isOk;
	}
	
	private static boolean buildAppPath() {
		Template tpl = new Template(TplNames.APP_PATH_TEMPLATE, Charset.ISO);
		return createScript(ScriptNames.APP_PATH, 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildThreadName() {
		Template tpl = new Template(TplNames.THREADNAME_TEMPLATE, Charset.ISO);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		tpl.set(Placeholders.THREAD_SUFFIX, Config.getInstn().getThreadSuffix());
		return createScript(ScriptNames.THREAD_NAME, 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixPid() {
		Template tpl = new Template(TplNames.PID_TEMPLATE_UNIX, Charset.ISO);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		return createScript(ScriptNames.ECHO_PID, 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixStart() {
		Template tpl = new Template(TplNames.START_TEMPLATE_UNIX, Charset.ISO);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
				
		// 声明变量�?-cp路径前缀�?
		String exports = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			exports = StrUtils.concat(exports, 
					"export lib", idx, "=", prefixs.get(idx), Delimiter.LF);
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, exports);
		
		// 设置JDK命令
		tpl.set(Placeholders.JDK_PATH, Config.getInstn().getJdkPath());
		
		// 设置JDK参数
		String jdkParams = StrUtils.concat("-Xms", Config.getInstn().getXms(), 
				" -Xmx", Config.getInstn().getXmx(), " ", 
				Config.getInstn().getJdkParams());
		tpl.set(Placeholders.JDK_PARAMS, jdkParams);
		
		// 设置-cp路径
		String cps = "";
		List<String> jarPaths = JarMgr.getInstn().getJarPaths();
		for(String jarPath : jarPaths) {
			for(int idx = 0; idx < prefixs.size(); idx++) {
				String prefix = prefixs.get(idx);
				if(jarPath.startsWith(prefix)) {
					jarPath = jarPath.replace(prefix, ("$lib" + idx));
					break;
				}
			}
			cps = cps.concat(jarPath).concat(":");
		}
		tpl.set(Placeholders.CLASSPATH, cps);
		
		// 设置main方法与入�?
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getMainClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, Config.getInstn().getMainArgs());
		tpl.set(Placeholders.VER, "");
		
		// 设置标准流和异常流输出位�?
		tpl.set(Placeholders.STDOUT_CTRL, ">/dev/null");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		tpl.set(Placeholders.RUN_IN_BACKGROUND, "&");
		
		return createScript(ScriptNames.START_SH, 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixStop() {
		Template tpl = new Template(TplNames.STOP_TEMPLATE_DOS, Charset.ISO);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		return createScript(ScriptNames.STOP_SH, 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixVersion() {
		Template tpl = new Template(TplNames.START_TEMPLATE_UNIX, Charset.ISO);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
				
		// 声明变量�?-cp路径前缀�?
		String exports = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			exports = StrUtils.concat(exports, 
					"export lib", idx, "=", prefixs.get(idx), Delimiter.LF);
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, exports);
		
		// 设置JDK命令（版本脚本不使用图形界面�?
		tpl.set(Placeholders.JDK_PATH, 
				Config.getInstn().getJdkPath().replace("javaw", "java"));
		
		// 设置JDK参数
		String jdkParams = StrUtils.concat("-Xms", Config.getInstn().getXms(), 
				" -Xmx", Config.getInstn().getXmx(), " ", 
				Config.getInstn().getJdkParams());
		tpl.set(Placeholders.JDK_PARAMS, jdkParams);
		
		// 设置-cp路径
		String cps = "";
		List<String> jarPaths = JarMgr.getInstn().getJarPaths();
		for(String jarPath : jarPaths) {
			for(int idx = 0; idx < prefixs.size(); idx++) {
				String prefix = prefixs.get(idx);
				if(jarPath.startsWith(prefix)) {
					jarPath = jarPath.replace(prefix, ("$lib" + idx));
					break;
				}
			}
			cps = cps.concat(jarPath).concat(":");
		}
		tpl.set(Placeholders.CLASSPATH, cps);
		
		// 设置main方法与入�?
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getVerClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "-p");	// 只打印版�?
		tpl.set(Placeholders.VER, "ver-");	// 声明为版本脚�?
		
		// 设置标准流和异常流输出位�?
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		tpl.set(Placeholders.RUN_IN_BACKGROUND, "");
		
		return createScript(ScriptNames.VERSION_SH, 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildDosStart() {
		Template tpl = new Template(TplNames.START_TEMPLATE_DOS, Charset.ISO);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		
		// 声明变量�?-cp路径前缀�?
		String sets = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			sets = StrUtils.concat(sets, 
					"set lib", idx, "=", prefixs.get(idx), Delimiter.CRLF);
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, sets);
				
		// 设置JDK命令
		tpl.set(Placeholders.JDK_PATH, Config.getInstn().getJdkPath());
		
		// 设置JDK参数
		String jdkParams = StrUtils.concat("-Xms", Config.getInstn().getXms(), 
				" -Xmx", Config.getInstn().getXmx(), " ", 
				Config.getInstn().getJdkParams());
		tpl.set(Placeholders.JDK_PARAMS, jdkParams);
		
		// 设置-cp路径
		String cps = "";
		List<String> jarPaths = JarMgr.getInstn().getJarPaths();
		for(String jarPath : jarPaths) {
			for(int idx = 0; idx < prefixs.size(); idx++) {
				String prefix = prefixs.get(idx);
				if(jarPath.startsWith(prefix)) {
					jarPath = jarPath.replace(prefix, ("%lib" + idx + "%"));
					break;
				}
			}
			cps = cps.concat(jarPath).concat(";");
		}
		tpl.set(Placeholders.CLASSPATH, cps);
				
		// 设置main方法与入�?
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getMainClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, Config.getInstn().getMainArgs());
		
		// 设置标准流和异常流输出位�?
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		
		// 标准化脚本内�?, 并修正脚本中的set命令
		String scriptContent = _repairSetCmd(
				StandardUtils.unix2dos(tpl.getContent()));
		return createScript(ScriptNames.START_BAT, scriptContent);
	}
	
	private static boolean buildDosVersion() {
		Template tpl = new Template(TplNames.START_TEMPLATE_DOS, Charset.ISO);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		
		// 声明变量�?-cp路径前缀�?
		String sets = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			sets = StrUtils.concat(sets, 
					"set lib", idx, "=", prefixs.get(idx), Delimiter.CRLF);
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, sets);
				
		// 设置JDK命令（版本脚本不使用图形界面�?
		tpl.set(Placeholders.JDK_PATH, 
				Config.getInstn().getJdkPath().replace("javaw", "java"));
		
		// 设置JDK参数
		String jdkParams = StrUtils.concat("-Xms", Config.getInstn().getXms(), 
				" -Xmx", Config.getInstn().getXmx(), " ", 
				Config.getInstn().getJdkParams());
		tpl.set(Placeholders.JDK_PARAMS, jdkParams);
		
		// 设置-cp路径
		String cps = "";
		List<String> jarPaths = JarMgr.getInstn().getJarPaths();
		for(String jarPath : jarPaths) {
			for(int idx = 0; idx < prefixs.size(); idx++) {
				String prefix = prefixs.get(idx);
				if(jarPath.startsWith(prefix)) {
					jarPath = jarPath.replace(prefix, ("%lib" + idx + "%"));
					break;
				}
			}
			cps = cps.concat(jarPath).concat(";");
		}
		tpl.set(Placeholders.CLASSPATH, cps);
				
		// 设置main方法与入�?
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getVerClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "-p");	// 只打印版�?
		
		// 设置标准流和异常流输出位�?
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		
		// 标准化脚本内�?, 并修正脚本中的set命令
		String scriptContent = _repairSetCmd(
				StandardUtils.unix2dos(tpl.getContent()));
		return createScript(ScriptNames.VERSION_BAT, scriptContent);
	}
	
	/**
	 * <PRE>
	 * 修正dos脚本用于读取线程文件�? "set /p" 命令.
	 * 该命令由�? {@link StandardUtils.unix2dos} 中的路径标准�?, 
	 * 使得反斜�? / 变成 \\ 导致失效, 需要修�?.
	 * </PRE>
	 * @param dosScriptContent
	 * @return
	 */
	private static String _repairSetCmd(String dosScriptContent) {
		return dosScriptContent.replace("set \\p threadname", "set /p threadname");
	}
	
}
