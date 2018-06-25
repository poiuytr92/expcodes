package exp.libs.warp.cep.fun.impl.num;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.CEPParseException;
import exp.libs.warp.cep.fun.BaseFunction1;

/**
 * <pre>
 * 自定函数：
 * 	进制转换： 10 -> 8
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Oct extends BaseFunction1 {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = 4706890338047487794L;
	
	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "oct";
	
	/**
	 * 进制转换�? 10 -> 8
	 * �?1个入参：
	 * @param1 Integer/String:10进制数�?(字符�?)
	 * @return String: 8进制数值字符串
	 * @exception EvaluationException 若解析失败则抛出异常
	 */
	@Override
	protected Object eval(Object param) throws EvaluationException {
		int iNum = 0;
		
		if(param instanceof String) {
			String sNum = asString(1, param);
			try {
				iNum = Integer.parseInt(sNum);
			} catch (NumberFormatException e) {
				throw new CEPParseException(this, 1, sNum, Integer.class, e);
			}
			
		} else {
			iNum = asInt(1, param);
		}
		return Integer.toOctalString(iNum);
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
