package exp.spring.boot.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// RestController的意思就是controller里面的方法都以json格式输出，不用再写什么jackjson配置的了！
// 启动主程序，打开浏览器访问http://localhost:8080/hello，就可以看到效果了
@RestController
public class HelloWorldController {

	@RequestMapping("/hello")
	public String index() {
		return "Hello World!";
	}
	
}
