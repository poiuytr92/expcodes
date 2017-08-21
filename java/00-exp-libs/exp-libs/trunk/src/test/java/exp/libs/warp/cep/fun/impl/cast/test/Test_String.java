package exp.libs.warp.cep.fun.impl.cast.test;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._String;

/**
 * <pre>
 * Catt自定函数测试：
 * 	强制类型转换: 调用入参的toString方法得到其 String 类型
 * </pre>	
 * @version   1.0 by 2014-09-29
 * @since     jdk版本：1.6
 * @author 廖权斌 ：liaoquanbin@gdcattsoft.com	<PRE>
 * 	<B>任务编号</B>： 
 *	<B>项目</B>：研发-集约化产品开发平台	 
 *	<B>公司</B>：广东凯通软件开发技术有限公司 综合网管接口组 (c) 2013 </PRE>
 */
public class Test_String {

	/**
	 * toString测试
	 * @throws Exception
	 */
	@Test
	public void testCastString() throws Exception {
		
		//正确性测试
		Object rst = CEPUtils.call(
				_String.NAME, new Object[] {"-123456789"});
		Assert.assertEquals("-123456789", rst);
		
		rst = CEPUtils.call("str(\"987654321012345678\")");
		Assert.assertEquals("987654321012345678", rst);
		
		rst = CEPUtils.call("str(\"abc\")");
		Assert.assertEquals("abc", rst);
		
		rst = CEPUtils.call("str(\"\")");
		Assert.assertEquals("", rst);
		
		rst = CEPUtils.call("str(\"null\")");
		Assert.assertEquals("null", rst);
		
		rst = CEPUtils.call("str(\"abc123!@#\")");
		Assert.assertEquals("abc123!@#", rst);
		
		rst = CEPUtils.call("str(\"    \")");
		Assert.assertEquals("    ", rst);

		//错误测试
		try {
			CEPUtils.call("str()");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
		try {
			CEPUtils.call("str(\" \r\n \t \0   \")");	//参数不能有换行
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
	}
}
