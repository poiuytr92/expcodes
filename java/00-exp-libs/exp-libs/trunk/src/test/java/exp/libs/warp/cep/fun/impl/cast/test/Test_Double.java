package exp.libs.warp.cep.fun.impl.cast.test;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._Double;

/**
 * <pre>
 * Catt自定函数测试：
 * 	强制类型转换: String -> Double
 * </pre>	
 * @version   1.0 by 2014-09-29
 * @since     jdk版本：1.6
 * @author 廖权斌 ：liaoquanbin@gdcattsoft.com	<PRE>
 * 	<B>任务编号</B>： 
 *	<B>项目</B>：研发-集约化产品开发平台	 
 *	<B>公司</B>：广东凯通软件开发技术有限公司 综合网管接口组 (c) 2013 </PRE>
 */
public class Test_Double {

	/**
	 * Double强制类型转换测试
	 * @throws Exception
	 */
	@Test
	public void testCastDouble() throws Exception {
		
		//正确性测试
		Object dnum1 = CEPUtils.call(
				_Double.NAME, new Object[] {"-123456789"});
		Assert.assertTrue(dnum1 instanceof Double);
		System.out.println(dnum1);
		
		Object dnum2 = CEPUtils.call("double(\"9876543210123456789\")");
		Assert.assertTrue(dnum2 instanceof Double);
		System.out.println(dnum2);
		
		//错误测试
		try {
			CEPUtils.call("double(\"1D\")");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
		try {
			CEPUtils.call("double()");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
	}
}
