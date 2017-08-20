package exp.libs.utils.encode;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.Regex;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 加解密工具
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CryptoUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(CryptoUtils.class);
	
	/**
	 * DES共有四种工作模式.
	 * 	ECB：电子密码本模式
	 * 	CBC：加密分组链接模式
	 * 	CFB：加密反馈模式
	 * 	OFB：输出反馈模式
	 */
	private static final String CIPHER_MODE = "DES/ECB/NOPADDING";
	
	/** 加密算法: DES */
	public static final String ALGORITHM_DES = "DES";
	
	/** 摘要算法: DES */
	public static final String ALGORITHM_MD5 = "MD5";
	
	/** 默认密钥 */
	public final static String DEFAULT_KEY = "exp-libs";
	
	/** 默认加密编码 */
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/** 私有化构造函数 */
	protected CryptoUtils() {}
	
	/**
	 * 计算字符串的32位MD5(默认编码为UTF-8)
	 * @param data 待加密的字符串
	 * @return 32位MD5
	 */
	public static String toMD5(String data) {
		return toMD5(data, DEFAULT_CHARSET);
	}
	
	/**
	 * 计算字符串的32位MD5
	 * @param data 待加密的字符串
	 * @param charset 字符串编码
	 * @return 32位MD5
	 */
	public static String toMD5(String data, String charset) {
		byte[] md5 = toMD5Byte(CharsetUtils.toBytes(data, charset));
		String sMD5 = BODHUtils.toHex(md5);
		return sMD5;
	}
	
	/**
	 * 拼接多个字符串生成32位MD5(默认编码为UTF-8)
	 * @param datalist 待加密的字符串列表
	 * @return 32位MD5
	 */
	public static String toMD5(String[] datalist) {
		return toMD5(datalist, DEFAULT_CHARSET);
	}
	
	/**
	 * 拼接多个字符串生成32位MD5
	 * @param strlist 待加密的字符串列表
	 * @param datalist 字符串编码
	 * @return 32位MD5
	 */
	public static String toMD5(String[] datalist, String charset) {
		String data = StrUtils.concat(datalist);
		return toMD5(data, charset);
	}
	
	/**
	 * 计算字节数组的MD5值
	 * @param data 待加密的字节数组
	 * @return MD5的字节数组
	 */
	private static byte[] toMD5Byte(byte[] data) {
		byte[] md5 = {};
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
			md.update(data);
			md5 = md.digest();
			
		} catch (Exception e) {
			log.error("计算MD5失败.", e);
		}
		return md5;
	}
	
	/**
	 * 根据32位MD5获取对应的16位MD5
	 *	(实则32位MD5中的第8到24位)
	 * @param _32MD5 32位MD5
	 * @return 16位MD5
	 */
	public static String to16MD5(String _32MD5) {
		_32MD5 = (_32MD5 == null ? "" : _32MD5.trim());
		if(_32MD5.length() != 32 || !_32MD5.matches(Regex.MD5.VAL)) {
			_32MD5 = toMD5(_32MD5);
		}
		String _16MD5 = _32MD5.substring(8, 24);
		return _16MD5;
	}
	
	/**
	 * 使用默认密钥对数据进行DES加密（加密编码为UTF-8）
	 * @param data 被加密数据
	 * @return DES加密后的16进制字符串
	 */
	public static String toDES(String data) {
		return encrypt(CharsetUtils.toBytes(data, DEFAULT_CHARSET), 
				CharsetUtils.toBytes(DEFAULT_KEY, DEFAULT_CHARSET));
	}

	/**
	 * 对数据进行DES加密（加密编码为UTF-8）
	 * @param data 被加密数据
	 * @param key 密钥
	 * @return DES加密后的16进制字符串
	 */
	public static String toDES(String data, String key) {
		return encrypt(CharsetUtils.toBytes(data, DEFAULT_CHARSET), 
				CharsetUtils.toBytes(key, DEFAULT_CHARSET));
	}
	
	/**
	 * 对数据进行DES加密
	 * @param data 被加密数据
	 * @param key 密钥
	 * @param charset 加密编码
	 * @return DES加密后的16进制字符串
	 */
	public static String toDES(String data, String key, String charset) {
		return encrypt(CharsetUtils.toBytes(data, charset), 
				CharsetUtils.toBytes(key, charset));
	}
	
	private static String encrypt(byte[] data, byte[] key) {
		String eData = "";
		if(data == null || data.length <= 0) {
			return eData;
		}
		
		int m = data.length / 8;
		int n = data.length % 8;
		
		// 对被加密的数据的前 m*8位 加密
		if (m > 0) {
			int len = m * 8;	// 必须是8的倍数
			byte[] bytes = new byte[len]; 
			for (int i = 0; i < len; i++) {
				bytes[i] = data[i];
			}
			eData = BODHUtils.toHex(_encrypt(bytes, key, ALGORITHM_DES));
		}
		
		// 被加密的数据长度不为8的倍数，右补0后加密
		if (n != 0) {
			byte[] bytes = new byte[8];
			int i = 0;
			while (i < n) {
				bytes[i] = data[m * 8 + i];
				i++;
			}
			while (i < 8) {
				bytes[i++] = 0;
			}
			eData = StrUtils.concat(eData, 
					BODHUtils.toHex(_encrypt(bytes, key, ALGORITHM_DES)));
		}
		return eData;
	}
	
	private static byte[] _encrypt(byte[] data, byte[] key, String algorithm) {
		byte[] eData = {};
		key = zeroize(key);
		try {
			SecureRandom sr = new SecureRandom();
			KeySpec ks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey securekey = keyFactory.generateSecret(ks);
			
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
			eData = cipher.doFinal(data);
			
		} catch (Exception e) {
			log.error("执行 [{}加密] 失败.", algorithm, e);
		}
		return eData;
	}
	
	/**
	 * 使用默认密钥对DES加密串解码（解密编码为UTF-8）
	 * @param des 16进制加密串
	 * @return 加密前的数据
	 */
	public static String deDES(String des) {
		return decrypt(BODHUtils.toBytes(des), 
				CharsetUtils.toBytes(DEFAULT_KEY, DEFAULT_CHARSET), 
				DEFAULT_CHARSET);
	}
	
	/**
	 * 对DES加密串解码（解密编码为UTF-8）
	 * @param des 16进制加密串
	 * @param key 密钥
	 * @return 加密前的数据
	 */
	public static String deDES(String des, String key) {
		return decrypt(BODHUtils.toBytes(des), 
				CharsetUtils.toBytes(key, DEFAULT_CHARSET), DEFAULT_CHARSET);
	}
	
	/**
	 * 对DES加密串解码
	 * @param des 16进制加密串
	 * @param key 密钥
	 * @param charset 解密编码
	 * @return 加密前的数据
	 */
	public static String deDES(String des, String key, String charset) {
		return decrypt(BODHUtils.toBytes(des), 
				CharsetUtils.toBytes(key, charset), charset);
	}
	
	private static String decrypt(byte[] des, byte[] key, String charset) {
		String dData = "";
		if(des == null || des.length <= 0) {
			return dData;
		}
		
		byte[] buf = _decrypt(des, key, ALGORITHM_DES);
		try {
			int i = 0;
			while ((i < buf.length) && 
					(buf[buf.length - 1 - i] == 0)) {  i++; }	// 去掉末尾0
			dData = new String(buf, 0, buf.length - i, charset);	// 解密
			
		} catch (Exception e) {
			log.error("执行 [{}解密] 失败.", ALGORITHM_DES, e);
		}
		return dData;
	}
	
	private static byte[] _decrypt(byte[] des, byte[] key, String algorithm) {
		byte[] dData = {};
		key = zeroize(key);
		try {
			SecureRandom sr = new SecureRandom();
			KeySpec ks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey securekey = keyFactory.generateSecret(ks);
			
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
			dData = cipher.doFinal(des);
			
		} catch (Exception e) {
			log.error("执行 [{}解密] 失败.", algorithm, e);
		}
		return dData;
	}
	
	/**
	 * 对长度不足8位的密钥补零
	 * @param key
	 * @return
	 */
	private static byte[] zeroize(byte[] key) {
		byte[] zKey = key; 
		if(key == null || key.length < 8) {
			zKey = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
			for(int i = 0; i < key.length; i++) {
				zKey[i] = key[i];
			}
		}
		return zKey;
	}

}
