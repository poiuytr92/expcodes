package exp.libs.warp.net.wsdl.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlLoader;

import exp.libs.envm.Regex;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <pre>
 * wsdl客户端加载器.
 * 	用于获取wsdl地址上xml文本的文件输入流(如果是文件地址，则把文件转换成文件输入流)
 * </pre>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-06-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _WsdlLoader extends WsdlLoader {

	/** 终止状�? */
	private boolean isAborted;
	
	/** HTTP客户�? */
	private HttpClient client;
	
	/**
	 * 构造函�?
	 * @param wsdlURL wsdl地址, 支持格式: 
	 * 		http://127.0.0.1:8080/services/myService?wsdl
	 * 		E:\ManagedElementRetrievalHttp.wsdl
	 * 		file:///E:/ManagedElementRetrievalHttp.wsdl 
	 */
	protected _WsdlLoader(String wsdlURL) {
		super(wsdlURL);
		this.isAborted = false;
	}

	@Override
	public void close() {
		isAborted = true;
		
		HttpUtils.close(client);
		client = null;
	}

	@Override
	public boolean abort() {
		isAborted = true;	// 强制终止
		return true;
	}

	@Override
	public boolean isAborted() {
		return isAborted;
	}

	/**
     * <pre>
     * 加载wsdl文件，返回文件流.
     * 	如果是本地文件，则需要全量的依赖文件（含wsdl和xsd文件�?
     * </pre>
     * @param wsdlURL
     * @return InputStream
     */
    @Override
    public InputStream load(String wsdlURL) throws HttpException, IOException {
    	InputStream is = null;
    	
    	// wsdlURL格式形如:  http://127.0.0.1:8080/services/myService?wsdl
        if(RegexUtils.matches(wsdlURL, Regex.HTTP.VAL)) {
        	
        	if(client == null) {
        		client = HttpUtils.createHttpClient();
        	}
        	GetMethod get = new GetMethod(wsdlURL);
        	get.setDoAuthentication(true);
        	
        	try {
        		int httpStatus = client.executeMethod(get);
        		if(HttpUtils.isResponseOK(httpStatus)) {
        			is = new ByteArrayInputStream(get.getResponseBody());
        		}
        	} finally {
        		get.releaseConnection();
        	}
        	
        // wsdlURL格式形如:  file:///E:/ManagedElementRetrievalHttp.wsdl
        } else if(wsdlURL.startsWith("file")) {
        	is = new URL(wsdlURL).openStream();
        	
        // wsdlURL格式形如:  E:\ManagedElementRetrievalHttp.wsdl
        } else {
        	is = new URL("file:///".concat(wsdlURL)).openStream();
        }
        return is;
    }
	
}
