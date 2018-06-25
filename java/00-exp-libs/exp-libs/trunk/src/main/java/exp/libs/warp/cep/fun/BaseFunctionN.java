package exp.libs.warp.cep.fun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.functions.IllegalParameterException;
import com.singularsys.jep.functions.PostfixMathCommand;

/**
 * <pre>
 * 自定义函数基类。
 * 允许传参个数为 N>=0 个。
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class BaseFunctionN extends PostfixMathCommand {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -4411960211324903595L;

	/**
	 * 构造函�?
	 */
	protected BaseFunctionN() {
		super(-1);	//定义不限数量的入参个�?
	}
	
	/**
	 * 构造函�?
	 * @param parameterNum 定义入参个数
	 */
	protected BaseFunctionN(int parameterNum) {
		super(parameterNum);
	}
	
	/**
	 * 检查入参个�?,做参数个数限�?.
	 * 不在此范围内的参数个�?,在解析表达式时就会自动抛出异�?.
	 * 保证异常不会在eval方法中抛�?.
	 * <B>建议子类根据实际需要的参数个数对此方法进行覆写�?<B>
	 */
	@Override
	public boolean checkNumberOfParameters(int inParamsNum){
        return inParamsNum >= 0;
    }
	
	/**
	 * 堆栈运算
	 */
	@Override
	public final void run(Stack<Object> stack) throws EvaluationException {
		
		//弹栈取参
		List<Object> params = new ArrayList<Object>(curNumberOfParameters);
        for(int i = 0; i < curNumberOfParameters ; i++) {
        	params.add(0, stack.pop());	//弹栈与压栈时的顺序是相反�?,因此永远都要放到队头
        }
        
        //自定义运�?
        Object result = eval(params);
        
        //压栈返回结果
        stack.push(result);
	}
	
	/**
	 * 自定义函数接�?,由子类实现函数逻辑
	 * @param params 由Cep解析后传入的参数�?,不会为null
	 * @return 执行结果
	 * @throws EvaluationException 执行异常
	 */
	protected abstract Object eval(List<Object> params) 
			throws EvaluationException;
	
	
	/**
	 * 参考JEP自带的类型转换补充的 Object -> Date
	 * @param i 参数序号
	 * @param obj 参数对象
	 * @return Date
	 * @throws IllegalParameterException 参数非法
	 */
	protected Date asDate(int i, Object obj) throws IllegalParameterException {
		if (!(obj instanceof Date)) {
			throw new IllegalParameterException(this, i, Date.class, obj);
		} else {
			return (Date) obj;
		}
	}
	
	/**
	 * 获取函数名称
	 * @return 函数名称
	 */
	public abstract String getName();
	
}
