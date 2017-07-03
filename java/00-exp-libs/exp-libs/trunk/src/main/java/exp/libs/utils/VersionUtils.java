package exp.libs.utils;

/**
 * <PRE>
 * 版本工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class VersionUtils {

	public static String cutVer(String jarName) {
		String name = jarName;
		if(name != null && jarName.contains(".jar")) {
			name = name.trim();
			if(!"".equals(name)) {
				name = name.replaceAll("[-_\\.]?\\d+\\..*", ".jar");
			}
		}
		return name;
	}
	
}
