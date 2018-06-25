package exp.libs.warp.net.http;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import exp.libs.envm.HttpHead;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * HTTP-URL工具(原生工具)
 * -----------------------------------------------
 *   在JDK1.6、JDK1.7、JDK1.8下使用均支持TLSv1.2
 * </PRE>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-12-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class HttpURLUtils extends HttpUtils {

	/** 私有化构造函�? */
	protected HttpURLUtils() {}
	
	/**
	 * 提交POST请求
	 * @param url 资源路径
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doPost(String url) {
		return doPost(url, null, null);
	}
	
	/**
	 * 提交POST请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doPost(String url, Map<String, String> header, 
			Map<String, String> request) {
		return doPost(url, header, request, DEFAULT_CHARSET);
	}
	
	/**
	 * 提交POST请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param charset 字符集编�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doPost(String url, Map<String, String> header, 
			Map<String, String> request, String charset) {
		return doPost(url, header, request, 
				CONN_TIMEOUT, CALL_TIMEOUT, charset);
	}
	
	/**
	 * 提交POST请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doPost(String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) {
		String response = "";
		try {
			response = _doPost(url, header, request, 
					connTimeout, readTimeout, charset);
			
		} catch(Exception e) {
			log.error("提交{}请求失败: [{}]", METHOD_POST, url, e);
		}
		return response;
	}
	
	/**
	 * 提交POST请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 * @throws Exception
	 */
	private static String _doPost(String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) throws Exception {
		String response = "";
		HttpURLConnection conn = createHttpConn(url, METHOD_POST, 
				header, connTimeout, readTimeout);
		
		// POST的请求参数是在结构体中发过去�?
		String kvs = encodeRequests(request, charset);
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
	 * @param url 资源路径
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doGet(String url) {
		return doGet(url, null, null);
	}
	
	/**
	 * 提交GET请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doGet(String url, Map<String, String> header, 
			Map<String, String> request) {
		return doGet(url, header, request, DEFAULT_CHARSET);
	}
	
	/**
	 * 提交GET请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param charset 字符集编�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doGet(String url, Map<String, String> header, 
			Map<String, String> request, String charset) {
		return doGet(url, header, request, 
				CONN_TIMEOUT, CALL_TIMEOUT, charset);
	}

	/**
	 * 提交GET请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	public static String doGet(String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) {
		String response = "";
		try {
			response = _doGet(url, header, request, 
					connTimeout, readTimeout, charset);
			
		} catch(Exception e) {
			log.error("提交{}请求失败: [{}]", METHOD_GET, url, e);
		}
		return response;
	}
	
	/**
	 * 提交GET请求
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 * @throws Exception
	 */
	private static String _doGet(String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) throws Exception {
		String kvs = encodeRequests(request, charset);	
		url = HttpUtils.concatGET(url, kvs);	// GET的参数是拼在url后面�?
		
		HttpURLConnection conn = createHttpConn(url, METHOD_GET,
				header, connTimeout, readTimeout);
		String response = responseAsString(conn, charset);
		close(conn);
		return response;
	}

	/**
	 * 提取HTTP连接的响应结�?
	 * @param conn HTTP连接
	 * @param charset 字符集编�?
	 * @return HTTP返回的字符串（包括文本、json、xml等内容）
	 */
	private static String responseAsString(HttpURLConnection conn, String charset) {
		if(!isResponseOK(conn)) {
			return "";
		}
		
		// 检测返回的内容是否使用gzip压缩�?
		String encode = conn.getContentEncoding();
		boolean isGzip = HttpHead.VAL.GZIP.equalsIgnoreCase(encode);

		String response = "";
		try {
			InputStreamReader is = isGzip ? 
					new InputStreamReader(new GZIPInputStream(conn.getInputStream()), charset) : 
					new InputStreamReader(conn.getInputStream(), charset);
			response = IOUtils.toStr(is);
			is.close();
			
		} catch (Exception e) {
			log.error("获取HTTP响应结果失败", e);
		}
		return response;
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 */
	public static boolean downloadByPost(String savePath, String url) {
		return downloadByPost(savePath, url, null, null);
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 */
	public static boolean downloadByPost(String savePath, String url, 
			Map<String, String> header, Map<String, String> request) {
		return downloadByPost(savePath, url, header, request, 
				CONN_TIMEOUT, CALL_TIMEOUT, DEFAULT_CHARSET);
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 */
	public static boolean downloadByPost(String savePath, String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) {
		boolean isOk = false;
		try {
			isOk = _downloadByPost(savePath, url, header, request, 
					connTimeout, readTimeout, charset);
		} catch (Exception e) {
			log.error("下载资源失败: [{}]", url, e);
		}
		return isOk;
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 * @throws Exception
	 */
	private static boolean _downloadByPost(String savePath, String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) throws Exception {
		boolean isOk = false;
		HttpURLConnection conn = createHttpConn(url, METHOD_POST, 
				header, connTimeout, readTimeout);
		
		// POST的请求参数是在结构体中发过去�?
		String kvs = encodeRequests(request, charset);
		if (StrUtils.isNotEmpty(kvs)) {
			byte[] bytes = CharsetUtils.toBytes(kvs, charset);
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			
			isOk = responseAsRes(conn, savePath);
		}
		close(conn);
		return isOk;
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 */
	public static boolean downloadByGet(String savePath, String url) {
		return downloadByGet(savePath, url, null, null);
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 */
	public static boolean downloadByGet(String savePath, String url, 
			Map<String, String> header, Map<String, String> request) {
		return downloadByGet(savePath, url, header, request, 
				CONN_TIMEOUT, CALL_TIMEOUT, DEFAULT_CHARSET);
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 */
	public static boolean downloadByGet(String savePath, String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) {
		boolean isOk = false;
		try {
			isOk = _downloadByGet(savePath, url, header, request, 
					connTimeout, readTimeout, charset);
		} catch (Exception e) {
			log.error("下载资源失败: [{}]", url, e);
		}
		return isOk;
	}
	
	/**
	 * 下载资源，适用于返回类型是非文本的响应
	 * @param savePath 包括文件名的保存路径
	 * @param url 资源路径
	 * @param header 请求头参数表
	 * @param request 请求参数�?
	 * @param connTimeout 连接超时（ms�?
	 * @param readTimeout 读取超时（ms�?
	 * @param charset 字符集编�?
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 * @throws Exception
	 */
	private static boolean _downloadByGet(String savePath, String url, 
			Map<String, String> header, Map<String, String> request, 
			int connTimeout, int readTimeout, String charset) throws Exception {
		String kvs = encodeRequests(request, charset);	
		url = HttpUtils.concatGET(url, kvs);	// GET的参数是拼在url后面�?
		
		HttpURLConnection conn = createHttpConn(url, METHOD_GET,
				header, connTimeout, readTimeout);
		boolean isOk = responseAsRes(conn, savePath);
		close(conn);
		return isOk;
	}
	
	/**
	 * 提取HTTP连接的响应资�?
	 * @param conn HTTP连接
	 * @param savePath 包括文件名的保存路径
	 * @return 是否下载成功（下载成功会保存到savePath�?
	 */
	private static boolean responseAsRes(HttpURLConnection conn, String savePath) {
		boolean isOk = false;
		if(!isResponseOK(conn)) {
			return isOk;
		}
		
		try {
			InputStream is = conn.getInputStream();
			isOk = IOUtils.toFile(is, savePath);
			is.close();
			
		} catch (Exception e) {
			log.error("保存资源 [{}] 失败", savePath, e);
		} 
		return isOk;
	}
	
}
