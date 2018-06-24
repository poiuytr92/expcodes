package exp.libs.warp.net.tracert;

/**
 * <PRE>
 * 接口返回处理，如ping，tracert等操作，每次返回时都可以对消息进行处理
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-14
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public interface IDealReturn {
	
	/**
	 * 控制台返回消息处理
	 *
	 * @param line 控制台信息
	 */
	public void deal(String line);

}
