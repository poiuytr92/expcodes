package exp.libs.utils.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.pub.StrUtils;

/**
 * <PRE>
 * 系统命令行操作工具包.	
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CmdUtils {
	
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(CmdUtils.class);
	
	/** 私有化构造函数 */
	protected CmdUtils() {}
	
	/**
	 * 执行控制台命令
	 * @param cmd
	 * @return
	 */
	public static String execute(String cmd) {
		return execute(cmd, true);
	}
	
	/**
	 * 执行控制台命令
	 * @param cmd
	 * @param onlyResult 只返回命令执行结果（无执行状态、异常信息）
	 * @return
	 */
	public static String execute(String cmd, boolean onlyResult) {
		String result = "";
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			result = getCmdResult(process, onlyResult);
			process.destroy();
			
		} catch (Exception e) {
			log.error("执行控制台命令失败: {}", cmd, e);
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
			log.error("获取命令返回值失败.", e);
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
			log.error("读取命令IO流失败.", e);
		}
		return buff.toString();
	}
	
	/**
	 * 通过命令行进行文件/文件夹复制
	 * @param srcPath
	 * @param destPath
	 */
	public static String copy(String srcPath, String destPath) {
		String cmd = "";
		File file = new File(srcPath);
		if (OSUtils.isWin()) {
			if (file.isFile()) {
				cmd = StrUtils.concat("copy ", srcPath, " ", destPath, " /Y");
				
			} else {
				srcPath = srcPath.trim().replaceAll("\\\\$", "");
				cmd = StrUtils.concat("xcopy ", srcPath, " ", destPath, " /e /y");
			}
		} else {
			srcPath = srcPath.trim().replaceAll("/$", "");
			cmd = StrUtils.concat("cp -r ", srcPath, " ", destPath);
		}
		return execute(cmd);
	}
	
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
	 * 打开文件（只支持win系统）
	 * @param filePath
	 * @return
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
	
	public static void kill(String processName) {
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
