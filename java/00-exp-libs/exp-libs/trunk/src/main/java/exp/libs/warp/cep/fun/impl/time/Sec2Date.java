package exp.libs.warp.cep.fun.impl.time;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.fun.BaseFunction1;

/**
 * <pre>
 * 自定函数：
 * 	纪元秒 -> yyyy-MM-dd HH:mm:ss 转换
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Sec2Date extends BaseFunction1 {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -4753226223001415587L;

	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "sec2date";
	
	/**
	 * 纪元�? -> yyyy-MM-dd HH:mm:ss 转换.
	 * �?1个入参：
	 * @param1 Long: 纪元�?
	 * @return String: yyyy-MM-dd HH:mm:ss格式的日�?
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(Object param) throws EvaluationException {
		long second = asLong(1, param);
		Date date = new Date(second);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
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
