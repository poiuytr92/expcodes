package exp.libs.warp.net.http;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <PRE>
 * URL工具包
 * </PRE>
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class HttpUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(HttpUtils.class);
	
	/** 连接超时,默认1分钟 */
	public static final int CONN_TIMEOUT = 60000;

	/** 响应超时 ,默认1分钟 */
	public static final int CALL_TIMEOUT = 60000;
	
	/** 默认下载路径 */
	private final static String DEFAULT_DOWNLOAD_DIR = "./downloads/";
	
	/** 私有化构造函数 */
	protected HttpUtils() {}
	
	/**
	 * 测试URL是否依然有效
	 * @param httpUrl url路径
	 * @return true:有效; false:无效
	 */
	public static boolean testValid(String httpUrl) {
		boolean isValid = false;
		try {
			HttpURLConnection httpConn = 
					(HttpURLConnection) new URL(httpUrl).openConnection();
			isValid = (httpConn.getResponseCode() == 200);
		} catch (Exception e) {}
		return isValid;
	}
	
	public static HttpClient createHttpClient() {
		return createHttpClient(CONN_TIMEOUT, CALL_TIMEOUT);
	}

	public static HttpClient createHttpClient(int connTimeOut, int callTimeout) {
		HttpConnectionManagerParams managerParams = new HttpConnectionManagerParams();
		managerParams.setConnectionTimeout(connTimeOut);
		managerParams.setDefaultMaxConnectionsPerHost(2);
		managerParams.setSoTimeout(callTimeout);
		
		HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		httpConnectionManager.setParams(managerParams);

		HttpClient httpClient = new HttpClient(new HttpClientParams());
		httpClient.setHttpConnectionManager(httpConnectionManager);
		return httpClient;
	}
	
	public static void close(HttpClient httpClient) {
		if(httpClient != null) {
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
	}
	
	public static String encodeURL(final String httpUrl) {
		return encodeURL(httpUrl, Charset.UTF8);
	}
	
	/**
	 * 对URL进行编码
	 * @param httpUrl url路径
	 * @return 编码后URL
	 */
	public static String encodeURL(final String httpUrl, final String encoding) {
		String encodeURL = "";
		try {
			encodeURL = URLEncoder.encode(httpUrl, encoding);
			
		} catch (Exception e) {
			log.error("对URL以 [{}] 编码失败: {}", encoding, httpUrl, e);
		}
		return encodeURL;
	}

	public static String decodeURL(final String httpUrl) {
		return decodeURL(httpUrl, Charset.UTF8);
	}
	
	/**
	 * 对URL进行解码
	 * @param httpUrl url路径
	 * @return 解码后URL
	 */
	public static String decodeURL(final String httpUrl, final String encoding) {
		String decodeURL = "";
		try {
			decodeURL = URLDecoder.decode(httpUrl, encoding);
			
		} catch (Exception e) {
			log.error("对URL以 [{}] 解码失败: {}", encoding, httpUrl, e);
		}
		return decodeURL;
	}

	public static String encodeParams(final Map<String, String> requestParams) {
		return encodeParams(requestParams, Charset.UTF8);
	}
	
	/**
	 * 把请求参数转换为 k1=v1&k2=v2&... 形式并进行编码
	 * @param requestParams
	 * @param encoding
	 * @return
	 */
	public static String encodeParams(
			final Map<String, String> requestParams, final String encoding) {
		if(requestParams == null || requestParams.size() <= 0) {
			return "";
		}
		
		StringBuffer params = new StringBuffer(256);
		Iterator<String> it = requestParams.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = requestParams.get(key);
			
			params.append(key);
			params.append("=");
			try {
				params.append(URLEncoder.encode(value, encoding));
			} catch (UnsupportedEncodingException e) {
				params.append(value);
			}
			params.append("&");
		}
		params.setLength(params.length() - 1);
		return params.toString();
	}

	/**
	 * 获取页面编码
	 * @param httpUrl
	 * @return
	 */
	public static String getPageEncoding(String httpUrl) {
		final String RGX_ENCODING = "text/html;\\s*?charset=([a-zA-Z0-9\\-]+)";
		String encoding = Charset.UTF8;
		try {
			URL url = new URL(httpUrl);
			URLConnection conn = url.openConnection();
			conn.connect();
			
			encoding = conn.getContentEncoding();
			encoding = (CharsetUtils.isVaild(encoding) ? encoding : 
					RegexUtils.findFirst(conn.getContentType(), RGX_ENCODING));	
			
			if(CharsetUtils.isInvalid(encoding)) {
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, Charset.ISO);
				BufferedReader buff = new BufferedReader(isr);
				String line = null;
				while ((line = buff.readLine()) != null) {
					if(line.contains("<meta ")) {
						encoding = RegexUtils.findFirst(line, RGX_ENCODING);
						if(StrUtils.isNotEmpty(encoding)) {
							break;
						}
					}
				}
				buff.close();
				is.close();
			}
		} catch (Exception e) {
			log.error("获取页面编码失败: {}", httpUrl, e);
			encoding = Charset.UTF8;
		}
		encoding = (CharsetUtils.isVaild(encoding) ? encoding : Charset.UTF8);
		return encoding;
	}
	
	/**
	 * 获取页面源码
	 * @param httpUrl
	 * @return
	 */
	public static String getPageSource(String httpUrl) {
		StringBuffer pageSource = new StringBuffer();
		try {
			String encoding = getPageEncoding(httpUrl);
			URL url = new URL(httpUrl);
			URLConnection conn = url.openConnection();
			conn.connect();
			
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, encoding);
			BufferedReader buff = new BufferedReader(isr);
			String line = null;
			while ((line = buff.readLine()) != null) {
				pageSource.append(line).append("\r\n");
			}
			buff.close();
			is.close();
			
		} catch (Exception e) {
			log.error("获取页面源码失败: {}", httpUrl, e);
		}
		return pageSource.toString();
	}
	
	public static boolean download(String url) {
		return download(url, null);
	}

	public static boolean download(String url, String savePath) {
		boolean isOk = false;
		savePath = StrUtils.isNotEmpty(savePath) ? savePath : 
			StrUtils.concat(DEFAULT_DOWNLOAD_DIR, System.currentTimeMillis());
		
		HttpClient client = HttpUtils.createHttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			client.executeMethod(method);
			InputStream is = method.getResponseBodyAsStream();
			IOUtils.toFile(is, savePath);
			is.close();
			log.info("下载文件 [{}] 成功, 来源: [{}]", savePath, url);

		} catch (Exception e) {
			isOk = false;
			log.error("下载文件 [{}] 失败, 来源: [{}]", savePath, url, e);
			
		} finally {
			method.releaseConnection();
			HttpUtils.close(client);
		}
		return isOk;
	}
	
}