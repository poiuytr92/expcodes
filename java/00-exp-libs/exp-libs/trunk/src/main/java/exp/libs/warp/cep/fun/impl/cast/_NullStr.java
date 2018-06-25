package exp.libs.warp.cep.fun.impl.cast;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.fun.BaseFunction1;

/**
 * <pre>
 * 自定函数：
 * 	强制类型转换: 强制把任何值转换为空串
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _NullStr extends BaseFunction1 {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = 6734551744369308465L;
	
	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "nullstr";
	
	/**
	 * 强制类型转换: 强制把任何值转换为空串
	 * �?1个入参：
	 * @param1 Object:各种类型�?
	 * @return String:""
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(Object param) throws EvaluationException {
		return "";
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
