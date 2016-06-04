package exp.libs.warp.other.queue.loop;

/**
 * <PRE>
 * 循环队列
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LoopQueue<T> {
	
	/** 队列的默认大小 */
	private final static int DEFAULT_QUEUE_SIZE = 16;
	
	private int capacity;

	private Object[] queue;

	private int pHead;

	private int pTail;

	public LoopQueue() {
		this.queue = new Object[DEFAULT_QUEUE_SIZE];
		this.pHead = 0;
		this.pTail = 0;
	}
	
	/**
	 * 构造函数
	 * @param capacity
	 */
	public LoopQueue(final int capacity) {
		this.capacity = (capacity <= 0 ? DEFAULT_QUEUE_SIZE : capacity);
		this.queue = new Object[this.capacity];
		this.pHead = 0;
		this.pTail = 0;
	}

	/**
	 * 插入元素到队尾
	 * @param element
	 * @return
	 */
	public boolean add(T element) {
		boolean isOk = false;
		if (!isFull()) {
			isOk = true;
			queue[pTail++] = element;
			pTail = (pTail == capacity ? 0 : pTail);	// 如果pTail已经到头，那就转头
		}
		return isOk;
	}

	/**
	 * 取出队头元素
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T take() {
		T element = null;
		if (!isEmpty()) {
			element = (T) queue[pHead];
			queue[pHead++] = null;
			pHead = (pHead == capacity ? 0 : pHead);
		}
		return element;
	}

	/**
	 * 返回但不删除队头元素
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		return (isEmpty() ? null : ((T) queue[pHead]));
	}

	/**
	 * 判断循环队列是否已空
	 * @return
	 */
	public boolean isEmpty() {
		return (pTail == pHead && queue[pTail] == null);
	}
	
	/**
	 * 判断循环队列是否已满
	 * @return
	 */
	public boolean isFull() {
		return (pTail == pHead && queue[pHead] != null);
	}
	
	/**
	 * 获取循环队列的大小
	 * @return
	 */
	public int size() {
		if (isEmpty()) {
			return 0;
		}
		return ((pTail > pHead) ? (pTail - pHead) : (capacity - pHead + pTail));
	}

	/**
	 * 清空循环队列
	 */
	public void clear() {
		for(int i = 0; i < capacity; i++) {
			queue[i] = null;
		}
		pHead = 0;
		pTail = 0;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		if (!isEmpty()) {
			
			// 若pHead < pTail，有效元素为[pHead, pTail]
			if (pHead < pTail) {
				for (int i = pHead; i < pTail; i++) {
					sb.append(queue[i].toString() + ", ");
				}
				
			// 若pHead >= pTail，有效元素为 [pHead, capacity] 和 [0, pTail]
			} else {
				for (int i = pHead; i < capacity; i++) {
					sb.append(queue[i].toString() + ", ");
				}
				for (int i = 0; i < pTail; i++) {
					sb.append(queue[i].toString() + ", ");
				}
			}
			sb.setLength(sb.length() - 2);
		}
		sb.append("]");
		return sb.toString();
	}

}
