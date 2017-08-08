package exp.libs.warp.net.sock.nio.client;

import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.nio.common.cache.NioConfig;
import exp.libs.warp.net.sock.nio.common.filter.ThreadPoolFilter;
import exp.libs.warp.net.sock.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.sock.nio.common.interfaze.IHandler;

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
class NioClientConfig extends NioConfig {

	protected NioClientConfig(SocketBean socketBean, IHandler handler) {
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
