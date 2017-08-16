package exp.libs.mrp.envm;

/**
 * <PRE>
 * 所生成的脚本名称定义。
 * 要求所有脚本文件名称都要先在此处定义(注意末尾还有一个所有脚本清单ALL_SCRIPTS)，以便管理。
 * 
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class ScriptNames {

	/**
	 * ant打包脚本:dos
	 */
	public final static String BUILD_BAT = "build.bat";
	
	/**
	 * ant配置脚本
	 */
	public final static String BUILD_XML = "build.xml";
	
	/**
	 * SQL自动化部署脚本:dos
	 */
	public final static String AUTODB_BAT = "autodb.bat";
	
	/**
	 * SQL自动化部署脚本:unix
	 */
	public final static String AUTODB_SH = "autodb.sh";
	
	/**
	 * 加密脚本:dos
	 */
	public final static String CRYPTO_BAT = "crypto.bat";
	
	/**
	 * 加密脚本:unix
	 */
	public final static String CRYPTO_SH = "crypto.sh";
	
	/**
	 * 接入环境检查脚本:dos
	 */
	public final static String STARTCHECK_BAT = "startcheck.bat";
	
	/**
	 * 接入环境检查脚本:unix
	 */
	public final static String STARTCHECK_SH = "startcheck.sh";
	
	/**
	 * 程序位置声明脚本
	 */
	public final static String APP_PATH = "_app_path";
	
	/**
	 * 线程名声明脚本
	 */
	public final static String THREAD_NAME = "_threadname";
	
	/**
	 * 线程号查询脚本:unix
	 */
	public final static String ECHO_PID = "echo-pid.sh";
	
	/**
	 * 项目启动脚本（含gc日志）:dos
	 */
	public final static String START_GC_BAT = "start_gc.bat";
	
	/**
	 * 项目启动脚本（含gc日志）:unix
	 */
	public final static String START_GC_SH = "start_gc.sh";
	
	/**
	 * 项目启动脚本（无gc日志）:dos
	 */
	public final static String START_BAT = "start.bat";
	
	/**
	 * 项目启动脚本（无gc日志）:unix
	 */
	public final static String START_SH = "start.sh";
	
	/**
	 * 项目停止脚本:unix
	 */
	public final static String STOP_SH = "stop.sh";
	
	/**
	 * 项目版本打印脚本:dos
	 */
	public final static String VERSION_BAT = "version.bat";
	
	/**
	 * 项目版本打印脚本:unix
	 */
	public final static String VERSION_SH = "version.sh";
	
	/**
	 * 所有脚本名称清单
	 */
	public final static String[] ALL_SCRIPTS = {
		BUILD_BAT,
		BUILD_XML,
		AUTODB_BAT,
		AUTODB_SH,
		CRYPTO_BAT,
		CRYPTO_SH,
		STARTCHECK_BAT,
		STARTCHECK_SH,
		THREAD_NAME,
		ECHO_PID,
		START_GC_BAT,
		START_GC_SH,
		START_BAT,
		START_SH,
		STOP_SH,
		VERSION_BAT,
		VERSION_SH,
	};
	
	/**
	 * 禁止构造，避免误用
	 */
	private ScriptNames() {}
	
}