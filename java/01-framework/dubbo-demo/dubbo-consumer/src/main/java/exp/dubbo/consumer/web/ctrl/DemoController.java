package exp.dubbo.consumer.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import exp.dubbo.api.DemoService;

/**
 * 是个控制器，使用requestMapping注解提供http接口服务
 * @author Administrator
 *
 */
@RestController
public class DemoController {

	// FIXME 通过tomcat方式或者不通过tomcat启动 provider与consumer
	
	@Autowired
    private DemoService demoService;

    @RequestMapping("/index.jsp")
    @ResponseBody
    public String sayHello(String name) {
        String welcome = demoService.sayHello(name);
        return welcome;
    }
    
}
