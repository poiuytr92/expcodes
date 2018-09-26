package exp.dubbo.consumer.web.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import exp.dubbo.api.DemoService;


/**
 * <PRE>
 * 把dubbo接口放到控制器中实例化/调用.
 * （打包成war包，并在tomcat运行后，等待页面触发调用dubbo接口即可）
 * 
 * 注：控制器的类名是随意的。
 * </PRE>
 * <br/><B>PROJECT : </B> dubbo-demo
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@Controller
public class DemoServiceController extends AbstractController {

	@Autowired	// 自动装配（初始化）
    private DemoService demoService;
	
    @Override
    @RequestMapping(value="/demo-dubbo-consumer")	// 在请求URL中包含此路径时则映射到此方法
    protected ModelAndView handleRequestInternal(
    		HttpServletRequest request, HttpServletResponse response) 
    				throws Exception {
    	
    	// 调用dubbo接口
    	String welcome = demoService.sayHello("exp");
    	System.out.println(welcome);
    	
		return null;	// 不返回视图
    }

}
