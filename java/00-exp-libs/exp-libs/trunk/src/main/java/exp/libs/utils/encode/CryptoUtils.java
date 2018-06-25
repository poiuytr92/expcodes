package exp.libs.utils.encode;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.Regex;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.num.UnitUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 加解密工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CryptoUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(CryptoUtils.class);
	
	/**
	 * DES共有四种工作模式.
	 * 	ECB：电子密码本模式
	 * 	CBC：加密分组链接模�?
	 * 	CFB：加密反馈模�?
	 * 	OFB：输出反馈模�?
	 */
	private final static String CIPHER_MODE = "DES/ECB/NOPADDING";
	
	/** 加密算法: DES */
	public static final String ALGORITHM_DES = "DES";
	
	/** 摘要算法: MD5 */
	public static final String ALGORITHM_MD5 = "MD5";
	
	/** 签名算法 */
	public static final String ALGORITHMS_SIGN = "SHA1WithRSA";
	
	/** 加密算法: RSA */
	public static final String ALGORITHM_RSA = "RSA";
	
	/** RSA公钥�? */
	private final static String RSA_PUBLIC_KEY_BGN = "-----BEGIN PUBLIC KEY-----\n";
	
	/** RSA公钥�? */
	private final static String RSA_PUBLIC_KEY_END = "\n-----END PUBLIC KEY-----\n";
	
	/** RSA私钥�? */
	private final static String RSA_PRIVATE_KEY_BGN = "-----BEGIN RSA PRIVATE KEY-----\n";
	
	/** RSA私钥�? */
	private final static String RSA_PRIVATE_KEY_END = "\n-----END RSA PRIVATE KEY-----\n";
	
	/** 默认密钥 */
	public final static String DEFAULT_KEY = "exp-libs";
	
	/** 默认加密编码 */
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/** 私有化构造函�? */
	protected CryptoUtils() {}
	
	/**
	 * 计算字符串的32位MD5(默认编码为UTF-8)
	 * @param data 待加密的字符�?
	 * @return 32位MD5
	 */
	public static String toMD5(String data) {
		return toMD5(data, DEFAULT_CHARSET);
	}
	
	/**
	 * 计算字符串的32位MD5
	 * @param data 待加密的字符�?
	 * @param charset 字符串编�?
	 * @return 32位MD5
	 */
	public static String toMD5(String data, String charset) {
		byte[] md5 = toMD5Byte(CharsetUtils.toBytes(data, charset));
		String sMD5 = BODHUtils.toHex(md5);
		return sMD5;
	}
	
	/**
	 * 拼接多个字符串生�?32位MD5(默认编码为UTF-8)
	 * @param datalist 待加密的字符串列�?
	 * @return 32位MD5
	 */
	public static String toMD5(String[] datalist) {
		return toMD5(datalist, DEFAULT_CHARSET);
	}
	
	/**
	 * 拼接多个字符串生�?32位MD5
	 * @param strlist 待加密的字符串列�?
	 * @param datalist 字符串编�?
	 * @return 32位MD5
	 */
	public static String toMD5(String[] datalist, String charset) {
		String data = StrUtils.concat(datalist);
		return toMD5(data, charset);
	}
	
	/**
	 * 计算字节数组的MD5�?
	 * @param data 待加密的字节数组
	 * @return MD5的字节数�?
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
	 * 生成文件MD5
	 * @param filePath 文件路径
	 * @return 文件MD5
	 */
	public static String toFileMD5(String filePath) {
		return toFileMD5(new File(filePath));
	}
	
	/**
	 * 生成文件MD5.
	 * ------------------------------------
	 * 	�?:
	 * 	  DigestUtils.md5Hex 的作用与此方法效果是一样的, 
	 *    但是 DigestUtils.md5Hex 有个问题: 在生成大文件的MD5时，前面会多一�?0
	 * 
	 * @param file 文件对象
	 * @return 文件MD5
	 */
	public static String toFileMD5(File file) {
		String MD5 = "";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
			
			// 分片读取文件（确保可以计算大文件的MD5�?
			byte[] buffer = new byte[UnitUtils._1_MB];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			byte[] bytes = md.digest();
			BigInteger bi = new BigInteger(1, bytes);
			MD5 = bi.toString(16).toUpperCase();	// 16表示生成16进制形式的字符串
			
		} catch (Exception e) {
			log.error("生成文件 [{}] 的MD5失败.", 
					(file == null ? "null" : file.getAbsolutePath()), e);
			
		} finally {
			IOUtils.close(fis);
		}
		return MD5;
	}
	
	/**
	 * 根据32位MD5获取对应�?16位MD5
	 *	(实则32位MD5中的�?8�?24�?)
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
	 * 使用默认密钥对数据进行DES加密（加密编码为UTF-8�?
	 * @param data 被加密数�?
	 * @return DES加密后的16进制字符�?
	 */
	public static String toDES(String data) {
		return encrypt(CharsetUtils.toBytes(data, DEFAULT_CHARSET), 
				CharsetUtils.toBytes(DEFAULT_KEY, DEFAULT_CHARSET));
	}

	/**
	 * 对数据进行DES加密（加密编码为UTF-8�?
	 * @param data 被加密数�?
	 * @param key 密钥
	 * @return DES加密后的16进制字符�?
	 */
	public static String toDES(String data, String key) {
		return encrypt(CharsetUtils.toBytes(data, DEFAULT_CHARSET), 
				CharsetUtils.toBytes(key, DEFAULT_CHARSET));
	}
	
	/**
	 * 对数据进行DES加密
	 * @param data 被加密数�?
	 * @param key 密钥
	 * @param charset 加密编码
	 * @return DES加密后的16进制字符�?
	 */
	public static String toDES(String data, String key, String charset) {
		return encrypt(CharsetUtils.toBytes(data, charset), 
				CharsetUtils.toBytes(key, charset));
	}
	
	/**
	 * DES加密过程
	 * @param data
	 * @param key
	 * @return
	 */
	private static String encrypt(byte[] data, byte[] key) {
		String eData = "";
		if(data == null || data.length <= 0) {
			return eData;
		}
		
		int m = data.length / 8;
		int n = data.length % 8;
		
		// 对被加密的数据的�? m*8�? 加密
		if (m > 0) {
			int len = m * 8;	// 必须�?8的倍数
			byte[] bytes = new byte[len]; 
			for (int i = 0; i < len; i++) {
				bytes[i] = data[i];
			}
			eData = BODHUtils.toHex(_encrypt(bytes, key));
		}
		
		// 被加密的数据长度不为8的倍数，右�?0后加�?
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
					BODHUtils.toHex(_encrypt(bytes, key)));
		}
		return eData;
	}
	
	/**
	 * DES加密过程
	 * @param data
	 * @param key
	 * @return
	 */
	private static byte[] _encrypt(byte[] data, byte[] key) {
		byte[] bytes = {};
		key = zeroize(key);
		try {
			SecureRandom sr = new SecureRandom();
			KeySpec ks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
			SecretKey securekey = keyFactory.generateSecret(ks);
			
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
			bytes = cipher.doFinal(data);
			
		} catch (Exception e) {
			log.error("执行 [{}加密] 失败.", ALGORITHM_DES, e);
		}
		return bytes;
	}
	
	/**
	 * 使用默认密钥对DES加密串解码（解密编码为UTF-8�?
	 * @param des 16进制加密�?
	 * @return 加密前的数据
	 */
	public static String deDES(String des) {
		return decrypt(BODHUtils.toBytes(des), 
				CharsetUtils.toBytes(DEFAULT_KEY, DEFAULT_CHARSET), 
				DEFAULT_CHARSET);
	}
	
	/**
	 * 对DES加密串解码（解密编码为UTF-8�?
	 * @param des 16进制加密�?
	 * @param key 密钥
	 * @return 加密前的数据
	 */
	public static String deDES(String des, String key) {
		return decrypt(BODHUtils.toBytes(des), 
				CharsetUtils.toBytes(key, DEFAULT_CHARSET), DEFAULT_CHARSET);
	}
	
	/**
	 * 对DES加密串解�?
	 * @param des 16进制加密�?
	 * @param key 密钥
	 * @param charset 解密编码
	 * @return 加密前的数据
	 */
	public static String deDES(String des, String key, String charset) {
		return decrypt(BODHUtils.toBytes(des), 
				CharsetUtils.toBytes(key, charset), charset);
	}
	
	/**
	 * DES解密过程
	 * @param des
	 * @param key
	 * @param charset
	 * @return
	 */
	private static String decrypt(byte[] des, byte[] key, String charset) {
		String data = "";
		if(des == null || des.length <= 0) {
			return data;
		}
		
		byte[] buf = _decrypt(des, key);
		try {
			int i = 0;
			while ((i < buf.length) && 
					(buf[buf.length - 1 - i] == 0)) {  i++; }	// 去掉末尾0
			data = new String(buf, 0, buf.length - i, charset);	// 解密
			
		} catch (Exception e) {
			log.error("执行 [{}解密] 失败.", ALGORITHM_DES, e);
		}
		return data;
	}
	
	/**
	 * DES解密过程
	 * @param des
	 * @param key
	 * @return
	 */
	private static byte[] _decrypt(byte[] des, byte[] key) {
		byte[] bytes = {};
		key = zeroize(key);
		try {
			SecureRandom sr = new SecureRandom();
			KeySpec ks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
			SecretKey securekey = keyFactory.generateSecret(ks);
			
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
			bytes = cipher.doFinal(des);
			
		} catch (Exception e) {
			log.error("执行 [{}解密] 失败.", ALGORITHM_DES, e);
		}
		return bytes;
	}
	
	/**
	 * 对长度不�?8位的密钥补零
	 * @param key
	 * @return
	 */
	private static byte[] zeroize(byte[] key) {
		key = (key == null ? new byte[0] : key);
		byte[] zKey = key;
		if(key.length < 8) {
			zKey = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
			for(int i = 0; i < key.length; i++) {
				zKey[i] = key[i];
			}
		}
		return zKey;
	}
	
	/**
	 * 对数据进行RSA公钥加密
	 * @param data 被加密数�?
	 * @param publicKey 公钥, 形如(可无首尾�?): -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 * @return RSA加密后的字符�?
	 */
	public static String toRSAByPubKey(String data, String publicKey) {
		return toRSAByPubKey(data, publicKey, DEFAULT_CHARSET);
	}
	
	/**
	 * 对数据进行RSA公钥加密
	 * @param data 被加密数�?
	 * @param publicKey 公钥, 形如(可无首尾�?): -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 * @param charset 加密数据编码
	 * @return RSA加密后的字符�?
	 */
	public static String toRSAByPubKey(String data, String publicKey, String charset) {
		byte[] plainBytes = CharsetUtils.toBytes(data, charset);
		RSAPublicKey rsaPK = toRSAPublicKey(publicKey);
		
		byte[] bytes = encrypt(plainBytes, rsaPK);	// RSA加密
		return Base64.encode(bytes);	// Base64编码
	}
	
	/**
	 * RSA公钥加密过程
	 * @param plainBytes 明文字节数据
	 * @param publicKey RSA公钥
	 * @return 密文字节数据
	 */
	private static byte[] encrypt(byte[] plainBytes, RSAPublicKey publicKey) {
		byte[] bytes = new byte[0];
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			bytes = cipher.doFinal(plainBytes);
			
		} catch(Exception e) {
			log.error("执行 [{}公钥加密] 失败.", ALGORITHM_RSA, e);
		} 
		return bytes;
	}
	
	/**
	 * 对数据进行RSA私钥加密
	 * @param data 被加密数�?
	 * @param privateKey 私钥, 形如(可无首尾�?): -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 * @return RSA加密后的字符�?
	 */
	public static String toRSAByPriKey(String data, String privateKey) {
		return toRSAByPriKey(data, privateKey, DEFAULT_CHARSET);
	}
	
	/**
	 * 对数据进行RSA私钥加密
	 * @param data 被加密数�?
	 * @param privateKey 私钥, 形如(可无首尾�?): -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 * @param charset 加密数据编码
	 * @return RSA加密后的字符�?
	 */
	public static String toRSAByPriKey(String data, String privateKey, String charset) {
		byte[] plainBytes = CharsetUtils.toBytes(data, charset);
		RSAPrivateKey rsaPK = toRSAPrivateKey(privateKey);
		
		byte[] bytes = encrypt(plainBytes, rsaPK);	// RSA加密
		return Base64.encode(bytes);	// Base64编码
	}
	
	/**
	 * RSA私钥加密过程
	 * @param plainBytes 明文字节数据
	 * @param privateKey RSA私钥
	 * @return 密文字节数据
	 */
	private static byte[] encrypt(byte[] plainBytes, RSAPrivateKey privateKey) {
		byte[] bytes = new byte[0];
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			bytes = cipher.doFinal(plainBytes);
			
		} catch(Exception e) {
			log.error("执行 [{}私钥加密] 失败.", ALGORITHM_RSA, e);
		} 
		return bytes;
	}
	
	/**
	 * �? [RSA私钥加密数据] 进行 [RSA公钥解密]
	 * @param rsa RSA私钥加密数据
	 * @param publicKey 公钥, 形如(可无首尾�?): -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 * @return 加密前的明文数据
	 */
	public static String deRSAByPubKey(String rsa, String publicKey) {
		return deRSAByPubKey(rsa, publicKey, DEFAULT_CHARSET);
	}
	
	/**
	 * �? [RSA私钥加密数据] 进行 [RSA公钥解密]
	 * @param rsa RSA私钥加密数据
	 * @param publicKey 公钥, 形如(可无首尾�?): -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 * @param charset 解密编码
	 * @return 加密前的明文数据
	 */
	public static String deRSAByPubKey(String rsa, String publicKey, String charset) {
		byte[] cipherBytes = Base64.decode(rsa);
		RSAPublicKey rsaPK = toRSAPublicKey(publicKey);
		
		byte[] bytes = decrypt(cipherBytes, rsaPK);
		return CharsetUtils.toStr(bytes, charset);
	}
	
	/**
	 * RSA公钥解密过程
	 * @param cipherBytes 密文字节数据
	 * @param publicKey RSA公钥
	 * @return 明文字节数据
	 */
	private static byte[] decrypt(byte[] cipherBytes, RSAPublicKey publicKey) {
		byte[] bytes = new byte[0];
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			bytes = cipher.doFinal(cipherBytes);
			
		} catch(Exception e) {
			log.error("执行 [{}公钥解密] 失败.", ALGORITHM_RSA, e);
		} 
		return bytes;
	}
	
	/**
	 * �? [RSA公钥加密数据] 进行 [RSA私钥解密]
	 * @param rsa RSA公钥加密数据
	 * @param privateKey 私钥, 形如(可无首尾�?): -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 * @return 加密前的明文数据
	 */
	public static String deRSAByPriKey(String rsa, String privateKey) {
		return deRSAByPriKey(rsa, privateKey, DEFAULT_CHARSET);
	}
	
	/**
	 * �? [RSA公钥加密数据] 进行 [RSA私钥解密]
	 * @param rsa RSA公钥加密数据
	 * @param privateKey 私钥, 形如(可无首尾�?): -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 * @param charset 解密编码
	 * @return 加密前的明文数据
	 */
	public static String deRSAByPriKey(String rsa, String privateKey, String charset) {
		byte[] cipherBytes = Base64.decode(rsa);
		RSAPrivateKey rsaPK = toRSAPrivateKey(privateKey);
		
		byte[] bytes = decrypt(cipherBytes, rsaPK);
		return CharsetUtils.toStr(bytes, charset);
	}
	
	/**
	 * RSA私钥解密过程
	 * @param cipherBytes 密文字节数据
	 * @param privateKey RSA私钥
	 * @return 明文字节数据
	 */
	private static byte[] decrypt(byte[] cipherBytes, RSAPrivateKey privateKey) {
		byte[] bytes = new byte[0];
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			bytes = cipher.doFinal(cipherBytes);
			
		} catch(Exception e) {
			log.error("执行 [{}私钥解密] 失败.", ALGORITHM_RSA, e);
		} 
		return bytes;
	}
	
	/**
	 * 对数据进行RSA私钥签名
	 * @param data 未被签名的原始数�?
	 * @param privateKey 私钥, 形如(可无首尾�?): -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 * @return RSA签名后的字符�?
	 */
	public static String doRSASign(String data, String privateKey) {
		return doRSASign(data, privateKey, DEFAULT_CHARSET);
	}
	
	/**
	 * 对数据进行RSA私钥签名
	 * @param data 未被签名的原始数�?
	 * @param privateKey 私钥, 形如(可无首尾�?): -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 * @param charset 签名数据编码
	 * @return RSA签名后的字符�?
	 */
	public static String doRSASign(String data, String privateKey, String charset) {
		byte[] bytes = CharsetUtils.toBytes(data, charset);
		RSAPrivateKey rsaPK = toRSAPrivateKey(privateKey);
		
		byte[] signed = sign(bytes, rsaPK);	// RSA签名
		return Base64.encode(signed);	// Base64编码
	}
	
	/**
	 * RSA私钥签名过程
	 * @param bytes 未被签名的原始字节数�?
	 * @param privateKey RSA私钥
	 * @return 签名后的字节数据
	 */
	private static byte[] sign(byte[] bytes, RSAPrivateKey privateKey) {
		byte[] signed = new byte[0];
		try {
			Signature signature = Signature.getInstance(ALGORITHMS_SIGN);
			signature.initSign(privateKey);
			signature.update(bytes);
			signed = signature.sign();

		} catch (Exception e) {
			log.error("执行 [{}私钥签名] 失败.", ALGORITHMS_SIGN, e);
		}
		return signed;
	}

	/**
	 * �? [RSA私钥签名数据] 进行 [RSA公钥校验]
	 * @param data 未被签名的原始数�?
	 * @param rsaSigned 已被RSA私钥签名数据
	 * @param publicKey 公钥, 形如(可无首尾�?): -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 * @return true:通过签名校验; false:不通过签名校验
	 */
	public static boolean checkRSASign(String data, String rsaSigned, String publicKey) {
		return checkRSASign(data, rsaSigned, publicKey, DEFAULT_CHARSET);
	}
	
	/**
	 * �? [RSA私钥签名数据] 进行 [RSA公钥校验]
	 * @param data 未被签名的原始数�?
	 * @param rsaSigned 已被RSA私钥签名数据
	 * @param publicKey 公钥, 形如(可无首尾�?): -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 * @param charset 解密编码
	 * @return true:通过签名校验; false:不通过签名校验
	 */
	public static boolean checkRSASign(String data, String rsaSigned, 
			String publicKey, String charset) {
		byte[] bytes = CharsetUtils.toBytes(data, charset);
		byte[] signed = Base64.decode(rsaSigned);
		RSAPublicKey rsaPK = toRSAPublicKey(publicKey);
		return checkSign(bytes, signed, rsaPK);
	}
	
	/**
	 * RSA公钥校验签名过程
	 * @param bytes 未被签名的原始字节数�?
	 * @param signed 已被RSA私钥签名的字节数�?
	 * @param publicKey RSA公钥
	 * @return true:通过签名校验; false:不通过签名校验
	 */
	private static boolean checkSign(
			byte[] bytes, byte[] signed, RSAPublicKey publicKey) {
		boolean isOk = false;
		try {
			Signature signature = Signature.getInstance(ALGORITHMS_SIGN);
			signature.initVerify(publicKey);
			signature.update(bytes);
			isOk = signature.verify(signed);

		} catch (Exception e) {
			log.error("执行 [{}公钥签名校验] 失败.", ALGORITHMS_SIGN, e);
		}
		return isOk;
	}
	
	/**
	 * 随机生成RSA密钥对（公钥+私钥�?
	 * 
	 * @return 若失败返回null, 否则返回密钥对：
	 * 		Key[2]: { RSAPublicKey, RSAPrivateKey }
	 * 		RSAPublicKey publicKey = (RSAPublicKey) key[0];
	 * 		RSAPrivateKey privateKey = (RSAPrivateKey) key[1];
	 */
	public static Key[] getRSAKeyPair() {
		Key[] rsaKeys = new Key[2];
		try {
			// 基于RSA算法初始化密钥对生成�?, 密钥大小�?96-1024�?
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
			keyPairGen.initialize(1024, new SecureRandom());	
			
			// 生成密钥�?
			KeyPair keyPair = keyPairGen.generateKeyPair();
			rsaKeys[0] = keyPair.getPublic();
			rsaKeys[1] = keyPair.getPrivate();
			
		} catch (Exception e) {
			rsaKeys = null;
			log.error("随机生成 [{}密钥对] 失败.", ALGORITHM_RSA, e);
		}
		return rsaKeys;
	}
	
	/**
	 * 随机生成RSA密钥对字符串（公�?+私钥�?
	 * 
	 * @return 若失败则公私钥均为空�?"", 否则返回密钥对：
	 * 		String[2]: { "RSAPublicKey", "RSAPrivateKey" }
	 * 		String rsaPublicKey = key[0];
	 * 		String rsaPrivateKey = key[1];
	 */
	public static String[] getRSAKeyStrPair() {
		String[] strKeys = { "", "" };
		Key[] rsaKeys = getRSAKeyPair();
		if(rsaKeys != null && rsaKeys.length == strKeys.length) {
			RSAPublicKey rsaPublicKey = (RSAPublicKey) rsaKeys[0];
			strKeys[0] = Base64.encode(rsaPublicKey.getEncoded());
			
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) rsaKeys[1];
			strKeys[1] = Base64.encode(rsaPrivateKey.getEncoded());
		}
		return strKeys;
	}
	
	/**
	 * 通过公钥字符串生成RSA公钥
	 * @param publicKey 公钥数据字符�?, 形如(可无首尾�?): -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 * @return RSA公钥
	 */
	public static RSAPublicKey toRSAPublicKey(String publicKey)  {
		RSAPublicKey rsaPublicKey = null;
		try {
			publicKey = publicKey.replace(RSA_PUBLIC_KEY_BGN, "").
					replace(RSA_PUBLIC_KEY_END, "");
			byte[] buffer = Base64.decode(publicKey);
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
			
		} catch(Exception e) {
			log.error("生成 [{}公钥] 失败.", ALGORITHM_RSA, e);
		} 
		return rsaPublicKey;
	}
	
	/**
	 * 通过私钥字符串生成RSA私钥
	 * @param privateKey 私钥数据字符�?, 形如(可无首尾�?): -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 * @return RSA私钥
	 */
	public static RSAPrivateKey toRSAPrivateKey(String privateKey)  {
		RSAPrivateKey rsaPrivateKey = null;
		try {
			privateKey = privateKey.replace(RSA_PRIVATE_KEY_BGN, "").
					replace(RSA_PRIVATE_KEY_END, "");
			byte[] buffer = Base64.decode(privateKey);
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			
		} catch(Exception e) {
			log.error("生成 [{}私钥] 失败.", ALGORITHM_RSA, e);
		} 
		return rsaPrivateKey;
	}
	
	/**
	 * 把RSA公钥对象转换成字符串
	 * @param rsaPublicKey RSA公钥对象
	 * @return 公钥数据字符�?, 形如: MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB
	 */
	public static String toRSAPublicKey(RSAPublicKey rsaPublicKey) {
		return toRSAPublicKey(rsaPublicKey, false);
	}
	
	/**
	 * 把RSA公钥对象转换成字符串
	 * @param rsaPublicKey RSA公钥对象
	 * @param appendHeadTail 添加密钥首尾标识: "-----BEGIN PUBLIC KEY-----\n" �? "\n-----END PUBLIC KEY-----\n"
	 * @return 公钥数据字符�?, 形如: -----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08J\nbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98\n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHr\nNDjdNcaefJIQHMW+sQIDAQAB\n-----END PUBLIC KEY-----\n
	 */
	public static String toRSAPublicKey(
			RSAPublicKey rsaPublicKey, boolean appendHeadTail) {
		String publicKey = "";
		if(rsaPublicKey != null) {
			publicKey = Base64.encode(rsaPublicKey.getEncoded());
			if(appendHeadTail == true) {
				publicKey = StrUtils.concat(
						RSA_PUBLIC_KEY_BGN, publicKey, RSA_PUBLIC_KEY_END);
			}
		}
		return publicKey;
	}
	
	/**
	 * 把RSA私钥对象转换成字符串
	 * @param rsaPrivateKey RSA私钥对象
	 * @return 私钥数据字符�?, 形如: MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==
	 */
	public static String toRSAPrivateKey(RSAPrivateKey rsaPrivateKey) {
		return toRSAPrivateKey(rsaPrivateKey, false);
	}
	
	/**
	 * 把RSA私钥对象转换成字符串
	 * @param rsaPrivateKey RSA私钥对象
	 * @param appendHeadTail 添加密钥首尾标识: "-----BEGIN RSA PRIVATE KEY-----\n" �? "\n-----END RSA PRIVATE KEY-----\n"
	 * @return 私钥数据字符�?, 形如: -----BEGIN RSA PRIVATE KEY-----\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMaKkeYlORQcJjKdJFD5RHqVVMBYA1RasnF/LTqHKaX1GO6IuRUXf5CR9b+VREG+4c+uVO/RC1k8vzOU7FtOgFXjOFqSGAJ5DKXHd1fjjFv++jjNpBScyXg7+/bjQFq8VuACSM6yG3J+Ou/ql35iRypjx3eEdeaLecHcQ7jP9l2LAgMBAAECgYAhvTCX/KFbgoEXPs8KF6IEdtYFLa+7KQKD+Qm1lXyFYEZRWtig9fJOng819Ga6CXcUNNroOg0EqCcR2+/igE+ce7PF2K+ooO2jYKKaoNmCr1xKuP1Iy8aGrcKeobN8FsWSIi5eyvB847dp/1rmAqqR9hOw5FUnblDvFf95olyvEQJBAPzxFk/Sw49AxuKXyYC6VXuH/aNu+ExK+wCdnr1pjJpW75D9xi94AqWf7XdB5PblTBKCv3aFpxhFMzTZe/1Iq7MCQQDI8RnQupWU5rwb+OGWJVyra5ApimsQidWEKORPz0U+HrhiYuTOJHa24J584EqEWu9hqm9HYWpgvSIo1rgUezrJAkEAnXUC+6vrWxjq9hGhOYZFQoIUbZHd9bhTaj20nJrBES7/MRYZMmGV3D6jV7Locp2o7nj/8SsgKqahSsv8OF7tqwJAMJBMm+ysQBtvtRb2dlI7TlaltdR1Qb7+Mn2riDpg0r2b9HNQNx4K7vHke+u9NrW/iwwk7sx1aEHtoo8aWCDcOQJAO+XdCt05WLkUKaThB3JIlgjDwTx4561+ahpJ4bLmRgC0TWJyF6IgR0/oyAweXh7m9UTxAU/n+XvB3tjieGx6QA==\n-----END RSA PRIVATE KEY-----\n
	 */
	public static String toRSAPrivateKey(
			RSAPrivateKey rsaPrivateKey, boolean appendHeadTail) {
		String privateKey = "";
		if(rsaPrivateKey != null) {
			privateKey = Base64.encode(rsaPrivateKey.getEncoded());
			if(appendHeadTail == true) {
				privateKey = StrUtils.concat(
						RSA_PRIVATE_KEY_BGN, privateKey, RSA_PRIVATE_KEY_END);
			}
		}
		return privateKey;
	}
	
}
