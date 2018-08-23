package exp.fpf.utils;

import net.sf.json.JSONObject;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <pre>
 * 业务逻辑工具
 * </pre>	
 * <br/><B>PROJECT : </B> file-port-forwarding
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
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
	
	/**
	 * 构造Json格式数据流.
	 * @param sessionId 会话ID
	 * @param data 需要封装的原数据
	 * @return
	 */
	public static String genJsonData(String sessionId, String data) {
		JSONObject json = new JSONObject();
		json.put(Param.SID, sessionId);
		json.put(Param.DATA, data);
		return json.toString();
	}
	
	/**
	 * 构造数据流文件路径.
	 * @param srMgr
	 * @param sessionId
	 * @param timeSequence
	 * @param type
	 * @param snkIP
	 * @param snkPort
	 * @return
	 */
	public static String genFileDataPath(SRMgr srMgr, String sessionId, 
			int timeSequence, String type, String snkIP, int snkPort) {
		String recvFileName = BIZUtils.toFileName(
				sessionId, timeSequence++, type, snkIP, snkPort);
		srMgr.addRecvTabu(recvFileName);
		
		String recvFilePath = PathUtils.combine(srMgr.getSendDir(), recvFileName);
		return recvFilePath;
	}
	
	
}
