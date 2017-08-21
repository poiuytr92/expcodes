package exp.libs.warp.cep.fun.impl.cast.test;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._Long;

/**
 * <pre>
 * Catt自定函数测试：
 * 	强制类型转换: String -> long
 * </pre>	
 * @version   1.0 by 2014-09-29
 * @since     jdk版本：1.6
 * @author 廖权斌 ：liaoquanbin@gdcattsoft.com	<PRE>
 * 	<B>任务编号</B>： 
 *	<B>项目</B>：研发-集约化产品开发平台	 
 *	<B>公司</B>：广东凯通软件开发技术有限公司 综合网管接口组 (c) 2013 </PRE>
 */
public class Test_Long {

	/**
	 * long强制类型转换测试
	 * @throws Exception
	 */
	@Test
	public void testCastLong() throws Exception {
		
		//正确性测试
		Object lnum1 = CEPUtils.call(
				_Long.NAME, new Object[] {"-123456789"});
		Assert.assertTrue(lnum1 instanceof Long);
		System.out.println(lnum1);
		
		Object lnum2 = CEPUtils.call("long(\"987654321012345678\")");
		Assert.assertTrue(lnum2 instanceof Long);
		System.out.println(lnum2);
		
		//错误测试
		try {
			CEPUtils.call("long(\"9876543210123456789\")");	//数值溢出
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
		try {
			CEPUtils.call("long(\"1L\")");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
		try {
			CEPUtils.call("long()");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
	}
}
