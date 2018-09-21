package exp.dubbo.api;

/**
 * <PRE>
 * 示例服务接口.
 * ------------------------
 * 此子项目(dubbo-api)的主要功能就是自定义服务接口，
 * 再统一提供给子项目 dubbo-provider 和子项目 dubbo-consumer 引用
 * </PRE>
 * <br/><B>PROJECT : </B> dubbo-demo
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface DemoService {

	public String sayHello(String name);
	
}
