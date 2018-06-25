package exp.libs.warp.cep.fun.impl.str;

import java.util.Formatter;
import java.util.List;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.fun.BaseFunctionN;

/**
 * <pre>
 * 自定函数：
 * 	获取C语言 sprintf 风格的格式字符串（仅针对int入参）
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SprintfINT extends BaseFunctionN {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = 2987656382495060819L;
	
	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "sprintf";
	
	/**
	 * 限定参数个数�?2.
	 */
	@Override
	public boolean checkNumberOfParameters(int inParamsNum){
        return inParamsNum == 2;
    }
	
	/**
	 * printf 风格的格式字符串
	 * �?2个参数：
	 * @param1 String:sprintf 表达�? C语言符号 %02d
	 * @param2 Integer:仅支持数值入�?
	 * @return String
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(List<Object> params) throws EvaluationException {
		String exp = asString(1, params.remove(0));
		int var = asInt(2, params.remove(0));
		Formatter fmt = new Formatter();
		fmt.format(exp, var);
		String rst = fmt.toString();
		fmt.close();
		return rst;
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
