package exp.bilibili.plugin.utils;

import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 安全校验工具类
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SafetyUtils {

	/** 授权码正�? */
	private final static String REGEX = "[a-zA-Z]\\d[a-zA-Z]\\d";
	
	/** 授权时间单位 */
	private final static long DAY_MILLIS = 86400000L;
	
	/** 授权码文�? */
	private final static String A_PATH = "./conf/ac/authorization";
	
	/** 授权时间证书 */
	private final static String C_PATH = "./conf/ac/certificate";
	
	/** 私有化构造函�? */
	protected SafetyUtils() {}
	
	/**
	 * 校验软件的证�?(授权码和授权时间)是否有效.
	 *  管理�?:拥有全部功能, 不受授权码和证书影响
	 *  主播:拥有全部功能, 仅有授权时间
	 *  普通用�?:拥有部分功能, 受授权码和授权时间影�?
	 * @param code 授权�?(UPLIVE:主播; 匹配正则:普通用�?)
	 * @param isAdmin 是否为管理员
	 * @return true:有效; false:无效
	 */
	public static String checkAC(String code) {
		String errMsg = "";
		if(!checkAuthorization(code)) {
			errMsg = "无效的授权码";
		}
		
		// 对私时间用于对外出售，限制其使用期限（过期后不管对公时间如何，均无法启动�?
		if(!checkCertificate()) {
			errMsg = "软件授权已过�?";
		}
		return errMsg;
	}
	
	/**
	 * 检查输入的授权码是否有�?
	 * @param code 授权�?
	 * @return true:有效; false:无效
	 */
	private static boolean checkAuthorization(String code) {
		boolean isOk = false;
		if(!code.matches(REGEX)) {
			return isOk;
		}
		
		String authorization = fileToAuthorization();
		if(StrUtils.isEmpty(authorization)) {
			authorizationToFile(code);
			isOk = true;
			
		} else {
			isOk = authorization.equalsIgnoreCase(code);
		}
		return isOk;
	}
	
	/**
	 * 生成授权码到文件
	 * @return 授权�?
	 */
	private static String authorizationToFile(String code) {
		String authorization = CryptoUtils.toDES(code);
		FileUtils.write(A_PATH, authorization, Charset.ISO, false);
		return authorization;
	}
	
	/**
	 * 从文件还原授权码
	 * @return 授权�?
	 */
	private static String fileToAuthorization() {
		String authorization = FileUtils.read(A_PATH, Charset.ISO);
		return CryptoUtils.deDES(authorization);
	}
	
	/**
	 * 检测软件是否在授权有效期内
	 * @return
	 */
	private static boolean checkCertificate() {
		return (System.currentTimeMillis() < fileToCertificate());
	}
	
	/**
	 * 生成从现在开始一直到day天的之后的授权时�?, 并写入文�?
	 * @param day 有效�?
	 * @return 授权截止时间
	 */
	public static String certificateToFile(int day) {
		day = (day < 0 ? 0 : day);
		long time = System.currentTimeMillis() + DAY_MILLIS * day;
		String certificate = CryptoUtils.toDES(String.valueOf(time));
		FileUtils.write(C_PATH, certificate, Charset.ISO, false);
		return certificate;
	}
	
	/**
	 * 从文件还原授权时�?
	 * @return 授权截止时间
	 */
	public static long fileToCertificate() {
		String certificate = FileUtils.read(C_PATH, Charset.ISO);
		return NumUtils.toLong(CryptoUtils.deDES(certificate), 0);
	}
	
}
