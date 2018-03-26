package exp.crawler.qq.utils;

import exp.libs.utils.other.JSUtils;

/**
 * <PRE>
 * 加密工具类
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class EncryptUtils {

	/** 生成GTK码的JS脚本 */
	private final static String GTK_JS_PATH = "./conf/js/GTK.js";
	
	/** 生成GTK码的JS函数 */
	private final static String GTK_METHOD = "getACSRFToken";
	
	/** RSA加密的JS脚本 */
	private final static String RSA_JS_PATH = "./conf/js/MD5-RSA.js";
	
	/** RSA加密登陆密码的JS函数 */
	private final static String RSA_METHOD = "getEncryption";
	
	/** 私有化构造函数 */
	protected EncryptUtils() {}
	
	/**
	 * 通过 skey 计算GTK码.
	 * ---------------------------------
	 * 先用 外置的JS算法 计算 GTK， 当使用 JS计算失败 时，才使用内置算法计算。
	 * 外置JS算法主要是为了在QQ更新了GTK算法情况下，可以对应灵活修改。
	 * 
	 * @param skey
	 * @return
	 */
	public static String toGTK(String skey) {
		String gtk = "";
		try {
			Double dNum = (Double) JSUtils.executeJS(GTK_JS_PATH, GTK_METHOD, skey);
			gtk = String.valueOf((int) dNum.doubleValue());
			
		} catch (Throwable e) {
			gtk = _toGTK(skey);
		}
		return gtk;
	}
	
	/**
	 * 内置GTK算法
	 * @param skey
	 * @return
	 */
	private static String _toGTK(String skey) {
		String gtk = "";
		int hash = 5381;
		for (int i = 0; i < skey.length(); ++i) {
			hash += (hash << 5) + (int) skey.charAt(i);
		}
		gtk = String.valueOf(hash & 0x7fffffff);
		return gtk;
	}
	
	/**
	 * 使用 外置的JS算法 对QQ密码做RSA加密.
	 * ---------------------------------
	 * 	QQ密码的加密逻辑过于复杂, 此处只能直接抽取QQ的JS脚本执行加密
	 * 
	 * @param QQ号 1QQ号
	 * @param password 密码明文
	 * @param vccode 验证码
	 * @return RSA加密后的QQ密码
	 */
	public static String toRSA(String QQ, String password, String vcode) {
		Object rsaPwd = JSUtils.executeJS(RSA_JS_PATH, 
				RSA_METHOD, password, QQ, vcode, "");
		return (rsaPwd == null ? "" : rsaPwd.toString());
	}
	
}
