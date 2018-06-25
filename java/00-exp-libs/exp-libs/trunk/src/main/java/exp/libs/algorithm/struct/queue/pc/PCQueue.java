package exp.libs.algorithm.struct.queue.pc;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 生产者消费者队列
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PCQueue<E> extends ArrayBlockingQueue<E> {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(PCQueue.class);
	
	/** 序列化标�? */
	private static final long serialVersionUID = 4960086438647523367L;

	/** 生产�?/消费者队列的默认大小 */
	private final static int DEFAULT_PC_QUEUE_SIZE = 1024;
	
	/**
	 * 构造函�?
	 * @param capacity 队列容量
	 */
	public PCQueue(final int capacity) {
		super((capacity <= 0 ? DEFAULT_PC_QUEUE_SIZE : capacity), 
				true);	// FIFO = true
	}
	
	/**
	 * 往队尾放入一个元�?.
	 * （阻塞操作）
	 * 
	 * @param e 元素
	 * @return 是否添加成功（若发生中断异常则返回false，否则阻塞等待）
	 */
	public final boolean add(E e) {
		boolean isAdd = false;
		
		if(e != null) {
			try {
				super.put(e);
				isAdd = true;
				
			} catch (InterruptedException ex) {
				log.error("元素 [{}] 插入PC队列失败, 已从内存丢失.", ex);
			}
		}
		return isAdd;
	}
	
	/**
	 * 往队尾放入一个元�?.
	 * （阻塞操作）
	 * @param e 元素
	 * @param timeout 超时时间(ms)
	 * @return 是否添加成功 （若超时未能插入则返回false�?
	 */
	public final boolean add(E e, long timeout) {
		boolean isAdd = false;
		if(e != null) {
			try {
				timeout = (timeout < 0 ? 0 : timeout);
				isAdd = super.offer(e, timeout, TimeUnit.MILLISECONDS);
				
			} catch (InterruptedException ex) {
				log.error("元素 [{}] 插入PC队列失败, 已从内存丢失.", ex);
			}
		}
		return isAdd;
	}
	
	/**
	 * 往队尾放入一个元�?.
	 * （非阻塞操作�?
	 * 
	 * @param e 元素
	 * @return 是否添加成功 （若队列已满则马上返回false�?
	 */
	public final boolean addQuickly(E e) {
		boolean isAdd = false;
		if(e != null) {
			isAdd = super.offer(e);
		}
		return isAdd;
	}
	
	/**
	 * 从队头取出一个元�?.
	 * （阻塞操作）
	 * 
	 * @return 元素（若发生内部异常，返回null�?
	 */
	public final E get() {
		E e = null;
		try {
			e = super.take();
			
		} catch (InterruptedException ex) {
			log.error("从PC队列取出元素失败.", ex);
		}
		return e;
	}
	
	/**
	 * 从队头取出一个元�?.
	 * （阻塞操作）
	 * 
	 * @param timeout 超时时间(ms)
	 * @return 元素（若超时或发生内部异常，返回null�?
	 */
	public final E get(long timeout) {
		E e = null;
		try {
			timeout = (timeout < 0 ? 0 : timeout);
			e = super.poll(timeout, TimeUnit.MILLISECONDS);
			
		} catch (InterruptedException ex) {
			log.error("从PC队列取出元素失败.", ex);
		}
		return e;
	}
	
	/**
	 * 从队头取出一个元�?.
	 * （非阻塞操作�?
	 * 
	 * @return 元素（若队列为空、或发生内部异常，返回null�?
	 */
	public final E getQuickly() {
		return super.poll();
	}
	
	/**
	 * 释放资源
	 */
	public void destory() {
		clear();
	}
	
	//////////////////////////////////////////////////////////////
	// 为了控制队列中的对象，禁止基类提供的其他增减对象的方�?  ///////////////////
	//////////////////////////////////////////////////////////////
	
	/**
	 * 不执行任何处理，永远返回false
	 */
	@Override
	@Deprecated
	public final boolean addAll(Collection<? extends E> alarms) {
		System.err.println(PCQueue.class.getName() + 
				" : ArrayBlockingQueue.addAll() has been removed.");
		return false;
	}
	
	/**
	 * 不执行任何处理，永远返回false
	 */
	@Override
	@Deprecated
	public final boolean offer(E e) {
		System.err.println(PCQueue.class.getName() + 
				" : ArrayBlockingQueue.offer() has been removed.");
		return false;
	}
	
	/**
	 * 不执行任何处理，永远返回false
	 */
	@Override
	@Deprecated
	public final boolean offer(E e, long timeout, TimeUnit unit) {
		System.err.println(PCQueue.class.getName() + 
				" : ArrayBlockingQueue.offer() has been removed.");
		return false;
	}
	
	/**
	 * 不执行任何处�?
	 */
	@Override
	@Deprecated
	public final void put(E e) {
		log.warn("{} : ArrayBlockingQueue.put() has been removed.", 
				PCQueue.class.getName());
	}
	
	/**
	 * 不执行任何处理，永远返回null
	 */
	@Override
	@Deprecated
	public final E poll() {
		log.warn("{} : ArrayBlockingQueue.poll() has been removed.", 
				PCQueue.class.getName());
		return null;
	}
	
	/**
	 * 不执行任何处理，永远返回null
	 */
	@Override
	@Deprecated
	public final E poll(long timeout, TimeUnit unit) {
		log.warn("{} : ArrayBlockingQueue.poll() has been removed.", 
				PCQueue.class.getName());
		return null;
	}
	
	/**
	 * 不执行任何处理，永远返回null
	 */
	@Override
	@Deprecated
	public final E take() {
		log.warn("{} : ArrayBlockingQueue.take() has been removed.", 
				PCQueue.class.getName());
		return null;
	}
	
	/**
	 * 不执行任何处理，永远返回null
	 */
	@Override
	@Deprecated
	public final E peek() {
		log.warn("{} : ArrayBlockingQueue.peek() has been removed.", 
				PCQueue.class.getName());
		return null;
	}
	
	/**
	 * 不执行任何处理，永远返回null
	 */
	@Override
	@Deprecated
	public final E remove() {
		log.warn("{} : ArrayBlockingQueue.remove() has been removed.", 
				PCQueue.class.getName());
		return null;
	}
	
	/**
	 * 不执行任何处理，永远返回false
	 */
	@Override
	@Deprecated
	public final boolean remove(Object o) {
		log.warn("{} : ArrayBlockingQueue.remove() has been removed.", 
				PCQueue.class.getName());
		return false;
	}
	
	/**
	 * 不执行任何处理，永远返回false
	 */
	@Override
	@Deprecated
	public final boolean removeAll(Collection<?> list) {
		log.warn("{} : ArrayBlockingQueue.removeAll() has been removed.", 
				PCQueue.class.getName());
		return false;
	}
	
}
