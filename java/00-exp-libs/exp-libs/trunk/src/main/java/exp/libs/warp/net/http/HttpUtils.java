package exp.libs.warp.net.http;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Decoder.BASE64Decoder;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.IOUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <PRE>
 * HTTP工具
 * </PRE>
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class HttpUtils extends _HttpUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(HttpUtils.class);
	
	/** 页面源码中标识编码字符集的位置正则 */
	private final static String RGX_CHARSET = "text/html;\\s*?charset=([a-zA-Z0-9\\-]+)";
	
	/** 页面使用BASE64存储的图像信息正则 */
	private final static String RGX_BASE64_IMG = "data:image/([^;]+);base64,(.*)";
	
	/** 默认下载路径 */
	private final static String DEFAULT_DOWNLOAD_DIR = "./downloads/";
	
	/** 私有化构造函数 */
	protected HttpUtils() {}
	
	/**
	 * 获取页面编码
	 * @param url
	 * @return
	 */
	public static String getPageEncoding(final String url) {
		String charset = Charset.UTF8;
		try {
			URL _URL = new URL(url);
			URLConnection conn = _URL.openConnection();
			conn.connect();
			
			charset = conn.getContentEncoding();
			charset = (CharsetUtils.isVaild(charset) ? charset : 
					RegexUtils.findFirst(conn.getContentType(), RGX_CHARSET));	
			
			if(CharsetUtils.isInvalid(charset)) {
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, Charset.ISO);
				BufferedReader buff = new BufferedReader(isr);
				String line = null;
				while ((line = buff.readLine()) != null) {
					if(line.contains("<meta ")) {
						charset = RegexUtils.findFirst(line, RGX_CHARSET);
						if(StrUtils.isNotEmpty(charset)) {
							break;
						}
					}
				}
				buff.close();
				is.close();
			}
		} catch (Exception e) {
			log.error("获取页面编码失败: {}", url, e);
			charset = Charset.UTF8;
		}
		charset = (CharsetUtils.isVaild(charset) ? charset : Charset.UTF8);
		return charset;
	}
	
	/**
	 * 获取页面源码
	 * @param url
	 * @return
	 */
	public static String getPageSource(final String url) {
		String charset = getPageEncoding(url);
		return getPageSource(url, charset);
	}
	
	/**
	 * 获取页面源码
	 * @param url
	 * @param charset 页面编码
	 * @return
	 */
	public static String getPageSource(final String url, String charset) {
		charset = CharsetUtils.isVaild(charset) ? charset : getPageEncoding(charset);
		StringBuffer pageSource = new StringBuffer();
		try {
			URL _URL = new URL(url);
			URLConnection conn = _URL.openConnection();
			conn.connect();
			
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, charset);
			BufferedReader buff = new BufferedReader(isr);
			String line = null;
			while ((line = buff.readLine()) != null) {
				pageSource.append(line).append("\r\n");
			}
			buff.close();
			is.close();
			
		} catch (Exception e) {
			log.error("获取页面源码失败: {}", url, e);
		}
		return pageSource.toString();
	}
	
	/**
	 * 下载资源
	 * @param url 资源位置
	 * @param savePath 本地保存位置（包括文件名）
	 * @return
	 */
	public static boolean download(String url, String savePath) {
		boolean isOk = false;
		savePath = StrUtils.isNotEmpty(savePath) ? savePath : 
			StrUtils.concat(DEFAULT_DOWNLOAD_DIR, System.currentTimeMillis());
		
		HttpClient client = createHttpClient();
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
	
	/**
	 * 保存Base64编码的图片数据到本地
	 * @param imgUrl 图片编码地址，格式形如   data:image/png;base64,base64编码的图片数据
	 * @param saveDir 希望保存的图片目录
	 * @param imgName 希望保存的图片名称（不含后缀，后缀通过编码自动解析）
	 * @return true:保存成功; false:保存失败
	 */
	public static boolean convertBase64Img(String imgUrl, 
			String saveDir, String imgName) {
		boolean isOk = false;
		Pattern ptn = Pattern.compile(RGX_BASE64_IMG);  
        Matcher mth = ptn.matcher(imgUrl);      
        if(mth.find()) {
        	String ext = mth.group(1);	// 图片后缀
        	String base64Data = mth.group(2);	// 图片数据
            String savePath = StrUtils.concat(saveDir, "/", imgName, ".", ext);
            
            try {
            	BASE64Decoder decoder = new BASE64Decoder();
            	byte[] bytes = decoder.decodeBuffer(base64Data);  
                FileUtils.writeByteArrayToFile(new File(savePath), bytes, false);
                isOk = true;
                
            } catch (Exception e) {  
                e.printStackTrace();  
            }         
        }     
        return isOk;  
    }  
	
}