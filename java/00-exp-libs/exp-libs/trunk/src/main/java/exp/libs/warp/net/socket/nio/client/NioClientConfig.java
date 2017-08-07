package exp.libs.warp.net.socket.nio.client;

import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.nio.common.cache.NioConfig;
import exp.libs.warp.net.socket.nio.common.filter.ThreadPoolFilter;
import exp.libs.warp.net.socket.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.socket.nio.common.interfaze.IHandler;

/**
 * <pre>
 * NIOSocket客户端配置类。
 * Socket公共配置继承自SocketBean类。
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NioClientConfig extends NioConfig {

	public NioClientConfig(IHandler handler) {
		super(handler);
	}

	public NioClientConfig(SocketBean socketBean, IHandler handler) {
		super(socketBean, handler);
	}
	
	/**
	 * 客户端默认移除线程池过滤器
	 */
	@Override
	protected void initFilterChain() {
		delFilter(ThreadPoolFilter.class.getSimpleName());
	}
	
	/**
	 * 获取过滤链
	 * @return
	 */
	protected FilterChain getFilterChain() {
		return filterChain;
	}

}
