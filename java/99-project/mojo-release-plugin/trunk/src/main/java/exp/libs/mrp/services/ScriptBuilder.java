package exp.libs.mrp.services;

import java.util.List;

import exp.libs.mrp.Config;
import exp.libs.mrp.Log;
import exp.libs.mrp.cache.JarMgr;
import exp.libs.mrp.envm.Placeholders;
import exp.libs.mrp.envm.ScriptNames;
import exp.libs.mrp.envm.TplNames;
import exp.libs.utils.StrUtils;
import exp.libs.utils.format.StandardUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.warp.tpl.Template;

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
		Template tpl = new Template(ScriptNames.APP_PATH);
		tpl.read(TplNames.APP_PATH_TEMPLATE);
		return createScript(tpl.getName(), 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildThreadName() {
		Template tpl = new Template(ScriptNames.THREAD_NAME);
		tpl.read(TplNames.THREADNAME_TEMPLATE);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		tpl.set(Placeholders.THREAD_SUFFIX, Config.getInstn().getThreadSuffix());
		return createScript(tpl.getName(), 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixPid() {
		Template tpl = new Template(ScriptNames.ECHO_PID);
		tpl.read(TplNames.PID_TEMPLATE_UNIX);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		return createScript(tpl.getName(), 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixStart() {
		Template tpl = new Template(ScriptNames.START_SH);
		tpl.read(TplNames.START_TEMPLATE_UNIX);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
				
		// 声明变量（-cp路径前缀）
		String exports = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			exports = StrUtils.concat(exports, 
					"export lib", idx, "=", prefixs.get(idx), "\n");
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
		
		// 设置main方法与入参
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getMainClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, Config.getInstn().getMainArgs());
		tpl.set(Placeholders.VER, "");
		
		// 设置标准流和异常流输出位置
		tpl.set(Placeholders.STDOUT_CTRL, ">/dev/null");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		tpl.set(Placeholders.RUN_IN_BACKGROUND, "&");
		
		return createScript(tpl.getName(), 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixStop() {
		Template tpl = new Template(ScriptNames.STOP_SH);
		tpl.read(TplNames.STOP_TEMPLATE_DOS);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		return createScript(tpl.getName(), 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildUnixVersion() {
		Template tpl = new Template(ScriptNames.VERSION_SH);
		tpl.read(TplNames.START_TEMPLATE_UNIX);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
				
		// 声明变量（-cp路径前缀）
		String exports = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			exports = StrUtils.concat(exports, 
					"export lib", idx, "=", prefixs.get(idx), "\n");
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, exports);
		
		// 设置JDK命令（版本脚本不使用图形界面）
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
		
		// 设置main方法与入参
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getMainClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "-p");	// 只打印版本
		tpl.set(Placeholders.VER, "ver-");	// 声明为版本脚本
		
		// 设置标准流和异常流输出位置
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		tpl.set(Placeholders.RUN_IN_BACKGROUND, "");
		
		return createScript(tpl.getName(), 
				StandardUtils.dos2unix(tpl.getContent()));
	}
	
	private static boolean buildDosStart() {
		Template tpl = new Template(ScriptNames.START_BAT);
		tpl.read(TplNames.START_TEMPLATE_DOS);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		
		// 声明变量（-cp路径前缀）
		String sets = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			sets = StrUtils.concat(sets, 
					"set lib", idx, "=", prefixs.get(idx), "\r\n");
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
				
		// 设置main方法与入参
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getMainClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, Config.getInstn().getMainArgs());
		
		// 设置标准流和异常流输出位置
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		
		return createScript(tpl.getName(), 
				StandardUtils.unix2dos(tpl.getContent()));
	}
	
	private static boolean buildDosVersion() {
		Template tpl = new Template(ScriptNames.VERSION_BAT);
		tpl.read(TplNames.START_TEMPLATE_DOS);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		
		// 声明变量（-cp路径前缀）
		String sets = "";
		List<String> prefixs = JarMgr.getInstn().getJarPathPrefixs();
		for(int idx = 0; idx < prefixs.size(); idx++) {
			sets = StrUtils.concat(sets, 
					"set lib", idx, "=", prefixs.get(idx), "\r\n");
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, sets);
				
		// 设置JDK命令（版本脚本不使用图形界面）
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
				
		// 设置main方法与入参
		tpl.set(Placeholders.MAIN_METHOD, Config.getInstn().getMainClass());
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "-p");	// 只打印版本
		
		// 设置标准流和异常流输出位置
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		
		return createScript(tpl.getName(), 
				StandardUtils.unix2dos(tpl.getContent()));
	}
	
}
