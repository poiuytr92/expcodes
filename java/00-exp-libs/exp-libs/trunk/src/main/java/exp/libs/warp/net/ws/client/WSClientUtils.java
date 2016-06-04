package exp.libs.warp.net.ws.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.xmlbeans.XmlException;
import org.dom4j.DocumentException;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.support.SoapUIException;

import exp.libs.utils.net.HttpUtils;
import exp.libs.utils.pub.StrUtils;

/**
 * <PRE>
 * Websevices客户端工具类
 * </PRE>
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-09
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WSClientUtils {

	/** 连接超时,默认1分钟 */
	public static final int CONN_TIMEOUT = 60000;

	/** 响应超时 ,默认10分钟 */
	public static final int CALL_TIMEOUT = 600000;

	/** soap协议版本，默认1.2 */
	public final static String SOAP_VERSION_11 = "SOAP 1.1";

	/** soap协议版本，默认1.2 */
	public final static String SOAP_VERSION_12 = "SOAP 1.2";

	/** 字符编码，默认UTF8 */
	public final static String CHARSET = "UTF-8";
	
	/**
	 * contentType: 其中的charset默认为 ISO-8859-1.
	 * 服务端响应时可能会利用contentType的charset作为编码，若不指定则可能引起中文乱码.
	 */
	private final static String CONTENT_TYPE = "text/xml;charset=" + CHARSET;

	/** sopui的WsdlInterface接口实例集合，以wsdlUrl为key */
	private static Map<String, WsdlInterface[]> wsdls = 
			new LRULinkedHashMap<String, WsdlInterface[]>(256);
			
	/**
	 * <PRE>
	 * 创建soapUI使用的request（发送）XML字符串，参考soapUI工具中的XML窗口
	 * 支持wsdlUrl格式：file:///E:/ManagedElementRetrievalHttp.wsdl 
	 * 				E:\ManagedElementRetrievalHttp.wsdl				
	 * 				http://172.168.10.7:8686/services/myService?wsdl
	 * 该方法要加锁，否则会有SoapUI抛出org.apache.xmlbeans.XmlException
	 * </PRE>
	 * 
	 * @param wsdlUrl
	 *            wsdlUrl 地址
	 * @param method
	 *            调用方法名
	 * @return request（发送）XML字符串
	 * @throws XmlException
	 * @throws IOException
	 */
	public static String createRequestXmlStr(String wsdlUrl, String method)
			throws SoapUIException, XmlException, IOException {
		return createSendXMLString(wsdlUrl, method);
	}

	/**
	 * <PRE>
	 * 创建soapUI使用的request（发送）XML字符串，参考soapUI工具中的XML窗口
	 * 支持wsdlUrl格式：file:///E:/ManagedElementRetrievalHttp.wsdl 
	 * 				E:\ManagedElementRetrievalHttp.wsdl				
	 * 				http://172.168.10.7:8686/services/myService?wsdl
	 * 该方法要加锁，否则会有SoapUI抛出org.apache.xmlbeans.XmlException
	 * </PRE>
	 * 
	 * @param wsdlUrl
	 *            wsdlUrl 地址
	 * @param method
	 *            调用方法名
	 * @return request（发送）XML字符串
	 * @throws XmlException
	 * @throws IOException
	 */
	@Deprecated
	public synchronized static String createSendXMLString(String wsdlUrl,
			String method) throws SoapUIException, XmlException, IOException {
		Operation operationInst = null;
		WsdlInterface[] wsdlInterfaces = null;
		String requestXMLString = null;

		wsdlInterfaces = wsdls.get(wsdlUrl);
		if (wsdlInterfaces == null) {
			WsdlProject wsdlProject = new WsdlProject();
			ClientWsdlLoader cwl = new ClientWsdlLoader(wsdlUrl,
					new HttpClient());
			wsdlInterfaces = wsdlProject.importWsdl(wsdlUrl, true, cwl);
			wsdls.put(wsdlUrl, wsdlInterfaces);
		}

		for (WsdlInterface wsdlInterface : wsdlInterfaces) {
			operationInst = wsdlInterface.getOperationByName(method);
		}

		requestXMLString = operationInst.getRequestAt(0).getRequestContent();
		
		// 根据不同的soap消息版本，调整消息头
		// soap 1.1-->xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"
		// soap 1.2-->xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"
		String soapVersion = ((WsdlInterface) operationInst.getInterface())
				.getSoapVersion().toString();

		if (StrUtils.isNotEmpty(soapVersion)
				&& SOAP_VERSION_12.equals(soapVersion)) {
			requestXMLString = requestXMLString.replace(
					"xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"",
					"xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");
		}
		return requestXMLString;
	}

	/**
	 * 替换request（发送）XML字符串中的问号为入参；
	 * 
	 * @param requestXMLString
	 *            request（发送）XML字符串
	 * @param params
	 *            方法入参 顺序list集合
	 * @return request（发送）XML字符串（带参数）
	 * @throws DocumentException
	 */
	public static String addParamsToRequestXMLString(String requestXMLString,
			List<String> params) {
		int size = params.size();

		for (int i = 0; i < size; i++) {
			requestXMLString = requestXMLString.replaceFirst("[?]",
					params.get(i));
		}
		return requestXMLString;
	}

	/**
	 * 替换request（发送）XML字符串中的问号为入参；
	 * 
	 * @param requestXMLString
	 *            request（发送）XML字符串
	 * @param params
	 *            方法入参 key Value Map集合
	 * @return request（发送）XML字符串（带参数）
	 * @throws DocumentException
	 */
	public static String addParamsToRequestXMLString(String requestXMLString,
			Map<String, String> params) {

		String oldChar = "";
		String newChar = "";

		Set<String> set = params.keySet();
		Iterator<String> it = set.iterator();

		while (it.hasNext()) {
			String key = it.next();
			oldChar = "<" + key + ">?</" + key + ">";
			// newChar = "<" + key + ">" + params.get(key) + "</" + key + ">";
			newChar = oldChar.replace("?", params.get(key));
			requestXMLString = requestXMLString.replace(oldChar, newChar);
		}

		return requestXMLString;
	}

	/**
	 * <PRE>
	 * 调用webservices接口，获取返回的XML字符串
	 * 支持wsdlUrl格式：http://172.168.10.7:8686/services/myService?wsdl
	 * </PRE>
	 * 
	 * @param wsdlUrl
	 *            wsdlUrl 地址
	 * @param method
	 *            调用方法名
	 * @param requestXMLString
	 *            request（发送）XML字符串
	 * @return response（返回）XML字符串（带参数）
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String callSendMethod(String wsdlUrl, String method,
			String requestXMLString) throws HttpException, IOException {
		return callSendMethod(wsdlUrl, method, requestXMLString, true);
	}

	/**
	 * <PRE>
	 * 调用webservices接口，获取返回的XML字符串
	 * 支持wsdlUrl格式：http://172.168.10.7:8686/services/myService?wsdl
	 * </PRE>
	 * 
	 * @param wsdlUrl
	 *            wsdlUrl 地址
	 * @param method
	 *            调用方法名
	 * @param requestXMLString
	 *            request（发送）XML字符串
	 * @param header
	 *            是否需要带头文件
	 * @return response（返回）XML字符串（带参数）
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String callSendMethod(String wsdlUrl, String method,
			String requestXMLString, boolean header) throws HttpException,
			IOException {
		return callSendMethod(wsdlUrl, method, requestXMLString, header,
				CONN_TIMEOUT, CALL_TIMEOUT);
	}

	/**
	 * <PRE>
	 * 调用webservices接口，获取返回的XML字符串
	 * 支持wsdlUrl格式：http://172.168.10.7:8686/services/myService?wsdl
	 * </PRE>
	 * 
	 * @param wsdlUrl
	 *            wsdlUrl 地址
	 * @param method
	 *            调用方法名
	 * @param requestXMLString
	 *            request（发送）XML字符串
	 * @param header
	 *            是否需要带头文件
	 * @param connTimeOut
	 *            请求超时
	 * @param soTimeOut
	 *            响应超时
	 * @return response（返回）XML字符串（带参数）
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String callSendMethod(String wsdlUrl, String method,
			String requestXMLString, boolean header, int connTimeOut,
			int soTimeOut) throws HttpException, IOException {
		String responseXMLString = null;
		if (wsdlUrl.endsWith("?wsdl")) {
			wsdlUrl = wsdlUrl.replace("?wsdl", "");
		}
		PostMethod postMethod = new PostMethod(wsdlUrl);
		try {
			if (header) {
				postMethod.setRequestHeader("SOAPAction", method);
			}
			postMethod.setRequestEntity(new InputStreamRequestEntity(
					new ByteArrayInputStream(requestXMLString.getBytes(CHARSET)), 
					CONTENT_TYPE));
			HttpUtils.createHttpClient(connTimeOut, soTimeOut).executeMethod(postMethod);
			responseXMLString = postMethod.getResponseBodyAsString();
		} finally {
			postMethod.releaseConnection();
		}
		return responseXMLString;
	}

	/**
	 * 调用webservices接口，获取返回的XML字符串
	 * 
	 * @param wsdlUrl
	 *            wsdl地址
	 * @param method
	 *            调用方法名
	 * @param params
	 *            方法入参 顺序List
	 * @return response（返回）XML字符串（带参数）
	 * @throws IOException
	 * @throws XmlException
	 * @throws SoapUIException
	 */
	public static String callSendMethod(String wsdlUrl, String method,
			List<String> params) throws SoapUIException, XmlException,
			IOException {
		String requestXMLString = createSendXMLString(wsdlUrl, method);
		requestXMLString = addParamsToRequestXMLString(requestXMLString, params);
		String responseXMLString = callSendMethod(wsdlUrl, method,
				requestXMLString);
		return responseXMLString;
	}

	/**
	 * <PRE>
	 * 调用webservices接口，获取返回的XML字符串
	 * 支持wsdlUrl格式：http://172.168.10.7:8686/services/myService?wsdl
	 * </PRE>
	 * 
	 * @param wsdlUrl
	 *            wsdl地址
	 * @param method
	 *            调用方法名
	 * @param params
	 *            方法入参 Map
	 * @return response（返回）XML字符串（带参数）
	 * @throws IOException
	 * @throws XmlException
	 * @throws SoapUIException
	 */
	public static String callSendMethod(String wsdlUrl, String method,
			Map<String, String> params) throws SoapUIException, XmlException,
			IOException {
		String requestXMLString = createSendXMLString(wsdlUrl, method);
		requestXMLString = addParamsToRequestXMLString(requestXMLString, params);
		String responseXMLString = callSendMethod(wsdlUrl, method,
				requestXMLString);
		return responseXMLString;
	}

	/*
	 * axis2客户端调用 
	 * 应用rpc的方式调用 这种方式就等于远程调用， 即通过url定位告诉远程服务器，告知方法名称，参数等，
	 * 调用远程服务，得到结果。 使用 org.apache.axis2.rpc.client.RPCServiceClient类调用WebService
	 * 
	 * 【注】：
	 * 
	 * 如果被调用的WebService方法有返回值 应使用 invokeBlocking 方法 该方法有三个参数
	 * 第一个参数的类型是QName对象，表示要调用的方法名； 第二个参数表示要调用的WebService方法的参数值，参数类型为Object[]；
	 * 当方法没有参数时，invokeBlocking方法的第二个参数值不能是null，而要使用new Object[]{}。
	 * 第三个参数表示WebService方法的 返回值类型的Class对象，参数类型为Class[]。
	 * 
	 * 
	 * 如果被调用的WebService方法没有返回值 应使用 invokeRobust 方法
	 * 该方法只有两个参数，它们的含义与invokeBlocking方法的前两个参数的含义相同。
	 * 
	 * 在创建QName对象时，QName类的构造方法的第一个参数表示WSDL文件的命名空间名， 也就是
	 * <wsdl:definitions>元素的targetNamespace属性值。
	 */

	/**
	 * axis2 动态客户端调用webservice服务 默认超时10分钟
	 * 
	 * @param url
	 *            地址
	 * @param nameSpace
	 *            用含wsdl的url在IE打开 <wsdl:definitions> 的 targetNamespace 的值
	 * @param methodName
	 *            调用的方法名
	 * @param args
	 *            参数数组 Object[] inParam = new Object[]{param..};
	 * @param reType
	 *            服务器返回值类型数组 Class[] reType = new Class[]{BaseCommand.class..};
	 * @return 响应结果 Object[],一般直接使用obj[0]
	 * @throws Exception
	 * @author xuzhicheng
	 */
	public static Object[] axis2Invoke(String url, String nameSpace,
			String methodName, Object[] args,
			@SuppressWarnings("rawtypes") Class[] types) throws Exception {
		return axis2Invoke(url, nameSpace, methodName, args, types, CALL_TIMEOUT);
	}

	/**
	 * <PRE>
	 * axis2 动态客户端调用webservice服务
	 * 需要注意，如果方法的参数名称非默认值（默认值arg0~argN），该方法无法使用。
	 * 
	 * </PRE>
	 * 
	 * @param url
	 *            地址
	 * @param nameSpace
	 *            用含wsdl的url在IE打开 <wsdl:definitions> 的 targetNamespace 的值
	 * @param methodName
	 *            调用的方法名
	 * @param args
	 *            参数数组 Object[] inParam = new Object[]{param..};
	 * @param reType
	 *            服务器返回值类型数组 Class[] reType = new Class[]{BaseCommand.class..};
	 * @param timeOut
	 *            响应超时，单位毫秒， 默认10分钟
	 * 
	 * @return 响应结果 Object[],一般直接使用obj[0]
	 * @throws Exception
	 * @author xuzhicheng
	 */
	public static Object[] axis2Invoke(String url, String nameSpace,
			String methodName, Object[] args,
			@SuppressWarnings("rawtypes") Class[] reType, long timeOut)
			throws Exception {

		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();

		EndpointReference targetEPR = new EndpointReference(url);
		options.setTimeOutInMilliSeconds(timeOut);
		options.setTo(targetEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		options.setProperty(HTTPConstants.CHUNKED, "false"); // 设置不受限制.

		// 指定要调用的getGreeting方法及WSDL文件的命名空间
		QName requestMethod = new QName(nameSpace, methodName);

		// 调用方法并输出该方法的返回值
		return serviceClient.invokeBlocking(requestMethod, args, reType);
	}

	/*
	 * cxf客户端动态调用
	 */

	/**
	 * cxf客户端动态调用 默认连接超时1分钟，接收超时10分钟
	 * 
	 * @param wsdlUrl
	 *            wsdl地址
	 * @param method
	 *            调用方法名称
	 * @param orgs
	 *            入参数组
	 * @return Object数组
	 * @throws Exception
	 */
	public static Object[] cxfInvoke(String wsdlUrl, String method,
			Object... orgs) throws Exception {
		return cxfInvoke(wsdlUrl, method, 60000, 600000, orgs);
	}

	/**
	 * cxf客户端动态调用 默认连接超时1分钟，接收超时10分钟
	 * 
	 * @param wsdlUrl
	 *            wsdl地址
	 * @param method
	 *            调用方法名称
	 * @param orgs
	 *            入参数组
	 * @return Object
	 * @throws Exception
	 */
	public static Object cxfInvokeReObj(String wsdlUrl, String method,
			Object... orgs) throws Exception {
		return cxfInvoke(wsdlUrl, method, 60000, 600000, orgs)[0];
	}

	/**
	 * cxf客户端动态调用
	 * 
	 * @param wsdlUrl
	 *            wsdl地址
	 * @param method
	 *            调用方法名称
	 * @param connTimeOut
	 *            连接超时
	 * @param receiveTimeOut
	 *            接收超时
	 * @param orgs
	 *            入参数组
	 * @return Object数组
	 * @throws Exception
	 */
	public static Object[] cxfInvoke(String wsdlUrl, String method,
			long connTimeOut, long receiveTimeOut, Object... orgs)
			throws Exception {
		JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory
				.newInstance();
		Client client = factory.createClient(wsdlUrl);
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(connTimeOut); // 连接超时时间
		policy.setReceiveTimeout(receiveTimeOut);// 请求超时时间.
		conduit.setClient(policy);
		return client.invoke(method, orgs);
	}

	/**
	 * cxf客户端动态调用
	 * 
	 * @param wsdlUrl
	 *            wsdl地址
	 * @param method
	 *            调用方法名称
	 * @param connTimeOut
	 *            连接超时
	 * @param receiveTimeOut
	 *            接收超时
	 * @param orgs
	 *            入参数组
	 * @return Object
	 * @throws Exception
	 */
	public static Object cxfInvokeReObj(String wsdlUrl, String method,
			long connTimeOut, long receiveTimeOut, Object... orgs)
			throws Exception {
		return cxfInvoke(wsdlUrl, method, connTimeOut, receiveTimeOut, orgs)[0];
	}

}
