package exp.fpf.utils;

import exp.fpf.envm.Param;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <pre>
 * 业务逻辑工具
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-28
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class BIZUtils {

	protected BIZUtils() {}
	
	public static String encode(byte[] data, int offset, int len) {
		String hex = BODHUtils.toHex(data, offset, len);
		return CryptoUtils.toDES(hex);
	}
	
	public static byte[] decode(String data) {
		String hex = CryptoUtils.deDES(data);
		return BODHUtils.toBytes(hex);
	}
	
	public static String toFileName(String sessionId, int timeSequence, 
			String fileType, String snkIP, int snkPort) {
		String fileName = StrUtils.concat(fileType, "#", snkIP, "@", snkPort, 
				"-S", sessionId, "-T", timeSequence, Param.SUFFIX);
		return fileName;
	}
	
}
