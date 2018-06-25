package exp.libs.warp.net.wsdl.client;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.iface.Operation;

import exp.libs.envm.HttpHead;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * Websevices客户端工具
 * </PRE>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-06-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WsdlUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(WsdlUtils.class);
	
	/** 连接超时, 默认1分钟 */
	public final static int CONN_TIMEOUT = HttpUtils.CONN_TIMEOUT;

	/** 响应/读取超时 , 默认1分钟 */
	public final static int CALL_TIMEOUT = HttpUtils.CALL_TIMEOUT;
	
	/** 默认编码 */
	public final static String DEFAULT_CHARSET = HttpUtils.DEFAULT_CHARSET;
	
	/** 缓存区大�? */
	private final static int CACHE_SIZE = 256;
	
	/** wsdl接口的方法集缓存(可加速访�?) */
	private Map<String, WsdlInterface[]> wsdlCache;
	
	/** 单例 */
	private static volatile WsdlUtils instance;
	
	/**
	 * 构造函�?
	 */
	private WsdlUtils() {
		this.wsdlCache = new HashMap<String, WsdlInterface[]>();
	}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	private static WsdlUtils getInstn() {
		if(instance == null) {
			synchronized (WsdlUtils.class) {
				if(instance == null) {
					instance = new WsdlUtils();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 创建用于请求wsdl的soap格式的XML报文模板(无参)
	 * @param wsdlURL wsdl地址, 支持格式: 
	 * 		http://127.0.0.1:8080/services/myService?wsdl
	 * 		E:\ManagedElementRetrievalHttp.wsdl
	 * 		file:///E:/ManagedElementRetrievalHttp.wsdl 
	 * 
	 * @param method 调用方法�?
	 * @return requestXmlTpl 用于请求wsdl的soup格式的XML报文 (无参模板)
	 */
	public static String getRequestXmlTpl(String wsdlURL, String method) {
		return getInstn()._getRequestXmlTpl(wsdlURL, method);
	}
	
	/**
	 * 创建用于请求wsdl的soap格式的XML报文模板(无参)
	 * @param wsdlURL wsdl地址, 支持格式: 
	 * 		http://127.0.0.1:8080/services/myService?wsdl
	 * 		E:\ManagedElementRetrievalHttp.wsdl
	 * 		file:///E:/ManagedElementRetrievalHttp.wsdl 
	 * 
	 * @param method 调用方法�?
	 * @return requestXmlTpl 用于请求wsdl的soup格式的XML报文 (无参模板)
	 */
	private String _getRequestXmlTpl(String wsdlURL, String method) {
		String requestXmlTpl = "";
		try {
			requestXmlTpl = __getRequestXmlTpl(wsdlURL, method);
			
		} catch(Exception e) {
			log.error("获取WebService的xml请求报文模板失败.\r\nwsdl:{}\r\nmethod:{}", 
					wsdlURL, method, e);
		}
		return requestXmlTpl;
	}
	
	/**
	 * 创建用于请求wsdl的soap格式的XML报文模板(无参)
	 * @param wsdlURL wsdl地址, 支持格式: 
	 * 		http://127.0.0.1:8080/services/myService?wsdl
	 * 		E:\ManagedElementRetrievalHttp.wsdl
	 * 		file:///E:/ManagedElementRetrievalHttp.wsdl 
	 * 
	 * @param method 调用方法�?
	 * @return requestXmlTpl 用于请求wsdl的soup格式的XML报文 (无参模板)
	 * @throws Exception 
	 */
	private String __getRequestXmlTpl(String wsdlURL, String method) throws Exception {
		WsdlInterface[] wsdlInterfaces = wsdlCache.get(wsdlURL);
		if (wsdlInterfaces == null) {
			WsdlProject wsdlProject = new WsdlProject();
			_WsdlLoader wsdlLoader = new _WsdlLoader(wsdlURL);
			wsdlInterfaces = wsdlProject.importWsdl(wsdlURL, true, wsdlLoader);
			wsdlLoader.close();
			
			if(wsdlCache.size() >= CACHE_SIZE) {
				wsdlCache.clear();
			}
			wsdlCache.put(wsdlURL, wsdlInterfaces);
		}

		Operation wsdlOperation = null;
		for (WsdlInterface wsdlInterface : wsdlInterfaces) {
			wsdlOperation = wsdlInterface.getOperationByName(method);
			if(wsdlOperation != null) {
				break;
			}
		}

		// 获取xml请求报文 (无参模板)
		String requestXmlTpl = wsdlOperation.getRequestAt(0).getRequestContent();
		
		// 由于JDK1.6不支持soup1.2协议, 为了向下兼容, 调整xml报文的消息头
		String soapVersion = ((WsdlInterface) wsdlOperation.getInterface())
				.getSoapVersion().toString();
		if (StrUtils.isNotEmpty(soapVersion) && 
				_SOAP._1_2.VER().equals(soapVersion)) {
			requestXmlTpl = requestXmlTpl.replace(
					_SOAP._1_2.STATEMENT(), _SOAP._1_1.STATEMENT());
		}
		return requestXmlTpl;
	}
	
	/**
	 * 按顺序设置参数到xml请求报文模板
	 * @param requestXmlTpl xml请求报文模板
	 * @param params 参数�?
	 * @return requestXml 设置参数后的请求报文
	 */
	public static String setParams(String requestXmlTpl, String... params) {
		return getInstn()._setParams(requestXmlTpl, params);
	}
	
	/**
	 * 按顺序设置参数到xml请求报文模板
	 * @param requestXmlTpl xml请求报文模板
	 * @param params 参数�?
	 * @return requestXml 设置参数后的请求报文
	 */
	private String _setParams(String requestXmlTpl, String... params) {
		return (params == null ? requestXmlTpl : 
			_setParams(requestXmlTpl, Arrays.asList(params)));
	}
	
	/**
	 * 按顺序设置参数到xml请求报文模板
	 * @param requestXmlTpl xml请求报文模板
	 * @param params 参数�?
	 * @return requestXml 设置参数后的请求报文
	 */
	public static String setParams(String requestXmlTpl, List<String> params) {
		return getInstn()._setParams(requestXmlTpl, params);
	}
	
	/**
	 * 按顺序设置参数到xml请求报文模板
	 * @param requestXmlTpl xml请求报文模板
	 * @param params 参数�?
	 * @return requestXml 设置参数后的请求报文
	 */
	private String _setParams(String requestXmlTpl, List<String> params) {
		String requestXml = requestXmlTpl;
		if(params != null && !params.isEmpty()) {
			for(String param : params) {
				requestXml.replaceFirst("\\?", param);
			}
		}
		return requestXml;
	}
	
	/**
	 * 按键值设置参数到xml请求报文模板
	 * @param requestXmlTpl xml请求报文模板
	 * @param params 参数�?
	 * @return requestXml 设置参数后的请求报文
	 */
	public static String setParams(String requestXmlTpl, Map<String, String> params) {
		return getInstn()._setParams(requestXmlTpl, params);
	}
	
	/**
	 * 按键值设置参数到xml请求报文模板
	 * @param requestXmlTpl xml请求报文模板
	 * @param params 参数�?
	 * @return requestXml 设置参数后的请求报文
	 */
	private String _setParams(String requestXmlTpl, Map<String, String> params) {
		String requestXml = requestXmlTpl;
		if(params != null && !params.isEmpty()) {
			Iterator<String> keys = params.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				String _old = StrUtils.concat("<", key, ">?</", key, ">");
				String _new = StrUtils.concat("<", key, ">", params.get(key), "</", key, ">");
				requestXml = requestXml.replace(_old, _new);
			}
		}
		return requestXml;
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @return responseXml xml响应报文
	 */
	public static String doCall(String wsdlURL, String method,
			String... requestParams) {
		List<String> requestParamList = (requestParams != null ? 
				Arrays.asList(requestParams) : new LinkedList<String>());
		return doCall(wsdlURL, method, requestParamList, true);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @return responseXml xml响应报文
	 */
	public static String doCall(String wsdlURL, String method,
			List<String> requestParams) {
		return doCall(wsdlURL, method, requestParams, true);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @return responseXml xml响应报文
	 */
	public static String doCall(String wsdlURL, String method,
			List<String> requestParams, boolean header) {
		return doCall(wsdlURL, method, requestParams, 
				header, CONN_TIMEOUT, CALL_TIMEOUT, DEFAULT_CHARSET);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 请求超时(ms)
	 * @param charset 字符集编�?
	 * @return responseXml xml响应报文
	 */
	public static String doCall(String wsdlURL, String method,
			List<String> requestParams, 
			boolean header, int connTimeout, int callTimeout, String charset) {
		return getInstn()._doCall(wsdlURL, method, requestParams, 
				header, connTimeout, callTimeout, charset);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 请求超时(ms)
	 * @param charset 字符集编�?
	 * @return responseXml xml响应报文
	 */
	private String _doCall(String wsdlURL, String method,
			List<String> requestParams, 
			boolean header, int connTimeout, int callTimeout, String charset) {
		String requestXmlTpl = getRequestXmlTpl(wsdlURL, method);
		String requestXml = setParams(requestXmlTpl, requestParams);
		return __doCall(wsdlURL, method, requestXml, header, connTimeout, callTimeout, charset);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @return responseXml xml响应报文
	 */
	public static String doCall(String wsdlURL, String method, 
			Map<String, String> requestParams) {
		return doCall(wsdlURL, method, requestParams, true);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @return responseXml xml响应报文
	 */
	public static String doCall(String wsdlURL, String method, 
			Map<String, String> requestParams, boolean header) {
		return doCall(wsdlURL, method, requestParams, 
				header, CONN_TIMEOUT, CALL_TIMEOUT, DEFAULT_CHARSET);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 请求超时(ms)
	 * @param charset 字符集编�?
	 * @return responseXml xml响应报文
	 */
	public static String doCall(String wsdlURL, String method, 
			Map<String, String> requestParams, 
			boolean header, int connTimeout, int callTimeout, String charset) {
		return getInstn()._doCall(wsdlURL, method, requestParams, 
				header, connTimeout, callTimeout, charset);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 请求超时(ms)
	 * @param charset 字符集编�?
	 * @return responseXml xml响应报文
	 */
	private String _doCall(String wsdlURL, String method, 
			Map<String, String> requestParams, 
			boolean header, int connTimeout, int callTimeout, String charset) {
		String requestXmlTpl = getRequestXmlTpl(wsdlURL, method);
		String requestXml = setParams(requestXmlTpl, requestParams);
		return __doCall(wsdlURL, method, requestXml, header, connTimeout, callTimeout, charset);
	}
	
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestXml xml请求报文
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 请求超时(ms)
	 * @param charset 字符集编�?
	 * @return responseXml xml响应报文
	 */
	private String __doCall(String wsdlURL, String method,
			String requestXml, boolean header, int connTimeout,
			int callTimeout, String charset) {
		String responseXml = "";
		try {
			responseXml = __doCall__(wsdlURL, method, requestXml, 
					header, connTimeout, callTimeout, charset);
			
		} catch (Exception e) {
			log.error("调用WebService接口失败.\r\nwsdl:{}\r\nmethod:{}\r\nrequest:{}", 
					wsdlURL, method, requestXml, e);
		}
		return responseXml;
	}
			
	/**
	 * 调用webservices接口(通过HTTP协议)
	 * 
	 * @param wsdlURL wsdl地址, 支持格式:
	 * 		http://172.168.10.7:8686/services/myService?wsdl
	 * 		http://172.168.10.7:8686/services/myService
	 * @param method 调用方法�?
	 * @param requestXml xml请求报文
	 * @param header 是否需要带头参�?  SOAPAction:method
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 请求超时(ms)
	 * @param charset 字符集编�?
	 * @return responseXml xml响应报文
	 */
	private String __doCall__(String wsdlURL, String method,
			String requestXml, boolean header, int connTimeout,
			int callTimeout, String charset) throws Exception {
		final String CONTENT_TYPE = HttpHead.VAL.GET_TXT.concat(charset);
		wsdlURL = wsdlURL.replaceFirst("(?i)\\?wsdl$", "");
		String responseXml = "";
		
		HttpClient httpClient = HttpUtils.createHttpClient(connTimeout, callTimeout);
		PostMethod postMethod = new PostMethod(wsdlURL);
		try {
			if(header == true) {
				postMethod.setRequestHeader("SOAPAction", method);
			}
			
			postMethod.setRequestEntity(new InputStreamRequestEntity(
					new ByteArrayInputStream(requestXml.getBytes(charset)), CONTENT_TYPE));
			httpClient.executeMethod(postMethod);
			responseXml = postMethod.getResponseBodyAsString();
			
		} finally {
			postMethod.releaseConnection();
			HttpUtils.close(httpClient);
		}
		return responseXml;
	}
	
	/**
	 * <pre>
	 * 调用webservices服务(axis2框架).
	 * 此方式为应用RPC的远程调用， 即通过url定位告诉远程服务器方法名称、参数等，调用远程服务，得到结果�?
	 *  <b>注：若所调用的服务参数表非默认值（默认值为 arg0~argN�?, 则此方法无法使用.</b>
	 * </pre>
	 * @param wsdlURL wsdl地址
	 * @param namespace 命名空间: 浏览器打开wsdlURL后在 <wsdl:definitions> 中的 targetNamespace 的�?
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?: new Object[] { paramA, paramB, ... };
	 * @param resopnseType 服务器响应数组的值类�?(若无范围值该参数应设null): new Class[] { A.class, B.class, ...};
	 * @return Object[] 响应结果�?
	 */
	public static Object[] doCallAxis2(String wsdlURL, String namespace,
			String method, Object[] requestParams, Class<?>[] resopnseType) {
		return doCallAxis2(wsdlURL, namespace, method, 
				requestParams, resopnseType, CALL_TIMEOUT);
	}
	
	/**
	 * <pre>
	 * 调用webservices服务(axis2框架).
	 * 此方式为应用RPC的远程调用， 即通过url定位告诉远程服务器方法名称、参数等，调用远程服务，得到结果�?
	 *  <b>注：若所调用的服务参数表非默认值（默认值为 arg0~argN�?, 则此方法无法使用.</b>
	 * </pre>
	 * @param wsdlURL wsdl地址
	 * @param namespace 命名空间: 浏览器打开wsdlURL后在 <wsdl:definitions> 中的 targetNamespace 的�?
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?: new Object[] { paramA, paramB, ... };
	 * @param resopnseType 服务器响应数组的值类�?(若无范围值该参数应设null): new Class[] { A.class, B.class, ...};
	 * @param callTimeout 响应超时(ms)
	 * @return Object[] 响应结果�?
	 */
	public static Object[] doCallAxis2(String wsdlURL, String namespace,
			String method, Object[] requestParams, 
			Class<?>[] resopnseType, int callTimeout) {
		return getInstn()._doCallAxis2(wsdlURL, namespace, method, 
				requestParams, resopnseType, callTimeout);
	}
	
	/**
	 * <pre>
	 * 调用webservices服务(axis2框架).
	 *  注：若所调用的服务参数表非默认值（默认值为 arg0~argN�?, 则此方法无法使用.
	 * </pre>
	 * @param wsdlURL wsdl地址
	 * @param namespace 命名空间: 浏览器打开wsdlURL后在 <wsdl:definitions> 中的 targetNamespace 的�?
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?: new Object[] { paramA, paramB, ... };
	 * @param resopnseType 服务器响应数组的值类�?(若无范围值该参数应设null): new Class[] { A.class, B.class, ...};
	 * @param callTimeout 响应超时(ms)
	 * @return Object[] 响应结果�?
	 */
	private Object[] _doCallAxis2(String wsdlURL, String namespace,
			String method, Object[] requestParams, 
			Class<?>[] resopnseType, int callTimeout) {
		Object[] responses = new Object[0];
		try {
			responses = __doCallAxis2(wsdlURL, namespace, method, requestParams, 
					resopnseType, callTimeout);
			
		} catch(Exception e) {
			log.error("调用(Axis2)WebService服务失败.\r\nwsdl:{}\r\nnamespace:{}\r\nmethod:{}", 
					wsdlURL, namespace, method, e);
		}
		return responses;
	}

	/**
	 * <pre>
	 * 调用webservices服务(axis2框架).
	 *  注：若所调用的服务参数表非默认值（默认值为 arg0~argN�?, 则此方法无法使用.
	 * </pre>
	 * @param wsdlURL wsdl地址
	 * @param namespace 命名空间: 浏览器打开wsdlURL后在 <wsdl:definitions> 中的 targetNamespace 的�?
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?: new Object[] { paramA, paramB, ... };
	 * @param resopnseType 服务器响应数组的值类�?(若无范围值该参数应设null): new Class[] { A.class, B.class, ...};
	 * @param callTimeout 响应超时(ms)
	 * @return Object[] 响应结果�?
	 * @throws Exception
	 */
	private Object[] __doCallAxis2(String wsdlURL, String namespace,
			String method, Object[] requestParams, 
			Class<?>[] resopnseType, int callTimeout) throws Exception {
		RPCServiceClient rpc = new RPCServiceClient();
		Options options = rpc.getOptions();

		EndpointReference targetEPR = new EndpointReference(wsdlURL);
		options.setTimeOutInMilliSeconds(callTimeout);
		options.setTo(targetEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		options.setProperty(HTTPConstants.CHUNKED, "false"); // 设置不受限制

		QName requestMethod = new QName(namespace, method);
		Object[] responses = new Object[0];
		
		// ws服务有返回�?
		if(resopnseType != null && resopnseType.length > 0) {
			responses = rpc.invokeBlocking(requestMethod, requestParams, resopnseType);
			
		// ws服务无返回�?
		} else {
			rpc.invokeRobust(requestMethod, requestParams);
		}
		return responses;
	}
	
	/**
	 * 调用webservices服务(CXF框架).
	 * 
	 * @param wsdlURL wsdl地址
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @return Object[] 响应结果�?
	 */
	public static Object[] doCallCXF(String wsdlURL, String method, 
			Object... requestParams) {
		return getInstn()._doCallCXF(wsdlURL, method, 
				requestParams, CONN_TIMEOUT, CALL_TIMEOUT);
	}
	
	/**
	 * 调用webservices服务(CXF框架).
	 * 
	 * @param wsdlURL wsdl地址
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 响应超时(ms)
	 * @return Object[] 响应结果�?
	 */
	public static Object[] doCallCXF(String wsdlURL, String method, 
			Object[] requestParams, int connTimeout, int callTimeout) {
		return getInstn()._doCallCXF(wsdlURL, method, 
				requestParams, connTimeout, callTimeout);
	}
	
	/**
	 * 调用webservices服务(CXF框架).
	 * 
	 * @param wsdlURL wsdl地址
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 响应超时(ms)
	 * @return Object[] 响应结果�?
	 */
	private Object[] _doCallCXF(String wsdlURL, String method, 
			Object[] requestParams, int connTimeout, int callTimeout) {
		Object[] responses = new Object[0];
		try {
			responses = __doCallCXF(wsdlURL, method, requestParams, 
					connTimeout, callTimeout);
			
		} catch(Exception e) {
			log.error("调用(CXF)WebService服务失败.\r\nwsdl:{}\r\nmethod:{}", 
					wsdlURL, method, e);
		}
		return responses;
	}
	
	/**
	 * 调用webservices服务(CXF框架).
	 * 
	 * @param wsdlURL wsdl地址
	 * @param method 调用方法�?
	 * @param requestParams 请求参数�?
	 * @param connTimeout 连接超时(ms)
	 * @param callTimeout 响应超时(ms)
	 * @return Object[] 响应结果�?
	 * @throws Exception
	 */
	private Object[] __doCallCXF(String wsdlURL, String method, 
			Object[] requestParams, int connTimeout, int callTimeout) 
			throws Exception {
		
		JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
		Client client = factory.createClient(wsdlURL);
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(connTimeout);
		policy.setReceiveTimeout(callTimeout);
		conduit.setClient(policy);
		return client.invoke(method, requestParams);
	}
	
}
