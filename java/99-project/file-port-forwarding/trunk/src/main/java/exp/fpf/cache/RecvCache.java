package exp.fpf.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 接收文件缓存区.
 * --------------------------
 * 	第三方程序在传送数据流文件时，可能会使用并发传送使得文件时序错乱.
 * 	此缓存区目的是修正文件时序
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-16
 * @author    EXP: 272629724@qq.com
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
	 *  timeSequence -> file name
	 */
	private Map<Integer, String> caches;
	
	/**
	 * 
	 */
	public RecvCache() {
		this.waitTimeSequence = 0;
		this.readTimeSequence = -1;
		this.caches = new HashMap<Integer, String>();
	}
	
	public boolean add(int timeSequence, String fileName) {
		boolean isOk = false;
		if(timeSequence > readTimeSequence) {
			caches.put(timeSequence, fileName);
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
	
	public String get() {
		String fileName = "";
		if(readTimeSequence + 1 < waitTimeSequence) {
			fileName = caches.remove(++readTimeSequence);
		}
		return (fileName == null ? "" : fileName);
	}
	
	public void clear() {
		caches.clear();
	}
	
}
