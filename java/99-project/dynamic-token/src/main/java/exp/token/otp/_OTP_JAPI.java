package exp.token.otp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import exp.token.utils.CharsetUtils;
import exp.token.utils.OsUtils;

/**
 * <PRE>
 * OTP API(java)
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class _OTP_JAPI {

	/** 提供OTP算法接口的dll文件(win x64) */
	private final static String OTP_DLL_X64 = "dt_otp_x64.dll";
	
	/** 提供OTP算法接口的dll文件(win x86) */
	private final static String OTP_DLL_X86 = "dt_otp_x86.dll";
	
	/** 提供OTP算法接口的so文件(linux x64) */
	private final static String OTP_SO_X64 = "dt_otp_x64.so";
	
	/** 提供OTP算法接口的so文件(linux x86) */
	private final static String OTP_SO_X86 = "dt_otp_x86.so";
	
	/** dll/so临时文件的名称前缀 */
	private final static String OTP_FILE_PREFIX = "dt_otp";
	
	/** dll临时文件的名称后缀 */
	private final static String OTP_DLL_FILE_SUFFIX = ".dll";
	
	/** so临时文件的名称后缀 */
	private final static String OTP_SO_FILE_SUFFIX = ".so";
	
	/** 最小时间偏移量(1s - 受限于linux平台精度) */
	private final static long MIN_TIMEOFFSET = 1000L;
	
	/** 最大时间偏移量(1hour) */
	private final static long MAX_TIMEOFFSET = 3600000L;
	
	/** 实例 */
	private static volatile _OTP_JAPI instance;
	
	/** 是否初始化成功 */
	private boolean isInit;
	
	/**
	 * 构造函数
	 */
	private _OTP_JAPI() {
		loadDynamicLib();
	}
	
	/**
	 * 加载OTP算法接口的动态链接库
	 */
	private void loadDynamicLib() {
		this.isInit = false;
		try {
			System.load(copyLibToTempDir());
			this.isInit = true;
			
		} catch(Exception e) {
			System.err.println("加载DLL文件失败, 无法生成/校验动态令牌.");
			e.printStackTrace();
		}
	}
	
	/**
	 * 复制动态库文件到临时目录
	 * @return 所复制的动态库文件的绝对路径
	 */
	private String copyLibToTempDir() {
		String otpLibPath = "";
		String otpLibName = OsUtils.isWin() ? 
				(OsUtils.isX64() ? OTP_DLL_X64 : OTP_DLL_X86) : 
				(OsUtils.isX64() ? OTP_SO_X64 : OTP_SO_X86);
		try {
			InputStream in = _OTP_JAPI.class.getResource(otpLibName).openStream();
			File dll = File.createTempFile(OTP_FILE_PREFIX, 
					OsUtils.isWin() ? OTP_DLL_FILE_SUFFIX : OTP_SO_FILE_SUFFIX);
			FileOutputStream out = new FileOutputStream(dll);
			
			int i;
			byte [] buf = new byte[1024];
			while((i = in.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
			out.close();
			in.close();
			
			dll.deleteOnExit();	//请求在程序退出时删除该临时文件(对junit不适用)
			otpLibPath = dll.getAbsolutePath();
			
		} catch (Exception e) {
			System.err.println("构造DLL文件失败, 无法生成/校验动态令牌.");
			e.printStackTrace();
		}
		return otpLibPath;
	}
	
	/**
	 * 获取实例
	 * @return 实例
	 */
	public static _OTP_JAPI getInstn() {
		if(instance == null) {
			synchronized (_OTP_JAPI.class) {
				if(instance == null) {
					instance = new _OTP_JAPI();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 获取默认私钥.
	 * 	若初始化失败, 始终返回 "INTERNAL_ERROR".
	 * @return 默认私钥
	 */
	public String getDefaultPrivateKey() {
		return isInit ? _OTP_CAPI.getDefaultPrivateKey() : "INTERNAL_ERROR";
	}
	
	/**
	 * 获取默认时间偏移量.
	 * 	若初始化失败, 始终返回 "0".
	 * @return 默认时间偏移量(ms)
	 */
	public long getDefaultTimeOffset() {
		return isInit ? _OTP_CAPI.getDefaultTimeOffset() : 0;
	}
	
	/**
	 * 获取动态令牌.
	 * 	若初始化失败, 始终返回 "0000".
	 * @param privateKey 私钥
	 * @param timeOffset 时间偏移量(ms)
	 * @return 动态令牌
	 */
	public String getOtpToken(String privateKey, long timeOffset) {
		String otpToken = "0000";
		if(isInit) {
			privateKey = CharsetUtils.getWordChars(privateKey);
			timeOffset = timeOffset < MIN_TIMEOFFSET ? MIN_TIMEOFFSET : timeOffset;
			timeOffset = timeOffset > MAX_TIMEOFFSET ? MAX_TIMEOFFSET : timeOffset;
			otpToken = _OTP_CAPI.getOtpToken(privateKey, timeOffset);
		}
		return otpToken;
	}
	
	/**
	 * 校验动态令牌是否有效.
	 * 	若初始化失败, 始终返回 "false".
	 * @param otpToken 动态令牌
	 * @param privateKey 私钥
	 * @return true:有效; false:无效
	 */
	public boolean isValid(final String otpToken, String privateKey) {
		boolean isValid = false;
		if(isInit && otpToken.matches("^[0-9a-fA-F]+$")) {
			privateKey = CharsetUtils.getWordChars(privateKey);
			isValid = _OTP_CAPI.isValid(otpToken, privateKey);
		}
		return isValid;
	}
	
}
