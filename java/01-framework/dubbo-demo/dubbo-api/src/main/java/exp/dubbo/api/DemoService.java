package exp.dubbo.api;

/**
 * 定义了统一的接口让provider和consumer来引用
 * @author Administrator
 *
 */
public interface DemoService {

	public String sayHello(String name);
	
	
	/**
	 * 启动zookeeper注册中心集群
	 * 
	 * 先通过dubbo-provider的Main.java中的main方法 启动dubbo-provider项目。

然后将dubbo-consumer项目打成war包，部署到tomcat或其他web容器中。

在浏览器中访问：localhost:tomcatport/dubbo-consumer/hello.html?name=test

tomcatport换成你本地的tomcat端口号

可以看到浏览器输出：
	 */
	
}
