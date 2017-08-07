package exp.libs.utils.other;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import exp.libs.utils.num.NumUtils;

/**
 * <PRE>
 * 队列/数组集合操作工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LAUtils {

	/** 私有化构造函数 */
	protected LAUtils() {}
	
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length <= 0);
	}
	
	public static boolean isEmpty(List<?> list) {
		return (list == null || list.size() <= 0);
	}
	
	public static String toString(Object[] array) {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		if(array != null) {
			for(Object o : array) {
				sb.append(o == null ? "null" : o.toString()).append(", ");
			}
			
			if(array.length > 0) {
				sb.setLength(sb.length() - 2);
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	/**
	 * 移除list中的重复元素，但保持顺序不变
	 * @param list 需要移除重复元素的list
	 */
	public static <E> void removeDuplicate(List<E> list) {
		if(list != null) {
			Set<E> set = new HashSet<E>();	// 唯一集
			for(Iterator<E> its = list.iterator(); its.hasNext();) {
				E e = its.next();
				if(set.add(e) == false) {
					its.remove();
				}
			}
			set.clear();
		}
	}
	
	/**
     * 后移数组中所有null值，返回非null元素的个数(即数组实际长度)
     * @param array
     * @return 数组实际长度
     */
    public static int cutbackNull(Object[] array) {
    	if(array == null) {
    		return 0;
    	}
    	
		int nullPos = 0;	// 上次检索到的空元素指针位置
		int len = 0;		// 非空数组的实际长度
		for(; len < array.length; len++) {
			if(array[len] != null) {
				continue;
			}
			
			for(int j = NumUtils.max(len, nullPos) + 1; j < array.length; j++) {
				if(array[j] == null) {
					continue;
				}
				
				array[len] = array[j];
				array[j] = null;
				nullPos = j;
				break;
			}
			
			// 说明后续所有元素均为null
			if(array[len] == null) {
				break;
			}
		}
    	return len;
    }
    
	/**
	 * 检查队列大小是否已经 > 阀值。
	 * 此方法不会调用size()遍历list所有元素。
	 * 	(1.6版本之后的JDK的size()方法返回的是一个变量，不会执行迭代操作)
	 * 
	 * @param list 队列
	 * @param sizeLimit 大小阀值
	 * @return true:已超过阀值; false:未超过阀值
	 */
	@Deprecated
	public static <E> boolean checkSize(List<E> list, int sizeLimit) {
		boolean isOver = false;
		if(list != null) {
			int cnt = 0;
			for(Iterator<E> its = list.iterator(); its.hasNext();) {
				its.next();
				if(++cnt > sizeLimit) {
					isOver = true;
					break;
				}
			}
		}
		return isOver;
	}
	
	/**
	 * 求两个集合的交集
	 * @param c1  集合1
	 * @param c2  集合2
	 * @return 交集
	 */
	public static <E> Set<E> intersection(Collection<E> c1, Collection<E> c2) {
		Set<E> set = new HashSet<E>();
		if(c1 == null && c2 == null) {
			// None
			
		} else if(c1 == null) {
			set.addAll(c2);
			
		} else if(c2 == null) {
			set.addAll(c1);
			
		} else {
			Set<E> s1 = new HashSet<E>(c1);
			for(Iterator<E> its = c2.iterator(); its.hasNext();) {
				E e = its.next();
				if(s1.remove(e) == true) {
					set.add(e);
				}
			}
			s1.clear();
		}
		return set;
	}

	/**
	 * 求两个集合的差集
	 * @param c1  集合1
	 * @param c2  集合2
	 * @return 差集
	 */
	public static <E> Set<E> subtraction(Collection<E> c1, Collection<E> c2) {
		Set<E> set = new HashSet<E>();
		if(c1 == null && c2 == null) {
			// None
			
		} else if(c1 == null) {
			set.addAll(c2);
			
		} else if(c2 == null) {
			set.addAll(c1);
			
		} else {
			set.addAll(c1);
			for(Iterator<E> its = c2.iterator(); its.hasNext();) {
				set.remove(its.next());
			}
		}
		return set;
	}

	/**
	 * 求两个集合的并集
	 * @param c1  集合1
	 * @param c2  集合2
	 * @return 并集
	 */
	public static <E> Set<E> union(Collection<E> c1, Collection<E> c2) {
		Set<E> set = new HashSet<E>();
		if(c1 != null) { set.addAll(c1); }
		if(c2 != null) { set.addAll(c2); }
		return set;
	}
	
	/**
	 * 比较两个集合是否完全一致(顺序无关)
	 * @param <T>
	 * @param c1 集合1
	 * @param c2 集合2
	 * @return
	 */
	public static <E extends Comparable<E>> boolean compare(
			Collection<E> c1, Collection<E> c2) {
		if(c1 == null || c2 == null) {
			return false;
		}
		
		if (c1.size() != c2.size()) {
			return false;
		}
		
		boolean isConsistency = true;
		HashSet<E> s1 = new HashSet<E>(c1);
		for(Iterator<E> its = c2.iterator(); its.hasNext();) {
			if(s1.contains(its.next())) {
				isConsistency = false;
				break;
			}
		}
		return isConsistency;
	}
	
	/**
	 * 比较两个队列是否完全一致(顺序有关)
	 * @param <T>
	 * @param c1 集合1
	 * @param c2 集合2
	 * @return
	 */
	public static <E extends Comparable<E>> boolean compare(
			List<E> list1, List<E> list2) {
		if(list1 == null || list2 == null) {
			return false;
		}
		
		int len = list1.size();
		if (len != list2.size()) {
			return false;
		}
		
		boolean isConsistency = true;
		for(int i = 0; i < len; i++) {
			E e1 = list1.get(i);
			E e2 = list2.get(i);
			
			if(e1 != null && e2 != null && e1.equals(e2)) {
				continue;
				
			} else if (e1 == null && e2 == null) {
				continue;
				
			} else {
				isConsistency = false;
				break;
			}
		}
		return isConsistency;
	}

	public static <E> List<E> copy(List<E> list) {
		return copyLink(list);
	}
	
	public static <E> List<E> copyLink(List<E> list) {
		List<E> copy = new LinkedList<E>();
		if(list != null) {
			copy.addAll(list);
		}
		return copy;
	}
	
	public static <E> List<E> copyArray(List<E> list) {
		List<E> copy = new ArrayList<E>(list == null ? 1 : list.size());
		if(list != null) {
			copy.addAll(list);
		}
		return copy;
	}
	
	public static <E> List<E> reverse(List<E> list) {
		List<E> reverse = copy(list);
		Collections.reverse(reverse);
		return reverse;
	}
	
	public static <E> List<E> reverseLink(List<E> list) {
		List<E> reverse = copyLink(list);
		Collections.reverse(reverse);
		return reverse;
	}
	
	public static <E> List<E> reverseArray(List<E> list) {
		List<E> reverse = copyArray(list);
		Collections.reverse(reverse);
		return reverse;
	}
	
}
