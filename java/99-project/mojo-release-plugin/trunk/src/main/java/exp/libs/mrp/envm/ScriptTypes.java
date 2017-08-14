package exp.libs.mrp.envm;

/**
 * <PRE>
 * 所生成的启动脚本类型定义。
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class ScriptTypes {

	/**
	 * 生成引用lib目录优先的脚本。
	 * 即把所有依赖包都复制到./lib目录下，然后脚本引用./lib的包。
	 */
	public final static String LIB = "lib";
	
	/**
	 * 生成引用commonLib目录优先的脚本。
	 * 即若依赖包原本是在commonLib的，则在脚本直接引用。
	 * 若不在commonLib的，但在commonLib检索到的，则引用检索路径。
	 * 若不在commonLib的，又不能在先commonLib检索到的，尝试复制到./lib下，再在脚本引用。
	 */
	public final static String COMMONLIB = "commonLib";
	
	/**
	 * 生成引用maven目录优先的脚本。
	 * 即若依赖包原本是在maven仓库的，则在脚本直接引用。
	 * 若不在maven仓库的，先复制到./lib下，再在脚本引用。
	 */
	public final static String MAVEN = "maven";
	
	/**
	 * 禁止构造，避免误用
	 */
	private ScriptTypes() {}
}
