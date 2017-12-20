package exp.bilibli.plugin.https;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;

import exp.bilibli.plugin.Config;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * http://blog.csdn.net/luckyzhoustar/article/details/50471302
 * @ClassName: HttpTest
 * @Description: 自己开始封装的HTTP连接工具,http连接传递的参数封装到一个对象里面，
 *               http中get请求时，是把参数拼接到url后面的，而post请求直接输出即可
 * @author: LUCKY
 * @date:2016年1月6日 下午3:49:28
 */
public class HttpTest {

	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String HTTPS = "https";
	private static final String TLS = "tls";
	
	// 设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)  
    // application/x-javascript text/xml ->xml数据
	// application/x-javascript ->json对象
	// application/x-www-form-urlencoded ->表单数据
	private static final String CTYPE = "application/x-www-form-urlencoded;charset=" + DEFAULT_CHARSET;

	public static void main(String[] args) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		param.put("rnd", String.valueOf(System.currentTimeMillis() / 1000));
		param.put("fontsize", "25");
		param.put("mode", "1");
		param.put("msg", "测试发言哈哈");
		param.put("color", "16772431");
		param.put("roomid", "269706");
		String rst = getUrl(param, DEFAULT_CHARSET);
		System.out.println(rst);
		
//		String aa = doPost("https://api.live.bilibili.com/msg/send", param, 3000, 3000);
//		System.out.println(aa);
	}

	/**
	 * @throws Exception
	 * @Title: doPost
	 * @Description: doPost发送请求操作
	 * @param @param url
	 * @param @param params
	 * @param @param readTimeOut
	 * @param @param connectTimeOut
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String doPost(String url, Map<String, String> params,
			int readTimeOut, int connectTimeOut) throws Exception {
		HttpURLConnection connection = null;
		OutputStream out = null;
		String result = null;
		try {
			connection = getConnection(new URL(url), readTimeOut,
					connectTimeOut, METHOD_POST, CTYPE, null);
			
			byte[] content = new byte[0];
			String param = getUrl(params, DEFAULT_CHARSET);
			if (StrUtils.isNotEmpty(param)) {
				content = param.getBytes(DEFAULT_CHARSET);
			}
			
			out = connection.getOutputStream();
			out.write(content);	// post传送消息内容
			result = responseAsString(connection);	// 接受的返回值通过buffer来接受
			
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return result;

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
	public static String doGet(String url, Map<String, String> params,
			int readTimeOut, int connectTimeOut) {

		HttpURLConnection connection = null;
		String result=null;
		try {
			String query = getUrl(params, DEFAULT_CHARSET);
			connection = getConnection(new URL(buildUrl(url, query)),
					readTimeOut, connectTimeOut, METHOD_GET, CTYPE, null);
       result=responseAsString(connection);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
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

	// 需要再写一个getConnection的方法，把doPost和doGet的connection都抽取出来
	private static HttpURLConnection getConnection(URL url, int readTimeOut,
			int connectTimeOut, String method, String ctype,
			Map<String, String> headMap) throws Exception {
		HttpURLConnection connection = null;
		if (url == null) {
			return null;
		}
		
		if (HTTPS.equals(url.getProtocol())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance(TLS);
				ctx.init(new KeyManager[0],
						new TrustManager[] { new DefaultTrustManager() },
						new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url
					.openConnection();
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			connection = connHttps;
			
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}

		connection.setReadTimeout(readTimeOut);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod(method);
		connection.setConnectTimeout(connectTimeOut);
		connection.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		connection.setRequestProperty("User-Agent", Config.USER_AGENT);
		connection.setRequestProperty("Content-Type", ctype);
		connection.setRequestProperty("Content-Language", "zh-CN,zh;");
		
		// 遍历设置headMap
		if (null != headMap) {
			Set<Entry<String, String>> entries = headMap.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		
		connection.setRequestProperty("Referer", "http://live.bilibili.com/390480");	// 来源
		
		// cookie作为字符串时，不能直接append，每个都得加上"; " ，然后把最后一个去掉，看看chrome或Firefox的消息头就知道了。
		connection.setRequestProperty("Cookie", 
		"fts=1513650651; _cnt_notify=34; sid=6zdr4xli; DedeUserID__ckMd5=686caa22740f2663; SESSDATA=e6e4328c%2C1513821274%2Cd3b08aee; DedeUserID=1650868; LIVE_BUVID=AUTO7815136506667388; buvid3=9DCA74AB-E181-4BA0-B310-3E46E027CB1623668infoc; bili_jct=a6c32a1350e9f1592b4898846f1dbf4a; _cnt_pm=0; finger=540129ae; LIVE_PLAYER_TYPE=1");

		return connection;
	}

	
	
	/**
	 * @Title: getUrl
	 * @Description: doGet发送请求时拼接的URL
	 * @param @param params
	 * @param @param charset
	 * @param @return
	 * @param @throws UnsupportedEncodingException
	 * @return String
	 * @throws
	 */
	public static String getUrl(Map<String, String> params, String charset)
			throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer("?");
		if (params == null && params.isEmpty()) {
			return null;
		}
		// 否则的话，开始拼接需要传递的值，也就是URL?AA==BB&CC==EE这样的类似的连接值
		Set<Entry<String, String>> entries = params.entrySet();
		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			// 还需要进行一次判断是否为空，一定要谨慎
			if (StrUtils.isNotEmpty(name) && StrUtils.isNotEmpty(value)) {
				// 如果不为空的话，开始进行连接操作
				buffer.append("&").append(name).append("=")
						.append(URLEncoder.encode(value, charset));
			}

		}
		return buffer.toString().substring(0, buffer.toString().length() - 1);

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
