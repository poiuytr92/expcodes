package exp.libs.warp.cep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.singularsys.jep.Jep;
import com.singularsys.jep.functions.PostfixMathCommand;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.cep.fun.impl.cast._Date;
import exp.libs.warp.cep.fun.impl.cast._Double;
import exp.libs.warp.cep.fun.impl.cast._Int;
import exp.libs.warp.cep.fun.impl.cast._Long;
import exp.libs.warp.cep.fun.impl.cast._Null;
import exp.libs.warp.cep.fun.impl.cast._NullStr;
import exp.libs.warp.cep.fun.impl.cast._String;
import exp.libs.warp.cep.fun.impl.num.BaseConvert;
import exp.libs.warp.cep.fun.impl.num.Bin;
import exp.libs.warp.cep.fun.impl.num.Hex;
import exp.libs.warp.cep.fun.impl.num.Oct;
import exp.libs.warp.cep.fun.impl.other.IfElse;
import exp.libs.warp.cep.fun.impl.other.Macro;
import exp.libs.warp.cep.fun.impl.other.Print;
import exp.libs.warp.cep.fun.impl.other.Wait;
import exp.libs.warp.cep.fun.impl.sql.CopyDquote;
import exp.libs.warp.cep.fun.impl.sql.CopySquote;
import exp.libs.warp.cep.fun.impl.str.Concat;
import exp.libs.warp.cep.fun.impl.str.Cut;
import exp.libs.warp.cep.fun.impl.str.LTrim;
import exp.libs.warp.cep.fun.impl.str.MTrim;
import exp.libs.warp.cep.fun.impl.str.RTrim;
import exp.libs.warp.cep.fun.impl.str.Replace;
import exp.libs.warp.cep.fun.impl.str.Reverse;
import exp.libs.warp.cep.fun.impl.str.SprintfINT;
import exp.libs.warp.cep.fun.impl.str.Trim;
import exp.libs.warp.cep.fun.impl.time.Date2Sec;
import exp.libs.warp.cep.fun.impl.time.GetLongTime;
import exp.libs.warp.cep.fun.impl.time.GetYear;
import exp.libs.warp.cep.fun.impl.time.Now;
import exp.libs.warp.cep.fun.impl.time.Sec2Date;

/**
 * <pre>
 * Expression Parser
 * 表达式/函数式解析工具
 * 
 * 使用示例:
 * 	// 完整表达式调用
 * 	String expression = "(6 + 2) * (4 / (5 % 3) ^ 7)";
 * 	Object rst = CEPUtils.eval(expression)
 * 
 * 	// 声明变量后调用表达式(变量在表达式中需需用$包括起来)
 * 	CEPUtils.declare("x", 10);
 * 	CEPUtils.declare("y", -2);
 * 	Object rst = CEPUtils.eval("$x$ + $y$ - 3");
 * 
 *	// 函数调用
 *	CEPUtils.declare("z", -2);
 *	Object rst = CEPUtils.call("abs($z$)");
 *
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class CEPUtils {

	private final static Logger log = LoggerFactory.getLogger(CEPUtils.class);
	
	/**
	 * 声明变量失败提示关键�?
	 */
	public final static String DECLARE_VARIABLE_ERROR = 
			"Declare Variable Error : ";
	
	/**
	 * 注册函数失败提示关键�?
	 */
	public final static String REGISTER_FUNCTION_ERROR = 
			"Register Function Error : ";
	
	/**
	 * 解析表达�?/函数失败提示关键�?
	 */
	public final static String PARSE_EXPRESSION_ERROR = 
			"Parse Expression Error : ";
	
	/**
	 * 执行计算发生异常得到的返回�?.
	 * (实际上不会存在这个返回�?,所有执行异常都向外抛出,即一旦发生异常就没有返回�?)
	 */
	public final static String ERROR_RESULT = "NaN";
	
	/**
	 * 变量包围符号
	 */
	public final static String VAR_CH = "$";
	
	/**
	 * 函数/表达式解析器
	 */
	private final static Jep jep = new Jep();
	
	/**
	 * 由内部定义的常用函数表名�?
	 */
	private final static List<String> innerFunNames = 
			new LinkedList<String>();
	
	/**
	 * 自定义的函数表名�?
	 */
	private final static List<String> customFunNames = 
			new LinkedList<String>();
	
	/**
	 * 编译时解析得到的函数�?.用于自动注册.
	 * 函数默认�? -> 函数类路�?
	 */
	private final static Map<String, String> allFunsMap = 
			new HashMap<String, String>();
	
	/**
	 * 初始�? CepUtils:
	 * 	添加JEP自带的标准常�? �? 内部定义的常用函�?
	 */
	static {
		init();
	}
	
	/**
	 * 私有化构造函�?,避免误用
	 */
	private CEPUtils() {}
	
	/**
	 * 初始�? CepUtils.
	 * 一般无需调用,除非调用�? unregisterAll 方法反注册了所有变量函数后,需要恢复成初始状�?.
	 */
	public static void init() {
		jep.addStandardConstants();	//添加JEP自带的标准常量e与pi
		addInnerFunctions();		//添加内部定义的默认函�?(与JEP默认函数同名则覆�?)
	}
	
	/**
	 * 变量声明.
	 * 利用此方法声明的变量可直接用�? 函数/表达�? 计算.
	 * 
	 * 此方法会自动为变量添加首尾括�? 标识，避免复杂的嵌套调用时出现表达式解释失败引致冲突�?
	 * 因此变量在调用时需要添�? 首尾括号 标识�?  {变量名称}
	 * 
	 * 如声明为�? declare(x, 2);
	 * 则调用为�? eval("$x$ + 100")
	 * 得到结果为： 102
	 * 
	 * @param variableName 变量名称
	 * @param value 变量�?
	 */
	public static void declare(String variableName, Object value) {
		try {
			jep.addVariable(getVarCallName(variableName), value);
			
		} catch (Exception e) {
			log.error(DECLARE_VARIABLE_ERROR.concat("{}"), variableName, e);
		}
	}
	
	/**
	 * 获取变量的调用名
	 * @param variableName 变量名称
	 * @return $变量名称$
	 */
	public static String getVarCallName(String variableName) {
		return VAR_CH + variableName + VAR_CH;
	}
	
	/**
	 * 表达式计�?.
	 * 支持以下多种混合运算,常用包括�?
	 * �?+ �?- �?* 非取整除/ 求模% 乘方^ 括号()
	 * 
	 * @param expression 完整的表达式
	 * @return 
	 * 	解析正常且表达式合法:返回结果�?(一般为Double类型,除非计算失败).
	 * 	解析正常但表达式非法：如�?0, 返回Infinity�?
	 */
	public static Object eval(String expression) {
		Object result = ERROR_RESULT;
		try {
			result = evaluate(expression);
			
		} catch (Exception e) {
			log.error(PARSE_EXPRESSION_ERROR.concat("{}"), expression, e);
		}
		return result;
	}
	
	/**
	 * 注册自定义函�?.
	 * 可通过选择继承 BaseFunction1、BaseFunction2 �? BaseFunctionN 实现自定义函数类.
	 * 通过此方法注册函数后,就可以使�? call 方法进行调用.
	 * 
	 * @param customFunctionName 注册函数名称，调用时依赖此名�?
	 * @param clazzPath 自定义函数的类路径，如： foo.bar.util.expression.functions.xxx
	 */
	public static void register(String customFunctionName, String clazzPath) {
		try {
			PostfixMathCommand funInstn = 
					(PostfixMathCommand) Class.forName(clazzPath).newInstance();
			jep.addFunction(customFunctionName, funInstn);
			customFunNames.add(customFunctionName);
			
		} catch (Exception e) {
			log.error(REGISTER_FUNCTION_ERROR.concat("{}"), customFunctionName, e);
		}
	}
	
	/**
	 * 自动注册函数.
	 * 当调用单个函数时,若发现函数未注册,则尝试自动注�?.
	 * 
	 * @param functionName 需要注册的函数名称
	 * @return true:注册成功; false:注册失败
	 */
	private static boolean autoRegister(String functionName) {
		boolean isRegister = false;
		String funClassPath = allFunsMap.get(functionName);
		
		if(funClassPath != null && !"".equals(funClassPath)) {
			register(functionName, funClassPath);
			isRegister = true;
		}
		return isRegister;
	}
	
	/**
	 * 检查函数是否已被注�?
	 * @param functionName 函数名称
	 * @return true:已注�?; false:未注�?
	 */
	public static boolean checkRegister(String functionName) {
		return getAllFunctionsName().contains(functionName);
	}
	
	/**
	 * 单个函数调用.
	 * 若调用自定义函数,需先通过 registerCustomFunction 方法进行注册.
	 * <B>若所传参包含非数字或非字符串类型，只能通过 declare 声明变量传参�?</B>
	 * 
	 * @param functionName 函数名称.注册时用的名�?
	 * @param params 函数参数,支持传入变量
	 * @return 执行结果
	 */
	public static Object call(String functionName, Object[] params) {
		List<Object> paramList = new ArrayList<Object>();
		if(params != null) {
			paramList = Arrays.asList(params);
		}
		return call(functionName, paramList);
	}
	
	/**
	 * 单个函数调用.
	 * 若调用自定义函数,需先通过 registerCustomFunction 方法进行注册.
	 * 对于未注册的函数名称,会尝试自动注册函�?,但要求functionName为函数类中的默认�?.
	 * 
	 * <B>若所传参包含非数字或非字符串类型，只能通过 declare 声明变量传参�?</B>
	 * 
	 * @param functionName 函数名称.注册时用的名�?
	 * @param params 函数参数,支持传入变量
	 * @return 执行结果
	 */
	public static Object call(String functionName, List<Object> params) {
		Object result = ERROR_RESULT;
		if(params == null) {
			params = new ArrayList<Object>();
		}
			
		//当函数未注册�?,尝试自动注册
		if(!checkRegister(functionName) && !autoRegister(functionName)) {
			log.error(REGISTER_FUNCTION_ERROR.concat(
					"The function [{}] has not been registered."), functionName);
			return ERROR_RESULT;
		}
		
		//构造函数表达式
		StringBuilder exp = new StringBuilder();
		exp.append(functionName).append('(');
		
		for(Object param : params) {
			
			//字符串参数且不是变量名称,必须加上双引�?,否则jep认为是不存在的变�?
			if(param instanceof String && 
					!param.toString().matches(
							"^\\" + VAR_CH + ".*\\" + VAR_CH + "$")) {
				exp.append('\"').append(param).append("\",");
				
			} else {
				exp.append(param).append(',');
			}
		}
		exp.setLength(exp.length() - 1);	//去除最后的","
		exp.append(')');
		String funExpression = exp.toString();
		
		//调用函数
		result = call(funExpression);
		return result;
	}
	
	/**
	 * 复杂函数调用.需自己拼装完整的函数表达式,支持嵌套.
	 * 最外层函数会做注册检查，但所有函数均不支持自动注�?.
	 * 
	 * 建议若包含自定义函数,需先通过 registerCustomFunction 方法进行注册.
	 * <B>若所传参包含非数字或非字符串类型，只能通过 declare 声明变量传参�?</B>
	 * 
	 * 注意�?
	 * (1)若入参是字符串必须加上双引号,否则会解析失�?.
	 * (2)若入参是变量则需�?$...$包围.
	 * 
	 * @param funExpression 完整的函数表达式,支持嵌套调用.
	 * @return 执行结果
	 */
	public static Object call(String funExpression) {
		Object result = ERROR_RESULT;
		try {
			result = evaluate(funExpression);
		
		} catch (Exception e) {
			String errMsg = PARSE_EXPRESSION_ERROR.concat(funExpression);
			
			//检查最外层函数是否已注�?
			Pattern ptn = Pattern.compile("^([^\\(]+)\\(.*$");
			Matcher mth = ptn.matcher(funExpression);
			if(mth.find()) {
				String functionName = mth.group(1);
				if(checkRegister(functionName) == false) {
					errMsg = StrUtils.concat(REGISTER_FUNCTION_ERROR, 
							"The function [", functionName, "] has not been registered.");
				}
			}
			log.error(errMsg, e);
		}
		return result;
	}
	
	/**
	 * 获取所有已声明的变量名�?
	 * @return 所有已声明的变量名�?
	 */
	public static List<String> getAllVariablesName() {
		List<String> vars = new LinkedList<String>();
		for(Iterator<String> varNameIts = 
				jep.getVariableTable().keySet().iterator(); 
				varNameIts.hasNext();) {
			vars.add(varNameIts.next());
		}
		return vars;
	}
	
	/**
	 * 获取所有可用的函数名称,包括jep提供的默认函�?.
	 * 
	 * JEP-3.3.0 提供的函数表如下�?
	 * (01)三角函数:sin(?),cos(?);
	 * (02)反三角函�?:asin(?),acos(?);
	 * (03)正切函数：tan(?);
	 * (04)反正切函数：atan(x,y),atan2(y,x);
	 * (05)余切函数:cot(?);
	 * (06)双曲函数:sinh(?),cosh(?);
	 * (07)反双曲函�?:asinh(?),acosh(?);
	 * (08)正切双曲函数:tanh(?);
	 * (09)反正切双曲函�?:atanh(?);
	 * (10)正割函数:sec(?);
	 * (11)余割函数:cosec(?);
	 * (12)对数运算:ln(?),lg(?),log(?,?);
	 * (13)复数运算:complex(?,?)[构造复数],arg(?)[幅角],conj(?)[共轭];
	 * (14)幂运�?:exp(?),pow(?,?),sqrt(?,?);
	 * (15)简单数值运�?:sum(?...),abs(?),min(?...),max(?...),mod(?,?),avg(?...);
	 * (16)其他运算:round(?,?)[四舍五入],rand()[随机数],ceil(?)[向上取整],floor(?)[向下取整];
	 * (17)概率函数：binom(?,?,?)[二项分布];
	 * (18)字符串运�?:cut(2?; 3?),trim(?);
	 * (19)强制类型转换:str(?)[字符串],re(?)[实数];
	 * (20)未知功能函数:cmod,if,polar,im,signum;
	 * 
	 * @return 所有函数名�?
	 */
	public static List<String> getAllFunctionsName() {
		List<String> funs = new LinkedList<String>();
		String[] funNames = jep.getFunctionTable().toString().
				substring(11).split(",");
		for(String funName : funNames) {
			if(!"".equals(funName.trim())) {
				funs.add(funName.trim());
			}
		}
		return funs;
	}
	
	/**
	 * 获取所有内部定义的函数名称
	 * @return 所有内部定义的函数名称
	 */
	public static List<String> getAllInnerFunctionsName() {
		return innerFunNames;
	}
	
	/**
	 * 获取所有自定义的函数名�?
	 * @return 所有自定义函数名称
	 */
	public static List<String> getAllCustomFunctionsName() {
		return customFunNames;
	}
	
	/**
	 * 反注册所有变量和函数
	 */
	public static void unregisterAll() {
		unregisterAllVariables();
		unregisterAllFunctions();
	}
	
	/**
	 * 反注册指定变�?.
	 * 此方法实用性不大，同名变量自动覆盖.
	 * @param variableName 变量名称.无需�?$...$包围.
	 */
	public static void unregisterVariable(String variableName) {
		if(false == variableName.matches("^\\$.*\\$$")) {
			variableName = getVarCallName(variableName);
		}
		jep.getVariableTable().remove(variableName);
	}
	
	/**
	 * 反注册所有变�?
	 */
	public static void unregisterAllVariables() {
		jep.getVariableTable().clear();
	}
	
	/**
	 * 反注册指定函�?.
	 * 此方法实用性不大，同名函数自动覆盖.
	 * @param variableName 函数名称
	 */
	public static void unregisterFunction(String funcationName) {
		jep.getFunctionTable().remove(funcationName);
	}
	
	/**
	 * 反注册所有函�?
	 */
	public static void unregisterAllFunctions() {
		jep.getFunctionTable().clear();
		innerFunNames.clear();
		customFunNames.clear();
	}
	
	/**
	 * 函数/表达式解析执行器
	 * @param expression 完整的函�?/表达�?
	 * @return 执行结果
	 * @throws Exception 执行异常
	 */
	private static Object evaluate(String expression) throws Exception {
		jep.parse(expression);
		return jep.evaluate();
	}
	
	/**
	 * 添加内部定义的默认函�?
	 */
	private static void addInnerFunctions() {
		//强制类型转换函数
		jep.addFunction(_Date.NAME, new _Date());
		innerFunNames.add(_Date.NAME);
		
		jep.addFunction(_Double.NAME, new _Double());
		innerFunNames.add(_Double.NAME);
		
		jep.addFunction(_Int.NAME, new _Int());
		innerFunNames.add(_Int.NAME);
		
		jep.addFunction(_Long.NAME, new _Long());
		innerFunNames.add(_Long.NAME);
		
		jep.addFunction(_Null.NAME, new _Null());
		innerFunNames.add(_Null.NAME);
		
		jep.addFunction(_NullStr.NAME, new _NullStr());
		innerFunNames.add(_NullStr.NAME);
		
		jep.addFunction(_String.NAME, new _String());
		innerFunNames.add(_String.NAME);
		
		//数值操作类函数
		jep.addFunction(BaseConvert.NAME, new BaseConvert());
		innerFunNames.add(BaseConvert.NAME);
		
		jep.addFunction(Bin.NAME, new Bin());
		innerFunNames.add(Bin.NAME);
		
		jep.addFunction(Hex.NAME, new Hex());
		innerFunNames.add(Hex.NAME);
		
		jep.addFunction(Oct.NAME, new Oct());
		innerFunNames.add(Oct.NAME);
		
		//SQL操作类函�?
		jep.addFunction(CopyDquote.NAME, new CopyDquote());
		innerFunNames.add(CopyDquote.NAME);
		
		jep.addFunction(CopySquote.NAME, new CopySquote());
		innerFunNames.add(CopySquote.NAME);
		
		//字符串操作类函数
		jep.addFunction(Concat.NAME, new Concat());
		innerFunNames.add(Concat.NAME);
		
		jep.addFunction(Cut.NAME, new Cut());
		innerFunNames.add(Cut.NAME);
		
		jep.addFunction(LTrim.NAME, new LTrim());
		innerFunNames.add(LTrim.NAME);
		
		jep.addFunction(Replace.NAME, new Replace());
		innerFunNames.add(Replace.NAME);
		
		jep.addFunction(Reverse.NAME, new Reverse());
		innerFunNames.add(Reverse.NAME);
		
		jep.addFunction(RTrim.NAME, new RTrim());
		innerFunNames.add(RTrim.NAME);
		
		jep.addFunction(Trim.NAME, new Trim());
		innerFunNames.add(Trim.NAME);
		
		jep.addFunction(SprintfINT.NAME, new SprintfINT());
		innerFunNames.add(SprintfINT.NAME);
		
		jep.addFunction(MTrim.NAME, new MTrim());
		innerFunNames.add(MTrim.NAME);
		
		//时间操作类函�?
		jep.addFunction(Date2Sec.NAME, new Date2Sec());
		innerFunNames.add(Date2Sec.NAME);
		
		jep.addFunction(Now.NAME, new Now());
		innerFunNames.add(Now.NAME);
		
		jep.addFunction(Sec2Date.NAME, new Sec2Date());
		innerFunNames.add(Sec2Date.NAME);
		
		jep.addFunction(GetLongTime.NAME, new GetLongTime());
		innerFunNames.add(GetLongTime.NAME);
		
		jep.addFunction(GetYear.NAME, new GetYear());
		innerFunNames.add(GetYear.NAME);
		
		//其他函数
		jep.addFunction(IfElse.NAME, new IfElse());
		innerFunNames.add(IfElse.NAME);
		
		jep.addFunction(Macro.NAME, new Macro());
		innerFunNames.add(Macro.NAME);
		
		jep.addFunction(Print.NAME, new Print());
		innerFunNames.add(Print.NAME);
		
		jep.addFunction(Wait.NAME, new Wait());
		innerFunNames.add(Wait.NAME);
	}
	
}
