package exp.libs.warp.net.ws.client;

/**
 * <p>
 * Websevices客户端工具类
 * SSL模式可以使用在启动脚本中，添加证书相关配置的方法实现，如下：
 * <BR>
 * java -Dssl-test -Xms64m -Xmx128m <B>
 * -Djavax.net.ssl.keyStoreType=JKS 
 * -Djavax.net.ssl.trustStore=D:/server.keystore 
 * -Djavax.net.ssl.trustStorePassword=123456 
 * -Djavax.net.ssl.keyStore=D:/server.keystore 
 * -Djavax.net.ssl.keyStorePassword=123456</B> ...略
 * <BR>
 * 本类提供设置上述环境变量的方法，在调用call方法前，先设置上述环境变量即可。
 * JSK模式的trustStore和keyStore设置成同一个即可。
 * PKCS12则参考下面方法进行试，再进行配置：
 * <BR><b>
 * -Djavax.net.ssl.keyStoreType=PKCS12 
 * -Djavax.net.ssl.trustStore=D:/client.truststore
 * -Djavax.net.ssl.trustStorePassword=123456 
 * -Djavax.net.ssl.keyStore=D:/client.p12 
 * -Djavax.net.ssl.keyStorePassword=123456</B> ...略<br>
 * 
 * 可以参考单元测试中的实例，测试用的store文件在
 * src/test/resources/com/xxxx/pub/net/webservices/client/test/ssl中
 * 
 * 详见：http://172.168.27.5:81/svn/repos1/01train/03_technical/09_SSL
 * </p>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SSLWSClientUtils extends WSClientUtils {
	
	/** 
	 * 密钥库类型，有JKS PKCS12
	 */
	public static final String JKS = "JKS";
	
	/** 
	 * 密钥库类型，有JKS PKCS12
	 */
	public static final String PKCS12= "PKCS12";

	/**
	 * https连接前，环境变量设置/SSL连接模式必要设置
	 *
	 * @param keyStoreType 			密钥库类型
	 * @param trustStoreType		受信任密钥库类型，当前只能设置为JKS模式或者不设置
	 * @param trustStore			受信任密钥库文件
	 * @param trustStorePassword	受信任密钥库文件密码
	 * @param keyStore				密钥库文件
	 * @param keyStorePassword		密钥库文件密码
	 */
	public static void sslBefore(String keyStoreType, String trustStoreType, 
			String trustStore,	String trustStorePassword, String keyStore, 
			String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);
		System.setProperty("javax.net.ssl.trustStore", trustStore);
		System.setProperty("javax.net.ssl.trustStorePassword",
				trustStorePassword);
		System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
		System.setProperty("javax.net.ssl.keyStore", keyStore);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	/**
	 * JSK模式<BR>
	 * https连接前，环境变量设置/SSL连接模式必要设置
	 *
	 *
	 * @param keyStore				密钥库文件
	 * @param keyStorePassword		密钥库文件密码
	 */
	public static void sslJSKModel(String keyStore, String keyStorePassword) {
		sslBefore(JKS, JKS, keyStore, keyStorePassword, keyStore, keyStorePassword);
	}
	
	/**
	 * PKCS12模式<BR>
	 * https连接前，环境变量设置/SSL连接模式必要设置
	 *
	 * @param trustStore			受信任密钥库文件
	 * @param trustStorePassword	受信任密钥库文件密码
	 * @param keyStore				密钥库文件
	 * @param keyStorePassword		密钥库文件密码
	 */
	public static void sslPKCS12Model(String trustStore,
			String trustStorePassword, String keyStore, String keyStorePassword) {
		sslBefore(PKCS12, JKS, trustStore, trustStorePassword, keyStore, keyStorePassword);
	}

}
