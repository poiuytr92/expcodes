package exp.libs.utils.encode;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * <PRE>
 * Base64编解码工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Base64 {

	/** Base64编码�? */
	private final static BASE64Encoder ENCODER = new BASE64Encoder();
	
	/** Base64解码�? */
	private final static BASE64Decoder DECODER = new BASE64Decoder();
	
	/** 私有化构造函�? */
	protected Base64() {}
	
	/**
	 * Base64编码
	 * @param bytes 原始字节数据
	 * @return Base64编码字符�?
	 */
	public static String encode(byte[] bytes) {
		String base64 = "";
		try {
			base64 = ENCODER.encodeBuffer(bytes).trim();
//			base64 = base64.replaceAll("[\r\n]", "");	// 可不去掉内部换行, 不影�?
			
		} catch(Exception e) {}
		return base64;
	}
	
	/**
	 * Base64解码
	 * @param base64 Base64编码字符�?
	 * @return 原始字节数据
	 */
	public static byte[] decode(String base64) {
		byte[] bytes = new byte[0];
		try {
			bytes = DECODER.decodeBuffer(base64);
			
		} catch(Exception e) {}
		return bytes;
	}
	
}
