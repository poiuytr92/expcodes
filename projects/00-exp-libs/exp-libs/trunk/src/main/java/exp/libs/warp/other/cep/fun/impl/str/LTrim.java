package exp.libs.warp.other.cep.fun.impl.str;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.other.cep.fun.BaseFunction1;

/**
 * <pre>
 * 自定函数：
 * 	字符串操作：去除字符串左侧空字符.
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LTrim extends BaseFunction1 {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -3842868018959587426L;

	/**
	 * 建议函数名,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "ltrim";
	
	/**
	 * 去除字符串左侧空字符.
	 * 共1个入参：
	 * @param1 String:原字符串
	 * @return String
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(Object param) throws EvaluationException {
		String str = asString(1, param);
		return str.replaceAll("^\\s*", "");
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
