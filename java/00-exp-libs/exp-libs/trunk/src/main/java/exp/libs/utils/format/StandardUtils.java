package exp.libs.utils.format;


/**
 * <PRE>
 * 标准化工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class StandardUtils {

	/** 私有化构造函�?. */
	private StandardUtils() {}
	
	/**
	 * <PRE>
	 * 根据实际需要的容量，返回构造Map的标准容�?(使得Map的搜索性能最�?)�?
	 * 	返回值为大于 actualSize �? 2^n (不超�?2^30 -1, 即int最大�?)
	 * </PRE>
	 * 
	 * @param actualSize 实际容量
	 * @return 标准容量
	 */
	public static int getMapSize(int actualSize) {
		boolean isGet = false;
		int size = 2;
		
		for(int i = 1; i < 30; i++) {
			size = size<<1;
			if(size >= actualSize) {
				isGet = true;
				break;
			}
		}
		
		if(isGet == false) {
			size = actualSize;
		}
		return size;
	}
	
	/**
	 * <PRE>
	 * 把dos内容转换为符合unix标准内容�?
	 * 	(实则上不是dos也能转换为unix)
	 * </PRE>
	 * 
	 * @param dos dos内容
	 * @return 符合unix标准内容
	 */
	public static String dos2unix(String dos) {
		String unix = "";
		if(dos != null) {
			unix = dos.replace("\r", "").replace('\\', '/');
		}
		return unix;
	}
	
	/**
	 * <PRE>
	 * unix内容转换为符合dos标准内容�?
	 * 	(实则上不是unix也能转换为dos)
	 * </PRE>
	 * 
	 * @param unix unix内容
	 * @return 符合dos标准内容
	 */
	public static String unix2dos(String unix) {
		String dos = "";
		if(unix != null) {
			dos = unix.replace("\r", "").replace("\n", "\r\n").replace('/', '\\');
		}
		return dos;
	}
	
}
