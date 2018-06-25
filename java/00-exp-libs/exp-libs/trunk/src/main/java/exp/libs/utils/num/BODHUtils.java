package exp.libs.utils.num;

import exp.libs.utils.other.StrUtils;


/**
 * <PRE>
 * 进制转换工具.
 * B:bin
 * O:oct
 * D:dec
 * H:hex
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BODHUtils {

	/** 私有化构造函�? */
	protected BODHUtils() {}
	
	/**
	 * 将字节数组转为可视的十六进制的字符串
	 * @param bytes 字节数组
	 * @return 可视的十六进制的字符�?
	 */
	public static String toHex(byte[] bytes) {
		if(bytes == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		for(byte b : bytes) {
			sb.append(StrUtils.leftPad(Integer.toHexString(b & 0xFF), '0', 2));
		}
		return sb.toString().toUpperCase();
	}
	
	/**
	 * 将字节数组转为可视的十六进制的字符串
	 * @param bytes 字节数组
	 * @param offset 数组起始偏移索引
	 * @param len 转换字节�?
	 * @return 可视的十六进制的字符�?
	 */
	public static String toHex(byte[] bytes, int offset, int len) {
		if(bytes == null || bytes.length <= offset || len < 0) {
			return "";
		}
		
		offset = (offset < 0 ? 0 : offset);
		len = offset + len;
		len = (len > bytes.length ? bytes.length : len);
		
		StringBuilder sb = new StringBuilder();
		for(int i = offset; i < len; i++) {
			byte b = bytes[i];
			sb.append(StrUtils.leftPad(Integer.toHexString(b & 0xFF), '0', 2));
		}
		return sb.toString().toUpperCase();
	}
	
	/**
	 * 把可视的十六进制字符串还原为字节数组
	 * @param hex 可视的十六进制字符串
	 * @return 字节数组
	 */
	public static byte[] toBytes(String hex) {
		byte[] bytes = {};
		if(StrUtils.isTrimEmpty(hex)) {
			return bytes;
		}
		
		hex = hex.trim().toUpperCase();
		if (!hex.matches("[0-9A-F]+")) {
			return bytes;
		}
		
		if(hex.length() % 2 == 1) {
			hex = StrUtils.concat("0", hex);
		}
		
		int len = hex.length() / 2;
		bytes = new byte[len];
		
		char[] chars = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int j = i * 2;
			byte high = (byte) (toByte(chars[j]) << 4);
			byte low = toByte(chars[j + 1]);
			bytes[i] = (byte) (high | low);
		}
		return bytes;
	}
	
	/**
	 * �?0-F�?16进制字符转换成对应的byte
	 * @param hex 0-F�?16进制字符
	 * @return 对应的byte
	 */
	private static byte toByte(char hex) {
		int n = 0;
		switch(hex) {
			case 'a': case 'A': { n = 10; break; }
			case 'b': case 'B': { n = 11; break; }
			case 'c': case 'C': { n = 12; break; }
			case 'd': case 'D': { n = 13; break; }
			case 'e': case 'E': { n = 14; break; }
			case 'f': case 'F': { n = 15; break; }
			default: { n = (hex - '0'); break; }
		}
		return ((byte) n);
	}
	
	/**
	 * 二进制数字串 -> 八进制数字串
	 * @param bin 二进制数字串
	 * @return 八进制数字串
	 */
	public static String binToOct(String bin) {
		return decToOct(binToDec(bin));
	}
	
	/**
	 * 二进制数字串 -> 十进制数
	 * @param bin 二进制数
	 * @return 十进制数字串
	 */
	public static long binToDec(String bin) {
		long dec = 0L;
		if(bin != null) {
			char[] binarys = StrUtils.reverse(bin.trim()).toCharArray();
			dec += ('0' == binarys[0] ? 0 : 1);
			
			for(int i = 1; i < binarys.length; i++) {
				if('0' != binarys[i]) {
					dec += (2 << (i - 1));
				}
			}
		}
		return dec;
	}
	
	/**
	 * 二进制数字串 -> 十六进制数字�?
	 * @param bin 二进制数字串
	 * @return 十六进制数字�?
	 */
	public static String binToHex(String bin) {
		return decToHex(binToDec(bin));
	}
	
	/**
	 * 八进制数字串 -> 二进制数字串
	 * @param oct 八进制数字串
	 * @return 二进制数字串
	 */
	public static String octToBin(String oct) {
		StringBuilder bin = new StringBuilder();
		if(oct != null) {
			char[] binarys = oct.trim().toCharArray();
			for(char c : binarys) {
				switch (c) {
					case '1' : { bin.append("001"); break; }
					case '2' : { bin.append("010"); break; }
					case '3' : { bin.append("011"); break; }
					case '4' : { bin.append("100"); break; }
					case '5' : { bin.append("101"); break; }
					case '6' : { bin.append("110"); break; }
					case '7' : { bin.append("111"); break; }
					default  : { bin.append("000"); break; }
				}
			}
		}
		return bin.toString();
	}
	
	/**
	 * 八进制数字串 -> 十进制数
	 * @param oct 八进制数字串
	 * @return 十进制数
	 */
	public static long octToDec(String oct) {
		return binToDec(octToBin(oct));
	}
	
	/**
	 * 八进制数字串 -> 十六进制数字�?
	 * @param oct 八进制数字串
	 * @return 十六进制数字�?
	 */
	public static String octToHex(String oct) {
		return binToHex(octToBin(oct));
	}
	
	/**
	 * 十进制数 -> 二进制数字串
	 * @param dec 十进制数
	 * @return 二进制数字串
	 */
	public static String decToBin(long dec) {
		return Long.toBinaryString(dec);
	}
	
	/**
	 * 十进制数 -> 八进制数字串
	 * @param dec 十进制数
	 * @return 八进制数字串
	 */
	public static String decToOct(long dec) {
		return Long.toOctalString(dec);
	}
	
	/**
	 * 十进制数 -> 十六进制数字�?
	 * @param dec 十进制数
	 * @return 十六进制数字�?
	 */
	public static String decToHex(long dec) {
		return Long.toHexString(dec).toUpperCase();
	}
	
	/**
	 * 十进制数字串 -> 二进制数字串
	 * @param dec 十进制数字串
	 * @return 二进制数字串
	 */
	public static String decToBin(String dec) {
		return Long.toBinaryString(NumUtils.toLong(dec));
	}
	
	/**
	 * 十进制数字串 -> 八进制数字串
	 * @param dec 十进制数字串
	 * @return 八进制数字串
	 */
	public static String decToOct(String dec) {
		return Long.toOctalString(NumUtils.toLong(dec));
	}
	
	/**
	 * 十进制数字串 -> 十六进制数字�?
	 * @param dec 十进制数字串
	 * @return 十六进制数字�?
	 */
	public static String decToHex(String dec) {
		return Long.toHexString(NumUtils.toLong(dec)).toUpperCase();
	}
	
	/**
	 * 十六进制数字�? -> 二进制数字串
	 * @param dec 十六进制数字�?
	 * @return 二进制数字串
	 */
	public static String hexToBin(String hex) {
		StringBuilder bin = new StringBuilder();
		if(hex != null) {
			hex = hex.trim().toLowerCase();
			char[] binarys = hex.replace("0x", "").toCharArray();
			for(char c : binarys) {
				switch (c) {
					case '1' : { bin.append("0001"); break; }
					case '2' : { bin.append("0010"); break; }
					case '3' : { bin.append("0011"); break; }
					case '4' : { bin.append("0100"); break; }
					case '5' : { bin.append("0101"); break; }
					case '6' : { bin.append("0110"); break; }
					case '7' : { bin.append("0111"); break; }
					case '8' : { bin.append("1000"); break; }
					case '9' : { bin.append("1001"); break; }
					case 'a' : { bin.append("1010"); break; }
					case 'b' : { bin.append("1011"); break; }
					case 'c' : { bin.append("1100"); break; }
					case 'd' : { bin.append("1101"); break; }
					case 'e' : { bin.append("1110"); break; }
					case 'f' : { bin.append("1111"); break; }
					default  : { bin.append("0000"); break; }
				}
			}
		}
		return bin.toString();
	}
	
	/**
	 * 十六进制数字�? -> 八进制数字串
	 * @param dec 十六进制数字�?
	 * @return 八进制数字串
	 */
	public static String hexToOct(String hex) {
		return decToOct(hexToDec(hex));
	}
	
	/**
	 * 十六进制数字�? -> 十进制数字串
	 * @param dec 十六进制数字�?
	 * @return 十进制数字串
	 */
	public static long hexToDec(String hex) {
		return binToDec(hexToBin(hex));
	}
	
}
