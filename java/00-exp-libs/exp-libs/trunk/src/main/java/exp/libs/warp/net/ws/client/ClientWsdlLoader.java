package exp.libs.warp.net.ws.client;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlLoader;

/**
 * 获取wsdl地址上xml文本的文件输入流
 * 如果是文件地址，则把文件转换成文件输入流
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ClientWsdlLoader extends WsdlLoader {

	/** 终止状态  */
    private boolean isAborted = false;
    
    /** HTTP客户端  */
    private HttpClient httpClient = null;
    
    /** 在httpClient中用getMethod下载网页  */
    private GetMethod httpGetMethod = null;
    /**
     * 
     * 构造方法
     * @param url
     * @param httpClient
     */
    public ClientWsdlLoader(String url, HttpClient httpClient) {
        super(url);
        this.httpClient = httpClient;
    }

    /**
     * 加载wsdl文件，返回文件流，如果是本地文件，则需要全量的依赖文件
     * 												（wsdl和xsd文件）
     * @param url
     * @return InputStream
     */
    @Override
    public InputStream load(String url) throws HttpException, IOException {
//    	System.out.println("load :" + url);
       
    	/* 如果是本地wsdl文件，则返回文本流  */
        if(url.startsWith("file")) {
            return new URL(url) .openStream();
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
        	httpGetMethod = new GetMethod(url);
        	httpGetMethod.setDoAuthentication(true);
        	
        	try {
        		int result = httpClient.executeMethod(httpGetMethod);
        		
        		if(result != HttpStatus.SC_OK) {
        			if(result < 200 || result > 299) {
        				throw new HttpException("返回HTTP 状态编码HttpStatus：" + result + "；WSDL地址：" + url);
        			} else {
        				//TODO 打印日志或记录?
        			}
        		}
        		return new ByteArrayInputStream(httpGetMethod.getResponseBody());
        	} finally {
        		httpGetMethod.releaseConnection();
        	}
        } else {
        	return new URL("file:///".concat(url)).openStream();
        }

    }

    @Override
    public boolean abort() {
    	//使终止
        isAborted = true;
        return true;
    }

    @Override
    public boolean isAborted() {
        return isAborted;
    }

    @Override
    public void close() {  
//    	httpGetMethod.releaseConnection();
    }
    
    public static void main(String[] args) throws MalformedURLException, IOException {
    	String local = "file:///E:/Workspace_eclipse/WSClientUtils/conf/ManagedElementRetrievalHttp.wsdl";
    	new URL(local) .openStream();
    	System.out.println("success");
    	
	}
}
