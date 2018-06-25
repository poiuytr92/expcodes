package exp.libs.warp.net.sock.nio.common.filterchain;

import exp.libs.warp.net.sock.nio.common.interfaze.ISession;

/**
 * <pre>
 * 关系过滤器接口
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface INextFilter {

	/**
	 * 触发下一个业务过滤器的onSessionCreated事件
	 * @param session 会话
	 */
	public void onSessionCreated(ISession session);

	/**
	 * 触发下一个业务过滤器的onMessageReceived事件
	 * @param session 会话
	 * @param msg 接收消息
	 */
	public void onMessageReceived(ISession session, Object msg);

	/**
	 * 触发上一个业务过滤器的onMessageSent事件
	 * @param session 会话
	 * @param msg 发送消�?
	 */
	public void onMessageSent(ISession session, Object msg);

	/**
	 * 触发下一个业务过滤器的onExceptionCaught事件
	 * @param session 会话
	 * @param exception 异常
	 */
	public void onExceptionCaught(ISession session, Throwable exception);

}
