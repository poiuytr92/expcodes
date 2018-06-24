package exp.fpf.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import exp.libs.utils.other.StrUtils;

/**
 * <pre>
 * 接收文件缓存区(非多线程安全).
 * --------------------------
 * 	第三方程序在传送数据流文件时，可能会使用并发传送使得文件时序错乱.
 * 	此缓存区目的是修正接收文件的时序
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-16
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class RecvCache {

	/** 当前等待的文件时序 */
	private int waitTimeSequence;
	
	/**
	 * 当前已读取的文件时序.
	 *   <= readTimeSequence 的必定已读取
	 *   且必有 readTimeSequence < waitTimeSequence
	 */
	private int readTimeSequence;
	
	/**
	 * 文件缓存.
	 *  timeSequence -> file name/path
	 */
	private Map<Integer, String> caches;
	
	/**
	 * 构造函数
	 */
	public RecvCache() {
		this.waitTimeSequence = 0;
		this.readTimeSequence = -1;
		this.caches = new HashMap<Integer, String>();
	}
	
	/**
	 * 添加新文件到缓存, 并根据时序进行调整
	 * @param timeSequence 文件时序
	 * @param file 文件名称/路径
	 * @return 是否添加成功
	 */
	public boolean add(int timeSequence, String file) {
		boolean isOk = false;
		if(timeSequence > readTimeSequence) {
			caches.put(timeSequence, file);
			isOk = true;
			
			// 修正等待时序
			if(timeSequence == waitTimeSequence) {
				for(int t = timeSequence + 1; ; t++) {
					if(!caches.containsKey(t)) {
						waitTimeSequence = t;
						break;
					}
				}
			}
		}
		return isOk;
	}
	
	/**
	 * 获取时序位置最早的一个文件
	 * @return
	 */
	public String get() {
		String file = "";
		if(readTimeSequence + 1 < waitTimeSequence) {
			file = caches.remove(++readTimeSequence);
		}
		return (file == null ? "" : file);
	}
	
	/**
	 * 获取时序位置最早的多个文件, 直到出现时序断层为止.
	 * -------------------------------
	 * 	例如缓存了时序文件  [2、3、4、8、10], 由于时序4之后出现了断层，则返回 [2、3、4]
	 * 	
	 * @return
	 */
	public List<String> getAll() {
		List<String> files = new LinkedList<String>();
		while(true) {
			String file = get();
			if(StrUtils.isNotEmpty(file)) {
				files.add(file);
			} else {
				break;
			}
		}
		return files;
	}
	
	/**
	 * 清理缓存
	 */
	public void clear() {
		caches.clear();
	}
	
}
