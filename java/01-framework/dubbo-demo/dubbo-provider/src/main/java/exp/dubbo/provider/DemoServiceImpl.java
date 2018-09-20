package exp.dubbo.provider;

import exp.dubbo.api.DemoService;
import exp.libs.utils.other.StrUtils;

/**
 * 服务实现类
 * @author Administrator
 *
 */
public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		return StrUtils.concat("Hello, ", name, "!");
	}

}
