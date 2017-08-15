package exp.libs.mrp.path;

/**
 * <PRE>
 * 构建器接口
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public interface IBuilder {
	
	/**
	 * 获取装载了项目的各个脚本的容器
	 * @return 项目脚本容器
	 */
	public ProjectScript constructScripts();
	
	/**
	 * 创建线程名称声明脚本
	 */
	public void buildThreadName();
	
	/**
	 * 创建unix线程号查询脚本
	 */
	public void buildUnixPid();
	
	/**
	 * 创建dos启动脚本
	 */
	public void buildDosStart();
	
	/**
	 * 创建unix启动脚本
	 */
	public void buildUnixStart();
	
	/**
	 * 创建unix停止脚本
	 */
	public void buildUnixStop();
	
	/**
	 * 创建dos版本脚本
	 */
	public void buildDosVersion();
	
	/**
	 * 创建dos版本脚本
	 */
	public void buildUnixVersion();
	
	/**
	 * 创建dos监控脚本
	 */
	public void buildDosGC();
	
	/**
	 * 创建unix监控脚本
	 */
	public void buildUnixGC();
	
	/**
	 * 创建Ant启动脚本(build.bat)
	 */
	public void buildAntStart();
	
	/**
	 * 创建Ant配置脚本(build.xml)
	 */
	public void buildAntConf();
	
}
