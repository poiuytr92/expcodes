package exp.dubbo.consumer.web.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import exp.dubbo.api.DemoService;


/**
 * 基于注解方式配置的Controller
 * 此demo与 spring-mvc.xml 配置文件配套
 */
@Controller
public class AnnotationController extends AbstractController{

	@Autowired
    private DemoService demoService;
	
    @Override
    @RequestMapping(value="/demo-dubbo-consumer")	// 在请求URL中包含此路径时则映射到此方法
    protected ModelAndView handleRequestInternal(
    		HttpServletRequest request, HttpServletResponse response) 
    				throws Exception {
    	
    	// ---------------------------------------------------
    	// TODO 业务逻辑
    	System.out.println("AnnotationController Working.");
    	String welcome = demoService.sayHello("exp");
    	System.out.println(welcome);
    	
    	// ---------------------------------------------------
    	
		return new ModelAndView();
    }

}
