package exp.libs.warp.net.http;

import org.apache.commons.httpclient.HttpClient;

/**
 *<pre>
 *    HTTP 请求接口类
 * </pre> 
 * @version {1.0} by 2013-10-14
 * @since jdk版本：1.5以上
 * @author xiaolin
 * HTTP 请求接口类
 */
public interface BakHttpUrlRequest {
       
	/**
	 * 请求连接GET方法
	 * 
	 * @param client
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String requestGet(HttpClient client, String url)  
			throws Exception;

	/**
	 * 需要验证用户名密码请求连接
	 * @param sHostIp 需要验证用户密码的IP地址
	 * @param iHostPort 需要验证用户密码的端口号
	 * @param sUserName 需要验证用户名信息
	 * @param sPwd 需要验证密码信息
	 * @return 返回字符串
	 */
	public HttpClient requestCredentials(String sHostIp, int iHostPort, 
			  String username, String password) throws Exception;
	
	/**
	 * 请求连接POST方法
	 *
	 * @param client
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String requestPost(HttpClient client, String url)  
			throws Exception;
}
