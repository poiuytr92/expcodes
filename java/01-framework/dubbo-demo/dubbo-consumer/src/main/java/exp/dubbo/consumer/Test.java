package exp.dubbo.consumer;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import exp.dubbo.api.DemoService;

public class Test {

	public static void main(String[] args) {
        ClassPathXmlApplicationContext context = 
        		new ClassPathXmlApplicationContext("classpath:spring-mvc.xml");
        context.start();
        DemoService demoService = (DemoService) context.getBean("demoService");

        System.out.println(demoService.sayHello("哈哈哈"));
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
	
}
