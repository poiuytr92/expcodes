package exp.libs.mrp.bean;

/**
 * <PRE>
 * 常量参数表
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1_0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft_com
 * @since     jdk版本：jdk1_6
 */
public class FinalParams {

	/**
	 * classpath kind属性枚举值之一：lib
	 */
	public final static String CP_KIND_LIB = "lib";
	
	/**
	 * classpath kind属性枚举值之一：var
	 */
	public final static String CP_KIND_VAR = "var";
	
	/**
	 * classpath kind属性枚举值之一：src
	 */
	public final static String CP_KIND_SRC = "src";
	
	/**
	 * classpath定义的路径变量名称：lib
	 */
	public final static String VAR_LIB = "lib";
	
	/**
	 * classpath定义的路径变量名称：commonLib
	 */
	public final static String VAR_COMMONLIB = "commonLib";
	
	/**
	 * classpath定义的路径变量名称：maven
	 */
	public final static String VAR_MAVEN = "maven";
	
	/**
	 * classpath定义的路径变量名称：M2_REPO(由  mvn eclipse:eclipse 命令自动生成)
	 */
	public final static String VAR_MAVEN_CMD = "M2_REPO";
	
	/**
	 * 以变量开头的路径前缀：lib/
	 */
	public final static String VP_LIB = VAR_LIB + "/";
	
	/**
	 * 以变量开头的路径前缀：commonLib/
	 */
	public final static String VP_COMMONLIB = VAR_COMMONLIB + "/";
	
	/**
	 * 以变量开头的路径前缀：maven/
	 */
	public final static String VP_MAVEN = VAR_MAVEN + "/";
	
	/**
	 * 以变量开头的路径前缀：M2_REPO/
	 */
	public final static String VP_MAVEN_CMD = VAR_MAVEN_CMD + "/";
	
	/**
	 * JDK路径
	 */
	public final static String JDK_PATH = "java";
		
	/**
	 * JDK版本
	 */
	public final static String JDK_VERSION = "1.6";
	
	/**
	 * 默认编码
	 */
	public final static String DEFAULT_CHARSET = "ISO-8859-1";
	
	/**
	 * catt 包名前缀
	 */
	public final static String CATT_JAR_PREFIX = "catt-";
	
	/**
	 * catt 包名正则
	 */
	public final static String CATT_JAR_REGEX = 
			"^(catt-\\D*)((\\d+\\.\\d+\\.\\d+\\.\\d+)?).*?\\.jar$";
	
	/**
	 * 禁止外部构造，避免误用
	 */
	private FinalParams() {}
}
