package exp.libs.warp.net.http;

/**
 * <PRE>
 * 封装了Apache-HttpClient.
 *  可以保持连接对象, 并介入获取连接过程中的请求/响应参数.
 * -----------------------------------------------
 *   在JDK1.6、JDK1.7、JDK1.8下使用均支持TLSv1.2
 * </PRE>
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-21
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class HttpClient extends _HttpClient {

	public HttpClient() {
		super();
	}
	
	public HttpClient(String charset, int connTimeout, int callTimeout) {
		super(charset, connTimeout, callTimeout);
	}

}
