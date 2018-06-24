package exp.libs.warp.net.wsdl.server;

import javax.jws.WebService;  

/**
 * <PRE>
 * Websevices服务提供的API接口定义.
 * </PRE>
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-06-20
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
@WebService  	// JAX-WS注解, 必须有
public interface _IWsdlService {  
    
	/**
	 * 自定义接口服务
	 * @param param
	 * @return
	 */
	public String foo(String param);  
	
	/**
	 * 自定义接口服务
	 * @param param
	 * @return
	 */
    public void bar(int param);  
    
}  
