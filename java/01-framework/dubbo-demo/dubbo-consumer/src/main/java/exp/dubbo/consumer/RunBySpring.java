package exp.dubbo.consumer;


/**
 * <PRE>
 * 服务启动方式一：
 * 	通过Spring/SpirngMVC启动（打包成war包，由tomcat运行）
 * </PRE>
 * <br/><B>PROJECT : </B> dubbo-demo
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RunBySpring {

	/**
	 * 确保在 WebRoot/web.xml 中
	 * 以 Spring 或 SpringMVC 方式，直接或间接地加载了 dubbo-consumer.xml 配置文件即可, 
	 * 
	 * 尔后通过tomcat启动时就会自动获取服务（dubbo-provider必须已经启动）,
	 * 可以通过界面的点击等操作触发dubbo接口的调用。
	 */
	
}
