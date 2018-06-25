package exp.libs.warp.cep.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import exp.libs.warp.cep.CEPUtils;
import exp.libs.warp.cep.fun.impl.str.Cut;
import exp.libs.warp.cep.fun.impl.str.Trim;

/**
 * <pre>
 * Expression Parser
 * 
 * 表达式/函数式解析工具测试/样例
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestCEPUtils {

	/**
	 * 简单表达式计算测试/样例
	 * @throws Exception
	 */
	@Test
	public void testEval() throws Exception {
		//完整表达式调�?
		String expression = "(6 + 2) * (4 / (5 % 3) ^ 7)";
		Object rst1 = CEPUtils.eval(expression);
		System.out.println("rst1: " + rst1);
		
		//分步调用验证
		Object rst2 = CEPUtils.eval("5 % 3");
		rst2 = CEPUtils.eval(rst2 + " ^ 7");
		rst2 = CEPUtils.eval("4 / " + rst2);
		rst2 = CEPUtils.eval("8 * " + rst2);
		System.out.println("result2: " + rst2);
		Assert.assertEquals(rst2, rst1);
		
		//非法运算调用测试
		Object rst3 = CEPUtils.eval("4 / 0");
		System.out.println("rst3: " + rst3);
		Assert.assertEquals("Infinity", rst3.toString());
		
		//解析失败调用测试
		try {
			CEPUtils.eval("x + y");	//没有声明的变�?
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			System.err.println(e);
			Assert.assertTrue(e.getMessage().
					contains(CEPUtils.PARSE_EXPRESSION_ERROR));
		}
	}
	
	/**
	 * [声明变量]调用表达�?/函数�? 测试/样例
	 * @throws Exception 
	 */
	@Test
	public void testDeclare() throws Exception {
		
		//声明变量
		CEPUtils.declare("x", 10);
		CEPUtils.declare("y", -2);
		
		//表达式调用：注意变量调用需要用$包围
		Object rst = CEPUtils.eval("$x$ + $y$ - 3");
		System.out.println("10 + (-2) - 3 = " + rst);
		Assert.assertEquals(5.0, rst);
		
		//函数调用
		rst = CEPUtils.call("abs($y$)");
		System.out.println("abs(-2) = " + rst);
		Assert.assertEquals(2, rst);
	}
	
	/**
	 * [自定义函数名称]调用测试/样例
	 * @throws Exception
	 */
	@Test
	public void testCustomNameCall() throws Exception {
		
		// 经验库自定义或jep已经提供的函数可直接使用，实际应用时建议用函数自带名字常�?,�? Cut.NAME 
		String defaultName = Cut.NAME;
		Object cutStr = CEPUtils.call(
				defaultName, new Object[] {"abcdef", 1, 5});
		
		System.out.println("\"abcdef\" cut 1-5: " + cutStr);
		Assert.assertEquals("bcde", cutStr.toString());
		
		// 也可重新注册函数名称调用，名称可随意（注意同名函数会被覆盖）
		String MyFunName = "TestCut";
		CEPUtils.register(MyFunName, "exp.libs.warp.cep.fun.impl.str.Cut");
		cutStr = CEPUtils.call(MyFunName, new Object[] {"abcdef", 2, 4});
		
		System.out.println("\"abcdef\" cut 2-4: " + cutStr);
		Assert.assertEquals("cd", cutStr.toString());
	}
	
	/**
	 * 1、[两种调用函数]的方式测�?/样例
	 * 2、函数[不定参数类型]测试/样例
	 * @throws Exception
	 */
	@Test
	public void testDiffCall() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date nowDate = new Date();
		String nowStr = sdf.format(nowDate);
		nowDate = sdf.parse(nowStr);	//重新解析是因为末3位尾数被日期格式舍弃了，不这样做后面的断言会出�?
		
		//先注册一个自定义函数,该函数功能是�? Date �? 特定格式的时间字符串 转换成秒
		//换而言之，该函数的入参可能�? Date类型，也可能�? String类型，但出参均是long类型
		CEPUtils.register("TimeToSecond", "exp.libs.warp.cep.fun.impl.time.Date2Sec");
		
		//调用方式1：传参调�?
		//此方法适用�? 参数表中的类型只�? 字符�? �? 数字 的情�?
		Object sec = CEPUtils.call(
				"TimeToSecond", new Object[] {nowStr});	//这里传参是String
		System.out.println("Now sec:" + sec);
		Assert.assertEquals(nowDate.getTime(), sec);
		
		//调用方式2：声明变�?,构造完整函数式调用
		//此方法适用�? 参数表中的类型除�? 字符�? �? 数字 之外，还包含其他类型的情�?
		CEPUtils.declare("inDate", nowDate);				//这里声明�? Date 类型的入�? 
		sec = CEPUtils.call("TimeToSecond($inDate$)");
		System.out.println("Now sec:" + sec);
		Assert.assertEquals(nowDate.getTime(), sec);
		
		//尝试用方�?1调用，由于给�? Date 类型参数,因此会解析失�?
		try {
			sec = CEPUtils.call(
					"TimeToSecond", new Object[] {nowDate});	//这里传参�? Date
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		
		//其实方式1也可以给�? [非字符串或数字] 类型的参数，只要把该参数声明为变量即�?(即把字符串类型的变量名称作为入参)
		CEPUtils.declare("inDate", nowDate);
		sec = CEPUtils.call(
				"TimeToSecond", new Object[] {"$inDate$"});	//这里传参�? Date 的变�?
		System.out.println("Now sec:" + sec);
		Assert.assertEquals(nowDate.getTime(), sec);
	}
	
	/**
	 * 当入参是常量字符串时，两种调用方式的区别 测试/样例
	 */
	@Test
	public void testDiffStrCall() throws Exception {
		
		//调用方式1：传参调�?
		//此方法会自动加工字符串，因此原样传入字符串即�?
		Object str = CEPUtils.call(
				Trim.NAME, new Object[] {"  abcd fg   "});	//这里原样传入
		System.out.println("trim:" + str);
		Assert.assertEquals("abcd fg", str);
		
		//调用方式2：构造完整函数式调用
		//此方法不会自动加工字符串，因此需为字符串手动加上双引�?
		str = CEPUtils.call("trim(\"  abcd fg   \")");	//这里传入需为字符串添加前后双引�?
		System.out.println("trim:" + str);
		Assert.assertEquals("abcd fg", str);
		
		//调用方式2：尝试不加双引号
		//jep会认为不存在变量"  abcd fg   "而解析失败报�?
		try {
			CEPUtils.call("trim(  abcd fg   )");	
			Assert.assertTrue(false);
			
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * [不定个数入参] 函数调用
	 * @throws Exception
	 */
	@Test
	public void testIndefiniteParamNumCall() throws Exception {
		//自定义函数Now的默认函数名�?  now, 支持不定个数入参的调用方�?
		
		//当参数个数是 0 个时,返回默认格式 yyyy-MM-dd HH:mm:ss 的当前时�?
		Object now = CEPUtils.call("now()");
		System.out.println(now);
		Assert.assertEquals(19, now.toString().length());
		
		//当参数个数是1个时，该参数用于指定日期格式
		now = CEPUtils.call("now(\"yyyy-MM-dd\")");
		System.out.println(now);
		Assert.assertEquals(10, now.toString().length());
	}
	
	/**
	 * 函数[嵌套调用]测试
	 * @throws Exception
	 */
	@Test
	public void testNestCall() throws Exception {
		//为了使表达式更复杂，可以定义个变�?
		CEPUtils.declare("abc", 10);
		
		//嵌套调用 cut �? trim函数(嵌套只有一种调用方�?)
		Object str = CEPUtils.call("trim(cut(\"   aaaa mmmm   \",1,$abc$))");
		System.out.println("cut & trim: " + str);
		Assert.assertEquals("aaaa mm", str);
	}
	
}
