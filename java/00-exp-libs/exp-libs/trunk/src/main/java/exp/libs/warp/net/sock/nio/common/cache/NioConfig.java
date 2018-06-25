package exp.libs.warp.net.sock.nio.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.sock.nio.common.handler._DefaultHandler;
import exp.libs.warp.net.sock.nio.common.interfaze.IConfig;
import exp.libs.warp.net.sock.nio.common.interfaze.IFilter;
import exp.libs.warp.net.sock.nio.common.interfaze.IHandler;

/**
 * <pre>
 * NIOSocket本地机配置类。
 * Socket公共配置继承自SocketBean类。
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class NioConfig extends SocketBean implements IConfig {

	private final static Logger log = LoggerFactory.getLogger(NioConfig.class);
	
	private final static String A_BRACKET = "@{";
	
	private final static String Z_BRACKET = "}@";
	
	/**
	 * 接收消息分隔符集
	 */
	private String[] readDelimiters;
	
	/** 业务处理�?  */
	private IHandler handler;

	/** 过滤�? */
	protected FilterChain filterChain;

	/**
	 * @param sb 从配置文件获取的配置实体
	 * @param handler 业务处理�?
	 */
	public NioConfig(SocketBean socketBean, IHandler handler) {
		super(socketBean);
		
		this.readDelimiters = StrUtils.split(
				getReadDelimiter(), A_BRACKET, Z_BRACKET);
		if(ListUtils.isEmpty(readDelimiters)) {
			readDelimiters = new String[] { getReadDelimiter() };
		}
		
		this.handler = (handler == null ? new _DefaultHandler() : handler);
		this.filterChain = new FilterChain();
		filterChain.setHandler(this.handler);
		initFilterChain();
	}

	@Deprecated
	@Override
	public String getReadDelimiter() {
		return super.getReadDelimiter();
	}
	
	public String[] getReadDelimiters() {
		return readDelimiters;
	}
	
	/**
	 * <pre>
	 * 初始化过滤链
	 * </pre>
	 */
	protected abstract void initFilterChain();
	
	/**
	 * 添加过滤�?.
	 * 此方法只在服务端启动前调用才生效.
	 * @param name 过滤器名�?
	 * @param filter 过滤器接�?
	 */
	public void addFilter(String name, IFilter filter) {
		filterChain.addFilter(name, filter);
	}
	
	/**
	 * 移除过滤�?.
	 * 此方法只在服务端启动前调用才生效.
	 * @param name 过滤器名�?
	 * @param filter 过滤器接�?
	 */
	public void delFilter(String name) {
		filterChain.removeFilter(name);
	}
	
	/**
	 * 清除所有过滤器
	 */
	public void clearFilters() {
		try {
			filterChain.clean();
		} catch(Exception e) {
			log.error("清理过滤器资源失�?", e);
		}
	}
	
	/**
	 * 获取业务逻辑处理�?
	 * @return 业务逻辑处理�?
	 */
	public IHandler getHandler() {
		return handler;
	}

}
