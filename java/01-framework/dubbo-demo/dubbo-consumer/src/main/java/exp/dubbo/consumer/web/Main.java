package exp.dubbo.consumer.web;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import exp.dubbo.api.DemoService;

public class Main {

	public static void main(String[] args) {
        //测试常规服务
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("dubbo-consumer.xml");
        context.start();
        System.out.println("consumer start");
        DemoService demoService = context.getBean(DemoService.class);
        System.out.println(demoService.sayHello("exp"));
    }

}
