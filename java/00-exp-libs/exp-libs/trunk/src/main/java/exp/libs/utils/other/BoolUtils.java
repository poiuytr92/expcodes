package exp.libs.utils.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 布尔数据处理工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BoolUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(BoolUtils.class);
	
	/** 私有化构造函数 */
	protected BoolUtils() {}
	
	public static boolean toBool(String tof) {
		return toBool(tof, false);
	}
	
	public static boolean toBool(String tof, boolean defavlt) {
		boolean bool = defavlt;
		try {
			bool = Boolean.parseBoolean(tof);
		} catch (Exception e) {
			log.error("转换 [{}] 为bool类型失败.", tof, e);
		}
		return bool;
	}
	
}
