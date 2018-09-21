package exp.dubbo.provider;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main2 {

	 public static void main(String[] args) throws IOException {
         ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubbo-provider.xml");
         System.out.println(context.getDisplayName() + ": here");
         context.start();
         System.out.println("服务已经启动...");
         System.in.read();
     }

}
