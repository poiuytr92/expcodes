package exp.libs.warp.net.sock.io.server;

import exp.libs.warp.net.sock.io.common.ISession;


public interface IHandler {

	/**
	 * 业务处理逻辑.
	 * 	离开此方法的控制域后, session会话依然存活.
	 * 	默认的会话关闭方式是 客户端关闭 或 超时关闭, 但根据实际需要也可在此方法内直接close会话
	 * @param session 客户端代理会话
	 */
	public void _handle(ISession session);
	
	/**
	 * 若实现类的构造函数中存在持久化对象(即退出_handle方法后不会被注销的非常量的活动对象), 则必须实现此接口.
	 *  若不实现克隆接口, 此实现类的引用会被 Socket服务端的所有 客户端代理线程共用, 该活动对象可能出现问题.
	 *  
	 * 若实现类的构造函数中不存在持久化对象, 则可以不实现此接口.
	 * 	此时意味着允许 Socket服务端的所有 客户端代理线程共用此实现类的引用.
	 * @return
	 */
	public IHandler _clone();
	
}
