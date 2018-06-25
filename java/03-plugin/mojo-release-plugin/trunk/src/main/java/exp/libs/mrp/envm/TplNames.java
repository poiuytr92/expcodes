package exp.libs.mrp.envm;

/**
 * <PRE>
 * 模板文件名称定义。
 * 要求所有模板文件名称都要先在此处定义，以便管理。
 * 
 * </PRE>
 * <B>PROJECT : </B> mojo-release-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TplNames {

	private final static String PACKAGE = "/exp/lib/mrp/";
	
	/**
	 * ant启动脚本模板名称
	 */
	public final static String BUILD_TEMPLATE_DOS = PACKAGE.concat("build_template_dos");
	
	/**
	 * ant配置脚本模板名称
	 */
	public final static String BUILD_TEMPLATE_XML = PACKAGE.concat("build_template_xml");
	
	/**
	 * 程序位置脚本模板名称
	 */
	public final static String APP_PATH_TEMPLATE = PACKAGE.concat("app_path_template");
	
	/**
	 * 线程名脚本模板名�?
	 */
	public final static String THREADNAME_TEMPLATE = PACKAGE.concat("threadname_template");
	
	/**
	 * 线程号查询脚本模板名�?
	 */
	public final static String PID_TEMPLATE_UNIX = PACKAGE.concat("pid_template_unix");
	
	/**
	 * dos启动脚本模板名称
	 */
	public final static String START_TEMPLATE_DOS = PACKAGE.concat("start_template_dos");
	
	/**
	 * unix启动脚本模板名称
	 */
	public final static String START_TEMPLATE_UNIX = PACKAGE.concat("start_template_unix");
	
	/**
	 * unix停止脚本模板名称
	 */
	public final static String STOP_TEMPLATE_DOS = PACKAGE.concat("stop_template_unix");
	
	/**
	 * 禁止外部构造，避免误用
	 */
	private TplNames() {}
}
