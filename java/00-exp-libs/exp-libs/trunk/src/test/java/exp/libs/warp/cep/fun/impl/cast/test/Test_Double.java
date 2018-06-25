package exp.libs.warp.cep.fun.impl.cast.test;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._Double;

/**
 * <pre>
 * 自定义函数测试：
 * 	强制类型转换: String -> Double
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Test_Double {

	/**
	 * Double强制类型转换测试
	 * @throws Exception
	 */
	@Test
	public void testCastDouble() throws Exception {
		
		//正确性测�?
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
