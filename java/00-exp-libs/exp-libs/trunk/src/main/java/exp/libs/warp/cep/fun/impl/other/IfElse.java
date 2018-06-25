package exp.libs.warp.cep.fun.impl.other;

import java.util.List;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.fun.BaseFunctionN;

/**
 * <pre>
 * 自定函数：
 * 	条件选择
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class IfElse extends BaseFunctionN {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -266592048304197949L;

	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "if";
	
	/**
	 * 限定参数个数�?2~5.
	 */
	@Override
	public boolean checkNumberOfParameters(int inParamsNum){
        return (inParamsNum >= 2 && inParamsNum <= 5);
    }
	
	/**
	 * 条件选择函数.
	 * 可自由选择2~5个参�?,不同个数的参�?,其参数类型、选择逻辑也不�?.
	 */
	@Override
	protected Object eval(List<Object> params) throws EvaluationException {
		int size = params.size();
		Object result = null;
		
		switch(size) {
			case 2: {
				result = eval2(params);
				break;
			}
			case 3: {
				result = eval3(params);
				break;
			}
			case 4: {
				result = eval4(params);
				break;
			}
			case 5: {
				result = eval5(params);
				break;
			}
		}
		return result;
	}
	
	/**
	 * 2参数功能�?
	 * 	if x != empty && y != empty return x
	 * 	if x != empty && y == empty return x
	 * 	if x == empty && y != empty return y
	 * 	if x == empty && y == empty return ""
	 * 
	 * �?:empty �? null �? ""
	 * 
	 * @param1 String:字符串x
	 * @param2 String:字符串y
	 * @return String
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	private Object eval2(List<Object> params) throws EvaluationException {
		String strX = asString(1, params.remove(0));
		String strY = asString(2, params.remove(0));
		String result = "";
		
		if(isEmpty(strX) == false) {
			result = strX;
			
		} else if(isEmpty(strY) == false) {
			result = strY;
		}
		return result;
	}
	
	/**
	 * 3参数功能�?
	 * 	if x == empty return y
	 * 	if x != empty return z
	 * 
	 * �?:empty �? null �? ""
	 * 
	 * @param1 String:字符串x
	 * @param2 String:字符串y
	 * @param3 String:字符串z
	 * @return String
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	private Object eval3(List<Object> params) throws EvaluationException {
		String strX = asString(1, params.remove(0));
		String strY = asString(2, params.remove(0));
		String strZ = asString(3, params.remove(0));
		String result = "";
		
		if(isEmpty(strX) == true) {
			result = strY;
			
		} else {
			result = strZ;
		}
		return result;
	}
	
	/**
	 * 4参数功能�?
	 * 	if x == y return a
	 * 	if x != y return b
	 * 
	 * �?:
	 * 字符串若同为empty,则认为是相等.
	 * empty �? null �? ""
	 * 
	 * @param1 String:字符串x
	 * @param2 String:字符串y
	 * @param3 String:字符串a
	 * @param4 String:字符串b
	 * @return String
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	private Object eval4(List<Object> params) throws EvaluationException {
		String strX = asString(1, params.remove(0));
		String strY = asString(2, params.remove(0));
		String strA = asString(3, params.remove(0));
		String strB = asString(4, params.remove(0));
		
		String result = "";
		if(isEmpty(strX) == isEmpty(strY)) {
			result = strA;
			
		} else {
			result = strB;
		}
		return result;
	}
	
	/**
	 * 5参数功能�?
	 * 	if x < y && x > z return a
	 * 	if x >= y && x <= z return b
	 * 
	 * @param1 double:实数x
	 * @param2 double:实数y
	 * @param3 double:实数z
	 * @param4 Object:不定类型参数a
	 * @param5 Object:不定类型参数b
	 * @return Object
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	private Object eval5(List<Object> params) throws EvaluationException {
		double numX = asDouble(1, params.remove(0));
		double numY = asDouble(2, params.remove(0));
		double numZ = asDouble(3, params.remove(0));
		Object paramA = params.remove(0);
		Object paramB = params.remove(0);
		
		Object result = null;
		if(numX < numY && numX > numZ) {
			result = paramA;
			
		} else {
			result = paramB;
		}
		return result;
	}

	/**
	 * 判断字符串是否为null或空�?
	 * @param str 待判断字符串
	 * @return true:�?; false:非空
	 */
	private boolean isEmpty(String str) {
		boolean isNull = false;
		if(str == null || "".equals(str)) {
			isNull = true;
		}
		return isNull;
	}
	
	/**
	 * 获取函数名称
	 * @return 函数名称
	 */
	@Override
	public String getName() {
		return NAME;
	}
	
}
