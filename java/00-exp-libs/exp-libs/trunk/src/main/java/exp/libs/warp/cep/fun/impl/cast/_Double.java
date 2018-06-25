package exp.libs.warp.cep.fun.impl.cast;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.CEPParseException;
import exp.libs.warp.cep.fun.BaseFunction1;

/**
 * <pre>
 * 自定函数：
 * 	强制类型转换: String -> Double
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _Double extends BaseFunction1 {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -6336189456459865860L;

	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "double";
	
	/**
	 * 强制类型转换: String -> Double
	 * �?1个入参：
	 * @param1 String:数字字符�?
	 * @return Double
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(Object param) throws EvaluationException {
		Double dNum = new Double(-1D);
		String sNum = asString(1, param);
		
		try {
			dNum = Double.parseDouble(sNum);
		} catch (NumberFormatException e) {
			throw new CEPParseException(this, 1, sNum, Double.class, e);
		}
		return dNum;
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
