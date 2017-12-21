package exp.libs.warp.net.http;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * <pre>
 *    简单的 HTTP 请求接口实现类
 *    高级应用请参考下面网址内容进行开发。
 *    参考：http://www.ibm.com/developerworks/cn/opensource/os-httpclient/
 * </pre>
 * 
 * @version 1.0 by 2013-10-14
 * @since jdk版本：1.5以上
 * @author xiaolin HTTP 请求接口实现类
 */
@Deprecated
class BakHttpUtils {

	/** 请求类型 */
	public final static int POST = 1;

	/** 请求类型 */
	public final static int GET = 0;
	
	/** 编码，默认utf-8 */
	public final static String ENCODING = "UTF-8";
	
	/**
	 * 连接超时，默认90秒； 
	 */
	public static int timeOut = 90;

	/** 请求客户端 */
	private static HttpClient client = null;
	
	/** 请求客户端集合，保存需要账号密码的请求  */
	private static Map<String ,HttpClient> clients = null;
	
//	public static String charset = "UTF-8";

	/**
	 * 单例http客户端对象
	 * 
	 * @return http客户端对象
	 */
	public synchronized static HttpClient getInstance() {
		if (client == null) {
			client = new HttpClient();
		}
		return client;
	}
	
	/**
	 * 返回需要账号密码的http客户端对象
	 *
	 * @param url 请求地址
	 * @return http客户端对象
	 */
	public synchronized static HttpClient getInstance(String url) {
		if (clients == null) {
			clients = new HashMap<String, HttpClient>();
		} else {
			return clients.get(url);
		}
		return null;
	}

	/**
	 * 获取http请求的回复
	 * 
	 * @param url
	 *            请求地址
	 * @param type
	 *            0 get, 1 post
	 * @return 返回请求结果字符串
	 * @throws IOException
	 * @throws HttpException
	 */
	public static String getResponse(String url, int type)
			throws HttpException, IOException {
		client = getInstance();
		return getResponse(client, url, type);
	}

	/**
	 * 请求连接POST方法
	 * 
	 * @param url
	 *            请求地址
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	public static String getResponse(String url) throws HttpException,
			IOException {
		return getResponse(url, BakHttpUtils.POST);
	}

	/**
	 * 获取http请求的回复
	 * 
	 * @param url
	 *            请求地址
	 * @param type
	 *            0 get, 1 post
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	public static String getResponse(URL url, int type) throws HttpException,
			IOException {
		return getResponse(url.toString(), type);
	}

	/**
	 * 获取http请求的回复，POST
	 * 
	 * @param url
	 *            请求地址
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	public static String getResponse(URL url) throws HttpException, IOException {
		return getResponse(url.toString(), BakHttpUtils.POST);
	}

	/**
	 * 获取http请求的回复
	 * 
	 * @param url
	 *            请求的地址
	 * @param type
	 *            0 get, 1 post
	 * @param username
	 * 				用户名
	 * @param password
	 * 				密码
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	public static String getResponse(String url, int type, String username,
			String password) throws HttpException, IOException {
		return getResponse(new URL(url), type, username, password);
	}
	
	/**
	 * 获取http请求的回复，POST
	 * 
	 * @param url
	 *            请求的地址
	 * @param username
	 * 				用户名
	 * @param password
	 * 				密码
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	public static String getResponse(String url, String username,
			String password) throws HttpException, IOException {
		return getResponse(new URL(url), BakHttpUtils.POST, username, password);
	}

	/**
	 * 获取http请求的回复，POST
	 * 
	 * @param url
	 *            请求的地址
	 * @param username
	 * 				用户名
	 * @param password
	 * 				密码
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	public static String getResponse(URL url, String username,
			String password) throws HttpException, IOException {
		return getResponse(url, BakHttpUtils.POST, username, password);

	}
	
	/**
	 * 获取http请求的回复
	 * 
	 * @param url
	 *            请求的地址
	 * @param type
	 *            0 get, 1 post
	 * @param username
	 * 				用户名
	 * @param password
	 * 				密码
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	public static String getResponse(URL url, int type, String username,
			String password) throws HttpException, IOException {
		String strUrl = url.toString();
		client = getInstance(strUrl);
		
		if (client == null) {
			client = new HttpClient();
			client.getState().setCredentials(
					new AuthScope(url.getHost(), url.getPort(), null),
					new UsernamePasswordCredentials(username, password));
			clients.put(strUrl, client);
		}
		return getResponse(client, strUrl, type);

	}

	/**
	 * 获取http请求的回复
	 * 
	 * @param httpClient
	 *            http客户端对象
	 * @param url
	 *            请求的地址
	 * @param type
	 *            0 get, 1 post
	 * @return 请求响应反应字符串
	 * @throws HttpException 发生致命的异常，可能是协议不对或者返回的内容有问题
	 * @throws IOException 发生网络异常
	 */
	private static String getResponse(HttpClient httpClient, String url,
			int type) throws HttpException, IOException {
		
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				timeOut * 1000);
		String response = null;
		if (type == BakHttpUtils.GET) {
//			System.out.println("GET*******************");
			GetMethod get = new GetMethod(url);
			httpClient.executeMethod(get);
			response = new String(get.getResponseBody(), ENCODING);
			get.releaseConnection();
		} else {
//			System.out.println("POST*******************");
			PostMethod post = new PostMethod(url);
			httpClient.executeMethod(post);
			response=  new String(post.getResponseBody(), ENCODING);
			post.releaseConnection();
		}
		return response;
	}

}
