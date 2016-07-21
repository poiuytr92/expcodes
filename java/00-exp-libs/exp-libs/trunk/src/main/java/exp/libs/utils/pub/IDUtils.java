package exp.libs.utils.pub;

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

	private final static byte[] LOCK_SECOND_ID = new byte[1];
	private static volatile int LAST_SECOND_ID = -1;
	
	private final static byte[] LOCK_MILLIS_ID = new byte[1];
	private static volatile long LAST_MILLIS_ID = -1L;
	
	private final static byte[] LOCK_TIME_ID = new byte[1];
	private static volatile long LAST_TIME_ID = -1L;
	
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
		synchronized (LOCK_SECOND_ID) {
			do {
				id = (int) (System.currentTimeMillis() / 1000);
				ThreadUtils.tSleep(500);
			} while(LAST_SECOND_ID == id);
			LAST_SECOND_ID = id;
		}
		return id;
	}
	
	/**
	 * 获取时间序唯一性ID（毫秒级）
	 * @return
	 */
	public static long getMillisID() {
		long id = -1;
		synchronized (LOCK_MILLIS_ID) {
			do {
				id = System.currentTimeMillis();
			} while(LAST_MILLIS_ID == id);
			LAST_MILLIS_ID = id;
		}
		return id;
	}
	
	/**
	 * 获取时间序唯一性ID（yyyyMMddHHmmssSSS）
	 * @return
	 */
	public static long getTimeID() {
		long id = -1;
		synchronized (LOCK_TIME_ID) {
			do {
				id = NumUtils.toLong(TimeUtils.getSysDate("yyyyMMddHHmmssSSS"));
			} while(LAST_TIME_ID == id);
			LAST_TIME_ID = id;
		}
		return id;
	}
	
}
