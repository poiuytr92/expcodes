package exp.token.otp.test;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import exp.token.otp.OTPToken;
import exp.token.utils.FileUtils;

/**
 * <PRE>
 * OTP令牌测试用例.
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestOtpToken {

	/** dll临时文件的名称前缀 */
	private final static String OTP_DLL_FILE_PREFIX = "dt_otp";
	
	/** dll临时文件的名称后缀 */
	private final static String OTP_DLL_FILE_SUFFIX = ".dll";
	
	/**
	 * OTP令牌生成/校验 测试:
	 * 	令牌创建测试
	 */
	@Test
	public void testGenerate() {
		System.out.println("默认密钥、默认时间偏差:" + OTPToken.getToken());
		System.out.println("默认密钥:" + OTPToken.getToken(1000));
		System.out.println("默认时间偏差:" + OTPToken.getToken("myKey"));
		System.out.println("自定义密钥、时间偏差:" + OTPToken.getToken("myKey", 12345678));
		System.out.println("===================");
	}
	
	/**
	 * OTP令牌生成/校验 测试:
	 * 	令牌时效性测试
	 */
	@Test
	public void testValid() {
		String privateKey = "exp-20150720";
		long timeOffset = 2000L;	//偏差值 2+2秒
		String otpToken = OTPToken.getToken(privateKey, timeOffset);
		System.out.println("动态令牌:" + otpToken);
		System.out.println("校验时间偏差值: " + timeOffset + "ms");
		
		boolean isValid = OTPToken.isValid(otpToken, privateKey);
		System.out.println("currentTime: " + System.currentTimeMillis());
		System.out.println("即时校验(通过): isValid=" + isValid);
		Assert.assertEquals(true, isValid);
		
		tSleep(1100);	// 1.1秒后(相对于令牌生成时间)
		isValid = OTPToken.isValid(otpToken, privateKey);
		System.out.println("currentTime: " + System.currentTimeMillis());
		System.out.println("1.1秒后校验([令牌生成时间]的[正偏差范围内]-通过): isValid=" + isValid);
		Assert.assertEquals(true, isValid);
		
		tSleep(1000);	// 2.1秒后(相对于令牌生成时间)]
		isValid = OTPToken.isValid(otpToken, privateKey);
		System.out.println("currentTime: " + System.currentTimeMillis());
		System.out.println("2.1秒后校验([令牌生成时间]的[下一个时间点]的[负偏差范围内]-通过): isValid=" + isValid);
		Assert.assertEquals(true, isValid);
		
		tSleep(2000);	// 4.1秒后(相对于令牌生成时间)
		isValid = OTPToken.isValid(otpToken, privateKey);
		System.out.println("currentTime: " + System.currentTimeMillis());
		System.out.println("4.1秒后校验([令牌生成时间]的[后一个时间点]的[负偏差范围外]-不通过): isValid=" + isValid);
		Assert.assertEquals(false, isValid);
	}
	
	/**
	 * junit不会自动删除临时文件，需在测试用例运行完成后指定删除.
	 * 
	 * 由于dll运行库尚被占用，可能无法删除，只能删除上次测试遗留的dll.
	 * 但即使残留dll在临时目录也没关系, 系统会自动清理临时目录的文件.
	 */
	@After
	public void tearDown() {
		try {
			File tmpFile = File.createTempFile("tmpJavaFile", "txt");
			String path = tmpFile.getAbsolutePath().replace('\\', '/');
			int pos = path.lastIndexOf('/');
			if(pos > 0) {
				path = path.substring(0, pos);
			}
			
			FileUtils.delete(tmpFile);
			
			File dir = new File(path);
			File[] dllFiles = dir.listFiles();
			for(File dllFile : dllFiles) {
				if(dllFile.getName().startsWith(OTP_DLL_FILE_PREFIX) && 
						dllFile.getName().endsWith(OTP_DLL_FILE_SUFFIX)) {
					dllFile.deleteOnExit();	
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 休眠.
	 * 	用于时间过渡.
	 * @param millis 休眠时间(毫秒)
	 */
	private void tSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
