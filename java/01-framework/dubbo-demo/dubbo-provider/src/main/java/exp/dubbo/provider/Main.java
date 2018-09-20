package exp.dubbo.provider;

public class Main {

	public static void main(String[] args) {
		// 该类是dubbo框架提供，作用是启动dubbo服务，dubbo会在启动服务时，读取classpath下一个名为dubbo.properties文件的属性值。
		com.alibaba.dubbo.container.Main.main(args);
		
		
		
		/* 
		 * dubbo.properties:
		 * 
		 * dubbo.container指定了dubbo的容器使用spring，dubbo内部有四种容器实现，SpringContainer是其中一种，也是默认的容器
		 * dubbo.spring.config指定了dubbo在启动服务时加载的spring配置文件。
		 * dubbo.protocol.name和dubbo.protocol.port分别指定使用的协议名和端口
		 */
	}
	
}
