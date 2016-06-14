package exp.libs.utils.net;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import exp.libs.utils.pub.IOUtils;
import exp.libs.utils.pub.StrUtils;

/**
 * 说明
 * 利用httpclient下载文件
 * maven依赖
 * <dependency>
*			<groupId>org.apache.httpcomponents</groupId>
*			<artifactId>httpclient</artifactId>
*			<version>4.0.1</version>
*		</dependency>
*  可下载http文件、图片、压缩文件
*  bug：获取response header中Content-Disposition中filename中文乱码问题
 * @author tanjundong
 *
 */
public class HttpDownload {

	private final static String DEFAULT_DOWNLOAD_DIR = "./downloads/";
	
	public static boolean download(String url) {
		return download(url, null);
	}

	public static boolean download(String url, String savePath) {
		HttpClient client = new DefaultHttpClient();
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = client.execute(httpGet);
			outHeaders(response);

			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			if(StrUtils.isEmpty(savePath)) {
				savePath = getFilePath(response);
			}
			IOUtils.toFile(is, savePath);

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			client.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
		}
		return true;
	}
	
	public static String getFilePath(HttpResponse response) {
		String filepath = DEFAULT_DOWNLOAD_DIR;
		String filename = getFileName(response);

		if (filename != null) {
			filepath += filename;
		} else {
			filepath += String.valueOf(System.currentTimeMillis());
		}
		return filepath;
	}
	
	/**
	 * 获取response header中Content-Disposition中的filename值
	 * @param response
	 * @return
	 */
	public static String getFileName(HttpResponse response) {
		String fileName = "";
		Header contentHeader = response.getFirstHeader("Content-Disposition");
		if (contentHeader != null) {
			HeaderElement[] values = contentHeader.getElements();
			if (values.length >= 1) {
				NameValuePair param = values[0].getParameterByName("filename");
				if (param != null) {
					fileName = param.getValue();
				}
			}
		}
		return fileName;
	}
	
	public static void outHeaders(HttpResponse response) {
		Header[] headers = response.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers[i]);
		}
	}
	
	public static void main(String[] args) {
//		String url = "http://bbs.btwuji.com/job.php?action=download&pid=tpc&tid=320678&aid=216617";
		String url="http://b258.photo.store.qq.com/psb?/V11SsIrh3FLupS/xWhpOWiy6POvNCdhUgL.hqgVZVWcA8Cp.xMgejoMCGg!/b/dAIBAAAAAAAA&amp;bo=FQIgAwAAAAAFABc!&amp;w=392&amp;h=5000";
//		String filepath = "D:\\test\\a.torrent";
		String filepath = "";
		HttpDownload.download(url, filepath);
	}
}
