package exp.libs.warp.net.tracert;

/**
 * <PRE>
 * 接口返回处理，如ping，tracert等操作，每次返回时都可以对消息进行处理
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
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
