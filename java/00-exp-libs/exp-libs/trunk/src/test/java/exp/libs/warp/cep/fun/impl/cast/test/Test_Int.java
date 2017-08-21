package exp.libs.warp.cep.fun.impl.cast.test;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._Int;

/**
 * <pre>
 * Catt自定函数测试：
 * 	强制类型转换: String -> int
 * </pre>	
 * @version   1.0 by 2014-09-29
 * @since     jdk版本：1.6
 * @author 廖权斌 ：liaoquanbin@gdcattsoft.com	<PRE>
 * 	<B>任务编号</B>： 
 *	<B>项目</B>：研发-集约化产品开发平台	 
 *	<B>公司</B>：广东凯通软件开发技术有限公司 综合网管接口组 (c) 2013 </PRE>
 */
public class Test_Int {

	/**
	 * int强制类型转换测试
	 * @throws Exception
	 */
	@Test
	public void testCastInt() throws Exception {
		
		//正确性测试
		Object inum1 = CEPUtils.call(
				_Int.NAME, new Object[] {"-123"});
		Assert.assertTrue(inum1 instanceof Integer);
		System.out.println(inum1);
		
		Object inum2 = CEPUtils.call("int(\"987\")");
		Assert.assertTrue(inum2 instanceof Integer);
		System.out.println(inum2);
		
		//错误测试
		try {
			CEPUtils.call("int(\"abc\")");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
		
		try {
			CEPUtils.call("int()");
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e.getCause());
			Assert.assertTrue(true);
		}
	}
}
