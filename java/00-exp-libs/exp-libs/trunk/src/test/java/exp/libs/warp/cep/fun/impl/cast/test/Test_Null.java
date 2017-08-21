package exp.libs.warp.cep.fun.impl.cast.test;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._Null;

/**
 * <pre>
 * Catt自定函数测试：
 * 	强制类型转换: 强制把任何值转换为null
 * </pre>	
 * @version   1.0 by 2014-09-29
 * @since     jdk版本：1.6
 * @author 廖权斌 ：liaoquanbin@gdcattsoft.com	<PRE>
 * 	<B>任务编号</B>： 
 *	<B>项目</B>：研发-集约化产品开发平台	 
 *	<B>公司</B>：广东凯通软件开发技术有限公司 综合网管接口组 (c) 2013 </PRE>
 */
public class Test_Null {

	/**
	 * null强制类型转换测试
	 * @throws Exception
	 */
	@Test
	public void testCastNull() throws Exception {
		
		//正确性测试
		Object rst = CEPUtils.call(
				_Null.NAME, new Object[] {"-123456789"});
		Assert.assertNull(rst);
		
		rst = CEPUtils.call("null(\"987654321012345678\")");
		Assert.assertNull(rst);
		
		rst = CEPUtils.call("null(\"abc\")");
		Assert.assertNull(rst);
		
		rst = CEPUtils.call("null(\"\")");
		Assert.assertNull(rst);
		
		rst = CEPUtils.call("null(\"null\")");
		Assert.assertNull(rst);
		
		rst = CEPUtils.call("null(\"abc123!@#\")");
		Assert.assertNull(rst);
		
		rst = CEPUtils.call("null(\"    \")");
		Assert.assertNull(rst);

		Date now = new Date();
		CEPUtils.declare("now", now);
		rst = CEPUtils.call("null($now$)");
		Assert.assertNull(rst);
		
		//错误测试
		try {
			CEPUtils.call("null()");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
		try {
			CEPUtils.call("null(\" \r\n \t \0   \")");	//参数不能有换行
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
	}
}
