package exp.libs.mrp.services;

import java.util.List;

import exp.libs.mrp.Config;
import exp.libs.mrp.Log;
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
	
	private static boolean createScript(Template tpl) {
		String filePath = PathUtils.combine(
				Config.getInstn().getReleaseDir(), tpl.getName());
		String charset = Config.getInstn().getCharset();
		boolean isOk = FileUtils.write(filePath, tpl.getContent(), charset, false);
		if(isOk == true) {
			Log.info("创建脚本成功: ".concat(tpl.getName()));
		} else {
			Log.warn("创建脚本失败: ".concat(tpl.getName()));
		}
		return isOk;
	}
	
	private static boolean buildAppPath() {
		Template tpl = new Template(ScriptNames.APP_PATH);
		tpl.read(TplNames.APP_PATH_TEMPLATE);
		return createScript(tpl);
	}
	
	private static boolean buildThreadName() {
		Template tpl = new Template(ScriptNames.THREAD_NAME);
		tpl.read(TplNames.THREADNAME_TEMPLATE);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		tpl.set(Placeholders.THREAD_SUFFIX, Config.getInstn().getThreadSuffix());
		return createScript(tpl);
	}
	
	private static boolean buildUnixPid() {
		Template tpl = new Template(ScriptNames.ECHO_PID);
		tpl.read(TplNames.PID_TEMPLATE_UNIX);
		tpl.set(Placeholders.PROJECT_NAME, Config.getInstn().getPrjName());
		return createScript(tpl);
	}
	
	private static boolean buildUnixStart() {
		boolean isOk = true;
		
		StringBuilder sb = new StringBuilder();
		
		//获取依赖包集合
		List<String> jarPaths = JarMgr.getInstn().getLinuxJarPaths(prjName);
		
		//构造路径树,获取路径前缀集合
		_PathTree pt = new _PathTree("LINUX-" + prjName);
		pt.addMore(jarPaths);
		List<String> pathPres = pt.getLinuxPathPrefixSet(
				_MvnConfig.getInstn().getPathPrefixMode());
		int preNum = pathPres.size();
		
		/////////////////////////////////////////////////////
		// 加载模板
		Template tpl = new Template(ScriptNames.START_SH);
		tpl.read(TplNames.START_TEMPLATE_UNIX);
		
		/////////////////////////////////////////////////////
		// 设置项目名称
		tpl.set(Placeholders.PROJECT_NAME, prjName);
				
		// 设置路径前缀变量
		sb.setLength(0);
		for(int idx = 0; idx < preNum; idx++) {
			sb.append("export lib").append(idx);
			sb.append('=').append(pathPres.get(idx)).append("\n");
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, sb.toString());
		
		// 设置JDK路径
		tpl.set(Placeholders.JDK_PATH, FinalParams.JDK_PATH);
		
		// 设置JDK参数
		sb.setLength(0);
		sb.append("-Xms").append(_MvnConfig.getInstn().getXms()).append(' ');
		sb.append("-Xmx").append(_MvnConfig.getInstn().getXmx()).append(' ');
		sb.append(_MvnConfig.getInstn().getJdkParams());
		tpl.set(Placeholders.JDK_PARAMS, sb.toString());
		
		// 设置CP
		sb.setLength(0);
		for(String jarPath : jarPaths) {
			for(int i = 0; i < preNum; i++) {
				String pathPre = pathPres.get(i);
				if(jarPath.startsWith(pathPre)) {
					jarPath = jarPath.replace(pathPre, "$lib" + i);
					break;
				}
			}
			sb.append(jarPath).append(':');
		}
		tpl.set(Placeholders.CLASSPATH, sb.toString());
		
		// 设置main方法
		tpl.set(Placeholders.MAIN_METHOD, _MvnConfig.getInstn().getMainClass());
		
		// 设置main方法入参
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "");
		
		// 设置标准流和异常流
		tpl.set(Placeholders.STDOUT_CTRL, ">/dev/null");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		tpl.set(Placeholders.RUN_IN_BACKGROUND, "&");
		
		/////////////////////////////////////////////////////
		//标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.dos2unix(tpl.getContent());
		ps.addScript(ScriptNames.START_SH, scriptContent);
		
		return isOk;
	}
	
	private static boolean buildUnixStop() {
		boolean isOk = true;
		return isOk;
	}
	
	private static boolean buildUnixVersion() {
		boolean isOk = true;
		return isOk;
	}
	
	private static boolean buildDosStart() {
		boolean isOk = true;
		return isOk;
	}
	
	private static boolean buildDosVersion() {
		boolean isOk = true;
		return isOk;
	}
	
}
