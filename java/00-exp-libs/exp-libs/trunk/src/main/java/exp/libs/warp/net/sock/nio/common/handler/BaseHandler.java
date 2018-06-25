package exp.libs.warp.net.sock.nio.common.handler;

import exp.libs.warp.net.sock.nio.common.envm.States;
import exp.libs.warp.net.sock.nio.common.interfaze.IHandler;
import exp.libs.warp.net.sock.nio.common.interfaze.ISession;

/**
 * <pre>
 * 基本业务处理器
 * 
 * 用于在客户编写的业务逻辑前后添加默认操作
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class BaseHandler extends AbstractHandler {

	/**
	 * 构造函�?
	 * @param handler 客户业务处理�?
	 */
	public BaseHandler(IHandler handler) {
		super(handler);
	}

	/**
	 * <pre>
	 * 会话验证事件处理逻辑
	 * </pre>
	 * 
	 * @param session 会话
	 * @throws Exception 异常
	 */
	@Override
	public void onSessionCreated(ISession session) throws Exception {

		handler.onSessionCreated(session);
		
		//如果客户逻辑没有进行过验证，则认为验证成�?
		if(session.isVerfied() == false) {
			session.setVerfyState(true);
			
		} else if(session.getState() == States.VERIFY_FAIL) {
			//TODO: EVENT: 验证失败
		}
	}

	/**
	 * <pre>
	 * 消息接收事件处理逻辑
	 * </pre>
	 * 
	 * @param session 会话
	 * @param msg 消息
	 * @throws Exception 异常
	 */
	@Override
	public void onMessageReceived(ISession session, Object msg) throws Exception {
		
		handler.onMessageReceived(session, msg);
	}

	/**
	 * <pre>
	 * 异常事件处理逻辑
	 * </pre>
	 * @param session 会话
	 * @param exception 异常
	 */
	@Override
	public void onExceptionCaught(ISession session, Throwable exception) {
		handler.onExceptionCaught(session, exception);
	}

}
