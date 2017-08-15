package exp.libs.mrp.envm;

/**
 * <PRE>
 * 模板文件名称定义。
 * 要求所有模板文件名称都要先在此处定义，以便管理。
 * 
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class TplNames {

	/**
	 * ant启动脚本模板名称
	 */
	public final static String BUILD_TEMPLATE_DOS = "build_template_dos";
	
	/**
	 * ant配置脚本模板名称
	 */
	public final static String BUILD_TEMPLATE_XML = "build_template_xml";
	
	/**
	 * 程序位置脚本模板名称
	 */
	public final static String APP_PATH_TEMPLATE = "app_path_template";
	
	/**
	 * 线程名脚本模板名称
	 */
	public final static String THREADNAME_TEMPLATE = "threadname_template";
	
	/**
	 * 线程号查询脚本模板名称
	 */
	public final static String PID_TEMPLATE_UNIX = "pid_template_unix";
	
	/**
	 * dos启动脚本模板名称
	 */
	public final static String START_TEMPLATE_DOS = "start_template_dos";
	
	/**
	 * unix启动脚本模板名称
	 */
	public final static String START_TEMPLATE_UNIX = "start_template_unix";
	
	/**
	 * unix停止脚本模板名称
	 */
	public final static String STOP_TEMPLATE_DOS = "stop_template_unix";
	
	/**
	 * 禁止外部构造，避免误用
	 */
	private TplNames() {}
}
