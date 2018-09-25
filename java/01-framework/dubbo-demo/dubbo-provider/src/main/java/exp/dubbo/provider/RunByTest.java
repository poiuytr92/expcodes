package exp.dubbo.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <PRE>
 * 服务启动方式三：
 * 	此方式与通过Spring启动是一样的（只是不用经过tomcat），
 *  但一般只用于测试，不建议使用
 * </PRE>
 * <br/><B>PROJECT : </B> dubbo-demo
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RunByTest {

	 public static void main(String[] args) throws Exception {
         ClassPathXmlApplicationContext context = // 加载Spring配置文件
        		 new ClassPathXmlApplicationContext("dubbo-provider.xml");
         context.start();	// 模拟Spring启动
         
         System.out.println("Dubbo service provider started!");
         Thread.sleep(Long.MAX_VALUE);	// 通过阻塞简单对线程保活, 仅用于测试
         context.close();
     }

}
