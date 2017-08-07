package exp.libs.warp.net.sock.nio.common.cache;

import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.sock.nio.common.interfaze.IConfig;
import exp.libs.warp.net.sock.nio.common.interfaze.IFilter;
import exp.libs.warp.net.sock.nio.common.interfaze.IHandler;

/**
 * <pre>
 * NIOSocket本地机配置类。
 * Socket公共配置继承自SocketBean类。
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class NioConfig extends SocketBean implements IConfig {

	/** 业务处理器  */
	private IHandler handler;

	/** 过滤链 */
	protected FilterChain filterChain;

	/**
	 * 
	 * @param handler 业务处理器
	 */
	public NioConfig(IHandler handler) {
		this(null, handler);
	}
	
	/**
	 * @param sb 从配置文件获取的配置实体
	 * @param handler 业务处理器
	 */
	public NioConfig(SocketBean socketBean, IHandler handler) {
		super(socketBean);
		
		this.handler = handler;
		this.filterChain = new FilterChain();
		filterChain.setHandler(handler);
		
		initFilterChain();
	}

	/**
	 * <pre>
	 * 初始化过滤链
	 * </pre>
	 */
	protected abstract void initFilterChain();
	
	/**
	 * 添加过滤器.
	 * 此方法只在服务端启动前调用才生效.
	 * @param name 过滤器名称
	 * @param filter 过滤器接口
	 */
	public void addFilter(String name, IFilter filter) {
		filterChain.addFilter(name, filter);
	}
	
	/**
	 * 移除过滤器.
	 * 此方法只在服务端启动前调用才生效.
	 * @param name 过滤器名称
	 * @param filter 过滤器接口
	 */
	public void delFilter(String name) {
		filterChain.removeFilter(name);
	}
	
	/**
	 * 清除所有过滤器
	 */
	public void clearFilters() {
		filterChain.clean();
	}
	
	/**
	 * 获取业务逻辑处理器
	 * @return 业务逻辑处理器
	 */
	public IHandler getHandler() {
		return handler;
	}

}
