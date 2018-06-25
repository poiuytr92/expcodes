package exp.libs.warp.net.sock.nio.common.filterchain.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import exp.libs.warp.net.sock.nio.common.filterchain.AbstractNextFilter;
import exp.libs.warp.net.sock.nio.common.filterchain.INextFilter;
import exp.libs.warp.net.sock.nio.common.interfaze.IFilter;
import exp.libs.warp.net.sock.nio.common.interfaze.IHandler;
import exp.libs.warp.net.sock.nio.common.interfaze.ISession;

/**
 * <pre>
 * 过滤链
 * 
 * 过滤链实际也是一个关系过滤器
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class FilterChain implements INextFilter {

	/**
	 * 链头，必�?
	 */
	private HeadFilter headFilter;

	/**
	 * 链尾，必�?
	 */
	private TailFilter tailFilter;

	/**
	 * 业务处理器，置于链尾接驳业务逻辑
	 */
	private IHandler handler;

	/**
	 * 过滤链主�?
	 */
	private Map<String, AbstractNextFilter> filterChain;

	/**
	 * <pre>
	 * 构造函�?
	 * </pre>
	 */
	public FilterChain() {
		this.filterChain = new HashMap<String, AbstractNextFilter>();
		this.init();
	}

	/**
	 * <pre>
	 * 初始化过滤链
	 * </pre>
	 */
	private void init() {
		this.headFilter = new HeadFilter(new BaseFilter());
		this.tailFilter = new TailFilter(new BaseFilter());

		this.headFilter.setFilterName("HeadFilter");
		this.tailFilter.setFilterName("TailFilter");

		this.headFilter.setFilterChain(this);
		this.tailFilter.setFilterChain(this);

		this.headFilter.setNextFilter(this.tailFilter);
		this.tailFilter.setPreFilter(this.headFilter);

		this.filterChain.put(this.headFilter.getFilterName(), this.headFilter);
		this.filterChain.put(this.tailFilter.getFilterName(), this.tailFilter);
	}

	/**
	 * <pre>
	 * 重置过滤链，相当于初始化操作
	 * </pre>
	 */
	public void reset() {
		synchronized (filterChain) {
			this.filterChain.clear();
			this.init();
		}
	}

	/**
	 * <pre>
	 * 清空过滤链，程序结束时释放资源时调用
	 * </pre>
	 */
	public void clean() {
		synchronized (filterChain) {
			for (Iterator<String> keys = filterChain.keySet().iterator(); 
					keys.hasNext();) {
				
			   String key = (String) keys.next();
			   AbstractNextFilter filter = filterChain.get(key);
			   filter.getFilter().clean();
			}
			
			// 不清空过滤器，确保重启服务时过滤链仍然可�?
//			this.filterChain.clear();
//			this.filterChain = null;
		}
	}

	/**
	 * <pre>
	 * 添加自定义过滤器
	 * 
	 * 建议过滤器名称与过滤器的类名保持一致，使得在添加同名过滤器时，可以覆盖
	 * </pre>
	 * 
	 * @param filterName 过滤器名�?
	 * @param newFilter 新过滤器
	 */
	public void addFilter(String filterName, IFilter newFilter) {
		synchronized (filterChain) {
			AbstractNextFilter midFilter = filterChain.get(filterName);

			if (midFilter != null) {
				midFilter.setFilter(newFilter);
			} else {
				midFilter = new BaseNextFilter(newFilter);
				midFilter.setFilterName(filterName);

				midFilter.setNextFilter(this.tailFilter);
				midFilter.setPreFilter(this.tailFilter.getPreFilter());

				this.tailFilter.getPreFilter().setNextFilter(midFilter);
				this.tailFilter.setPreFilter(midFilter);

				this.filterChain.put(filterName, midFilter);
			}
		}
	}
	
	/**
	 * <pre>
	 * 根据过滤器名称移除过滤器，并释放相关资源
	 * </pre>
	 * 
	 * @param filterName 过滤器名�?
	 * @return true:移除成功; false:移除失败
	 */
	public boolean removeFilter(String filterName) {
		boolean isOk = false;

		synchronized (filterChain) {

			if (this.headFilter.getFilterName().equals(filterName)
					|| this.tailFilter.getFilterName().equals(filterName)) {
				isOk = false;
			} else {
				AbstractNextFilter midFilter = filterChain.remove(filterName);

				if (midFilter == null) {
					isOk = false;
				} else {
					AbstractNextFilter pFilter = midFilter.getPreFilter();
					AbstractNextFilter nFilter = midFilter.getNextFilter();

					pFilter.setNextFilter(nFilter);
					nFilter.setPreFilter(pFilter);

					midFilter.getFilter().clean();
					midFilter = null;
					isOk = true;
				}
			}

		}
		return isOk;
	}
	
	/**
	 * <pre>
	 * 过滤链是会话创建事件的入口，交付链头处理
	 * </pre>
	 * 
	 * @param session 会话
	 */
	@Override
	public void onSessionCreated(ISession session) {
		this.headFilter.onSessionCreated(session);
	}

	/**
	 * <pre>
	 * 过滤链是消息接收事件的入口，交付链头处理
	 * </pre>
	 * 
	 * @param session 会话
	 * @param msg 消息
	 */
	@Override
	public void onMessageReceived(ISession session, Object msg) {
		this.headFilter.onMessageReceived(session, msg);
	}

	/**
	 * <pre>
	 * 过滤链是消息发送事件的入口，交付链尾处�?
	 * </pre>
	 * 
	 * @param session 会话
	 * @param msg 消息
	 */
	@Override
	public void onMessageSent(ISession session, Object msg) {
		this.tailFilter.onMessageSent(session, msg);
	}

	/**
	 * <pre>
	 * 过滤链是异常事件的入口，交付链头处理�?
	 * 使得异常可以通过中间所有过滤器，以便被某些需要处理异常的过滤器捕�?
	 * </pre>
	 * 
	 * @param session 会话
	 * @param exception 异常
	 */
	@Override
	public void onExceptionCaught(ISession session, Throwable exception) {
		this.headFilter.onExceptionCaught(session, exception);
	}

	/**
	 * <pre>
	 * 在确定业务处理逻辑的同时，将其接驳到链尾的出口
	 * @param handler 业务逻辑
	 * </pre>
	 */
	public void setHandler(IHandler handler) {
		this.handler = handler;
		this.tailFilter.setHandler(this.handler);
	}

}
