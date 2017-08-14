package exp.libs.mrp.services;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.catt.plugin.core.builder.impl.AntBuilder;
import com.catt.plugin.core.builder.impl.AutodbBuilder;
import com.catt.plugin.core.builder.impl.CryptoBuilder;
import com.catt.plugin.core.builder.impl.MainBuilder;
import com.catt.plugin.core.builder.impl.StartcheckBuilder;
import com.catt.plugin.core.manager.LogMgr;
import com.catt.util.io.FileUtils;

/**
 * <PRE>
 * 脚本导建器。
 * 分离 创建单个脚本 与 组装多个脚本 的方法，减少以后某些项目需要增减脚本时的改动量。
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class ScriptDirector {

	/**
	 * 禁止外部构造，避免误用
	 */
	private ScriptDirector() {}
	
	/**
	 * 创建项目脚本
	 * @param outDir 输出目录
	 * @param ps 项目脚本容器
	 * @return 是否创建成功
	 */
	public static boolean createScripts(String outDir, ProjectScript ps) {
		boolean isSucc = true;
		for(Iterator<String> scriptNameIts = 
				ps.getProScripts().keySet().iterator();
				scriptNameIts.hasNext();) {
			String scriptName = scriptNameIts.next();
			String scriptContent = ps.getScript(scriptName);
			String scriptPath = outDir + File.separator + scriptName;
			
			try {
				FileUtils.writeStringToFile(
						new File(scriptPath), scriptContent, false);
			} catch (IOException e) {
				LogMgr.error("创建脚本[" + scriptName + "]失败.", e);
				isSucc = false;
			}
		}
		return isSucc;
	}
	
	/**
	 * 获取Ant项目脚本
	 * @return 构造好的Ant项目脚本
	 */
	public static ProjectScript getAntScript() {
		AntBuilder builder = new AntBuilder();
		builder.buildAntStart();
		builder.buildAntConf();
		return builder.constructScripts();
	}
	
	/**
	 * 获取Autodb项目脚本
	 * @return 构造好的Autodb项目脚本
	 */
	public static ProjectScript getAutodbScript() {
		AutodbBuilder builder = new AutodbBuilder();
		builder.buildDosStart();
		builder.buildUnixStart();
		return builder.constructScripts();
	}
	
	/**
	 * 获取Crypto项目脚本
	 * @return 构造好的Crypto项目脚本
	 */
	public static ProjectScript getCryptoScript() {
		CryptoBuilder builder = new CryptoBuilder();
		builder.buildDosStart();
		builder.buildUnixStart();
		return builder.constructScripts();
	}
	
	/**
	 * 获取主项目脚本
	 * @return 构造好的主项目脚本
	 */
	public static ProjectScript getMainScript() {
		MainBuilder builder = new MainBuilder();
		builder.buildThreadName();
		builder.buildUnixPid();
		builder.buildDosStart();
		builder.buildUnixStart();
		builder.buildDosVersion();
		builder.buildUnixVersion();
		builder.buildUnixStop();
		builder.buildDosGC();
		builder.buildUnixGC();
		return builder.constructScripts();
	}
	
	/**
	 * 获取Startcheck项目脚本
	 * @return 构造好的Startcheck项目脚本
	 */
	public static ProjectScript getStartcheckScript() {
		StartcheckBuilder builder = new StartcheckBuilder();
		builder.buildDosStart();
		builder.buildUnixStart();
		return builder.constructScripts();
	}
	
}
