package exp.libs.warp.cep.fun.impl.str;

import java.util.List;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.fun.BaseFunctionN;

/**
 * <pre>
 * 自定函数：
 * 	字符串操作： 无限拼接
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Concat extends BaseFunctionN {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -8008474521815772996L;

	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "concat";
	
	/**
	 * 字符串拼�?.若出现null串则跳过.
	 * N个入参：
	 * @param1 String:字符�?1
	 * @param2 String:字符�?2
	 * @param... String:字符�?...
	 * @paramN String:字符串N
	 * @return String: 所有字符串顺序拼接
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(List<Object> params) throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		int pidx = 0;
		for(Object oParam : params) {
			if(oParam == null) {
				continue;
			}
			String sParam = asString(++pidx, oParam);
			sb.append(sParam);
		}
		return sb.toString();
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
