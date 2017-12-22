package exp.libs.warp.net.http;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * HTTPS工具
 * </PRE>
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class HttpsUtils extends _HttpUtils {

	/** 私有化构造函数 */
	protected HttpsUtils() {}
	
	/**
	 * 提交POST请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @return
	 */
	public static String doPost(String url, Map<String, String> headParams, 
			Map<String, String> requestParams) {
		return doPost(url, headParams, requestParams, DEFAULT_CHARSET);
	}
	
	/**
	 * 提交POST请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @param charset
	 * @return
	 */
	public static String doPost(String url, Map<String, String> headParams, 
			Map<String, String> requestParams, String charset) {
		return doPost(url, headParams, requestParams, 
				CONN_TIMEOUT, CALL_TIMEOUT, charset);
	}
	
	/**
	 * 提交POST请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @param connTimeout
	 * @param readTimeout
	 * @param charset
	 * @return
	 */
	public static String doPost(String url, 
			Map<String, String> headParams, Map<String, String> requestParams, 
			int connTimeout, int readTimeout, String charset) {
		String response = "";
		try {
			response = _doPost(url, headParams, requestParams, 
					connTimeout, readTimeout, charset);
			
		} catch(Exception e) {
			log.error("提交{}请求失败: {}", METHOD_POST, url, e);
		}
		return response;
	}
	
	/**
	 * 提交POST请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @param connTimeout
	 * @param readTimeout
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private static String _doPost(String url, 
			Map<String, String> headParams, Map<String, String> requestParams, 
			int connTimeout, int readTimeout, String charset) throws Exception {
		String response = "";
		HttpURLConnection conn = createHttpConn(new URL(url), METHOD_POST, 
				headParams, connTimeout, readTimeout);
		
		// POST的请求参数是发过去的
		String kvs = encodeRequests(requestParams, charset);
		if (StrUtils.isNotEmpty(kvs)) {
			byte[] bytes = CharsetUtils.toBytes(kvs, charset);
			
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			response = responseAsString(conn, charset);
		}
		close(conn);
		return response;

	}
	
	/**
	 * 提交GET请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @return
	 */
	public static String doGet(String url, Map<String, String> headParams, 
			Map<String, String> requestParams) {
		return doGet(url, headParams, requestParams, DEFAULT_CHARSET);
	}
	
	/**
	 * 提交GET请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @param charset
	 * @return
	 */
	public static String doGet(String url, Map<String, String> headParams, 
			Map<String, String> requestParams, String charset) {
		return doGet(url, headParams, requestParams, 
				CONN_TIMEOUT, CALL_TIMEOUT, charset);
	}

	/**
	 * 提交GET请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @param connTimeout
	 * @param readTimeout
	 * @param charset
	 * @return
	 */
	public static String doGet(String url, 
			Map<String, String> headParams, Map<String, String> requestParams, 
			int connTimeout, int readTimeout, String charset) {
		String response = "";
		try {
			response = _doGet(url, headParams, requestParams, 
					connTimeout, readTimeout, charset);
			
		} catch(Exception e) {
			log.error("提交{}请求失败: {}", METHOD_GET, url, e);
		}
		return response;
	}
	
	/**
	 * 提交GET请求
	 * @param url
	 * @param headParams
	 * @param requestParams
	 * @param connTimeout
	 * @param readTimeout
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private static String _doGet(String url, 
			Map<String, String> headParams, Map<String, String> requestParams, 
			int connTimeout, int readTimeout, String charset) throws Exception {
		String kvs = encodeRequests(requestParams, charset);	
		url = url.concat(kvs);	// GET的参数是拼在url后面的
		
		HttpURLConnection conn = createHttpConn(new URL(url), METHOD_POST,
				headParams, connTimeout, readTimeout);
		String response = responseAsString(conn, charset);
		close(conn);
		return response;
	}

	/**
	 * 提取HTTP/HTTPS连接的响应结果
	 * @param conn
	 * @param charset
	 * @return
	 */
	private static String responseAsString(HttpURLConnection conn, String charset) {
		if(!isResponseOK(conn)) {
			return "";
		}
		
		// 检测返回的内容是否使用gzip压缩过
		String encode = conn.getContentEncoding();
		boolean isGzip = HEAD.VAL.GZIP.equals(encode);

		StringBuilder response = new StringBuilder();
		try {
			InputStreamReader in = isGzip ? 
					new InputStreamReader(new GZIPInputStream(conn.getInputStream()), charset) : 
					new InputStreamReader(conn.getInputStream(), charset);
			int len = 0;
			char[] buff = new char[1024];
			while((len = in.read(buff)) != -1) {
				response.append(buff, 0, len);
			}
			in.close();
			
		} catch (Exception e) {
			log.error("提取HTTP响应结果失败", e);
		}
		return response.toString();
	}
	
}
