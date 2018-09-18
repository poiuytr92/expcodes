package exp.dubbo.provider.web.ctrl;

import org.springframework.stereotype.Service;

import exp.dubbo.api.DemoService;
import exp.libs.utils.other.StrUtils;

@Service("demoService")
public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		return StrUtils.concat("Hello, ", name, "!");
	}

}
