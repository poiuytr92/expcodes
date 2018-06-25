package exp.libs.warp.cep.fun.impl.other;

import com.singularsys.jep.EvaluationException;

import exp.libs.warp.cep.fun.BaseFunction1;

/**
 * <pre>
 * 自定函数：
 * 	使执行线程休眠
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Wait extends BaseFunction1 {

	/**
	 * 序列化唯一ID
	 */
	private static final long serialVersionUID = -6571366584139724081L;

	/**
	 * 建议函数�?,方便调用.
	 * 可不使用.
	 */
	public final static String NAME = "wait";
	
	/**
	 * 使执行线程休�? X ms.
	 * �?1个入参：
	 * @param1 Long:休眠时长,单位ms
	 * @return Boolean: true:休眠成功; false:休眠失败
	 * @throws EvaluationException 若执行失败则抛出异常
	 */
	@Override
	protected Object eval(Object param) throws EvaluationException {
		long waitTime = asLong(1, param);
		Boolean isWait = true;
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			isWait = false;
		}
		return isWait;
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
