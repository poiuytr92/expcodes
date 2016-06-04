package exp.libs.warp.net.http;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;


/**
 * <pre>
 *    HTTP 请求接口实现类
 * </pre> 
 * @version {1.0} by 2013-10-14
 * @since jdk版本：1.5以上
 * @author xiaolin
 * HTTP 请求接口实现类
 */
public class HttpUrlRequestImpl implements HttpUrlRequest {
	/**
	 * 请求客户端
	 */
	private HttpClient client = null;
	
	/**
	 * 初始化客户端对象
	 * @return
	 */
	public HttpClient getInstance(){
		if(client == null){
		  client = new HttpClient();
		}
		return client;
	}

	/**
	 * 请求连接GET方法
	 * @param client http请求客户端
	 * @param url 请求地址
	 * @return 返回请求结果字符串
	 */
	public String requestGet(HttpClient client, String url) throws Exception {
		String sContent = visit(client, url);
		return sContent;
	}
	
	/**
	 * 请求连接POST方法
     * @param client http请求客户端
	 * @param url 请求地址
	 * @return 返回请求结果字符串
	 */
	public String requestPost(HttpClient client, String url) throws Exception {
		String sContent = visitPost(client, url);
		return sContent;
	}


	/**
	 * 需要验证用户名密码请求连接
	 * @param sHostIp 请求服务器IP地址
	 * @param iHostPort 请求服务器端口信息
	 * @param sUserName 请求验证的用户名
	 * @param sPwd 请求验证的密码
	 * @return http请求客户端对象
	 */
	public HttpClient requestCredentials(String sHostIp, int iHostPort,
			String sUserName, String sPwd) throws Exception{
		client = null;
		try{
			client = getInstance();
			client.getState().setCredentials(
					new AuthScope(sHostIp, iHostPort, null),
					new UsernamePasswordCredentials(sUserName, sPwd));
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		return client;
	}

	
	/**
	 * 访问指定url的内容,访问失败则返回null
	 * @param client http请求客户端
	 * @param url
	 *            访问URL
	 * @return 返回请求结果字符串对象
	 */
	private String visit(HttpClient client, String url) throws Exception {
		int status = 0;
		try {
			if(client == null){
				client =  getInstance();
			}
			GetMethod get = new GetMethod(url);
			status = client.executeMethod(get);
			if (status == 200) {
				return get.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			e.printStackTrace();
			System.err.println("HTTP status:" + status);
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 访问指定url的内容,访问失败则返回null
	 * @param client http请求客户端
	 * @param url
	 *            访问URL
	 * @return 返回请求结果字符串对象
	 *   如果验证信息失效则返回401错误
	 */
	 private String visitPost(HttpClient client, String url) throws Exception {
		int status = 0;
		try {
			PostMethod post = new  PostMethod(url);  
			post.setDoAuthentication(true);   
			status = client.executeMethod(post);   
			if (status == 200) {
				return post.getResponseBodyAsString();
			}else if(status == 401){
				return "401";
			}
		} catch (HttpException e) {
			e.printStackTrace();
			System.err.println("HTTP status:" + status);
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

}
