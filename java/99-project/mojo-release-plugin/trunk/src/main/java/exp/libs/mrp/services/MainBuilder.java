package exp.libs.mrp.services;

import java.util.List;

import com.catt.plugin.Config;
import com.catt.plugin.core.builder.IBuilder;
import com.catt.plugin.core.builder.ProjectScript;
import com.catt.plugin.core.envm.FinalParams;
import com.catt.plugin.core.envm.ScriptNames;
import com.catt.plugin.core.manager.JarMgr;
import com.catt.plugin.path.PathTree;
import com.catt.plugin.template.Template;
import com.catt.plugin.template.envm.Placeholders;
import com.catt.plugin.template.envm.TemplateFiles;
import com.catt.plugin.utils.StandUtils;

/**
 * <PRE>
 * 主项目脚本构建器
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
final public class MainBuilder implements IBuilder {

	/**
	 * 主项目脚本容器
	 */
	private ProjectScript ps;
	
	/**
	 * 主项目名称
	 */
	private String prjName;
	
	/**
	 * 构造函数
	 */
	public MainBuilder() {
		this.prjName = _MvnConfig.getInstn().getProjectName();
		this.ps = new ProjectScript(prjName);
	}
	
	@Override
	public ProjectScript constructScripts() {
		return ps;
	}

	@Override
	public void buildThreadName() {
		// 加载模板
		Template tpl = new Template(ScriptNames.THREAD_NAME);
		tpl.read(TemplateFiles.THREADNAME_TEMPLATE);
		
		// 设置项目名称
		tpl.set(Placeholders.PROJECT_NAME, prjName);
		
		// 设置线程后缀
		tpl.set(Placeholders.THREAD_SUFFIX, _MvnConfig.getInstn().getThreadSuffix());
				
		// 标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.dos2unix(tpl.getContent());
		ps.addScript(ScriptNames.THREAD_NAME, scriptContent);
	}

	@Override
	public void buildUnixPid() {
		// 加载模板
		Template tpl = new Template(ScriptNames.ECHO_PID);
		tpl.read(TemplateFiles.PID_TEMPLATE_UNIX);
		
		// 设置项目名称
		tpl.set(Placeholders.PROJECT_NAME, prjName);
				
		// 标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.dos2unix(tpl.getContent());
		ps.addScript(ScriptNames.ECHO_PID, scriptContent);
	}
	
	@Override
	public void buildDosStart() {
		StringBuilder sb = new StringBuilder();
		
		//获取依赖包集合
		List<String> jarPaths = JarMgr.getInstn().getWinJarPaths(prjName);
		
		//构造路径树,获取路径前缀集合
		PathTree pt = new PathTree("DOS-" + prjName);
		pt.addMore(jarPaths);
		List<String> pathPres = pt.getWinPathPrefixSet(
				_MvnConfig.getInstn().getPathPrefixMode());
		int preNum = pathPres.size();
		
		/////////////////////////////////////////////////////
		// 加载模板
		Template tpl = new Template(ScriptNames.START_BAT);
		tpl.read(TemplateFiles.START_TEMPLATE_DOS);
		
		/////////////////////////////////////////////////////
		// 设置项目名称
		tpl.set(Placeholders.PROJECT_NAME, prjName);
		
		// 设置路径前缀变量
		sb.setLength(0);
		for(int idx = 0; idx < preNum; idx++) {
			sb.append("set lib").append(idx);
			sb.append('=').append(pathPres.get(idx)).append("\r\n");
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
					jarPath = jarPath.replace(pathPre, "%lib" + i + "%");
					break;
				}
			}
			sb.append(jarPath).append(';');
		}
		tpl.set(Placeholders.CLASSPATH, sb.toString());
		
		// 设置main方法
		tpl.set(Placeholders.MAIN_METHOD, _MvnConfig.getInstn().getMainClass());
		
		// 设置main方法入参
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "");
		
		// 设置标准流和异常流
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		
		/////////////////////////////////////////////////////
		//标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.unix2dos(tpl.getContent());
		ps.addScript(ScriptNames.START_BAT, scriptContent);
	}

	@Override
	public void buildUnixStart() {
		StringBuilder sb = new StringBuilder();
		
		//获取依赖包集合
		List<String> jarPaths = JarMgr.getInstn().getLinuxJarPaths(prjName);
		
		//构造路径树,获取路径前缀集合
		PathTree pt = new PathTree("LINUX-" + prjName);
		pt.addMore(jarPaths);
		List<String> pathPres = pt.getLinuxPathPrefixSet(
				_MvnConfig.getInstn().getPathPrefixMode());
		int preNum = pathPres.size();
		
		/////////////////////////////////////////////////////
		// 加载模板
		Template tpl = new Template(ScriptNames.START_SH);
		tpl.read(TemplateFiles.START_TEMPLATE_UNIX);
		
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
	}

	@Override
	public void buildUnixStop() {
		// 加载模板
		Template tpl = new Template(ScriptNames.STOP_SH);
		tpl.read(TemplateFiles.STOP_TEMPLATE_DOS);
		
		// 设置项目名称
		tpl.set(Placeholders.PROJECT_NAME, prjName);
				
		// 标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.dos2unix(tpl.getContent());
		ps.addScript(ScriptNames.STOP_SH, scriptContent);
	}

	@Override
	public void buildDosVersion() {
		StringBuilder sb = new StringBuilder();
		
		//获取依赖包集合
		List<String> jarPaths = JarMgr.getInstn().getWinJarPaths(prjName);
		
		//构造路径树,获取路径前缀集合
		PathTree pt = new PathTree("DOS-" + prjName);
		pt.addMore(jarPaths);
		List<String> pathPres = pt.getWinPathPrefixSet(
				_MvnConfig.getInstn().getPathPrefixMode());
		int preNum = pathPres.size();
		
		/////////////////////////////////////////////////////
		// 加载模板
		Template tpl = new Template(ScriptNames.VERSION_BAT);
		tpl.read(TemplateFiles.START_TEMPLATE_DOS);
		
		/////////////////////////////////////////////////////
		// 设置项目名称
		tpl.set(Placeholders.PROJECT_NAME, prjName);
				
		// 设置路径前缀变量
		sb.setLength(0);
		for(int idx = 0; idx < preNum; idx++) {
			sb.append("set lib").append(idx);
			sb.append('=').append(pathPres.get(idx)).append("\r\n");
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
					jarPath = jarPath.replace(pathPre, "%lib" + i + "%");
					break;
				}
			}
			sb.append(jarPath).append(';');
		}
		tpl.set(Placeholders.CLASSPATH, sb.toString());
		
		// 设置main方法
		tpl.set(Placeholders.MAIN_METHOD, _MvnConfig.getInstn().getVersionClass());
		
		// 设置main方法入参
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "");
		
		// 设置标准流和异常流
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		
		/////////////////////////////////////////////////////
		//标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.unix2dos(tpl.getContent());
		ps.addScript(ScriptNames.VERSION_BAT, scriptContent);
	}

	@Override
	public void buildUnixVersion() {
		StringBuilder sb = new StringBuilder();
		
		//获取依赖包集合
		List<String> jarPaths = JarMgr.getInstn().getLinuxJarPaths(prjName);
		
		//构造路径树,获取路径前缀集合
		PathTree pt = new PathTree("LINUX-" + prjName);
		pt.addMore(jarPaths);
		List<String> pathPres = pt.getLinuxPathPrefixSet(
				_MvnConfig.getInstn().getPathPrefixMode());
		int preNum = pathPres.size();
		
		/////////////////////////////////////////////////////
		// 加载模板
		Template tpl = new Template(ScriptNames.VERSION_SH);
		tpl.read(TemplateFiles.START_TEMPLATE_UNIX);
		
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
		tpl.set(Placeholders.MAIN_METHOD, _MvnConfig.getInstn().getVersionClass());
		
		// 设置main方法入参
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "");
		
		// 设置标准流和异常流
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		tpl.set(Placeholders.RUN_IN_BACKGROUND, "");
		
		/////////////////////////////////////////////////////
		//标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.dos2unix(tpl.getContent());
		ps.addScript(ScriptNames.VERSION_SH, scriptContent);
	}

	@Override
	public void buildDosGC() {
		StringBuilder sb = new StringBuilder();
		
		//获取依赖包集合
		List<String> jarPaths = JarMgr.getInstn().getWinJarPaths(prjName);
		
		//构造路径树,获取路径前缀集合
		PathTree pt = new PathTree("DOS-" + prjName);
		pt.addMore(jarPaths);
		List<String> pathPres = pt.getWinPathPrefixSet(
				_MvnConfig.getInstn().getPathPrefixMode());
		int preNum = pathPres.size();
		
		/////////////////////////////////////////////////////
		// 加载模板
		Template tpl = new Template(ScriptNames.START_GC_BAT);
		tpl.read(TemplateFiles.START_TEMPLATE_DOS);
		
		/////////////////////////////////////////////////////
		// 设置项目名称
		tpl.set(Placeholders.PROJECT_NAME, prjName);
				
		// 设置路径前缀变量
		sb.setLength(0);
		for(int idx = 0; idx < preNum; idx++) {
			sb.append("set lib").append(idx);
			sb.append('=').append(pathPres.get(idx)).append("\r\n");
		}
		tpl.set(Placeholders.VARIABLE_DECLARATION, sb.toString());
		
		// 设置JDK路径
		tpl.set(Placeholders.JDK_PATH, FinalParams.JDK_PATH);
		
		// 设置JDK参数
		sb.setLength(0);
		sb.append("-Xms").append(_MvnConfig.getInstn().getXms()).append(' ');
		sb.append("-Xmx").append(_MvnConfig.getInstn().getXmx()).append(' ');
		sb.append("-Dcom.sun.management.jmxremote.port=9004 ");
		sb.append("-Dcom.sun.management.jmxremote.ssl=\"false\" ");
		sb.append("-Dcom.sun.management.jmxremote.authenticate=\"false\" ");
		sb.append("-verbose:gc -Xloggc:gc.log ");
		sb.append(_MvnConfig.getInstn().getJdkParams());
		tpl.set(Placeholders.JDK_PARAMS, sb.toString());
		
		// 设置CP
		sb.setLength(0);
		for(String jarPath : jarPaths) {
			for(int i = 0; i < preNum; i++) {
				String pathPre = pathPres.get(i);
				if(jarPath.startsWith(pathPre)) {
					jarPath = jarPath.replace(pathPre, "%lib" + i + "%");
					break;
				}
			}
			sb.append(jarPath).append(';');
		}
		tpl.set(Placeholders.CLASSPATH, sb.toString());
		
		// 设置main方法
		tpl.set(Placeholders.MAIN_METHOD, _MvnConfig.getInstn().getMainClass());
		
		// 设置main方法入参
		tpl.set(Placeholders.MAIN_METHOD_PARAMS, "");
		
		// 设置标准流和异常流
		tpl.set(Placeholders.STDOUT_CTRL, "");
		tpl.set(Placeholders.ERROUT_CTRL, "2>err.log");
		
		/////////////////////////////////////////////////////
		//标准化脚本内容,并将其植入到项目脚本容器,等待构造
		String scriptContent = StandUtils.unix2dos(tpl.getContent());
		ps.addScript(ScriptNames.START_GC_BAT, scriptContent);
	}

	@Override
	public void buildUnixGC() {
		StringBuilder sb = new StringBuilder();
		
		//获取依赖包集合
		List<String> jarPaths = JarMgr.getInstn().getLinuxJarPaths(prjName);
		
		//构造路径树,获取路径前缀集合
		PathTree pt = new PathTree("LINUX-" + prjName);
		pt.addMore(jarPaths);
		List<String> pathPres = pt.getLinuxPathPrefixSet(
				_MvnConfig.getInstn().getPathPrefixMode());
		int preNum = pathPres.size();
		
		/////////////////////////////////////////////////////
		// 加载模板
		Template tpl = new Template(ScriptNames.START_GC_SH);
		tpl.read(TemplateFiles.START_TEMPLATE_UNIX);
		
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
		sb.append("-Dcom.sun.management.jmxremote.port=9004 ");
		sb.append("-Dcom.sun.management.jmxremote.ssl=\"false\" ");
		sb.append("-Dcom.sun.management.jmxremote.authenticate=\"false\" ");
		sb.append("-verbose:gc -Xloggc:gc.log ");
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
		ps.addScript(ScriptNames.START_GC_SH, scriptContent);
	}

	@Override
	public void buildAntStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buildAntConf() {
		// TODO Auto-generated method stub
		
	}

}
