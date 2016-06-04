package exp.libs.warp.net.ws.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类说明：利用LinkedHashMap实现简单的缓存， 
 * 必须实现removeEldestEntry方法，具体参见JDK文档
 * LRU是Least Recently Used 近期最少使用算法
 * 
 * @author 蔡俊彬
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {
	

	/**
	 *	id
	 */
	private static final long serialVersionUID = 5308091722537270882L;

	/** 队列最大数  */
	private final int maxCapacity;

	/**   */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**   */
    private final Lock lock = new ReentrantLock();

    /**
     * 
     * 构造方法
     * @param maxCapacity
     */
    public LRULinkedHashMap(int maxCapacity) {
        super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
    
    @Override
    public boolean containsKey(Object key) {
        try {
            lock.lock();
            return super.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    
    @Override
    public V get(Object key) {
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lock();
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 
     */
    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 
     */
    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 
     *
     * @return
     */
    public Collection<Map.Entry<K, V>> getAll() {
        try {
            lock.lock();
            return new ArrayList<Map.Entry<K, V>>(super.entrySet());
        } finally {
            lock.unlock();
        }
    }
}