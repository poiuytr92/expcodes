package exp.libs.warp.cep.fun.impl.str.test;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;

/**
 * <pre>
 * 自定义函数测试：
 * 	获取C语言 sprintf 风格的格式字符串（仅针对int入参）
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestSPrintfInt {

	@Test
	public void testEval() throws Exception {
		Object re = null;
		re = CEPUtils.call("sprintf(\"%2f\", 0.22)");
		System.out.println(re);
	}

}
