package exp.bilibli.plugin.https;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.cache.Browser;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.other.StrUtils;

public class HttpsUtils {

	public final static int CONN_TIMEOUT = 10000;
	
	public final static int READ_TIMEOUT = 10000;
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	public final static String METHOD_GET = "GET";
	
	public final static String METHOD_POST = "POST";
	
	public final static String TYPE_XML = 
			StrUtils.concat("application/x-javascript text/xml; charset=", DEFAULT_CHARSET);
	
	public final static String TYPE_JSON = 
			StrUtils.concat("application/x-javascript; charset=", DEFAULT_CHARSET);
	
	public final static String TYPE_FORM = 
			StrUtils.concat("application/x-www-form-urlencoded; charset=", DEFAULT_CHARSET);
	
	public final static String HTTPS = "https";
	
	public final static String TLS = "tls";
	
	public static String doPost(String url, Map<String, String> headParams, 
			Map<String, String> requestParams) {
		return doPost(url, headParams, requestParams, CONN_TIMEOUT, READ_TIMEOUT);
	}
	
	public static String doPost(String url, Map<String, String> headParams, 
			Map<String, String> requestParams, int connTimeOut, int readTimeOut) {
		String response = "";
		try {
			response = _doPost(url, headParams, requestParams, connTimeOut, readTimeOut);
			
		} catch(Exception e) {
			// TODO
			e.printStackTrace();
		}
		return response;
	}
	
	private static String _doPost(String url, 
			Map<String, String> headParams, Map<String, String> requestParams, 
			int connTimeOut, int readTimeOut) throws Exception {
		String response = "";
		HttpURLConnection conn = createConn(new URL(url), METHOD_POST, 
				headParams, connTimeOut, readTimeOut);
		
		// POST的请求参数是发过去的
		String kvs = toUrlKVs(requestParams, DEFAULT_CHARSET);
		if (StrUtils.isNotEmpty(kvs)) {
			byte[] bytes = CharsetUtils.toBytes(kvs, DEFAULT_CHARSET);
			
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			response = responseAsString(conn);
			conn.disconnect();
		}
		return response;

	}

	/**
	 * @Title: doGet
	 * @Description: doGet发送消息请求
	 * @param @param url
	 * @param @param params
	 * @param @param readTimeOut
	 * @param @param connectTimeOut
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String doGet(String url, Map<String, String> headParams, 
			Map<String, String> requestParams, int connTimeOut, int readTimeOut) {

		HttpURLConnection conn = null;
		String result = null;
		try {
			String query = toUrlKVs(requestParams, DEFAULT_CHARSET);	// GET的参数是拼在url后
			conn = createConn(new URL(buildUrl(url, query)), METHOD_POST,
					headParams, connTimeOut, readTimeOut);
			result = responseAsString(conn);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}

	// 把doGet和doPost请求最后的接受参数再次的抽取出来
	private static String responseAsString(HttpURLConnection connection) {
		StringBuffer buffer = new StringBuffer();
		InputStreamReader reader = null;
		OutputStream out = null;
		try {
			// 如果返回成功
			if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
				reader = new InputStreamReader(connection.getInputStream());
				char[] ch = new char[1024];
				int x = 0;
				while ((x = reader.read(ch)) != -1) {
					buffer.append(ch, 0, x);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return buffer.toString();
	}

	private static HttpURLConnection createConn(URL url, String method, 
			Map<String, String> headParams, int connTimeOut, int readTimeOut) 
					throws Exception {
		if (url == null) {
			return null;
		}
		HttpURLConnection conn = null;
		
		if(HTTPS.equals(url.getProtocol())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance(TLS);
				ctx.init(new KeyManager[0],
						new TrustManager[] { new DefaultTrustManager() },
						new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			conn = connHttps;
			
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestMethod(method);
		conn.setConnectTimeout(connTimeOut);
		conn.setReadTimeout(readTimeOut);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		
		Iterator<String> keyIts = headParams.keySet().iterator();
		while(keyIts.hasNext()) {
			String key = keyIts.next();
			String val = headParams.get(key);
			if(StrUtils.isNotEmpty(key, val)) {
				conn.setRequestProperty(key, val);
			}
		}
		return conn;
	}

	public static String toUrlKVs(Map<String, String> params, String charset)
			throws UnsupportedEncodingException {
		if(params == null || params.isEmpty()) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder("?");
		Iterator<String> keyIts = params.keySet().iterator();
		while(keyIts.hasNext()) {
			String key = keyIts.next();
			String val = params.get(key);
			if(StrUtils.isNotEmpty(key, val)) {
				sb.append(key).append("=");
				sb.append(URLEncoder.encode(val, charset)).append("&");
			}
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();

	}

	/**
	 * @Title: buildUrl
	 * @Description: 拼接url地址操作
	 * @param @param url
	 * @param @param query
	 * @param @return
	 * @return String
	 * @throws
	 */
	private static String buildUrl(String url, String query) {
		if (query == null && query.isEmpty()) {
			return url;
		}
		if (url.endsWith("?")) {
			url = url + query;
		} else {
			url = url + "?" + query;
		}

		return url;
	}

	private static class DefaultTrustManager implements X509TrustManager {

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	}

}
