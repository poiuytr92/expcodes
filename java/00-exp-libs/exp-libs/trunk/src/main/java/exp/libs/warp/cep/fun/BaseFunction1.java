package exp.libs.warp.cep.fun;

import java.util.List;

import com.singularsys.jep.EvaluationException;

/**
 * <pre>
 * 自定义函数基类。
 * 允许传参个数为1个。
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class BaseFunction1 extends BaseFunctionN {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = 6444148167565368874L;

	/**
	 * 构造函�?
	 */
	protected BaseFunction1() {
		super(1);	//定义入参个数�?1
	}
	
	/**
	 * 检查入参个�?,限定参数个数�?1.
	 * 不允许子类覆�?,保证 eval 操作的入参个数不会异�?.
	 */
	@Override
	public final boolean checkNumberOfParameters(int inParamsNum){
        return inParamsNum == 1;
    }
	
	/**
	 * 转嫁基类的函数接�?,仅取�?1个参�?,由子类实现函数逻辑
	 * @param params 由Cep解析后传入的参数�?,不会为null
	 * @return 执行结果
	 * @throws EvaluationException 执行异常
	 */
	@Override
	protected Object eval(List<Object> params) throws EvaluationException {
		return eval(params.remove(0));
	}
	
	/**
	 * 自定义函数接�?,由子类实现函数逻辑
	 * @param param 由Cep解析后传入的参数
	 * @return 执行结果
	 * @throws EvaluationException 执行异常
	 */
	protected abstract Object eval(Object param)
			throws EvaluationException;
	
	/**
	 * 获取函数名称
	 * @return 函数名称
	 */
	public abstract String getName();
	
}
