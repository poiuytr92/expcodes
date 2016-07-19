package exp.libs.utils.pub;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import exp.libs.utils.os.ThreadUtils;

/**
 * <PRE>
 * 唯一性ID生成器工具包.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class IDUtils {

	private final static int LIMIT = 128;
	
	private final static Set<Integer> USED_SECOND = new HashSet<Integer>(LIMIT);
	
	private final static Set<Long> USED_MILLIS = new HashSet<Long>(LIMIT);
	
	private final static Set<Long> USED_TIME = new HashSet<Long>(LIMIT);
	
	protected IDUtils() {}
	
	/**
	 * 获取时空唯一性ID
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 获取时间序唯一性ID（秒级）
	 * @return
	 */
	public static int getSecondID() {
		int id = -1;
		do {
			id = (int) (System.currentTimeMillis() / 1000);
			ThreadUtils.tSleep(500);
		} while(USED_SECOND.contains(id));
		
		if(USED_SECOND.size() >= LIMIT) {
			USED_SECOND.clear();
		}
		USED_SECOND.add(id);
		return id;
	}
	
	/**
	 * 获取时间序唯一性ID（毫秒级）
	 * @return
	 */
	public static long getMillisID() {
		long id = -1;
		do {
			id = System.currentTimeMillis();
		} while(USED_MILLIS.contains(id));
		
		if(USED_MILLIS.size() >= LIMIT) {
			USED_MILLIS.clear();
		}
		USED_MILLIS.add(id);
		return id;
	}
	
	/**
	 * 获取时间序唯一性ID（yyyyMMddHHmmssSSS）
	 * @return
	 */
	public static long getTimeID() {
		long id = -1;
		do {
			id = NumUtils.toLong(TimeUtils.getSysDate("yyyyMMddHHmmssSSS"));
		} while(USED_TIME.contains(id));
		
		if(USED_TIME.size() >= LIMIT) {
			USED_TIME.clear();
		}
		USED_TIME.add(id);
		return id;
	}
	
}
