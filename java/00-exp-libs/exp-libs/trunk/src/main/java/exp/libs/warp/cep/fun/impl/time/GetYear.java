package exp.libs.warp.cep.fun.impl.time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.fun.BaseFunctionN;

/**
 * <pre>
 * 自定函数：
 * 	获取年份
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class GetYear extends BaseFunctionN {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -732319624881462843L;
	
	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "getYear";
	
	/**
	 * 限定参数个数�?0�?1.
	 */
	@Override
	public boolean checkNumberOfParameters(int inParamsNum){
        return inParamsNum == 0 || inParamsNum == 1;
    }
	
	/**
	 * 获取当前系统时间的字符串.
	 * 可�?0个或1个入参：
	 * @param1 String(可�?):获取时间的格�?,若无此参数则默认�? yyyy-MM-dd HH:mm:ss
	 * @return String
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(List<Object> params) throws EvaluationException {
		String dateFormat = "yyyy";
		if(params.size() == 1) {
			dateFormat = asString(1, params.remove(0));
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date now = new Date();
		return sdf.format(now);
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
