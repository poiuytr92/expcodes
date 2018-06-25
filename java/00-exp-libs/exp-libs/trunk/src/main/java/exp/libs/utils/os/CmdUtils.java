package exp.libs.utils.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 系统命令行操作工具.	
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CmdUtils {
	
	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(CmdUtils.class);
	
	/** 私有化构造函�? */
	protected CmdUtils() {}
	
	/**
	 * 执行控制台命�?
	 * @param cmd 控制台命�?
	 * @return 执行结果
	 */
	public static String execute(String cmd) {
		return execute(cmd, true);
	}
	
	/**
	 * 执行控制台命�?
	 * @param cmd 控制台命�?
	 * @param onlyResult true:只返回命令执行结�?; false:包含执行状态、异常信�?
	 * @return 执行结果
	 */
	public static String execute(String cmd, boolean onlyResult) {
		String result = "";
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			result = getCmdResult(process, onlyResult);
			process.destroy();
			
		} catch (Exception e) {
			log.error("执行控制台命令失�?: {}", cmd, e);
		}
		return result;
	}

	private static String getCmdResult(Process process, boolean onlyResult) {
		StringBuffer result = new StringBuffer();
		try {
			InputStream is = process.getInputStream();
			
			if(onlyResult) {
				result.append(readInputStream(is));
				
			} else {
				InputStream isErr = process.getErrorStream();
				int exitValue = process.waitFor();	// 此方法会阻塞, 直到命令执行结束
				
				result.append("[info ]\r\n").append(readInputStream(is));
				result.append("[error]\r\n").append(readInputStream(isErr));
				result.append("[state] ").append(exitValue);
				
				isErr.close();
			}
			is.close();
	
		} catch (Exception e) {
			log.error("获取命令返回值失�?.", e);
		}
		return result.toString();
	}

	private static String readInputStream(InputStream is) {
		StringBuffer buff = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is, OSUtils.getSysEncoding()));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				buff.append(line).append("\r\n");
			}
		} catch (Exception e) {
			log.error("读取命令IO流失�?.", e);
		}
		return buff.toString();
	}
	
	/**
	 * 通过命令行进行文�?/文件夹复�?
	 * @param srcPath 源文件路�?
	 * @param snkPath 目标文件路径
	 * @return 执行结果
	 */
	public static String copy(String srcPath, String snkPath) {
		String cmd = "";
		File file = new File(srcPath);
		if (OSUtils.isWin()) {
			if (file.isFile()) {
				cmd = StrUtils.concat("copy ", srcPath, " ", snkPath, " /Y");
				
			} else {
				srcPath = srcPath.trim().replaceAll("\\\\$", "");
				cmd = StrUtils.concat("xcopy ", srcPath, " ", snkPath, " /e /y");
			}
		} else {
			srcPath = srcPath.trim().replaceAll("/$", "");
			cmd = StrUtils.concat("cp -r ", srcPath, " ", snkPath);
		}
		return execute(cmd);
	}
	
	/**
	 * 打开DOS控制台（只支持win系统�?
	 * @return 执行结果
	 */
	public static String openDosUI() {
		String result = "";
		if(!OSUtils.isWin()) {
			result = "Unsupport except windows-system.";
			
		} else {
			String cmd = "cmd /c start";
			result = execute(cmd);
		}
		return result;
	}
	
	/**
	 * 打开文件（只支持win系统�?
	 * @param filePath 文件路径
	 * @return 执行结果
	 */
	public static String openFile(String filePath) {
		String result = "";
		if(!OSUtils.isWin()) {
			result = "Unsupport except windows-system.";
			
		} else if(StrUtils.isEmpty(filePath)) {
			result = StrUtils.concat("Invaild file: ", filePath);
			
		} else {
			File file = new File(filePath);
			if(!file.exists()) {
				result = StrUtils.concat("Invaild file: ", filePath);
				
			} else {
				String cmd = StrUtils.concat(
						"rundll32 url.dll FileProtocolHandler ", 
						file.getAbsolutePath());
				result = execute(cmd);
			}
		}
		return result;
	}
	
	/**
	 * 打开网页（只支持win系统�?
	 * @param url 网页地址
	 * @return 执行结果
	 */
	public static String openHttp(String url) {
		String result = "";
		if(!OSUtils.isWin()) {
			result = "Unsupport except windows-system.";
			
		} else if(StrUtils.isEmpty(url)) {
			result = StrUtils.concat("Invaild url: ", url);
			
		} else {
			String cmd = StrUtils.concat("cmd /c start ", url);
			result = execute(cmd);
		}
		return result;
	}
	
	/**
	 * 运行bat脚本（只支持win系统�?
	 * @param batFilePath 批处理脚本路�?
	 * @return 执行结果
	 */
	public static String runBat(String batFilePath) {
		String result = "";
		if(!OSUtils.isWin()) {
			result = "Unsupport except windows-system.";
			
		} else if(StrUtils.isEmpty(batFilePath)) {
			result = StrUtils.concat("Invaild bat: ", batFilePath);
			
		} else {
			String cmd = StrUtils.concat("cmd /c start ", batFilePath);
			result = execute(cmd);
		}
		return result;
	}
	
	/**
	 * 终止进程（只支持win系统�?
	 * @param processName 进程名称
	 */
	public static void kill(String processName) {
		if(!OSUtils.isWin()) {
			return;
		}
		
		Pattern ptn = Pattern.compile(StrUtils.concat(processName, " +?(\\d+) "));
		String tasklist = execute("tasklist");
		String[] tasks = tasklist.split("\n");
		
		for(String task : tasks) {
			if(task.startsWith(StrUtils.concat(processName, " "))) {
				Matcher mth = ptn.matcher(task);
				if(mth.find()) {
					String pid = mth.group(1);
					execute(StrUtils.concat("taskkill /f /t /im ", pid));
				}
			}
		}
	}

}
