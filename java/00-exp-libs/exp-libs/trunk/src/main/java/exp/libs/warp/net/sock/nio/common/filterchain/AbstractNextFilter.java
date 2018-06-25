package exp.libs.warp.net.sock.nio.common.filterchain;

import exp.libs.warp.net.sock.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.sock.nio.common.interfaze.IFilter;
import exp.libs.warp.net.sock.nio.common.interfaze.ISession;

/**
 * <pre>
 * 抽象关系过滤器
 * 
 * 定义了关系过滤器内部的基本属性
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class AbstractNextFilter implements INextFilter {

	/**
	 * 关系过滤器名�?
	 */
	protected String filterName;

	/**
	 * 下一个关系过滤器
	 */
	protected AbstractNextFilter nextFilter;

	/**
	 * 上一个关系过滤器
	 */
	protected AbstractNextFilter preFilter;

	/**
	 * 业务过滤�?
	 */
	protected IFilter filter;

	/**
	 * 过滤链实�?
	 */
	protected FilterChain filterChain;

	/**
	 * 构造函�?
	 * @param filter 业务过滤�?
	 */
	public AbstractNextFilter(IFilter filter) {
		this.filter = filter;
	}

	/**
	 * 处理异常，把异常抛给过滤�?
	 * @param session 会话
	 * @param exception 异常
	 */
	protected void fireExceptionCaught(ISession session, Throwable exception) {
		filterChain.onExceptionCaught(session, exception);
	}

	/**
	 * 获取关系过滤器名�?
	 * @return 关系过滤器名�?
	 */
	public String getFilterName() {
		return filterName;
	}

	/**
	 * 设置关系过滤器名�?
	 * @param filterName 关系过滤器名�?
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * 获取下一个关系过滤器名称
	 * @return 下一个关系过滤器名称
	 */
	public AbstractNextFilter getNextFilter() {
		return nextFilter;
	}

	/**
	 * 设置下一个关系过滤器名称
	 * @param nextFilter 下一个关系过滤器名称
	 */
	public void setNextFilter(AbstractNextFilter nextFilter) {
		this.nextFilter = nextFilter;
	}

	/**
	 * 获取上一个关系过滤器名称
	 * @return 上一个关系过滤器名称
	 */
	public AbstractNextFilter getPreFilter() {
		return preFilter;
	}

	/**
	 * 设置上一个关系过滤器名称
	 * @param preFilter 上一个关系过滤器名称
	 */
	public void setPreFilter(AbstractNextFilter preFilter) {
		this.preFilter = preFilter;
	}

	/**
	 * 获取业务过滤�?
	 * @return 业务过滤�?
	 */
	public IFilter getFilter() {
		return filter;
	}

	/**
	 * 设置业务过滤�?
	 * @param filter 业务过滤�?
	 */
	public void setFilter(IFilter filter) {
		this.filter = filter;
	}

	/**
	 * 获取过滤�?
	 * @return 过滤�?
	 */
	public FilterChain getFilterChain() {
		return filterChain;
	}

	/**
	 * 设置过滤�?
	 * @param filterChain 过滤�?
	 */
	public void setFilterChain(FilterChain filterChain) {
		this.filterChain = filterChain;
	}

}
