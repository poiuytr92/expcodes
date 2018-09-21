package exp.dubbo.provider.impl;

import exp.dubbo.api.DemoService;

/**
 * <PRE>
 * 实现子项目(dubbo-api)所声明的服务接口.
 * </PRE>
 * <br/><B>PROJECT : </B> dubbo-demo
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		name = (name == null ? "http://exp-blog.com" : name);
		return "Welcome: [" +  name + "] !";
	}

}
