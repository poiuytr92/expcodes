package exp.dubbo.provider;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <PRE>
 * 服务启动方式二：
 * 	通过spring启动（打包成jar包直接运行）
 * </PRE>
 * <br/><B>PROJECT : </B> dubbo-demo
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RunBySpring {

	 public static void main(String[] args) throws IOException {
         ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubbo-provider.xml");
         System.out.println(context.getDisplayName() + ": here");
         context.start();
         System.out.println("服务已经启动...");
         System.in.read();
     }

}
