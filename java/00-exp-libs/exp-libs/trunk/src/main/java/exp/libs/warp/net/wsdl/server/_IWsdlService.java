package exp.libs.warp.net.wsdl.server;

import javax.jws.WebService;  

/**
 * <PRE>
 * Websevices服务提供的API接口定义.
 * </PRE>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-06-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@WebService  	// JAX-WS注解, 必须�?
public interface _IWsdlService {  
    
	/**
	 * 自定义接口服�?
	 * @param param
	 * @return
	 */
	public String foo(String param);  
	
	/**
	 * 自定义接口服�?
	 * @param param
	 * @return
	 */
    public void bar(int param);  
    
}  
