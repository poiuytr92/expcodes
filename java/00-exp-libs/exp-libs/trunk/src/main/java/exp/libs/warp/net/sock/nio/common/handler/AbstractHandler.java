package exp.libs.warp.net.sock.nio.common.handler;

import exp.libs.warp.net.sock.nio.common.interfaze.IHandler;

/**
 * <pre>
 * 业务处理抽象类
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
abstract class AbstractHandler implements IHandler {

	/**
	 * 客户业务处理接口
	 */
	protected IHandler handler;

	/**
	 * 构造函�?
	 * @param handler 客户业务处理�?
	 */
	public AbstractHandler(IHandler handler) {
		this.handler = handler;
	}

	/**
	 * 设置客户实现的业务处理器
	 * @param handler 业务处理�?
	 */
	public void setHandler(IHandler handler) {
		this.handler = handler;
	}

}
