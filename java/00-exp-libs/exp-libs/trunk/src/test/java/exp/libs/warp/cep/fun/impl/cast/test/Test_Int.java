package exp.libs.warp.cep.fun.impl.cast.test;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.cast._Int;

/**
 * <pre>
 * 自定义函数测试：
 * 	强制类型转换: String -> int
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Test_Int {

	/**
	 * int强制类型转换测试
	 * @throws Exception
	 */
	@Test
	public void testCastInt() throws Exception {
		
		//正确性测�?
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
