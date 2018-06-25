package exp.libs.warp.cep.fun.impl.cast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.CEPParseException;
import exp.libs.warp.cep.fun.BaseFunctionN;

/**
 * <pre>
 * 自定函数：
 * 	强制类型转换: String -> Date
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _Date extends BaseFunctionN {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = 6634078944769182381L;

	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "date";
	
	/**
	 * 限定参数个数�?1�?2.
	 */
	@Override
	public boolean checkNumberOfParameters(int inParamsNum){
        return inParamsNum == 1 || inParamsNum == 2;
    }
	
	/**
	 * 强制类型转换: String -> Date
	 * 1个或2个入参：
	 * @param1 String:日期字符�?
	 * @param1 String:日期字符串的格式，无此参数则认为�? yyyy-MM-dd HH:mm:ss 格式
	 * @return Date
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(List<Object> params) throws EvaluationException {
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		int size = params.size();
		if(size == 2) {
			dateFormat = asString(2, params.get(1));
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String dataStr = asString(1, params.get(0));
		Date date = new Date();
		try {
			date = sdf.parse(dataStr);
		} catch (ParseException e) {
			throw new CEPParseException(this, 1, dataStr, dateFormat, e);
		}
		return date;
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
