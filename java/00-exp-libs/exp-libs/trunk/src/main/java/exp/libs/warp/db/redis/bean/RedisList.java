package exp.libs.warp.db.redis.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.ListUtils;
import exp.libs.warp.db.redis.RedisClient;

public class RedisList<E extends Serializable> {

	private final static Logger log = LoggerFactory.getLogger(RedisList.class);
	
	private final static String DEFAULT_LIST_NAME = "REDIS_LIST";
	
	private final String LIST_NAME;
	
	private RedisClient redis;
	
	private boolean typeIsStr;
	
	/**
	 * 构造函数
	 * @param listName 列表在redis中的名称（需确保不为空）
	 * @param redis redi客户端对象
	 */
	public RedisList(String listName, RedisClient redis) {
		this.LIST_NAME = (listName == null ? DEFAULT_LIST_NAME : listName);
		this.redis = (redis == null ? new RedisClient() : redis);
		this.typeIsStr = true;
	}
	
	public boolean isEmpty() {
		return size() <= 0;
	}
	
	public long size() {
		long size = 0L;
		try {
			size = redis.getListSize(LIST_NAME);
			
		} catch(Exception e) {
			log.error("查询redis缓存失败", e);
		}
		return size;
	}
	
	public boolean add(E e) {
		return addToTail(e);
	}
	
	public boolean addToHead(E e) {
		boolean isOk = false;
		if(e == null) {
			return isOk;
		}
		
		try {
			if(typeIsStr || e instanceof String) {
				typeIsStr = true;
				isOk = redis.addToListHead(LIST_NAME, (String) e) > 0;
				
			} else {
				typeIsStr = false;
				isOk = redis.addToListHead(LIST_NAME, e) > 0;
			}
		} catch(Exception ex) {
			log.error("写入redis缓存失败", ex);
		}
		return isOk;
	}
	
	public boolean addToTail(E e) {
		boolean isOk = false;
		if(e == null) {
			return isOk;
		}
		
		try {
			if(typeIsStr || e instanceof String) {
				typeIsStr = true;
				isOk = redis.addToListTail(LIST_NAME, (String) e) > 0;
				
			} else {
				typeIsStr = false;
				isOk = redis.addToListTail(LIST_NAME, e) > 0;
			}
		} catch(Exception ex) {
			log.error("写入redis缓存失败", ex);
		}
		return isOk;
	}
	
	public boolean addAll(E... es) {
		boolean isOk = false;
		if(es == null) {
			return isOk;
		}
		
		isOk = true;
		for(E e : es) {
			isOk &= add(e);
		}
		return isOk;
	}
	
	public boolean addAll(List<E> es) {
		boolean isOk = false;
		if(es == null) {
			return isOk;
		}
		
		isOk = true;
		for(E e : es) {
			isOk &= add(e);
		}
		return isOk;
	}

	@SuppressWarnings("unchecked")
	public E get(int index) {
		E e = null;
		if(isEmpty() && index >= 0 && index < size()) {
			return e;
		}
		
		try {
			if(typeIsStr == true) {
				String str = redis.getListVal(LIST_NAME, index);
				if(str != null) {
					e = (E) str;
				}
				
			} else {
				Object obj = redis.getListObj(LIST_NAME, index);
				if(obj != null) {
					e = (E) obj;
				}
			}
		} catch(Exception ex) {
			log.error("读取redis缓存失败", ex);
		}
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public List<E> getAll() {
		List<E> list = new LinkedList<E>();
		if(isEmpty()) {
			return list;
		}
		
		try {
			if(typeIsStr == true) {
				List<String> sList = redis.getListAllVals(LIST_NAME);
				if(ListUtils.isNotEmpty(sList)) {
					for(String s : sList) {
						list.add((E) s);
					}
				}
				
			} else {
				List<Object> oList = redis.getListAllObjs(LIST_NAME);
				if(ListUtils.isNotEmpty(oList)) {
					for(Object o : oList) {
						list.add((E) o);
					}
				}
			}
		} catch(Exception e) {
			log.error("读取redis缓存失败", e);
		}
		return list;
	}
	
	public boolean clear() {
		boolean isOk = false;
		try {
			isOk = redis.delKeys(LIST_NAME) >= 0;
			
		} catch(Exception e) {
			log.error("删除redis缓存失败", e);
		}
		return isOk;
	}
	
}
