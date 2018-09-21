package exp.dubbo.provider;

/**
 * <PRE>
 * 服务启动方式一：
 * 	注入到dubbo启动（打包成jar包直接运行）
 * </PRE>
 * <br/><B>PROJECT : </B> dubbo-demo
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RunByDubbo {

	public static void main(String[] args) {
		
		/*
		 * 此类由dubbo框架提供，作用是启动dubbo服务
		 * dubbo会在启动服务时，会读取 classpath 根目录下名为 dubbo.properties 文件的配置属性
		 */
		com.alibaba.dubbo.container.Main.main(args);
	}
	
}
