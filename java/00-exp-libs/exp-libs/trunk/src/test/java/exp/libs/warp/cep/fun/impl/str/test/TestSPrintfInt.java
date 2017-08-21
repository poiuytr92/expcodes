package exp.libs.warp.cep.fun.impl.str.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;

/**
 * <PRE>
 * {类说明}
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2015
 * @version   1.0 2015年5月19	日
 * @author    黄坚：huangjian@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class TestSPrintfInt {

	/**
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 *
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.catt.util.expression.functions.impl.str.SPrintfInt#eval(java.util.List)}.
	 * @throws Exception 
	 */
	@Test
	public void testEval() throws Exception {
		Object re = null;
		re = CEPUtils.call("sprintf(\"%2f\", 0.22)");
		System.out.println(re);
	}

}
