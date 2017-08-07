package exp.libs.warp.net.socket.nio.server;

import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.nio.common.cache.MsgQueue;
import exp.libs.warp.net.socket.nio.common.cache.NioConfig;
import exp.libs.warp.net.socket.nio.common.filter.ThreadPoolFilter;
import exp.libs.warp.net.socket.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.socket.nio.common.interfaze.IHandler;

/**
 * <pre>
 * NIOSocket服务器配置类。
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NioServerConfig extends NioConfig {

	public NioServerConfig(IHandler handler) {
		super(handler);
	}

	public NioServerConfig(SocketBean socketBean, IHandler handler) {
		super(socketBean, handler);
	}
	
	/**
	 * 服务端默认添加线程池过滤器
	 */
	@Override
	protected void initFilterChain() {
		addFilter(ThreadPoolFilter.class.getSimpleName(), 
				new ThreadPoolFilter(getMaxConnectionCount(), MsgQueue.MAX_MSG_LIMIT));
	}
	
	/**
	 * 获取过滤链
	 * @return
	 */
	protected FilterChain getFilterChain() {
		return filterChain;
	}

}
