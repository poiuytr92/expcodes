package exp.bilibli.plugin.utils;

import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;

public class SafetyUtils {

	private final static long DAY_MILLIS = 86400000L;
	
	/** 授权码文件 */
	private final static String AC_PATH = "./data/ac/certificate";
	
	protected SafetyUtils() {}
	
	/**
	 * 检测软件是否在授权有效期内
	 * @return
	 */
	public static boolean timeInvalidity() {
		return (System.currentTimeMillis() < fileToCertificate());
	}
	
	/**
	 * 生成授权码到文件
	 * @param day 有效期
	 * @return 授权截止时间
	 */
	public static String certificateToFile(int day) {
		String certificate = toCertificate(day);
		FileUtils.write(AC_PATH, certificate, Charset.ISO, false);
		return certificate;
	}
	
	/**
	 * 从文件还原授权码
	 * @return 授权截止时间
	 */
	public static long fileToCertificate() {
		String certificate = FileUtils.read(AC_PATH, Charset.ISO);
		return unCertificate(certificate);
	}
	
	/**
	 * 生成从现在开始一直到day天的之后的授权码
	 * @param day 有效期
	 * @return 授权码
	 */
	public static String toCertificate(int day) {
		day = (day < 0 ? 0 : day);
		long time = System.currentTimeMillis() + DAY_MILLIS * day;
		return CryptoUtils.toDES(String.valueOf(time));
	}
	
	/**
	 * 还原授权码为授权时间
	 * @param certificate 授权码
	 * @return 授权时间
	 */
	public static long unCertificate(String certificate) {
		return NumUtils.toLong(CryptoUtils.deDES(certificate), 0);
	}
	
}
