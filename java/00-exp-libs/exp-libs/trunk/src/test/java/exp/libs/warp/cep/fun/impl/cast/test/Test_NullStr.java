package exp.libs.warp.cep.fun.impl.cast.test;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._NullStr;

/**
 * <pre>
 * Catt自定函数测试：
 * 	强制类型转换: 强制把任何值转换为空串
 * </pre>	
 * @version   1.0 by 2014-09-29
 * @since     jdk版本：1.6
 * @author 廖权斌 ：liaoquanbin@gdcattsoft.com	<PRE>
 * 	<B>任务编号</B>： 
 *	<B>项目</B>：研发-集约化产品开发平台	 
 *	<B>公司</B>：广东凯通软件开发技术有限公司 综合网管接口组 (c) 2013 </PRE>
 */
public class Test_NullStr {

	/**
	 * 空串强制类型转换测试
	 * @throws Exception
	 */
	@Test
	public void testCastNullStr() throws Exception {
		
		//正确性测试
		Object rst = CEPUtils.call(
				_NullStr.NAME, new Object[] {"-123456789"});
		Assert.assertEquals("", rst);
		
		rst = CEPUtils.call("nullstr(\"987654321012345678\")");
		Assert.assertEquals("", rst);
		
		rst = CEPUtils.call("nullstr(\"abc\")");
		Assert.assertEquals("", rst);
		
		rst = CEPUtils.call("nullstr(\"\")");
		Assert.assertEquals("", rst);
		
		rst = CEPUtils.call("nullstr(\"null\")");
		Assert.assertEquals("", rst);
		
		rst = CEPUtils.call("nullstr(\"abc123!@#\")");
		Assert.assertEquals("", rst);
		
		rst = CEPUtils.call("nullstr(\"    \")");
		Assert.assertEquals("", rst);

		Date now = new Date();
		CEPUtils.declare("now", now);
		rst = CEPUtils.call("nullstr($now$)");
		Assert.assertEquals("", rst);
		
		//错误测试
		try {
			CEPUtils.call("nullstr()");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
		try {
			CEPUtils.call("nullstr(\" \r\n \t \0   \")");	//参数不能有换行
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
	}
}
