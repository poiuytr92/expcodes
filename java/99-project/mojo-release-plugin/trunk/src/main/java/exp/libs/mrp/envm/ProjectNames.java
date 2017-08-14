package exp.libs.mrp.envm;

/**
 * <PRE>
 * 需要生成脚本的 内联项目名称 定义(主项目除外).
 * 内联项目的名称为全小写,避免歧义.
 * 
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class ProjectNames {

	/**
	 * ant项目
	 */
	public final static String ANT = "ant".toLowerCase();
	
	/**
	 * crypto项目
	 */
	public final static String CRYPTO = "crypto".toLowerCase();
	
	/**
	 * autodb项目
	 */
	public final static String AUTODB = "autodb".toLowerCase();
	
	/**
	 * startcheck项目
	 */
	public final static String STARTCHECK = "startcheck".toLowerCase();
	
	/**
	 * 线程名修改项目
	 */
	public final static String MTN = "MTN".toLowerCase();	// modify thread name
	
	/**
	 * 禁止外部构造，避免误用
	 */
	private ProjectNames() {}
}
